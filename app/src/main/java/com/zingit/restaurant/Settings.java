package com.zingit.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Settings extends AppCompatActivity {
    FirebaseFirestore db;
    SwitchMaterial openSwitch;
    TextView outletName;
    TextView outletDesc;
    TextView outletStatus;
    RecyclerView itemRV;
    ArrayList<Item> itemList;
    LoadingDialog loadingDialog;
    ItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        outletName = findViewById(R.id.outlet_name);
        outletDesc = findViewById(R.id.outlet_desc);
        outletStatus = findViewById(R.id.outlet_status);
        openSwitch = findViewById(R.id.open_switch);
        itemRV = findViewById(R.id.itemRV);
        db = FirebaseFirestore.getInstance();
        itemList = new ArrayList<>();
        loadingDialog = new LoadingDialog(Settings.this, "Fetching restaurant menu");
        loadingDialog.startLoadingDialog();

        adapter = new ItemAdapter(itemList);
        itemRV.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        itemRV.setLayoutManager(new LinearLayoutManager(this));
        itemRV.addItemDecoration(
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
        openSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changeOpenStatus(isChecked);
            }
        });
        setupUI();
        updateItemList(Dataholder.outlet.getId());

    }
    public void updateItemList(String outletID){
        db.collection("item")
                .whereEqualTo("outletID", outletID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Item item = document.toObject(Item.class);
                                itemList.add(item);
                            }
                            adapter.notifyDataSetChanged();
                            loadingDialog.dismissDialog();
                        } else {
                            Log.d("Outlet Data", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    public void changeOpenStatus(boolean status){
        String openStatus = (status)? "OPEN":"CLOSED";
        db.collection("outlet").document(Dataholder.outlet.getId()).update("openStatus", openStatus).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(Settings.this, "Your shop is now "+ openStatus, Toast.LENGTH_SHORT).show();
                Dataholder.outlet.setOpenStatus(openStatus);
            }
        });
    }
    public void setupUI(){
        outletName.setText(Dataholder.outlet.getName());
        outletDesc.setText(Dataholder.outlet.getDescription());
        outletStatus.setText(Dataholder.outlet.getOpenStatus());
        openSwitch.setChecked((Dataholder.outlet.getOpenStatus().equals("OPEN")));
    }

    public void updateItemAvailability(Item item){
        db.collection("item").document(item.getId()).update("availableOrNot", item.isAvailableOrNot()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //updateLocally
                //adapter.notifyDataSetChanged();
                Toast.makeText(Settings.this, "Updated successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Homescreen.class);
        startActivity(intent);
        finish();
    }
}