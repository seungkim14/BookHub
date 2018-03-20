var Book = require('../models/book');
var Transaction = require('../models/transaction');
const request = require('request-promise-native');

module.exports = {};

async function getBook(isbn) {
    let book = await Book.findOne({$or: [{isbn10: isbn}, {isbn13: isbn}]});

    // The book has been cached
    if (book) {
        return book;
    }

    // Look up the book
    const url = 'https://openlibrary.org/api/books?bibkeys=ISBN:'
              + isbn + '&format=json&jscmd=data';
    try {
        const response = await request(url);
        book = JSON.parse(response)['ISBN:' + isbn];
        if (!book) {
            throw {};
        }
    }
    catch (err) {
        throw new Error('Failed to look up book');
    }

    // Create a new Book instance
    const newBook = new Book();
    newBook.title = book.title ? book.title : '';
    newBook.publisher = book.publisher ? book.publisher : '';
    try {
        newBook.author = book.authors[0].name;
    }
    catch (err) {
        newBook.author = '';
    }
    try {
        newBook.isbn10 = book.identifiers.isbn_10[0];
    }
    catch (err) {
        newBook.isbn10 = isbn;
    }
    try {
        newBook.isbn13 = book.identifiers.isbn_13[0];
    }
    catch (err) {
        newBook.isbn13 = '';
    }

    // If the image URL is available, try to download it
    try {
        const imageURL = book.cover.large;
        const response = await request.defaults({encoding: null})(imageURL);
        const buffer = new Buffer(response);
        newBook.thumbnail = buffer.toString('base64');
    }
    catch (err) {
        newBook.thumbnail = '';
    }

    await newBook.save();
    console.log('New book with ISBN', isbn, 'cached');

    return newBook;
}

async function getBookCheckTransaction(isbn, username) {
    const book = await getBook(isbn);
    book.items = await Promise.all(book.items.map(async item => {
        var transaction = await Transaction.findOne(
            {buyer: username, book: book._id}
        );
        item.isTransactionRequested = transaction ? true : false;
        return item;
    }));
    return book;
};

module.exports.getBookCheckTransaction = getBookCheckTransaction;

module.exports.isbnsToTitles = async isbns => {
    return Promise.all(isbns.map(async isbn => {
        const book = await
            Book.findOne({$or: [{isbn10: isbn}, {isbn13: isbn}]});
        return book.title;
    }));
};

module.exports.searchBook = async (req, res) => {
    var user = req.user;
    try {
        var book = await getBookCheckTransaction(
            req.params.isbn, user.username
        );
        res.writeHead(200, {'Content-Type': 'application/json'});
        res.end(JSON.stringify(book));
    }
    catch (err) {
        res.status(400).end(err.toString());
    }
};

module.exports.searchBooks = async (req, res) => {
    const maxSearchResults = 10;
    try {
        const url = 'https://openlibrary.org/search.json?q=' + req.params.term;
        const response = await request(url);
        const docs = JSON.parse(response).docs;

        const books = docs.slice(0, maxSearchResults).map(doc => {
            try {
                return {
                    'title': doc.title_suggest,
                    'isbn': doc.isbn[0],
                    'author': doc.author_name[0],
                    'publisher': doc.publisher[0],
                    'firstPublishYear': doc.first_publish_year,
                }
            }
            catch (err) {
                return undefined;
            }
        })
        .filter(doc => doc !== undefined);

        console.log('Books');
        console.log(books);
        res.writeHead(200, {'Content-Type': 'application/json'});
        res.end(JSON.stringify({books: books}));
    }
    catch (err) {
        res.status(400).end(err.toString());
    }
};
