package com.zingit.restaurant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EarningAdapter extends
        RecyclerView.Adapter<EarningAdapter.ViewHolder>{

    private ArrayList<Earning> earningList;
    Context context;

    public EarningAdapter(ArrayList<Earning> earningList){
        this.earningList = earningList;
    }
    @NonNull
    @Override
    public EarningAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView;
        // Inflate the custom layout
        contactView = inflater.inflate(R.layout.earning_rv_view, parent, false);
        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EarningAdapter.ViewHolder holder, int position) {
        Earning earning = earningList.get(position);
        TextView date = holder.date;
        TextView ordersProcessed = holder.ordersProcessed;
        TextView totalAmount = holder.totalAmount;
        TextView paidStatus = holder.paidStatus;
        ImageView statusImage = holder.statusImage;

        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy");
        date.setText(sfd.format(earning.getDate().toDate()));
        String ordersProcessedText = earning.getOrdersProcessed()+" orders";
        ordersProcessed.setText(ordersProcessedText);
        String totalAmountText = "₹ "+earning.getTotalAmount()+".00";
        totalAmount.setText(totalAmountText);
        String paidStatusText = (earning.isPaidStatus())?"paid":"unpaid";
        paidStatus.setText(paidStatusText);
        if(earning.isPaidStatus()){
            statusImage.setImageResource(R.drawable.paid);
        }
        else{
            statusImage.setImageResource(R.drawable.unpaid);
        }
    }

    @Override
    public int getItemCount() {
        return earningList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView date;
        public TextView ordersProcessed;
        public TextView totalAmount;
        public TextView paidStatus;
        public ImageView statusImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.date);
            ordersProcessed = (TextView) itemView.findViewById(R.id.orders_proessed);
            totalAmount = (TextView) itemView.findViewById(R.id.total_amount);
            paidStatus = (TextView) itemView.findViewById(R.id.status);
            statusImage = itemView.findViewById(R.id.status_image);
            context = itemView.getContext();

        }
    }
}
