let templateList = null;
let templateText = null;
const saveTemplate = () => {
	const templateElement = document.querySelector("#game-list-template");
	templateList = templateElement.parentElement;
	templateText = templateElement.outerHTML;
	templateElement.remove();
};

const loadGameSearch = () => {
	const loading = document.querySelector("#loading-results");

	const personal = document.querySelector("#personal-filters");
	if (!session) {
		personal.style.display = 'none';
	}

	const searchButton = document.querySelector("#game-search-button");
	const searchField  = document.querySelector("#game-search-field" );

	const owned            = document.querySelector("#owned-filter"             );
	const minAvgCompletion = document.querySelector("#min-avg-completion-filter");
	const maxAvgCompletion = document.querySelector("#max-avg-completion-filter");
	const minNumOwners     = document.querySelector("#min-num-owners-filter"    );
	const maxNumOwners     = document.querySelector("#max-num-owners-filter"    );
	const minNumPerfects   = document.querySelector("#min-num-perfects-filter"  );
	const maxNumPerfects   = document.querySelector("#max-num-perfects-filter"  );

	let ordering  = 'name';
	let direction = true;
	let canSearch = true;
	const loadList = async () => {
		if (canSearch) {
			canSearch = false;

			const body = {
				searchTerm:       searchField.value,
				userId:           owned.classList.contains('selected') ? session.id : null,
				owned:            owned.classList.contains('selected') ? true : null,
				minAvgCompletion: minAvgCompletion.value === '' ? null : Number(minAvgCompletion.value),
				maxAvgCompletion: maxAvgCompletion.value === '' ? null : Number(maxAvgCompletion.value),
				minNumOwners:     minNumOwners.value     === '' ? null : Number(minNumOwners.value    ),
				maxNumOwners:     maxNumOwners.value     === '' ? null : Number(maxNumOwners.value    ),
				minNumPerfects:   minNumPerfects.value   === '' ? null : Number(minNumPerfects.value  ),
				maxNumPerfects:   maxNumPerfects.value   === '' ? null : Number(maxNumPerfects.value  ),
				ordering:         ordering,
				orderDirection:   direction ? 'ASC' : 'DESC',
			};
			let successful = true;
			if (Number.isNaN(body.minAvgCompletion)) { successful = false; minAvgCompletion.style.backgroundColor = 'var(--error)'; } else { minAvgCompletion.style.backgroundColor = 'var(--foreground)'; }
			if (Number.isNaN(body.maxAvgCompletion)) { successful = false; maxAvgCompletion.style.backgroundColor = 'var(--error)'; } else { maxAvgCompletion.style.backgroundColor = 'var(--foreground)'; }
			if (Number.isNaN(body.minNumOwners))     { successful = false; minNumOwners.style.backgroundColor     = 'var(--error)'; } else { minNumOwners.style.backgroundColor     = 'var(--foreground)'; }
			if (Number.isNaN(body.maxNumOwners))     { successful = false; maxNumOwners.style.backgroundColor     = 'var(--error)'; } else { maxNumOwners.style.backgroundColor     = 'var(--foreground)'; }
			if (Number.isNaN(body.minNumPerfects))   { successful = false; minNumPerfects.style.backgroundColor   = 'var(--error)'; } else { minNumPerfects.style.backgroundColor   = 'var(--foreground)'; }
			if (Number.isNaN(body.maxNumPerfects))   { successful = false; maxNumPerfects.style.backgroundColor   = 'var(--error)'; } else { maxNumPerfects.style.backgroundColor   = 'var(--foreground)'; }

			if (!successful) {
				canSearch = true;
				return;
			}

			for (const entry of templateList.querySelectorAll(".list-page-entry")) {
				entry.remove();
			}
			templateList.innerHTML += templateText;
			loading.style.display = 'block';

			const data = fetch("/api/games", {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json'
				},
				body: JSON.stringify(body)
			})
			.then(response => response.json())

			template.clear();
			template.apply('games-page-list').promise(data.then(data => data.map(item => ({
				game_id:           item.ID,
				game_name:         item.name,
				achievement_count: item.achievement_count,
				avg_completion:    item.avg_completion == null ? 'N/A' : `${item.avg_completion}%`,
				num_owners:        item.num_owners,
				num_perfects:      item.num_perfects,
			}))));
			await template.expand();
			data.then(data => {
				console.log(data);
				loading.style.display = 'none';
				canSearch = true;
				loadLazyImages();
			});
	
			const headers = {
				game:              document.querySelector(".list-page-header > .game-name"             ),
				achievement_count: document.querySelector(".list-page-header > .game-achievement-count"),
				avg_completion:    document.querySelector(".list-page-header > .game-avg-completion"   ),
				num_owners:        document.querySelector(".list-page-header > .game-num-owners"       ),
				num_perfects:      document.querySelector(".list-page-header > .game-num-perfects"     ),
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
	
	await loadGameSearch();
});