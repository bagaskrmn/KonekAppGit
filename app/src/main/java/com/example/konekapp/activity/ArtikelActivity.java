package com.example.konekapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.konekapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ArtikelActivity extends AppCompatActivity {

    private ImageView ArtikelBackAction;
    private ProgressDialog pd;
    private DatabaseReference artikelRef;

    //Keperluan RecyclerView
    private ArrayList<ArtikelModel> list;
    private FullArtikelAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artikel);

        ArtikelBackAction = findViewById(R.id.artikelBackAction);

        artikelRef = FirebaseDatabase.getInstance().getReference().child("Artikel");
        list = new ArrayList<>();
        recyclerView = findViewById(R.id.fullArtikelrtikelRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FullArtikelAdapter(this, list);
        recyclerView.setAdapter(adapter);

        //init ProgressDialog
        pd = new ProgressDialog(this);
        pd.setTitle("Please Wait...");
        pd.setCanceledOnTouchOutside(false);

        pd.setMessage("Memuat data");
        pd.show();

        ArtikelBackAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ArtikelBackAction = new Intent(ArtikelActivity.this, MainActivity.class);
                startActivity(ArtikelBackAction);
            }
        });

        artikelRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pd.dismiss();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ArtikelModel artikel = dataSnapshot.getValue(ArtikelModel.class);
                    list.add(artikel);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pd.dismiss();
            }
        });

    }
}