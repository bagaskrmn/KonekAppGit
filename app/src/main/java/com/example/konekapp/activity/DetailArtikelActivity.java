package com.example.konekapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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

    private ProgressDialog pd;

    private View decorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_artikel);

        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility==0) {
                    decorView.setSystemUiVisibility(hideSystemBars());
                }
            }
        });

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

        //init progress dialog
        pd = new ProgressDialog(this);
        pd.setTitle("Please wait...");
        pd.setCanceledOnTouchOutside(false);

        //retrieve Key for artikel from Adapter
        Intent intent = getIntent();
        DetailKey = intent.getStringExtra("Key");
        //error right here
        if (DetailKey== null) {
            Intent i = new Intent(DetailArtikelActivity.this, ArtikelActivity.class);
            startActivity(i);
            finish();
        } else {
            artikelRef.child(DetailKey).addValueEventListener(listener);
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
                Intent BackActionDetailIntent = new Intent(DetailArtikelActivity.this, ArtikelActivity.class);
                startActivity(BackActionDetailIntent);
                finish();
            }
        });

        BtnEditArtikel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent EditArtikelIntent = new Intent(DetailArtikelActivity.this, EditArtikelActivity.class);
                EditArtikelIntent.putExtra("Key", DetailKey);
                startActivity(EditArtikelIntent);
            }
        });

        BtnDeleteArtikel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.setMessage("Menghapus Artikel");
                pd.show();
                artikelRef.child(DetailKey).removeEventListener(listener);
                artikelPath = ArtikelImagesRef.child(DetailKey+".jpg");
                artikelPath.delete();
                //delete on database
                artikelRef.child(DetailKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            pd.dismiss();
                            pd.setMessage("Artikel terhapus");
                            pd.show();
                            Intent backToArtikel = new Intent(DetailArtikelActivity.this, ArtikelActivity.class);
                            startActivity(backToArtikel);
                            finish();
                        }
                        else {
                            pd.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(DetailArtikelActivity.this, "Error :" + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });
    }

    ValueEventListener listener = new ValueEventListener() {
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
    };

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