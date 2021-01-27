const express = require('express');
const morgan  = require('morgan' );
const fs      = require('fs'     );
const http    = require('http'   );

const config  = require('./config.js').load(process.argv[2]);

const app = express();
app.use("/", morgan("dev"));
app.use("/", (request, response, next) => {
	/*if (request.headers['accept'].split(',')[0] === 'text/html') {
		let path = request.url;
		if (path === '/') {
			path = "/index.html";
		}
		path = 'webpage' + path;
		fs.readFile(path, 'utf8', (err, fileData) => {
			if (err) {
				response.statusCode    = 500;
				response.statusMessage = 'Unkown file read error';
				console.log(err);
			}
			
			if (path === 'webpage/index.html') {
				console.log("ummmm");
				dbRequest = http.get("http://localhost:4730/achievements", dbResponse => {
					console.log("Yesting?");
					dbResponse.on('data', achievementData => {
						console.log("Hello?");
						achievementData = JSON.parse(achievementData);
						let list = "";
						for (const achievement of achievementData.achievements) {
							list = list + 
								`<div class="list-page-list-entry">
									<img class="achievement-list-page-entry-icon" src="res/dummy_achievement.png" alt="Achievement Icon.png" />
									<p class="achievement-list-page-entry-name">${achievement.name}</p>
									<p class="achievement-list-page-entry-description">${achievement.description}</p>
									<p class="achievement-list-page-entry-game">${achievement.gameID}</p>
								</div>`;
						}

						response.statusCode = 200;
						response.statusMessage = 'OK';
						response.write(Buffer.from(fileDate.replace("\${list}", list), 'utf8'));
						response.end();
					});
				});
			}

		});
	} else {
		next();
	}*/
	next();
});
app.use("/", express.static("webpage"));

app.listen(config.port);