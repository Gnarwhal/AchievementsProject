window.addEventListener("load", async (loadEvent) => {
	await loadCommon();

	await commonTemplates();
	await template.expand();

	connectNavbar();
});