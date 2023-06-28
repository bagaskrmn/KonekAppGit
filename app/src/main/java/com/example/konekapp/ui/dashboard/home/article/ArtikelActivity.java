package com.example.konekapp.ui.dashboard.home.article;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.konekapp.R;
import com.example.konekapp.ui.dashboard.home.article.addarticle.AddArtikelActivity;
import com.example.konekapp.model.ArtikelModel;
import com.example.konekapp.ui.dashboard.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ArtikelActivity extends AppCompatActivity {

    private ImageView ArtikelBackAction, BtnAddArtikel;
    private ProgressDialog pd;
    private DatabaseReference articleRef, usersRef, rootRef;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private String role, currentUserId;

    //Keperluan RecyclerView
    private ArrayList<ArtikelModel> list;
    private FullArtikelAdapter adapter;
    private RecyclerView recyclerView;

    private View decorView, SpaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artikel);
        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility==0) {
                    decorView.setSystemUiVisibility(hideSystemBars());
                }
            }
        });

        SpaceView = findViewById(R.id.spaceView);

        BtnAddArtikel = findViewById(R.id.btnAddArtikel);
        BtnAddArtikel.setVisibility(View.GONE);

        ArtikelBackAction = findViewById(R.id.artikelBackAction);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        currentUserId = currentUser.getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        usersRef = rootRef.child("users");

        articleRef = rootRef.child("article");
        list = new ArrayList<>();
        recyclerView = findViewById(R.id.fullArtikelrtikelRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FullArtikelAdapter(this, list);
        recyclerView.setAdapter(adapter);

        //init ProgressDialog
        pd = new ProgressDialog(this);
        pd.setTitle("Please Wait...");
        pd.setCanceledOnTouchOutside(false);

        pd.setMessage("Memuat data");
        pd.show();

        usersRef.child(currentUserId).addValueEventListener(listener);

        BtnAddArtikel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addArtikelIntent = new Intent(ArtikelActivity.this, AddArtikelActivity.class);
                startActivity(addArtikelIntent);
            }
        });

        ArtikelBackAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArtikelActivity.super.onBackPressed();
//                Intent ArtikelBackAction = new Intent(ArtikelActivity.this, MainActivity.class);
//                startActivity(ArtikelBackAction);
            }
        });

        articleRef.addValueEventListener(listener1);

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

    ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            role = snapshot.child("role").getValue().toString();

            if (role.equals("2") || role.equals("3")) {
                BtnAddArtikel.setVisibility(View.VISIBLE);
                SpaceView.setVisibility(View.GONE);
            }
            else {
                BtnAddArtikel.setVisibility(View.GONE);
                SpaceView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    ValueEventListener listener1 = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            pd.dismiss();
            list.clear();
            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                ArtikelModel artikel = dataSnapshot.getValue(ArtikelModel.class);
                artikel.setKey(dataSnapshot.getKey());
                list.add(artikel);
            }
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        usersRef.child(currentUserId).removeEventListener(listener);
        articleRef.removeEventListener(listener1);
    }
}