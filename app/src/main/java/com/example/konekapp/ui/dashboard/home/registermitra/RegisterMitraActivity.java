package com.example.konekapp.ui.dashboard.home.registermitra;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class RegisterMitraActivity extends AppCompatActivity {

    private ConstraintLayout RegMitraDocumentConstraint;
    private TextView RegMitraPhoneNumber, RegMitraName, RegMitraFullAddress;
    private EditText RegMitraVillage, RegMitraSubdistrict, RegMitraCity, RegMitraProvince, RegMitraNIK, RegMitraEmail, RegMitraQuestion1, RegMitraQuestion2;
    private Button BtnRegMitraDone;
    private ImageView RegMitraBackAction, RegMitraDocument;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference rootRef, usersRef, notificationRef;
    private StorageReference idCardImagePath, idCardImagesRef, systemNotificationImageRef;

    private String idCardImageUrl, currentUserId, phoneNumber, removedPhoneNumber, systemNotificationImageUrl, notificationKey;
    private ProgressDialog pd;
    private Uri resultUri;

    private Boolean isAllFieldsChecked = false;

    private View decorView;

    //date
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_mitra);

        RegMitraName = findViewById(R.id.regMitraName);
        RegMitraPhoneNumber = findViewById(R.id.regMitraPhoneNumber);
        RegMitraNIK = findViewById(R.id.regMitraNIK);
        RegMitraEmail = findViewById(R.id.regMitraEmail);
        RegMitraFullAddress = findViewById(R.id.regMitraFullAddress);
        RegMitraVillage = findViewById(R.id.regMitraVillage);
        RegMitraSubdistrict = findViewById(R.id.regMitraSubdistrict);
        RegMitraCity = findViewById(R.id.regMitraCity);
        RegMitraProvince = findViewById(R.id.regMitraProvince);
        RegMitraDocumentConstraint = findViewById(R.id.regMitraDocumentConstraint);
        RegMitraDocument = findViewById(R.id.regiMitraDocument);
        RegMitraQuestion1 = findViewById(R.id.regMitraQuestion1);
        RegMitraQuestion2 = findViewById(R.id.regMitraQuestion2);
        RegMitraBackAction = findViewById(R.id.regMitraBackAction);
        BtnRegMitraDone = findViewById(R.id.btnRegMitraDone);

        BtnRegMitraDone.setEnabled(false);
        RegMitraNIK.addTextChangedListener(textWatcher);
        RegMitraEmail.addTextChangedListener(textWatcher);
        RegMitraVillage.addTextChangedListener(textWatcher);
        RegMitraSubdistrict.addTextChangedListener(textWatcher);
        RegMitraCity.addTextChangedListener(textWatcher);
        RegMitraProvince.addTextChangedListener(textWatcher);

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
        idCardImagesRef = FirebaseStorage.getInstance().getReference().child("idCardImages");
        notificationKey = rootRef.push().getKey();

        //calendar
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        date = dateFormat.format(calendar.getTime());

        notificationRef = rootRef.child("notification");
        systemNotificationImageRef = FirebaseStorage.getInstance().getReference().child("systemNotificationImage").child("konek_icon.png");

        systemNotificationImageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                systemNotificationImageUrl = task.getResult().toString();
            }
        });

        pd.setMessage("Memuat data anda");
        pd.show();

        //retrieve data to field
        usersRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String retrieveName = snapshot.child("name").getValue().toString();

                String retrieveFullAddress = snapshot.child("fullAddress").getValue().toString();

                RegMitraName.setText(retrieveName);
                RegMitraFullAddress.setText(retrieveFullAddress);
                RegMitraPhoneNumber.setText(removedPhoneNumber);

                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pd.dismiss();
                Toast.makeText(RegisterMitraActivity.this, ""+ error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        RegMitraDocumentConstraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open Gallery and Crop activity
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(2,1)
                        .start(RegisterMitraActivity.this);
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
                Picasso.get().load(resultUri).into(RegMitraDocument);

                idCardImagePath = idCardImagesRef.child(currentUserId + "_ktp.jpg");

            }
        }

    }


    private void RegMitraDone() {
        String NIK = RegMitraNIK.getText().toString();
        String Email = RegMitraEmail.getText().toString();
        String Village = RegMitraVillage.getText().toString();
        String Subdistrict = RegMitraSubdistrict.getText().toString();
        String City = RegMitraCity.getText().toString();
        String Province = RegMitraProvince.getText().toString();
        String Question1 = RegMitraQuestion1.getText().toString();
        String Question2 = RegMitraQuestion2.getText().toString();


        if (resultUri == null) {
            Toast.makeText(this, "Unggah dokumen KTP anda", Toast.LENGTH_SHORT).show();
            return;
        }

        isAllFieldsChecked = checkAllFields();

        if (isAllFieldsChecked) {
            pd.setMessage("Mengunggah Data");
            pd.show();

            //put croppedImage to firebase storage with filePath
            idCardImagePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    pd.dismiss();
                    if (task.isSuccessful()) {
                        idCardImagePath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                idCardImageUrl = task.getResult().toString();

                                pd.setMessage("Data terunggah");
                                pd.show();

                                HashMap<String, Object> profileMap = new HashMap<>();
                                profileMap.put("nik", NIK);
                                profileMap.put("email", Email);
                                profileMap.put("village", Village);
                                profileMap.put("subdistrict", Subdistrict);
                                profileMap.put("city", City);
                                profileMap.put("province", Province);
                                profileMap.put("idCardImage", idCardImageUrl);
                                profileMap.put("question1", Question1);
                                profileMap.put("question2", Question2);
                                profileMap.put("role", "4");

                                usersRef.child(currentUserId).updateChildren(profileMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                pd.dismiss();
                                                if (task.isSuccessful()) {
                                                    registerNotifToAdmin();
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
                        });
                    }
                    else {
                        String message = task.getException().toString();
                        Toast.makeText(RegisterMitraActivity.this, "Error :"+message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    private Boolean checkAllFields() {
        String NIK = RegMitraNIK.getText().toString().trim();
        String Email = RegMitraEmail.getText().toString().trim();

        if (NIK.length() != 16) {
            RegMitraNIK.requestFocus();
            RegMitraNIK.setError("NIK harus diisi 16 digit");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            RegMitraEmail.requestFocus();
            RegMitraEmail.setError("Masukkan alamat email yang tepat");
            return false;
        }
        return true;
    }

    //text Watcher for disable btn if any editText is empty
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String NIK = RegMitraNIK.getText().toString().trim();
            String Email = RegMitraEmail.getText().toString().trim();
            String Village = RegMitraVillage.getText().toString().trim();
            String Subdistrict = RegMitraSubdistrict.getText().toString().trim();
            String City = RegMitraCity.getText().toString().trim();
            String Province = RegMitraProvince.getText().toString().trim();

            BtnRegMitraDone.setEnabled(!NIK.isEmpty() && !Email.isEmpty() && !Village.isEmpty()
                    && !Subdistrict.isEmpty() && !City.isEmpty() && !Province.isEmpty());

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
        return View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    }


    private void registerNotifToAdmin() {
        String title = "Pendaftaran Mitra Baru";
        String description = "Terdapat mitra baru untuk ditinjau";
        int kind = 5;


        NotificationModel notificationModel = new NotificationModel(title, description, currentUserId, kind, date, systemNotificationImageUrl,false);

        notificationRef.child(notificationKey).setValue(notificationModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //
                    }
                });
    }
}