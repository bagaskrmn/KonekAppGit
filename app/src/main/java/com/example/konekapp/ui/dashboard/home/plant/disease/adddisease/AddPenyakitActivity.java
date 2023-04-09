package com.example.konekapp.ui.dashboard.home.plant.disease.adddisease;

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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.konekapp.R;
import com.example.konekapp.model.PenyakitModel;
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

public class AddPenyakitActivity extends AppCompatActivity {

    private ImageView AddImagePenyakit, AddPenyakitBackAction;
    private EditText AddNamePenyakit, AddDescriptionPenyakit;
    private Button BtnAddPenyakitDone;
    private StorageReference diseaseImagesRef, penyakitPath;
    private DatabaseReference diseaseRef, rootRef, plantRef;
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
        plantRef = rootRef.child("plant");
        diseaseImagesRef = FirebaseStorage.getInstance().getReference().child("plantDiseaseImages");
        penyakitId = rootRef.push().getKey();
        diseaseRef = plantRef.child(tanamanId).child("disease");

        BtnAddPenyakitDone.setEnabled(false);
        AddNamePenyakit.addTextChangedListener(textWatcher);
        AddDescriptionPenyakit.addTextChangedListener(textWatcher);

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

                penyakitPath = diseaseImagesRef.child(penyakitId + ".jpg");
            }
        }
    }

    private void AddPenyakitDone() {
        String Name = AddNamePenyakit.getText().toString();
        String Description = AddDescriptionPenyakit.getText().toString().trim();

        if (resultUri==null) {
            Toast.makeText(this, "Gambar belum ditambahkan", Toast.LENGTH_SHORT).show();
        }
        else {
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

                                PenyakitModel penyakitModel = new PenyakitModel(penyakitImageUrl, Name, Description);

                                diseaseRef.child(penyakitId).setValue(penyakitModel)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                pd.dismiss();
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(AddPenyakitActivity.this, "Penyakit berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                                                    AddPenyakitActivity.super.onBackPressed();
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
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String Name = AddNamePenyakit.getText().toString().trim();
            String Description = AddDescriptionPenyakit.getText().toString().trim();
            BtnAddPenyakitDone.setEnabled(!Name.isEmpty() && !Description.isEmpty());
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
}