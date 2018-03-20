package seung.bookhub;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Book implements Parcelable {
    private String title;
    private String author;
    private String publisher;
    private String ISBN10;
    private String ISBN13;
    private ArrayList<BookItem> items;

    public Book()
    {
        title = "";
        author = "";
        publisher = "";
        ISBN10 = "";
        ISBN13 = "";
    }

    public Book(String title, String author, String publisher)
    {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
    }

    private Book(Parcel in) {
        title = in.readString();
        author = in.readString();
        publisher = in.readString();
        ISBN10 = in.readString();
        ISBN13 = in.readString();
        items = in.readArrayList(BookItem.class.getClassLoader());
    }

    public static final Parcelable.Creator<Book> CREATOR = new
      Parcelable.Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel parcel) {
            return new Book(parcel);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[0];
        }
    };

    //-----------------------------------------------------
    // Getters and Setters
    //-----------------------------------------------------

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getISBN10() {
        return ISBN10;
    }

    public void setISBN10(String ISBN10) {
        this.ISBN10 = ISBN10;
    }

    public String getISBN13() {
        return ISBN13;
    }

    public void setISBN13(String ISBN13) {
        this.ISBN13 = ISBN13;
    }

    public ArrayList<BookItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<BookItem> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "title: " + title + "\n" +
                "author: " + author + "\n" +
                "publisher: " + publisher + "\n" +
                "ISBN10: " + ISBN10 + "\n" +
                "ISBN13: " + ISBN13;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(author);
        parcel.writeString(publisher);
        parcel.writeString(ISBN10);
        parcel.writeString(ISBN13);
        parcel.writeList(items);
    }
}
