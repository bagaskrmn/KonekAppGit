package com.example.konekapp.ui.dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.konekapp.R;
import com.example.konekapp.ui.dashboard.Consultation.ConsultationToAhliTaniFragment;
import com.example.konekapp.ui.dashboard.Consultation.ConsultationToMitraFragment;
import com.example.konekapp.ui.dashboard.account.AccountFragment;
import com.example.konekapp.ui.dashboard.home.HomeFragment;
import com.example.konekapp.ui.dashboard.home.waitingreview.WaitingReviewActivity;
import com.example.konekapp.ui.dashboard.notification.NotificationFragment;
import com.example.konekapp.ui.toregistmitra.UserChatToRegistMitraFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private View decorView;
    private DatabaseReference rootRef, usersRef;
    private FirebaseUser currentUser;
    private FirebaseAuth firebaseAuth;
    private String role, currentUserId;

    BottomNavigationView BottomNav;

    HomeFragment homeFragment = new HomeFragment();
    NotificationFragment notificationFragment = new NotificationFragment();
    AccountFragment accountFragment = new AccountFragment();
    UserChatToRegistMitraFragment userChatToRegistMitraFragment= new UserChatToRegistMitraFragment();

    ConsultationToMitraFragment consultationToMitraFragment = new ConsultationToMitraFragment();
    ConsultationToAhliTaniFragment consultationToAhliTaniFragment = new ConsultationToAhliTaniFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility==0) {
                    decorView.setSystemUiVisibility(hideSystemBars());
                }
            }
        });

        rootRef= FirebaseDatabase.getInstance().getReference();
        usersRef= rootRef.child("users");
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        currentUserId = currentUser.getUid();

        BottomNav = findViewById(R.id.bottomNav);

        BottomNav.setItemIconTintList(null);

        usersRef.child(currentUserId).addValueEventListener(listener);


        //replace FrameLayout in main_activity.xml (id : container) with fragment_home.xml
        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();

        BottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    //get id from the nav_menu
                    case R.id.home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
                        return true;
                    case R.id.chat:
                        if (role.equals("1")) {
                            getSupportFragmentManager().beginTransaction().replace(R.id.container, consultationToAhliTaniFragment).commit();
                        }
                        else if (role.equals("2")){
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, consultationToMitraFragment).commit();
                        }
                        else if (role.equals("0")) {
                            getSupportFragmentManager().beginTransaction().replace(R.id.container, userChatToRegistMitraFragment).commit();
                        }
                        else if (role.equals("4")) {
                            Intent i = new Intent(MainActivity.this, WaitingReviewActivity.class);
                            startActivity(i);
                        }
                        return true;
                    case R.id.notification:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, notificationFragment).commit();
                        return true;
                    case R.id.account:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, accountFragment).commit();
                        return true;
                }

                return false;
            }
        });
    }

    ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            role = snapshot.child("role").getValue().toString();
            if (role.equals("3")) {
                Log.d("MainActivity", "Role: "+role);
            BottomNav.getMenu().findItem(R.id.chat).setVisible(false);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
//            Toast.makeText(getApplicationContext(), ""+ error.getMessage(), Toast.LENGTH_SHORT).show();
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
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        usersRef.child(currentUserId).removeEventListener(listener);
    }
}