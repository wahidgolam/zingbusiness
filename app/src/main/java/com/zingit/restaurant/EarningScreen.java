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
    ArrayList<Earning> earningList;
    LoadingDialog loadingDialog;
    EarningAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earning_screen);
        outletName = findViewById(R.id.outlet_name);
        outletDesc = findViewById(R.id.outlet_desc);
        outletStatus = findViewById(R.id.outlet_status);
        earningRV = findViewById(R.id.earningRV);
        db = FirebaseFirestore.getInstance();
        earningList = new ArrayList<>();


        adapter = new EarningAdapter(earningList);
        earningRV.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        earningRV.setLayoutManager(new LinearLayoutManager(this));
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
        setupUI();
        updateEarningList(Dataholder.outlet.getId());
    }

    private void updateEarningList(String outletID) {
        loadingDialog = new LoadingDialog(EarningScreen.this, "Fetching your earnings");
        loadingDialog.startLoadingDialog();
        db.collection("earning")
                .whereEqualTo("outletID", outletID).orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Earning earning = document.toObject(Earning.class);
                                earningList.add(earning);
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
        outletName.setText(Dataholder.outlet.getName());
        outletDesc.setText(Dataholder.outlet.getDescription());
        outletStatus.setText(Dataholder.outlet.getOpenStatus());
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Homescreen.class);
        startActivity(intent);
        finish();
    }
}