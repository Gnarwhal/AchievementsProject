let consoleTop = true;
let importConsole = null;
const appendLine = (line) => {
	const template = document.createElement("template");
	template.innerHTML = `<p class="console-entry ${consoleTop ? 'top' : ''}">${line}</p>`
	importConsole.appendChild(template.content.firstElementChild);
	consoleTop = false;
};

const loadConsole = () => {
	importConsole = document.querySelector("#import-console");

	const dropzone = document.querySelector("#import-dropzone");
	const uploadWrapper = document.querySelector("#upload-wrapper");
	const upload = (dropEvent) => {
		dropEvent.preventDefault();

		dropzone.classList.remove('active');
		if (dropEvent.dataTransfer.files) {
			const file = dropEvent.dataTransfer.files[0];
			if (file.type === 'application/json') {
				importConsole.style.display = 'block';
				uploadWrapper.style.display = 'none';
				file.text().then(data => JSON.parse(data)).then(data => {
					let uploads = Promise.resolve();
					for (let i = 0; i < data.platforms.length; ++i) {
						const platform = data.platforms[i];
						uploads = uploads
							.then(() => {
								appendLine(`(${i + 1}/${data.platforms.length}) Creating platform: ${platform.name}`);
							}).then(() => fetch(
								'/api/import/platform', {
									method: 'POST',
									headers: {
										'Content-Type': 'application/json'
									},
									body: JSON.stringify(authenticate(platform))
								}
							)
						);
					}
					for (let i = 0; i < data.users.length; ++i) {
						const user = data.users[i];
						const userPlatforms = user.platforms;
						delete user.platforms;
						uploads = uploads
							.then(() => {
								appendLine(`(${i + 1}/${data.users.length}) Creating user: ${user.username}`);
							}).then(() => fetch(
								'/api/import/user', {
									method: 'POST',
									headers: {
										'Content-Type': 'application/json'
									},
									body: JSON.stringify(authenticate(user))
								}
							)
						);
						for (let j = 0; j < userPlatforms.length; ++j) {
							const platform = userPlatforms[j];
							platform.userEmail = user.email;
							uploads = uploads
								.then(() => {
									appendLine(`&nbsp;&nbsp;&nbsp;&nbsp;(${j + 1}/${userPlatforms.length}) Importing platform data: ${data.platforms[platform.platformId].name}`);
								}).then(() => fetch(
									'/api/import/user/platform', {
										method: 'POST',
										headers: {
											'Content-Type': 'application/json'
										},
										body: JSON.stringify(authenticate(platform))
									}
								)
							);
						}
					}
					uploads = uploads.then(() => {
						if (session.id === -1) {
							clearSession();
							window.location.href = '/login';
						} else {
							importConsole.innerHTML = '';
							importConsole.style.display = 'none';
							uploadWrapper.style.display = 'block';
						}
					});
				});
			}
		}
	};
	dropzone.addEventListener("drop", upload);
	dropzone.addEventListener("dragover", (dragEvent) => {
		dragEvent.preventDefault();
	});
};

window.addEventListener("load", async (loadEvent) => {
	await loadCommon();

	await commonTemplates();
	await template.expand();

	connectNavbar();
	loadConsole();
});