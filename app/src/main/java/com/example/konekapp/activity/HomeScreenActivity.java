package com.example.konekapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.konekapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeScreenActivity extends AppCompatActivity {

    private Button BtnLoginWithPhoneNumber;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference rootRef, usersRef;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        //database ref
        rootRef = FirebaseDatabase.getInstance().getReference();
        usersRef = rootRef.child("Users");

        //Progress Dialog
        pd = new ProgressDialog(this);
        pd.setTitle("Please wait...");
        pd.setCanceledOnTouchOutside(false);

        BtnLoginWithPhoneNumber = findViewById(R.id.btnLoginWithPhoneNumber);

        BtnLoginWithPhoneNumber.setOnClickListener(v -> {
            Intent HomeToLoginPhoneIntent = new Intent(HomeScreenActivity.this, LoginPhoneActivity.class);
            startActivity(HomeToLoginPhoneIntent);
        });
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
//        if (currentUser!=null) {
//            pd.setMessage("Memeriksa Akun");
//            pd.show();
//
//            String currentUserId = currentUser.getUid();
//            usersRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    if (snapshot.hasChild("Nama")) {
//                        pd.dismiss();
//                        Intent registeredUserIntent = new Intent(HomeScreenActivity.this, MainActivity.class);
//                        usersRef.removeEventListener(this);
//                        registeredUserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(registeredUserIntent);
//                    } else {
//                        pd.dismiss();
//                        Intent newUserIntent = new Intent(HomeScreenActivity.this, CompleteProfileActivity.class);
//                        usersRef.removeEventListener(this);
//                        newUserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(newUserIntent);
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });
//
//        }
//    }
}