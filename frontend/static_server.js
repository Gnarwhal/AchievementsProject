const express = require('express');
const morgan  = require('morgan' );
const fs      = require('fs'     );
const https   = require('https'  );

const config  = require('./config.js').load(process.argv[2]);

if (config.build === 'debug') {
	process.env["NODE_TLS_REJECT_UNAUTHORIZED"] = 0;
}

const app = express();
app.use("/", morgan("dev"));
app.use("/", express.static("webpage"));

app.listen(config.port);