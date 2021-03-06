let profileId   = window.location.pathname.split('/').pop();
let isReturn    = false;
let profileData = null;
const loadProfile = () => {
	const lists = document.querySelectorAll(".profile-list");
	const checkLists = () => {
		for (const list of lists) {
			let found = false;
			const entries = list.querySelectorAll(".profile-entry");
			for (const entry of entries) {
				if (window.getComputedStyle(entry).getPropertyValue('display') !== 'none') {
					found = true;
					break;
				}
			}
			if (!found) {
				list.style.display = 'none';
			} else {
				list.style.display = 'block';
			}
		}
	}
	checkLists();

	{
		const validImageFile = (type) => {
			return type === "image/apng"
			    || type === "image/avif"
			    || type === "image/gif"
			    || type === "image/jpeg"
			    || type === "image/png"
			    || type === "image/svg+xml"
			    || type === "image/webp"
		}
		
		const editProfileButton = document.querySelector("#info-edit-stack");
		const saveProfileButton = document.querySelector("#info-save-stack");

		const usernameText  = document.querySelector("#profile-info-username-text");
		const usernameField = document.querySelector("#profile-info-username-field");

		const finalizeName = () => {
			if (usernameField.value !== '') {
				fetch(`/api/user/${profileId}/username`, {
					method: 'POST',
					headers: {
						'Content-Type': 'application/json'
					},
					body: `{ "sessionKey": "${session.key}", "username": "${usernameField.value}" }`
				}).then(response => {
					if (response.status === 201) {
						usernameText.textContent = usernameField.value.substring(0, 32);
					}
				});
			}
		};

		usernameField.addEventListener("input", (inputEvent) => {
			if (usernameField.value.length > 32) {
				usernameField.value = usernameField.value.substring(0, 32);
			}
		});
		usernameField.addEventListener("keydown", (keyEvent) => {
			if (keyEvent.key === "Enter") {
				saveProfileButton.click();
			}
		})

		const pfp               = document.querySelector("#profile-info-pfp-img");
		const pfpStack          = document.querySelector("#profile-info-pfp");
		const upload            = document.querySelector("#profile-info-pfp-upload");
		const uploadHover       = document.querySelector("#profile-info-pfp-upload-hover");
		const uploadInvalid     = document.querySelector("#profile-info-pfp-upload-invalid");

		const togglePlatformEdit = (clickEvent) => {
			editProfileButton.classList.toggle("active");
			saveProfileButton.classList.toggle("active");
			usernameText.classList.toggle("active");
			usernameField.classList.toggle("active");
			upload.classList.toggle("active");
			uploadHover.classList.toggle("active");
			uploadInvalid.classList.toggle("active");
			pfpStack.classList.remove("hover");
			pfpStack.classList.remove("invalid");
		};
		editProfileButton.addEventListener("click", togglePlatformEdit);
		editProfileButton.addEventListener("click", () => {
			usernameField.value = usernameText.textContent;
		});
		saveProfileButton.addEventListener("click", togglePlatformEdit);
		saveProfileButton.addEventListener("click", finalizeName);

		pfpStack.addEventListener("drop", (dropEvent) => {
			if (upload.classList.contains("active")) {
				dropEvent.preventDefault();
				pfpStack.classList.remove("hover");
				pfpStack.classList.remove("invalid");
				if (dropEvent.dataTransfer.files) {
					const file = dropEvent.dataTransfer.files[0];
					if (validImageFile(file.type)) {
						const data = new FormData();
						data.append('session', new Blob([`{ "key": "${session.key}" }`], { type: `application/json` }));
						data.append('file', file);

						fetch(`/api/user/${profileId}/image`, {
							method: 'POST',
							body: data
						}).then(response => {
							if (upload.classList.contains("active")) {
								if (response.status === 201) {
									pfp.src = `/api/user/${profileId}/image?time=${Date.now()}`;
								} else {
									pfpStack.classList.add("invalid");
								}
							}
						});
						return;
					}
				}
				pfpStack.classList.add("invalid");
			}
		});
		pfpStack.addEventListener("dragover", (dragEvent) => {
			if (upload.classList.contains("active")) {
				dragEvent.preventDefault();
			}
		});
		pfpStack.addEventListener("dragenter", (dragEvent) => {
			if (upload.classList.contains("active")) {
				pfpStack.classList.remove("hover");
				pfpStack.classList.remove("invalid");
				if (dragEvent.dataTransfer.types.includes("application/x-moz-file")) {
					pfpStack.classList.add("hover");
				} else {
					pfpStack.classList.add("invalid");
				}
			}
		});
		pfpStack.addEventListener("dragleave", (dragEvent) => {
			if (upload.classList.contains("active")) {
				pfpStack.classList.remove("hover");
				pfpStack.classList.remove("invalid");
			}
		});
	}

	{
		const editPlatformsButton = document.querySelector("#platform-edit-stack");
		const savePlatformsButton = document.querySelector("#platform-save-stack");
		const platforms = document.querySelectorAll("#profile-platforms .profile-entry");

		const togglePlatformEdit = (clickEvent) => {
			editPlatformsButton.classList.toggle("active");
			savePlatformsButton.classList.toggle("active");
			for (const platform of platforms) {
				platform.classList.toggle("editing");
			}
			checkLists();
		};
		editPlatformsButton.addEventListener("click", togglePlatformEdit);
		savePlatformsButton.addEventListener("click", togglePlatformEdit);

		const steamButtons = [
			document.querySelector("#add-steam"),
			document.querySelector("#platform-0"),
		];

		let allowSteamImport = true;
		steamButtons[0].addEventListener("click", (clickEvent) => {
			if (allowSteamImport) {
				window.location.href = "/auth/steam";
			}
		});
		steamButtons[1].addEventListener("click", (clickEvent) => {
			fetch(`/api/user/${profileId}/platforms/remove`, {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json'
				},
				body: JSON.stringify({ sessionKey: session.key, platformId: 0 })
			}).then(() => {
				allowSteamImport = true;
			});
			allowSteamImport = false;
			steamButtons[1].parentElement.classList.remove("connected");
		});

		if (isReturn) {
			editPlatformsButton.click();
		}
	}

	// Canvasing

	const completionCanvas = document.querySelector("#profile-completion-canvas");

	const STROKE_WIDTH = 0.18;
	const style   = window.getComputedStyle(completionCanvas);
	const context = completionCanvas.getContext('2d');

	const drawCanvas = () => profileData.then(data => {
		const width  = Number(style.getPropertyValue('width').slice(0, -2));
		const height = width;
		
		context.canvas.width  = width;
		context.canvas.height = height;
		context.clearRect(0, 0, width, height);
		context.strokeStyle = root.getProperty('--accent-value3');
		context.lineWidth = (width / 2) * STROKE_WIDTH;
		context.beginPath();
		context.arc(width / 2, height / 2, (width / 2) * (1 - STROKE_WIDTH / 2), -0.5 * Math.PI, (-0.5 + (data.average === null ? 0 : (data.average / 100) * 2)) * Math.PI);
		context.stroke();
	});

	window.addEventListener('resize', drawCanvas);
	drawCanvas();

	if (profileId === session.id) {
		document.querySelector("#profile-page").classList.add("self");
	}

	{
		const noteworthy = document.querySelectorAll(".list-page-entry.achievement");
		for (const achievement of noteworthy) {
			achievement.addEventListener("click", (clickEvent) => {
				window.location.href = `/achievement/${achievement.dataset.id}`;
			});
		}
	}

	{
		const ratings = document.querySelectorAll(".list-page-entry.rating");
		for (const rating of ratings) {
			rating.addEventListener("click", (clickEvent) => {
				window.location.href = `/achievement/${rating.dataset.id}`;
			});
		}
	}
}

