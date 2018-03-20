package seung.bookhub;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

final class Utilities {
    static Bitmap stringToBitmap(String encodedString) {
        try {
            byte[] decodedBytes = Base64.decode(encodedString,Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(
                decodedBytes, 0, decodedBytes.length
            );
        }
        catch (Exception e) {
            Log.e("Utilities", e.getMessage());
        }
        return null;
    }

    static String bitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] byteArray = outputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    static Response.ErrorListener
      getErrorListener(final AppCompatActivity activity) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String message = new String(
                            response.data,
                            HttpHeaderParser.parseCharset(
                                response.headers, "utf-8"
                            )
                        );
                        Toast.makeText(activity.getBaseContext(), message,
                            Toast.LENGTH_LONG).show();
                    }
                    catch (Exception e) {
                        Log.e("Network Request", e.getMessage());
                    }
                }
            }
        };
    }

    private static ArrayList<BookItem> convertToBookItemList(JSONArray array) {
        ArrayList<BookItem> list = new ArrayList<>();
        for (int i = 0; i < array.length(); ++i) {
            try {
                JSONObject object = array.getJSONObject(i);
                list.add(new BookItem(
                    object.getString("seller"),
                    object.getString("condition"),
                    object.getDouble("price"),
                    !object.getBoolean("isTransactionRequested")
                ));
            }
            catch (Exception e) {
                Log.e("Parsing JSON", e.getMessage());
            }
        }
        return list;
    }

    static Book jsonObjectToBook(JSONObject object) throws JSONException {
        Book book = new Book();
        book.setTitle(object.getString("title"));
        book.setAuthor(object.getString("author"));
        book.setISBN10(object.getString("isbn10"));
        book.setISBN13(object.getString("isbn13"));
        book.setPublisher(object.getString("publisher"));
        book.setItems(convertToBookItemList(object.getJSONArray("items")));
        return book;
    }
}
