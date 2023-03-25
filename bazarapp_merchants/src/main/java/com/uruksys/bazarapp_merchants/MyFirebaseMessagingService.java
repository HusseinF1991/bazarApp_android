package com.uruksys.bazarapp_merchants;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMessagingService";

    public MyFirebaseMessagingService() {
        Log.d(TAG, "constructor MyFirebaseMessagingService ");
    }


    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        Log.d(TAG, "Refreshed token: " + s);

        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.sharedPreferencesName , MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LoginActivity.sharedPreferences_Token , s);
        editor.apply();

        SaveTokenKey();
    }




    private void SaveTokenKey(){
        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.sharedPreferencesName , MODE_PRIVATE);
        String providerTitle = sharedPreferences.getString(LoginActivity.sharedPreferences_Username , null);
        String token = sharedPreferences.getString(LoginActivity.sharedPreferences_Token , null);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("providerTitle", providerTitle);
            jsonObject.put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Thread(() -> {

            RequestBody requestBody = RequestBody.create(LoginActivity.JSON, String.valueOf(jsonObject));
            Request request = new Request.Builder()
                    .url(LoginActivity.serverIp + "/saveMerchantToken.php")
                    .post(requestBody)
                    .build();

            String s = null;
            try {
                Response response = LoginActivity.client.newCall(request).execute();

                s = response.body().string();

                String finalS = s;

            } catch (IOException e) {
                e.printStackTrace();
                Log.d("SaveTokenKey", e.getMessage());;
            }
        }).start();
    }



    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                //scheduleJob();
            } else {
                // Handle message within 10 seconds
                //handleNow();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        if(remoteMessage.getNotification().getTitle().trim().equals("purchaseRequest")){
            purchaseRequestNotification(remoteMessage);
        }
        else if(remoteMessage.getNotification().getTitle().trim().equals("newMessage")){
            NewMessageNotification(remoteMessage);
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }





    private void NewMessageNotification(RemoteMessage remoteMessage){



        Intent intent1 = new Intent("receivedToMessenger");
        //intent1.putExtra("identityId", senderIdentityId);
        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent1);

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "myNotification";
            String description = remoteMessage.getNotification().getTitle();
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("CHANNEL_ID", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Create an explicit intent for an Activity in your app
//        Intent intent = new Intent(this, AlertDetails.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "CHANNEL_ID")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("New message received")
                .setContentText(remoteMessage.getNotification().getBody())
//                .setStyle(new NotificationCompat.BigTextStyle()
//                        .bigText("Much longer text that cannot fit one line..."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                //.setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }



    private void purchaseRequestNotification(RemoteMessage remoteMessage){

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "myNotification";
            String description = remoteMessage.getNotification().getTitle();
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("CHANNEL_ID", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Create an explicit intent for an Activity in your app
//        Intent intent = new Intent(this, AlertDetails.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "CHANNEL_ID")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("purchase request")
                .setContentText(remoteMessage.getNotification().getBody())
//                .setStyle(new NotificationCompat.BigTextStyle()
//                        .bigText("Much longer text that cannot fit one line..."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                //.setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}