package com.example.konekapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
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
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class CompleteProfileActivity extends AppCompatActivity {

    private ImageView CompleteBackAction;
    private CircleImageView CompleteProfImage;
    private ConstraintLayout ImageConstraint;
    private EditText CompleteProfName, CompleteProfAddress, CompleteProfDetailAddress;
    private TextView PhoneNumberTV;
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

        CompleteBackAction = findViewById(R.id.completeBackAction);
        CompleteProfDetailAddress = findViewById(R.id.completeProfDetailAddress);
        PhoneNumberTV = findViewById(R.id.phoneNumberTV);
        CompleteProfImage = findViewById(R.id.completeProfImage);
        CompleteProfName = findViewById(R.id.completeProfName);
        CompleteProfAddress = findViewById(R.id.completeProfAddress);
        BtnCompleteProfileDone = findViewById(R.id.btnCompleteProfileDone);
        ImageConstraint = findViewById(R.id.imageConstraint);

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

        PhoneNumberTV.setText(phoneNumber);

        CompleteBackAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CompleteProfileActivity.super.onBackPressed();
            }
        });

        //profile image
        ImageConstraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Open Gallery and Crop activity
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(CompleteProfileActivity.this);
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
                        }

    }


    private void CompleteProfileDone() {

        String Name = CompleteProfName.getText().toString();
        String Address = CompleteProfAddress.getText().toString();
        String DetailAddress = CompleteProfDetailAddress.getText().toString();

//        if (checkInput()) {
//            //jalanin update database
//        }

        //empty checker
        if (TextUtils.isEmpty(Name) || TextUtils.isEmpty((DetailAddress)) || TextUtils.isEmpty(Address) || resultUri == null) {
            Toast.makeText(this, "Data belum lengkap", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            pd.setMessage("Mengunggah Data");
            pd.show();

            //put cropped uri to firebase storage
            filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {

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
                                profileMap.put("Nama", Name);
                                profileMap.put("Domisili", Address);
                                profileMap.put("Alamat Lengkap", DetailAddress);
                                profileMap.put("Image", profileUrl);
                                profileMap.put("Role", "1");


                                usersRef.child(currentUserId).updateChildren(profileMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(CompleteProfileActivity.this, "Profil selesai", Toast.LENGTH_SHORT).show();
                                                    Intent CompleteProfileDoneIntent = new Intent(CompleteProfileActivity.this, MainActivity.class);
                                                    startActivity(CompleteProfileDoneIntent);
                                                }
                                                else {
                                                    String message = task.getException().toString();
                                                    Toast.makeText(CompleteProfileActivity.this, "Error : "+message, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        });

                    }
                    else {
                        pd.dismiss();

                        String message = task.getException().toString();
                        Toast.makeText(CompleteProfileActivity.this, "Error :" + message, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

//    private Boolean checkInput() {
//        String Name = CompleteProfName.getText().toString();
//        String Email = CompleteProfEmail.getText().toString();
//        String Address = CompleteProfAddress.getText().toString();
//
//        if (TextUtils.isEmpty(Name) || TextUtils.isEmpty((Email)) || TextUtils.isEmpty(Address) || resultUri == null) {
//            Toast.makeText(this, "Data belum lengkap", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        if (regex tidak match) {
//
//            return false;
//        }
//
//        return true;
//    }


}