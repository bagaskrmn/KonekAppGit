package com.example.konekapp.ui.dashboard.manageuser.manageAhliTani;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.konekapp.R;
import com.example.konekapp.ui.dashboard.manageuser.manageAhliTani.listAhliTani.ListAhliTaniActivity;
import com.example.konekapp.ui.dashboard.manageuser.manageAhliTani.upgradeAhliTani.UpgradeAhliTaniActivity;

public class ListAndUpgradeAhliTaniActivity extends AppCompatActivity {

    private View decorView;
    private ImageView ListAndUpgradeAhliTaniBackAction;
    private ConstraintLayout BtnUpgradeAhliTani, BtnListAhliTani;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_and_upgrade_ahli_tani);

        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility==0) {
                    decorView.setSystemUiVisibility(hideSystemBars());
                }
            }
        });

        ListAndUpgradeAhliTaniBackAction = findViewById(R.id.listAndUpgradeAhliTaniBackAction);
        BtnListAhliTani = findViewById(R.id.btnListAhliTani);
        BtnUpgradeAhliTani = findViewById(R.id.btnUpgradeAhliTani);

        ListAndUpgradeAhliTaniBackAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListAndUpgradeAhliTaniActivity.super.onBackPressed();
            }
        });

        BtnListAhliTani.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ListAndUpgradeAhliTaniActivity.this, ListAhliTaniActivity.class);
                startActivity(i);
            }
        });

        BtnUpgradeAhliTani.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ListAndUpgradeAhliTaniActivity.this, UpgradeAhliTaniActivity.class);
                startActivity(i);
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
        return
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    }
}