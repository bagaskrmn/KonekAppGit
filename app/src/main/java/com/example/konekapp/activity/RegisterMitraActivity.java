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

    private TextView RegPhoneNumber;
    private EditText RegMitraName, RegMitraAddress, RegMitraNIK, RegMitraEmail;
    private CircleImageView RegMitraImage;
    private Button BtnRegMitraDone;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference rootRef, usersRef;
    private String currentUserId, phoneNumber, profileUrl;
    private ProgressDialog pd;
    private StorageReference UserProfileImagesRef, filePath;

    private Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_mitra);

        RegPhoneNumber = findViewById(R.id.regPhoneNumber);
        RegMitraName = findViewById(R.id.regMitraName);
        RegMitraAddress = findViewById(R.id.regMitraAddress);
        RegMitraNIK = findViewById(R.id.regMitraNIK);
        RegMitraEmail = findViewById(R.id.regMitraEmail);
        RegMitraImage = findViewById(R.id.regMitraImage);
        BtnRegMitraDone = findViewById(R.id.btnRegMitraDone);

        //init progress dialog
        pd = new ProgressDialog(this);
        pd.setTitle("Please wait...");
        pd.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        currentUserId = currentUser.getUid();
        phoneNumber = currentUser.getPhoneNumber();
        rootRef = FirebaseDatabase.getInstance().getReference();
        usersRef = rootRef.child("Users");
        UserProfileImagesRef = FirebaseStorage.getInstance().getReference().child("Profile Images");


        pd.setMessage("Memuat data anda");
        pd.show();

        //retrieve data to field
        usersRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String retrieveName = snapshot.child("Nama").getValue().toString();
                String retrieveAddress = snapshot.child("Alamat").getValue().toString();
                String retrieveImage = snapshot.child("Image").getValue().toString();

                RegMitraName.setText(retrieveName);
                RegMitraAddress.setText(retrieveAddress);
                RegPhoneNumber.setText("Nomor HP anda " + phoneNumber);
                Picasso.get().load(retrieveImage).into(RegMitraImage);

                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pd.dismiss();
                Toast.makeText(RegisterMitraActivity.this, ""+ error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        RegMitraImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(RegisterMitraActivity.this);
            }
        });

        BtnRegMitraDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegMitraDone();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //result crop image OK
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                //result of cropped image into Uri
                resultUri = result.getUri();

                //retrieve to CircleImage
                Picasso.get().load(resultUri).into(RegMitraImage);

                filePath = UserProfileImagesRef.child(currentUserId + ".jpg");

            }
        }
    }

    private void RegMitraDone() {
        String registeredEmail = RegMitraEmail.getText().toString();
        String registeredNIK = RegMitraNIK.getText().toString();
        String registeredName = RegMitraName.getText().toString();
        String registeredAddress = RegMitraAddress.getText().toString();

        if (TextUtils.isEmpty(registeredEmail) || TextUtils.isEmpty(registeredNIK) ||
                TextUtils.isEmpty(registeredName) || TextUtils.isEmpty(registeredAddress)) {
            Toast.makeText(this, "Data belum lengkap", Toast.LENGTH_SHORT).show();
            return;
        }

        //if ProfileImage not changed
        if (resultUri == null) {

            pd.setMessage("Data terunggah");
            pd.show();
            //hanya diupdate objek selain gambar
            HashMap<String, Object> profileMap = new HashMap<>();
            profileMap.put("Nama", registeredName);
            profileMap.put("Alamat", registeredAddress);
            profileMap.put("Email", registeredEmail);
            profileMap.put("NIK", registeredNIK);
            profileMap.put("Role", "2");

            usersRef.child(currentUserId).updateChildren(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterMitraActivity.this, "Update berhasil", Toast.LENGTH_SHORT).show();
                        Intent toProfileIntent = new Intent(RegisterMitraActivity.this, RegisterSuccessActivity.class);
                        startActivity(toProfileIntent);
                        finish();
                    }
                    else {
                        String message = task.getException().toString();
                        Toast.makeText(RegisterMitraActivity.this, "Error : "+message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        else {
            pd.setMessage("Mengunggah Data");
            pd.show();

            filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterMitraActivity.this, "Foto Profil terunggah", Toast.LENGTH_SHORT).show();

                        //get download Url from the storage with the path
                        filePath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {

                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                //downloadUrl result into String
                                profileUrl = task.getResult().toString();
                                Log.d("CompleteProfile profileUrl", profileUrl);

                                pd.setMessage("Data terunggah");
                                pd.show();

                                HashMap<String, Object> profileMap = new HashMap<>();
                                profileMap.put("Nama", registeredName);
                                profileMap.put("Alamat", registeredAddress);
                                profileMap.put("Image", profileUrl);
                                profileMap.put("Email", registeredEmail);
                                profileMap.put("NIK", registeredNIK);
                                profileMap.put("Role", "2");


                                usersRef.child(currentUserId).updateChildren(profileMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(RegisterMitraActivity.this, "Pendaftaran selesai", Toast.LENGTH_SHORT).show();
                                                    Intent toProfileIntent = new Intent(RegisterMitraActivity.this, RegisterSuccessActivity.class);
                                                    startActivity(toProfileIntent);
                                                }
                                                else {
                                                    String message = task.getException().toString();
                                                    Toast.makeText(RegisterMitraActivity.this, "Error : "+message, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        });

                    }
                    else {
                        pd.dismiss();
                        String message = task.getException().toString();
                        Toast.makeText(RegisterMitraActivity.this, "Error :" + message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}