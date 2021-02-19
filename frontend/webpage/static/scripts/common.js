let root = null;
const loadRoot = () => {
	const rootElement = document.documentElement;

	root = {};
	root.getProperty = (name) => window.getComputedStyle(document.documentElement).getPropertyValue(name);
	root.setProperty = (name, value) => {
		rootElement.style.setProperty(name, value);
	}
};

let session = { id: null };
const clearSession = () => session = { id: null };
const loadSession = async () => {
	window.addEventListener('beforeunload', (beforeUnloadEvent) => {
		window.sessionStorage.setItem('session', JSON.stringify(session));
	});

	session = JSON.parse(window.sessionStorage.getItem('session')) || { id: -1 };
	if (session.hue) {
		root.setProperty('--accent-hue', session.hue);
	}

	if (session.id !== null) {
		await fetch(`/api/auth/refresh`, {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify({ key: session.key, id: session.id })
		})
		.then(async response => ({ status: response.status, data: await response.json() }))
		.then(response => {
			if (response.status !== 200 && window.location.pathname !== "/login") {
				session.id  = null;
				session.key = null;
				if (session.id !== -1) {
					window.location.href = "/login";
				}
			} else {
				session.key = response.data.key;
				session.id  = response.data.id;
				if (session.id === -1 && window.location.pathname !== '/import') {
					window.location.href = '/import';
				}
			}
		});
	}
};

const authenticate = (obj) => {
	obj.sessionKey = session.key;
	obj.userId     = session.id;
	return obj;
}

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
	if (session.id !== -1 && session.id !== null) {
		template.apply("navbar-section-right").values([
			{ item: "profile", title: "Profile" },
			{ item: "logout",  title: "Logout"  }
		]);
	} else {
		template.apply("navbar-section-right").values([
			{ item: "login", title: "Login / Create Account" }
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
	if (session.id !== -1) {
		const navItems = document.querySelectorAll(".navbar-item");

		if (!session.admin) {
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
					clearSession();
					window.location.href = "/login";
				});
			} else if (item.dataset.pageName === "profile") {
				item.addEventListener("click", (clickEvent) => window.location.href = `/user/${session.id}`);
			} else {
				item.addEventListener("click", (clickEvent) => window.location.href = `/${item.dataset.pageName}`);
			}
		}
	}
};