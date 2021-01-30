var template = template || {};

template.type = {};
template.type._entryMap = new Map();
template.type.register = (type, callback) => {
	if (typeof type !== 'string') {
		console.error(`'type' must be a string, recieved: `, type);
	} else {
		const TYPE_REGEX = /^(\w+)\s*(<\s*\?(?:\s*,\s*\?)*\s*>)?\s*$/;
		const result = type.match(TYPE_REGEX);
		if (result === null) {
			console.error(`'${type}' is not a valid type id`);
		} else {
			if (result[2] === undefined) {
				result[2] = 0;
			} else {
				result[2] = result[2].split(/\s*,\s*/).length;
			}
			const completeType = result[1] + ':' + result[2];
			if (template.type._entryMap.get(completeType) === undefined) {
				template.type._entryMap.set(completeType, async function() {
					await callback.apply(null, Array.from(arguments));
				});
			} else {
				console.error(`${type} is already a registered template!`);
			}
		}
	}
};

// Courtesy of https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Regular_Expressions#escaping
function escapeRegExp(string) {
	return string.replace(/[.*+?^${}()|\[\]\\]/g, '\\$&'); // $& means the whole matched string
}

/* Intrinsic Templates */

// Basic - Simple search and replace
template.type.register('Basic', (element, map) => {
	let html = element.innerHTML;
	function applyObject(object, path) {
		for (const key in object) {
			const regexKey = escapeRegExp(path + key);
			html = html.replace(new RegExp(`(?:(?<!\\\\)\\\${${regexKey}})`, 'gm'), object[key]);
			if (typeof object[key] === 'object') {
				applyObject(object[key], path + key + '.');
			}
		}
	}
	applyObject(map, '');
	html = html.replace('\\&', '&');
	element.outerHTML = html.trim();
});

// Extern - Retrieve template from webserver
template.type.register('Extern', (element, name) => {
	return fetch(`templates/${name}.html.template`, {
			method: 'GET',
			mode: 'no-cors',
			headers: {
				'Content-Type': 'text/plain'
			}
		}).then(response => response.text().then((data) => {
			element.outerHTML = data;
		})).catch(error => {
			console.error(`failed to retrieve template '${name}': `, error);
		});
});

// List - Iterate over list and emit copy of child for each iteration
template.type.register('List<?>', async (element, subtype, arrayMap) => {
	let cumulative = '';
	const temp = document.createElement('template');
	for (const obj of arrayMap) {
		temp.innerHTML = `<template></template>`;
		const child = temp.content.children[0];
		child.innerHTML = element.innerHTML;
		const callback = template.type._entryMap.get(subtype.type);
		if (callback === undefined) {
			cumulative = '';
			console.error(`'${subtype.type}' is not a registered template type`);
		} else {
			await callback.apply(null, [ child, obj ]);
		}
		cumulative = cumulative + temp.innerHTML.trim();
	}
	element.outerHTML = cumulative;
});

template._entryMap = new Map();
template.apply = function(pattern, promise) {
	if (typeof pattern !== 'string') {
		console.error('pattern must be a string, received: ', pattern);
	} else {
		return new template.apply.applicators(pattern);
	}
};
template.apply.applicators = class {
	constructor(pattern) {
		this._pattern = pattern;
	}

	_apply(asyncArgs) {
		template._entryMap.set(RegExp('^' + this._pattern + '$'), asyncArgs);
	}

	values(...args) {
		this._apply(async () => Array.from(args));
	}

	promise(promise) {
		let args = null;
		promise = promise.then(data => args = [ data ]);
		this._apply(async () => args || promise);
	}

	fetch(dataProcessor, url, options) {
		if (typeof dataProcessor === 'string') {
			const path = dataProcessor;
			dataProcessor = data => {
				for (const id of path.split(/\./)) {
					data = data[id];
					if (data === undefined) {
						throw `invalid path '${path}'`;
					}
				}
				
				return data;
			};
		};
		this.promise(
			fetch(url, options || { method: 'GET', mode: 'cors' })
				.then(response => response.json())
				.then(data => dataProcessor(data))
		);
	}
};

(() => {
	const parseType = (type) => {
		let result = type.match(/^\s*(\w+)\s*(?:<(.*)>)?\s*$/);
		let id = result[1];
		result = result[2] ? result[2].split(/\s*,\s*/).map(parseType) : [];
		return { type: id + ':' + result.length, params: result };
	};

	const findTemplates = (element) =>
		Array
			.from(element.querySelectorAll('template'))
			.filter(child => Boolean(child.dataset.template))
			.map(child => {
				const data = child.dataset.template.split(/\s*:\s*/);
				return {
					id: data[0],
					typeCapture: parseType(data[1] || 'Begin'),
					element: child
				};
			});

	const expand = async (element) => {
		let children = findTemplates(element);
		let promises = [];
		let parents  = new Set();
		for (const child of children) {
			for (const [pattern, argsCallback] of template._entryMap) {
				await argsCallback().then(args => {
					if (pattern.test(child.id)) {
						const callback = template.type._entryMap.get(child.typeCapture.type);
						if (typeof callback !== 'function') {
							console.error(`'${child.typeCapture.type}' is not a registered template type`);
						} else {
							let params = Array.from(args)
							for (const subtype of child.typeCapture.params) {
								params.unshift(subtype);
							}
							params.unshift(child.element);
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
	};

	template.expand = async () => expand(document.children[0]);
})();