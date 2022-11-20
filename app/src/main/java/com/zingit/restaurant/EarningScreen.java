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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class EarningScreen extends AppCompatActivity {

    FirebaseFirestore db;
    TextView outletName;
    TextView outletDesc;
    TextView outletStatus;
    RecyclerView earningRV;
    ArrayList<Earnings> earningList;
    LoadingDialog loadingDialog;
    EarningAdapter adapter;
    ImageView backBtn;
    ImageView home,earning,settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earning_screen);

        earningRV = findViewById(R.id.earningRV);
        earningRV.setNestedScrollingEnabled(false);
        db = FirebaseFirestore.getInstance();
        earningList = new ArrayList<>();
        backBtn = findViewById(R.id.backBtn);
        earning = findViewById(R.id.earning);
        home = findViewById(R.id.home);
        settings = findViewById(R.id.settings);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Homescreen_latest.class);
                startActivity(intent);
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


        adapter = new EarningAdapter(earningList);
        earningRV.setAdapter(adapter);
        earningRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));


        /*earningRV.setLayoutManager(new LinearLayoutManager(this));
        earningRV.addItemDecoration(
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
        setupUI();*/
        updateEarningList(Dataholder.outlet.getId());
    }

    private void updateEarningList(String outletID) {
        loadingDialog = new LoadingDialog(EarningScreen.this, "Fetching your earnings");
        loadingDialog.startLoadingDialog();
        db.collection("earnings")
                .whereEqualTo("outletID", outletID).orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Earnings earning = document.toObject(Earnings.class);
                                earningList.add(earning);
                                Log.e("Earnings", earning.getTotalAmount()+"");
                            }
                            adapter.notifyDataSetChanged();
                            loadingDialog.dismissDialog();
                        } else {
                            Log.d("Outlet Data", "Error getting documents: ", task.getException());
                        }
                    }
                });
       }

    public void OrderHistory(Earnings earning)
    {
        Intent intent = new Intent(getApplicationContext(), OrderHistory.class);
        startActivity(intent);
    }

    public void setupUI(){
        outletName.setText(Dataholder.outlet.getName());
        outletDesc.setText(Dataholder.outlet.getDescription());
        outletStatus.setText(Dataholder.outlet.getOpenStatus());
        home = findViewById(R.id.home);
        settings = findViewById(R.id.settings);
        earning = findViewById(R.id.earning);
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Homescreen_latest.class);
        startActivity(intent);
        finish();
    }
}