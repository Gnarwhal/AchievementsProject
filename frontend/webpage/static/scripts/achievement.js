let achievementId   = window.location.pathname.split('/').pop();
let isReturn        = false;
let achievementData = null;
let myRating        = {};
const loadAchievement = () => {
	if (myRating.invalid) {
		document.querySelector("#achievement-rating").remove();
	}

	const description = document.querySelector("#achievement-description-text");
	if (description.textContent === '') {
		description.remove();
	}

	// Canvasing

	const completionCanvas = document.querySelector("#achievement-completion-canvas");

	const STROKE_WIDTH = 0.18;
	const style   = window.getComputedStyle(completionCanvas);
	const context = completionCanvas.getContext('2d');

	const drawCanvas = () => achievementData.then(data => {
		const width  = Number(style.getPropertyValue('width').slice(0, -2));
		const height = width;
		
		context.canvas.width  = width;
		context.canvas.height = height;
		context.clearRect(0, 0, width, height);
		context.strokeStyle = root.getProperty('--accent-value3');
		context.lineWidth = (width / 2) * STROKE_WIDTH;
		context.beginPath();
		context.arc(width / 2, height / 2, (width / 2) * (1 - STROKE_WIDTH / 2), -0.5 * Math.PI, (-0.5 + (data.completion === null ? 0 : (data.completion / 100) * 2)) * Math.PI);
		context.stroke();
	});

	window.addEventListener('resize', drawCanvas);
	drawCanvas();

	if (!myRating.invalid) {
		const saveReview = document.querySelector("#rating-save-stack");

		const myDifficulty = document.querySelector("#achievement-difficulty-rating-text");
		const myQuality    = document.querySelector("#achievement-quality-rating-text");
		const myReview     = document.querySelector("#achievement-review-rating-text");

		const reviewInput = () => {
			saveReview.style.display = 'block';
		}
		myDifficulty.addEventListener('input', reviewInput);
		myQuality.addEventListener('input', reviewInput);
		myReview.addEventListener('input', reviewInput);

		const saveInputOnEnter = (keyEvent) => {
			if (keyEvent.key === 'Enter') {
				saveReview.click();
			}
		}
		myDifficulty.addEventListener('keydown', saveInputOnEnter);
		myQuality.addEventListener('keydown', saveInputOnEnter);

		saveReview.addEventListener('click', (clickEvent) => {
			let successful = true;
			const difficulty = Number(myDifficulty.value);
			const quality    = Number(myQuality.value   );
			if ((Number.isNaN(difficulty) && myDifficulty.value !== '') || difficulty < 0 || difficulty > 10) {
				myDifficulty.style.backgroundColor = 'var(--error)';
				successful = false;
			}
			if ((Number.isNaN(quality) && myQuality.value !== '') || quality < 0 || quality > 10) {
				myQuality.style.backgroundColor = 'var(--error)';
				successful = false;
			}
			if (successful) {
				myDifficulty.style.backgroundColor =  'var(--foreground)';
				myQuality.style.backgroundColor =  'var(--foreground)';
				saveReview.style.display = 'none';
				fetch(`/api/achievement/${achievementId}/rating/${session.id}`, {
					method: 'POST',
					headers: {
						'Content-Type': 'application/json'
					},
					body: JSON.stringify({
						sessionKey: session.key,
						difficulty: difficulty,
						quality: quality,
						review: myReview.value
					})
				})
				.then(response => {
					if (response.status === 401) {
						responese.json().then(data => {
							myDifficulty.value = data.difficulty ? data.difficulty : '';
							myQuality.value = data.quality ? data.quality : '';
							myReview.value = data.review ? data.review : '';
						});
					}
				});
			}
		});
	}

	{
		const ratings = document.querySelectorAll(".list-page-entry.rating");
		for (const rating of ratings) {
			rating.addEventListener("click", (clickEvent) => {
				window.location.href = `/user/${rating.dataset.id}`;
			});
		}
	}
}

const expandTemplates = async () => {
	await commonTemplates();
	if (session.key) {
		myRating = await fetch(`/api/achievement/${achievementId}/rating/${session.id}`, { method: 'GET' })
			.then(response => {
				if (response.status !== 200) {
					return { invalid: true };
				} else {
					return response.json();
				}
			});
	} else {
		myRating = { invalid: true };
	}
	template.apply("achievement-page").promise(achievementData.then(data => ({
		id: achievementId,
		name: data.name,
		description: data.description ? data.description : '',
		completion: data.completion === null ? "N/A" : `${data.completion}%`,
		difficulty: data.difficulty === null ? "N/A" : `${data.difficulty} / 10`,
		quality: data.quality === null ? "N/A" : `${data.quality} / 10`,
		my_difficulty: myRating.difficulty ? myRating.difficulty : '',
		my_quality: myRating.quality ? myRating.quality : '',
		my_review: myRating.review ? myRating.review : '',
	})));
	template.apply("rating-list").promise(achievementData.then(data => data.ratings.map(data => ({
		user_id: data.userId,
		user_username: data.username,
		user_difficulty: data.difficulty,
		user_quality: data.quality,
		user_review: data.review
	}))));
}

window.addEventListener("load", async (loadEvent) => {
	await loadCommon();

	var importing = document.querySelector("#importing");
	if (/\d+/.test(achievementId)) {
		achievementId = Number(achievementId);
	} else {
		// Handle error
	}
	importing.remove();	

	achievementData = fetch(`/api/achievement/${achievementId}`, { method: 'GET' })
		.then(response => response.json());

	await expandTemplates();
	await template.expand();

	loadLazyImages();
	connectNavbar();
	loadAchievement();
});