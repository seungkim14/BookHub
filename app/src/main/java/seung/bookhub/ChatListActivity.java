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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChatListActivity extends AppCompatActivity {
    private String username;

    private class Chat {
        String seller;
        String buyer;
        String bookTitle;
        String transactionID;

        Chat(JSONObject object) throws JSONException {
            this.seller = object.getString("seller");
            this.buyer = object.getString("buyer");
            this.bookTitle = object.getString("bookTitle");
            this.transactionID = object.getString("transactionID");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        username = new UserManager().getCurrentUser().getUsername();

        final ArrayList<Chat> chats = new ArrayList<>();
        final ChatListAdapter adapter = new
            ChatListAdapter(this, chats);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = NetworkConfiguration.getURL() + "chats";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
          url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray array = response.getJSONArray("chats");
                    for (int i = 0; i < array.length(); ++i) {
                        JSONObject object = array.getJSONObject(i);
                        Chat chat = new Chat(object);
                        chats.add(chat);
                        adapter.notifyDataSetChanged();
                    }
                }
                catch (JSONException e) {
                    Log.e("Error", e.getMessage());
                }
            }
        }, Utilities.getErrorListener(this));
        queue.add(request);

        ListView listView = (ListView) findViewById(R.id.listView_chatList);
        listView.setAdapter(adapter);
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
              int i, long l) {
                Intent intent = new Intent(
                    ChatListActivity.this, ChatActivity.class
                );
                Chat chat = chats.get(i);
                intent.putExtra("transactionID", chat.transactionID);
                intent.putExtra("otherUser", chat.buyer.equals(username)
                                           ? chat.seller
                                           : chat.buyer);
                startActivity(intent);
            }
        });
    }

    private class ChatListAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<Chat> data;
        private LayoutInflater inflater;

        ChatListAdapter(Context context, ArrayList<Chat> data) {
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
                    android.R.layout.simple_list_item_2, parent, false
                );
            }
            TextView otherUserView = (TextView)
                view.findViewById(android.R.id.text1);
            TextView bookView = (TextView)
                view.findViewById(android.R.id.text2);
            Chat chat = data.get(position);
            if (username.equals(chat.seller)) {
                otherUserView.setText(chat.buyer + " (Buyer)");
            }
            else {
                otherUserView.setText(chat.seller + " (Seller)");
            }
            bookView.setText(chat.bookTitle);
            return view;
        }
    }
}
