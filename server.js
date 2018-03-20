var express  = require('express');
var app      = express();
var port     = process.env.PORT || 8080;
var mongoose = require('mongoose');
var passport = require('passport');
var flash    = require('connect-flash');

var morgan       = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser   = require('body-parser');
var session      = require('express-session');

var useLocalHost = true;

if (useLocalHost) {
    mongoose.connect('mongodb://localhost/bookhub', function(err) {
        if (err) throw err;
    });
}
else {
    mongoose.connect(process.env.MONGODB_URI, function(err) {
        if (err) throw err;
    });
}
mongoose.Promise = require('bluebird');

require('./config/passport')(passport);

app.use(morgan('dev'));
app.use(cookieParser());
app.use(bodyParser.json({limit: '5mb'}));
app.use(bodyParser.urlencoded({ extended: true }));

app.use(session({ secret: 'verysecretsessionsecret', resave: true, saveUninitialized: true }));
app.use(passport.initialize());
app.use(passport.session());
app.use(flash());

require('./app/routes.js')(app, passport);

app.listen(port);
console.log('BookHub running on port ' + port);
