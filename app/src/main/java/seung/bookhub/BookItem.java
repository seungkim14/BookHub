package seung.bookhub;

import android.os.Parcel;
import android.os.Parcelable;

class BookItem implements Parcelable {
    private String seller;
    private String condition;
    private double price;
    private boolean canRequestTransaction;

    BookItem(String seller, String condition, double price,
      boolean canRequestTransaction) {
        this.seller = seller;
        this.condition = condition;
        this.price = price;
        this.canRequestTransaction = canRequestTransaction;
    }

    private BookItem(Parcel in) {
        seller = in.readString();
        condition = in.readString();
        price = in.readDouble();
        canRequestTransaction = in.readByte() != 0;
    }

    public static final Parcelable.Creator<BookItem> CREATOR = new
      Parcelable.Creator<BookItem>() {
        @Override
        public BookItem createFromParcel(Parcel parcel) {
            return new BookItem(parcel);
        }

        @Override
        public BookItem[] newArray(int size) {
            return new BookItem[0];
        }
    };

    String getSeller() {
        return seller;
    }

    String getCondition() {
        return condition;
    }

    double getPrice() {
        return price;
    }

    boolean getCanRequestTransaction() {
        return canRequestTransaction;
    }

    void setCanRequestTransaction(boolean canRequestTransaction) {
        this.canRequestTransaction = canRequestTransaction;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(seller);
        parcel.writeString(condition);
        parcel.writeDouble(price);
        parcel.writeByte((byte) (canRequestTransaction ? 1 : 0));
    }
}
