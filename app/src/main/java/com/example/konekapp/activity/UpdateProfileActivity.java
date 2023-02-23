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

public class UpdateProfileActivity extends AppCompatActivity {

    private TextView UpdatePhoneNumber;
    private EditText UpdateProfName, UpdateProfAddress;
    private CircleImageView UpdateProfImage;
    private Button BtnUpdateProfileDone;

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
        setContentView(R.layout.activity_update_profile);

        UpdatePhoneNumber = findViewById(R.id.updatePhoneNumber);
        UpdateProfName = findViewById(R.id.updateProfName);
        UpdateProfAddress = findViewById(R.id.updateProfAddress);
        UpdateProfImage = findViewById(R.id.updateProfImage);
        BtnUpdateProfileDone = findViewById(R.id.btnUpdateProfileDone);

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


        //retrieve data to field
        usersRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String retrieveName = snapshot.child("Nama").getValue().toString();
                String retrieveAddress = snapshot.child("Alamat").getValue().toString();
                String retrieveImage = snapshot.child("Image").getValue().toString();

                UpdateProfName.setText(retrieveName);
                UpdateProfAddress.setText(retrieveAddress);
                UpdatePhoneNumber.setText("Nomor HP anda " + phoneNumber);
                Picasso.get().load(retrieveImage).into(UpdateProfImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateProfileActivity.this, ""+ error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        UpdateProfImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(UpdateProfileActivity.this);
            }
        });

        BtnUpdateProfileDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateProfileDone();
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
                Log.d("CompleteProfile", resultUri.toString());
                //retrieve to CircleImage
                Picasso.get().load(resultUri).into(UpdateProfImage);

                filePath = UserProfileImagesRef.child(currentUserId + ".jpg");
                Log.d("CompleteProfile", filePath.toString());
            }
        }
    }

    private void UpdateProfileDone() {
        String updatedName = UpdateProfName.getText().toString();
        String updatedAddress = UpdateProfAddress.getText().toString();

        if (TextUtils.isEmpty(updatedName)) {
            Toast.makeText(this, "Isikan nama anda", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(updatedAddress)) {
            Toast.makeText(this, "Isikan alamat anda", Toast.LENGTH_SHORT).show();
        }
        else {
            filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(UpdateProfileActivity.this, "Foto Profil terunggah", Toast.LENGTH_SHORT).show();

                        //get download Url from the storage with the path
                        filePath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {

                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                //downloadUrl result into String
                                profileUrl = task.getResult().toString();
                                Log.d("CompleteProfile profileUrl", profileUrl);

                                HashMap<String, Object> profileMap = new HashMap<>();
                                profileMap.put("Nama", updatedName);
                                profileMap.put("Alamat", updatedAddress);
                                profileMap.put("Image", profileUrl);


                                usersRef.child(currentUserId).updateChildren(profileMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(UpdateProfileActivity.this, "Profil selesai", Toast.LENGTH_SHORT).show();
                                                    Intent toProfileIntent = new Intent(UpdateProfileActivity.this, ProfileActivity.class);
                                                    startActivity(toProfileIntent);
                                                }
                                                else {
                                                    String message = task.getException().toString();
                                                    Toast.makeText(UpdateProfileActivity.this, "Error : "+message, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        });

                    }
                    else {
                        String message = task.getException().toString();
                        Toast.makeText(UpdateProfileActivity.this, "Error :" + message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}