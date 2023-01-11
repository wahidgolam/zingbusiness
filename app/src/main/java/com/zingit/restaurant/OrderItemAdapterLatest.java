package com.zingit.restaurant;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.Timestamp;
import com.ncorti.slidetoact.SlideToActView;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class OrderItemAdapterLatest extends RecyclerView.Adapter<com.zingit.restaurant.OrderItemAdapterLatest.ViewHolder>{
        private ArrayList<Payment> paymentList;
        Context context;
        int progress=0;
        int flag=1;  // for checking if order is on time or is running late   1 means on time 2 means late

        public OrderItemAdapterLatest(ArrayList<Payment> paymentList) {
            this.paymentList = paymentList;
        }

        @NonNull
        @Override
        public com.zingit.restaurant.OrderItemAdapterLatest.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View contactView = inflater.inflate(R.layout.order_rv_latest, parent, false);
            // Return a new holder instance
            com.zingit.restaurant.OrderItemAdapterLatest.ViewHolder viewHolder = new com.zingit.restaurant.OrderItemAdapterLatest.ViewHolder(contactView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull com.zingit.restaurant.OrderItemAdapterLatest.ViewHolder holder, int position) {
            Payment payment = paymentList.get(holder.getAdapterPosition());

            int statusCode = payment.getStatusCode();
            final int[] time = {0}; //default zing time - seconds
            holder.orderPreparingStatus.setVisibility(View.VISIBLE);
            holder.swipeToDispatchView.setVisibility(View.GONE);
            holder.slideToDispatch.resetSlider();
            Timer timer = new Timer();
            final String[] description = {""};









            //render basic data

            //holder.userName.setText(payment.getUserName());

            holder.userName.setText(payment.getUserName());

            String orderID = payment.getPaymentOrderID().substring(payment.getPaymentOrderID().length()-4);
            orderID = orderID.toUpperCase();
            holder.orderID.setText("Order "+"#"+orderID);
            //holder.orderID.setText("#"+payment.getPaymentOrderID().substring(payment.getPaymentOrderID().length()-4));



            final String[] timerDetails = {""};
            final long[] secs = {0};



           // Toast.makeText(context, "" + payment.getStatusCode(), Toast.LENGTH_SHORT).show();



            //request 3

            switch (statusCode)
            {


                case 2:  holder.orderPreparingStatus.setVisibility(View.VISIBLE);
                         holder.userName.setText(payment.getUserName());
                         holder.slideToDispatch.resetSlider();

                    final Runnable setTextViewUpdateRunnable = new Runnable() {
                        public void run() {
                            if (!holder.orderTime.getText().equals(String.valueOf(timerText(secs[0] + 1)))) {
                                if (secs[0]<=0)
                                {
                                    holder.orderPreparingStatus.setBackgroundResource(R.drawable.rad8_redback);

                                }
                                else
                                {
                                    holder.orderPreparingStatus.setBackgroundResource(R.drawable.rad_8_greenback);
                                }
                                holder.orderTime.setText (description[0]);

                            }
                        }
                    };
                    TimerTask task = new TimerTask() {
                        public void run() {
                            Timestamp zingTime = payment.getZingTime();
                            Date date = new Date();
                            Timestamp nowTime = new Timestamp(date);
                            //Log.e("Difference", nowTime.getSeconds()/60.0 - zingTime.getSeconds()/60+" ");
                            secs[0] = zingTime.getSeconds() - nowTime.getSeconds();
                            if(secs[0]>0)
                            {
                                description[0] = "Order Ready (" + secs[0]/60 + " mins left )";

                            }
                            else
                            {
                                description[0] = "Order Ready (" + Math.abs(secs[0]/60) + " mins late )";

                            }

                            ((Homescreen_latest) context).runOnUiThread(setTextViewUpdateRunnable);
                        }
                    };
                    timer.schedule(task, 0, 60000);




                    break;

                case 3:  holder.swipeToDispatchView.setVisibility(View.VISIBLE);
                         holder.orderPreparingStatus.setVisibility(View.GONE);
                         holder.slideToDispatch.resetSlider();
                         timer.cancel();
                         break;





                default:
                    Log.e("Error","error in data fetch");


            }





            //accept1*/

            holder.slideToDispatch.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
                @Override
                public void onSlideComplete(@NonNull SlideToActView slideToActView) {
                    slideToActView.getBumpVibration();
                    slideToActView.setBumpVibration(1000);

                    ((Homescreen_latest) context).OrderComplete(payment);
                        ((Homescreen_latest) context).CompletedOrders(payment.getPaymentOrderID());

                    //Toast.makeText(context, "Dispatched", Toast.LENGTH_SHORT).show();
                    //CompletedOrders

                }
            });



            holder.info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((Homescreen_latest) context).showOrderDialog(payment);
                    ((Homescreen_latest) context).infoDialogPayment = payment;


                }
            });

            holder.orderPreparingStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((Homescreen_latest) context).infoDialogPayment = payment;
                    ((Homescreen_latest) context).OrderReadyDialog();



                }
            });







        }

    public String timerText(long seconds){
        if(seconds>0)
            return  (int)seconds/60 +" mins left";
        else
            return (int)Math.abs(seconds)/60 + " mins late";
    }

        @Override
        public int getItemCount() {
            return paymentList.size();
        }

       public void filterList(ArrayList<Payment> paymentList1)
       {

        paymentList = paymentList1;
        notifyDataSetChanged();
       }

       public void orderAdded()
       {
           notifyDataSetChanged();
       }

       public void changePreparingColor()
       {

       }




        public class ViewHolder extends RecyclerView.ViewHolder {

            RelativeLayout swipeToDispatchView;
            public SlideToActView slideToDispatch;
            TextView orderID,userName,orderTime;
            public RelativeLayout orderPreparingStatus;
            ProgressBar progressBar;
            ImageView info;


            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                swipeToDispatchView = itemView.findViewById(R.id.swipeToDispatch);
                slideToDispatch = itemView.findViewById(R.id.slideToDispatch);
                orderID = itemView.findViewById(R.id.orderId);
                userName = itemView.findViewById(R.id.userName);
                //progressBar = itemView.findViewById(R.id.progressBar);
                orderPreparingStatus = itemView.findViewById(R.id.preparingStatus);
                orderTime = itemView.findViewById(R.id.order_time);
                info = itemView.findViewById(R.id.info);
                final String[] description = {""};



                context = itemView.getContext();
            }
        }
    }



