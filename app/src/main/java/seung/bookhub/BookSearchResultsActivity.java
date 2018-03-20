package seung.bookhub;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BookSearchResultsActivity extends AppCompatActivity {
    private class Book {
        String title;
        String isbn;
        String author;
        String publisher;
        int firstPublishYear;

        Book(JSONObject object) throws JSONException {
            this.title = object.getString("title");
            this.isbn = object.getString("isbn");
            this.author = object.getString("author");
            this.publisher = object.getString("publisher");
            this.firstPublishYear = object.getInt("firstPublishYear");
        }
    }

    private ArrayList<String> isbns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_search_results);

        Intent intent = getIntent();
        final ArrayList<JSONObject> bookObjects = new ArrayList<>();
        isbns = new ArrayList<>();
        try {
            JSONArray booksArray =
                new JSONObject(intent.getStringExtra("books"))
                    .getJSONArray("books");
            for (int i = 0; i < booksArray.length(); ++i) {
                JSONObject bookObject = booksArray.getJSONObject(i);
                bookObjects.add(bookObject);
                isbns.add(bookObject.getString("isbn"));
            }
        }
        catch (JSONException e) {
            Toast.makeText(getBaseContext(),
                "Cannot load results", Toast.LENGTH_LONG).show();
            Log.e("Error", e.getMessage());
            finish();
        }

        final BookSearchResultsAdapter adapter = new
            BookSearchResultsAdapter(this, bookObjects);

        ListView listView = (ListView)
            findViewById(R.id.listView_bookSearchResults);
        listView.setAdapter(adapter);
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
              int i, long l) {
                searchISBN(i);
            }
        });
    }

    private void searchISBN(int i) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = NetworkConfiguration.getURL() + "search/" + isbns.get(i);
        JsonObjectRequest request = new JsonObjectRequest(
          Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String thumbnail = response.getString("thumbnail");
                    Intent intent = new Intent(
                        BookSearchResultsActivity.this,
                        BookSearchResultActivity.class
                    );
                    intent.putExtra(
                        "book", Utilities.jsonObjectToBook(response)
                    );
                    intent.putExtra("thumbnail", thumbnail);
                    startActivity(intent);
                    finish();
                }
                catch (JSONException e) {
                    Log.e("Error", e.getMessage());
                }
            }
        }, Utilities.getErrorListener(this));
        request.setRetryPolicy(new DefaultRetryPolicy(
            5000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        queue.add(request);
    }

    private class BookSearchResultsAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<JSONObject> data;
        private LayoutInflater inflater;

        BookSearchResultsAdapter(Context context, ArrayList<JSONObject> data) {
            this.context = context;
            this.data = data;
            inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = inflater.inflate(
                    R.layout.book_search_results_list_item, parent, false
                );
            }
            TextView titleView = (TextView) view.findViewById(
                R.id.textView_bookSearchResultsListItem_title);
            TextView authorView = (TextView) view.findViewById(
                R.id.textView_bookSearchResultsListItem_author);
            TextView publishView = (TextView) view.findViewById(
                R.id.textView_bookSearchResultsListItem_publish);
            try {
                Book book = new Book(data.get(position));
                titleView.setText(book.title);
                authorView.setText(book.author);
                publishView.setText(
                    "First published by " + book.publisher + " in "
                        + book.firstPublishYear
                );
            }
            catch (JSONException e) {
                Log.e("Error", e.getMessage());
            }
            return view;
        }
    }
}
