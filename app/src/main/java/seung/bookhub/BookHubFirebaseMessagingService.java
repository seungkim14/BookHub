package seung.bookhub;

import android.content.Intent;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class BookHubFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Intent intent = new Intent();
        Map<String, String> data = remoteMessage.getData();
        String type = data.get("type");
        if (type.equals("chat")) {
            intent.setAction("seung.bookhub.CHAT_RECEIVED_MESSAGE");
            intent.putExtra("sender", data.get("sender"));
            intent.putExtra("text", data.get("text"));
        }
        else if (type.equals("transaction")) {
            intent.setAction("seung.bookhub.TRANSACTION_APPROVED");
        }
        sendBroadcast(intent);
    }
}
