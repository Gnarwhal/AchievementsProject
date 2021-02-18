let root = null;
const loadRoot = () => {
	const rootElement = document.documentElement;

	root = {};
	root.getProperty = (name) => window.getComputedStyle(document.documentElement).getPropertyValue(name);
	root.setProperty = (name, value) => {
		rootElement.style.setProperty(name, value);
	}
};

let session = null;
const loadSession = async () => {
	window.addEventListener('beforeunload', (beforeUnloadEvent) => {
		if (session) {
			window.sessionStorage.setItem('session', JSON.stringify(session));
		} else {
			window.sessionStorage.removeItem('session');
		}
	});

	session = JSON.parse(window.sessionStorage.getItem('session'));
	if (session) {
		root.setProperty('--accent-hue', session.hue);

		await fetch(`/api/auth/refresh`, {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify({ key: session.key })
		})
		.then(async response => ({ status: response.status, data: await response.json() }))
		.then(response => {
			if (response.status !== 200 && window.location.pathname != "/login") {
				delete session.key;
				window.location.href = "/login";
			}
		});
	}
};

const loadCommon = async () => {
	loadRoot();
	await loadSession();
}

const commonTemplates = async () => {
	template.apply("navbar").values([
		{ section: "left" },
		{ section: "right" }
	]);
	template.apply("navbar-section-left").values([
		{ item: "achievements", title: "Achievements" },
		{ item: "users",        title: "Users"        },
		{ item: "games",        title: "Games"        },
		{ item: "import",       title: "Import"       }
	]);
	if (session) {
		template.apply("navbar-section-right").values([
			{ item: "profile", title: "Profile" },
			{ item: "logout",  title: "Logout"  }
		]);
	} else {
		template.apply("navbar-section-right").values([
			{ item: "login", title: "Login" }
		]);
	}
};

const loadLazyImages = () => {
	const imgs = document.querySelectorAll(".lazy-img");
	for (const img of imgs) {
		img.src = img.dataset.src;
	}
}

const connectNavbar = () => {
	const navItems = document.querySelectorAll(".navbar-item");

	if (!session || !session.admin) {
		document.querySelector("#navbar-item-import").remove();
	}

	for (const item of navItems) {
		if (item.dataset.pageName === "logout") {
			item.addEventListener("click", (clickEvent) => {
				fetch(`/api/auth/logout`, {
					method: 'POST',
					headers: {
						'Content-Type': 'application/json'
					},
					body: JSON.stringify({ key: session.key })
				});
				session = undefined;
				window.location.href = "/login";
			});
		} else if (item.dataset.pageName === "profile") {
			item.addEventListener("click", (clickEvent) => window.location.href = `/profile/${session.id}`);
		} else {
			item.addEventListener("click", (clickEvent) => window.location.href = `/${item.dataset.pageName}`);
		}
	}
};