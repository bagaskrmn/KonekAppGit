package com.example.konekapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
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
import com.theartofdev.edmodo.cropper.CropImageActivity;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class EditArtikelActivity extends AppCompatActivity {

    private ConstraintLayout EditImageArtikelConstraint;
    private EditText EditTitleArtikel, EditSourceArtikel, EditDescriptionArtikel;
    private ImageView EditImageArtikel, EditArtikelBackAction;
    private Button BtnEditArtikelDone;
    private StorageReference ArtikelImagesRef, artikelPath;
    private DatabaseReference rootRef, artikelRef;
    private ProgressDialog pd;

//    private Calendar calendar;
//    private SimpleDateFormat dateFormat;
    private String date, artikelImageUrl, DetailKey;

    private Uri resultUri;

    private View decorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_artikel);

        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility==0) {
                    decorView.setSystemUiVisibility(hideSystemBars());
                }
            }
        });

        EditImageArtikelConstraint = findViewById(R.id.editImageArtikelConstraint);
        EditTitleArtikel = findViewById(R.id.editTitleArtikel);
        EditSourceArtikel = findViewById(R.id.editSourceArtikel);
        EditDescriptionArtikel = findViewById(R.id.editDescriptionArtikel);
        EditImageArtikel = findViewById(R.id.editImageArtikel);
        EditArtikelBackAction = findViewById(R.id.editArtikelBackAction);
        BtnEditArtikelDone = findViewById(R.id.btnEditArtikelDone);

        //init progress dialog
        pd = new ProgressDialog(this);
        pd.setTitle("Please wait...");
        pd.setCanceledOnTouchOutside(false);

        //date
//        calendar = Calendar.getInstance();
//        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
//        date = dateFormat.format(calendar.getTime());

        //database and storage
        rootRef = FirebaseDatabase.getInstance().getReference();
        artikelRef = rootRef.child("Artikel");
        ArtikelImagesRef = FirebaseStorage.getInstance().getReference().child("Artikel Images");

//        artikelId = rootRef.push().getKey();

        //recieve StringExtra from previous Activity via Intent
        Intent intent = getIntent();
        DetailKey = intent.getStringExtra("Key");
        //listener for retrieve data from database
        artikelRef.child(DetailKey).addValueEventListener(listener);

        EditArtikelBackAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditArtikelActivity.super.onBackPressed();
            }
        });

        EditImageArtikelConstraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(EditArtikelActivity.this);
            }
        });

        BtnEditArtikelDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditArtikelActivityDone();
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
                Picasso.get().load(resultUri).into(EditImageArtikel);

                artikelPath = ArtikelImagesRef.child(DetailKey+".jpg");
                Log.d("AddArtikel", artikelPath.toString());
            }
        }
    }

    private void EditArtikelActivityDone() {
        String EditTitle = EditTitleArtikel.getText().toString();
        String EditSource = EditSourceArtikel.getText().toString();
        String EditDescription = EditDescriptionArtikel.getText().toString();

        //if image artikel not changed
        if (resultUri==null) {
            pd.setMessage("Mengubah Artikel");
            pd.show();

            HashMap<String, Object> artikelMap = new HashMap<>();
            artikelMap.put("Title", EditTitle);
            artikelMap.put("Source", EditSource);
            artikelMap.put("Description", EditDescription);

            artikelRef.child(DetailKey).updateChildren(artikelMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                pd.dismiss();
                                Toast.makeText(EditArtikelActivity.this, "Ubah Artikel Berhasil", Toast.LENGTH_SHORT).show();
                                Intent EditArtikelDone = new Intent(EditArtikelActivity.this, DetailArtikelActivity.class);
                                EditArtikelDone.putExtra("Key", DetailKey);
                                startActivity(EditArtikelDone);
                                finish();
                            }
                            else {
                                pd.dismiss();
                                String message = task.getException().toString();
                                Toast.makeText(EditArtikelActivity.this, "Error : "+message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        //if image artikel changed
        else {
            pd.setMessage("Mengubah Artikel");
            pd.show();
            artikelPath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        pd.dismiss();
                        artikelPath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                artikelImageUrl = task.getResult().toString();

                                pd.setMessage("Artikel Terubah");
                                pd.show();
                                HashMap<String, Object> artikelMap = new HashMap<>();
                                artikelMap.put("Title", EditTitle);
                                artikelMap.put("Source", EditSource);
                                artikelMap.put("Description", EditDescription);
                                artikelMap.put("Image", artikelImageUrl);

                                artikelRef.child(DetailKey).updateChildren(artikelMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    pd.dismiss();
                                                    Toast.makeText(EditArtikelActivity.this, "Ubah Artikel Berhasil", Toast.LENGTH_SHORT).show();
                                                    Intent EditArtikelDone = new Intent(EditArtikelActivity.this, DetailArtikelActivity.class);
                                                    EditArtikelDone.putExtra("Key", DetailKey);
                                                    startActivity(EditArtikelDone);
                                                    finish();
                                                }
                                                else {
                                                    pd.dismiss();
                                                    String message = task.getException().toString();
                                                    Toast.makeText(EditArtikelActivity.this, "Error : "+message, Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        });
                            }
                        });
                    }
                }
            });
        }
    }

    ValueEventListener listener=new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            //retrieve Data Artikel
            String retrieveTitle = snapshot.child("Title").getValue().toString();
            String retrieveImage = snapshot.child("Image").getValue().toString();
            String retrieveSource = snapshot.child("Source").getValue().toString();
            String retrieveDescription = snapshot.child("Description").getValue().toString();

            //set Data to the item
            EditTitleArtikel.setText(retrieveTitle);
            Picasso.get().load(retrieveImage).into(EditImageArtikel);
            EditSourceArtikel.setText(retrieveSource);
            EditDescriptionArtikel.setText(retrieveDescription);
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
        return View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    }
}