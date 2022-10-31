package com.zingit.restaurant;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NotifService extends Service {

    FirebaseFirestore db;
    String outletID;
    Intent notificationIntent;

    @Override
    public void onCreate() {
        db = FirebaseFirestore.getInstance();
        super.onCreate();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.e("Notif", "onStartCommand");


        outletID = intent.getStringExtra("outletID");
        Log.e("OutletId",outletID);
         notificationIntent = new Intent(this, Homescreen_latest.class);

        //Trying Pending Intent
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getActivity(this,
                    0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        }else {
            pendingIntent = PendingIntent.getActivity(this,
                    0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        }
        /*PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);*/




        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        createNotificationChannel();

        Notification notification = new NotificationCompat.Builder(this, "123")
                .setContentTitle("Zing Business Running")
                .setContentText("Your Shop is Open")
                .setSmallIcon(R.drawable.ic_notif)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);
        fetchRequests();
        //do heavy work on a background thread
        //stopSelf();

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    /*public void showNotification(Payment payment){
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.notifsound);
        mp.start();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });

        Log.e("Notif", "showNotification");


        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        *//*NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "024")
                .setSmallIcon(R.drawable.ic_notif)
                .setContentTitle(order.getItemName()+" x"+order.getQuantity())
                .setContentText("New order request")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(alarmSound)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);*//*

        Uri soundUri = Uri.parse("android.resource://" + getApplicationContext()
                .getPackageName() + "/" + R.raw.notifsound);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, "024")
                        .setSmallIcon(R.drawable.ic_notif)
                        .setContentTitle("Received new order")
                        .setContentText("New order request")
                        .setVibrate(new long[]{500, 500})
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setSound(soundUri)
                        .setFullScreenIntent(pendingIntent, true)
                        .setChannelId("025");

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);
        createNotificationChannel();
        notificationManager.notify(getNextUniqueRandomNumber() *//* ID of notification *//*, notificationBuilder.build());


    }*/
    public void fetchRequests(){
        Log.e("Notif", "fetchRequest");
        //fetching requests
        Query query = db.collection("payment").whereEqualTo("outletID", Dataholder.outlet.getId()).whereEqualTo("statusCode", 1).whereGreaterThan("placedTime", startOfDay()).whereLessThan("placedTime", endOfDay());
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w("Order fetch error", "listen:error" + error.getLocalizedMessage());
                    return;
                }
                if(!snapshots.isEmpty()) {
                    Log.d("SNAPSHOT", String.valueOf(snapshots.isEmpty()));
                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                Log.d("NOTIFICATION ADDED", "New order: " + dc.getDocument().getData());
                                try {
                                    Payment payment = dc.getDocument().toObject(Payment.class);
                                    if(payment.getStatusCode()==1){
                                    notification("New Order", "You have a new order");
                                    Log.e("StatusCode","1");}
                                }
                                catch (Exception e){
                                    e.printStackTrace();
                                }
                                break;
                            case MODIFIED:
                                Log.d("Modified", "Modified order: " + dc.getDocument().getData());
                                Payment payment = dc.getDocument().toObject(Payment.class);
                                //showNotification(payment);

                                break;
                            case REMOVED:
                                Log.d("Removed", "Removed order: " + dc.getDocument().getData());
                        }
                    }
                }
            }
        });
    }

/*    private static int number = 10000;
    public int getNextUniqueRandomNumber() {
        return number++;
    }*/

    public Timestamp startOfDay() {
        Date date = new Date();
        Timestamp nowTime = new Timestamp(date);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(nowTime.getSeconds()*1000);
        cal.set(Calendar.HOUR_OF_DAY, 0); //set hours to zero
        cal.set(Calendar.MINUTE, 0); // set minutes to zero
        cal.set(Calendar.SECOND, 0); //set seconds to zero
        Date date2 = cal.getTime();
        nowTime = new Timestamp(date2);
        return (nowTime);
    }
    public Timestamp endOfDay() {
        Date date = new Date();
        Timestamp nowTime = new Timestamp(date);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(nowTime.getSeconds()*1000);
        cal.set(Calendar.HOUR_OF_DAY, 23); //set hours to zero
        cal.set(Calendar.MINUTE, 59); // set minutes to zero
        cal.set(Calendar.SECOND, 59); //set seconds to zero
        Date date2 = cal.getTime();
        nowTime = new Timestamp(date2);
        return (nowTime);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }



    /*private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library

        Log.e("Notif", "CreateNotificationChannel");

        Uri soundUri = Uri.parse("android.resource://" + getApplicationContext()
                .getPackageName() + "/" + R.raw.notifsound);

        NotificationManager mNotificationManager = getSystemService(NotificationManager.class);

        List<NotificationChannel> channelList = mNotificationManager.getNotificationChannels();
        for(int i =0; i<channelList.size();i++){
            mNotificationManager.deleteNotificationChannel(channelList.get(i).getId());
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel("024", "Zing Business", importance);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

            if(soundUri != null){
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build();
                notificationChannel.setSound(soundUri,audioAttributes);
            }

            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }*/

    private void createNotificationChannel() {

        Log.e("Notif", "CreateNofitification");
        Uri soundUri = Uri.parse("android.resource://" + getApplicationContext()
                .getPackageName() + "/" + R.raw.notifsound);

        NotificationManager mNotificationManager = getSystemService(NotificationManager.class);

        List<NotificationChannel> channelList = mNotificationManager.getNotificationChannels();
        for(int i =0; i<channelList.size();i++){
            //mNotificationManager.deleteNotificationChannel(channelList.get(i).getId());
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel("123", "Zing Business", importance);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

            if(soundUri != null){
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build();
                notificationChannel.setSound(soundUri,audioAttributes);
            }

            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private void sendNotification(String title, String message) throws IOException {
        Log.e("SendNotification","yaha hu me");
        Intent intent = new Intent(this, Homescreen_latest.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "123";

        Uri soundUri = Uri.parse("android.resource://" + getApplicationContext()
                .getPackageName() + "/" + R.raw.notifsound);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_notif)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setVibrate(new long[]{500, 500})
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setSound(soundUri)
                        .setFullScreenIntent(pendingIntent, true)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setChannelId("123");

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);

        // Since android Oreo notification channel is needed.
        createNotificationChannel();

        //pushing notification
        notificationManager.notify(getNextUniqueRandomNumber() /* ID of notification */, notificationBuilder.build());
    }

    public void notification(String title,String message)
    {
        Intent fullScreenIntent = new Intent(this, Homescreen_latest.class);
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(this, 0,
                fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, "123")
                        .setSmallIcon(R.drawable.ic_notif)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_CALL)

                        // Use a full-screen intent only for the highest-priority alerts where you
                        // have an associated activity that you would like to launch after the user
                        // interacts with the notification. Also, if your app targets Android 10
                        // or higher, you need to request the USE_FULL_SCREEN_INTENT permission in
                        // order for the platform to invoke this notification.
                        .setFullScreenIntent(fullScreenPendingIntent, true);

        Notification incomingCallNotification = notificationBuilder.build();
        // Provide a unique integer for the "notificationId" of each notification.

        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getActivity(this,
                    0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        }else {
            pendingIntent = PendingIntent.getActivity(this,
                    0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        }

        Notification notification = new NotificationCompat.Builder(this, "123")
                .setContentTitle("Zing Business Running")
                .setContentText("Your Shop is Open")
                .setSmallIcon(R.drawable.ic_notif)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, incomingCallNotification);
    }



    private static int number = 10000;
    public int getNextUniqueRandomNumber() {
        return number++;
    }




}
