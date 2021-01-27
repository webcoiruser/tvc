package com.pickanddrop;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.pickanddrop.activities.CameraActivity;
import com.pickanddrop.activities.DrawerContentSlideActivity;
import com.pickanddrop.activities.PaymentRequest;
import com.pickanddrop.utils.AppSession;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

public class MyFirebaseMEssagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";
    private AppSession appSession;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getData().toString());
        appSession = new AppSession(MyFirebaseMEssagingService.this);

        Map<String, String> data = remoteMessage.getData();
        try {

            String message = (String) new JSONObject(data.get("payload")).get("message");
            String title = (String) new JSONObject(data.get("payload")).get("title");
            String notification_for = (String) data.get("notification_for");
            Log.d(TAG, "From: notification_for " + notification_for);

            if(notification_for.equalsIgnoreCase("pickup_delivery")){
                String delivery_type = (String) new JSONObject(data.get("payload")).get("delivery_type");
                String orderId = (String) new JSONObject(data.get("payload")).get("order_id");
                Intent intent = new Intent(MyFirebaseMEssagingService.this, DrawerContentSlideActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("order_id",orderId);
                intent.putExtra("delivery_type",delivery_type.trim());
                intent.putExtra("opentraker","yesopen");
                startActivity(intent);
                sendNotification(title, message, getApplicationContext(),"normal","nodata","nodata","open_home_no");

            }else  if(notification_for.equalsIgnoreCase("accept_delivery")){

                String payload = new JSONObject(data.get("payload")).toString();
                Gson gson = new Gson();
                appSession.setNotificationData(payload);
                Log.d(TAG, "message: " + message);
                Log.d(TAG, "title: " + title);
                Log.d(TAG, "notipayload: " + payload);

                Intent intent = new Intent(MyFirebaseMEssagingService.this, PaymentRequest.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                sendNotification(title, message, getApplicationContext(),"normal","nodata","nodata","open_home_no");

            } else  if(notification_for.equalsIgnoreCase("deliver_order_multiple")) {
                String last_drop_point = (String) new JSONObject(data.get("payload")).get("last_drop_point");
                String delivery_type = (String) new JSONObject(data.get("payload")).get("delivery_type");
                String orderId = (String) new JSONObject(data.get("payload")).get("order_id");

                Log.d(TAG, "From: notification_for orderId " + orderId);

                if(last_drop_point.equalsIgnoreCase("1")){
                    sendNotification(title, message, getApplicationContext(), "custom",delivery_type,orderId,"open_home_no");

                }else {
                    sendNotification(title, message, getApplicationContext(), "normal",delivery_type,orderId,"open_home_no");

                }

            } else  if(notification_for.equalsIgnoreCase("deliver_order")) {
                String delivery_type = (String) new JSONObject(data.get("payload")).get("delivery_type");
                String orderId = (String) new JSONObject(data.get("payload")).get("order_id");
                Log.d(TAG, "From: notification_for  orderId " + orderId);
                sendNotification(title, message, getApplicationContext(),"custom",delivery_type,orderId,"open_home_no");

            } else {
                sendNotification(title, message, getApplicationContext(),"normal","nodata","nodata","open_home_yes");

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void sendNotification(String title, String aMessage, Context contex,String ringTypew,String delivery_type,String order_id,String open_home) {

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.loginlogo);

        String NOTIFICATION_CHANNEL_ID = String.valueOf(R.string.default_notification_channel_id);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "VanCarGo",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("VanCarGo");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);

        }

        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, DrawerContentSlideActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        if(!delivery_type.equalsIgnoreCase("nodata") && !order_id.equalsIgnoreCase("nodata")){
            Log.d(TAG, "From: delivery_type " + delivery_type);
            Log.d(TAG, "From: order_id " + order_id);
            intent.putExtra("order_id",order_id);
            intent.putExtra("delivery_type",delivery_type.trim());
            intent.putExtra("opentraker","yesopen");

        }
        if(ringTypew.equalsIgnoreCase("custom")){
            intent.putExtra("custom","toto_history");

        }
        //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);


        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.notification_ic)
                .setColor(Color.RED)
                .setLargeIcon(icon)
                .setPriority(Notification.PRIORITY_HIGH)
                .setTicker("VanCarGo")
                .setContentTitle(title)
                .setContentText(aMessage)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(aMessage))
                .setContentIntent(pendingIntent)
                .setContentInfo(title);

        if(!delivery_type.equalsIgnoreCase("nodata") && !order_id.equalsIgnoreCase("nodata")){
            notificationBuilder.setContentIntent(pendingIntent);

        }
        if(open_home.equalsIgnoreCase("open_home_yes")){
            notificationBuilder.setContentIntent(pendingIntent);

        }


        if(ringTypew.equalsIgnoreCase("custom")){
            customRingtone();

        }else {
            ringtone();

        }
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(Integer.parseInt(NOTIFICATION_CHANNEL_ID), notificationBuilder.build());

    }

    public void customRingtone() {
        try {
            Uri nsound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification_sound);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), nsound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void ringtone() {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}