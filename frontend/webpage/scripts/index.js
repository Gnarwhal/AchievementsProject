const expandTemplates = async () => {
	template.apply("navbar").values([
		{ section: "left" },
		{ section: "right" }
	]);
	template.apply("navbar-section-left").values([
		{ item: "games",        title: "Games"        },
		{ item: "achievements", title: "Achievements" }
	]);
	template.apply("navbar-section-right").values([
		{ item: "profile", title: "Profile" }
	]);
	template.apply("content-body").values([
		{ page: "games",        title: "Games"        },
		{ page: "achievements", title: "Achievements" },
		{ page: "profile",      title: "Profile"      }
	]);
	template.apply("extern-games-page"       ).values("games_page"       );
	template.apply("extern-achievements-page").values("achievements_page");
	template.apply("extern-profile-page"     ).values("profile_page"     );
	template.apply("achievements-page-list"  ).fetch("achievements", "https://localhost:4730/achievements");

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
