package com.example.konekapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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

public class AddPenyakitActivity extends AppCompatActivity {

    private ImageView AddImagePenyakit, AddPenyakitBackAction;
    private EditText AddNamePenyakit, AddDescriptionPenyakit;
    private Button BtnAddPenyakitDone;
    private StorageReference PenyakitImagesRef, penyakitPath;
    private DatabaseReference rootRef, tanamanRef, penyakitRef;
    private ProgressDialog pd;
    private ConstraintLayout AddImagePenyakitConstraint;
    private String tanamanId, penyakitId, penyakitImageUrl;

    private Uri resultUri;

    private View decorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_penyakit);

        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility==0) {
                    decorView.setSystemUiVisibility(hideSystemBars());
                }
            }
        });

        AddImagePenyakitConstraint = findViewById(R.id.addImagePenyakitConstraint);
        AddImagePenyakit = findViewById(R.id.addImagePenyakit);
        AddPenyakitBackAction = findViewById(R.id.addPenyakitBackAction);
        AddNamePenyakit = findViewById(R.id.addNamePenyakit);
        AddDescriptionPenyakit = findViewById(R.id.addDescriptionPenyakit);
        BtnAddPenyakitDone = findViewById(R.id.btnAddPenyakitDone);

        //init progress dialog
        pd = new ProgressDialog(this);
        pd.setTitle("Please wait...");
        pd.setCanceledOnTouchOutside(false);

        Intent intent = getIntent();
        tanamanId = intent.getStringExtra("Key");

        rootRef = FirebaseDatabase.getInstance().getReference();
        tanamanRef = rootRef.child("plant");
        PenyakitImagesRef = FirebaseStorage.getInstance().getReference().child("plantDiseaseImages");
        penyakitId = rootRef.push().getKey();
        penyakitRef = tanamanRef.child(tanamanId).child("disease");

        AddImagePenyakitConstraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(2,1)
                        .start(AddPenyakitActivity.this);
            }
        });

        BtnAddPenyakitDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPenyakitDone();
            }
        });

        AddPenyakitBackAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPenyakitActivity.super.onBackPressed();
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
                Picasso.get().load(resultUri).into(AddImagePenyakit);

                penyakitPath = PenyakitImagesRef.child(penyakitId + ".jpg");
            }
        }
    }

    private void AddPenyakitDone() {
        String Name = AddNamePenyakit.getText().toString();
        String Description = AddDescriptionPenyakit.getText().toString().trim();

        pd.setMessage("Mengunggah Penyakit");
        pd.show();

        penyakitPath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    pd.dismiss();
                    penyakitPath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            penyakitImageUrl = task.getResult().toString();

                            pd.setMessage("Data penyakit terunggah");
                            pd.show();

                            HashMap<String, Object> penyakitMap = new HashMap<>();
                            penyakitMap.put("name", Name);
                            penyakitMap.put("description", Description);
                            penyakitMap.put("image", penyakitImageUrl);

                            penyakitRef.child(penyakitId).updateChildren(penyakitMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            pd.dismiss();
                                            if (task.isSuccessful()) {
                                                Toast.makeText(AddPenyakitActivity.this, "Penyakit berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                                                Intent addPenyakitDone = new Intent(AddPenyakitActivity.this, PenyakitDanObatActivity.class);
                                                addPenyakitDone.putExtra("Key", tanamanId);
                                                //add put string extra?
                                                startActivity(addPenyakitDone);
                                                finish();
                                            }
                                            else {
                                                String message = task.getException().toString();
                                                Toast.makeText(AddPenyakitActivity.this, "Error : "+message, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    });
                }
                else {
                    pd.dismiss();
                    String message = task.getException().toString();
                    Toast.makeText(AddPenyakitActivity.this, "Error :" + message, Toast.LENGTH_SHORT).show();
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