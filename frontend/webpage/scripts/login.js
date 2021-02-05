window.addEventListener("load", (loadEvent) => {
	const fields = {
		email:    document.querySelector("#email"   ),
		username: document.querySelector("#username"),
		password: document.querySelector("#password"),
		confirm:  document.querySelector("#confirm" )
	};

	const createUser = document.querySelector("#create-user-button");
	const login      = document.querySelector("#login-button");

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

	const switchToCreateAction = (clickEvent) => {
		if (!frozen) {
			fields.username.style.display = "block";
			fields.confirm.style.display = "block";
			login.style.display = "none";
			header.textContent = "Create User";

			createUser.removeEventListener("click", switchToCreateAction);
			createUser.addEventListener("click", createUserAction);
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
				frozen = true;
				fetch('https://localhost:4730/create_user', {
					method: 'POST',
					mode: 'cors',
					headers: {
						'Content-Type': 'application/json'
					},
					body: JSON.stringify({ email: fields.email.value, username: fields.username.value, password: fields.password.value })
				})
				.then(async response => ({ status: response.status, data: await response.json() }))
				.then(response => {
					const data = response.data;
					if (response.status === 200) {
						window.sessionStorage.setItem('sessionKey', data.key);
						window.location.href = "/";
					} else if (response.status === 500) {
						raiseError([], "Internal server error :(");
					} else {
						if (data.code === 1) {
							raiseError([ "email" ], "A user with that email is already registered");
							fields.email.value = '';
						}
					}
				})
				.catch(error => {
					console.error(error);
					raiseError([], "Server error :(");
				}).then(() => frozen = false);
			}
		}
	};
	createUser.addEventListener("click", switchToCreateAction);

	const loginAction = (clickEvent) => {
		if (!frozen) {
			if (fields.email.value === '') {
				raiseError([ "email" ], "Email cannot be empty");
			} else if (fields.password.value === '') {
				raiseError([ "password" ], "Password cannot be empty");
			} else {
				frozen = true;
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
						window.sessionStorage.setItem('sessionKey', data.key);
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
				}).then(() => frozen = false);
			}
		}
	};
	login.addEventListener("click", loginAction);
});