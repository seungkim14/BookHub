package seung.bookhub;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// Super class for WishListActivity and MyBooksActivity
public abstract class BookListActivity extends AppCompatActivity {
    protected ArrayList<Book> items;
    private ArrayList<Integer> indexesToRemove;
    private ArrayList<CheckBox> checkBoxes;
    private Color normalButtonColor;
    private boolean isEditing;
    private Button editButton;

    abstract protected String getListName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        items = new ArrayList<>();
        indexesToRemove = new ArrayList<>();
        checkBoxes = new ArrayList<>();
        isEditing = false;
        editButton = (Button) findViewById(R.id.button_bookList_edit);
        ListView listView = (ListView) findViewById(R.id.listView_bookList);

        final BooksListAdapter adapter = new BooksListAdapter(this, items);
        listView.setAdapter(adapter);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = NetworkConfiguration.getURL() + getListName();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
          url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray array = response.getJSONArray("books");
                    for (int i = 0; i < array.length(); ++i) {
                        JSONObject object = array.getJSONObject(i);
                        Book book = Utilities.jsonObjectToBook(object);
                        items.add(book);
                        adapter.notifyDataSetChanged();
                    }
                }
                catch (JSONException e) {
                    Log.e("Error", e.getMessage());
                }
            }
        }, Utilities.getErrorListener(this));
        queue.add(request);
    }

    public void onEdit(View view) {
        isEditing = !isEditing;

        // Start editing
        if (isEditing) {
            editButton.setText("Cancel");
            for (CheckBox checkBox : checkBoxes) {
                checkBox.setVisibility(View.VISIBLE);
            }
        }
        // No changes to list
        else if (indexesToRemove.isEmpty()) {
            cancelEditing();
        }
        // End editing
        else {
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = NetworkConfiguration.getURL()
                       + getListName() + "/remove";

            JSONArray isbnsToRemove = new JSONArray();
            for (Integer indexToRemove : indexesToRemove) {
                isbnsToRemove.put(items.get(indexToRemove).getISBN10());
            }
            JSONObject requestBody = new JSONObject();
            try {
                requestBody.putOpt("isbnsToRemove", isbnsToRemove);
            }
            catch (JSONException e) {
                Log.e("Error", e.getMessage());
            }

            JsonObjectRequest request = new JsonObjectRequest(
              Request.Method.POST, url, requestBody,
              new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String message = response.getString("message");
                        Toast.makeText(getBaseContext(), message,
                            Toast.LENGTH_LONG).show();
                        recreate();
                    }
                    catch (JSONException e) {
                        Log.e("Error", e.getMessage());
                    }
                }
            }, Utilities.getErrorListener(this));
            queue.add(request);
        }
    }

    private void cancelEditing() {
        indexesToRemove.clear();
        editButton.setText("Edit");
        for (CheckBox checkBox : checkBoxes) {
            checkBox.setVisibility(View.INVISIBLE);
        }
    }

    private class BooksListAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<Book> data;
        private LayoutInflater inflater = null;

        BooksListAdapter(Context context, ArrayList<Book> data)
        {
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
                view = inflater.inflate(R.layout.book_list_item, parent, false);
            }
            TextView titleTextView = (TextView)
                view.findViewById(R.id.textView_bookListItem_title);
            TextView authorTextView = (TextView)
                view.findViewById(R.id.textView_bookListItem_author);
            Book book = data.get(position);
            titleTextView.setText(book.getTitle());
            authorTextView.setText(book.getAuthor());

            CheckBox checkBox = (CheckBox)
                view.findViewById(R.id.checkBox_bookListItem);
            checkBoxes.add(checkBox);

            checkBox.setTag(position);
            checkBox.setOnCheckedChangeListener(
              new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton,
                  boolean isChecked) {
                    Integer tag = (Integer) compoundButton.getTag();
                    if (isChecked) {
                        indexesToRemove.add(tag);
                    }
                    else {
                        indexesToRemove.remove(tag);
                    }
                    setEditButtonText();
                }
            });

            return view;
        }
    }

    private void setEditButtonText() {
        if (indexesToRemove.isEmpty()) {
            editButton.setText("Cancel");
            editButton.setBackgroundResource(R.drawable.button_bg_blue);
            editButton.setTextAppearance(R.style.button_style);
        }
        else {
            editButton.setText("Delete selected");
            editButton.setBackgroundResource(R.drawable.button_bg_red);
            editButton.setTextAppearance(R.style.button_style_red);
        }
    }
}
