package com.example.konekapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.konekapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private TextView ProfName, ProfPhoneNumber, ProfAddress;
    private Button BtnProfToMain;
    private CircleImageView ProfImage;

    //variabel
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference rootRef, usersRef;
    private String currentUserId, phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ProfImage = findViewById(R.id.profImage);
        ProfName = findViewById(R.id.profName);
        ProfAddress = findViewById(R.id.profAddress);
        ProfPhoneNumber = findViewById(R.id.profPhoneNumber);
        BtnProfToMain = findViewById(R.id.btnProfToMain);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        currentUserId = currentUser.getUid();
        phoneNumber = currentUser.getPhoneNumber();

        rootRef = FirebaseDatabase.getInstance().getReference();
        usersRef = rootRef.child("Users");

        BtnProfToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ProfToMain = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(ProfToMain);
            }
        });

        usersRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String retrieveName = snapshot.child("Nama").getValue().toString();
                String retrieveAddress = snapshot.child("Alamat").getValue().toString();
                //profile image
                String retrieveImage = snapshot.child("Image").getValue().toString();

                ProfName.setText("Nama : " +retrieveName);
                ProfPhoneNumber.setText("Nomor HP : " + phoneNumber);
                ProfAddress.setText("Alamat : "+retrieveAddress);

                //profile image
                Picasso.get().load(retrieveImage).into(ProfImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, ""+ error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}