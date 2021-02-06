window.addEventListener("load", (loadEvent) => {
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

	const switchToCreateAction = (clickEvent) => {
		if (!frozen) {
			fields.username.style.display = "block";
			fields.confirm.style.display = "block";
			header.textContent = "Create Account";

			createUser.removeEventListener("click", switchToCreateAction);
			createUser.addEventListener("click", createUserAction);

			login.removeEventListener("click", loginAction);
			login.addEventListener("click", switchToLoginAction);
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
				.then(response =>{
					const data = response.data;
					if (response.status === 200) {
						window.sessionStorage.setItem('sessionKey', data.key);
						window.sessionStorage.setItem('id',         data.id );
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
				}).then(() => frozen = false);
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
		}
	};
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
						console.log(data);
						window.sessionStorage.setItem('sessionKey', data.key);
						window.sessionStorage.setItem('id',         data.id );
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

	guest.addEventListener("click", (clickEvent) => {
		if (!frozen) {
			frozen = true;
			fetch('https://localhost:4730/login?guest=true', {
				method: 'POST',
				mode: 'cors',
				headers: {
					'Content-Type': 'application/json'
				},
				body: "{}"
			})
			.then(async response => ({ status: response.status, data: await response.json() }))
			.then(response => {
				const data = response.data;
				if (response.status === 200) {
					console.log(data);
					window.sessionStorage.setItem('sessionKey', data.key);
					window.sessionStorage.setItem('id',         data.id );
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
	});
});