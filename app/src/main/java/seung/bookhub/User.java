package seung.bookhub;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable
{
    private String username;
    private String email;
    private String password;
    private String picture;
    private List<Book> wishlist;
    private List<Book> myBooks;
    private int sellerRating;
    private int buyerRating;

    public User()
    {
        username = "";
        email = "";
        password = "";
        wishlist = new ArrayList<Book>();
        myBooks = new ArrayList<Book>();
        sellerRating = -1;
        buyerRating = -1;
    }

    public void addBooktoWishlist(Book book)
    {
        wishlist.add(book);
    }

    public void removeBookFromWishlist(Book book)
    {
        wishlist.remove(book);
    }

    public void addBooktoMyBooks(Book book)
    {
        myBooks.add(book);
    }

    public void removeBookFromMyBooks(Book book)
    {
        myBooks.remove(book);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getSellerRating() {
        return sellerRating;
    }

    public void setSellerRating(int sellerRating) {
        this.sellerRating = sellerRating;
    }

    public int getBuyerRating() {
        return buyerRating;
    }

    public void setBuyerRating(int buyerRating) {
        this.buyerRating = buyerRating;
    }

    public List<Book> getWishlist() {
        return wishlist;
    }

    public List<Book> getMyBooks() {
        return myBooks;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
