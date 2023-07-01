package com.example.konekapp.ui.login.completeprofile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.konekapp.R;
import com.example.konekapp.model.NotificationModel;
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
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class CompleteProfileActivity extends AppCompatActivity {

    private View decorView;

    private ImageView CompleteBackAction;
    private CircleImageView CompleteProfImage;
    private ConstraintLayout ImageConstraint;
    private EditText CompleteProfName, CompleteProfAddress, CompleteProfDetailAddress;
    private TextView PhoneNumberTV;
    private Button BtnCompleteProfileDone;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference rootRef, usersRef, notificationRef;
    private String currentUserId,phoneNumber, removedPhoneNumber, profileUrl, systemNotificationImageUrl, notificationKey;
    private ProgressDialog pd;
    private StorageReference userProfileImagesRef, imageProfilePath, systemNotificationImageRef;
//    private boolean isNotificationRead = false;

    //date
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String date;

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

        //calendar
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        date = dateFormat.format(calendar.getTime());

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        currentUserId = currentUser.getUid();
        phoneNumber = currentUser.getPhoneNumber();
        removedPhoneNumber = phoneNumber.substring(3);
        rootRef = FirebaseDatabase.getInstance().getReference();
        usersRef = rootRef.child("users");
        userProfileImagesRef = FirebaseStorage.getInstance().getReference().child("profileImages");

        notificationRef = rootRef.child("notification");
        notificationKey = rootRef.push().getKey();
        systemNotificationImageRef = FirebaseStorage.getInstance().getReference().child("systemNotificationImage").child("konek_icon.png");

        systemNotificationImageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                systemNotificationImageUrl = task.getResult().toString();
            }
        });

        PhoneNumberTV.setText(removedPhoneNumber);

        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility==0) {
                    decorView.setSystemUiVisibility(hideSystemBars());
                }
            }
        });

        //enable and disable button
        BtnCompleteProfileDone.setEnabled(false);
        CompleteProfName.addTextChangedListener(textWatcher);
        CompleteProfAddress.addTextChangedListener(textWatcher);
        CompleteProfDetailAddress.addTextChangedListener(textWatcher);


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

                //retrieve to CircleImage
                Picasso.get().load(resultUri).into(CompleteProfImage);
                imageProfilePath = userProfileImagesRef.child(currentUserId + ".jpg");
            }
        }
    }


    private void CompleteProfileDone() {

        String Name = CompleteProfName.getText().toString();
        String Address = CompleteProfAddress.getText().toString();
        String DetailAddress = CompleteProfDetailAddress.getText().toString();

        //if image is empty
        if (resultUri == null) {
            Toast.makeText(this, "Unggah foto profil anda", Toast.LENGTH_SHORT).show();
        }
        else
        {
            pd.setMessage("Mengunggah Data");
            pd.show();

            //put cropped uri to firebase storage
            imageProfilePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    pd.dismiss();
                    if (task.isSuccessful()) {
                        //get download Url from the storage with the path
                        imageProfilePath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {

                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                //downloadUrl result into String
                                profileUrl = task.getResult().toString();
                                Log.d("CompleteProfile profileUrl", profileUrl);

                                pd.setMessage("Data terunggah");
                                pd.show();

                                HashMap<String, Object> profileMap = new HashMap<>();
                                profileMap.put("name", Name);
                                profileMap.put("domicile", Address);
                                profileMap.put("fullAddress", DetailAddress);
                                profileMap.put("image", profileUrl);
                                profileMap.put("role", "0");
                                profileMap.put("dateJoined", date);
                                profileMap.put("nik", "");
                                profileMap.put("email", "");
                                profileMap.put("village", "");
                                profileMap.put("subdistrict", "");
                                profileMap.put("city", "");
                                profileMap.put("province", "");
                                profileMap.put("idCardImage", "");
                                profileMap.put("question1","");
                                profileMap.put("question2","");



                                usersRef.child(currentUserId).updateChildren(profileMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    pd.dismiss();
                                                    askToJoinMitraNotification();
                                                    Toast.makeText(CompleteProfileActivity.this, "Profil selesai", Toast.LENGTH_SHORT).show();
                                                    Intent CompleteProfileDoneIntent = new Intent(CompleteProfileActivity.this, CompleteProfileSuccess.class);
                                                    CompleteProfileDoneIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(CompleteProfileDoneIntent);
                                                }
                                                else {
                                                    pd.dismiss();
                                                    String message = task.getException().toString();
                                                    Toast.makeText(CompleteProfileActivity.this, "Error : "+message, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        });

                    }
                    else {
                        String message = task.getException().toString();
                        Toast.makeText(CompleteProfileActivity.this, "Error :" + message, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

    }

    //text Watcher for disable btn if any editText is empty
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String textName = CompleteProfName.getText().toString().trim();
            String textAddress = CompleteProfAddress.getText().toString().trim();
            String textDetailAddress = CompleteProfDetailAddress.getText().toString().trim();
            BtnCompleteProfileDone.setEnabled(!textName.isEmpty() && !textAddress.isEmpty() && !textDetailAddress.isEmpty());

        }

        @Override
        public void afterTextChanged(Editable s) {

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
        return
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    }

    private void askToJoinMitraNotification() {
        String title = "Yuk Gabung sebagai Mitra Konek!";
        String description = "Daftarkan diri anda sebagai Mitra Konek dan dapatkan manfaatnya!";
        String kind = "1";

        NotificationModel notificationModel = new NotificationModel(title, description, currentUserId, kind, date, systemNotificationImageUrl, false );

        notificationRef.child(notificationKey).setValue(notificationModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //
            }
        });
    }
}