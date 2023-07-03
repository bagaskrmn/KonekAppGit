package com.example.konekapp.ui.dashboard.account.profile;


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
import com.example.konekapp.ui.dashboard.account.AccountFragment;
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

    private TextView ProfName, ProfPhoneNumber, ProfAddress, ProfDetailAddress, ProfNameTop, ProfDateJoined;
    private ImageView ProfBackAction;
    private Button BtnUpdateProfile;
    private CircleImageView ProfImage;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference rootRef, usersRef;
    private String currentUserId, phoneNumber, removedPhoneNumber;
    private ProgressDialog pd;

    private View decorView;

    AccountFragment accountFragment= new AccountFragment();

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
        ProfNameTop = findViewById(R.id.profNameTop);
        ProfDateJoined = findViewById(R.id.profDateJoined);

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
        usersRef = rootRef.child("users");

        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility==0) {
                    decorView.setSystemUiVisibility(hideSystemBars());
                }
            }
        });

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
                Intent UpdateProfileIntent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(UpdateProfileIntent);
            }
        });


        usersRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                String retrieveName = snapshot.child("name").getValue().toString();
                String retrieveAddress = snapshot.child("domicile").getValue().toString();
                String retrieveDetailAddress = snapshot.child("fullAddress").getValue().toString();
                String retrieveDateJoined = snapshot.child("dateJoined").getValue().toString();

                //profile image
                String retrieveImage = snapshot.child("image").getValue().toString();

                ProfName.setText(retrieveName);
                ProfNameTop.setText(retrieveName);
                ProfPhoneNumber.setText(removedPhoneNumber);
                ProfAddress.setText(retrieveAddress);
                ProfDetailAddress.setText(retrieveDetailAddress);
                //profile image
                Picasso.get().load(retrieveImage).into(ProfImage);
                ProfDateJoined.setText("Bergabung sejak "+retrieveDateJoined.substring(0,10));

                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pd.dismiss();
                Toast.makeText(ProfileActivity.this, ""+ error.getMessage(), Toast.LENGTH_SHORT).show();
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