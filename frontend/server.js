const fs       = require('fs'      );
const path     = require('path'    );

const https    = require('https'   );
const express  = require('express' );
const morgan   = require('morgan'  );
const passport = require('passport');
const SteamStrategy = require('passport-steam').Strategy;

const promptly = require('promptly');

const config = require('./config.js').load(process.argv[2]);

console.log(`Running server at '${config.hosts.frontend}'`);

passport.use(new SteamStrategy({
	returnURL: `${config.hosts.frontend}/user/steam`,
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
	res.sendFile(path.join(__dirname + "/webpage/search_achievements.html"));
});
app.get("/achievements", (req, res) => {
	res.sendFile(path.join(__dirname + "/webpage/search_achievements.html"));
});
app.get("/users", (req, res) => {
	res.sendFile(path.join(__dirname + "/webpage/search_users.html"));
});
app.get("/games", (req, res) => {
	res.sendFile(path.join(__dirname + "/webpage/search_games.html"));
});
app.get("/achievement/:id", (req, res) => {
	res.sendFile(path.join(__dirname + "/webpage/achievement.html"));
});
app.get("/user/:id", (req, res) => {
	res.sendFile(path.join(__dirname + "/webpage/user.html"));
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