package com.example.konekapp.ui.dashboard.home.article;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.konekapp.R;
import com.example.konekapp.ui.dashboard.home.article.editarticle.EditArtikelActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

    private ImageView BackActionDetailArtikel, DetailArtikelImage, MenuArtikel;
    private TextView DetailArtikelTitle, DetailArtikelSource, DetailArtikelDate, DetailArtikelDescription, DetailArtikelSourceImage;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private String role, DetailKey, currentUserId;
    private DatabaseReference articleRef, rootRef, usersRef;
    private StorageReference articlePath, articleImagesRef;

    private ProgressDialog pd;

    private View decorView, spaceViewArtikel;

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

        DetailArtikelSourceImage = findViewById(R.id.detailArtikelSourceImage);
        DetailArtikelImage = findViewById(R.id.detailArtikelImage);
        DetailArtikelTitle = findViewById(R.id.detailArtikelTitle);
        DetailArtikelSource = findViewById(R.id.detailArtikelSource);
        DetailArtikelDate = findViewById(R.id.detailArtikelDate);
        DetailArtikelDescription = findViewById(R.id.detailArtikelDescription);
        BackActionDetailArtikel = findViewById(R.id.backActionDetailArtikel);

        spaceViewArtikel = findViewById(R.id.spaceViewArtikel);
        MenuArtikel = findViewById(R.id.menuArtikel);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        currentUserId = currentUser.getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        usersRef = rootRef.child("users");
        articleRef = rootRef.child("article");
        articleImagesRef = FirebaseStorage.getInstance().getReference().child("articleImages");
        articlePath = articleImagesRef.child(DetailKey+".jpg");


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
            articleRef.child(DetailKey).addValueEventListener(listener);
        }

        usersRef.child(currentUserId).addValueEventListener(listener2);

        MenuArtikel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(DetailArtikelActivity.this, v);
                popupMenu.getMenuInflater().inflate(R.menu.artikel_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.editArtikelMenu:
                                toEditArtikel();
                                return true;
                            case R.id.deleteArtikelMenu:
                                deleteArtikel();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();
            }
        });

        BackActionDetailArtikel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailArtikelActivity.super.onBackPressed();
            }
        });
    }

    private void deleteArtikel() {
        articleRef.child(DetailKey).removeEventListener(listener);
        articleRef.child(DetailKey).addValueEventListener(listener1);
    }

    private void toEditArtikel() {
        Intent EditArtikelIntent = new Intent(DetailArtikelActivity.this, EditArtikelActivity.class);
        EditArtikelIntent.putExtra("Key", DetailKey);
        startActivity(EditArtikelIntent);
    }

    ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            //retrieve Data Artikel
            String retrieveTitle = snapshot.child("title").getValue().toString();
            String retrieveImage = snapshot.child("image").getValue().toString();
            String retrieveSource = snapshot.child("source").getValue().toString();
            String retrieveDate = snapshot.child("date").getValue().toString();
            String retrieveDescription = snapshot.child("description").getValue().toString();
            String retrieveSourceImage = snapshot.child("sourceImage").getValue().toString();

            //set Data to the item
            DetailArtikelTitle.setText(retrieveTitle);
            Picasso.get().load(retrieveImage).into(DetailArtikelImage);
            DetailArtikelSource.setText(retrieveSource);
            DetailArtikelDate.setText(retrieveDate);
            DetailArtikelDescription.setText(retrieveDescription);
            DetailArtikelSourceImage.setText(retrieveSourceImage);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    ValueEventListener listener1 = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            String imageUrl = snapshot.child("image").getValue().toString();

            pd.setMessage("Menghapus Artikel");
            pd.show();

            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference imageRefFromUrl = firebaseStorage.getReferenceFromUrl(imageUrl);

            articleRef.child(DetailKey).removeEventListener(listener);
            articleRef.child(DetailKey).removeEventListener(listener1);

            imageRefFromUrl.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    pd.dismiss();
                    articleRef.child(DetailKey).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            DetailArtikelActivity.super.onBackPressed();
                            Toast.makeText(DetailArtikelActivity.this, "Artikel Terhapus", Toast.LENGTH_SHORT).show();
//                            Intent backToArtikel = new Intent(DetailArtikelActivity.this, ArtikelActivity.class);
//                            startActivity(backToArtikel);
//                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DetailArtikelActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(DetailArtikelActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    ValueEventListener listener2 = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            role = snapshot.child("role").getValue().toString();

            if (role.equals("2") || role.equals("3")) {
                spaceViewArtikel.setVisibility(View.GONE);
                MenuArtikel.setVisibility(View.VISIBLE);
            }
            else {
                spaceViewArtikel.setVisibility(View.VISIBLE);
                MenuArtikel.setVisibility(View.GONE);
            }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        usersRef.child(currentUserId).removeEventListener(listener2);
    }
}