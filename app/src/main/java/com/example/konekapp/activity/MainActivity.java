package com.example.konekapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.konekapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private Button BtnLogout, BtnMainToProfile, BtnRegisterToMitra;
    private FirebaseAuth firebaseAuth;
    private String currentUserId;
    private DatabaseReference rootRef, usersRef;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BtnLogout = findViewById(R.id.btnLogout);
        BtnMainToProfile = findViewById(R.id.btnMainToProfile);
        BtnRegisterToMitra = findViewById(R.id.btnRegisterToMitra);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        currentUserId = currentUser.getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        usersRef = rootRef.child("Users");

//        detect if user is mitra or not
        usersRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String role = snapshot.child("Role").getValue().toString();

                //if user has registered as mitra
                if (role.equals("2")) {
                    //hide registerMitraButton
                    BtnRegisterToMitra.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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