let pages = null;
const loadPages = () => {	
	pages = document.querySelectorAll(".page");
}

const connectNavbar = () => {
	const navItems = document.querySelectorAll(".navbar-item");

	for (let item of navItems) {
		item.addEventListener("click", (clickEvent) => {
			const navItemPageId = item.dataset.pageName + "-page"
			for (page of pages) {
				if (page.id === navItemPageId) {
					page.style.display = "block";
				} else {
					page.style.display = "none";
				}
			}
		});
	}
};

const connectProfile = () => {
	const games        = document.querySelector("#profile-games");
	const achievements = document.querySelector("#profile-achievements");

	games.children[0].addEventListener("click", (clickEvent) => {
		for (page of pages) {
			if (page.id === "games-page") {
				page.style.display = "block";
			} else {
				page.style.display = "none";
			}
		}
	});

	console.log(achievements.firstElement);
	achievements.children[0].addEventListener("click", (clickEvent) => {
		for (page of pages) {
			if (page.id === "achievements-page") {
				page.style.display = "block";
			} else {
				page.style.display = "none";
			}
		}
	});
}

const loadFilters = () => {
	const filters = document.querySelectorAll(".list-page-filter");
	for (let filter of filters) {
		filter.addEventListener("click", (clickEvent) => {
			if (filter.classList.contains("selected")) {
				filter.classList.remove("selected");
			} else {
				filter.classList.add("selected");
			}
		});
	}
}

window.addEventListener("load", (loadEvent) => {
	loadPages();

	connectNavbar();
	connectProfile();

	loadFilters();
});
