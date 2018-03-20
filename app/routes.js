var users  = require('./controllers/users');
var books = require('./controllers/books');
var ensureLoggedIn = require('connect-ensure-login').ensureLoggedIn();

module.exports = function(app, passport) {

    app.post('/login', passport.authenticate('local'), function(req, res) {
        res.redirect('/user/' + req.user._id + '/' + req.body.deviceToken);
    });

    app.get('/user/:id/:deviceToken', isLoggedIn, users.read);
    app.get('/wishList', ensureLoggedIn, users.getList);
    app.get('/myBooks', ensureLoggedIn, users.getList);
    app.get('/searchBooks/:term', books.searchBooks);
    app.get('/search/:isbn', books.searchBook);
    app.get('/transactionHistory', ensureLoggedIn, users.getTransactionHistory);
    app.get('/transaction/:id', ensureLoggedIn, users.getTransaction);
    app.get('/chats', ensureLoggedIn, users.getChatList);
    app.get('/chat/:transactionID', ensureLoggedIn, users.getChat);
    app.get('/profile/:username', users.getProfile);
    app.put('/user', users.create);
    app.post('/user', isLoggedIn, users.update);
    app.post('/wishList', ensureLoggedIn, users.addToWishList);
    app.post('/wishList/remove', ensureLoggedIn, users.removeFromWishList);
    app.post('/myBooks', ensureLoggedIn, users.addToMyBooks);
    app.post('/myBooks/remove', ensureLoggedIn, users.removeFromMyBooks);
    app.post('/myBooks/edit', ensureLoggedIn, users.editBookForSale);
    app.post('/requestTransaction', ensureLoggedIn, users.requestTransaction);
    app.post('/approveTransaction', ensureLoggedIn, users.approveTransaction);
    app.post('/rateUser', ensureLoggedIn, users.rateUser);
    app.post('/sendMessage', ensureLoggedIn, users.sendMessage);
    app.delete('/user/:id', isLoggedIn, users.delete);

    app.post('/logout', function(req, res) {
        req.logout();
        res.end('Logged out')
    });

};

function isLoggedIn(req, res, next) {
    if (req.isAuthenticated())
        return next();

    res.end('Not logged in');
}
