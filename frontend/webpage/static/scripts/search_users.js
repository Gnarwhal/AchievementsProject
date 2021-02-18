let templateList = null;
let templateText = null;
const saveTemplate = () => {
	const templateElement = document.querySelector("#user-list-template");
	templateList = templateElement.parentElement;
	templateText = templateElement.outerHTML;
	templateElement.remove();
};

const loadUserSearch = () => {
	const loading = document.querySelector("#loading-results");


	const searchButton = document.querySelector("#user-search-button");
	const searchField  = document.querySelector("#user-search-field" );

	const minOwned         = document.querySelector("#min-owned-filter"         );
	const maxOwned         = document.querySelector("#max-owned-filter"         );
	const minCompleted     = document.querySelector("#min-completed-filter"     );
	const maxCompleted     = document.querySelector("#max-completed-filter"     );
	const minAvgCompletion = document.querySelector("#min-avg-completion-filter");
	const maxAvgCompletion = document.querySelector("#max-avg-completion-filter");

	let ordering  = 'name';
	let direction = true;
	let canSearch = true;
	const loadList = async () => {
		if (canSearch) {
			canSearch = false;

			const body = {
				searchTerm:       searchField.value,
				minOwned:         minOwned.value         === '' ? null : Number(minOwned.value        ),
				maxOwned:         maxOwned.value         === '' ? null : Number(maxOwned.value        ),
				minCompleted:     minCompleted.value     === '' ? null : Number(minCompleted.value    ),
				maxCompleted:     maxCompleted.value     === '' ? null : Number(maxCompleted.value    ),
				minAvgCompletion: minAvgCompletion.value === '' ? null : Number(minAvgCompletion.value),
				maxAvgCompletion: maxAvgCompletion.value === '' ? null : Number(maxAvgCompletion.value),
			};
			let successful = true;
			if (Number.isNaN(body.minOwned        )) { successful = false; minOwned.style.backgroundColor         = 'var(--error)'; } else { minOwned.style.backgroundColor         = 'var(--foreground)'; }
			if (Number.isNaN(body.maxOwned        )) { successful = false; maxOwned.style.backgroundColor         = 'var(--error)'; } else { maxOwned.style.backgroundColor         = 'var(--foreground)'; }
			if (Number.isNaN(body.minCompleted    )) { successful = false; minCompleted.style.backgroundColor     = 'var(--error)'; } else { minCompleted.style.backgroundColor     = 'var(--foreground)'; }
			if (Number.isNaN(body.maxCompleted    )) { successful = false; maxCompleted.style.backgroundColor     = 'var(--error)'; } else { maxCompleted.style.backgroundColor     = 'var(--foreground)'; }
			if (Number.isNaN(body.minAvgCompletion)) { successful = false; minAvgCompletion.style.backgroundColor = 'var(--error)'; } else { minAvgCompletion.style.backgroundColor = 'var(--foreground)'; }
			if (Number.isNaN(body.maxAvgCompletion)) { successful = false; maxAvgCompletion.style.backgroundColor = 'var(--error)'; } else { maxAvgCompletion.style.backgroundColor = 'var(--foreground)'; }

			if (!successful) {
				canSearch = true;
				return;
			}

			for (const entry of templateList.querySelectorAll(".list-page-entry")) {
				entry.remove();
			}
			templateList.innerHTML += templateText;
			loading.style.display = 'block';

			const data = fetch("/api/users", {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json'
				},
				body: JSON.stringify(body)
			})
			.then(response => response.json())

			template.clear();
			template.apply('user-page-list').promise(data.then(data => data.map(item => ({
				user_id:           item.id,
				username:          item.username,
				game_count:        item.game_count,
				achievement_count: item.achievement_count,
				avg_completion:    item.avg_completion == null ? 'N/A' : `${item.avg_completion}%`,
				perfect_games:     item.perfect_games
			}))));
			await template.expand();
			data.then(data => {
				loading.style.display = 'none';
				canSearch = true;
				loadLazyImages();

				const entries = document.querySelectorAll(".list-page-entry.user");
				for (const entry of entries) {
					entry.addEventListener("click", (clickEvent) => {
						window.location.href = `/user/${entry.dataset.id}`;
					});
				}
			});

			const headers = {
				username:          document.querySelector(".list-page-header > .user-username"         ),
				game_count:        document.querySelector(".list-page-header > .user-game-count"       ),
				achievement_count: document.querySelector(".list-page-header > .user-achievement-count"),
				avg_completion:    document.querySelector(".list-page-header > .user-avg-completion"   ),
				perfect_games:     document.querySelector(".list-page-header > .user-perfect-games"    ),
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
	
	await loadUserSearch();
});