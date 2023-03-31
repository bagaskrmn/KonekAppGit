package com.example.konekapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.konekapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

public class AddObatActivity extends AppCompatActivity {
    private ImageView AddImageObat, AddObatBackAction;
    private EditText AddNameObat, AddDescriptionObat;
    private Button BtnAddObatDone;
    private StorageReference ObatImagesRef, obatPath;
    private DatabaseReference rootRef, tanamanRef, obatRef;
    private ProgressDialog pd;
    private ConstraintLayout AddImageObatConstraint;
    private String tanamanId, obatId, obatImageUrl;

    private Uri resultUri;

    private View decorView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_obat);
        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility==0) {
                    decorView.setSystemUiVisibility(hideSystemBars());
                }
            }
        });

        AddImageObat = findViewById(R.id.addImageObat);
        AddObatBackAction = findViewById(R.id.addObatBackAction);
        AddNameObat = findViewById(R.id.addNameObat);
        AddDescriptionObat = findViewById(R.id.addDescriptionObat);
        BtnAddObatDone = findViewById(R.id.btnAddObatDone);

        //init progress dialog
        pd = new ProgressDialog(this);
        pd.setTitle("Please wait...");
        pd.setCanceledOnTouchOutside(false);

        Intent intent = getIntent();
        tanamanId = intent.getStringExtra("Key");

        rootRef = FirebaseDatabase.getInstance().getReference();
        tanamanRef = rootRef.child("Tanaman");
        ObatImagesRef = FirebaseStorage.getInstance().getReference().child("Obat Images");
        obatId = rootRef.push().getKey();
        obatRef = tanamanRef.child(tanamanId).child("Obat");

        AddImageObatConstraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(2,1)
                        .start(AddObatActivity.this);
            }
        });

        BtnAddObatDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddObatDone();
            }
        });

        AddObatBackAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddObatActivity.super.onBackPressed();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //result crop Image OK
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();

                //set Uri to ImageArtikel
                Picasso.get().load(resultUri).into(AddImageObat);

                obatPath = ObatImagesRef.child(obatId + ".jpg");
            }
        }
    }

    private void AddObatDone() {
        String Name = AddNameObat.getText().toString();
        String Description = AddDescriptionObat.getText().toString();

        pd.setMessage("Mengunggah Obat");
        pd.show();

        obatPath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    pd.dismiss();
                    obatPath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            obatImageUrl=task.getResult().toString();

                            pd.setMessage("Data Obat terunggah");
                            pd.show();

                            HashMap<String, Object> obatMap = new HashMap<>();
                            obatMap.put("Name", Name);
                            obatMap.put("Description", Description);
                            obatMap.put("Image", obatImageUrl);

                            obatRef.child(obatId).updateChildren(obatMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            pd.dismiss();
                                            if (task.isSuccessful()) {
                                                Toast.makeText(AddObatActivity.this, "Penyakit berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                                                Intent addObatDone = new Intent(AddObatActivity.this, PenyakitDanObatActivity.class);
                                                addObatDone.putExtra("Key", tanamanId);
                                                startActivity(addObatDone);
                                                finish();
                                            }
                                            else {
                                                String message = task.getException().toString();
                                                Toast.makeText(AddObatActivity.this, "Error : "+message, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    });
                }
                else {
                    pd.dismiss();
                    String message = task.getException().toString();
                    Toast.makeText(AddObatActivity.this, "Error :" + message, Toast.LENGTH_SHORT).show();
                }
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