package com.theironfoundry8890.stjohnsteacher;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public MyFirebaseMessagingService() {
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String TAG = "Message Recieved";
        // ...
        Log.v("message Recieved", String.valueOf(remoteMessage));
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            generateNotification(String.valueOf(remoteMessage.getData()));

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
//                scheduleJob();
            } else {
                // Handle message within 10 seconds
//                handleNow();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    public void generateNotification(String body) {


        if (body != null) {
            body = body.substring(body.indexOf("{message=") + 9, body.lastIndexOf("}"));
            Log.v("notificationText", body);
            String bodyArray[] = body.split("<comma3384>");

            String notificationTitle = bodyArray[0];
            String notificationDescription = bodyArray[1];


            NotificationManager notif = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notify = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                notify = new Notification.Builder
                        (getApplicationContext())
                        .setContentTitle(notificationTitle)
                        .setContentText(notificationDescription)
                        .setSmallIcon(R.drawable.notification_icon)
                        .build();
            }

            notify.flags |= Notification.FLAG_AUTO_CANCEL;
            notif.notify(0, notify);
        }
    }
}