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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MitraProfileActivity extends AppCompatActivity {

    private TextView ProfMitraName, ProfMitraPhoneNumber, ProfMitraNIK, ProfMitraEmail,
            ProfMitraFullAddress, ProfMitraVillage, ProfMitraSubdistrict,
            ProfMitraCity, ProfMitraProvince, ProfMitraQuestion1, ProfMitraQuestion2;
    private ImageView ProfMitraDocument, ProfMitraBackAction;
    private CircleImageView ProfMitraImage;
    private Button BtnEditProfMitra;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference rootRef, usersRef;
    private String currentUserId, phoneNumber, removedPhoneNumber;
    private ProgressDialog pd;

    private View decorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mitra_profile);

        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility==0) {
                    decorView.setSystemUiVisibility(hideSystemBars());
                }
            }
        });

        //init progress dialog
        pd = new ProgressDialog(this);
        pd.setTitle("Please wait...");
        pd.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        currentUserId = currentUser.getUid();
        phoneNumber = currentUser.getPhoneNumber();
        removedPhoneNumber = phoneNumber.substring(3);

        rootRef = FirebaseDatabase.getInstance().getReference();
        usersRef = rootRef.child("users");

        ProfMitraName = findViewById(R.id.profMitraName);
        ProfMitraPhoneNumber = findViewById(R.id.profMitraPhoneNumber);
        ProfMitraNIK = findViewById(R.id.profMitraNIK);
        ProfMitraEmail = findViewById(R.id.profMitraEmail);
        ProfMitraFullAddress = findViewById(R.id.profMitraFullAddress);
        ProfMitraVillage = findViewById(R.id.profMitraVillage);
        ProfMitraSubdistrict = findViewById(R.id.profMitraSubdistrict);
        ProfMitraCity = findViewById(R.id.profMitraCity);
        ProfMitraProvince = findViewById(R.id.profMitraProvince);
        ProfMitraQuestion1 = findViewById(R.id.profMitraQuestion1);
        ProfMitraQuestion2 = findViewById(R.id.profMitraQuestion2);
        ProfMitraDocument = findViewById(R.id.profMitraDocument);
        ProfMitraBackAction = findViewById(R.id.profMitraBackAction);
        ProfMitraImage = findViewById(R.id.profMitraImage);
        BtnEditProfMitra = findViewById(R.id.btnEditProfMitra);

        pd.setMessage("Memuat data anda");
        pd.show();

        usersRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pd.dismiss();
                String Image = snapshot.child("image").getValue().toString();
                String Name = snapshot.child("name").getValue().toString();
                String NIK = snapshot.child("nik").getValue().toString();
                String Email = snapshot.child("email").getValue().toString();
                String FullAddress = snapshot.child("fullAddress").getValue().toString();
                String Village = snapshot.child("village").getValue().toString();
                String Subdistrict = snapshot.child("subdistrict").getValue().toString();
                String City = snapshot.child("city").getValue().toString();
                String Province = snapshot.child("province").getValue().toString();
                String IdCardImage = snapshot.child("idCardImage").getValue().toString();
                String Question1 = snapshot.child("question1").getValue().toString();
                String Question2 = snapshot.child("question2").getValue().toString();

                Picasso.get().load(Image).into(ProfMitraImage);
                ProfMitraName.setText(Name);
                ProfMitraPhoneNumber.setText(removedPhoneNumber);
                ProfMitraNIK.setText(NIK);
                ProfMitraEmail.setText(Email);
                ProfMitraFullAddress.setText(FullAddress);
                ProfMitraVillage.setText(Village);
                ProfMitraSubdistrict.setText(Subdistrict);
                ProfMitraCity.setText(City);
                ProfMitraProvince.setText(Province);
                Picasso.get().load(IdCardImage).into(ProfMitraDocument);
                ProfMitraQuestion1.setText(Question1);
                ProfMitraQuestion2.setText(Question2);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pd.dismiss();
                Toast.makeText(MitraProfileActivity.this, ""+ error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        ProfMitraBackAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MitraProfileActivity.super.onBackPressed();
            }
        });

        BtnEditProfMitra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editProfMitraIntent = new Intent(MitraProfileActivity.this, UpdateMitraProfileActivity.class);
                startActivity(editProfMitraIntent);
            }
        });
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