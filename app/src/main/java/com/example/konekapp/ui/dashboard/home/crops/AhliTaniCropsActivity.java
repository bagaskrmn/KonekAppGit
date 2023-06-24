package com.example.konekapp.ui.dashboard.home.crops;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.konekapp.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AhliTaniCropsActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_ahli_tani_crops);

        BackAction = findViewById(R.id.backAction);
        BackAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AhliTaniCropsActivity.super.onBackPressed();
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