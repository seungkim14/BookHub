package seung.bookhub;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {
    private static final int RESULT_LOAD_IMAGE = 1;
    private boolean isChangingPassword = false;
    private boolean isProfilePictureChanged = false;
    private ImageView profilePicture;
    private EditText oldPasswordView;
    private EditText newPasswordView;
    private EditText newPasswordConfirmView;
    private Button passwordButton;
    private LinearLayout passwordLayout;
    private TextView passwordWarningView;
    private Button saveButton;
    private User currentUser;
    private String newPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView usernameView = (TextView)
            findViewById(R.id.textView_profile_username);
        TextView emailView = (TextView)
            findViewById(R.id.textView_profile_email);
        profilePicture = (ImageView)
            findViewById(R.id.imageView_profile_profilePicture);
        oldPasswordView = (EditText)
            findViewById(R.id.editText_profile_oldPassword);
        newPasswordView = (EditText)
            findViewById(R.id.editText_profile_newPassword);
        newPasswordConfirmView = (EditText)
            findViewById(R.id.editText_profile_newPasswordConfirm);
        passwordButton = (Button)
            findViewById(R.id.button_profile_password);
        passwordLayout = (LinearLayout)
            findViewById(R.id.linearLayout_profile_password);
        passwordWarningView = (TextView)
            findViewById(R.id.textView_profile_passwordWarning);
        saveButton = (Button)
            findViewById(R.id.button_profile_save);

        currentUser = new UserManager().getCurrentUser();
        usernameView.setText(currentUser.getUsername());
        emailView.setText(currentUser.getEmail());

        if (!currentUser.getPicture().isEmpty()) {
            profilePicture.setImageBitmap(
                Utilities.stringToBitmap(currentUser.getPicture())
            );
        }

        newPasswordConfirmView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence,
              int i, int i1, int i2) {
                newPassword = newPasswordView.getText().toString();
            }

            @Override
            public void onTextChanged(CharSequence newPasswordConfirm,
              int i, int i1, int i2) {
                if (newPasswordConfirm.toString().equals(newPassword)) {
                    passwordWarningView.setVisibility(View.INVISIBLE);
                    saveButton.setClickable(true);
                }
                else {
                    passwordWarningView.setVisibility(View.VISIBLE);
                    saveButton.setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });
    }

    public void onChangePassword(View view) {
        isChangingPassword = !isChangingPassword;
        if (isChangingPassword) {
            passwordButton.setText("Cancel Changing Password");
            oldPasswordView.setText("");
            newPasswordView.setText("");
            newPasswordConfirmView.setText("");
            passwordLayout.setVisibility(View.VISIBLE);
        }
        else {
            passwordButton.setText("Change Password");
            passwordLayout.setVisibility(View.INVISIBLE);
            saveButton.setClickable(true);
        }
    }

    public void onChangePicture(View view) {
        Intent intent = new Intent(
            Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        );
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
      Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(
                    this.getContentResolver(), selectedImage
                );
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            String picture = Utilities.bitmapToString(bitmap);
            currentUser.setPicture(picture);
            profilePicture.setImageBitmap(bitmap);
            isProfilePictureChanged = true;
        }
    }

    public void onSave(View view) {
        JSONObject requestBody = new JSONObject();
        try {
            if (isChangingPassword) {
                requestBody.putOpt(
                    "oldPassword", oldPasswordView.getText().toString()
                );
                requestBody.putOpt(
                    "newPassword", newPasswordView.getText().toString()
                );
            }
            if (isProfilePictureChanged) {
                requestBody.putOpt("profilePicture", currentUser.getPicture());
            }
        }
        catch (JSONException e) {
            Log.e("Error", e.getMessage());
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = NetworkConfiguration.getURL() + "user";

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
                    e.printStackTrace();
                }
            }
        }, Utilities.getErrorListener(this));
        queue.add(request);
    }
}
