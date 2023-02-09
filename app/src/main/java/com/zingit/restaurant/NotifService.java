package com.zingit.restaurant;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
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
import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.dantsu.escposprinter.exceptions.EscPosBarcodeException;
import com.dantsu.escposprinter.exceptions.EscPosConnectionException;
import com.dantsu.escposprinter.exceptions.EscPosEncodingException;
import com.dantsu.escposprinter.exceptions.EscPosParserException;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;






public class NotifService extends Service {

    FirebaseFirestore db;
    String outletID;
    Intent notificationIntent;
    ArrayList<Payment> orderList;
    EscPosPrinter printer = null;

    @Override
    public void onCreate() {
        db = FirebaseFirestore.getInstance();
        super.onCreate();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        outletID = Dataholder.outlet.getId();
        notificationIntent = new Intent(this, MainActivity.class);
        orderList = new ArrayList<>();

        //Trying Pending Intent
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getActivity(this,
                    0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        }else {
            pendingIntent = PendingIntent.getActivity(this,
                    0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        }

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        createNotificationChannel("123");

        Notification notification = new NotificationCompat.Builder(this, "123")
                .setContentTitle("Zing Business Running")
                .setContentText("Your Shop is Open")
                .setSmallIcon(R.drawable.ic_notif)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);
        fetchRequests();

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
        //fetching requests
        Query query = db.collection("payment").whereEqualTo("outletID", Dataholder.outlet.getId()).whereEqualTo("statusCode", 1);
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
                                        AddWithCaution(payment);}

                                    //notification("New Order", "You have a new order");
                                    //Log.e("StatusCode","1");}
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
                else{
                    Log.d("SNAPSHOT EMPTY", "EMPTY");
                }
            }
        });
    }

