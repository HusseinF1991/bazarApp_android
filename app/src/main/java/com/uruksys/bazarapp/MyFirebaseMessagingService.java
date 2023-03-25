package com.uruksys.bazarapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMessagingService";


    public MyFirebaseMessagingService() {
        Log.d(TAG, "MyFirebaseMessagingService: constructor");
    }


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        try {
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
                Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody() + ", title: " + remoteMessage.getNotification().getTitle());


                if (Objects.equals(remoteMessage.getNotification().getTitle(), MainActivity.CUSTOMER_NOTIFICATION_NEW_CHAT_MESSAGE)) {
                    NewMessageNotification(remoteMessage);
                } else if (remoteMessage.getNotification().getTitle().equals(MainActivity.CUSTOMER_NOTIFICATION_INVOICE_MODIFIED)) {
                    EditInvoiceNotification(remoteMessage);
                } else if (remoteMessage.getNotification().getTitle().equals(MainActivity.CUSTOMER_NOTIFICATION_INVOICE_DELIVERED)) {
                    invoiceDeliveredNotification(remoteMessage);
                } else if (remoteMessage.getNotification().getTitle().equals(MainActivity.CUSTOMER_NOTIFICATION_INVOICE_REJECTED)) {
                    invoiceRejectedNotification(remoteMessage);
                }
            }

            // Also if you intend on generating your own notifications as a result of a received FCM
            // message, here is where that should be initiated. See sendNotification method below.
        } catch (NullPointerException myException) {
            myException.printStackTrace();
        }
    }


    private void invoiceRejectedNotification(RemoteMessage remoteMessage) {
        try {
//        MySqliteDB mySqliteDB = new MySqliteDB(this);
//        mySqliteDB.DeleteInvoice_invoice(Integer.parseInt(remoteMessage.getNotification().getBody()));

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

            int invoiceId = Integer.parseInt(Objects.requireNonNull(remoteMessage.getData().get("invoiceId")));
            String shopName = remoteMessage.getData().get("shopName");
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "CHANNEL_ID")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getString(R.string.MyFirebaseMessagingService_String1))
//                    .setContentText(
//                            getString(R.string.MyFirebaseMessagingService_String2a) +
//                                    " " +
//                                    invoiceId +
//                                    " " +
//                                    getString(R.string.MyFirebaseMessagingService_String8) +
//                                    " " +
//                                    shopName +
//                                    " " +
//                                    getString(R.string.MyFirebaseMessagingService_String2b))
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(getString(R.string.MyFirebaseMessagingService_String2a) +
                                    " " +
                                    invoiceId +
                                    " " +
                                    getString(R.string.MyFirebaseMessagingService_String8) +
                                    " " +
                                    shopName +
                                    " " +
                                    getString(R.string.MyFirebaseMessagingService_String2b)))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    //.setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

            // notificationId is a unique int for each notification that you must define
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        } catch (NullPointerException myException) {
            myException.printStackTrace();
        }
    }


    private void invoiceDeliveredNotification(RemoteMessage remoteMessage) {
        try {
//        MySqliteDB mySqliteDB = new MySqliteDB(this);
//        mySqliteDB.InvoiceDelivered_invoice(Integer.parseInt(remoteMessage.getNotification().getBody()), "delivered");

            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = "myNotification";
                String description = Objects.requireNonNull(remoteMessage.getNotification()).getTitle();
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

            int invoiceId = Integer.parseInt(Objects.requireNonNull(remoteMessage.getData().get("invoiceId")));
            String shopName = remoteMessage.getData().get("shopName");
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "CHANNEL_ID")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getString(R.string.MyFirebaseMessagingService_String3))
//                    .setContentText(
//                            getString(R.string.MyFirebaseMessagingService_String4a) +
//                                    " " +
//                                    invoiceId +
//                                    " " +
//                                    getString(R.string.MyFirebaseMessagingService_String8) +
//                                    " " +
//                                    shopName +
//                                    " " +
//                                    getString(R.string.MyFirebaseMessagingService_String4b))
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(
                                    getString(R.string.MyFirebaseMessagingService_String4a) +
                                            " " +
                                            invoiceId +
                                            " " +
                                            getString(R.string.MyFirebaseMessagingService_String8) +
                                            " " +
                                            shopName +
                                            " " +
                                            getString(R.string.MyFirebaseMessagingService_String4b)))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    //.setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

            // notificationId is a unique int for each notification that you must define
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        } catch (NullPointerException myException) {
            myException.printStackTrace();
        }
    }


    private void EditInvoiceNotification(RemoteMessage remoteMessage) {
        try {
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


            int invoiceId = Integer.parseInt(Objects.requireNonNull(remoteMessage.getData().get("invoiceId")));
            String shopName = remoteMessage.getData().get("shopName");
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "CHANNEL_ID")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getString(R.string.MyFirebaseMessagingService_String5))
//                    .setContentText(
//                            getString(R.string.MyFirebaseMessagingService_String6) +
//                                    invoiceId +
//                                    " " +
//                                    getString(R.string.MyFirebaseMessagingService_String8) +
//                                    " " +
//                                    shopName +
//                                    " " +
//                                    getString(R.string.MyFirebaseMessagingService_String9))
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(
                                    getString(R.string.MyFirebaseMessagingService_String6) +
                                            " " +
                                            invoiceId +
                                            " " +
                                            getString(R.string.MyFirebaseMessagingService_String8) +
                                            " " +
                                            shopName +
                                            " " +
                                            getString(R.string.MyFirebaseMessagingService_String9)))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    //.setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

            // notificationId is a unique int for each notification that you must define
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        } catch (NullPointerException myException) {
            myException.printStackTrace();
        }
    }


    private void NewMessageNotification(RemoteMessage remoteMessage) {
        try {

            int invoiceId = Integer.parseInt(Objects.requireNonNull(remoteMessage.getData().get("invoiceId")));
            int invoiceShopId = Integer.parseInt(Objects.requireNonNull(remoteMessage.getData().get("invoiceShopId")));
            int shopId = Integer.parseInt(Objects.requireNonNull(remoteMessage.getData().get("shopId")));
            String shopName = remoteMessage.getData().get("shopName");
            if (InvoiceShopChatActivity.CurrentMessaging_invoiceShopId != invoiceShopId &&
                    InvoiceShopChatActivity.CurrentMessaging_shopId != shopId) {
                //used socket when app is on
//        Intent intent1 = new Intent("receivedToMessenger");
                //intent1.putExtra("identityId", senderIdentityId);
//        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent1);


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
                        .setContentTitle(getString(R.string.MyFirebaseMessagingService_String7))
//                .setContentText(remoteMessage.getNotification().getBody())
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(
                                        getString(R.string.MyFirebaseMessagingService_String10) +
                                                " " +
                                                invoiceId +
                                                " " +
                                                getString(R.string.MyFirebaseMessagingService_String8) +
                                                " " +
                                                shopName))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        //.setContentIntent(pendingIntent)
                        .setAutoCancel(true);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

                // notificationId is a unique int for each notification that you must define
                notificationManager.notify((int) System.currentTimeMillis(), builder.build());

            }
        } catch (NullPointerException myException) {
            myException.printStackTrace();
        }
    }


    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        Log.d(TAG, "Refreshed token: " + token);

        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.sharedPreferencesName, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MainActivity.sharedPreferences_Token, token);
        editor.apply();
        int customerId = sharedPreferences.getInt(MainActivity.sharedPreferences_CustomerId, 0);

        if (customerId != 0) {
            HttpRequests.UpdateCustomerToken(customerId, token);
        }
    }
}