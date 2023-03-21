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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AddArtikelActivity extends AppCompatActivity {

    private EditText AddTitleArtikel, AddSourceArtikel, AddDescriptionArtikel;
    private ImageView AddImageArtikel, AddArtikelBackAction;
    private Button BtnAddArtikelDone;
    private StorageReference ArtikelImagesRef, artikelPath;
    private DatabaseReference rootRef, artikelRef;
    private ProgressDialog pd;

    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String date, artikelId, artikelImageUrl;

    private Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_artikel);

        AddTitleArtikel = findViewById(R.id.addTitleArtikel);
        AddSourceArtikel = findViewById(R.id.addSourceArtikel);
        AddDescriptionArtikel = findViewById(R.id.addDescriptionArtikel);
        AddImageArtikel = findViewById(R.id.addImageArtikel);
        AddArtikelBackAction = findViewById(R.id.addArtikelBackAction);
        BtnAddArtikelDone = findViewById(R.id.btnAddArtikelDone);

        //init progress dialog
        pd = new ProgressDialog(this);
        pd.setTitle("Please wait...");
        pd.setCanceledOnTouchOutside(false);

        //date
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        date = dateFormat.format(calendar.getTime());

        //database and storage
        rootRef = FirebaseDatabase.getInstance().getReference();
        artikelRef = rootRef.child("Artikel");
        ArtikelImagesRef = FirebaseStorage.getInstance().getReference().child("Artikel Images");

        artikelId = rootRef.push().getKey();

        AddArtikelBackAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddArtikelActivity.super.onBackPressed();
            }
        });

        //Open Gallery and Crop
        AddImageArtikel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(AddArtikelActivity.this);
            }
        });
        
        BtnAddArtikelDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddActivityDone();
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
                Picasso.get().load(resultUri).into(AddImageArtikel);

                artikelPath = ArtikelImagesRef.child(artikelId+".jpg");
                Log.d("AddArtikel", artikelPath.toString());
            }
        }
    }

    private void AddActivityDone() {
        String Title = AddTitleArtikel.getText().toString();
        String Source = AddSourceArtikel.getText().toString();
        String Description = AddDescriptionArtikel.getText().toString();

        //add empty checker here
        pd.setMessage("Mengunggah Artikel");
        pd.show();

        //put cropped uri image to storage
        artikelPath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    pd.dismiss();
                    artikelPath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            artikelImageUrl = task.getResult().toString();

                            pd.setMessage("Artikel terunggah");
                            pd.show();

                            HashMap<String, Object> artikelMap = new HashMap<>();
                            artikelMap.put("Title", Title);
                            artikelMap.put("Source", Source);
                            artikelMap.put("Date", date);
                            artikelMap.put("Image", artikelImageUrl);
                            artikelMap.put("Description", Description);



                            artikelRef.child(artikelId).updateChildren(artikelMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            pd.dismiss();
                                            if(task.isSuccessful()) {
                                                Toast.makeText(AddArtikelActivity.this, "Artikel Berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                                                Intent addArtikelDone = new Intent(AddArtikelActivity.this, ArtikelActivity.class);
                                                startActivity(addArtikelDone);
                                            }
                                            else {
                                                String message = task.getException().toString();
                                                Toast.makeText(AddArtikelActivity.this, "Error : "+message, Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });
                        }
                    });
                }
                else {
                    pd.dismiss();
                    String message = task.getException().toString();
                    Toast.makeText(AddArtikelActivity.this, "Error :" + message, Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}