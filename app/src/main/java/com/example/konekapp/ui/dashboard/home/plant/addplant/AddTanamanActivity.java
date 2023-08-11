package com.example.konekapp.ui.dashboard.home.plant.addplant;


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
import com.example.konekapp.model.TanamanModel;
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

public class AddTanamanActivity extends AppCompatActivity {
    private ImageView AddImageTanaman, AddTanamanBackAction;
    private EditText AddNameTanaman;
    private Button BtnAddTanamanDone;
    private StorageReference plantPath, plantImagesRef;
    private DatabaseReference rootRef, plantRef;
    private ProgressDialog pd;
    private ConstraintLayout AddImageTanamanConstraint;
    private View decorView;

    private String plantId, tanamanImageUrl;

    private Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tanaman);

        AddImageTanamanConstraint = findViewById(R.id.addImageTanamanConstraint);
        AddImageTanaman = findViewById(R.id.addImageTanaman);
        AddNameTanaman = findViewById(R.id.addNameTanaman);
        BtnAddTanamanDone = findViewById(R.id.btnAddTanamanDone);
        AddTanamanBackAction = findViewById(R.id.addTanamanBackAction);


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

        rootRef = FirebaseDatabase.getInstance().getReference();
        plantRef = rootRef.child("plant");
        plantImagesRef = FirebaseStorage.getInstance().getReference().child("plantImages");
        plantId = rootRef.push().getKey();

        BtnAddTanamanDone.setEnabled(false);
        AddNameTanaman.addTextChangedListener(textWatcher);

        AddImageTanamanConstraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(AddTanamanActivity.this);
            }
        });

        BtnAddTanamanDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddTanamanDone();
            }
        });

        AddTanamanBackAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddTanamanActivity.super.onBackPressed();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //result crop Image OK
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK) {
                resultUri = result.getUri();

                //set Uri to ImageArtikel
                Picasso.get().load(resultUri).into(AddImageTanaman);

                plantPath = plantImagesRef.child(plantId+".jpg");
            }
        }

    }

    private void AddTanamanDone() {
        String Name = AddNameTanaman.getText().toString();

        if (resultUri==null) {
            Toast.makeText(this, "Gambar belum ditambahkan", Toast.LENGTH_SHORT).show();
        }
        else {
            pd.setMessage("Mengunggah Tanaman");
            pd.show();

            plantPath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        pd.dismiss();
                        plantPath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                tanamanImageUrl = task.getResult().toString();

                                pd.setMessage("Data tanaman terunggah");
                                pd.show();

                                TanamanModel tanamanModel = new TanamanModel(tanamanImageUrl, Name);

                                plantRef.child(plantId).setValue(tanamanModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        pd.dismiss();
                                        if (task.isSuccessful()) {
                                            Toast.makeText(AddTanamanActivity.this, "Tanaman berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                                            AddTanamanActivity.super.onBackPressed();
                                        }
                                        else {
                                            String message = task.getException().toString();
                                            Toast.makeText(AddTanamanActivity.this, "Error : "+message, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                    else {
                        pd.dismiss();
                        String message = task.getException().toString();
                        Toast.makeText(AddTanamanActivity.this, "Error :" + message, Toast.LENGTH_SHORT).show();
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
            String NameTanaman = AddNameTanaman.getText().toString().trim();
            BtnAddTanamanDone.setEnabled(!NameTanaman.isEmpty());
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

}