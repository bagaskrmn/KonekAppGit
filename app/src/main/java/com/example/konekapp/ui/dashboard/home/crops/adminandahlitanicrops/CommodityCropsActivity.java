package com.example.konekapp.ui.dashboard.home.crops.adminandahlitanicrops;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.konekapp.R;
import com.example.konekapp.model.CommodityCropsModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class CommodityCropsActivity extends AppCompatActivity {

    private ImageView CommodityCropsBackAction;

    private ProgressDialog pd;
    private View decorView;

    private DatabaseReference commodityRef, rootRef, usersRef;
    private ArrayList<CommodityCropsModel> list;
    private CommodityCropsAdapter adapter;
    private RecyclerView recyclerView;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private String currentUserId, role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commodity_crops);

        //backAction
        CommodityCropsBackAction = findViewById(R.id.commodityCropsBackAction);
        CommodityCropsBackAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommodityCropsActivity.super.onBackPressed();
            }
        });
        //backAction

        //decorView
        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility==0) {
                    decorView.setSystemUiVisibility(hideSystemBars());
                }
            }
        });
        //decorView


        //users
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        currentUserId = currentUser.getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        usersRef = rootRef.child("users");
        commodityRef = rootRef.child("commodity");

        //init ProgressDialog
        pd = new ProgressDialog(this);
        pd.setTitle("Please Wait...");
        pd.setCanceledOnTouchOutside(false);

        pd.setMessage("Memuat data");
        pd.show();

        list = new ArrayList<>();
        recyclerView = findViewById(R.id.commodityCropsRv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CommodityCropsAdapter(this);
        recyclerView.setAdapter(adapter);

        commodityRef.addValueEventListener(listenerData);
    }

    ValueEventListener listenerData = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            list.clear();
            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                CommodityCropsModel commodity = dataSnapshot.getValue(CommodityCropsModel.class);
                commodity.setKey(dataSnapshot.getKey());
                list.add(commodity);
                pd.dismiss();
                Log.d("commodityCropsActvty", Arrays.toString(new ArrayList[]{list}));

            }
            adapter.setListCommodity(list);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    //decorView
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            decorView.setSystemUiVisibility(hideSystemBars());
        }
    }
    private int hideSystemBars() {
        return View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    }
    //decorView
}