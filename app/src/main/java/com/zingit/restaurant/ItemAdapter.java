package com.zingit.restaurant;

import android.content.Context;
import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class ItemAdapter extends
        RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    public ArrayList<Item> itemList;
    Context context;

    public ItemAdapter(ArrayList<Item> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView;
        // Inflate the custom layout
        contactView = inflater.inflate(R.layout.item_rv_view, parent, false);
        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = itemList.get(holder.getAdapterPosition());
        // Set item views based on your views and data model
        //handle is available
        TextView itemName = holder.itemName;
        TextView itemPrice = holder.itemPrice;
        SwitchMaterial availableSwitch = holder.availableSwitch;
        RelativeLayout itemRVLayout = holder.itemRVLayout;

        //render basic data

        String priceDisplayText = "₹ "+item.getPrice()+".00";
        itemName.setText(item.getName());
        itemPrice.setText(priceDisplayText);

        availableSwitch.setChecked(item.isAvailableOrNot());
        availableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                item.setAvailableOrNot(isChecked);
                ((Settings) context).updateItemAvailability(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void filterList(ArrayList<Item> items)
    {

        itemList = items;
        notifyDataSetChanged();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder{
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView itemName;
        public TextView itemPrice;
        public ImageView itemImage;
        public ImageView vegImage;
        public RelativeLayout itemRVLayout;
        public SwitchMaterial availableSwitch;
        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            itemName = (TextView) itemView.findViewById(R.id.item_name);
            itemPrice = (TextView) itemView.findViewById(R.id.item_price);
            //itemImage = (ImageView) itemView.findViewById(R.id.item_image);
            //vegImage = (ImageView) itemView.findViewById(R.id.veg_image);
            availableSwitch = itemView.findViewById(R.id.available_switch);
            itemRVLayout = (RelativeLayout) itemView.findViewById(R.id.item_rv_layout);

            context = itemView.getContext();
        }
    }
}