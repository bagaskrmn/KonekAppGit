package com.example.konekapp.ui.dashboard.manageuser.upgrademitra;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.konekapp.R;
import com.example.konekapp.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UpgradeMitraActivity extends AppCompatActivity {

    private List<UserModel> list;
    private List<UserModel> filteredListUser;
    private RecyclerView UpgradeMitraRecyclerView;
    private ImageView UpgradeMitraBackAction;
    private UpgradeMitraAdapter adapter;
    private View decorView;
    private ProgressDialog pd;
    private DatabaseReference usersRef, rootRef;
    private String currentUserId, phoneNumber;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private TextView UserNoData;
    private EditText SearchUser;

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
        adapter = new UpgradeMitraAdapter(this);
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

        UserNoData = findViewById(R.id.userNoData);
        SearchUser = findViewById(R.id.searchUser);
        SearchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterListUser(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

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
                    if (!user.getUserId().equals(currentUserId) && (user.getRole().equals("0") || user.getRole().equals("4"))) {
                        list.add(user);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (list.size() > 0) {
                UserNoData.setVisibility(View.GONE);
                UpgradeMitraRecyclerView.setVisibility(View.VISIBLE);
            } else {
                UserNoData.setVisibility(View.VISIBLE);
                UpgradeMitraRecyclerView.setVisibility(View.GONE);
            }

            if (filteredListUser != null) {
                adapter.setListUser(filteredListUser);
            } else {
                adapter.setListUser(list);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    private void filterListUser(String text) {
        filteredListUser = new ArrayList<>();
        filteredListUser.clear();
        if (text.isEmpty()) {
            filteredListUser.addAll(list);
        } else {
            for (UserModel userModel : list) {
                if (userModel.name.toLowerCase().contains(text.toLowerCase())) {
                    filteredListUser.add(userModel);
                }
            }
        }

        if (filteredListUser.size() > 0) {
            UserNoData.setVisibility(View.GONE);
            UpgradeMitraRecyclerView.setVisibility(View.VISIBLE);
        } else {
            UserNoData.setVisibility(View.VISIBLE);
            UpgradeMitraRecyclerView.setVisibility(View.GONE);
        }
        adapter.setListUser(filteredListUser);
    }


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