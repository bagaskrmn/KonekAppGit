package com.example.konekapp.ui.dashboard.home.consultation.addconsultation.mitra;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.konekapp.R;
import com.example.konekapp.ui.dashboard.home.consultation.chatroom.ChatRoomActivity;
import com.example.konekapp.model.UserModel;
import com.example.konekapp.ui.dashboard.home.consultation.addconsultation.AddConsultationAdapter;
import com.example.konekapp.ui.dashboard.home.consultation.addconsultation.UserListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TambahKonsultasiToAhliTaniActivity extends AppCompatActivity implements UserListener {

    private DatabaseReference userRef;
    //Keperluan RecyclerView
    private ArrayList<UserModel> listUser;
    private RecyclerView recyclerView;
    ImageView btnBack;
    TextView tvNoData;
    EditText edtSearch;

    private View decorView;

    private AddConsultationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_konsultasi);

        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility==0) {
                    decorView.setSystemUiVisibility(hideSystemBars());
                }
            }
        });


        tvNoData = findViewById(R.id.tvNoDataUser);
        edtSearch = findViewById(R.id.edtSearchUser);

        userRef = FirebaseDatabase.getInstance().getReference().child("users");
        listUser = new ArrayList<>();

        //recyclerView
        adapter = new AddConsultationAdapter(this);
        recyclerView = findViewById(R.id.recyclerViewMentor);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        listenUserFromDatabase();

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterList(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

    }

    private void listenUserFromDatabase() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listUser.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    UserModel user = ds.getValue(UserModel.class);
                    user.setUserId(ds.getKey());
                    try {
                        if (!user.getUserId().equals(currentUserId) && user.getRole().equals("2")) {
                            listUser.add(user);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                adapter.setListUserModels(listUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void filterList(String query) {
        List<UserModel> filteredListUser = new ArrayList<>();
        filteredListUser.clear();
        Log.d("RecentConversationAdapter", "filterList: "+query);
        if (query.isEmpty()) {
            filteredListUser.addAll(listUser);
        } else {
            for (UserModel item : listUser) {
                if (item.name.toLowerCase().contains(query.toLowerCase())) {
                    filteredListUser.add(item);
                }
            }
        }

        if (filteredListUser.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            tvNoData.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            tvNoData.setVisibility(View.VISIBLE);
        }

        Log.d("AddConsultationAdapter", "list: "+ listUser);
        Log.d("AddConsultationAdapter", "filterList: "+ filteredListUser);
        adapter.setListUserModels(filteredListUser);
    }

    @Override
    public void onUserClick(UserModel user) {
        Intent intent  = new Intent(TambahKonsultasiToAhliTaniActivity.this, ChatRoomActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
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