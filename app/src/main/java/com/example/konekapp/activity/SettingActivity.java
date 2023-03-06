package com.example.konekapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.konekapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingActivity extends AppCompatActivity {

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
        usersRef = rootRef.child("Users");

        //check role
        usersRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                role = snapshot.child("Role").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        SettingBackAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingActivity.super.onBackPressed();
            }
        });

        BtnKelolaAkun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (role.equals("1")) {
                    Intent userProfileIntent = new Intent(SettingActivity.this, ProfileActivity.class);
                    startActivity(userProfileIntent);
                }
                if (role.equals("2")) {
                    Intent mitraProfileIntent = new Intent(SettingActivity.this, MitraProfileActivity.class);
                    startActivity(mitraProfileIntent);
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
                firebaseAuth.signOut();
                //error
                Intent signOutIntent = new Intent(SettingActivity.this, HomeScreenActivity.class);
                signOutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(signOutIntent);

                Toast.makeText(SettingActivity.this, "Logout Berhasil", Toast.LENGTH_SHORT).show();
            }
        });

    }
}