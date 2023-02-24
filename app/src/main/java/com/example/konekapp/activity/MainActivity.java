package com.example.konekapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.konekapp.R;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private Button BtnLogout, BtnMainToProfile, BtnRegisterToMitra;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BtnLogout = findViewById(R.id.btnLogout);
        BtnMainToProfile = findViewById(R.id.btnMainToProfile);
        BtnRegisterToMitra = findViewById(R.id.btnRegisterToMitra);

        firebaseAuth = FirebaseAuth.getInstance();

        BtnRegisterToMitra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent RegToMitra = new Intent(MainActivity.this, RegisterMitraActivity.class);
                startActivity(RegToMitra);
            }
        });

        BtnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                Intent signOutIntent = new Intent(MainActivity.this, HomeScreenActivity.class);
                signOutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(signOutIntent);

                Toast.makeText(MainActivity.this, "Logout berhasil", Toast.LENGTH_SHORT).show();
            }
        });

        BtnMainToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent MainToProfileIntent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(MainToProfileIntent);
            }
        });
    }
}