let templateList = null;
let templateText = null;
const saveTemplate = () => {
	const templateElement = document.querySelector("#achievement-list-template");
	templateList = templateElement.parentElement;
	templateText = templateElement.outerHTML;
	templateElement.remove();
};

const loadAchievementSearch = () => {
	const loading = document.querySelector("#loading-results");

	const personal = document.querySelector("#personal-filters");
	if (!session) {
		personal.style.display = 'none';
	}

	const searchButton = document.querySelector("#achievement-search-button");
	const searchField  = document.querySelector("#achievement-search-field" );

	const completed     = document.querySelector("#completed-filter");
	const minCompletion = document.querySelector("#min-completion-filter");
	const maxCompletion = document.querySelector("#max-completion-filter");
	const minDifficulty = document.querySelector("#min-difficulty-filter");
	const maxDifficulty = document.querySelector("#max-difficulty-filter");
	const minQuality    = document.querySelector("#min-quality-filter"   );
	const maxQuality    = document.querySelector("#max-quality-filter"   );

	let ordering  = 'name';
	let direction = true;
	let canSearch = true;
	const loadList = async () => {
		if (canSearch) {
			canSearch = false;

			const body = {
				searchTerm:     searchField.value,
				userId:         completed.classList.contains('selected') ? session.id : null,
				completed:      completed.classList.contains('selected') ? true : null,
				minCompletion:  minCompletion.value === '' ? null : Number(minCompletion.value),
				maxCompletion:  maxCompletion.value === '' ? null : Number(maxCompletion.value),
				minDifficulty:  minDifficulty.value === '' ? null : Number(minDifficulty.value),
				maxDifficulty:  maxDifficulty.value === '' ? null : Number(maxDifficulty.value),
				minQuality:     minQuality.value    === '' ? null : Number(minQuality.value   ),
				maxQuality:     maxQuality.value    === '' ? null : Number(maxQuality.value   ),
				ordering:       ordering,
				orderDirection: direction ? 'ASC' : 'DESC',
			};
			let successful = true;
			if (Number.isNaN(body.minCompletion)) { successful = false; minCompletion.style.backgroundColor = 'var(--error)'; } else { minCompletion.style.backgroundColor = 'var(--foreground)'; }
			if (Number.isNaN(body.maxCompletion)) { successful = false; maxCompletion.style.backgroundColor = 'var(--error)'; } else { maxCompletion.style.backgroundColor = 'var(--foreground)'; }
			if (Number.isNaN(body.minDifficulty)) { successful = false; minDifficulty.style.backgroundColor = 'var(--error)'; } else { minDifficulty.style.backgroundColor = 'var(--foreground)'; }
			if (Number.isNaN(body.maxDifficulty)) { successful = false; maxDifficulty.style.backgroundColor = 'var(--error)'; } else { maxDifficulty.style.backgroundColor = 'var(--foreground)'; }
			if (Number.isNaN(body.minQuality   )) { successful = false; minQuality.style.backgroundColor    = 'var(--error)'; } else { minQuality.style.backgroundColor    = 'var(--foreground)'; }
			if (Number.isNaN(body.maxQuality   )) { successful = false; maxQuality.style.backgroundColor    = 'var(--error)'; } else { maxQuality.style.backgroundColor    = 'var(--foreground)'; }

			if (!successful) {
				canSearch = true;
				return;
			}

			for (const entry of templateList.querySelectorAll(".list-page-entry")) {
				entry.remove();
			}
			templateList.innerHTML += templateText;
			loading.style.display = 'block';

			const data = fetch("/api/achievements", {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json'
				},
				body: JSON.stringify(body)
			})
			.then(response => response.json())

			template.clear();
			template.apply('achievements-page-list').promise(data.then(data => data.map(item => ({
				achievement_id:   item.ID,
				achievement_name: item.name,
				game_name:  item.game,
				completion: item.completion == null ? 'N/A' : `${item.completion}%`,
				difficulty: item.difficulty == null ? 'N/A' : `${item.difficulty} / 10`,
				quality:    item.quality    == null ? 'N/A' : `${item.quality} / 10`
			}))));
			await template.expand();
			data.then(data => {
				loading.style.display = 'none';
				canSearch = true;
				loadLazyImages();
			});
	
			const headers = {
				game:       document.querySelector(".list-page-header > .achievement-game-name" ),
				name:       document.querySelector(".list-page-header > .achievement-name"      ),
				completion: document.querySelector(".list-page-header > .achievement-completion"),
				difficulty: document.querySelector(".list-page-header > .achievement-difficulty"),
				quality:    document.querySelector(".list-page-header > .achievement-quality"   ),
			}
			for (const header in headers) {
				headers[header].addEventListener("click", (clickEvent) => {
					if (ordering === header) {
						direction = !direction;
					} else {
						ordering = header;
						direction = true;
					}
					loadList();
				});
			}
		}
	};

	searchButton.addEventListener("click", loadList);
	searchField.addEventListener("keydown", (keyEvent) => {
		if (keyEvent.key === 'Enter') {
			loadList();
		}
	});

	loadList();
};

window.addEventListener("load", async (loadEvent) => {
	await loadCommonSearch();

	saveTemplate();
	await template.expand();

	connectNavbar();
	loadFilters();
	
	await loadAchievementSearch();
});