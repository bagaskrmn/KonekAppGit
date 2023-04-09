package com.example.konekapp.ui.dashboard.account.setting.accountsetting.editaccount;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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

public class EditProfileActivity extends AppCompatActivity {

    private TextView UpdatePhoneNumber;
    private EditText UpdateProfName, UpdateProfAddress, UpdateProfDetailAddress;
    private CircleImageView UpdateProfImage;
    private Button BtnUpdateProfileDone;
    private ImageView UpdateProfBackAction;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference rootRef, usersRef;
    private String profileUrl, currentUserId, phoneNumber, removedPhoneNumber;
    private ProgressDialog pd;
    private StorageReference userProfileImagesRef, imageProfilePath;

    private Uri resultUri;

    private View decorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        UpdateProfDetailAddress = findViewById(R.id.updateProfDetailAddress);
        UpdatePhoneNumber = findViewById(R.id.updatePhoneNumber);
        UpdateProfName = findViewById(R.id.updateProfName);
        UpdateProfAddress = findViewById(R.id.updateProfAddress);
        UpdateProfImage = findViewById(R.id.updateProfImage);
        BtnUpdateProfileDone = findViewById(R.id.btnUpdateProfileDone);
        UpdateProfBackAction = findViewById(R.id.updateProfBackAction);

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
        userProfileImagesRef = FirebaseStorage.getInstance().getReference().child("profileImages");

        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility==0) {
                    decorView.setSystemUiVisibility(hideSystemBars());
                }
            }
        });

        pd.setMessage("Memuat data Anda");
        pd.show();

        //retrieve data to field
        usersRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String retrieveName = snapshot.child("name").getValue().toString();
                String retrieveAddress = snapshot.child("domicile").getValue().toString();
                String retrieveDetailAddress = snapshot.child("fullAddress").getValue().toString();
                String retrieveImage = snapshot.child("image").getValue().toString();

                UpdateProfName.setText(retrieveName);
                UpdateProfAddress.setText(retrieveAddress);
                UpdateProfDetailAddress.setText(retrieveDetailAddress);
                UpdatePhoneNumber.setText(removedPhoneNumber);
                Picasso.get().load(retrieveImage).into(UpdateProfImage);

                //retrieveImage is Url from database which is linked to storage
                //transform retrieveImage into Uri
                //or just make condition where filePath is null, doesn't have to put resultUri to filePath in Database

                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pd.dismiss();
                Toast.makeText(EditProfileActivity.this, ""+ error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        UpdateProfImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(EditProfileActivity.this);
            }
        });

        BtnUpdateProfileDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateProfileDone();
            }
        });

        UpdateProfBackAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditProfileActivity.super.onBackPressed();
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
                Picasso.get().load(resultUri).into(UpdateProfImage);

                imageProfilePath = userProfileImagesRef.child(currentUserId+".jpg");

            }
        }
    }

    private void UpdateProfileDone() {
        String updatedName = UpdateProfName.getText().toString();
        String updatedAddress = UpdateProfAddress.getText().toString();
        String updatedDetailAddress = UpdateProfDetailAddress.getText().toString();

        if (TextUtils.isEmpty(updatedName) || TextUtils.isEmpty(updatedAddress) || TextUtils.isEmpty(updatedDetailAddress)) {
            Toast.makeText(this, "Data tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        //jika gambar tidak diganti
        if (resultUri == null) {
            pd.setMessage("Mengunggah Data");
            pd.show();
            //hanya diupdate objek selain gambar
            HashMap<String, Object> profileMap = new HashMap<>();
            profileMap.put("name", updatedName);
            profileMap.put("domicile", updatedAddress);
            profileMap.put("fullAddress", updatedDetailAddress);

            usersRef.child(currentUserId).updateChildren(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    pd.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(EditProfileActivity.this, "Update berhasil", Toast.LENGTH_SHORT).show();
                        EditProfileActivity.super.onBackPressed();
                    }
                    else {
                        String message = task.getException().toString();
                        Toast.makeText(EditProfileActivity.this, "Error : "+message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        else {
            pd.setMessage("Mengunggah Data");
            pd.show();

            //put Uri into firebaseStorage
            imageProfilePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    pd.dismiss();
                    if (task.isSuccessful()) {

                        //get download Url from the storage path(filePath)
                        imageProfilePath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {


                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {

                                //downloadUrl result into String
                                profileUrl = task.getResult().toString();

                                pd.setMessage("Data terunggah");
                                pd.show();

                                HashMap<String, Object> profileMap = new HashMap<>();
                                profileMap.put("name", updatedName);
                                profileMap.put("domicile", updatedAddress);
                                profileMap.put("fullAddress", updatedDetailAddress);
                                profileMap.put("image", profileUrl);

                                //update child onDatabase from hashmap(profileMap)
                                usersRef.child(currentUserId).updateChildren(profileMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                pd.dismiss();
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(EditProfileActivity.this, "Update berhasil", Toast.LENGTH_SHORT).show();
                                                    EditProfileActivity.super.onBackPressed();
                                                }
                                                else {
                                                    String message = task.getException().toString();
                                                    Toast.makeText(EditProfileActivity.this, "Error : "+message, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        });

                    }
                    else {
                        String message = task.getException().toString();
                        Toast.makeText(EditProfileActivity.this, "Error :" + message, Toast.LENGTH_SHORT).show();
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