const expandTemplates = async () => {
	await commonTemplates();
	template.apply("profile-page").promise(profileData.then(data => ({
		id: profileId,
		username: data.username,
		completed: data.completed,
		average: data.average === null ? "N/A" : data.average + "%",
		perfect: data.perfect,
	})));
	template.apply("profile-noteworthy-list").promise(
		fetch(`/api/user/${profileId}/noteworthy`, {
			method: 'GET'
		})
		.then(response => response.json())
		.then(data => data.map(data => ({
				achievement_id:	data.ID,
				achievement_name: data.name,
				completion: data.completion
			}))
		)
	);
	template.apply("profile-platforms-list").promise(profileData.then(data =>
		data.platforms.map(platform => ({
			platform_id: platform.id,
			img: `<img class="profile-entry-icon" src="/api/platform/${platform.id}/image" alt="Steam Logo" />`,
			name: platform.name,
			connected: platform.connected ? "connected" : "",
			add:
				(platform.id === 0 ? `<img id="add-steam" class="platform-add" src="https://steamcdn-a.akamaihd.net/steamcommunity/public/images/steamworks_docs/english/sits_small.png" alt="Add" />` :
				(platform.id === 1 ? `<p class="platform-unsupported">Coming soon...</p>` :
				(platform.id === 2 ? `<p class="platform-unsupported">Coming soon...</p>` :
				"")))
		}))
	));
	template.apply("rating-list").promise(profileData.then(data => data.ratings.map(data => ({
		achievement_id: data.achievementId,
		rating_achievement: data.name,
		rating_difficulty: data.difficulty,
		rating_quality: data.quality,
		rating_review: data.review
	}))));
}

window.addEventListener("load", async (loadEvent) => {
	await loadCommon();

	var importing = document.querySelector("#importing");
	if (!/\d+/.test(profileId)) {
		isReturn = true;
		const platform = profileId;
		if  (!session.key) {
			window.location.href = "/404";
		} else {
			profileId = session.lastProfile;
			delete session.lastProfile;
		}

		const importingText = importing.querySelector("#importing-text");
		importingText.textContent = `Importing from ${platform}...`;
		importing.style.display = `flex`;
		if (platform === 'steam') {
			const query = new URLSearchParams(window.location.search);

			if (query.get('openid.mode') === 'cancel') {
				
			} else {
				// Regex courtesy of https://github.com/liamcurry/passport-steam/blob/master/lib/passport-steam/strategy.js
				var steamId = /^https?:\/\/steamcommunity\.com\/openid\/id\/(\d+)$/.exec(query.get('openid.claimed_id'))[1];
				await fetch(`/api/user/${profileId}/platforms/add`, {
					method: 'POST',
					headers: {
						'Content-Type': 'application/json'
					},
					body: JSON.stringify({ sessionKey: session.key, userId: profileId, platformId: 0, platformUserId: `${steamId}` })
				});
			}
		}

		window.history.replaceState({}, '', `/profile/${profileId}`);
	} else if (/\d+/.test(profileId)) {
		profileId = Number(profileId);
		if (session.key) {
			session.lastProfile = profileId;
		}
	} else {
		// Handle error
	}
	importing.remove();	

	profileData = fetch(`/api/user/${profileId}`, { method: 'GET' })
		.then(response => response.json());

	await expandTemplates();
	await template.expand();

	loadLazyImages();
	connectNavbar();
	loadProfile();
});