var mongoose = require('mongoose');
var Book = require('../models/book');

var transactionSchema = mongoose.Schema({
    seller: String,
    buyer: String,
    book: {type: mongoose.Schema.Types.ObjectId, ref: 'Book'},
    sellerRating: Number,
    buyerRating: Number,
    approvedBySeller: Boolean,
    approvedByBuyer: Boolean,
    dateRequested: Date,
    dateApprovedBySeller: Date,
    dateApprovedByBuyer: Date,
});

const dateOptions = {
    year: 'numeric',
    month: 'numeric',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
};

transactionSchema.methods.getNames = function(username) {
    if (username === this.seller) {
        return {
            seller:  'you',
            sellers: 'your',
            buyer:   this.buyer,
            buyers:  this.buyer + '\'s',
        };
    }
    return {
        seller:  this.seller,
        sellers: this.seller + '\'s',
        buyer:   'you',
        buyers:  'your',
    };
}

transactionSchema.methods.getRequestMessage =
  function(username, showDate = false) {
    let requestMessage = 'Requested by ';
    if (username === this.buyer) {
        requestMessage += 'you';
    }
    else {
        requestMessage += this.buyer;
    }
    if (showDate) {
        requestMessage += ' on ' + this.dateRequested.toLocaleDateString(
            'en-US', dateOptions);
    }
    return requestMessage;
}

transactionSchema.methods.getApprovalMessage =
  function(username, showDate = false) {
    const names = this.getNames(username);

    let sellerApproval = '';
    if (this.approvedBySeller) {
        sellerApproval += 'Approved by ' + names.seller;
        if (showDate) {
            sellerApproval += ' on ' +
                this.dateApprovedBySeller.toLocaleDateString(
                    'en-US', dateOptions);
        }
    }
    else {
        sellerApproval += 'Pending ' + names.sellers + ' approval';
    }

    let buyerApproval = '';
    if (this.approvedByBuyer) {
        buyerApproval += 'Approved by ' + names.buyer;
        if (showDate) {
            buyerApproval += ' on ' +
                this.dateApprovedByBuyer.toLocaleDateString(
                    'en-US', dateOptions);
        }
    }
    else {
        buyerApproval += 'Pending ' + names.buyers + ' approval';
    }

    if (username === this.seller) {
        return sellerApproval + '\n' + buyerApproval;
    }
    return buyerApproval + '\n' + sellerApproval;
};

transactionSchema.methods.isSeller = function(username) {
    return username === this.seller;
};

transactionSchema.methods.getTradedWith = function(username) {
    return username === this.seller ? this.buyer : this.seller;
};

transactionSchema.methods.getTradedWithRating = function(username) {
    return username === this.seller ? this.buyerRating : this.sellerRating;
};

transactionSchema.methods.canBeRated = function() {
    return this.approvedBySeller && this.approvedByBuyer;
}

transactionSchema.methods.canBeApproved = function(username) {
    return username === this.seller && !this.approvedBySeller
        || username !== this.seller && !this.approvedByBuyer;
};

module.exports = mongoose.model('Transaction', transactionSchema);
