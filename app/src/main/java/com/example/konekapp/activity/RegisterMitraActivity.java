package com.example.konekapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterMitraActivity extends AppCompatActivity {

    private TextView RegMitraPhoneNumber, RegMitraName;
    private EditText RegMitraDetailAddress, RegMitraNIK, RegMitraEmail, RegKuisioner1, RegKuisioner2;
    private Button BtnRegMitraDone;
    private ImageView RegMitraBackAction;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference rootRef, usersRef;
    private String currentUserId, phoneNumber, removedPhoneNumber;
    private ProgressDialog pd;

    private View decorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_mitra);

        RegMitraDetailAddress = findViewById(R.id.regMitraDetailAddress);
        RegMitraPhoneNumber = findViewById(R.id.regMitraPhoneNumber);
        RegMitraName = findViewById(R.id.regMitraName);
        RegMitraNIK = findViewById(R.id.regMitraNIK);
        RegMitraEmail = findViewById(R.id.regMitraEmail);
        BtnRegMitraDone = findViewById(R.id.btnRegMitraDone);
        RegKuisioner1 = findViewById(R.id.regKuisioner1);
        RegKuisioner2 = findViewById(R.id.regKuisioner2);
        RegMitraBackAction = findViewById(R.id.regMitraBackAction);

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
        usersRef = rootRef.child("Users");


        pd.setMessage("Memuat data anda");
        pd.show();

        //retrieve data to field
        usersRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String retrieveName = snapshot.child("Nama").getValue().toString();

                String retrieveDetailAddress = snapshot.child("Alamat Lengkap").getValue().toString();

                RegMitraName.setText(retrieveName);
                RegMitraDetailAddress.setText(retrieveDetailAddress);
                RegMitraPhoneNumber.setText(removedPhoneNumber);

                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pd.dismiss();
                Toast.makeText(RegisterMitraActivity.this, ""+ error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        BtnRegMitraDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegMitraDone();
            }
        });

        RegMitraBackAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterMitraActivity.super.onBackPressed();
            }
        });
    }

    private void RegMitraDone() {
        String registeredNIK = RegMitraNIK.getText().toString();
        String registeredEmail = RegMitraEmail.getText().toString();
        String registeredDetailAddress = RegMitraDetailAddress.getText().toString();
        String registeredKuisioner1 = RegKuisioner1.getText().toString();
        String registeredKuisioner2 = RegKuisioner2.getText().toString();

        if (TextUtils.isEmpty(registeredEmail) || TextUtils.isEmpty(registeredNIK) || TextUtils.isEmpty(registeredDetailAddress)
                || TextUtils.isEmpty(registeredKuisioner1) || TextUtils.isEmpty(registeredKuisioner2) ) {
            Toast.makeText(this, "Data belum lengkap", Toast.LENGTH_SHORT).show();
            return;
        }

        else {
            pd.setMessage("Mengunggah Data");
            pd.show();

            HashMap<String, Object> profileMap = new HashMap<>();
            profileMap.put("NIK", registeredNIK);
            profileMap.put("Email", registeredEmail);
            profileMap.put("Alamat Lengkap", registeredDetailAddress);
            profileMap.put("Kuisioner 1", registeredKuisioner1);
            profileMap.put("Kuisioner 2", registeredKuisioner2);
            profileMap.put("Role", "2");


            usersRef.child(currentUserId).updateChildren(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegisterMitraActivity.this, "Registrasi selesai", Toast.LENGTH_SHORT).show();
                                Intent registerMitraSuccess = new Intent(RegisterMitraActivity.this, RegisterSuccessActivity.class);
                                startActivity(registerMitraSuccess);
                                finish();
                            }
                            else {
                                String message = task.getException().toString();
                                Toast.makeText(RegisterMitraActivity.this, "Error : "+message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
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