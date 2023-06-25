package com.example.konekapp.ui.dashboard.home.crops.mitracrops;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.konekapp.R;

public class PreMitraCropsActivity extends AppCompatActivity {

    private Button BtnNextPreMitraCrops;
    private View decorView, PreMitraCropsBackAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_mitra_crops);

        BtnNextPreMitraCrops = findViewById(R.id.btnNextPreMitraCrops);
        PreMitraCropsBackAction = findViewById(R.id.preMitraCropsBackAction);

        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility==0) {
                    decorView.setSystemUiVisibility(hideSystemBars());
                }
            }
        });

        BtnNextPreMitraCrops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PreMitraCropsActivity.this, MitraCropsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        PreMitraCropsBackAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreMitraCropsActivity.super.onBackPressed();
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