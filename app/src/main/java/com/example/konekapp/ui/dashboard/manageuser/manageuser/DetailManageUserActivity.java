package com.example.konekapp.ui.dashboard.manageuser.manageuser;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

public class DetailManageUserActivity extends AppCompatActivity {

    private ImageView DetailManageUserBackAction;
    private CircleImageView DetailManageUserImage;
    private TextView DetailManageUserPhoneNumber;
    private EditText DetailManageUserName, DetailManageUserAddress, DetailManageUserDetailAddress;
    private Button BtnDetailManageUserDone, BtnDeleteUser;
    private ProgressDialog pd;
    private String SelectedUserId, profileUrl;

    private Uri resultUri;

    private DatabaseReference usersRef, rootRef;
    private StorageReference userProfileImagesRef, imageProfilePath;

    private View decorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_manage_user);

        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility==0) {
                    decorView.setSystemUiVisibility(hideSystemBars());
                }
            }
        });

        DetailManageUserBackAction = findViewById(R.id.detailManageUserBackAction);
        DetailManageUserImage = findViewById(R.id.detailManageUserImage);
        DetailManageUserPhoneNumber = findViewById(R.id.detailManageUserPhoneNumber);
        DetailManageUserName = findViewById(R.id.detailManageUserName);
        DetailManageUserAddress = findViewById(R.id.detailManageUserAddress);
        DetailManageUserDetailAddress = findViewById(R.id.detailManageUserDetailAddress);
        BtnDetailManageUserDone = findViewById(R.id.btnDetailManageUserDone);
        BtnDeleteUser = findViewById(R.id.btnDeleteUser);
        rootRef = FirebaseDatabase.getInstance().getReference();
        usersRef = rootRef.child("users");
        userProfileImagesRef = FirebaseStorage.getInstance().getReference().child("profileImages");

        //init progress dialog
        pd = new ProgressDialog(this);
        pd.setTitle("Please wait...");
        pd.setCanceledOnTouchOutside(false);

        Intent intent = getIntent();
        SelectedUserId = intent.getStringExtra("Key");

        DetailManageUserBackAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usersRef.child(SelectedUserId).removeEventListener(listener);
                DetailManageUserActivity.super.onBackPressed();
            }
        });

        if (SelectedUserId== null) {
            Intent i = new Intent(DetailManageUserActivity.this, ManageUserActivity.class);
            startActivity(i);
            finish();
        } else {
            usersRef.child(SelectedUserId).addValueEventListener(listener);
        }

        DetailManageUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(DetailManageUserActivity.this);
            }
        });

        BtnDetailManageUserDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manageUserDone();
            }
        });

        BtnDeleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUser();
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
                Picasso.get().load(resultUri).into(DetailManageUserImage);

                imageProfilePath = userProfileImagesRef.child(SelectedUserId+".jpg");

            }
        }
    }

    private void manageUserDone() {
        String updateName = DetailManageUserName.getText().toString();
        String updateAddress = DetailManageUserAddress.getText().toString();
        String updateDetailAddress = DetailManageUserDetailAddress.getText().toString();

        if (TextUtils.isEmpty(updateName) || TextUtils.isEmpty(updateAddress) || TextUtils.isEmpty(updateDetailAddress)) {
            Toast.makeText(this, "Data tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        if (resultUri ==null) {
            HashMap<String, Object> profileMap = new HashMap<>();
            profileMap.put("name", updateName);
            profileMap.put("domicile", updateAddress);
            profileMap.put("fullAddress", updateDetailAddress);

            usersRef.child(SelectedUserId).updateChildren(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {
                        usersRef.child(SelectedUserId).removeEventListener(listener);
                        Toast.makeText(DetailManageUserActivity.this, "Update berhasil", Toast.LENGTH_SHORT).show();
                        DetailManageUserActivity.super.onBackPressed();
                    }
                    else {
                        usersRef.child(SelectedUserId).removeEventListener(listener);
//                        String message = task.getException().toString();
//                        Toast.makeText(DetailManageUserActivity.this, "Error : "+message, Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

        else {
            //put Uri into firebaseStorage
            imageProfilePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        //get download Url from the storage path(filePath)
                        imageProfilePath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {

                                //downloadUrl result into String
                                profileUrl = task.getResult().toString();

                                HashMap<String, Object> profileMap = new HashMap<>();
                                profileMap.put("name", updateName);
                                profileMap.put("domicile", updateAddress);
                                profileMap.put("fullAddress", updateDetailAddress);
                                profileMap.put("image", profileUrl);

                                //update child onDatabase from hashmap(profileMap)
                                usersRef.child(SelectedUserId).updateChildren(profileMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()) {
//                                                    pd.dismiss();
                                                    usersRef.child(SelectedUserId).removeEventListener(listener);
                                                    Toast.makeText(DetailManageUserActivity.this, "Update berhasil", Toast.LENGTH_SHORT).show();
                                                    DetailManageUserActivity.super.onBackPressed();
                                                }
                                                else {
                                                    usersRef.child(SelectedUserId).removeEventListener(listener);
//                                                    String message = task.getException().toString();
//                                                    Toast.makeText(EditProfileActivity.this, "Error : "+message, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        });

                    }
                    else {
//                        String message = task.getException().toString();
//                        Toast.makeText(EditProfileActivity.this, "Error :" + message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void deleteUser() {
        usersRef.child(SelectedUserId).removeEventListener(listener);
        usersRef.child(SelectedUserId).addValueEventListener(deleteListener);
    }

    //listenData
    ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            String Image = snapshot.child("image").getValue().toString();
            String Name = snapshot.child("name").getValue().toString();
            String PhoneNumber = snapshot.child("phoneNumber").getValue().toString();
            String FullAddress = snapshot.child("fullAddress").getValue().toString();
            String Domicile = snapshot.child("domicile").getValue().toString();

            String SubPhoneNumber = PhoneNumber.substring(3);

            Picasso.get().load(Image).into(DetailManageUserImage);
            DetailManageUserName.setText(Name);
            DetailManageUserPhoneNumber.setText(SubPhoneNumber);
            DetailManageUserAddress.setText(Domicile);
            DetailManageUserDetailAddress.setText(FullAddress);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    //deleteListener
    ValueEventListener deleteListener =new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            String imageUrl = snapshot.child("image").getValue().toString();

            pd.setMessage("Menghapus pengguna..");
            pd.show();

            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference imageRefFromUrl = firebaseStorage.getReferenceFromUrl(imageUrl);

            usersRef.child(SelectedUserId).removeEventListener(listener);
            usersRef.child(SelectedUserId).removeEventListener(deleteListener);

            imageRefFromUrl.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    pd.dismiss();
                    usersRef.child(SelectedUserId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            DetailManageUserActivity.super.onBackPressed();
                            Toast.makeText(DetailManageUserActivity.this, "Pengguna terhapus", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DetailManageUserActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(DetailManageUserActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
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
        return
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    }
}