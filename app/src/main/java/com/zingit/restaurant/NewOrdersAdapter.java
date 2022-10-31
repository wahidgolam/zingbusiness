package com.zingit.restaurant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.Locale;

public class NewOrdersAdapter extends RecyclerView.Adapter<com.zingit.restaurant.NewOrdersAdapter.ViewHolder> {
    public ArrayList<OrderItem> itemList;
    Context context;

    public NewOrdersAdapter(ArrayList<OrderItem> itemList) {
        this.itemList = itemList;
    }


    public NewOrdersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView;

        // Inflate the custom layout

        contactView = inflater.inflate(R.layout.new_items_rv, parent, false);
        // Return a new holder instance

        NewOrdersAdapter.ViewHolder viewHolder = new NewOrdersAdapter.ViewHolder(contactView);
        return viewHolder;
    }

    public void onBindViewHolder(@NonNull NewOrdersAdapter.ViewHolder holder, int position) {
        OrderItem item = itemList.get(holder.getAdapterPosition());



        holder.itemPrice.setText("₹ "+item.getItemQuantity()*item.getItemTotal());
        holder.itemQuantity.setText(item.getItemQuantity() + "x");
        String itemNameFormatted = FormatItemName(item.getItemName());
        itemNameFormatted = camelCase(itemNameFormatted);
        holder.itemName.setText(itemNameFormatted);



    }

    public int getItemCount() {
        return itemList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView itemName,itemQuantity,itemPrice;
        public ViewHolder(View itemView) {

            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            itemQuantity = itemView.findViewById(R.id.order_nos);
            itemPrice = itemView.findViewById(R.id.itemPrice);
            context = itemView.getContext();
        }
    }
    public String FormatItemName(String itemName)
    {
        String finalName ="";
        int f=0; // flag to check
        int c=0; // counter to count spaces
        int first_space=0;
        int third_space = 0;
        for(int i=0;i<itemName.length();i++)
        {
            char ch = itemName.charAt(i);
            if(ch==' ')
                c++;
            if(c==1 && ch==' ')
                first_space = i;
            if(c==3 && ch==' ')
               third_space = i;

        }
        if(itemName.length()>18 && c<=1)
        {
            finalName = itemName.substring(0,first_space) +"\n"+ itemName.substring(first_space+1);
        }
        else if(c>2 && c<=4)
        {
            finalName = itemName.substring(0,third_space) + itemName.substring(third_space+1);
        }
        else
            finalName = itemName;

        return finalName;

    }
    public String camelCase(String input){
        input = input.trim();
        input = " "+input;
        input = input.toLowerCase(Locale.ROOT);
        for(int i=0; i<input.length(); i++){
            if(input.charAt(i)==' '){
                input = input.substring(0,i+1) + (input.charAt(i+1)+"").toUpperCase(Locale.ROOT) +  input.substring(i+2);
            }
        }
        input = input.trim();
        return input;
    }

}
