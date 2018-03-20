package seung.bookhub;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class RegistrationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
    }

    public void onSubmit(View view) {
        String username =
            ((EditText) findViewById(R.id.text_registration_username)).getText().toString();
        String email =
            ((EditText) findViewById(R.id.text_registration_email)).getText().toString();
        String password =
            ((EditText) findViewById(R.id.text_registration_password)).getText().toString();

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.putOpt("email", email);
            requestBody.putOpt("username", username);
            requestBody.putOpt("password", password);
            requestBody.putOpt("picture", "");
        } catch (JSONException e) {
            Log.e("Error", e.getMessage());
        }
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = NetworkConfiguration.getURL() + "user";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.PUT, url, requestBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject resp) {
                String username;
                try {
                    username = resp.getString("username");

                    Toast.makeText(getBaseContext(), "Account " + username + " created",
                            Toast.LENGTH_LONG).show();
                    finish();
                } catch (JSONException e) {
                    Log.e("Error", e.getMessage());
                }
            }
        }, Utilities.getErrorListener(this));
        queue.add(jsObjRequest);
    }
}
