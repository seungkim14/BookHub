const mongoose = require('mongoose');

const chatSchema = mongoose.Schema({
    buyer: String,
    seller: String,
    transaction: {type: mongoose.Schema.Types.ObjectId, ref: 'Transaction'},
    history: [{sender: String, text: String}],
});

module.exports = mongoose.model('Chat', chatSchema);
