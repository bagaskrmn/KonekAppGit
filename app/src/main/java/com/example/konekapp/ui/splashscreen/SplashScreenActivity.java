package com.example.konekapp.ui.splashscreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.konekapp.R;
import com.example.konekapp.ui.login.completeprofile.CompleteProfileActivity;
import com.example.konekapp.ui.login.LoginPhoneActivity;
import com.example.konekapp.ui.dashboard.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashScreenActivity extends AppCompatActivity {

    private View decorView;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference rootRef, usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility==0) {
                    decorView.setSystemUiVisibility(hideSystemBars());
                }
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();

        //database ref
        rootRef = FirebaseDatabase.getInstance().getReference();
        usersRef = rootRef.child("users");

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser!=null) {

                    String currentUserId = currentUser.getUid();
                    usersRef.child(currentUserId).addValueEventListener(checkUser);
                }
                else {
                    Intent toLogin = new Intent(SplashScreenActivity.this, LoginPhoneActivity.class);
                    toLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(toLogin);
                }
            }
        },1500);
    }

    ValueEventListener checkUser = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.hasChild("name")) {
                Intent registeredUserIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
                usersRef.removeEventListener(this);
                registeredUserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(registeredUserIntent);
            } else {

                Intent newUserIntent = new Intent(SplashScreenActivity.this, CompleteProfileActivity.class);
                usersRef.removeEventListener(this);
                newUserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(newUserIntent);
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError error) {
//            Toast.makeText(SplashScreenActivity.this, "Error :" + error.getMessage(), Toast.LENGTH_SHORT).show();
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
        return
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    }
}