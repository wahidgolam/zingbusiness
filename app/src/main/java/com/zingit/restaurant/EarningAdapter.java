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

    private ArrayList<Earnings> earningList;
    Context context;

    public EarningAdapter(ArrayList<Earnings> earningList){
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
        Earnings earning = earningList.get(position);



        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy");
        holder.date.setText(sfd.format(earning.getDate().toDate()));
        holder.totalAmount.setText("₹ "+earning.getTotalAmount());

        Boolean paidStatus = earning.isPaidStatus();
        if(paidStatus)
        {
            holder.paymentStatus.setText("PAID");
            holder.paymentStatus.setBackgroundResource(R.drawable.rad_8_greenback);
        }
        else
        {
            holder.paymentStatus.setText("UNPAID");
            holder.paymentStatus.setBackgroundResource(R.drawable.rad8_redback);
        }

        holder.showOrderHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((EarningScreen) context).OrderHistory(earning);
                Dataholder.earnings = earning;

            }
        });
    }

    @Override
    public int getItemCount() {
        return earningList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView date;
        public TextView paymentStatus;
        public TextView totalAmount;
        public RelativeLayout showOrderHistory;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.earningDate);
            paymentStatus = itemView.findViewById(R.id.paidStatusTextView);
            totalAmount = itemView.findViewById(R.id.earningTotal);
            showOrderHistory = itemView.findViewById(R.id.checkOrderHistoryView);
            context = itemView.getContext();

        }
    }
}
