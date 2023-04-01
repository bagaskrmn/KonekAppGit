package com.example.konekapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
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

public class UpdateMitraProfileActivity extends AppCompatActivity {

    private CircleImageView UpdateProfMitraImage;
    private ImageView UpdateProfMitraBackAction, UpdateProfMitraDocument;
    private TextView UpdateProfMitraPhoneNumber;
    private EditText UpdateProfMitraName, UpdateProfMitraNIK, UpdateProfMitraEmail,
            UpdateProfMitraFullAddress, UpdateProfMitraVillage, UpdateProfMitraSubdistrict,
            UpdateProfMitraCity, UpdateProfMitraProvince, UpdateProfMitraQuestion1, UpdateProfMitraQuestion2;
    private Button BtnUpdateProfMitraDone;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference rootRef, usersRef;
    private String currentUserId, phoneNumber, profileUrl, removedPhoneNumber;
    private ProgressDialog pd;
    private StorageReference UserProfileImagesRef, filePath;

    private Boolean isAllFieldsChecked = false;

    private Uri resultUri;

    private View decorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_mitra_profile);
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
        UserProfileImagesRef = FirebaseStorage.getInstance().getReference().child("profileImages");
        rootRef = FirebaseDatabase.getInstance().getReference();
        usersRef = rootRef.child("users");

        UpdateProfMitraImage = findViewById(R.id.updateProfMitraImage);
        UpdateProfMitraBackAction = findViewById(R.id.updateProfMitraBackAction);
        UpdateProfMitraDocument = findViewById(R.id.updateProfMitraDocument);
        BtnUpdateProfMitraDone = findViewById(R.id.btnUpdateProfMitraDone);
        UpdateProfMitraPhoneNumber = findViewById(R.id.updateProfMitraPhoneNumber);
        UpdateProfMitraName = findViewById(R.id.updateProfMitraName);
        UpdateProfMitraNIK = findViewById(R.id.updateProfMitraNIK);
        UpdateProfMitraEmail = findViewById(R.id.updateProfMitraEmail);
        UpdateProfMitraFullAddress = findViewById(R.id.updateProfMitraFullAddress);
        UpdateProfMitraVillage = findViewById(R.id.updateProfMitraVillage);
        UpdateProfMitraSubdistrict = findViewById(R.id.updateProfMitraSubdistrict);
        UpdateProfMitraCity = findViewById(R.id.updateProfMitraCity);
        UpdateProfMitraProvince = findViewById(R.id.updateProfMitraProvince);
        UpdateProfMitraQuestion1 = findViewById(R.id.updateProfMitraQuestion1);
        UpdateProfMitraQuestion2 = findViewById(R.id.updateProfMitraQuestion2);

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

                Picasso.get().load(Image).into(UpdateProfMitraImage);
                UpdateProfMitraName.setText(Name);
                UpdateProfMitraPhoneNumber.setText(removedPhoneNumber);
                UpdateProfMitraNIK.setText(NIK);
                UpdateProfMitraEmail.setText(Email);
                UpdateProfMitraFullAddress.setText(FullAddress);
                UpdateProfMitraVillage.setText(Village);
                UpdateProfMitraSubdistrict.setText(Subdistrict);
                UpdateProfMitraCity.setText(City);
                UpdateProfMitraProvince.setText(Province);
                Picasso.get().load(IdCardImage).into(UpdateProfMitraDocument);
                UpdateProfMitraQuestion1.setText(Question1);
                UpdateProfMitraQuestion2.setText(Question2);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pd.dismiss();
                Toast.makeText(UpdateMitraProfileActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        UpdateProfMitraImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(UpdateMitraProfileActivity.this);
            }
        });

        BtnUpdateProfMitraDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateProfileMitraDone();
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
                Picasso.get().load(resultUri).into(UpdateProfMitraImage);

                filePath = UserProfileImagesRef.child(currentUserId + ".jpg");
                Log.d("CompleteProfile", filePath.toString());
            }
        }
    }

    private void UpdateProfileMitraDone() {
        String Name = UpdateProfMitraName.getText().toString();
        String NIK = UpdateProfMitraNIK.getText().toString();
        String Email = UpdateProfMitraEmail.getText().toString();
        String FullAddress = UpdateProfMitraFullAddress.getText().toString();
        String Village = UpdateProfMitraVillage.getText().toString();
        String Subdistrict = UpdateProfMitraSubdistrict.getText().toString();
        String City = UpdateProfMitraCity.getText().toString();
        String Province = UpdateProfMitraProvince.getText().toString();
        String Question1 = UpdateProfMitraQuestion1.getText().toString();
        String Question2 = UpdateProfMitraQuestion2.getText().toString();

        //check empty
        if (TextUtils.isEmpty(Name) || TextUtils.isEmpty(NIK) || TextUtils.isEmpty(Email)
                || TextUtils.isEmpty(FullAddress) || TextUtils.isEmpty(Village) || TextUtils.isEmpty(Subdistrict)
                || TextUtils.isEmpty(City) || TextUtils.isEmpty(Province)) {
            Toast.makeText(this, "Data tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        isAllFieldsChecked = checkAllFields();

        //jika gambar tidak diganti
        if (resultUri == null && isAllFieldsChecked) {
            pd.setMessage("Mengunggah Data");
            pd.show();

            HashMap<String, Object> profileMap = new HashMap<>();
            profileMap.put("name", Name);
            profileMap.put("nik", NIK);
            profileMap.put("email", Email);
            profileMap.put("fullAddress", FullAddress);
            profileMap.put("village", Village);
            profileMap.put("subdistrict", Subdistrict);
            profileMap.put("city", City);
            profileMap.put("province", Province);
            profileMap.put("question1", Question1);
            profileMap.put("question2", Question2);

            usersRef.child(currentUserId).updateChildren(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            pd.dismiss();
                            if (task.isSuccessful()) {
                                Toast.makeText(UpdateMitraProfileActivity.this, "Update berhasil", Toast.LENGTH_SHORT).show();
                                Intent toProfileIntent = new Intent(UpdateMitraProfileActivity.this, MitraProfileActivity.class);
                                startActivity(toProfileIntent);
                                finish();
                            }
                            else {
                                String message = task.getException().toString();
                                Toast.makeText(UpdateMitraProfileActivity.this, "Error : "+message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            return;
        }

        //jika gambar diupdate
        if(isAllFieldsChecked) {
            pd.setMessage("Mengunggah data");
            pd.show();

            filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    pd.dismiss();
                    if (task.isSuccessful()) {
                        filePath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                profileUrl = task.getResult().toString();

                                pd.setMessage("Data terunggah");
                                pd.show();

                                HashMap<String, Object> profileMap = new HashMap<>();
                                profileMap.put("image", profileUrl);
                                profileMap.put("name", Name);
                                profileMap.put("nik", NIK);
                                profileMap.put("email", Email);
                                profileMap.put("fullAddress", FullAddress);
                                profileMap.put("village", Village);
                                profileMap.put("subdistrict", Subdistrict);
                                profileMap.put("city", City);
                                profileMap.put("province", Province);
                                profileMap.put("question1", Question1);
                                profileMap.put("question2", Question2);

                                usersRef.child(currentUserId).updateChildren(profileMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                pd.dismiss();
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(UpdateMitraProfileActivity.this, "Update berhasil", Toast.LENGTH_SHORT).show();
                                                    Intent toProfileIntent = new Intent(UpdateMitraProfileActivity.this, MitraProfileActivity.class);
                                                    startActivity(toProfileIntent);
                                                    finish();
                                                }
                                                else {
                                                    String message = task.getException().toString();
                                                    Toast.makeText(UpdateMitraProfileActivity.this, "Error : "+message, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                            }
                        });
                    }
                    else {
                        String message = task.getException().toString();
                        Toast.makeText(UpdateMitraProfileActivity.this, "Error : "+message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private Boolean checkAllFields() {
        String NIK = UpdateProfMitraNIK.getText().toString().trim();
        String Email = UpdateProfMitraEmail.getText().toString().trim();

        if (NIK.length() != 16) {
            UpdateProfMitraNIK.requestFocus();
            UpdateProfMitraNIK.setError("NIK harus diisi 16 digit");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            UpdateProfMitraEmail.requestFocus();
            UpdateProfMitraEmail.setError("Masukkan alamat email yang tepat");
            return false;
        }
        return true;
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