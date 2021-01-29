const expandTemplates = async () => {
	template.register("navbar", [
		{ section: "left" },
		{ section: "right" }
	]);
	template.register("navbar-section-left", [
		{ item: "games",        title: "Games"        },
		{ item: "achievements", title: "Achievements" }
	]);
	template.register("navbar-section-right", [
		{ item: "profile", title: "Profile" }
	]);
	template.register("content-body", [
		{ page: "games",        title: "Games"        },
		{ page: "achievements", title: "Achievements" },
		{ page: "profile",      title: "Profile"      }
	]);
	template.register("fetch-games-page",        "games_page"       );
	template.register("fetch-achievements-page", "achievements_page");
	template.register("fetch-profile-page",      "profile_page"     );
	template.registerFetch("achievements-page-list", "Achievements", "https://localhost:4730/achievements", { method: 'GET', mode: 'cors' });
	
	await template.expand();
};

let pages = null;
const loadPages = () => {
	pages = document.querySelectorAll(".page");
}

const connectNavbar = () => {
	const navItems = document.querySelectorAll(".navbar-item");

	for (const item of navItems) {
		item.addEventListener("click", (clickEvent) => {
			const navItemPageId = item.dataset.pageName + "-page"
			for (const page of pages) {
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
		for (const page of pages) {
			if (page.id === "games-page") {
				page.style.display = "block";
			} else {
				page.style.display = "none";
			}
		}
	});

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

window.addEventListener("load", async (loadEvent) => {
	await expandTemplates();

	loadPages();

	connectNavbar();
	connectProfile();

	loadFilters();
});
