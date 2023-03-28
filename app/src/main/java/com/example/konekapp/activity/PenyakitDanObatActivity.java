package com.example.konekapp.activity;

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
    private String TanamanKey, nameTanaman;
    private DatabaseReference tanamanRef, rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penyakit_dan_obat);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        PenyakitDanObatBackAction = findViewById(R.id.penyakitDanObatBackAction);
        JenisTanaman = findViewById(R.id.jenisTanaman);

        rootRef = FirebaseDatabase.getInstance().getReference();
        tanamanRef = rootRef.child("Tanaman");

        PenyakitDanObatBackAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PenyakitDanObatActivity.super.onBackPressed();
            }
        });

        //retrieve Key for tanaman from Tanaman Adapter
        Intent intent = getIntent();
        TanamanKey = intent.getStringExtra("Key");

        tanamanRef.child(TanamanKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nameTanaman = snapshot.child("Name").getValue().toString();
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
}