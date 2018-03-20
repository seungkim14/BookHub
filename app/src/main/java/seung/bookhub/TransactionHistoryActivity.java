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

public class TransactionHistoryActivity extends AppCompatActivity {
    private class Transaction {
        String id;
        boolean isSeller;
        String bookTitle;
        String requestMessage;
        String approvalMessage;

        Transaction(JSONObject object) throws JSONException {
            this.id = object.getString("id");
            this.isSeller = object.getBoolean("isSeller");
            this.bookTitle = object.getString("bookTitle");
            this.requestMessage = object.getString("requestMessage");
            this.approvalMessage = object.getString("approvalMessage");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        final ArrayList<Transaction> transactions = new ArrayList<>();
        final TransactionHistoryAdapter adapter = new
            TransactionHistoryAdapter(this, transactions);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = NetworkConfiguration.getURL() + "transactionHistory";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
          url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray array = response.getJSONArray("transactions");
                    for (int i = 0; i < array.length(); ++i) {
                        JSONObject object = array.getJSONObject(i);
                        Transaction transaction = new Transaction(object);
                        transactions.add(transaction);
                        adapter.notifyDataSetChanged();
                    }
                }
                catch (JSONException e) {
                    Log.e("Error", e.getMessage());
                }
            }
        }, Utilities.getErrorListener(this));
        queue.add(request);

        ListView listView = (ListView)
            findViewById(R.id.listView_transactionHistory);
        listView.setAdapter(adapter);
        listView.setClickable(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
              int i, long l) {
                Intent intent = new Intent(
                    TransactionHistoryActivity.this, TransactionActivity.class
                );
                Transaction transaction = transactions.get(i);
                intent.putExtra("transactionID", transaction.id);
                intent.putExtra("bookTitle", transaction.bookTitle);
                startActivity(intent);
                finish();
            }
        });
    }

    private class TransactionHistoryAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<Transaction> data;
        private LayoutInflater inflater;

        TransactionHistoryAdapter(Context context, ArrayList<Transaction> data)
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
                view = inflater.inflate(
                    R.layout.transaction_history_list_item, parent, false
                );
            }
            TextView bookView = (TextView) view.findViewById(
                R.id.textView_transactionHistoryListItem_book);
            TextView requestMessageView = (TextView) view.findViewById(
                R.id.textView_transactionHistoryListItem_requestMessage);
            TextView approvalMessageView = (TextView) view.findViewById(
                R.id.textView_transactionHistoryListItem_approvalMessage);
            Transaction transaction = data.get(position);
            bookView.setText(transaction.bookTitle);
            requestMessageView.setText(transaction.requestMessage);
            approvalMessageView.setText(transaction.approvalMessage);
            return view;
        }
    }
}
