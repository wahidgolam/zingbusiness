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

public class EarningPaymentsAdapter extends
        RecyclerView.Adapter<EarningPaymentsAdapter.ViewHolder>{

    public ArrayList<Payment> paymentList;
    Context context;

    public EarningPaymentsAdapter(ArrayList<Payment> paymentList){
        this.paymentList = paymentList;
    }
    @NonNull
    @Override
    public EarningPaymentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView;
        // Inflate the custom layout
        contactView = inflater.inflate(R.layout.earning_payment_rv, parent, false);
        // Return a new holder instance
        EarningPaymentsAdapter.ViewHolder viewHolder = new EarningPaymentsAdapter.ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EarningPaymentsAdapter.ViewHolder holder, int position) {
        Payment payments = paymentList.get(position);

        holder.orderTotal.setText("₹ "+payments.getBasePrice());
        String paymentOrderId = payments.getPaymentOrderID().toUpperCase();


        holder.paymentOrderId.setText("ID #" + paymentOrderId.substring(paymentOrderId.length()-4));

        holder.info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((OrderHistory) context).showDialog(payments);
            }
        });






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

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView paymentOrderId;
        public TextView orderTotal;
        public ImageView info;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            paymentOrderId = itemView.findViewById(R.id.paymentOrderId);
            orderTotal = itemView.findViewById(R.id.orderTotal);
            info = itemView.findViewById(R.id.info);
            context = itemView.getContext();

        }
    }
}
