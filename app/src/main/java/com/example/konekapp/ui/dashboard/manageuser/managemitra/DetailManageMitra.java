package com.example.konekapp.ui.dashboard.manageuser.managemitra;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.konekapp.R;
import com.example.konekapp.model.NotificationModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailManageMitra extends AppCompatActivity {

    private CircleImageView DetailManageMitraProfImage;
    private TextView DetailManageMitraName, DetailManageMitraPhoneNumber, DetailManageMitraNIK, DetailManageMitraEmail,
            DetailManageMitraFullAddress, DetailManageMitraVillage, DetailManageMitraSubdistrict,
            DetailManageMitraCity, DetailManageMitraProvince, DetailManageMitraQuestion1, DetailManageMitraQuestion2;
    private ImageView DetailManageMitraIdCard, DetailManageMitraBackAction;
    private Button BtnApproveMitra, BtnDecilneMitra;
    private ProgressDialog pd;
    private String SelectedUserId;

    private DatabaseReference usersRef, rootRef;

    private View decorView;

    //date
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String date;

    private StorageReference systemNotificationImageRef;
    private String systemNotificationImageUrl, notificationKey;
    private DatabaseReference notificationRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_manage_mitra);

        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility==0) {
                    decorView.setSystemUiVisibility(hideSystemBars());
                }
            }
        });


        DetailManageMitraProfImage = findViewById(R.id.detailManageMitraProfImage);
        DetailManageMitraName = findViewById(R.id.detailManageMitraName);
        DetailManageMitraPhoneNumber = findViewById(R.id.detailManageMitraPhoneNumber);
        DetailManageMitraNIK = findViewById(R.id.detailManageMitraNIK);
        DetailManageMitraEmail = findViewById(R.id.detailManageMitraEmail);
        DetailManageMitraFullAddress = findViewById(R.id.detailManageMitraFullAddress);
        DetailManageMitraVillage = findViewById(R.id.detailManageMitraVillage);
        DetailManageMitraSubdistrict = findViewById(R.id.detailManageMitraSubdistrict);
        DetailManageMitraCity = findViewById(R.id.detailManageMitraCity);
        DetailManageMitraProvince = findViewById(R.id.detailManageMitraProvince);
        DetailManageMitraQuestion1 = findViewById(R.id.detailManageMitraQuestion1);
        DetailManageMitraQuestion2 = findViewById(R.id.detailManageMitraQuestion2);
        DetailManageMitraIdCard = findViewById(R.id.detailManageMitraIdCard);

        DetailManageMitraBackAction = findViewById(R.id.detailManageMitraBackAction);

        BtnApproveMitra = findViewById(R.id.btnApproveMitra);
        BtnDecilneMitra = findViewById(R.id.btnDecilneMitra);

        rootRef = FirebaseDatabase.getInstance().getReference();
        usersRef = rootRef.child("users");

        notificationRef = rootRef.child("notification");
        notificationKey = rootRef.push().getKey();
        systemNotificationImageRef = FirebaseStorage.getInstance().getReference().child("systemNotificationImage").child("konek_icon.png");

        systemNotificationImageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                systemNotificationImageUrl = task.getResult().toString();
            }
        });

        //calendar
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        date = dateFormat.format(calendar.getTime());

        DetailManageMitraBackAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailManageMitra.super.onBackPressed();
            }
        });

        Intent intent = getIntent();
        SelectedUserId = intent.getStringExtra("Key");

        if (SelectedUserId== null) {
            Intent i = new Intent(DetailManageMitra.this, ManageMitra.class);
            startActivity(i);
            finish();
        } else {
            usersRef.child(SelectedUserId).addValueEventListener(listener);
        }

        //init progress dialog
        pd = new ProgressDialog(this);
        pd.setTitle("Please wait...");
        pd.setCanceledOnTouchOutside(false);

        BtnDecilneMitra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> declineMap = new HashMap<>();
                declineMap.put("role", "0");

                usersRef.child(SelectedUserId).updateChildren(declineMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        declineNotification();
                        DetailManageMitra.super.onBackPressed();
                        Toast.makeText(DetailManageMitra.this, "Pengguna ditolak", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        BtnApproveMitra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> declineMap = new HashMap<>();
                declineMap.put("role", "1");

                usersRef.child(SelectedUserId).updateChildren(declineMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        approvedNotification();
                        DetailManageMitra.super.onBackPressed();
                        Toast.makeText(DetailManageMitra.this, "Pengguna disetujui", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            pd.dismiss();
            String Image = snapshot.child("image").getValue().toString();
            String Name = snapshot.child("name").getValue().toString();
            String PhoneNumber = snapshot.child("phoneNumber").getValue().toString();
            String NIK = snapshot.child("nik").getValue().toString();
            String Email = snapshot.child("email").getValue().toString();
            String FullAddress = snapshot.child("fullAddress").getValue().toString();
            String Village = snapshot.child("village").getValue().toString();
            String Subdistrict = snapshot.child("subdistrict").getValue().toString();
            String City = snapshot.child("city").getValue().toString();
            String Province = snapshot.child("province").getValue().toString();
            String IdCardImage = snapshot.child("idCardImage").getValue().toString();
            String Question1 = snapshot.child("question1").getValue().toString();
            String Question2 = snapshot.child("question2").getValue().toString();

            String SubPhoneNumber = PhoneNumber.substring(3);

            Picasso.get().load(Image).into(DetailManageMitraProfImage);
            DetailManageMitraName.setText(Name);
            DetailManageMitraPhoneNumber.setText(SubPhoneNumber);
            DetailManageMitraNIK.setText(NIK);
            DetailManageMitraEmail.setText(Email);
            DetailManageMitraFullAddress.setText(FullAddress);
            DetailManageMitraVillage.setText(Village);
            DetailManageMitraSubdistrict.setText(Subdistrict);
            DetailManageMitraCity.setText(City);
            DetailManageMitraProvince.setText(Province);
            Picasso.get().load(IdCardImage).into(DetailManageMitraIdCard);
            DetailManageMitraQuestion1.setText(Question1);
            DetailManageMitraQuestion2.setText(Question2);

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
        return
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    }

    private void declineNotification() {
        String title = "Mohon Maaf";
        String description ="Pengajuan mitra anda ditolak";
        int kind = 3;

        NotificationModel notificationModel = new NotificationModel(title, description, SelectedUserId, kind, date, systemNotificationImageUrl,false);

        notificationRef.child(notificationKey).setValue(notificationModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //
                    }
                });
    }

    private void approvedNotification() {
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

}