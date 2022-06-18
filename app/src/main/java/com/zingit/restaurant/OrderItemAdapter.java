package com.zingit.restaurant;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder>{
    private ArrayList<Order> orderList;
    Context context;

    public OrderItemAdapter(ArrayList<Order> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.order_rv_view, parent, false);
        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orderList.get(holder.getAdapterPosition());
        int statusCode = order.getStatusCode();
        final int[] time = {20 * 60}; //default zing time - seconds

        TextView placedTime = holder.placedTime;
        TextView statusText = holder.statusText;
        TextView itemNameQuantity = holder.itemNameQuantity;
        TextView timerText = holder.timerText;
        TextView orderTotal = holder.orderTotal;

        LinearLayout request1 = holder.request1;
        LinearLayout request2 = holder.request2;
        LinearLayout request3 = holder.request3;
        LinearLayout active1 = holder.active1;
        RelativeLayout active2 = holder.active2;

        TextView group1text1 = holder.group1text1;
        TextView group1text2 = holder.group1text2;
        TextView group1text3 = holder.group1text3;
        TextView group1text4 = holder.group1text4;
        TextView group1text5 = holder.group1text5;
        TextView group2text1 = holder.group2text1;
        TextView group2text2 = holder.group2text2;
        TextView group2text3 = holder.group2text3;
        TextView group2text4 = holder.group2text4;
        TextView group2text5 = holder.group2text5;
        MaterialCardView group1card1 = holder.group1card1;
        MaterialCardView group1card2 = holder.group1card2;
        MaterialCardView group1card3 = holder.group1card3;
        MaterialCardView group1card4 = holder.group1card4;
        MaterialCardView group1card5 = holder.group1card5;

        Button acceptButton = holder.acceptButton;
        Button denyButton = holder.denyButton;
        SwitchMaterial preparedSwitch = holder.preparedSwitch;

        //setting up visibility
        if(statusCode==1){
            request1.setVisibility(View.VISIBLE);
            request2.setVisibility(View.VISIBLE);
            request3.setVisibility(View.VISIBLE);
            active1.setVisibility(View.GONE);
            active2.setVisibility(View.GONE);
            statusText.setText("paid");
        }
        else if(statusCode==2){
            active1.setVisibility(View.VISIBLE);
            active2.setVisibility(View.VISIBLE);
            request1.setVisibility(View.GONE);
            request2.setVisibility(View.GONE);
            request3.setVisibility(View.GONE);
            preparedSwitch.setChecked(false);
        }
        else if(statusCode==3||statusCode==4){
            request1.setVisibility(View.VISIBLE);
            request2.setVisibility(View.GONE);
            request3.setVisibility(View.GONE);
            active1.setVisibility(View.GONE);
            if(statusCode==3) {
                active2.setVisibility(View.VISIBLE);
            }
            else {
                active2.setVisibility(View.GONE);
            }
            if(statusCode==3){
                statusText.setText("prepared");
                preparedSwitch.setChecked(true);
                preparedSwitch.setClickable(false);
            }
            else {
                statusText.setText("completed");
            }
        }

        //render basic data
        String itemDetails = order.getItemName()+ " x"+ order.getQuantity();
        Timestamp placedTimestamp = order.getPlacedTime();
        Date date = new Date();
        Timestamp nowTime = new Timestamp(date);
        String timeDisplay = "";
        long timeDifferenceSec = nowTime.getSeconds() - placedTimestamp.getSeconds();
        long timeDifferenceMin =  (long) (timeDifferenceSec)/60;
        if(timeDifferenceMin<60){
            timeDisplay = timeDifferenceMin + " mins ago";
        }
        else{
            timeDisplay = ((long) (timeDifferenceMin/60)) + " hours ago";
        }
        String price = "₹ "+order.getTotalAmount()+".00";

        itemNameQuantity.setText(itemDetails);
        placedTime.setText(timeDisplay);
        //request 1
        orderTotal.setText(price);
        //request 2
        group1card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //change self style
                changeStyle(group1text1, group2text1, group1card1);
                //change other's style
                defaultStyle(group1text2, group2text2, group1card2);
                defaultStyle(group1text3, group2text3, group1card3);
                defaultStyle(group1text4, group2text4, group1card4);
                defaultStyle(group1text5, group2text5, group1card5);
                //change status
                time[0] = 5*60;
            }
        });
        group1card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //change self style
                changeStyle(group1text2, group2text2, group1card2);
                //change other's style
                defaultStyle(group1text1, group2text1, group1card1);
                defaultStyle(group1text3, group2text3, group1card3);
                defaultStyle(group1text4, group2text4, group1card4);
                defaultStyle(group1text5, group2text5, group1card5);
                //change status
                time[0] = 10*60;
            }
        });
        group1card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //change self style
                changeStyle(group1text3, group2text3, group1card3);
                //change other's style
                defaultStyle(group1text2, group2text2, group1card2);
                defaultStyle(group1text1, group2text1, group1card1);
                defaultStyle(group1text4, group2text4, group1card4);
                defaultStyle(group1text5, group2text5, group1card5);
                //change status
                time[0] = 15*60;
            }
        });
        group1card4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //change self style
                changeStyle(group1text4, group2text4, group1card4);
                //change other's style
                defaultStyle(group1text2, group2text2, group1card2);
                defaultStyle(group1text3, group2text3, group1card3);
                defaultStyle(group1text1, group2text1, group1card1);
                defaultStyle(group1text5, group2text5, group1card5);
                //change status
                time[0] = 20*60;
            }
        });
        group1card5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //change self style
                changeStyle(group1text5, group2text5, group1card5);
                //change other's style
                defaultStyle(group1text2, group2text2, group1card2);
                defaultStyle(group1text3, group2text3, group1card3);
                defaultStyle(group1text1, group2text1, group1card1);
                defaultStyle(group1text4, group2text4, group1card4);
                //change status
                time[0] = 25*60;
            }
        });
        //request 3
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Homescreen) context).acceptOrder(order,time[0]);
            }
        });
        denyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Homescreen) context).denyOrder(order);
            }
        });
        preparedSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(statusCode==2) {
                    ((Homescreen) context).orderPrepared(order);
                    preparedSwitch.setChecked(true);
                }
                else{
                    preparedSwitch.setChecked(true);
                }
            }
        });
        //accept1
        final String[] timerDetails = {""};
        final long[] secs = {0};
        Timer timer = new Timer();
        if (order.getStatusCode() == 2) {
            final Runnable setTextViewUpdateRunnable = new Runnable() {
                public void run() {
                    if (!timerText.getText().equals(String.valueOf(timerText(secs[0]+1)))) {
                        timerText.setText(timerText(secs[0]));
                    }
                }
            };
            TimerTask task = new TimerTask() {
                public void run() {
                    Timestamp zingTime = order.getZingTime();
                    Date date = new Date();
                    Timestamp nowTime = new Timestamp(date);
                    secs[0] = zingTime.getSeconds() - nowTime.getSeconds();
                    if (secs[0] <= 0 || order.getStatusCode() == 3) {
                        cancel();
                    }
                    ((Homescreen) context).runOnUiThread(setTextViewUpdateRunnable);
                }
            };
            timer.schedule(task, 0, 1000);
        }
    }
    public String timerText(long seconds){
        return "" + seconds / 60 + ":" + ((seconds % 60 < 10) ? "0" + seconds % 60 : seconds % 60);
    }
    public void changeStyle(TextView text, TextView subText, MaterialCardView card){
        card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.dark_orange));
        text.setTextColor(ContextCompat.getColor(context, R.color.white));
        subText.setTextColor(ContextCompat.getColor(context, R.color.white));
    }
    public void defaultStyle(TextView text, TextView subText, MaterialCardView card){
        card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.transparent_orange));
        text.setTextColor(ContextCompat.getColor(context, R.color.black));
        subText.setTextColor(ContextCompat.getColor(context, R.color.black));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView placedTime;
        TextView itemNameQuantity;
        TextView timerText;
        TextView orderTotal;
        TextView statusText;
        CardView orderCard;

        LinearLayout request1;
        LinearLayout request2;
        LinearLayout request3;
        LinearLayout active1;
        RelativeLayout active2;

        TextView group1text1;
        TextView group1text2;
        TextView group1text3;
        TextView group1text4;
        TextView group1text5;
        MaterialCardView group1card1;
        MaterialCardView group1card2;
        MaterialCardView group1card3;
        MaterialCardView group1card4;
        MaterialCardView group1card5;
        TextView group2text1;
        TextView group2text2;
        TextView group2text3;
        TextView group2text4;
        TextView group2text5;

        Button acceptButton;
        Button denyButton;
        SwitchMaterial preparedSwitch;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            placedTime = itemView.findViewById(R.id.placed_time);
            itemNameQuantity = itemView.findViewById(R.id.item_name_quantity);
            timerText = itemView.findViewById(R.id.timer);
            statusText = itemView.findViewById(R.id.status_text);
            orderTotal = itemView.findViewById(R.id.order_total);

            request1 = itemView.findViewById(R.id.request_1);
            request2 = itemView.findViewById(R.id.request_2);
            request3 = itemView.findViewById(R.id.request_3);

            active1 = itemView.findViewById(R.id.active_1);
            active2 = itemView.findViewById(R.id.active_2);

            group1text1 = itemView.findViewById(R.id.group1text1);
            group1text2 = itemView.findViewById(R.id.group1text2);
            group1text3 = itemView.findViewById(R.id.group1text3);
            group1text4 = itemView.findViewById(R.id.group1text4);
            group1text5 = itemView.findViewById(R.id.group1text5);

            group2text1 = itemView.findViewById(R.id.group2text1);
            group2text2 = itemView.findViewById(R.id.group2text2);
            group2text3 = itemView.findViewById(R.id.group2text3);
            group2text4 = itemView.findViewById(R.id.group2text4);
            group2text5 = itemView.findViewById(R.id.group2text5);

            group1card1 = itemView.findViewById(R.id.group1card1);
            group1card2 = itemView.findViewById(R.id.group1card2);
            group1card3 = itemView.findViewById(R.id.group1card3);
            group1card4 = itemView.findViewById(R.id.group1card4);
            group1card5 = itemView.findViewById(R.id.group1card5);

            acceptButton = itemView.findViewById(R.id.accept_button);
            denyButton = itemView.findViewById(R.id.deny_button);
            preparedSwitch = itemView.findViewById(R.id.switch1);

            context = itemView.getContext();
        }
    }
}
