window.addEventListener("load", (loadEvent) => {
	let session = window.sessionStorage.getItem('session');
	if (session) {
		window.location.href = '/';
	}

	const fields = {
		email:    document.querySelector("#email"   ),
		username: document.querySelector("#username"),
		password: document.querySelector("#password"),
		confirm:  document.querySelector("#confirm" )
	};

	const createUser = document.querySelector("#create-user-button");
	const login      = document.querySelector("#login-button");
	const guest      = document.querySelector("#guest-login-button");

	const header = document.querySelector("#login-header-text");
	const error  = document.querySelector("#error-message");

	const raiseError = (errorFields, message) => {
		for (const key in fields) {
			if (errorFields.includes(key)) {
				fields[key].classList.add("error");
			} else {
				fields[key].classList.remove("error");
			}
		}

		error.style.display = "block";
		error.textContent = message;
	}

	let frozen = false;
	const freeze = () => {
		frozen = true;
		createUser.classList.add("disabled");
		login.classList.add("disabled");
		guest.classList.add("disabled");
	};
	const unfreeze = () => {
		frozen = false;
		createUser.classList.remove("disabled");
		login.classList.remove("disabled");
		guest.classList.remove("disabled");
	};

	const switchToCreateAction = (clickEvent) => {
		if (!frozen) {
			fields.username.style.display = "block";
			fields.confirm.style.display = "block";
			header.textContent = "Create Account";

			createUser.removeEventListener("click", switchToCreateAction);
			createUser.addEventListener("click", createUserAction);

			login.removeEventListener("click", loginAction);
			login.addEventListener("click", switchToLoginAction);

			activeAction = createUserAction;
		}
	};
	const createUserAction = (clickEvent) => {
		if (!frozen) {
			if (fields.email.value === '') {
				raiseError([ "email" ], "Email cannot be empty");
			} else if (fields.username.value === '') {
				raiseError([ "username" ], "Username cannot be empty");
			} else if (fields.password.value !== fields.confirm.value) {
				raiseError([ "password", "confirm" ], "Password fields did not match");
			} else if (fields.password.value === '') {
				raiseError([ "password", "confirm" ], "Password cannot be empty");
			} else {
				freeze();
				fetch('https://localhost:4730/create_user', {
					method: 'POST',
					mode: 'cors',
					headers: {
						'Content-Type': 'application/json'
					},
					body: JSON.stringify({ email: fields.email.value, username: fields.username.value, password: fields.password.value })
				})
				.then(async response => ({ status: response.status, data: await response.json() }))
				.then(response =>{
					const data = response.data;
					if (response.status === 200) {
						window.sessionStorage.setItem('session', JSON.stringify(data));
						window.location.href = "/";
					} else if (response.status === 500) {
						raiseError([], "Internal server error :(");
					} else {
						if (data.code === 1) {
							raiseError([ "email" ], "A user with that email is already registered");
							fields.email.value = '';
						} else if (data.code === 2) {
							raiseError([ "email" ], "Invalid email address");
							fields.email.value = '';
						}
					}
				})
				.catch(error => {
					console.error(error);
					raiseError([], "Server error :(");
				}).then(unfreeze);
			}
		}
	};
	createUser.addEventListener("click", switchToCreateAction);

	const switchToLoginAction = (clickEvent) => {
		if (!frozen) {
			fields.username.style.display = "none";
			fields.confirm.style.display = "none";
			header.textContent = "Login";

			createUser.removeEventListener("click", createUserAction);
			createUser.addEventListener("click", switchToCreateAction);

			login.removeEventListener("click", switchToLoginAction);
			login.addEventListener("click", loginAction);

			activeAction = loginAction;
		}
	};
	const loginAction = (clickEvent) => {
		if (!frozen) {
			if (fields.email.value === '') {
				raiseError([ "email" ], "Email cannot be empty");
			} else if (fields.password.value === '') {
				raiseError([ "password" ], "Password cannot be empty");
			} else {
				freeze();
				fetch('https://localhost:4730/login', {
					method: 'POST',
					mode: 'cors',
					headers: {
						'Content-Type': 'application/json'
					},
					body: JSON.stringify({ email: fields.email.value, password: fields.password.value })
				})
				.then(async response => ({ status: response.status, data: await response.json() }))
				.then(response => {
					const data = response.data;
					if (response.status === 200) {
						console.log(data);
						window.sessionStorage.setItem('session', JSON.stringify(data));
						window.location.href = "/";
					} else if (response.status === 500) {
						raiseError([], "Internal server error :(");
					} else {
						raiseError([ "email", "password" ], "Email or password is incorrect");
						fields.password.value = '';
					}
				})
				.catch(error => {
					console.error(error);
					raiseError([], "Unknown error :(");
				}).then(unfreeze);
			}
		}
	};
	login.addEventListener("click", loginAction);

	guest.addEventListener("click", (clickEvent) => {
		if (!frozen) {
			window.location.href = '/';
		}
	});

	let activeAction = loginAction;
	for (const field in fields) {
		fields[field].addEventListener("keydown", (keyEvent) => {
			if (keyEvent.key === "Enter") {
				activeAction();
			}
		})
	}
});