/*    private static int number = 10000;
    public int getNextUniqueRandomNumber() {
        return number++;
    }*/
    public void AddWithCaution(Payment payment)
    {
        int flag=0;
        for(int i=0;i<orderList.size();i++)
        {
            if(orderList.get(i).getPaymentOrderID().equals(payment.getPaymentOrderID()))
                flag=1;
        }
        if(flag==0)
        {
            orderList.add(payment);
            if(Dataholder.outlet.getisPrinterAvailable()) {
                printSlip(payment);
                printSlip(payment);
            }

            try
            {sendNotification("New Order", "You have an order from " + payment.getUserName());}
            catch (Exception e)
            {
                Log.e("ERROR", e.getLocalizedMessage());
                try {
                    sendNotification("New Order", "You have an order from " + payment.getUserName());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

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

    private void createNotificationChannel(String id) {

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
            NotificationChannel notificationChannel = new NotificationChannel(id, "Zing Business", importance);
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

    public void sendNotification(String title, String message) throws IOException {

        createNotificationChannel("1234");

        final MediaPlayer mp = MediaPlayer.create(this, R.raw.notifsound);
        mp.start();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });

        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getActivity(this,
                    0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        }else {
            pendingIntent = PendingIntent.getActivity(this,
                    0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        }

        String channelId = "1234";



        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_notif)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setVibrate(new long[]{500, 500})
                        //.setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .setContentIntent(pendingIntent)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setChannelId("123");

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // Since android Oreo notification channel is needed.

        //pushing notification
        //mNotificationManager.notify(getNextUniqueRandomNumber(), notificationBuilder.build());
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

        /*Notification notification = new NotificationCompat.Builder(this, "123")
                .setContentTitle("Zing Business Running")
                .setContentText("Your Shop is Open")
                .setSmallIcon(R.drawable.ic_notif)
                .setContentIntent(pendingIntent)
                .build();*/
        Notification notification = new NotificationCompat.Builder(this, "123")
                .setContentTitle("New Order")
                .setContentText("You have a new order")
                .setSmallIcon(R.drawable.ic_notif)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);
    }



    private static int number = 10000;
    public int getNextUniqueRandomNumber() {
        return number++;
    }

    public String createPrintSlip(Payment payment)
    {
        String slip ="[C]<font size='big'>      ZING</font>";
        //slip = "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, this.getApplicationContext().getResources().getDrawableForDensity(R.drawable.logo_orange, DisplayMetrics.DENSITY_MEDIUM))+"</img>\n";
        slip += "[L]\n";
        slip += "[L]<b>Order type : ";
        slip += "[R]<font size='big'>        " + payment.getOrderType() + "</font>\n";
        slip += "[L]<b>" + "Order ID : ";
        slip += "[R]<font size='big'>        " +   "#" + payment.getPaymentOrderID().substring(payment.getPaymentOrderID().length()-4) + "</font>\n";
        slip += "[L]<b>" + "Order From : ";
        slip += "[R]<font size='big'>        " + payment.getUserName() + "</font>\n";
        //slip += "[L]<font size='big'>" + Dataholder.printingPayment.orderType + "           #" + Dataholder.printingPayment.getPaymentOrderID().substring(Dataholder.printingPayment.getPaymentOrderID().length()-4) + "</font>\n";
        //slip += "[L]<font size='big'>Order from        " + Dataholder.printingPayment.getUserName().toUpperCase() + "</font>\n";
        //Add phone no here
        slip += "[C]<b>=========================================\n";

        for(int i=0;i<payment.getOrderItems().size();i++)
        {
            slip += "[L]<font size='big-4'>" + payment.getOrderItems().get(i).getItemName() + "</font>";
            slip += "[R]<font size='big-4'> X" + payment.getOrderItems().get(i).getItemQuantity() + "</font>\n\n";
        }
        slip += "[C]<b>=========================================\n";

        slip += "[R]<font size='big-4'>     Total Amount: " + payment.getBasePrice() + "</font>\n";



        return slip;

    }


    /*public void startPrintingProcess()
    {
        Pos pos = new Pos();
    }*/



    public void printSlip(Payment payment)
    {

        String slip = createPrintSlip(payment);
        try {
            printer = new EscPosPrinter(BluetoothPrintersConnections.selectFirstPaired(), 203, 72f, 32);
        } catch (EscPosConnectionException e) {
            e.printStackTrace();
        }
        try{
            printer.printFormattedText(slip);

          /*  printer
                    .printFormattedText(
                            "[C]<img size='30'>https://zammit.s3-eu-west-1.amazonaws.com/website_assets/images/000/059/073/large/image.png?1664184913" +"</img>\n" +
                                    "[L]\n" +
                                    "[C]<u><font size='big'>ORDER N°045</font></u>\n" +
                                    "[L]\n" +
                                    "[C]================================\n" +
                                    "[L]\n" +
                                    "[L]<b>BEAUTIFUL SHIRT</b>[R]9.99e\n" +
                                    "[L]  + Size : S\n" +
                                    "[L]\n" +
                                    "[L]<b>AWESOME HAT</b>[R]24.99e\n" +
                                    "[L]  + Size : 57/58\n" +
                                    "[L]\n" +
                                    "[C]--------------------------------\n" +
                                    "[R]TOTAL PRICE :[R]34.98e\n" +
                                    "[R]TAX :[R]4.23e\n" +
                                    "[L]\n" +
                                    "[C]================================\n" +
                                    "[L]\n" +
                                    "[L]<font size='tall'>Customer :</font>\n" +
                                    "[L]Raymond DUPONT\n" +
                                    "[L]5 rue des girafes\n" +
                                    "[L]31547 PERPETES\n" +
                                    "[L]Tel : +33801201456\n" +
                                    "[L]\n" +
                                    "[C]<barcode type='ean13' height='10'>831254784551</barcode>\n" +
                                    "[C]<qrcode size='20'>http://www.developpeur-web.dantsu.com/</qrcode>"
                    );*/
        } catch (EscPosEncodingException e) {
            e.printStackTrace();
        } catch (EscPosBarcodeException e) {
            e.printStackTrace();
        } catch (EscPosParserException e) {
            e.printStackTrace();
        } catch (EscPosConnectionException e) {
            e.printStackTrace();
        }


    }






}
