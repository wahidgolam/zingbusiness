package com.zingit.restaurant;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NotifService extends Service {

    FirebaseFirestore db;
    String outletID;

    @Override
    public void onCreate() {
        db = FirebaseFirestore.getInstance();
        super.onCreate();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        outletID = intent.getStringExtra("outletID");
        Intent notificationIntent = new Intent(this, Homescreen.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification notification = new NotificationCompat.Builder(this, "008")
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
    public void showNotification(Order order){
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.notifsound);
        mp.start();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        /*NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "024")
                .setSmallIcon(R.drawable.ic_notif)
                .setContentTitle(order.getItemName()+" x"+order.getQuantity())
                .setContentText("New order request")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(alarmSound)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);*/

        Uri soundUri = Uri.parse("android.resource://" + getApplicationContext()
                .getPackageName() + "/" + R.raw.notifsound);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, "024")
                        .setSmallIcon(R.drawable.ic_notif)
                        .setContentTitle(order.getItemName()+" x"+order.getQuantity())
                        .setContentText("New order request")
                        .setVibrate(new long[]{500, 500})
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setSound(soundUri)
                        .setFullScreenIntent(pendingIntent, true)
                        .setChannelId("024");

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);
        createNotificationChannel();
        notificationManager.notify(getNextUniqueRandomNumber() /* ID of notification */, notificationBuilder.build());


    }
    public void fetchRequests(){
        //fetching requests
        Query query = db.collection("order").whereEqualTo("outletID", outletID).whereEqualTo("statusCode", 1).whereGreaterThan("placedTime", startOfDay()).whereLessThan("placedTime", endOfDay()).orderBy("placedTime", Query.Direction.DESCENDING);
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
                                Log.d("NOTIFICATION", "New order: " + dc.getDocument().getData());
                                try {
                                    Order order = dc.getDocument().toObject(Order.class);
                                    showNotification(order);
                                }
                                catch (Exception e){
                                    e.printStackTrace();
                                }
                                break;
                            case MODIFIED:
                                Log.d("Modified", "Modified order: " + dc.getDocument().getData());
                                break;
                            case REMOVED:
                                Log.d("Removed", "Removed order: " + dc.getDocument().getData());
                        }
                    }
                }
            }
        });
    }

    private static int number = 10000;
    public int getNextUniqueRandomNumber() {
        return number++;
    }

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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library

        Uri soundUri = Uri.parse("android.resource://" + getApplicationContext()
                .getPackageName() + "/" + R.raw.notifsound);

        NotificationManager mNotificationManager = getSystemService(NotificationManager.class);

        List<NotificationChannel> channelList = mNotificationManager.getNotificationChannels();
        for(int i =0; i<channelList.size();i++){
            mNotificationManager.deleteNotificationChannel(channelList.get(i).getId());
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel("024", "Zing Bussiness", importance);
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
}
