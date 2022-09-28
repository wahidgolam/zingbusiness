package com.zingit.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.L;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class OrderDetails extends AppCompatActivity {
    String orderId;
    Order order;
    public TextView outletNameOrderID;
    public TextView itemNameQuantity;
    public TextView timerText;
    public TextView statusText;
    public TextView status2;
    Button prepared;
    Button notPrepared;
    TextView msgText;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        db = FirebaseFirestore.getInstance();
        outletNameOrderID = findViewById(R.id.outlet_name_id);
        itemNameQuantity = findViewById(R.id.item_name_quantity);
        timerText = findViewById(R.id.timer);
        statusText = findViewById(R.id.status_text);
        status2 =findViewById(R.id.status2);
        msgText = findViewById(R.id.msg_text);
        prepared = findViewById(R.id.prepared_button);
        notPrepared = findViewById(R.id.not_prepared_button);
        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderId");
        if(orderId!=null){
            if(Dataholder.orderList.size()==0)
            {
                fetchFromDatabase();
            }
            else {
                Toast.makeText(this, "Fetch from dataholder", Toast.LENGTH_SHORT).show();
                Log.e("OrderId",orderId);
                order = getOrderDetails();
                setupUI();
            }
        }
        if(order==null){
            Toast.makeText(this, "Fetch from database", Toast.LENGTH_SHORT).show();
            fetchFromDatabase();
        }
    }
    public void fetchFromDatabase(){
        LoadingDialog loadingDialog = new LoadingDialog(this, "Loading");
        Toast.makeText(this, "OrderId", Toast.LENGTH_SHORT).show();
        db.collection("order").document(orderId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    order = task.getResult().toObject(Order.class);
                    loadingDialog.dismissDialog();
                    setupUI();

                }
                else{
                    setupUI();
                }
            }
        });
    }
    public void setupUI(){
        String outletDetails = "";
        String itemDetails = "";
        if(order!=null) {
            outletDetails = "#" + orderId.substring(orderId.length() - 5);
            itemDetails = order.getItemName() + " x" + order.getQuantity();
            outletNameOrderID.setText(outletDetails);
            itemNameQuantity.setText(itemDetails);


            if(order.getStatusCode()==3){
                Timestamp zingTimestamp = order.getZingTime();
                Date date = new Date();
                Timestamp nowTime = new Timestamp(date);
                String timeDisplay = "";
                String status = "";
                long timeDifferenceSec = nowTime.getSeconds() - zingTimestamp.getSeconds();
                if(timeDifferenceSec<0){
                    status = "early";
                }
                else{
                    status = "late";
                }
                timeDifferenceSec = Math.abs(timeDifferenceSec);
                long timeDifferenceMin =  (long) (timeDifferenceSec)/60;
                if(timeDifferenceMin<60){
                    timeDisplay = timeDifferenceMin + " mins";
                }
                else{
                    timeDisplay = ((long) (timeDifferenceMin/60)) + " hours";
                }
                msgText.setText("Collection "+ status + " by "+timeDisplay);
            }
            else{
                timerText.setText("error");
                statusText.setText("already dispatched");
                notPrepared.setText("Go back");
                prepared.setVisibility(View.GONE);
                status2.setText("Order has been already collected");
                msgText.setText("Do not dispatch");
                Toast.makeText(this, "Outlet" + outletDetails + " item " + itemDetails,  Toast.LENGTH_SHORT).show();
            }
        }
        else {
            outletNameOrderID.setText("-/-");
            itemNameQuantity.setText("Invalid Order");
            timerText.setText("-/-");
            statusText.setText("does not exist");
            notPrepared.setText("Go back");
            prepared.setVisibility(View.GONE);
            status2.setText("Order does not exist");
            msgText.setText("Invalid order");
        }
    }
    public Order getOrderDetails(){
        LoadingDialog loadingDialog2 = new LoadingDialog(OrderDetails.this, "Loading ");
        loadingDialog2.startLoadingDialog();

        Order order = new Order();
        int i;
        Toast.makeText(this, "Orderlist Size " + Dataholder.orderList.size() , Toast.LENGTH_SHORT).show();


            for (i = 0; i < Dataholder.orderList.size(); i++) {
                if (Dataholder.orderList.get(i).getOrderID().equals(orderId)) {
                    Toast.makeText(getApplicationContext(), "i " + i, Toast.LENGTH_SHORT).show();
                    order = Dataholder.orderList.get(i);
                    Toast.makeText(getApplicationContext(), "orderName " + order.getItemName(), Toast.LENGTH_SHORT).show();
                    break;
                }

            }
            //Toast.makeText(getApplicationContext(), "i " +i, Toast.LENGTH_SHORT).show();
            loadingDialog2.dismissDialog();
            return order;
        }


    public void prepared(View view){
        //what if already dispatched
        //show how much late they have come
        //order name is null -> old order
        LoadingDialog loadingDialog2 = new LoadingDialog(OrderDetails.this, "Thanks for your pleasant service");
        loadingDialog2.startLoadingDialog();
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.dispatch);
        mp.start();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
        Date date = new Date();
        Timestamp collectedTimeStamp = new Timestamp(date);
        order.setStatusCode(4);
        order.setCollectedTime(collectedTimeStamp);
        if(orderId!=null) {
            updateOrderToDataHolder();
            db.collection("order").document(orderId).update("statusCode", 4, "collectedTime", collectedTimeStamp).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(OrderDetails.this, "Order has been dispatched", Toast.LENGTH_SHORT).show();
                    loadingDialog2.dismissDialog();
                    navigateToHome();
                }
            });
        }
        else{
            Toast.makeText(this, "Error: Order not updated", Toast.LENGTH_SHORT).show();
            loadingDialog2.dismissDialog();
            navigateToHome();
        }
    }
    public void notPrepared(View view){
        navigateToHome();
    }
    public void updateOrderToDataHolder(){
        for(int i =0; i<Dataholder.orderList.size(); i++){
            if(Dataholder.orderList.get(i).getOrderID().equals(orderId)){
                Dataholder.orderList.get(i).setStatusCode(4);
                Dataholder.orderList.get(i).setCollectedTime(order.getCollectedTime());
            }
        }
    }
    public void navigateToHome(){
        Intent intent = new Intent(this, Homescreen.class);
        startActivity(intent);
        finish();
    }
}