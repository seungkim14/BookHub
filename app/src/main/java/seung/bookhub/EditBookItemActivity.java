package seung.bookhub;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class EditBookItemActivity extends AppCompatActivity {
    private TextView bookTitleView;
    private EditText priceView;
    private EditText conditionView;
    private BookItem bookItem;
    private String bookTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book_item);

        Intent intent = getIntent();
        bookItem = intent.getParcelableExtra("bookItem");
        bookTitle = intent.getStringExtra("bookTitle");

        bookTitleView = (TextView)
            findViewById(R.id.textView_editBookItem_bookTitle);
        priceView = (EditText)
            findViewById(R.id.editText_editBookItem_price);
        conditionView = (EditText)
            findViewById(R.id.editText_editBookItem_condition);

        bookTitleView.setText(bookTitle);
        priceView.setText("" + bookItem.getPrice());
        conditionView.setText(bookItem.getCondition());
    }

    public void onSubmit(View view) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = NetworkConfiguration.getURL() + "myBooks/edit";

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.putOpt("title", bookTitle);
            requestBody.putOpt("price", priceView.getText().toString());
            requestBody.putOpt("condition", conditionView.getText().toString());
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
                    Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG)
                         .show();
                }
                catch (JSONException e) {
                    Log.e("Error", e.getMessage());
                }
            }
        }, Utilities.getErrorListener(this));
        queue.add(request);
    }
}
