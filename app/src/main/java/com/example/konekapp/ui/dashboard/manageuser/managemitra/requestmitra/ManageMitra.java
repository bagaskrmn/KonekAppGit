package com.example.konekapp.ui.dashboard.manageuser.managemitra.requestmitra;


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

public class ManageMitra extends AppCompatActivity {

    private List<UserModel> list;
    private List<UserModel> filteredListUser;
    private RecyclerView recyclerView;
    private ImageView ManageMitraBackAction;
    private ManageMitraAdapter adapter;
    private View decorView;
    private ProgressDialog pd;
    private DatabaseReference usersRef, rootRef;
    private String currentUserId, phoneNumber;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private EditText SearchUser;
    private TextView UserNoData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_mitra);

        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility==0) {
                    decorView.setSystemUiVisibility(hideSystemBars());
                }
            }
        });

        ManageMitraBackAction = findViewById(R.id.manageMitraBackAction);
        SearchUser = findViewById(R.id.searchUser);
        UserNoData = findViewById(R.id.userNoData);

        list = new ArrayList<>();
        recyclerView = findViewById(R.id.manageMitraRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ManageMitraAdapter(this);
        recyclerView.setAdapter(adapter);

        rootRef = FirebaseDatabase.getInstance().getReference();
        usersRef = rootRef.child("users");
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        currentUserId = currentUser.getUid();

        ManageMitraBackAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ManageMitra.super.onBackPressed();
            }
        });

        //init ProgressDialog
        pd = new ProgressDialog(this);
        pd.setTitle("Please Wait...");
        pd.setCanceledOnTouchOutside(false);

        pd.setMessage("Memuat data");
        pd.show();

        listenUserFromDatabase();

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


    }



    private void listenUserFromDatabase() {
        usersRef.addValueEventListener(listener);
    }

    ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            pd.dismiss();
            list.clear();
            for (DataSnapshot ds : snapshot.getChildren()) {
                UserModel user = ds.getValue(UserModel.class);
                user.setUserId(ds.getKey());
                try {
                    if (!user.getUserId().equals(currentUserId) && user.getRole().equals("4")) {
                        list.add(user);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (list.size() > 0) {
                UserNoData.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            } else {
                UserNoData.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
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
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            UserNoData.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
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