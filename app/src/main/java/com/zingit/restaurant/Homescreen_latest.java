package com.zingit.restaurant;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
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
import com.ncorti.slidetoact.SlideToActView;
import com.zingit.restaurant.model.FcmToken;
import com.zingit.restaurant.model.RefundToken;
import com.zingit.restaurant.remote.FCMRetrofitClient;
import com.zingit.restaurant.remote.FcmCloudFunction;
import com.zingit.restaurant.remote.RefundCloudFunction;
import com.zingit.restaurant.remote.RefundRetrofitClient;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class Homescreen_latest extends AppCompatActivity {
    RecyclerView orderRV;
    OrderItemAdapterLatest orderItemAdapterLatest;
    ArrayList<Payment> paymentList;
    FirebaseFirestore db;
    String outletID;
    Outlet currentOutlet;
    Intent serviceIntent;
    //Dialog Views
    Dialog dialog;
    TextView dialogNoOfOrders;
    TextView dialogOrderId;
    TextView dialogUserName;
    TextView dialogOrderTime;
    RecyclerView dialogOrderRV;
    TextView dialogHelp;
    TextView dialogOrderTotal;
    TextView dialogSubstractTime;
    TextView dialogAddTime;
    TextView dialogZingTime;
    TextView dialogRejectOrder;
    TextView dialogAcceptOrder;
    NewOrdersAdapter newOrdersAdapter;
    ArrayList<OrderItem> newOrderItemList;
    int counter=0;
    FcmCloudFunction fcmCloudFunction;
    RefundCloudFunction refundCloudFunction;
    Dialog infoDialog;
    //LinearLayout DialogOrderReady;
    TextView DialogClose;
    RecyclerView infoDialogOrderRV;
    NewOrdersAdapter newOrdersAdapter1;
    ArrayList<OrderItem> dialogOrdersList = new ArrayList<>();
    TextView infoDialogOrderTotal;
    Payment infoDialogClickedPayment;
    TextView infoDialogHelp;
    TextView infoDialogOrderId;
    EditText searchOrderId;
    Switch openOutlet;
    RelativeLayout orderRVLayout;
    RelativeLayout orderEmptyView;
    ImageView orderEmptyImage;
    ImageView earnings;
    TextView infoDialogUserName;
    ImageView home,earning,settings;
    String FCMToken = "";
    LinearLayout infoOrderReady;
    RelativeLayout infoSwipeToDispatch;
    SlideToActView infoSlideToDispatch;
    Payment infoDialogPayment;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView infoDialogOrderTime;
    Payment payment;

    RelativeLayout refreshBtn;
    LinearLayout navBar;

    LinearLayout zingSearch;
    Switch open_close;
    TextView zingText;
    ImageView zingBusinessLogo;

    RelativeLayout registerPartner,logout;
    FirebaseAuth mAuth;
    FloatingActionButton qr_code;

    Context context;

    String applyUrl = "https://0owqe9mfaff.typeform.com/to/dv5erRgq?typeform-source=hottopdeal.com";






    int time; //default time for accepting order

    CompositeDisposable compositeDisposable;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen_latest);



        setupUI();

        dialogAcceptOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptOrders();
                Toast.makeText(Homescreen_latest.this, "Accept Orders", Toast.LENGTH_SHORT).show();
            }
        });
        dialogRejectOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DenyOrders();
            }
        });
        dialogAddTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(time>=60)
                {
                    Toast.makeText(Homescreen_latest.this, "Time cannot exceed 60 mins", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    time+=5;
                    dialogZingTime.setText( time + " mins");
                }

            }
        });
        dialogSubstractTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (time > 5) {
                    time -= 5;
                    dialogZingTime.setText(time + " mins");
                }
                else
                {
                    Toast.makeText(Homescreen_latest.this, "Time cannot be less than 5", Toast.LENGTH_SHORT).show();
                }
            }

        });

        DialogClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderInfoDialogClose();
            }
        });

        /*DialogOrderReady.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderReadyDialog();
            }
        });*/

        searchOrderId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }

            private void filter(String toString) {
                ArrayList<Payment> paymentList = new ArrayList<>();
                for(Payment payments : Dataholder.preparingOrderList)
                {
                    String orderId = payments.getPaymentOrderID();
                    if(orderId.substring(orderId.length()-4).toLowerCase().contains(toString.toLowerCase()))
                    {
                        paymentList.add(payments);

                    }
                }
                orderItemAdapterLatest.filterList(paymentList);

            }
        });

        /*openOutlet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b)   // If shop is closed then it will open with no conditions
                {

                    UpdateOutletStatus(b);
                    //Toast.makeText(Homescreen_latest.this, "OPEN", Toast.LENGTH_SHORT).show();
                    openOutlet.setText("Online");

                }
                else {

                    if(Dataholder.preparingOrderList.size()==0)   // To close a shop first check if it has pending orders or not
                    {
                        //Toast.makeText(Homescreen_latest.this, "CLOSED", Toast.LENGTH_SHORT).show();
                        openOutlet.setText("Offline");
                        UpdateOutletStatus(b);

                    }
                    else
                    {
                       // Toast.makeText(Homescreen_latest.this, "" + Dataholder.preparingOrderList.size() + "  " +  Dataholder.preparingOrderList.get(0).getId(), Toast.LENGTH_SHORT).show();
                        Log.e("PreparingList", Dataholder.preparingOrderList.get(0) + " ");
                        openOutlet.setChecked(true);
                        Toast.makeText(Homescreen_latest.this, "Cannot close shop with orders pending", Toast.LENGTH_SHORT).show();

                    }
                }


            }
        });*/



        openOutlet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean outletStatus = openOutlet.isChecked();
                if (outletStatus)   // If shop is closed then it will open with no conditions
                {
                    serviceIntent.putExtra("outletID", currentOutlet.getId());
                    ContextCompat.startForegroundService(getApplicationContext(), serviceIntent);

                    UpdateOutletStatus(outletStatus);
                    //Toast.makeText(Homescreen_latest.this, "OPEN", Toast.LENGTH_SHORT).show();
                    openOutlet.setText("Online");

                }
                else {

                    if(Dataholder.preparingOrderList.size()==0)   // To close a shop first check if it has pending orders or not
                    {
                        //Toast.makeText(Homescreen_latest.this, "CLOSED", Toast.LENGTH_SHORT).show();
                        openOutlet.setText("Offline");
                        stopService();
                        UpdateOutletStatus(outletStatus);

                    }
                    else
                    {
                       // Toast.makeText(Homescreen_latest.this, "" + Dataholder.preparingOrderList.size() + "  " +  Dataholder.preparingOrderList.get(0).getId(), Toast.LENGTH_SHORT).show();
                        Log.e("PreparingList", Dataholder.preparingOrderList.get(0) + " ");
                        openOutlet.setChecked(true);
                        Toast.makeText(Homescreen_latest.this, "Cannot close shop with orders pending", Toast.LENGTH_SHORT).show();

                    }
                }

            }
        });

        earning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),EarningScreen.class);
                startActivity(intent);
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Homescreen_latest.class);
                startActivity(intent);
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Settings.class);
                startActivity(intent);
            }
        });

        infoSlideToDispatch.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(@NonNull SlideToActView slideToActView) {
                infoDialog.dismiss();
                OrderComplete(infoDialogPayment);
                orderItemAdapterLatest.notifyDataSetChanged();
                infoSlideToDispatch.resetSlider();
            }
        });
        infoOrderReady.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               infoDialog.dismiss();
               OrderReadyDialog();


            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(getApplicationContext(),Login.class);
                startActivity(intent);
            }
        });

        infoDialogHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contactSupport();
            }
        });

        dialogHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contactSupport();
            }
        });





       /* swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setupUI();
                swipeRefreshLayout.setRefreshing(false);
            }
        });*/

        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //throw new RuntimeException("Test Crash"); // Force a crash
                //setupUI();
                Dataholder.preparingOrderList.clear();
                Dataholder.recentOrderList.clear();
                fetchOrders();
                Toast.makeText(Homescreen_latest.this, "Refresh", Toast.LENGTH_SHORT).show();
            }
        });

        registerPartner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent httpIntent = new Intent(Intent.ACTION_VIEW);
                httpIntent.setData(Uri.parse(applyUrl));
                startActivity(httpIntent);
            }
        });





    }








    public void OrderInfoDialogClose() {
        infoDialog.dismiss();
    }

    public void OrderReadyDialog()
    {
        payment = infoDialogPayment;
        //Payment payment1 = infoDialogPayment;


         db.collection("payment").document(payment.getId()).update("statusCode",3).addOnCompleteListener(new OnCompleteListener<Void>() {
             @Override
             public void onComplete(@NonNull Task<Void> task) {
                 if(task.isSuccessful())
                 {
                     getFCMToken(payment.getUserID(),"Order Prepared","Your order of # " +payment.getPaymentOrderID().substring(payment.getPaymentOrderID().length()-4)+  "is Prepared");
                     orderItemAdapterLatest.notifyDataSetChanged();
                    // Toast.makeText(Homescreen_latest.this, "Ready to Dispatch", Toast.LENGTH_SHORT).show();
                     infoDialog.dismiss();

                 }
             }
         });
    }







    public void setupUI()
    {

        View view = getLayoutInflater().inflate(R.layout.new_order_dialog,null);
        dialog = new Dialog(Homescreen_latest.this, android.R.style.Theme_DeviceDefault_Light_NoActionBar );
        dialog.setContentView(R.layout.new_order_dialog);
        dialog.setCancelable(false);

        infoDialog = new Dialog(Homescreen_latest.this);
        infoDialog.setContentView(R.layout.order_details_dialog);
        infoDialog.setCancelable(true);



        dialogNoOfOrders = dialog.findViewById(R.id.no_of_orders);
        dialogOrderId = dialog.findViewById(R.id.orderId);
        dialogOrderTime = dialog.findViewById(R.id.orderTime);
        dialogUserName =dialog.findViewById(R.id.userName);

        dialogHelp = dialog.findViewById(R.id.help);
        dialogOrderTotal = dialog.findViewById(R.id.orderTotal);
        dialogSubstractTime = dialog.findViewById(R.id.substractTime);
        dialogAddTime = dialog.findViewById(R.id.addTime);
        dialogZingTime = dialog.findViewById(R.id.timer);
        dialogRejectOrder = dialog.findViewById(R.id.rejectOrder);
        dialogAcceptOrder = dialog.findViewById(R.id.acceptOrder);

        paymentList = new ArrayList<>();
        orderRV = findViewById(R.id.orderRV);
        orderItemAdapterLatest = new OrderItemAdapterLatest(Dataholder.preparingOrderList);
        orderRV.setAdapter(orderItemAdapterLatest);
        orderRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));


        newOrderItemList = new ArrayList<>();
        dialogOrderRV = dialog.findViewById(R.id.orderRV1);
        newOrdersAdapter = new NewOrdersAdapter(dialogOrdersList);
        dialogOrderRV.setAdapter(newOrdersAdapter);
        dialogOrderRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        infoDialogUserName = infoDialog.findViewById(R.id.userName);



        db = FirebaseFirestore.getInstance();
        compositeDisposable = new CompositeDisposable();
        refundCloudFunction = RefundRetrofitClient.getInstance().create(RefundCloudFunction.class);
        fcmCloudFunction = FCMRetrofitClient.getRetrofitInstance().create(FcmCloudFunction.class);

        newOrdersAdapter1 = new NewOrdersAdapter(newOrderItemList);
        DialogClose = infoDialog.findViewById(R.id.close);
        //DialogOrderReady = infoDialog.findViewById(R.id.order_ready);
        infoDialogOrderId = infoDialog.findViewById(R.id.orderId);

        infoDialogOrderTotal = infoDialog.findViewById(R.id.orderTotal);
        infoDialogOrderRV = infoDialog.findViewById(R.id.dialogOrderRV);
        infoDialogOrderRV.setAdapter(newOrdersAdapter1);
        infoDialogOrderRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));



        searchOrderId = findViewById(R.id.search_orderID);
        openOutlet = findViewById(R.id.open_close_switch);
        orderRVLayout = findViewById(R.id.orderRV_View);
        orderEmptyImage = findViewById(R.id.orderEmptyImage);
        orderEmptyView = findViewById(R.id.orderEmptyView);


        home = findViewById(R.id.home);
        earning = findViewById(R.id.earning);
        settings = findViewById(R.id.settings);

        infoOrderReady = infoDialog.findViewById(R.id.order_ready);
        infoSwipeToDispatch = infoDialog.findViewById(R.id.swipeToDispatch);
        infoSlideToDispatch = infoDialog.findViewById(R.id.slideToDispatch);
        infoDialogHelp = infoDialog.findViewById(R.id.help);
        infoDialogPayment = new Payment();

        //swipeRefreshLayout = findViewById(R.id.swipeToRefresh);
        infoDialogOrderTime = infoDialog.findViewById(R.id.orderTime);

        zingText = findViewById(R.id.zingText);
        refreshBtn = findViewById(R.id.refresh);

        navBar = findViewById(R.id.navBar);
        zingSearch = findViewById(R.id.zing_search);
        open_close = findViewById(R.id.open_close_switch);
        zingText = findViewById(R.id.zingText);
        zingBusinessLogo = findViewById(R.id.zing_bussiness_logo);

        registerPartner = findViewById(R.id.register_as_partner);
        logout = findViewById(R.id.logout);
        mAuth = FirebaseAuth.getInstance();
        qr_code = findViewById(R.id.qr_scan);

        context = getApplicationContext();





        fetchOutletDetails();
        fetchOrders();



    }

    public void fetchOutletDetails() {
       // Toast.makeText(this, "Dataholder" + Dataholder.ownerUser.getOutletID(), Toast.LENGTH_SHORT).show();
        if (Dataholder.ownerUser.getOutletID() != null) {

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
                            if (currentOutlet.getOpenStatus().equals("OPEN")) {
                                try {
                                    openOutlet.setText("Online");
                                    openOutlet.setChecked(true);
                                    orderRVLayout.setVisibility(View.GONE);
                                    orderEmptyView.setVisibility(View.VISIBLE);
                                    orderEmptyImage.setBackgroundResource(R.drawable.store_openimg);
                                    serviceIntent.putExtra("outletID", currentOutlet.getId());
                                    ContextCompat.startForegroundService(getApplicationContext(), serviceIntent);
                                } catch (Exception e) {
                                    Toast.makeText(getApplicationContext(), "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                openOutlet.setChecked(false);
                                openOutlet.setText("Offline");
                                orderRVLayout.setVisibility(View.GONE);
                                orderEmptyView.setVisibility(View.VISIBLE);
                                orderEmptyImage.setImageResource(R.drawable.store_closed);
                                stopService();
                            }
                            //setupUI();
                            fetchOrders();
                            help();  //fetch support contact
                        } else {
                            Log.d("Firestore access", "No such document");
                        }
                    } else {
                        Log.d("Firestore access", "Got failed with ", task.getException());
                    }
                }
            });

        }
        else   // Outlet Not Registered
        {
            navBar.setVisibility(View.INVISIBLE);
            orderRV.setVisibility(View.INVISIBLE);
            refreshBtn.setVisibility(View.INVISIBLE);

            orderEmptyView.setVisibility(View.VISIBLE);

            zingSearch.setVisibility(View.INVISIBLE);
            open_close.setVisibility(View.INVISIBLE);
            zingText.setVisibility(View.INVISIBLE);
            zingBusinessLogo.setVisibility(View.VISIBLE);
            registerPartner.setVisibility(View.VISIBLE);
            logout.setVisibility(View.VISIBLE);
            orderEmptyImage.setImageResource(R.drawable.not_registered);
            qr_code.setVisibility(View.GONE);






        }
    }

    public void fetchOrders()
    {
        db = FirebaseFirestore.getInstance();
        Query query = db.collection("payment").whereGreaterThan("statusCode", 0).whereLessThan("statusCode",4).whereEqualTo("outletID",outletID);

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null)
                {
                    Log.e("Error", error.getLocalizedMessage());
                    return;
                }
                if(!value.isEmpty())
                {


                    for(DocumentChange dc : value.getDocumentChanges())
                    {
                        switch (dc.getType())
                        {
                            case ADDED:
                                Payment added_payment = dc.getDocument().toObject(Payment.class);
                                orderRVLayout.setVisibility(View.VISIBLE);
                                orderEmptyView.setVisibility(View.GONE);
                                orderEmptyImage.setBackgroundResource(R.drawable.store_openimg);
                                switch(added_payment.getStatusCode()){

                                    case 1: Log.e("Added Payment", counter++ + " ");
                                        //ContextCompat.startForegroundService(getApplicationContext(), serviceIntent);

                                        Log.e("Added Orders", added_payment.getPaymentOrderID());
                                    AddWithCaution(added_payment);
                                    break;
                                    case 2: Log.e("Added", added_payment.getPaymentOrderID());
                                        AddPreparingWithCaution(added_payment);
                                    break;

                                    case 3: AddPreparingWithCaution(added_payment);

                                    break;


                                    default:
                                        Log.e("Default","Default");
                                }
                                orderItemAdapterLatest.notifyDataSetChanged();
                                break;

                            case MODIFIED:

                                Payment modified_payment = dc.getDocument().toObject(Payment.class);
                                switch(modified_payment.getStatusCode()){

                                    case 1:
                                        break;
                                    case 2: Log.e("Order Modified", "Case 2");
                                        ModifyWithCaution(modified_payment);
                                    break;
                                    case 3: Log.e("Order Modified", "Case 3");
                                        UpdateStatusCode(modified_payment);
                                        break;

                                    case 4: Log.e("Order Modified","Case 4");
                                    break;

                                    case -3: Log.e("Order Modified", "Case -3");
                                             dialog.dismiss();
                                             Dataholder.recentOrderList.remove(0);
                                             orderItemAdapterLatest.notifyDataSetChanged();
                                             break;

                                        default: Log.e("Modified","Modified");


                                }

                                orderItemAdapterLatest.notifyDataSetChanged();
                                break;

                            case REMOVED:

                                Payment removed_payment = dc.getDocument().toObject(Payment.class);
                                if(Dataholder.preparingOrderList.size()==0)
                                {
                                    orderRVLayout.setVisibility(View.GONE);
                                    orderEmptyView.setVisibility(View.VISIBLE);
                                    orderEmptyImage.setBackgroundResource(R.drawable.store_openimg);
                                }
                                switch(removed_payment.getStatusCode()){

                                    case 1: RejectOrderWithCaution(removed_payment);
                                    Log.e("Refund","Order Denied");
                                    break;




                                    case 3: Log.e("Case 3","Removed 3");
                                        CompletedOrders(removed_payment.getPaymentOrderID());
                                        break;
                                    case 4: Log.e("Case 4","case 4");
                                    break;



                                    default:
                                }

                                orderItemAdapterLatest.notifyDataSetChanged();
                                break;

                        }
                    }
                }
            }
        });

    }
        public void stopService(){
            Intent serviceIntent = new Intent(this, NotifService.class);
            stopService(serviceIntent);
        }

    public void OrderComplete(Payment payment)   // Updating in database to status 4
    {
        db.collection("payment").document(payment.getId()).update("statusCode",4).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                //Toast.makeText(Homescreen_latest.this, "Order Dispatched", Toast.LENGTH_SHORT).show();
                addEarning(payment);
                CompletedOrders(payment.getPaymentOrderID());
                if(Dataholder.preparingOrderList.size()==0)
                {
                    orderRVLayout.setVisibility(View.GONE);
                    orderEmptyView.setVisibility(View.VISIBLE);
                    orderEmptyImage.setBackgroundResource(R.drawable.store_openimg);
                    infoDialog.dismiss();
                    orderItemAdapterLatest.notifyDataSetChanged();
                }
                else
                {
                    Log.e("PreparingList", Dataholder.preparingOrderList.get(0).getId());
                }
                orderItemAdapterLatest.notifyDataSetChanged();
                getFCMToken(payment.getUserID(),"Order Dispatched","Your order of #" +payment.getPaymentOrderID().substring(payment.getPaymentOrderID().length()-4)+  " is dispatched");



            }
        });


    }



    public void showDialog()
    {
        Log.e("ShowDialog","In show dialog");
        Payment newOrder = Dataholder.recentOrderList.get(0);

        Timestamp timestamp = newOrder.getPlacedTime();
        Date date = timestamp.toDate();
        DateFormat df = new SimpleDateFormat("HH:mm", Locale.US);
        String time_chat_s = df.format(date);
        Log.e("PlacedTime" , time_chat_s);
        String hh = time_chat_s.substring(0,2);
        String am_pm="";
        int hour=Integer.parseInt(hh);
        if(hour<=11)
        {
            am_pm = "AM";
        }
        else if(hour>12)
        {
            am_pm = "PM";
            hour = hour - 12;
        }
        else
        {
            am_pm = "PM";
        }

        String orderTime = hour + ":" + time_chat_s.substring(3,5) +" " +  am_pm;
        dialogOrderTime.setText("Today at " + orderTime);
        time = 15; // default time for all orders














        dialogNoOfOrders.setText(Dataholder.recentOrderList.size()+"");
        String orderId =  newOrder.getPaymentOrderID().substring(newOrder.getPaymentOrderID().length()-4).toUpperCase();
        dialogOrderId.setText("Order #" + orderId);
        dialogUserName.setText("from " + newOrder.getUserName());
        dialogOrderTotal.setText("₹ " + newOrder.getBasePrice());

        newOrdersAdapter.itemList = newOrder.getOrderItems();
        newOrdersAdapter.notifyDataSetChanged();

        dialog.show();
    }

    public void acceptOrders()
    {
        Date date = new Date();
        Timestamp acceptTimestamp = new Timestamp(date);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis((acceptTimestamp.getSeconds()+time*60)*1000);

        Timestamp zingTime = new Timestamp(cal.getTime());
        Log.e("OrderAccepted","Order Accepted");
        Payment payment = Dataholder.recentOrderList.get(0);


        db.collection("payment").document(payment.getId()).update("statusCode",2,"zingTime",zingTime, "reactionTime",acceptTimestamp).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    //Toast.makeText(Homescreen_latest.this, "Order Accepted", Toast.LENGTH_SHORT).show();
                    Log.e("OrderAccepted",Dataholder.recentOrderList.size() + " " );
                    getFCMToken(payment.getUserID(),"Order Accepted","Your order of #" +payment.getPaymentOrderID().substring(payment.getPaymentOrderID().length()-4)+  " is Accepted");

                }
                else
                {
                    Toast.makeText(Homescreen_latest.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void DenyOrders()
    {
        Date date = new Date();
        Timestamp acceptTimestamp = new Timestamp(date);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis((acceptTimestamp.getSeconds()+time)*1000);

        Timestamp zingTime = new Timestamp(cal.getTime());
        Payment payment = Dataholder.recentOrderList.get(0);


        db.collection("payment").document(payment.getId()).update("statusCode",-1, "reactionTime",acceptTimestamp).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(Homescreen_latest.this, "Order Rejected", Toast.LENGTH_SHORT).show();
                    initRefund(payment);
                }
            }
        });

    }


    public void AddWithCaution(Payment added_payment)
    {

        int flag = 0;
        for( int i=0;i<Dataholder.recentOrderList.size();i++)
        {
            if(Dataholder.recentOrderList.get(i).getId().equals(added_payment.getId())){
                flag = 1;
                break;
            }
        }
        orderItemAdapterLatest.orderAdded();
        Log.d("Flag of new order", flag+"");
        if(flag==0)
        {

            Dataholder.recentOrderList.add(added_payment);
        }
        if(Dataholder.recentOrderList.size()>0){
            showDialog();
        }
        orderItemAdapterLatest.notifyDataSetChanged();

    }

    public void CompletedOrders(String paymentOrderId) // Updating Locally to status 4
    {
        Log.e("Order Completed", "Case 4");
        //Toast.makeText(this, "Order Completed", Toast.LENGTH_SHORT).show();
       int i;
       int flag=0;
       for(i=0;i<Dataholder.preparingOrderList.size();i++)
       {
           if(Dataholder.preparingOrderList.get(i).getPaymentOrderID().equals(paymentOrderId))
           {
               flag=1;
               break;
           }

       }
       if(flag==1)
       {
           if(Dataholder.preparingOrderList.size()==1){
               Dataholder.preparingOrderList.clear();
               orderItemAdapterLatest.notifyDataSetChanged();
               //orderRV.setAdapter(orderItemAdapterLatest);

           }
           else{
               Dataholder.preparingOrderList.remove(i);
               printElements(Dataholder.preparingOrderList);
               orderItemAdapterLatest.notifyItemRemoved(i);
           }
       }

    }
    public void printElements(ArrayList<Payment> paymentList){
        for(Payment payment: paymentList){
            Log.d("printElements", payment.getPaymentOrderID()+":"+payment.getStatusCode());
        }
    }
    public void ModifyWithCaution(Payment modifiedPayment)
    {
        int flag=0;

        for(int i=0;i<Dataholder.recentOrderList.size();i++)
        {
            if(Dataholder.recentOrderList.get(i).getId().equals(modifiedPayment.getId()))
            {
                Log.e("Coming here", Dataholder.recentOrderList.size() + " ");

                flag=1;
                break;
            }
        }
        if(flag==1)
        {
            AddPreparingWithCaution(Dataholder.recentOrderList.get(0));
            Log.e("Coming here", Dataholder.recentOrderList.size() + " ");

            Dataholder.recentOrderList.remove(0);
            Log.e("ModifyAfterRemoved", Dataholder.recentOrderList.size() + " ");

            if(Dataholder.recentOrderList.size()!=0)
                showDialog();
            else
                dialog.dismiss();

        }
        newOrdersAdapter.notifyDataSetChanged();
        orderItemAdapterLatest.notifyDataSetChanged();
    }

    public void RejectOrderWithCaution(Payment payment)
    {
        int flag=0;
        for(int i=0;i<Dataholder.recentOrderList.size();i++)
        {
            if(Dataholder.recentOrderList.get(i).getId().equals(payment.getId()))
            {
                flag=1;
                break;
            }
        }
        if(flag==1)
        {
            Dataholder.recentOrderList.remove(0);
            //Toast.makeText(this, "Dataholder Size" + Dataholder.recentOrderList.size(), Toast.LENGTH_SHORT).show();
            if(Dataholder.recentOrderList.size()==0)
                dialog.dismiss();
            else
                showDialog();
        }
    }
    public void AddPreparingWithCaution(Payment payment)
    {
        int flag=0;
        int i;
        for(i=0;i<Dataholder.preparingOrderList.size();i++)
        {
            Log.e("Orders", Dataholder.preparingOrderList.get(i).getId() + "");
            if(Dataholder.preparingOrderList.get(i).getId().equals(payment.getId()))
            {
                Log.e("Comparing", Dataholder.preparingOrderList.get(i).getId() + "  " + payment.getId());
                flag=1;
                break;
            }
        }
        if(flag==0)
        {
            Log.e("Comparing1", Dataholder.preparingOrderList.size() + " ");
            Dataholder.preparingOrderList.add(payment);
            orderItemAdapterLatest.notifyDataSetChanged();
        }
        orderItemAdapterLatest.notifyDataSetChanged();

    }



    public void UpdateStatusCode(Payment payment)
    {
        int flag=0;
        int i;
        for( i=0;i<Dataholder.preparingOrderList.size();i++)
        {
            if(Dataholder.preparingOrderList.get(i).getId().equals(payment.getId()))
            {
                flag=1;
                break;
            }
        }
        if(flag==1 && i!=Dataholder.preparingOrderList.size())
        {
            Dataholder.preparingOrderList.get(i).setStatusCode(3);
            orderItemAdapterLatest.notifyDataSetChanged();
        }
    }

    public void showOrderDialog(Payment payment)
    {
        showInfoDialog(payment);
    }


    public void showInfoDialog(Payment payment)
    {

        Timestamp timestamp = payment.getPlacedTime();
        Date date = timestamp.toDate();
        DateFormat df = new SimpleDateFormat("HH:mm", Locale.US);
        String time_chat_s = df.format(date);


        Log.e("PlacedTime" , time_chat_s);

        String hh = time_chat_s.substring(0,2);
        String am_pm="";
        int hour=Integer.parseInt(hh);
        if(hour<=11)
        {
            am_pm = "AM";
        }
        else if(hour>12)
        {
            am_pm = "PM";
            hour = hour - 12;
        }
        else
        {
            am_pm = "PM";
        }

        String orderTime = hour + ":" + time_chat_s.substring(3,5) +" " +  am_pm;
        infoDialogOrderTime.setText("Today at " + orderTime);









        Window window = infoDialog.getWindow();
        window.setLayout(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        Log.e("Payment", payment.getStatusCode() + " ");
        if(payment.getStatusCode()==3)
        {
            infoOrderReady.setVisibility(View.GONE);
            infoSwipeToDispatch.setVisibility(View.VISIBLE);
        }
        else if(payment.getStatusCode()==2 || payment.getStatusCode()==1)
        {
            infoOrderReady.setVisibility(View.VISIBLE);
            infoSwipeToDispatch.setVisibility(View.GONE);
        }




        newOrdersAdapter1.itemList = payment.getOrderItems();

        infoDialogOrderTotal.setText("₹ "+payment.getBasePrice()+"");
        String orderID = payment.getPaymentOrderID().substring(payment.getPaymentOrderID().length()-4).toUpperCase();
        orderID.toUpperCase();

        infoDialogOrderId.setText(("Order #" +orderID));
        infoDialogUserName.setText("from " + payment.getUserName());

        newOrdersAdapter1.notifyDataSetChanged();
        infoDialogPayment = payment;
        infoDialog.show();

    }


















    public void SendingNotification(String title,String body,String FCMToken)
    {

        if(!FCMToken.equals("")) {
            compositeDisposable.add(fcmCloudFunction.sendNotification(FCMToken, title, body).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<FcmToken>() {
                        @Override
                        public void accept(FcmToken fcmToken) throws Exception {
                            if (fcmToken.getMulticast_id() != null) {
                                Log.e("Title",title + "Working");
                                //refund successful
                                //refund successful

                                // Toast.makeText(Homescreen.this, "Successful", Toast.LENGTH_SHORT).show();
                            } else {
                                // Toast.makeText(Homescreen.this, "Failure", Toast.LENGTH_SHORT).show();
                                //loadingDialog2.dismissDialog();
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            // Toast.makeText(Homescreen.this, "error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            //Toast.makeText(Homescreen.this, "Could not initiate refund for the last order. Contact support", Toast.LENGTH_SHORT).show();
                            Log.d("Initiating Payment: Error", throwable.getMessage());

                        }
                    }));
        }
    }
     public void initRefund(Payment payment){
        LoadingDialog loadingDialog2 = new LoadingDialog(Homescreen_latest.this, "Initiating refund");
        loadingDialog2.startLoadingDialog();
        //Log.d(order.getOrderID(), Long.toString(order.getTotalAmount()));
        compositeDisposable.add(refundCloudFunction.getToken(payment.getPaymentOrderID(), Long.toString((long)((long)Math.round (payment.getTotalAmountPaid()-payment.getTaxesAndCharges())))).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<RefundToken>() {
                    @Override
                    public void accept(RefundToken refundToken) throws Exception {
                        if(refundToken.getCf_refund_id()!=null) {
                            //refund successful
                            //refund successful
                            loadingDialog2.dismissDialog();
                            db.collection("payment").document(payment.getPaymentOrderID())
                                    .update(
                                            "statusCode",-1,
                                            "refundID", refundToken.getCf_refund_id()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            //Toast.makeText(Homescreen_latest.this, "Refund successful", Toast.LENGTH_SHORT).show();
                                            getFCMToken(payment.getUserID(),"Order Denied","Your order #" + payment.getPaymentOrderID().substring(payment.getPaymentOrderID().length()-4) + " has been denied, refund initiated");

                                        }
                                    });
                        }
                        else{
                            //Toast.makeText(Homescreen.this, "Could not initiate refund for the last order. Contact support", Toast.LENGTH_SHORT).show();
                            loadingDialog2.dismissDialog();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        //Toast.makeText(Homescreen.this, "error: "+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        //Toast.makeText(Homescreen.this, "Could not initiate refund for the last order. Contact support", Toast.LENGTH_SHORT).show();
                        Log.d("Initiating Payment: Error", throwable.getMessage());
                        loadingDialog2.dismissDialog();
                    }
                }));
    }

    public void UpdateOutletStatus(Boolean outletStatus)
    {
        String status = outletStatus==true?"OPEN":"CLOSED";
        db.collection("outlet").document(outletID).update("openStatus",status).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    if(outletStatus)
                    {
                        //Toast.makeText(Homescreen_latest.this, "Shop is Open", Toast.LENGTH_SHORT).show();
                        orderRVLayout.setVisibility(View.GONE);
                        orderEmptyImage.setImageResource(R.drawable.store_openimg);
                        orderEmptyView.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        Toast.makeText(Homescreen_latest.this, "Shop is Closed", Toast.LENGTH_SHORT).show();
                        orderRVLayout.setVisibility(View.GONE);
                        orderEmptyImage.setImageResource(R.drawable.store_closed);
                        orderEmptyView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    public void dispatchOrder(View view){
       // Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
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
                //Toast.makeText(getBaseContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                String paymentId = intentResult.getContents();
                searchPayment(paymentId);
                // if the intentResult is not null we'll set
                // the content and format of scan message
                /*Intent intent = new Intent(this, OrderDetails.class);
                intent.putExtra("orderId", intentResult.getContents());
                startActivity(intent);
                finish();*/
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void searchPayment(String paymentId)
    {
        int flag=0;
        for(int i=0;i<Dataholder.preparingOrderList.size();i++)
        {
            if(Dataholder.preparingOrderList.get(i).getPaymentOrderID().equals(paymentId))
            {
                showInfoDialog(Dataholder.preparingOrderList.get(i));
                flag=1;
                break;
            }
        }
        if(flag==0)
        {
            Toast.makeText(this, "Order Not Present", Toast.LENGTH_SHORT).show();
        }
    }


    public void addEarning(Payment payment){
        //orderBook.startLoadingDialog();
        db.collection("earnings").whereEqualTo("date", startOfDay()).whereEqualTo("outletID", Dataholder.outlet.getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.getResult().isEmpty()){
                    //Log.e("Earnings", task.getException().getLocalizedMessage());
                    Log.e("Earnings", "Creating new Doc");
                    createNewEarningDocument(payment);
                }
                else{
                    Earnings earning = new Earnings();
                    String key = "";
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        earning = document.toObject(Earnings.class);
                        key = document.getId();
                    }
                    Log.e("Earnings", "Updating Earnings");
                    updateEarning(key,earning,payment);
                }
            }
        });
    }
    public void updateEarning(String key, Earnings earning, Payment payment){
        //earning.addOrder(payment);
        earning.setTotalAmount(earning.getTotalAmount() + payment.getBasePrice());
        Log.e("Earnings", "Adding total");
        Log.e("Earnings", "" + earning.getTotalAmount());

        db.collection("earnings").document(key).set(earning).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });

    }


    public void createNewEarningDocument(Payment payment){
        Earnings earning = new Earnings((int)payment.getBasePrice(), false, Dataholder.outlet.getId(), startOfDay());
        db.collection("earnings").add(earning).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful())
                    Log.e("Earnings","New Earning Doc Added");
            }
        });
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

    public void getFCMToken(String StudentUserId,String title,String body)
    {
        String fcm;
        db.collection("studentUser").document(StudentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                try{
                    StudentWithFCM studentWithFCM = task.getResult().toObject(StudentWithFCM.class);
                    FCMToken = studentWithFCM.getFCMToken();
                    SendingNotification(title,body,FCMToken);
                }
                catch (Exception e)
                {
                    StudentWithoutFCM studentWithoutFCM = task.getResult().toObject(StudentWithoutFCM.class);
                    FCMToken =  "";
                }

            }
        });
    }

    public void help() {
        String campusID = Dataholder.outlet.getCampusID();
        db.collection("support").whereEqualTo("campusID", campusID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Support  support = document.toObject(Support.class);
                        Dataholder.support = support;
                    }
                }
            }
        });
    }

    public void contactSupport()
    {
        String text = "Hi, my payment orderID is " + infoDialogPayment.getPaymentOrderID() + " placed by " + infoDialogPayment.getUserName() + " total amount: " + infoDialogPayment.getBasePrice() ;
        String url = "https://api.whatsapp.com/send?phone=91" + Dataholder.support.getPhoneNumber()+ "&text=" + text;
        try {
            PackageManager pm = getApplicationContext().getPackageManager();
            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            i.putExtra(Intent.EXTRA_TEXT, text);
            i.setData(Uri.parse(url));
            getApplicationContext().startActivity(i);
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(getApplicationContext(), "Whatsapp is not installed in this phone", Toast.LENGTH_SHORT).show();
            Log.e("I am here",e.getLocalizedMessage());
        }
    }



    @Override
    protected void onStop() {
        super.onStop();
        compositeDisposable.clear();
    }



    /*@Override
    protected void onDestroy() {
        dialog.dismiss();
        super.onDestroy();
    }*/
}