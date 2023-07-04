package com.example.konekapp.ui.dashboard.home.plant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.konekapp.R;
import com.example.konekapp.ui.dashboard.home.plant.addplant.AddTanamanActivity;
import com.example.konekapp.model.TanamanModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TanamanActivity extends AppCompatActivity {
    private ImageView TanamanBackAction, BtnAddTanaman;
    private ProgressDialog pd;
    private DatabaseReference plantRef, rootRef, usersRef;
    private EditText SearchTanaman;

    private List<TanamanModel> list;
    private TanamanAdapter adapter;
    private RecyclerView recyclerView;

    private View decorView, SpaceViewTanaman;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private String currentUserId, role;
    private TextView TanamanNoData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tanaman);

        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility==0) {
                    decorView.setSystemUiVisibility(hideSystemBars());
                }
            }
        });

        TanamanBackAction = findViewById(R.id.tanamanBackAction);
        SearchTanaman = findViewById(R.id.searchTanaman);
        SpaceViewTanaman = findViewById(R.id.spaceViewTanaman);
        BtnAddTanaman = findViewById(R.id.btnAddTanaman);
        TanamanNoData = findViewById(R.id.tanamanNoData);
        rootRef = FirebaseDatabase.getInstance().getReference();
        plantRef = rootRef.child("plant");
        list = new ArrayList<>();
        recyclerView = findViewById(R.id.tanamanRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TanamanAdapter(this);
        recyclerView.setAdapter(adapter);

        //users
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        currentUserId = currentUser.getUid();
        usersRef = rootRef.child("users");

        //init ProgressDialog
        pd = new ProgressDialog(this);
        pd.setTitle("Please Wait...");
        pd.setCanceledOnTouchOutside(false);

        pd.setMessage("Memuat data");
        pd.show();

        //getAllData
        plantRef.addValueEventListener(allPlantListener);

        //getRole
        usersRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                role = snapshot.child("role").getValue().toString();

                if (role.equals("2") || role.equals("3")) {
                    BtnAddTanaman.setVisibility(View.VISIBLE);
                    SpaceViewTanaman.setVisibility(View.GONE);
                }
                else {
                    BtnAddTanaman.setVisibility(View.GONE);
                    SpaceViewTanaman.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        SearchTanaman.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        BtnAddTanaman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addTanamanIntent = new Intent(TanamanActivity.this, AddTanamanActivity.class);
                startActivity(addTanamanIntent);
            }
        });

        TanamanBackAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TanamanActivity.super.onBackPressed();
            }
        });
    }

    //getAllData
    ValueEventListener allPlantListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            pd.dismiss();
            list.clear();
            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                TanamanModel tanaman = dataSnapshot.getValue(TanamanModel.class);
                tanaman.setKey(dataSnapshot.getKey());
                list.add(tanaman);
            }


            if (list.size() > 0) {
                TanamanNoData.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            } else {
                TanamanNoData.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
            adapter.setListTanaman(list);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    public void filterList(String text){
        List<TanamanModel> filteredListTanaman = new ArrayList<>();
        filteredListTanaman.clear();
        if (text.isEmpty()) {
            filteredListTanaman.addAll(list);
        } else {
            for (TanamanModel tanamanModel : list) {
                if (tanamanModel.name.toLowerCase().contains(text.toLowerCase())) {
                    filteredListTanaman.add(tanamanModel);
                }
            }
        }

        if (filteredListTanaman.size() > 0) {
            TanamanNoData.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            TanamanNoData.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

        adapter.setListTanaman(filteredListTanaman);

    }



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
}