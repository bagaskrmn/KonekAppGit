package com.example.konekapp.ui.dashboard.home.crops.adminandahlitanicrops;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
import java.util.List;

public class CommodityCropsActivity extends AppCompatActivity {

    private ImageView CommodityCropsBackAction;

    private ProgressDialog pd;
    private View decorView;

    private DatabaseReference commodityRef, rootRef, usersRef;
    private List<CommodityCropsModel> list;
    private CommodityCropsAdapter adapter;
    private RecyclerView recyclerView;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private String currentUserId, role;

    private EditText SearchCommodity;
    private TextView CommodityNoData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commodity_crops);

        CommodityNoData = findViewById(R.id.commodityNoData);
        SearchCommodity = findViewById(R.id.searchCommodity);

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

//        pd.setMessage("Memuat data");
//        pd.show();

        list = new ArrayList<>();
        recyclerView = findViewById(R.id.commodityCropsRv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CommodityCropsAdapter(this);
        recyclerView.setAdapter(adapter);

        commodityRef.addValueEventListener(listenerData);

        SearchCommodity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterListCommodity(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    ValueEventListener listenerData = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            list.clear();
            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                CommodityCropsModel commodity = dataSnapshot.getValue(CommodityCropsModel.class);
                commodity.setKey(dataSnapshot.getKey());
                list.add(commodity);
//                pd.dismiss();
            }

            if (list.size() > 0) {
                CommodityNoData.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            } else {
                CommodityNoData.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
            adapter.setListCommodity(list);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    public void filterListCommodity(String text){
        List<CommodityCropsModel> filteredListCommodity = new ArrayList<>();
        filteredListCommodity.clear();
        if (text.isEmpty()) {
            filteredListCommodity.addAll(list);
        } else {
            for (CommodityCropsModel cropsModel : list) {
                if (cropsModel.name.toLowerCase().contains(text.toLowerCase())) {
                    filteredListCommodity.add(cropsModel);
                }
            }
        }

        if (filteredListCommodity.size() > 0) {
            CommodityNoData.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            CommodityNoData.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

        adapter.setListCommodity(filteredListCommodity);
    }

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