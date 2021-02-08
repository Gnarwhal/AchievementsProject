let session = null;
const loadSession = () => {
	session = JSON.parse(window.sessionStorage.getItem('session'));
	if (session) {
		document.querySelector(":root").style.setProperty('--accent-hue', session.hue);
	}
};

const expandTemplates = async () => {
	template.apply("navbar").values([
		{ section: "left" },
		{ section: "right" }
	]);
	template.apply("navbar-section-left").values([
		{ item: "games",        title: "Games"        },
		{ item: "achievements", title: "Achievements" }
	]);
	if (session) {
		template.apply("navbar-section-right").values([
			{ item: "profile", title: "Profile" },
			{ item: "logout",  title: "Logout"  }
		]);
	} else {
		template.apply("navbar-section-right").values([
			{ item: "login",   title: "Login" }
		]);
	}
	template.apply("content-body").values([
		{ page: "games",        title: "Games"        },
		{ page: "achievements", title: "Achievements" },
		{ page: "profile",      title: "Profile"      }
	]);
	template.apply("extern-games-page"       ).values("games_page"       );
	template.apply("extern-achievements-page").values("achievements_page");
	template.apply("extern-profile-page"     ).values("profile_page"     );

	await template.expand();
};

let pages = null;
const loadPages = () => {
	pages = document.querySelectorAll(".page");
}

const connectNavbar = () => {
	const navItems = document.querySelectorAll(".navbar-item");

	for (const item of navItems) {
		if (item.dataset.pageName === "logout") {
			item.addEventListener("click", (clickEvent) => {
				fetch('https://localhost:4730/logout', {
					method: 'POST',
					mode: 'cors',
					headers: {
						'Content-Type': 'application/json'
					},
					body: JSON.stringify({ key: session.key })
				})
				.then(response => {
					window.sessionStorage.removeItem('session');
					window.location.href = "/login.html";
				});
			});
		} else if (item.dataset.pageName === "login") {
			item.addEventListener("click", (clickEvent) => window.location.href = "/login.html");
		} else {
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
	}
};

const connectProfile = () => {
	const games        = document.querySelector("#profile-games-header");
	const achievements = document.querySelector("#profile-achievements-header");

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
	loadSession();

	await expandTemplates();

	loadPages();

	connectNavbar();
	connectProfile();

	loadFilters();
});
