package seung.bookhub;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
    }

    public void onLogin(View view) {
        CookieHandler.setDefault(new CookieManager());

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.putOpt("username", ((EditText) findViewById(R.id.text_login_username)).getText().toString());
            requestBody.putOpt("password", ((EditText) findViewById(R.id.text_login_password)).getText().toString());
            requestBody.putOpt("deviceToken", FirebaseInstanceId.getInstance().getToken());
        } catch (JSONException e) {
            Log.e("Error", e.getMessage());
        }
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = NetworkConfiguration.getURL() + "login";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject resp) {
                String username;
                String email;
                String picture;
                try {
                    username = resp.getString("username");
                    email = resp.getString("email");
                    picture = resp.getString("profilePicture");
                    UserManager um = new UserManager();
                    User myUser = new User();
                    myUser.setUsername(username);
                    myUser.setEmail(email);
                    myUser.setPicture(picture);
                    um.setCurrentUser(myUser);
                    Intent myIntent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(myIntent);
                } catch (JSONException e) {
                    Log.e("Error", e.getMessage());
                }
            }
        }, Utilities.getErrorListener(this));
        queue.add(jsObjRequest);
    }

    public void onNewUser(View view) {
        Intent myIntent = new Intent(LoginActivity.this, RegistrationActivity.class);
        startActivity(myIntent);
    }
}
