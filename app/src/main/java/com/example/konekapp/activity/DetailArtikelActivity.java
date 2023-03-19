package com.example.konekapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.konekapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class DetailArtikelActivity extends AppCompatActivity {

    private ImageView BackActionDetailArtikel, DetailArtikelImage;
    private TextView DetailArtikelTitle, DetailArtikelSource, DetailArtikelDate, DetailArtikelDescription;
    private Button BtnEditArtikel, BtnDeleteArtikel;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private String currentUserId, role, DetailKey;
    private DatabaseReference artikelRef, rootRef, usersRef;
    private StorageReference ArtikelImagesRef, artikelPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_artikel);

        DetailArtikelImage = findViewById(R.id.detailArtikelImage);
        DetailArtikelTitle = findViewById(R.id.detailArtikelTitle);
        DetailArtikelSource = findViewById(R.id.detailArtikelSource);
        DetailArtikelDate = findViewById(R.id.detailArtikelDate);
        DetailArtikelDescription = findViewById(R.id.detailArtikelDescription);
        BackActionDetailArtikel = findViewById(R.id.backActionDetailArtikel);
        BtnDeleteArtikel = findViewById(R.id.btnDeleteArtikel);
        BtnEditArtikel = findViewById(R.id.btnEditArtikel);


        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        currentUserId = currentUser.getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        usersRef = rootRef.child("Users");
        artikelRef = FirebaseDatabase.getInstance().getReference().child("Artikel");
        ArtikelImagesRef = FirebaseStorage.getInstance().getReference().child("Artikel Images");
        artikelPath = ArtikelImagesRef.child(DetailKey+".jpg");

        //retrieve Key for artikel from Adapter
        Intent intent = getIntent();
        DetailKey = intent.getStringExtra("Key");
        if (DetailKey.equals("")) {
            Intent i = new Intent(DetailArtikelActivity.this, ArtikelActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        } else {
            artikelRef.child(DetailKey).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //retrieve Data Artikel
                    String retrieveTitle = snapshot.child("Title").getValue().toString();
                    String retrieveImage = snapshot.child("Image").getValue().toString();
                    String retrieveSource = snapshot.child("Source").getValue().toString();
                    String retrieveDate = snapshot.child("Date").getValue().toString();
                    String retrieveDescription = snapshot.child("Description").getValue().toString();

                    //set Data to the item
                    DetailArtikelTitle.setText(retrieveTitle);
                    Picasso.get().load(retrieveImage).into(DetailArtikelImage);
                    DetailArtikelSource.setText(retrieveSource);
                    DetailArtikelDate.setText(retrieveDate);
                    DetailArtikelDescription.setText(retrieveDescription);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


        usersRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                role = snapshot.child("Role").getValue().toString();

                if (role.equals("3") || role.equals("4")) {
                    BtnDeleteArtikel.setVisibility(View.VISIBLE);
                    BtnEditArtikel.setVisibility(View.VISIBLE);
                }
                else {
                    BtnDeleteArtikel.setVisibility(View.GONE);
                    BtnEditArtikel.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        BackActionDetailArtikel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailArtikelActivity.super.onBackPressed();
            }
        });

        BtnDeleteArtikel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                artikelPath = ArtikelImagesRef.child(DetailKey+".jpg");
                artikelPath.delete();
                //delete on database
                artikelRef.child(DetailKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent backToArtikel = new Intent(DetailArtikelActivity.this, ArtikelActivity.class);
                            backToArtikel.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(backToArtikel);
                            finish();
                        }
                        else {
                            String message = task.getException().toString();
                            Toast.makeText(DetailArtikelActivity.this, "Error :" + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });






//        String DetailTitle = intent.getStringExtra("Title");
//        String DetailImage = intent.getStringExtra("Image");
//        String DetailSource = intent.getStringExtra("Source");
//        String DetailDate = intent.getStringExtra("Date");
//        String DetailDescription = intent.getStringExtra("Description");

        //set Data to the item
//        DetailArtikelTitle.setText(DetailTitle);
//        Picasso.get().load(DetailImage).into(DetailArtikelImage);
//        DetailArtikelSource.setText(DetailSource);
//        DetailArtikelDate.setText(DetailDate);
//        DetailArtikelDescription.setText(DetailDescription);

    }
}