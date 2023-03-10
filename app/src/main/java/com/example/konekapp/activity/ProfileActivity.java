package com.example.konekapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

    private TextView ProfName, ProfPhoneNumber, ProfAddress, ProfDetailAddress;
    private ImageView ProfBackAction;
    private Button BtnUpdateProfile;
    private CircleImageView ProfImage;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference rootRef, usersRef;
    private String currentUserId, phoneNumber, removedPhoneNumber;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ProfBackAction = findViewById(R.id.profBackAction);
        ProfImage = findViewById(R.id.profImage);
        ProfName = findViewById(R.id.profName);
        ProfAddress = findViewById(R.id.profAddress);
        ProfDetailAddress = findViewById(R.id.profDetailAddress);
        ProfPhoneNumber = findViewById(R.id.profPhoneNumber);
        BtnUpdateProfile = findViewById(R.id.btnUpdateProfile);

        //init progress dialog
        pd = new ProgressDialog(this);
        pd.setTitle("Please wait...");
        pd.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        currentUserId = currentUser.getUid();
        phoneNumber = currentUser.getPhoneNumber();
        removedPhoneNumber = phoneNumber.substring(3);

        rootRef = FirebaseDatabase.getInstance().getReference();
        usersRef = rootRef.child("Users");

        pd.setMessage("Memuat data anda");
        pd.show();

        ProfBackAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileActivity.super.onBackPressed();
            }
        });

        BtnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent UpdateProfileIntent = new Intent(ProfileActivity.this, UpdateProfileActivity.class);
                startActivity(UpdateProfileIntent);
            }
        });


        usersRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                String retrieveName = snapshot.child("Nama").getValue().toString();
                String retrieveAddress = snapshot.child("Domisili").getValue().toString();
                String retrieveDetailAddress = snapshot.child("Alamat Lengkap").getValue().toString();

                //profile image
                String retrieveImage = snapshot.child("Image").getValue().toString();

                ProfName.setText(retrieveName);
                ProfPhoneNumber.setText(removedPhoneNumber);
                ProfAddress.setText(retrieveAddress);
                ProfDetailAddress.setText(retrieveDetailAddress);
                //profile image
                Picasso.get().load(retrieveImage).into(ProfImage);

                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pd.dismiss();
                Toast.makeText(ProfileActivity.this, ""+ error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}