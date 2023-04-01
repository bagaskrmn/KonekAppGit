package com.example.konekapp.activity.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.konekapp.R;
import com.example.konekapp.activity.chat.addconsultation.AddConsultationAdapter;
import com.example.konekapp.activity.chat.addconsultation.UserListener;
import com.example.konekapp.activity.chat.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TambahKonsultasiActivity extends AppCompatActivity implements UserListener {

    private DatabaseReference userRef;
    //Keperluan RecyclerView
    private ArrayList<UserModel> listUser;
    private RecyclerView recyclerView;
    ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_konsultasi);

        userRef = FirebaseDatabase.getInstance().getReference().child("users");
        listUser = new ArrayList<>();

        //recyclerView
        recyclerView = findViewById(R.id.recyclerViewMentor);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new AddConsultationAdapter(listUser, this));

        listenUserFromDatabase();


        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
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
                recyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onUserClick(UserModel user) {
        Intent intent  = new Intent(TambahKonsultasiActivity.this, ChatRoomActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }
}