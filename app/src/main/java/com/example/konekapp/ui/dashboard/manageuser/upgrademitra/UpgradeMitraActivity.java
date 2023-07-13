package com.example.konekapp.ui.dashboard.manageuser.upgrademitra;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.konekapp.R;
import com.example.konekapp.model.UserModel;
import com.example.konekapp.ui.dashboard.manageuser.managemitra.ManageMitraAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UpgradeMitraActivity extends AppCompatActivity {

    private ArrayList<UserModel> list;
    private RecyclerView UpgradeMitraRecyclerView;
    private ImageView UpgradeMitraBackAction;
    private UpgradeMitraAdapter adapter;
    private View decorView;
    private ProgressDialog pd;
    private DatabaseReference usersRef, rootRef;
    private String currentUserId, phoneNumber;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade_mitra);

        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility==0) {
                    decorView.setSystemUiVisibility(hideSystemBars());
                }
            }
        });

        list = new ArrayList<>();
        UpgradeMitraRecyclerView = findViewById(R.id.upgradeMitraRecyclerView);
        UpgradeMitraRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UpgradeMitraAdapter(this, list);
        UpgradeMitraRecyclerView.setAdapter(adapter);

        rootRef = FirebaseDatabase.getInstance().getReference();
        usersRef = rootRef.child("users");
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        currentUserId = currentUser.getUid();

        //init ProgressDialog
        pd = new ProgressDialog(this);
        pd.setTitle("Please Wait...");
        pd.setCanceledOnTouchOutside(false);

        pd.setMessage("Memuat data");
        pd.show();

        UpgradeMitraBackAction = findViewById(R.id.upgradeMitraBackAction);
        UpgradeMitraBackAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpgradeMitraActivity.super.onBackPressed();
            }
        });
        
        listenUserFromDatabase();

    }

    private void listenUserFromDatabase() {
        usersRef.addValueEventListener(userListener);
    }

    ValueEventListener userListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            pd.dismiss();
            list.clear();
            for (DataSnapshot ds : snapshot.getChildren()) {
                UserModel user = ds.getValue(UserModel.class);
                user.setUserId(ds.getKey());
                try {
                    if (!user.getUserId().equals(currentUserId) && user.getRole().equals("0")) {
                        list.add(user);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            UpgradeMitraRecyclerView.getAdapter().notifyDataSetChanged();
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
        return
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    }
}