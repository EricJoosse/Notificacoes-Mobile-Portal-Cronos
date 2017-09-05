package pt.truewind.cronostest.service.push;

import android.content.Context;
import android.content.Intent;

import com.google.firebase.messaging.FirebaseMessagingService;

import pt.truewind.cronostest.log.Logger;

/**
 * Created by vasco.caetano on 04/11/2016.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(com.google.firebase.messaging.RemoteMessage remoteMessage) {
        Logger.e("MessageReceived");
        if(remoteMessage.getNotification() != null){
            String notification = remoteMessage.getNotification().getBody().toString();
            Logger.e("Notification - " + notification);
            updateMyActivity(this, notification);
        }
        if(!remoteMessage.getData().toString().equals("{}")) {
            Logger.e("Message - " + remoteMessage.getData().toString());
        }
    }

    // This function will create an intent. This intent must take as parameter the "unique_name" that you registered your activity with
    static void updateMyActivity(Context context, String message) {

        Intent intent = new Intent("com.google.firebase.MESSAGING_EVENT");

        //put whatever data you want to send, if any
        intent.putExtra("message", message);

        //send broadcast
        context.sendBroadcast(intent);
    }

}
