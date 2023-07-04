package com.example.konekapp.ui.dashboard.home.crops.mitracrops;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.konekapp.R;
import com.example.konekapp.model.CropsModel;
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

public class MitraCropsListActivity extends AppCompatActivity {

    private ArrayList<CropsModel> list;
    private MitraCropsListAdapter adapter;

    private View decorView;

    private CircleImageView ImageMitraCropsList;
    private TextView NameMitraCropsList;
    private RecyclerView MitraCropsListRv;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference rootRef, usersRef, cropsRef;
    private ProgressDialog pd;
    private String currentUserId, cropsId, currentUserName;

    private ImageView MitraCropsListBackAction, BtnAddMitraCrops;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mitra_crops_list);

        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility==0) {
                    decorView.setSystemUiVisibility(hideSystemBars());
                }
            }
        });

        MitraCropsListBackAction = findViewById(R.id.mitraCropsListBackAction);
        BtnAddMitraCrops = findViewById(R.id.btnAddMitraCrops);

        MitraCropsListBackAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MitraCropsListActivity.super.onBackPressed();
            }
        });

        ImageMitraCropsList = findViewById(R.id.imageMitraCropsList);
        NameMitraCropsList = findViewById(R.id.nameMitraCropsList);
        MitraCropsListRv = findViewById(R.id.mitraCropsListRv);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        currentUserId = currentUser.getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        usersRef = rootRef.child("users");
        cropsRef = rootRef.child("crops");

        list = new ArrayList<>();
        MitraCropsListRv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MitraCropsListAdapter(this, list);
        MitraCropsListRv.setAdapter(adapter);

        //init pd
        pd = new ProgressDialog(MitraCropsListActivity.this);
        pd.setTitle("Please Wait...");
        pd.setCanceledOnTouchOutside(false);

        pd.setMessage("Memuat data anda");
        pd.show();

        //menu add
        BtnAddMitraCrops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(MitraCropsListActivity.this, v);
                popupMenu.getMenuInflater().inflate(R.menu.add_mitra_crops_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.addMonitoring:
                                toAddMonitoring();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();
            }
        });

        usersRef.child(currentUserId).addValueEventListener(userListener);

        cropsRef.addListenerForSingleValueEvent(cropsListener);

    }

    private void toAddMonitoring() {
        Intent i = new Intent(MitraCropsListActivity.this, MitraCropsActivity.class);
        startActivity(i);
        finish();
    }

    ValueEventListener userListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            String name = snapshot.child("name").getValue().toString();
            String image = snapshot.child("image").getValue().toString();

            NameMitraCropsList.setText(name);
            Picasso.get().load(image).into(ImageMitraCropsList);

            pd.dismiss();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    ValueEventListener cropsListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            pd.dismiss();
            list.clear();
            for (DataSnapshot ds : snapshot.getChildren()) {
                CropsModel crops = ds.getValue(CropsModel.class);
                crops.setCropsId(ds.getKey());

                if (crops.getUserId().equals(currentUserId)) {
                    list.add(crops);
                }
            }

//            Collections.sort(list, (obj1, obj2) -> obj2.getDate().compareTo(obj1.getDate()));
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