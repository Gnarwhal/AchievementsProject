const fs = require("fs");

const loadConfig = module.exports.load = (configPath) => {
	function writeToObject(write, read) {
		for (const key in read) {
			if (typeof read[key] === 'object') {
				if (write[key] != null) {
					writeToObject(write[key], read[key]);
				} else {
					write[key] = read[key];
				}
			} else {
				write[key] = read[key];
			}
		}
	}

	const baseConfig = JSON.parse(fs.readFileSync(configPath));

	const config = {};
	if (baseConfig.extends !== undefined) {
		for (const path of baseConfig.extends) {
			const parent = loadConfig(path);
			writeToObject(config, parent);
		}
	}
	writeToObject(config, baseConfig);
	delete config.extends;
	return config;
};