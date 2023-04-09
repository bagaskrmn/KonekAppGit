package com.example.konekapp.ui.dashboard.account.setting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.konekapp.R;
import com.example.konekapp.ui.dashboard.account.setting.accountsetting.MitraProfileActivity;
import com.example.konekapp.ui.dashboard.account.setting.accountsetting.ProfileActivity;
import com.example.konekapp.ui.login.LoginPhoneActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingActivity extends AppCompatActivity {

    private View decorView;

    private ImageView SettingBackAction;
    private ConstraintLayout BtnKelolaAkun, BtnKelolaNotifikasi, BtnFaq, BtnSnk;
    private LinearLayout BtnLogout;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference rootRef, usersRef;
    private String currentUserId, role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility==0) {
                    decorView.setSystemUiVisibility(hideSystemBars());
                }
            }
        });

        SettingBackAction = findViewById(R.id.settingBackAction);
        BtnKelolaAkun = findViewById(R.id.btnKelolaAkun);
        BtnKelolaNotifikasi = findViewById(R.id.btnKelolaNotifikasi);
        BtnFaq = findViewById(R.id.btnFaq);
        BtnSnk = findViewById(R.id.btnSnk);
        BtnLogout = findViewById(R.id.btnLogout);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        currentUserId = currentUser.getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        usersRef = rootRef.child("users");

        //check role
        usersRef.child(currentUserId).addValueEventListener(listener);

        SettingBackAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingActivity.super.onBackPressed();
            }
        });

        BtnKelolaAkun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (role!=null) {
                    if (role.equals("1")) {
                        Intent mitraProfileIntent = new Intent(SettingActivity.this, MitraProfileActivity.class);
                        startActivity(mitraProfileIntent);
                    }

                    else {
                        Intent userProfileIntent = new Intent(SettingActivity.this, ProfileActivity.class);
                        startActivity(userProfileIntent);
                    }
                }
            }
        });

        BtnKelolaNotifikasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add Kelola Notifikasi here
            }
        });

        BtnFaq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add Faq Activity here
            }
        });

        BtnSnk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add Snk activity here
            }
        });

        BtnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usersRef.child(currentUserId).removeEventListener(listener);
                firebaseAuth.signOut();

                Intent signOutIntent = new Intent(SettingActivity.this, LoginPhoneActivity.class);
                signOutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(signOutIntent);
                Toast.makeText(SettingActivity.this, "Logout Berhasil", Toast.LENGTH_SHORT).show();
            }
        });

    }
    //chek user role
    ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            role = snapshot.child("role").getValue().toString();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

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