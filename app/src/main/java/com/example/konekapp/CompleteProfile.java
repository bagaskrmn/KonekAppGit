package com.example.konekapp;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageActivity;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class CompleteProfile extends AppCompatActivity {

    private CircleImageView CompleteProfImage;
    private TextView PhoneNumberTextView, BtnChangeProfImage;
    private EditText CompleteProfName, CompleteProfAddress;
    private Button BtnCompleteProfileDone;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference rootRef, usersRef;
    private String phoneNumber, currentUserId, profileUrl;
    private ProgressDialog pd;
    private StorageReference UserProfileImagesRef, filePath;
    private static final int GalleryPick = 1;

    private Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);

//        BtnChangeProfImage = findViewById(R.id.btnChangeProfImage);
        CompleteProfImage = findViewById(R.id.completeProfImage);
        PhoneNumberTextView = findViewById(R.id.phoneNumberTextView);
        CompleteProfName = findViewById(R.id.completeProfName);
        CompleteProfAddress = findViewById(R.id.completeProfAddress);
        BtnCompleteProfileDone = findViewById(R.id.btnCompleteProfileDone);

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

        PhoneNumberTextView.setText("Nomor Telfon anda adalah : " + phoneNumber);


        //profile image
        CompleteProfImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Open Gallery and Crop activity
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(CompleteProfile.this);
            }
        });
        //Profile Image

        BtnCompleteProfileDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CompleteProfileDone();
            }
        });
    }


    //profile image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //can be removed
//        if (requestCode==GalleryPick && resultCode==RESULT_OK && data !=null) {
//            Uri imageUri = data.getData();
//        }
        //can be removed

        //result crop image OK
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                //result of cropped image into Uri
                resultUri = result.getUri();
                Log.d("CompleteProfile", resultUri.toString());
                //retrieve to CircleImage
                Picasso.get().load(resultUri).into(CompleteProfImage);

                //potong di sini
                filePath = UserProfileImagesRef.child(currentUserId + ".jpg");
                Log.d("CompleteProfile", filePath.toString());

            }
                //put Uri cropped image into FirebaseStorage
//                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(CompleteProfile.this, "Foto Profil terunggah", Toast.LENGTH_SHORT).show();
//
//                            //from comment section
//                            //get download Url from the storage with the path
//                            filePath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
//                                //can be change into filePath.getDownloadUrl
//                                @Override
//                                public void onComplete(@NonNull Task<Uri> task) {
//                                    //downloadUrl result into String
//                                    String profileUrl = task.getResult().toString();
//                                    //set value of the downloadedUrl into database
//                                    usersRef.child(currentUserId).child("Image").setValue(profileUrl)
//                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<Void> task) {
//                                                    //when the database is uploaded with the download Url
//                                                    if (task.isSuccessful()) {
//                                                        Toast.makeText(CompleteProfile.this, "Gambar tersimpan di Database", Toast.LENGTH_SHORT).show();
//                                                    }
//                                                    else {
//                                                        String message = task.getException().toString();
//                                                        Toast.makeText(CompleteProfile.this, "Error : "+message, Toast.LENGTH_SHORT).show();
//                                                    }
//                                                }
//                                            });
//                                }
//                            });
//
//                        }
//                        else {
//                            String message = task.getException().toString();
//                            Toast.makeText(CompleteProfile.this, "Error :" + message, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
        }

    }

    //profile image

    private void CompleteProfileDone() {

        String Name = CompleteProfName.getText().toString();
        String Address = CompleteProfAddress.getText().toString();

        if (TextUtils.isEmpty(Name)) {
            Toast.makeText(this, "Isikan Nama anda", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(Address)) {
            Toast.makeText(this, "Isikan Alamat Anda", Toast.LENGTH_SHORT).show();
        }
        else {

            filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(CompleteProfile.this, "Foto Profil terunggah", Toast.LENGTH_SHORT).show();

                        //from comment section
                        //get download Url from the storage with the path
                        filePath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {

                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                //downloadUrl result into String
                                profileUrl = task.getResult().toString();
                                Log.d("CompleteProfile profileUrl", profileUrl);

                                HashMap<String, Object> profileMap = new HashMap<>();
                                profileMap.put("Nama", Name);
                                profileMap.put("Alamat", Address);
                                profileMap.put("Image", profileUrl);



                                usersRef.child(currentUserId).updateChildren(profileMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(CompleteProfile.this, "Profil selesai", Toast.LENGTH_SHORT).show();
                                                    Intent toProfileIntent = new Intent(CompleteProfile.this, Profile.class);
                                                    startActivity(toProfileIntent);
                                                }
                                                else {
                                                    String message = task.getException().toString();
                                                    Toast.makeText(CompleteProfile.this, "Error : "+message, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                //set value of the downloadedUrl into database
//                                usersRef.child(currentUserId).child("Image").setValue(profileUrl)
//                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<Void> task) {
//                                                //when the database is uploaded with the download Url
//                                                if (task.isSuccessful()) {
//                                                    Toast.makeText(CompleteProfile.this, "Gambar tersimpan di Database", Toast.LENGTH_SHORT).show();
//                                                }
//                                                else {
//                                                    String message = task.getException().toString();
//                                                    Toast.makeText(CompleteProfile.this, "Error : "+message, Toast.LENGTH_SHORT).show();
//                                                }
//                                            }
//                                        });
                            }
                        });

                    }
                    else {
                        String message = task.getException().toString();
                        Toast.makeText(CompleteProfile.this, "Error :" + message, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }


}