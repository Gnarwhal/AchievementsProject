const expandTemplates = async () => {
	await commonTemplates();
}

const loadFilters = () => {
	const filtersButton  = document.querySelector("#filter-dropdown-stack");
	const filtersSection = document.querySelector("#list-page-filters-flex");

	filtersButton.addEventListener("click", (clickEvent) => {
		filtersButton.classList.toggle("active");
		filtersSection.classList.toggle("active");
	});

	const filterCheckboxes = document.querySelectorAll(".list-page-filter-checkbox");
	for (const checkbox of filterCheckboxes) {
		checkbox.parentElement.addEventListener("click", (clickEvent) => {
			checkbox.parentElement.classList.toggle("selected");
		})
	}
}

const loadCommonSearch = async () => {
	await loadCommon();

	await expandTemplates();
};