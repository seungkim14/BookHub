package seung.bookhub;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ViewProfileActivity extends AppCompatActivity {
    private TextView usernameView;
    private TextView emailView;
    private ImageView profilePictureView;
    private RatingBar sellerRatingBar;
    private RatingBar buyerRatingBar;
    private ListView wishListView;
    private ListView inventoryView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        usernameView = (TextView)
            findViewById(R.id.textView_viewProfile_username);
        emailView = (TextView)
            findViewById(R.id.textView_viewProfile_email);
        profilePictureView = (ImageView)
            findViewById(R.id.imageView_viewProfile_profilePicture);
        sellerRatingBar = (RatingBar)
            findViewById(R.id.ratingBar_viewProfile_sellerRating);
        buyerRatingBar = (RatingBar)
            findViewById(R.id.ratingBar_viewProfile_buyerRating);
        wishListView = (ListView)
            findViewById(R.id.listView_viewProfile_wishList);
        inventoryView = (ListView)
            findViewById(R.id.listView_viewProfile_inventory);

        Intent intent = getIntent();
        String seller = intent.getStringExtra("seller");
        usernameView.setText(seller);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = NetworkConfiguration.getURL() + "profile/" + seller;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
          url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String profilePicture =
                        response.getString("profilePicture");
                    String email =
                        response.getString("email");
                    JSONArray wishListArray =
                        response.getJSONArray("wishList");
                    JSONArray inventoryArray =
                        response.getJSONArray("inventory");
                    double sellerRating = response.getDouble("sellerRating");
                    double buyerRating = response.getDouble("buyerRating");

                    ArrayList<String> wishList = new ArrayList<>();
                    for (int i = 0; i < wishListArray.length(); ++i) {
                        wishList.add(wishListArray.getString(i));
                    }
                    ArrayAdapter<String> wishListAdapter = new ArrayAdapter<>(
                        ViewProfileActivity.this,
                        android.R.layout.simple_list_item_1,
                        wishList
                    );
                    wishListView.setAdapter(wishListAdapter);

                    ArrayList<String> inventory = new ArrayList<>();
                    for (int i = 0; i < inventoryArray.length(); ++i) {
                        inventory.add(inventoryArray.getString(i));
                    }
                    ArrayAdapter<String> inventoryAdapter = new ArrayAdapter<>(
                        ViewProfileActivity.this,
                        android.R.layout.simple_list_item_1,
                        inventory
                    );
                    inventoryView.setAdapter(inventoryAdapter);

                    profilePictureView.setImageBitmap(
                        Utilities.stringToBitmap(profilePicture)
                    );
                    emailView.setText(email);
                    sellerRatingBar.setRating((float) sellerRating);
                    buyerRatingBar.setRating((float) buyerRating);
                }
                catch (JSONException e) {
                    Log.e("Error", e.getMessage());
                }
            }
        }, Utilities.getErrorListener(this));
        queue.add(request);
    }
}
