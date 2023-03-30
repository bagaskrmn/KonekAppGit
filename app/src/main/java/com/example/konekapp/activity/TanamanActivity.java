package com.example.konekapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.konekapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TanamanActivity extends AppCompatActivity {
    private ImageView TanamanBackAction, BtnAddTanaman;
    private ProgressDialog pd;
    private DatabaseReference tanamanRef, rootRef, usersRef;

    private ArrayList<TanamanModel> list;
    private TanamanAdapter adapter;
    private RecyclerView recyclerView;

    private View decorView, SpaceViewTanaman;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private String currentUserId, role;

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

        SpaceViewTanaman = findViewById(R.id.spaceViewTanaman);
        BtnAddTanaman = findViewById(R.id.btnAddTanaman);
        rootRef = FirebaseDatabase.getInstance().getReference();
        tanamanRef = rootRef.child("Tanaman");
        list = new ArrayList<>();
        recyclerView = findViewById(R.id.tanamanRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TanamanAdapter(this, list);
        recyclerView.setAdapter(adapter);

        //users
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        currentUserId = currentUser.getUid();
        usersRef = rootRef.child("Users");


        //init ProgressDialog
        pd = new ProgressDialog(this);
        pd.setTitle("Please Wait...");
        pd.setCanceledOnTouchOutside(false);

        pd.setMessage("Memuat data");
        pd.show();

        usersRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                role = snapshot.child("Role").getValue().toString();

                if (role.equals("3") || role.equals("4")) {
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


        tanamanRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pd.dismiss();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    TanamanModel tanaman = dataSnapshot.getValue(TanamanModel.class);
                    tanaman.setKey(dataSnapshot.getKey());
                    list.add(tanaman);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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