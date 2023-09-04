package com.example.konekapp.ui.dashboard.home.crops.adminandahlitanicrops;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.konekapp.R;
import com.example.konekapp.model.NotificationModel;
import com.example.konekapp.model.UserModel;
import com.example.konekapp.ui.dashboard.home.consultation.chatroom.ChatRoomActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ReviewedDetailMonitoringActivity extends AppCompatActivity {

    DatabaseReference rootRef, usersRef, cropsRef, notificationRef;
    String currentUserId, notificationKey;
    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;

    Calendar calendar;
    SimpleDateFormat dateFormat;
    String notifDate, cropsId;
    String name, period, date, qty, location, fertilizer, result, notes, userId, userImage;

    TextView NameDetailCrops, PeriodDetailCrops, DateDetailCrops, QtyDetailCrops, LocationDetailCrops,
            FertilizerDetailCrops, ResultDetailCrops, NotesDetailCrops;

    LinearLayout BtnDeleteDetailCrops, BtnEditDetailCrops, BtnChatDetailCrops;
    ConstraintLayout ConstraintDetailAdmin, ConstraintDetailAhliTani;
    Button BtnApproveDetailCrops;

    ImageView detailCropsBackAction;

    private View decorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviewed_detail_monitoring);

        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility==0) {
                    decorView.setSystemUiVisibility(hideSystemBars());
                }
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        currentUserId = currentUser.getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        usersRef=rootRef.child("users");
        cropsRef=rootRef.child("crops");
        notificationRef = rootRef.child("notification");
        notificationKey=rootRef.push().getKey();

        ConstraintDetailAdmin = findViewById(R.id.constraintDetailAdmin);
        ConstraintDetailAhliTani =findViewById(R.id.constraintDetailAhliTani);

        BtnDeleteDetailCrops = findViewById(R.id.btnDeleteDetailCrops);
        BtnEditDetailCrops = findViewById(R.id.btnEditDetailCrops);
        BtnApproveDetailCrops = findViewById(R.id.btnApproveDetailCrops);
        BtnChatDetailCrops = findViewById(R.id.btnChatDetailCrops);

        NameDetailCrops = findViewById(R.id.nameDetailCrops);
        PeriodDetailCrops = findViewById(R.id.periodDetailCrops);
        DateDetailCrops = findViewById(R.id.dateDetailCrops);
        QtyDetailCrops =findViewById(R.id.qtyDetailCrops);
        LocationDetailCrops =findViewById(R.id.locationDetailCrops);
        FertilizerDetailCrops = findViewById(R.id.fertilizerDetailCrops);
        ResultDetailCrops = findViewById(R.id.resultDetailCrops);
        NotesDetailCrops = findViewById(R.id.notesDetailCrops);
        detailCropsBackAction=findViewById(R.id.detailCropsBackAction);

        //calendar
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        notifDate = dateFormat.format(calendar.getTime());

        //detect role currentUser
        usersRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String role=snapshot.child("role").getValue().toString();
                if (role.equals("2")) {
                    ConstraintDetailAdmin.setVisibility(View.GONE);
                    ConstraintDetailAhliTani.setVisibility(View.VISIBLE);
                } else {
                    ConstraintDetailAdmin.setVisibility(View.VISIBLE);
                    ConstraintDetailAhliTani.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        detailCropsBackAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReviewedDetailMonitoringActivity.super.onBackPressed();
            }
        });
        cropsId=getIntent().getStringExtra("Key");

        cropsRef.child(cropsId).addValueEventListener(monitoringListener);

        BtnApproveDetailCrops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> cropsMaps = new HashMap<>();
                cropsMaps.put("status", 1);

                cropsRef.child(cropsId).updateChildren(cropsMaps)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    notifCropsToMitra();
                                    ReviewedDetailMonitoringActivity.super.onBackPressed();
                                    Toast.makeText(ReviewedDetailMonitoringActivity.this, "Data Disetujui", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    String message = task.getException().toString();
                                    Toast.makeText(ReviewedDetailMonitoringActivity.this, "Error: "+message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

            private void notifCropsToMitra() {
                String targetId = userId;
                String title = "Data Monitoring Disetujui";
                String description = "Data monitoring hasil panen anda telah disetujui";
                int kind = 4;

                StorageReference systemNotificationImageRef;
                systemNotificationImageRef = FirebaseStorage.getInstance().getReference().child("systemNotificationImage").child("konek_icon.png");
                systemNotificationImageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        String systemNotificationImageUrl = task.getResult().toString();
                        NotificationModel notificationModel = new NotificationModel(title, description, targetId, kind, notifDate, systemNotificationImageUrl);

                        notificationRef.child(notificationKey).setValue(notificationModel)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        //
                                    }
                                });
                    }
                });
            }
        });

        //edit by admin
        BtnEditDetailCrops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ReviewedDetailMonitoringActivity.this, AdminEditMitraCrops.class);
                i.putExtra("CropsId", cropsId);
                startActivity(i);
            }
        });

        //delete by admin
        BtnDeleteDetailCrops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showBottomSheetDialog();

//                cropsRef.child(cropsId).removeEventListener(monitoringListener);
//                cropsRef.child(cropsId).removeValue()
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()) {
//                                    ReviewedDetailMonitoringActivity.super.onBackPressed();
//                                    Toast.makeText(ReviewedDetailMonitoringActivity.this, "Data berhasil dihapus", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
            }
        });

        //ahli tani chat mitra
        BtnChatDetailCrops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserModel user = new UserModel();
                user.setUserId(userId);
                user.setName(name);
                user.setImage(userImage);
                Intent i = new Intent(ReviewedDetailMonitoringActivity.this, ChatRoomActivity.class);
                i.putExtra("user", user);
                startActivity(i);
            }
        });

    }

    private void showBottomSheetDialog() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.delete_monitoring_bs);

        ImageView closeBs = bottomSheetDialog.findViewById(R.id.deleteMonitoringBsClose);
        Button confirmDelete = bottomSheetDialog.findViewById(R.id.confirmDelete);
        Button cancelDelete = bottomSheetDialog.findViewById(R.id.cancelDelete);

        closeBs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.cancel();
            }
        });

        cancelDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.cancel();
            }
        });

        confirmDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropsRef.child(cropsId).removeEventListener(monitoringListener);
                cropsRef.child(cropsId).removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    ReviewedDetailMonitoringActivity.super.onBackPressed();
                                    Toast.makeText(ReviewedDetailMonitoringActivity.this, "Data berhasil dihapus", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        bottomSheetDialog.show();

    }

    ValueEventListener monitoringListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            name = snapshot.child("name").getValue().toString();
            period = snapshot.child("period").getValue().toString();
            date = snapshot.child("date").getValue().toString();
            qty = snapshot.child("qty").getValue().toString();
            location = snapshot.child("location").getValue().toString();
            fertilizer = snapshot.child("fertilizer").getValue().toString();
            result = snapshot.child("result").getValue().toString();
            notes = snapshot.child("notes").getValue().toString();

            userId = snapshot.child("userId").getValue().toString();
            userImage = snapshot.child("userImage").getValue().toString();

            NameDetailCrops.setText(name);
            PeriodDetailCrops.setText(period);
            DateDetailCrops.setText(date);
            QtyDetailCrops.setText(qty);
            LocationDetailCrops.setText(location);
            FertilizerDetailCrops.setText(fertilizer);
            ResultDetailCrops.setText(result);
            NotesDetailCrops.setText(notes);
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