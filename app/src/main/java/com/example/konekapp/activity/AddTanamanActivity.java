package com.example.konekapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

public class AddTanamanActivity extends AppCompatActivity {
    private ImageView AddImageTanaman, AddTanamanBackAction;
    private EditText AddNameTanaman;
    private Button BtnAddTanamanDone;
    private StorageReference TanamanImagesRef, tanamanPath;
    private DatabaseReference rootRef, tanamanRef;
    private ProgressDialog pd;

    private String tanamanId, tanamanImageUrl;

    private Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tanaman);

        AddImageTanaman = findViewById(R.id.addImageTanaman);
        AddNameTanaman = findViewById(R.id.addNameTanaman);
        BtnAddTanamanDone = findViewById(R.id.btnAddTanamanDone);
        AddTanamanBackAction = findViewById(R.id.addTanamanBackAction);

        //init progress dialog
        pd = new ProgressDialog(this);
        pd.setTitle("Please wait...");
        pd.setCanceledOnTouchOutside(false);

        rootRef = FirebaseDatabase.getInstance().getReference();
        tanamanRef = rootRef.child("Tanaman");
        TanamanImagesRef = FirebaseStorage.getInstance().getReference().child("Tanaman Images");
        tanamanId = rootRef.push().getKey();

        AddImageTanaman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(2,1)
                        .start(AddTanamanActivity.this);
            }
        });

        BtnAddTanamanDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddTanamanDone();
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

                tanamanPath = TanamanImagesRef.child(tanamanId+".jpg");
                Log.d("AddArtikel", tanamanPath.toString());
            }
        }

    }

    private void AddTanamanDone() {
        String Name = AddNameTanaman.getText().toString();

        //add empty checker here
        pd.setMessage("Mengunggah Tanaman");
        pd.show();

        tanamanPath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    pd.dismiss();
                    tanamanPath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            tanamanImageUrl = task.getResult().toString();

                            pd.setMessage("Data tanaman terunggah");
                            pd.show();

                            HashMap<String, Object> tanamanMap = new HashMap<>();
                            tanamanMap.put("Name", Name);
                            tanamanMap.put("Image", tanamanImageUrl);

                            tanamanRef.child(tanamanId).updateChildren(tanamanMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    pd.dismiss();
                                    if (task.isSuccessful()) {
                                        Toast.makeText(AddTanamanActivity.this, "Tanaman berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                                        Intent addTanamanDoneIntent = new Intent(AddTanamanActivity.this, TanamanActivity.class);
                                        startActivity(addTanamanDoneIntent);
                                        finish();
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