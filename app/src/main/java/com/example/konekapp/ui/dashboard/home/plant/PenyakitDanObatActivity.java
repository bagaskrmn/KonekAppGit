package com.example.konekapp.ui.dashboard.home.plant;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.konekapp.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PenyakitDanObatActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;

    private ImageView PenyakitDanObatBackAction;
    private TextView JenisTanaman;
    private String plantId, nameTanaman;
    private DatabaseReference plantRef, rootRef;
    private View decorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penyakit_dan_obat);
        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility==0) {
                    decorView.setSystemUiVisibility(hideSystemBars());
                }
            }
        });

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        PenyakitDanObatBackAction = findViewById(R.id.penyakitDanObatBackAction);
        JenisTanaman = findViewById(R.id.jenisTanaman);

        rootRef = FirebaseDatabase.getInstance().getReference();
        plantRef = rootRef.child("plant");

        PenyakitDanObatBackAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PenyakitDanObatActivity.super.onBackPressed();
            }
        });

        //retrieve Key for tanaman from Tanaman Adapter
        Intent intent = getIntent();
        plantId = intent.getStringExtra("Key");

        //getPlantData
        plantRef.child(plantId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nameTanaman = snapshot.child("name").getValue().toString();
                JenisTanaman.setText(nameTanaman);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        tabLayout.addTab(tabLayout.newTab().setText("Penyakit"));
        tabLayout.addTab(tabLayout.newTab().setText("Obat"));
        //gravity fill?
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final PenyakitDanObatAdapter adapter = new PenyakitDanObatAdapter(getSupportFragmentManager(),this, tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

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