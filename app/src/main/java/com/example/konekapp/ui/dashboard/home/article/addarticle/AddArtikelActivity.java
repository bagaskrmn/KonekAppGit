package com.example.konekapp.ui.dashboard.home.article.addarticle;

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
import com.example.konekapp.model.ArtikelModel;
import com.example.konekapp.model.NotificationModel;
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

public class AddArtikelActivity extends AppCompatActivity {

    private View decorView;

    private EditText AddTitleArtikel, AddSourceArtikel, AddDescriptionArtikel, AddSourceImageArtikel;
    private ImageView AddImageArtikel, AddArtikelBackAction;
    private Button BtnAddArtikelDone;
    private StorageReference articlePath, articleImagesRef;
    private DatabaseReference rootRef, articleRef, notificationRef;
    private ProgressDialog pd;

    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String date, artikelId, articleImageUrl, notificationKey, Title;

    private Uri resultUri;

    private ConstraintLayout AddImageArtikelConstraint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_artikel);

        AddSourceImageArtikel = findViewById(R.id.addSourceImageArtikel);
        AddImageArtikelConstraint = findViewById(R.id.addImageArtikelConstraint);
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
        articleRef = rootRef.child("article");
        articleImagesRef = FirebaseStorage.getInstance().getReference().child("articleImages");

        artikelId = rootRef.push().getKey();

        BtnAddArtikelDone.setEnabled(false);
        AddSourceImageArtikel.addTextChangedListener(textWatcher);
        AddTitleArtikel.addTextChangedListener(textWatcher);
        AddSourceArtikel.addTextChangedListener(textWatcher);
        AddDescriptionArtikel.addTextChangedListener(textWatcher);


        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility==0) {
                    decorView.setSystemUiVisibility(hideSystemBars());
                }
            }
        });

        notificationRef = rootRef.child("notification");
        notificationKey = rootRef.push().getKey();
//        systemNotificationImageRef = FirebaseStorage.getInstance().getReference().child("systemNotificationImage").child("konek_icon.png");
//
//        systemNotificationImageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
//            @Override
//            public void onComplete(@NonNull Task<Uri> task) {
//                systemNotificationImageUrl = task.getResult().toString();
//            }
//        });

        AddArtikelBackAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddArtikelActivity.super.onBackPressed();
            }
        });

        //Open Gallery and Crop
        AddImageArtikelConstraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(2,1)
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

                articlePath = articleImagesRef.child(artikelId+".jpg");
            }
        }
    }

    private void AddActivityDone() {
        Title = AddTitleArtikel.getText().toString();
        String Source = AddSourceArtikel.getText().toString();
        String Description = AddDescriptionArtikel.getText().toString();
        String SourceImage = AddSourceImageArtikel.getText().toString();

        if (resultUri==null) {
            Toast.makeText(this, "Gambar belum ditambahkan", Toast.LENGTH_SHORT).show();

        }
        else {
            //add empty checker here
            pd.setMessage("Mengunggah Artikel");
            pd.show();

            //put cropped uri image to storage
            articlePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        pd.dismiss();
                        articlePath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                articleImageUrl = task.getResult().toString();

                                ArtikelModel artikelModel = new ArtikelModel(Title, articleImageUrl, Source, date, Description, SourceImage);
                                

                                articleRef.child(artikelId).setValue(artikelModel)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                pd.dismiss();
                                                if(task.isSuccessful()) {
                                                    notifArticle();
                                                    Toast.makeText(AddArtikelActivity.this, "Artikel Berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                                                    AddArtikelActivity.super.onBackPressed();
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

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String SourceImage = AddSourceImageArtikel.getText().toString().trim();
            String Title = AddTitleArtikel.getText().toString().trim();
            String Source = AddSourceArtikel.getText().toString().trim();
            String Description = AddDescriptionArtikel.getText().toString().trim();
            BtnAddArtikelDone.setEnabled(!SourceImage.isEmpty() && !Title.isEmpty() &&
                    !Source.isEmpty() && !Description.isEmpty());
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

    private void notifArticle() {
        String title = "Artikel Baru";
        String kind = "0";

        NotificationModel notificationModel = new NotificationModel(title, Title, null, kind, date, articleImageUrl, true );

        notificationRef.child(notificationKey).setValue(notificationModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //
            }
        });
    }

}