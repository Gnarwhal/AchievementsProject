const expandTemplates = async () => {
	await commonTemplates();
}

const loadFilters = () => {
	const filtersButton = document.querySelector("#filter-dropdown-stack");
	const filters = document.querySelector("#list-page-filters-flex");

	filtersButton.addEventListener("click", (clickEvent) => {
		filtersButton.classList.toggle("active");
		filters.classList.toggle("active");
	});
}

window.addEventListener("load", async (loadEvent) => {
	loadRoot();
	loadSession();

	await expandTemplates();
	await template.expand();

	connectNavbar();
	loadFilters();
});