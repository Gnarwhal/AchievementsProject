var template = template || {};

template.type = {};
template.type._entryMap = new Map();
template.type.register = (type, callback) => {
	const asyncCallback = async function() {
		await callback.apply(null, Array.from(arguments));
	}
	if (typeof type !== 'string') {
		console.error(`'type' must be a string, recieved: `, type);
	} else {
		if (type === "") {
			console.error(`'type' may not be empty`);
		} else if (template.type._entryMap.get(type) === undefined) {
			template.type._entryMap.set(type, asyncCallback);
		} else {
			console.error(`${type} is already a registered template!`);
		}
	}
};

/* Intrinsic Templates */

// Basic - Simple search and replace
template.type.register('basic', (template, map) => {
	let html = template.element.innerHTML;
	for (const key in map) {
		html = html.replace(new RegExp(`(?:(?<!(?<!\\\\)\\\\)\\\${${key}})`, 'gm'), map[key]);
	}
	template.element.outerHTML = html.trim();
});

// Fetch - Retrieve template from webserver
template.type.register('fetch', (template, name) => {
	return fetch(`templates/${name}.html.template`, {
			method: 'GET',
			mode: 'no-cors',
			headers: {
				'Content-Type': 'text/plain'
			}
		}).then(response => response.text().then((data) => {
			template.element.outerHTML = data;
		})).catch(error => {
			console.error(`failed to retrieve template '${name}': `, error);
		});
});

// List - Iterate over list and emit copy of child for each iteration
template.type.register('list', (template, arrayMap) => {
	let cumulative = "";
	for (const map of arrayMap) {
		let html = template.element.innerHTML;
		for (const key in map) {
			html = html.replace(new RegExp(`(?:(?<!(?<!\\\\)\\\\)\\\${${template.id}\\[${key}\\]})`, 'gm'), map[key]);
		}
		cumulative = cumulative + html.trim();
	}
	template.element.outerHTML = cumulative;
});


template._entryMap = new Map();
template.register = function(pattern/*, arguments */) {
	if (typeof pattern !== 'string') {
		console.error('pattern must be a string, received: ', pattern);
	} else {
		const arrayArguments = Array.from(arguments);
		arrayArguments.splice(0, 1);
		template._entryMap.set(RegExp("^" + pattern + "$"), async () => arrayArguments);
	}
};
template.registerFetch = function(pattern, path, url, fetchOptions) {
	if (typeof pattern !== 'string') {
		console.error('pattern must be a string, received: ', pattern);
	} else {
		let args = null;
		template._entryMap.set(RegExp("^" + pattern + "$"), async () => {
			if (args !== null) {
				return Promise.resolve(args);
			} else {
				return fetch(url, fetchOptions)
					.then(response => response.json())
					.then(data => {
						args = data;
						path = path.split('\\.');
						for (const id of path) {
							args = args[id];
						}
						return args = [ args ];
					});
			}
		});
	}
};

(() => {
	const findTemplates = (element) =>
		Array
			.from(element.querySelectorAll("template"))
			.filter(child => Boolean(child.dataset.template))
			.map(child => {
				const data = child.dataset.template.split(/\s*:\s*/);
				return {
					id: data[0],
					type: data.length > 1 ? data[1] : 'basic',
					element: child
				};
			});

	const expand = async (element) => {
		let children = findTemplates(element);
		let promises = [];
		let parents  = new Set();
		for (const child of children) {
			for (const [pattern, argCallbacks] of template._entryMap) {
				await argCallbacks().then(args => {
					if (pattern.test(child.id)) {
						const callback = template.type._entryMap.get(child.type);
						if (typeof callback !== 'function') {
							console.error(`'${child.type}' is not a registered template type`);
						} else {
							let params = Array.from(args)
							params.splice(0, 0, child);
							let parent = child.element.parentElement;
							if (!parents.has(parent)) {
								parents.add(parent);
							}
							promises.push(callback.apply(null, params));
						}
					}
				}).catch(error => {
					console.error('failed to retrieve arguments: ', error);
				});
			}
		}
		await Promise.all(promises);
		promises = [];
		for (const parent of parents) {
			promises.push(expand(parent));
		}
		await Promise.all(promises);
	}

	template.expand = async () => expand(document.children[0]);
})();
