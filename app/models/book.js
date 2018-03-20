var mongoose = require('mongoose');

var bookSchema = mongoose.Schema({
    isbn10: String,
    isbn13: String,
    author: String,
    title: String,
    publisher: String,
    thumbnail: String,

    // List of {seller: String, condition: String, price: Number} objects
    items: Array,
});

module.exports = mongoose.model('Book', bookSchema);
