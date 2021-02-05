const express  = require('express' );
const morgan   = require('morgan'  );
const promptly = require('promptly');

const config  = require('./config.js').load(process.argv[2]);

if (config.build === 'debug') {
	process.env["NODE_TLS_REJECT_UNAUTHORIZED"] = 0;
}

const app = express();
app.use("/", morgan("dev"));
app.use("/", express.static("webpage"));

const server = app.listen(config.port);

const prompt = input => {
	if (/q(?:uit)?|exit/i.test(input)) {
		server.close();
	} else {
		promptly.prompt('')
			.then(prompt);
	}
};

prompt();