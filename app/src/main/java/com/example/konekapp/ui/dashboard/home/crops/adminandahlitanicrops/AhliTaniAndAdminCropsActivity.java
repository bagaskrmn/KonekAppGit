package com.example.konekapp.ui.dashboard.home.crops.adminandahlitanicrops;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.konekapp.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AhliTaniAndAdminCropsActivity extends AppCompatActivity {

    private View decorView;
    private ImageView BackAction;
    private DatabaseReference rootRef, cropsRef;
    private String nameCommodity;
    private TextView CommodityAhliTaniCrops;

    TabLayout tlCropsAhliTani;
    ViewPager vpCropsAhliTani;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ahli_tani_and_admin_crops);

        BackAction = findViewById(R.id.backAction);
        BackAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AhliTaniAndAdminCropsActivity.super.onBackPressed();
            }
        });

        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility==0) {
                    decorView.setSystemUiVisibility(hideSystemBars());
                }
            }
        });

        CommodityAhliTaniCrops = findViewById(R.id.commodityAhliTaniCrops);
        tlCropsAhliTani = findViewById(R.id.tabLayoutAhliTaniCrops);
        vpCropsAhliTani = findViewById(R.id.viewPagerAhliTaniCrops);

        rootRef = FirebaseDatabase.getInstance().getReference();
        cropsRef = rootRef.child("crops");

        //retrieve nameCommodity from CommodityCropsAdapter
        Intent intent = getIntent();
        nameCommodity = intent.getStringExtra("Commodity");
        CommodityAhliTaniCrops.setText(nameCommodity);


        tlCropsAhliTani.addTab(tlCropsAhliTani.newTab().setText("Belum Selesai"));
        tlCropsAhliTani.addTab(tlCropsAhliTani.newTab().setText("Selesai"));
        //gravity fill?
        tlCropsAhliTani.setTabGravity(TabLayout.GRAVITY_FILL);

        final AhliTaniAndAdminCropsAdapter adapter = new AhliTaniAndAdminCropsAdapter(getSupportFragmentManager(),this, tlCropsAhliTani.getTabCount());
        vpCropsAhliTani.setAdapter(adapter);

        vpCropsAhliTani.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tlCropsAhliTani));

        tlCropsAhliTani.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vpCropsAhliTani.setCurrentItem(tab.getPosition());
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