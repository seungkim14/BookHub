package seung.bookhub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class WishListActivity extends BookListActivity {
    @Override
    protected String getListName() {
        return "wishList";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListView listView = (ListView) findViewById(R.id.listView_bookList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
              int i, long l) {
                Intent intent = new Intent(
                    WishListActivity.this, BookItemListActivity.class
                );
                Book book = items.get(i);
                intent.putParcelableArrayListExtra(
                    "bookItems", book.getItems()
                );
                intent.putExtra("isbn10", book.getISBN10());
                startActivityForResult(intent, i);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
      Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            ArrayList<BookItem> bookItems =
                data.getParcelableArrayListExtra("bookItems");
            items.get(requestCode).setItems(bookItems);
        }
    }
}
