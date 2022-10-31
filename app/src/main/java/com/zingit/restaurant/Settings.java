package com.zingit.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
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
    EditText searchItems;
    ImageView backBtn;
    ImageView earnings,home,settings;
    ImageView logout;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //outletName = findViewById(R.id.outlet_name);
        //outletDesc = findViewById(R.id.outlet_desc);
        //outletStatus = findViewById(R.id.outlet_status);
        //openSwitch = findViewById(R.id.open_switch);
        //itemRV = findViewById(R.id.itemRV);
        db = FirebaseFirestore.getInstance();
        itemList = new ArrayList<>();
        searchItems = findViewById(R.id.searchMenuItems);
        loadingDialog = new LoadingDialog(Settings.this, "Fetching restaurant menu");
        loadingDialog.startLoadingDialog();
        backBtn = findViewById(R.id.backBtn);
        logout = findViewById(R.id.logout);
        mAuth = FirebaseAuth.getInstance();

        earnings = findViewById(R.id.earning);
        home = findViewById(R.id.home);
        settings = findViewById(R.id.settings);



        itemRV = findViewById(R.id.itemsRV);
        adapter = new ItemAdapter(itemList);
        itemRV.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        itemRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));

        searchItems.addTextChangedListener(new TextWatcher() {
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
            private void filter(String s)
            {
                ArrayList<Item> items = new ArrayList<>();
                for(Item item : itemList)
                {
                    String itemName = item.getName();
                    itemName = itemName.toLowerCase();
                    s = s.toLowerCase();
                    if(itemName.contains(s))
                    {
                        items.add(item);
                    }
                }
                adapter.filterList(items);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
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
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Homescreen_latest.class);
                startActivity(intent);
            }
        });
        earnings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),EarningScreen.class);
                startActivity(intent);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Toast.makeText(Settings.this, "Logging Out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);

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

    public void setupUI(){

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
        Intent intent = new Intent(this, Homescreen_latest.class);
        startActivity(intent);
        finish();
    }
}