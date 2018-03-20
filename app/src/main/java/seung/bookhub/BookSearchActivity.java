package seung.bookhub;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class BookSearchActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_search);
    }

    public void onSearch(View view) {
        final String term =
            ((EditText) findViewById(R.id.textfield_bookSearch_term))
            .getText().toString();

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = NetworkConfiguration.getURL() + "searchBooks/" + term;
        JsonObjectRequest request = new JsonObjectRequest(
          Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Intent intent = new Intent(
                    BookSearchActivity.this, BookSearchResultsActivity.class
                );
                intent.putExtra("books", response.toString());
                startActivity(intent);
                finish();
            }
        }, Utilities.getErrorListener(this));
        request.setRetryPolicy(new DefaultRetryPolicy(
            5000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        queue.add(request);
    }
}
