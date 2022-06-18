package com.zingit.restaurant;

import static com.google.firebase.firestore.DocumentChange.Type.ADDED;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.WeakHashMap;

public class Homescreen extends AppCompatActivity {

    String outletID;
    ArrayList<Order> orderList;

    FirebaseFirestore db;

    LoadingDialog loadingDialog;
    LoadingDialog orderBook;
    TextView outletName;
    TextView outletDesc;
    TextView outletStatus;
    TextView outletZingTime;
    TextView opHeading;
    MaterialCardView requestTab;
    MaterialCardView activeTab;
    MaterialCardView historyTab;
    TextView requestText;
    TextView activeText;
    TextView historyText;
    RecyclerView orderRV;
    LinearLayout tabDetails;
    LinearLayout emptyOrderView;
    RelativeLayout background;
    Intent serviceIntent;

    Outlet currentOutlet;
    OrderItemAdapter orderItemAdapter;

    int request_new = 0;
    int active_new = 0;
    int history_new = 0;

    int op = 0;
    //0 -> request
    //1 -> active
    //2 -> history

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen);

        //Firebase variables
        db = FirebaseFirestore.getInstance();

        //UI variables
        outletName = findViewById(R.id.outlet_name);
        outletDesc = findViewById(R.id.outlet_desc);
        outletStatus = findViewById(R.id.outlet_status);
        outletZingTime = findViewById(R.id.outlet_zing_time);
        opHeading = findViewById(R.id.op_heading);
        requestTab = findViewById(R.id.request_tab);
        requestText = findViewById(R.id.request_text);
        activeTab = findViewById(R.id.active_tab);
        activeText = findViewById(R.id.active_text);
        historyTab = findViewById(R.id.history_tab);
        historyText = findViewById(R.id.history_text);
        orderRV = findViewById(R.id.orderRV);
        tabDetails = findViewById(R.id.tab_details);
        emptyOrderView = findViewById(R.id.empty_order_view);
        background = findViewById(R.id.background);

        //creating notification channel
        createNotificationChannel();


        //initialising DataHolder orderList
        Dataholder.orderList = new ArrayList<>();
        orderList = new ArrayList<>();

        //Loading dialogBox
        loadingDialog = new LoadingDialog(Homescreen.this, "Fetching today's orders");
        orderBook = new LoadingDialog(Homescreen.this, "Updating your earnings");
        loadingDialog.startLoadingDialog();
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                disappearDialog();
            }
        }, 3000);

        //setting up order recycler view
        orderItemAdapter = new OrderItemAdapter(orderList);
        orderRV.setAdapter((orderItemAdapter));
        orderRV.setLayoutManager(new LinearLayoutManager(this));
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        orderRV.addItemDecoration(
                new DividerItemDecoration(this, layoutManager.getOrientation()) {
                    @Override
                    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                        int position = parent.getChildAdapterPosition(view);
                        // hide the divider for the last child
                        if (position == state.getItemCount() - 1) {
                            outRect.setEmpty();
                        } else {
                            super.getItemOffsets(outRect, view, parent, state);
                        }
                    }
                }
        );
        //setting up basic UI
        fetchOutletDetails();
    }

    public void fetchOutletDetails(){
        if(Dataholder.ownerUser.getOutletID()!=null){
            outletID = Dataholder.ownerUser.getOutletID();
            db.collection("outlet").document(outletID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            currentOutlet = document.toObject(Outlet.class);
                            Dataholder.outlet = currentOutlet;
                            serviceIntent = new Intent(getApplicationContext(), NotifService.class);
                            if(currentOutlet.getOpenStatus().equals("OPEN")) {
                                serviceIntent.putExtra("outletID", currentOutlet.getId());
                                ContextCompat.startForegroundService(getApplicationContext(), serviceIntent);
                            }
                            else{
                                stopService();
                            }
                            setupUI();
                            fetchAllOrders();
                        } else {
                            Log.d("Firestore access", "No such document");
                        }
                    } else {
                        Log.d("Firestore access", "Got failed with ", task.getException());
                    }
                }
            });

        }else{
            //display blank ui
            Toast.makeText(this, "You haven't been registered yet", Toast.LENGTH_SHORT).show();
        }
    }
    public void setupUI(){
        outletName.setText(currentOutlet.getName());
        outletDesc.setText(currentOutlet.getDescription());
        outletStatus.setText(currentOutlet.getOpenStatus());
        String outletZingDisplayText = currentOutlet.getZingTime()+" mins";
        outletZingTime.setText(outletZingDisplayText);
        if(currentOutlet.getOpenStatus().equals("CLOSED")){
            background.setBackgroundColor(getColor(R.color.grey));
        }
        String opDetails = "";
        if(op==0)
            opDetails = "Order Requests";
        else if(op==1)
            opDetails = "Active orders";
        else
            opDetails = "Order History";
        opHeading.setText(opDetails);
        if (orderList.isEmpty()){
            orderRV.setVisibility(View.GONE);
            tabDetails.setVisibility(View.GONE);
            emptyOrderView.setVisibility(View.VISIBLE);
        }
        else{
            orderRV.setVisibility(View.VISIBLE);
            tabDetails.setVisibility(View.VISIBLE);
            emptyOrderView.setVisibility(View.GONE);
        }

    }
    public void fetchAllOrders(){
        //collect statusCode 2,3,4 -> active, prepared and history

        Query query = db.collection("order").whereEqualTo("outletID", outletID).whereEqualTo("statusCode", 4).whereGreaterThan("placedTime", startOfDay()).whereLessThan("placedTime", endOfDay()).orderBy("placedTime", Query.Direction.DESCENDING);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            history_new++;
                            Order order = document.toObject(Order.class);
                            Dataholder.orderList.add(order);
                        }
                        fetchPrepared();
                        Log.d("Calling fetchRequests", "CALLING");
                    } else {
                        Log.d("Outlet Data", "Error getting documents: ", task.getException());
                    }
                }
            }
        });
    }
    public void fetchPrepared(){
        //collect statusCode 2,3,4 -> active, prepared and history

        Query query = db.collection("order").whereEqualTo("outletID", outletID).whereEqualTo("statusCode", 3).orderBy("placedTime", Query.Direction.DESCENDING);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Order order = document.toObject(Order.class);
                            Dataholder.orderList.add(order);
                        }
                        fetchActive();
                        Log.d("Calling fetchRequests", "CALLING");
                    } else {
                        Log.d("Outlet Data", "Error getting documents: ", task.getException());
                    }
                }
            }
        });
    }
    public void fetchActive(){
        //collect statusCode 2,3,4 -> active, prepared and history

        Query query = db.collection("order").whereEqualTo("outletID", outletID).whereEqualTo("statusCode", 2).orderBy("placedTime", Query.Direction.DESCENDING);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            active_new++;
                            Order order = document.toObject(Order.class);
                            Dataholder.orderList.add(order);
                        }
                        fetchRequests();
                        Log.d("Calling fetchRequests", "CALLING");
                    } else {
                        Log.d("Outlet Data", "Error getting documents: ", task.getException());
                    }
                }
            }
        });
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
                                Log.d("Added", "New order: " + dc.getDocument().getData());
                                Order order = dc.getDocument().toObject(Order.class);
                                //implement agar order pehle se hi h toh aur add na kro - will prevent multiple additions to the orderlist
                                request_new++;
                                addWithCaution(order);
                                updateRVList();
                                orderItemAdapter.notifyDataSetChanged();
                                requestAction();
                                disappearDialog();
                                break;
                            case MODIFIED:
                                Log.d("Modified", "Modified order: " + dc.getDocument().getData());
                                break;
                            case REMOVED:
                                Log.d("Removed", "Removed order: " + dc.getDocument().getData());
                                if(dc.getDocument().toObject(Order.class).getStatusCode()==4) {
                                    history_new++;
                                    showNewOrders();
                                }
                        }
                    }
                    showNewOrders();
                }
            }
        });
    }
    public void showNewOrders(){
        if(request_new>0){
            requestText.setText(String.format("Requests (%d)", request_new));
        }
        else{
            requestText.setText("Requests");
        }
        if(active_new>0){
            activeText.setText(String.format("Active (%d)", active_new));
        }
        else{
            activeText.setText("Active");
        }
        if(history_new>0){
            historyText.setText(String.format("History (%d)", history_new));
        }
        else{
            historyText.setText("History");
        }
    }
    public void addWithCaution(Order order){
        if(isOrderPresent(order)==-1){
            Dataholder.orderList.add(order);
        }
    }
    public int isOrderPresent(Order order){
        for(int i=0; i<Dataholder.orderList.size(); i++){
            if(order.getOrderID().equals(Dataholder.orderList.get(i).getOrderID())){
                return i;
            }
        }
        return -1;
    }
    public void updateRVList(){
        orderList.clear();
        for(int i =0; i<Dataholder.orderList.size(); i++){
            boolean condition;
            Order order = Dataholder.orderList.get(i);
            if(op==0){
                condition = order.getStatusCode()==1;
            }
            else if(op==1){
                condition = order.getStatusCode()==2||order.getStatusCode()==3;
            }
            else {
                condition = order.getStatusCode()==4;
            }
            if(condition){
                orderList.add(order);
            }
        }
    }
    public void updateOrder(Order order) {
        for (int i = 0; i < Dataholder.orderList.size(); i++) {
            if (Dataholder.orderList.get(i).getOrderID().equals(order.getOrderID())) {
                Dataholder.orderList.set(i, order);
                break;
            }
        }
        updateRVList();
    }
    public void removeOrder(Order order){
        for(int i=0; i<Dataholder.orderList.size(); i++){
            if(Dataholder.orderList.get(i).getOrderID().equals(order.getOrderID())){
                Dataholder.orderList.remove(i);
                break;
            }
        }
        for(int i=0; i<orderList.size(); i++){
            if(orderList.get(i).getOrderID().equals(order.getOrderID())){
                orderList.remove(i);
                orderItemAdapter.notifyDataSetChanged();
                break;
            }
        }
    }


    public void acceptOrder(Order order, int time){

        active_new++;
        request_new--;
        showNewOrders();

        LoadingDialog loadingDialog2 = new LoadingDialog(Homescreen.this, "Updating order status");
        loadingDialog2.startLoadingDialog();

        Date date = new Date();
        Timestamp acceptTimestamp = new Timestamp(date);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis((acceptTimestamp.getSeconds()+time)*1000);

        Timestamp zingTime = new Timestamp(cal.getTime());

        int statusCode = 2;
        order.setZingTime(zingTime);
        order.setReactionTime(acceptTimestamp);
        order.setStatusCode(statusCode);
        db.collection("order").document(order.getOrderID())
                .update(
                        "statusCode", 2,
                        "zingTime", zingTime,
                        "reactionTime", acceptTimestamp).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(Homescreen.this, "You have accepted the order of "+order.getQuantity()+" "+order.getItemName(), Toast.LENGTH_LONG).show();
                updateOrder(order);
                orderItemAdapter.notifyDataSetChanged();
                loadingDialog2.dismissDialog();
                addEarning(order);
            }
        });
    }
    public void addEarning(Order order){
        orderBook.startLoadingDialog();
        db.collection("earning").whereEqualTo("date", startOfDay()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.getResult().isEmpty()){
                    createNewEarningDocument(order);
                }
                else{
                    Earning earning = new Earning();
                    String key = "";
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        earning = document.toObject(Earning.class);
                        key = document.getId();
                    }
                    updateEarning(key,earning,order);
                }
            }
        });
    }
    public void updateEarning(String key, Earning earning, Order order){
        earning.addOrder(order);
        db.collection("earning").document(key).set(earning).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                orderBook.dismissDialog();
            }
        });

    }
    public void createNewEarningDocument(Order order){
        Earning earning = new Earning((int)order.getTotalAmount(), false, order.getQuantity(), Dataholder.outlet.getId(), startOfDay());
        db.collection("earning").add(earning).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                orderBook.dismissDialog();
            }
        });
    }
    public void denyOrder(Order order){
        request_new--;
        showNewOrders();
        LoadingDialog loadingDialog2 = new LoadingDialog(Homescreen.this, "Updating order status");
        loadingDialog2.startLoadingDialog();

        Date date = new Date();
        Timestamp denyTimestamp = new Timestamp(date);

        int statusCode = 0;
        order.setReactionTime(denyTimestamp);
        order.setStatusCode(statusCode);
        db.collection("order").document(order.getOrderID())
                .update(
                        "statusCode", 0,
                        "reactionTime", denyTimestamp).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(Homescreen.this, "You have denied the order of "+order.getQuantity()+" "+order.getItemName(), Toast.LENGTH_LONG).show();
                updateOrder(order);
                orderItemAdapter.notifyDataSetChanged();
                loadingDialog2.dismissDialog();
            }
        });
    }
    public void orderPrepared(Order order){
        active_new--;
        showNewOrders();
        LoadingDialog loadingDialog2 = new LoadingDialog(Homescreen.this, "Updating order status");
        loadingDialog2.startLoadingDialog();

        Date date = new Date();
        Timestamp preparedTimestamp = new Timestamp(date);

        int statusCode = 3;
        order.setReactionTime(preparedTimestamp);
        order.setStatusCode(statusCode);
        db.collection("order").document(order.getOrderID())
                .update(
                        "statusCode", 3,
                        "reactionTime", preparedTimestamp).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                updateOrder(order);
                orderList.sort(Comparator.comparing(Order::getZingTime));
                orderList.sort(Comparator.comparing(Order::getStatusCode));
                orderItemAdapter.notifyDataSetChanged();
                loadingDialog2.dismissDialog();
            }
        });
    }
    public void changeStyle(TextView text, MaterialCardView card){
        card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.dark_orange));
        text.setTextColor(ContextCompat.getColor(this, R.color.white));
    }
    public void defaultStyle(TextView text, MaterialCardView card){
        card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.transparent_orange));
        text.setTextColor(ContextCompat.getColor(this, R.color.black));
    }

    public void requestsSelected(View view){
        //change adaptor
        requestAction();
    }
    public void requestAction(){
        op = 0;
        changeStyle(requestText,requestTab);
        defaultStyle(activeText,activeTab);
        defaultStyle(historyText,historyTab);

        LoadingDialog loadingDialog2 = new LoadingDialog(Homescreen.this, "Fetching new requests");
        loadingDialog2.startLoadingDialog();

        updateRVList();
        orderList.sort(Comparator.comparing(Order::getPlacedTime));
        Collections.reverse(orderList);
        orderItemAdapter.notifyDataSetChanged();
        setupUI();
        loadingDialog2.dismissDialog();
    }
    public void activeSelected(View view){
        op = 1;
        defaultStyle(requestText,requestTab);
        changeStyle(activeText,activeTab);
        defaultStyle(historyText,historyTab);

        LoadingDialog loadingDialog2 = new LoadingDialog(Homescreen.this, "Fetching active orders");
        loadingDialog2.startLoadingDialog();

        updateRVList();
        orderList.sort(Comparator.comparing(Order::getZingTime));
        orderList.sort(Comparator.comparing(Order::getItemID));
        orderList.sort(Comparator.comparing(Order::getStatusCode));
        orderItemAdapter.notifyDataSetChanged();
        setupUI();
        loadingDialog2.dismissDialog();
        //change adaptor
    }
    public void historySelected(View view){
        op = 2;
        defaultStyle(requestText,requestTab);
        defaultStyle(activeText,activeTab);
        changeStyle(historyText,historyTab);

        LoadingDialog loadingDialog2 = new LoadingDialog(Homescreen.this, "Fetching order history");
        loadingDialog2.startLoadingDialog();

        updateRVList();
        orderList.sort(Comparator.comparing(Order::getPlacedTime));
        Collections.reverse(orderList);
        orderItemAdapter.notifyDataSetChanged();
        setupUI();

        loadingDialog2.dismissDialog();
        //change adaptor
    }

    public void disappearDialog(){
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingDialog.dismissDialog();
            }
        }, 500);
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
    public void dispatchOrder(View view){
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setPrompt("Scan a barcode or QR Code");
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.initiateScan();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        // if the intentResult is null then
        // toast a message as "cancelled"
        if (intentResult != null) {
            if (intentResult.getContents() == null) {
                Toast.makeText(getBaseContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                // if the intentResult is not null we'll set
                // the content and format of scan message
                Intent intent = new Intent(this, OrderDetails.class);
                intent.putExtra("orderId", intentResult.getContents());
                startActivity(intent);
                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    public void goToSettings(View view){
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
        finish();
    }
    public void goToEarning(View view){
        Intent intent = new Intent(this, EarningScreen.class);
        startActivity(intent);
        finish();
    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Zing Business";
            String description = "Zing Business Channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("008", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    public void stopService(){
        Intent serviceIntent = new Intent(this, NotifService.class);
        stopService(serviceIntent);
    }
}