package com.example.konekapp.ui.dashboard.manageuser.managemitra;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.konekapp.R;
import com.example.konekapp.ui.dashboard.manageuser.managemitra.listmitra.ListMitraActivity;
import com.example.konekapp.ui.dashboard.manageuser.managemitra.requestmitra.ManageMitra;

public class ListAndRequestMitraActivity extends AppCompatActivity {

    private View decorView;
    private ImageView ListAndReqMitraBackAction;
    private ConstraintLayout BtnReqMitra, BtnListMitra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_and_request_mitra);

        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility==0) {
                    decorView.setSystemUiVisibility(hideSystemBars());
                }
            }
        });

        ListAndReqMitraBackAction = findViewById(R.id.listAndReqMitraBackAction);
        BtnReqMitra = findViewById(R.id.btnReqMitra);
        BtnListMitra = findViewById(R.id.btnListMitra);

        ListAndReqMitraBackAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListAndRequestMitraActivity.super.onBackPressed();
            }
        });

        BtnReqMitra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ListAndRequestMitraActivity.this, ManageMitra.class);
                startActivity(i);
            }
        });

        BtnListMitra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ListAndRequestMitraActivity.this, ListMitraActivity.class);
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