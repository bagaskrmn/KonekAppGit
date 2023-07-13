package com.example.konekapp.ui.dashboard.manageuser.upgrademitra;

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
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.konekapp.R;
import com.example.konekapp.model.NotificationModel;
import com.example.konekapp.ui.dashboard.home.registermitra.RegisterMitraActivity;
import com.example.konekapp.ui.dashboard.home.registermitra.RegisterSuccessActivity;
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

public class DetailUpgradeMitraActivity extends AppCompatActivity {

    private ConstraintLayout UpgradeMitraDocumentConstraint;
    private TextView UpgradeMitraPhoneNumber, UpgradeMitraName, UpgradeMitraFullAddress;
    private EditText UpgradeMitraVillage, UpgradeMitraSubdistrict, UpgradeMitraCity, UpgradeMitraProvince,
            UpgradeMitraNIK, UpgradeMitraEmail, UpgradeMitraQuestion1, UpgradeMitraQuestion2;
    private Button BtnUpgradeMitraDone;
    private ImageView UpgradeMitraBackAction, UpgradeMitraDocument;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference rootRef, usersRef, notificationRef;
    private StorageReference idCardImagePath, idCardImagesRef, systemNotificationImageRef;

    private String idCardImageUrl, currentUserId, systemNotificationImageUrl, notificationKey, SelectedUserId;
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
        setContentView(R.layout.activity_detail_upgrade_mitra);

        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility==0) {
                    decorView.setSystemUiVisibility(hideSystemBars());
                }
            }
        });

        UpgradeMitraName = findViewById(R.id.upgradeMitraName);
        UpgradeMitraPhoneNumber = findViewById(R.id.upgradeMitraPhoneNumber);
        UpgradeMitraNIK = findViewById(R.id.upgradeMitraNIK);
        UpgradeMitraEmail = findViewById(R.id.upgradeMitraEmail);
        UpgradeMitraFullAddress = findViewById(R.id.upgradeMitraFullAddress);
        UpgradeMitraVillage = findViewById(R.id.upgradeMitraVillage);
        UpgradeMitraSubdistrict = findViewById(R.id.upgradeMitraSubdistrict);
        UpgradeMitraCity = findViewById(R.id.upgradeMitraCity);
        UpgradeMitraProvince = findViewById(R.id.upgradeMitraProvince);
        UpgradeMitraDocumentConstraint = findViewById(R.id.upgradeMitraDocumentConstraint);
        UpgradeMitraDocument = findViewById(R.id.upgradeMitraDocument);
        UpgradeMitraQuestion1 = findViewById(R.id.upgradeMitraQuestion1);
        UpgradeMitraQuestion2 = findViewById(R.id.upgradeMitraQuestion2);
        UpgradeMitraBackAction = findViewById(R.id.upgradeMitraBackAction);
        BtnUpgradeMitraDone = findViewById(R.id.btnUpgradeMitraDone);

        UpgradeMitraBackAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailUpgradeMitraActivity.super.onBackPressed();
            }
        });

        BtnUpgradeMitraDone.setEnabled(false);
        UpgradeMitraNIK.addTextChangedListener(textWatcher);
        UpgradeMitraEmail.addTextChangedListener(textWatcher);
        UpgradeMitraVillage.addTextChangedListener(textWatcher);
        UpgradeMitraSubdistrict.addTextChangedListener(textWatcher);
        UpgradeMitraCity.addTextChangedListener(textWatcher);
        UpgradeMitraProvince.addTextChangedListener(textWatcher);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        currentUserId = currentUser.getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        usersRef = rootRef.child("users");
        idCardImagesRef = FirebaseStorage.getInstance().getReference().child("idCardImages");
        notificationKey = rootRef.push().getKey();

        pd = new ProgressDialog(this);
        pd.setTitle("Please wait...");
        pd.setCanceledOnTouchOutside(false);

        //calendar
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        date = dateFormat.format(calendar.getTime());

        Intent intent = getIntent();
        SelectedUserId = intent.getStringExtra("Key");

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

        loadUserData();

        UpgradeMitraDocumentConstraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open Gallery and Crop activity
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(2,1)
                        .start(DetailUpgradeMitraActivity.this);
            }
        });

        BtnUpgradeMitraDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpgradeMitraDone();
            }
        });

    }

    private void UpgradeMitraDone() {
        String NIK = UpgradeMitraNIK.getText().toString();
        String Email = UpgradeMitraEmail.getText().toString();
        String Village = UpgradeMitraVillage.getText().toString();
        String Subdistrict = UpgradeMitraSubdistrict.getText().toString();
        String City = UpgradeMitraCity.getText().toString();
        String Province = UpgradeMitraProvince.getText().toString();
        String Question1 = UpgradeMitraQuestion1.getText().toString();
        String Question2 = UpgradeMitraQuestion2.getText().toString();


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
                                profileMap.put("role", "1");

                                usersRef.child(SelectedUserId).updateChildren(profileMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                pd.dismiss();
                                                if (task.isSuccessful()) {
                                                    notifToMitra();
                                                    DetailUpgradeMitraActivity.super.onBackPressed();
                                                    Toast.makeText(DetailUpgradeMitraActivity.this, "Registrasi selesai", Toast.LENGTH_SHORT).show();
//                                                    Intent registerMitraSuccess = new Intent(RegisterMitraActivity.this, RegisterSuccessActivity.class);
//                                                    startActivity(registerMitraSuccess);
//                                                    finish();
                                                }
                                                else {
                                                    String message = task.getException().toString();
                                                    Toast.makeText(DetailUpgradeMitraActivity.this, "Error : "+message, Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        });
                            }
                        });
                    }
                    else {
                        String message = task.getException().toString();
                        Toast.makeText(DetailUpgradeMitraActivity.this, "Error :"+message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    private void loadUserData() {
        usersRef.child(SelectedUserId).addValueEventListener(userListener);
    }

    //listenerUserData
    ValueEventListener userListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            String retrieveName = snapshot.child("name").getValue().toString();
            String phoneNumber = snapshot.child("phoneNumber").getValue().toString();
            String removedPhoneNumber = phoneNumber.substring(3);

            String retrieveFullAddress = snapshot.child("fullAddress").getValue().toString();

            UpgradeMitraName.setText(retrieveName);
            UpgradeMitraFullAddress.setText(retrieveFullAddress);
            UpgradeMitraPhoneNumber.setText(removedPhoneNumber);

            pd.dismiss();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            pd.dismiss();
        }
    };

    private Boolean checkAllFields() {
        String NIK = UpgradeMitraNIK.getText().toString().trim();
        String Email = UpgradeMitraEmail.getText().toString().trim();

        if (NIK.length() != 16) {
            UpgradeMitraNIK.requestFocus();
            UpgradeMitraNIK.setError("NIK harus diisi 16 digit");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            UpgradeMitraEmail.requestFocus();
            UpgradeMitraEmail.setError("Masukkan alamat email yang tepat");
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
            String NIK = UpgradeMitraNIK.getText().toString().trim();
            String Email = UpgradeMitraEmail.getText().toString().trim();
            String Village = UpgradeMitraVillage.getText().toString().trim();
            String Subdistrict = UpgradeMitraSubdistrict.getText().toString().trim();
            String City = UpgradeMitraCity.getText().toString().trim();
            String Province = UpgradeMitraProvince.getText().toString().trim();

            BtnUpgradeMitraDone.setEnabled(!NIK.isEmpty() && !Email.isEmpty() && !Village.isEmpty()
                    && !Subdistrict.isEmpty() && !City.isEmpty() && !Province.isEmpty());

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

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
                Picasso.get().load(resultUri).into(UpgradeMitraDocument);

                idCardImagePath = idCardImagesRef.child(SelectedUserId + "_ktp.jpg");

            }
        }

    }

    private void notifToMitra() {
        String title = "Selamat";
        String description ="Pengajuan mitra anda disetujui. Anda telah tergabung sebagai Petani Mitra";
        int kind = 2;

        NotificationModel notificationModel = new NotificationModel(title, description, SelectedUserId, kind, date, systemNotificationImageUrl,false);

        notificationRef.child(notificationKey).setValue(notificationModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //
                    }
                });
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