package seung.bookhub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MyBooksActivity extends BookListActivity {
    @Override
    protected String getListName() {
        return "myBooks";
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
                    MyBooksActivity.this, EditBookItemActivity.class
                );
                Book book = items.get(i);
                String username = new
                    UserManager().getCurrentUser().getUsername();
                for (BookItem bookItem : book.getItems()) {
                    if (bookItem.getSeller().equals(username)) {
                        intent.putExtra("bookItem", bookItem);
                        break;
                    }
                }
                intent.putExtra("bookTitle", book.getTitle());
                startActivityForResult(intent, i);
            }
        });
    }
}
