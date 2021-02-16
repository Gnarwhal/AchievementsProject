const fs       = require('fs'      );
const path     = require('path'    );

const https    = require('https'   );
const express  = require('express' );
const morgan   = require('morgan'  );
const passport = require('passport');
const SteamStrategy = require('passport-steam').Strategy;

const promptly = require('promptly');

const config = require('./config.js').load(process.argv[2]);

if (config.build === 'debug') {
	process.env["NODE_TLS_REJECT_UNAUTHORIZED"] = 0;
}

console.log(`Running server at '${config.hosts.frontend}'`);

passport.use(new SteamStrategy({
	returnURL: `${config.hosts.frontend}/profile/steam`,
	realm:     `${config.hosts.frontend}`,
	profile: false,
}));

const app = express();
app.use("/", morgan("dev"));
app.use("/static", express.static("webpage/static"));
app.get("/login", (req, res) => {
	res.sendFile(path.join(__dirname + "/webpage/login.html"));
});
app.get("/", (req, res) => {
	res.sendFile(path.join(__dirname + "/webpage/index.html"));
});
app.get("/about", (req, res) => {
	res.sendFile(path.join(__dirname + "/webpage/about.html"));
});
app.get("/profile/:id", (req, res) => {
	res.sendFile(path.join(__dirname + "/webpage/profile.html"));
});
app.get("/auth/steam", passport.authenticate('steam'), (req, res) => {});

// --- API Forward --- //

app.use("/api/*", (req, res) => {
	res.redirect(307, `${config.hosts.backend}/${req.params[0]}`)
});

// ------------------- //

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