package com.zingit.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class OrderHistory extends AppCompatActivity {
    FirebaseFirestore db;
    RecyclerView paymentRV;
    ArrayList<Payment> paymentList = new ArrayList<>();
    EarningPaymentsAdapter earningPaymentsAdapter = new EarningPaymentsAdapter(paymentList);
    EditText searchPaymentId;
    ImageView backBtn;
    Dialog infoDialog;

    ArrayList<OrderItem> newOrderItemList;
    RecyclerView infoDialogOrderRV;
    NewOrdersAdapter newOrdersAdapter1;
    LinearLayout DialogOrderReady;
    TextView DialogClose;
    ArrayList<OrderItem> dialogOrdersList = new ArrayList<>();
    TextView infoDialogOrderTotal;
    Payment infoDialogClickedPayment;
    TextView infoDialogOrderId;
    TextView infoDialogUserName;
    ImageView home,earning,settings;
    RelativeLayout swipeToDispatch;
    TextView infoDialogClose;
    TextView infoDialogOrderTime;
    TextView orderDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);


        setupUI();
        searchPaymentId = findViewById(R.id.search_orderID);
        searchPaymentId.addTextChangedListener(new TextWatcher() {
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
                ArrayList<Payment> paymentList1 = new ArrayList<>();
                for(Payment payments : paymentList)
                {
                    String orderId = payments.getPaymentOrderID();
                    if(orderId.substring(orderId.length()-4).toLowerCase().contains(toString.toLowerCase()))
                    {
                        paymentList1.add(payments);

                    }
                }
                earningPaymentsAdapter.filterList(paymentList1);

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),EarningScreen.class);
                startActivity(intent);
            }
        });

        DialogClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infoDialog.dismiss();
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





    }

    public void setupUI()
    {
        db = FirebaseFirestore.getInstance();
        paymentRV = findViewById(R.id.paymentOrderRV);
        backBtn = findViewById(R.id.backBtn);
        searchPaymentId = findViewById(R.id.search_orderID);
        earningPaymentsAdapter = new EarningPaymentsAdapter(paymentList);
        paymentRV.setAdapter(earningPaymentsAdapter);
        paymentRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        infoDialog = new Dialog(this);
        infoDialog.setContentView(R.layout.order_details_dialog);
        infoDialog.setCancelable(true);
        DialogClose = infoDialog.findViewById(R.id.close);
        DialogOrderReady = infoDialog.findViewById(R.id.order_ready);
        infoDialogOrderId = infoDialog.findViewById(R.id.orderId);
        newOrderItemList = new ArrayList<>();
        newOrdersAdapter1 = new NewOrdersAdapter(newOrderItemList);
        infoDialogUserName = infoDialog.findViewById(R.id.userName);

        infoDialogOrderTotal = infoDialog.findViewById(R.id.orderTotal);
        infoDialogOrderRV = infoDialog.findViewById(R.id.dialogOrderRV);
        infoDialogOrderRV.setAdapter(newOrdersAdapter1);
        infoDialogOrderRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        swipeToDispatch = infoDialog.findViewById(R.id.swipeToDispatch);
        infoDialogClose = infoDialog.findViewById(R.id.close);

        home = findViewById(R.id.home);
        earning = findViewById(R.id.earning);
        settings = findViewById(R.id.settings);

        swipeToDispatch.setVisibility(View.GONE);
        infoDialogOrderTime = infoDialog.findViewById(R.id.orderTime);
        orderDate = findViewById(R.id.orderDate);


        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy");
        orderDate.setText(sfd.format(Dataholder.earnings.getDate().toDate()));




        getPayments();
    }

    public void getPayments()
    {
        db.collection("payment").whereEqualTo("outletID", Dataholder.outlet.getId()).whereGreaterThan("placedTime",Dataholder.earnings.getDate()).whereLessThan("placedTime",endOfDay()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Payment payment = document.toObject(Payment.class);
                        Log.e("Payments", payment.getPaymentOrderID());
                        paymentList.add(payment);
                    }
                    earningPaymentsAdapter.notifyDataSetChanged();

                }
                else
                {
                    Log.e("Payments", task.getResult().toString());
                }
            }
        });
    }

    public Timestamp endOfDay() {
        Timestamp nowTime = Dataholder.earnings.getDate();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(nowTime.getSeconds()*1000);
        cal.set(Calendar.HOUR_OF_DAY, 23); //set hours to zero
        cal.set(Calendar.MINUTE, 59); // set minutes to zero
        cal.set(Calendar.SECOND, 59); //set seconds to zero
        Date date2 = cal.getTime();
        nowTime = new Timestamp(date2);
        return (nowTime);
    }

    public void showDialog(Payment payment)
    {

        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yy");
        String orderDate = (sfd.format(Dataholder.earnings.getDate().toDate()));

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
        infoDialogOrderTime.setText( orderDate +" at "+ orderTime);


        Window window = infoDialog.getWindow();
        window.setLayout(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);

        Log.e("Payment", payment.getPaymentOrderID());
        newOrdersAdapter1.itemList = payment.getOrderItems();

        infoDialogOrderTotal.setText("₹ "+payment.getBasePrice()+"");
        String orderID = payment.getPaymentOrderID().substring(payment.getPaymentOrderID().length()-4);
        orderID = orderID.toUpperCase();
        DialogOrderReady.setVisibility(View.GONE);

        infoDialogOrderId.setText(("Order #" +orderID));
        infoDialogUserName.setText("from " + payment.getUserName());


        newOrdersAdapter1.notifyDataSetChanged();
        infoDialogClickedPayment = payment;
        infoDialog.show();


    }
}