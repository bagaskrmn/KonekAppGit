package com.example.konekapp.ui.dashboard.home.crops.adminandahlitanicrops;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.konekapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class CropsDetail extends AppCompatActivity {

    private TextView NameDetailCrops, PeriodDetailCrops, DateDetailCrops, QtyDetailCrops,
            LocationDetailCrops, FertilizerDetailCrops, ResultDetailCrops, NotesDetailCrops;

    private Button BtnApprovedDetailCrops;
    private ConstraintLayout ConstraintDetailAhliTani, ConstraintDetailAdmin;
    private LinearLayout BtnChatDetailCrops, BtnDeleteDetailCrops, BtnEditDetailCrops;
    private DatabaseReference rootRef, cropsRef, usersRef;
    private String cropsId, commodity, currentUserId, role;
    private ImageView DetailCropsClose;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    private View decorView;

    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crops_detail);

        //decorView
        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility==0) {
                    decorView.setSystemUiVisibility(hideSystemBars());
                }
            }
        });
        //decorView

        ConstraintDetailAdmin = findViewById(R.id.constraintDetailAdmin);
        ConstraintDetailAhliTani = findViewById(R.id.constraintDetailAhliTani);

        BtnApprovedDetailCrops = findViewById(R.id.btnApproveDetailCrops);
        BtnChatDetailCrops = findViewById(R.id.btnChatDetailCrops);
        BtnEditDetailCrops = findViewById(R.id.btnEditDetailCrops);
        BtnDeleteDetailCrops = findViewById(R.id.btnDeleteDetailCrops);


        NameDetailCrops = findViewById(R.id.nameDetailCrops);
        PeriodDetailCrops = findViewById(R.id.periodDetailCrops);
        DateDetailCrops = findViewById(R.id.dateDetailCrops);
        QtyDetailCrops = findViewById(R.id.qtyDetailCrops);
        LocationDetailCrops = findViewById(R.id.locationDetailCrops);
        FertilizerDetailCrops = findViewById(R.id.fertilizerDetailCrops);
        ResultDetailCrops = findViewById(R.id.resultDetailCrops);
        NotesDetailCrops = findViewById(R.id.notesDetailCrops);
        DetailCropsClose = findViewById(R.id.detailCropsClose);

        //init ProgressDialog
        pd = new ProgressDialog(this);
        pd.setTitle("Please Wait...");
        pd.setCanceledOnTouchOutside(false);

        pd.setMessage("Memuat data");
        pd.show();

        //users
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        currentUserId = currentUser.getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        cropsRef = rootRef.child("crops");
        usersRef = rootRef.child("users");

        usersRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                role = snapshot.child("role").getValue().toString();
                //if role ahli tani
                if (role.equals("2")) {
                    ConstraintDetailAhliTani.setVisibility(View.VISIBLE);
                    ConstraintDetailAdmin.setVisibility(View.GONE);
                } else {
                    ConstraintDetailAhliTani.setVisibility(View.GONE);
                    ConstraintDetailAdmin.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Intent i = getIntent();
        cropsId = i.getStringExtra("CropsId");
        commodity = i.getStringExtra("Commodity");

        DetailCropsClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropsDetail.super.onBackPressed();
            }
        });

        //getData with cropsId
        cropsRef.child(cropsId).addValueEventListener(listenerData);

        //approve by ahli tani
        BtnApprovedDetailCrops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> cropsMaps = new HashMap<>();
                cropsMaps.put("status", "1");

                cropsRef.child(cropsId).updateChildren(cropsMaps)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Intent i = new Intent(CropsDetail.this, AhliTaniAndAdminCropsActivity.class);
                                    i.putExtra("Commodity", commodity);
                                    startActivity(i);
                                    finish();
                                    Toast.makeText(CropsDetail.this, "Data Disetujui", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    String message = task.getException().toString();
                                    Toast.makeText(CropsDetail.this, "Error: "+message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        //edit by Admin
        BtnEditDetailCrops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CropsDetail.this, AdminEditMitraCrops.class);
                i.putExtra("CropsId", cropsId);
                startActivity(i);
            }
        });

        //delete by admin
        BtnDeleteDetailCrops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropsRef.child(cropsId).removeEventListener(listenerData);
                cropsRef.child(cropsId).removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(CropsDetail.this, "Data berhasil dihapus", Toast.LENGTH_SHORT).show();
                                    CropsDetail.super.onBackPressed();
                                }
                            }
                        });
            }
        });
    }
    ValueEventListener listenerData = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            String name = snapshot.child("name").getValue().toString();
            String period = snapshot.child("period").getValue().toString();
            String date = snapshot.child("date").getValue().toString();
            String qty = snapshot.child("qty").getValue().toString();
            String location = snapshot.child("location").getValue().toString();
            String fertilizer = snapshot.child("fertilizer").getValue().toString();
            String result = snapshot.child("result").getValue().toString();
            String notes = snapshot.child("notes").getValue().toString();

            NameDetailCrops.setText(name);
            PeriodDetailCrops.setText(period);
            DateDetailCrops.setText(date);
            QtyDetailCrops.setText(qty);
            LocationDetailCrops.setText(location);
            FertilizerDetailCrops.setText(fertilizer);
            ResultDetailCrops.setText(result);
            NotesDetailCrops.setText(notes);

            pd.dismiss();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };


    //decorView
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
    //decorView
}