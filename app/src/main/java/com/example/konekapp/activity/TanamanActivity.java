package com.example.konekapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.konekapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TanamanActivity extends AppCompatActivity {
    private ImageView TanamanBackAction;
    private ProgressDialog pd;
    private DatabaseReference tanamanRef, rootRef;
    private Button BtnAddTanaman;


    private ArrayList<TanamanModel> list;
    private TanamanAdapter adapter;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tanaman);

        TanamanBackAction = findViewById(R.id.tanamanBackAction);

        BtnAddTanaman = findViewById(R.id.btnAddTanaman);
        rootRef = FirebaseDatabase.getInstance().getReference();
        tanamanRef = rootRef.child("Tanaman");
        list = new ArrayList<>();
        recyclerView = findViewById(R.id.tanamanRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TanamanAdapter(this, list);
        recyclerView.setAdapter(adapter);

        //init ProgressDialog
        pd = new ProgressDialog(this);
        pd.setTitle("Please Wait...");
        pd.setCanceledOnTouchOutside(false);

        pd.setMessage("Memuat data");
        pd.show();


        tanamanRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pd.dismiss();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    TanamanModel tanaman = dataSnapshot.getValue(TanamanModel.class);
                    tanaman.setKey(dataSnapshot.getKey());
                    list.add(tanaman);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        BtnAddTanaman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addTanamanIntent = new Intent(TanamanActivity.this, AddTanamanActivity.class);
                startActivity(addTanamanIntent);
            }
        });
    }
}