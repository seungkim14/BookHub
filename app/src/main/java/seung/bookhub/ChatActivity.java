package seung.bookhub;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {
    private String username;
    private RequestQueue requestQueue;
    private EditText sendTextView;
    private ArrayList<Message> messages;
    private MessagesAdapter adapter;
    private ListView listView;
    private String receiver;
    private String chatID;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Message message = new Message(
                intent.getStringExtra("sender"), intent.getStringExtra("text")
            );
            messages.add(message);
            adapter.notifyDataSetChanged();
        }
    };

    private class Message {
        String sender;
        String text;

        Message(JSONObject object) throws JSONException {
            this.sender = object.getString("sender");
            this.text = object.getString("text");
        }

        Message(String sender, String text) {
            this.sender = sender;
            this.text = text;
        }
    }

    private void updateMessages(String sender, String text) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        sendTextView = (EditText) findViewById(R.id.editText_chat_sendText);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("seung.bookhub.CHAT_RECEIVED_MESSAGE");
        registerReceiver(broadcastReceiver, intentFilter);

        username = new UserManager().getCurrentUser().getUsername();

        messages = new ArrayList<>();
        adapter = new MessagesAdapter(this, messages);
        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(adapter.getCount() - 1);
            }
        });

        Intent intent = getIntent();

        requestQueue = Volley.newRequestQueue(this);
        String url = NetworkConfiguration.getURL() + "chat/"
                   + intent.getStringExtra("transactionID");
        receiver = intent.getStringExtra("otherUser");
        getSupportActionBar().setTitle("Chat with " + receiver);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
          url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray array = response.getJSONArray("messages");
                    for (int i = 0; i < array.length(); ++i) {
                        JSONObject object = array.getJSONObject(i);
                        Message message = new Message(object);
                        messages.add(message);
                        adapter.notifyDataSetChanged();
                    }
                    chatID = response.getString("chatID");
                }
                catch (JSONException e) {
                    Log.e("Error", e.getMessage());
                }
            }
        }, Utilities.getErrorListener(this));
        requestQueue.add(request);

        listView = (ListView) findViewById(R.id.listView_chat);
        listView.setAdapter(adapter);
    }

    public void onSend(View view) {
        String url = NetworkConfiguration.getURL() + "sendMessage";

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.putOpt("receiver", receiver);
            requestBody.putOpt("text", sendTextView.getText());
            requestBody.putOpt("chatID", chatID);
        }
        catch (Exception e) {
            Log.e("Error", e.getMessage());
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
          url, requestBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    boolean success = response.getBoolean("success");
                    if (success) {
                        Message message = new Message(
                            username, sendTextView.getText().toString()
                        );
                        messages.add(message);
                        adapter.notifyDataSetChanged();
                        sendTextView.setText("");
                    }
                    else {
                        throw new JSONException("Message sending unsuccessful");
                    }
                }
                catch (JSONException e) {
                    Log.e("Error", e.getMessage());
                    Toast.makeText(getBaseContext(), "Failed to send message",
                        Toast.LENGTH_LONG).show();
                }
            }
        }, Utilities.getErrorListener(this));
        requestQueue.add(request);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    private class MessagesAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<Message> data;
        private LayoutInflater inflater;

        MessagesAdapter(Context context, ArrayList<Message> data) {
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
                view = inflater.inflate(R.layout.chat_message, parent, false);
            }
            TextView messageView = (TextView)
                view.findViewById(R.id.textView_chatMessage_text);
            Message message = data.get(position);
            RelativeLayout.LayoutParams layoutParams =
                (RelativeLayout.LayoutParams) messageView.getLayoutParams();
            messageView.setText(message.text);
            if (username.equals(message.sender)) {
                layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                messageView.setBackgroundResource(R.drawable.bubble_bg_blue);
                messageView.setTextAppearance(R.style.bubble_blue);
            }
            else {
                layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                messageView.setBackgroundResource(R.drawable.bubble_bg_gray);
                messageView.setTextAppearance(R.style.bubble_gray);
            }
            return view;
        }
    }
}
