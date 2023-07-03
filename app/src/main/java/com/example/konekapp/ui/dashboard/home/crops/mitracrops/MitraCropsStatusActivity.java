package com.example.konekapp.ui.dashboard.home.crops.mitracrops;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.konekapp.R;
import com.example.konekapp.model.CropsModel;
import com.example.konekapp.ui.dashboard.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MitraCropsStatusActivity extends AppCompatActivity {
    private ArrayList<CropsModel> list;
    private View decorView;
    private ProgressDialog pd;
    private DatabaseReference usersRef, rootRef, cropsRef;
    private String currentUserId, cropsId;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private CardView ReviewedStatus, ApprovedStatus;
    private ImageView MitraCropsStatusBackAction, ImageEditCropsStatus;
    private TextView EditCropsStatus;

    private CircleImageView ImageStatus;

    private TextView NameStatus, CommodityStatus, PeriodStatus, DateStatus, QtyStatus, LocationStatus, FertilizerStatus, ResultStatus, NotesStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mitra_crops_status);
        NameStatus = findViewById(R.id.nameStatus);
        CommodityStatus = findViewById(R.id.commodityStatus);
        PeriodStatus = findViewById(R.id.periodStatus);
        DateStatus = findViewById(R.id.dateStatus);
        QtyStatus = findViewById(R.id.qtyStatus);
        LocationStatus = findViewById(R.id.locationStatus);
        FertilizerStatus = findViewById(R.id.fertilizerStatus);
        ResultStatus = findViewById(R.id.resultStatus);
        NotesStatus = findViewById(R.id.notesStatus);
        ImageStatus = findViewById(R.id.imageStatus);
        ReviewedStatus = findViewById(R.id.reviewedStatus);
        ApprovedStatus = findViewById(R.id.approvedStatus);
        MitraCropsStatusBackAction = findViewById(R.id.mitraCropsStatusBackAction);

        //init pd
        pd = new ProgressDialog(MitraCropsStatusActivity.this);
        pd.setTitle("Please Wait...");
        pd.setCanceledOnTouchOutside(false);

        pd.setMessage("Memuat data anda");
        pd.show();

        ImageEditCropsStatus = findViewById(R.id.imageEditCropsStatus);
        EditCropsStatus = findViewById(R.id.editCropsStatus);

        MitraCropsStatusBackAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MitraCropsStatusActivity.super.onBackPressed();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        currentUserId = currentUser.getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        usersRef = rootRef.child("users");
        cropsRef = rootRef.child("crops");
        list = new ArrayList<>();
        //getStringExtra from Intent put extra
        Intent i = getIntent();
        cropsId = i.getStringExtra("CropsId");
        Log.d("MitraCropsStatus", "selectedCropsId: "+cropsId);

        EditCropsStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MitraCropsStatusActivity.this, EditMitraCropsActivity.class);
                i.putExtra("CropsId", cropsId);
                startActivity(i);
            }
        });

        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility==0) {
                    decorView.setSystemUiVisibility(hideSystemBars());
                }
            }
        });

        usersRef.child(currentUserId).addValueEventListener(listener1);

        cropsRef.child(cropsId).addValueEventListener(cropsStatusListener);

//        cropsRef.addValueEventListener(listener);

    }

    ValueEventListener cropsStatusListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            String name = snapshot.child("name").getValue().toString();
            String commodity = snapshot.child("commodity").getValue().toString();
            String period = snapshot.child("period").getValue().toString();
            String date = snapshot.child("date").getValue().toString();
            String qty = snapshot.child("qty").getValue().toString();
            String location = snapshot.child("location").getValue().toString();
            String fertilizer = snapshot.child("fertilizer").getValue().toString();
            String result = snapshot.child("result").getValue().toString();
            String notes = snapshot.child("notes").getValue().toString();
            String status = snapshot.child("status").getValue().toString();

            if (status.equals("0")) {
                ReviewedStatus.setVisibility(View.VISIBLE);
                ApprovedStatus.setVisibility(View.GONE);

                ImageEditCropsStatus.setVisibility(View.VISIBLE);
                EditCropsStatus.setVisibility(View.VISIBLE);
            } else {
                ReviewedStatus.setVisibility(View.GONE);
                ApprovedStatus.setVisibility(View.VISIBLE);
                ImageEditCropsStatus.setVisibility(View.GONE);
                EditCropsStatus.setVisibility(View.GONE);
            }

            NameStatus.setText(name);
            CommodityStatus.setText(commodity);
            PeriodStatus.setText(period);
            DateStatus.setText(date);
            QtyStatus.setText(qty);
            LocationStatus.setText(location);
            FertilizerStatus.setText(fertilizer);
            ResultStatus.setText(result);
            NotesStatus.setText(notes);

            pd.dismiss();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

//    ValueEventListener listener = new ValueEventListener() {
//        @Override
//        public void onDataChange(@NonNull DataSnapshot snapshot) {
//            list.clear();
//            //change get data method
//
//            for (DataSnapshot ds : snapshot.getChildren()) {
//                CropsModel cropsModel = ds.getValue(CropsModel.class);
//                cropsModel.setCropsId(ds.getKey());
//                try {
//                    if (cropsModel.getUserId().equals(currentUserId)) {
//                        list.add(cropsModel);
//                        cropsId = cropsModel.getCropsId();
//                        Log.d("MitraCropsStatus", "cropsId: "+cropsId);
//
//                        NameStatus.setText(cropsModel.getName());
//                        CommodityStatus.setText(cropsModel.getCommodity());
//                        PeriodStatus.setText(cropsModel.getPeriod());
//                        DateStatus.setText(cropsModel.getDate());
//                        QtyStatus.setText(cropsModel.getQty());
//                        LocationStatus.setText(cropsModel.getLocation());
//                        FertilizerStatus.setText(cropsModel.getFertilizer());
//                        ResultStatus.setText(cropsModel.getResult());
//                        NotesStatus.setText(cropsModel.getNotes());
//
//                        if (cropsModel.getStatus().equals("0")) {
//                            ReviewedStatus.setVisibility(View.VISIBLE);
//                            ApprovedStatus.setVisibility(View.GONE);
//
//                            ImageEditCropsStatus.setVisibility(View.VISIBLE);
//                            EditCropsStatus.setVisibility(View.VISIBLE);
//                        }
//                        else {
//                            ReviewedStatus.setVisibility(View.GONE);
//                            ApprovedStatus.setVisibility(View.VISIBLE);
//
//                            ImageEditCropsStatus.setVisibility(View.GONE);
//                            EditCropsStatus.setVisibility(View.GONE);
//                        }
//
//                    }
//                    pd.dismiss();
//                }
//                catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//
//        }
//
//        @Override
//        public void onCancelled(@NonNull DatabaseError error) {
//
//        }
//    };

    ValueEventListener listener1 = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            String retrieveImage = snapshot.child("image").getValue().toString();
            Picasso.get().load(retrieveImage).into(ImageStatus);
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