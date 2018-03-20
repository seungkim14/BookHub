package seung.bookhub;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class BookSearchResultActivity extends AppCompatActivity {
    private Book book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_search_result);

        Intent intent = getIntent();
        book = (Book) intent.getParcelableExtra("book");
        String thumbnail = intent.getStringExtra("thumbnail");
        int itemsCount = book.getItems().size();

        ((TextView) findViewById(R.id.text_bookSearchResult_author))
            .setText(book.getAuthor());
        ((TextView) findViewById(R.id.text_bookSearchResult_title))
            .setText(book.getTitle());
        ((TextView) findViewById(R.id.text_bookSearchResult_publisher))
            .setText(book.getPublisher());
        ((TextView) findViewById(R.id.button_bookSearchResult_viewBookItems))
            .setText("View " + itemsCount
                   + " Item" + (itemsCount > 1 ? "s" : ""));
        ((ImageView) findViewById(R.id.image_bookSearchResult_cover))
            .setImageBitmap(Utilities.stringToBitmap(thumbnail));
    }

    public void onAddToWishlist(View view) {
        makeRequest("wishList");
    }

    public void onSellACopy(View view) {
        makeRequest("myBooks");
    }

    private void makeRequest(String path) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = NetworkConfiguration.getURL() + path;

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.putOpt("isbn10", book.getISBN10());
        }
        catch (JSONException e) {
            Log.e("Error", e.getMessage());
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
          url, requestBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String message = response.getString("message");
                    Toast.makeText(getBaseContext(), message,
                        Toast.LENGTH_LONG).show();
                    finish();
                }
                catch (JSONException e) {
                    Log.e("Error", e.getMessage());
                }
            }
        }, Utilities.getErrorListener(this));

        queue.add(request);
    }

    public void onViewBookItems(View view) {
        Intent intent = new Intent(this, BookItemListActivity.class);
        intent.putParcelableArrayListExtra("bookItems", book.getItems());
        intent.putExtra("isbn10", book.getISBN10());
        startActivity(intent);
    }
}
