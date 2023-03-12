package com.example.konekapp.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.konekapp.R;
import com.example.konekapp.activity.RegisterMitraActivity;
import com.example.konekapp.activity.SettingActivity;
import com.example.konekapp.activity.chat.ConsultationActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountFragment extends Fragment {

    private CircleImageView AccImageProfile;
    private Button BtnBuatForum;
    private ConstraintLayout ConstraintGabungKemitraan, ConstraintKelolaKemitraan, ConstraintKonsultasi;
    private TextView NameAccountTv, RoleUserTv, DateJoinedTv;
    private ImageView BtnSetting;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference rootRef, usersRef;
    private String currentUserId;
    private ProgressDialog pd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        currentUserId = currentUser.getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        usersRef = rootRef.child("Users");

        //init pd
        pd = new ProgressDialog(getActivity());
        pd.setTitle("Please Wait...");
        pd.setCanceledOnTouchOutside(false);

        AccImageProfile = (CircleImageView) getView().findViewById(R.id.accImageProfile);
        BtnBuatForum = (Button) getView().findViewById(R.id.btnBuatForum);
        BtnSetting = (ImageView) getView().findViewById(R.id.btnSetting);
        ConstraintGabungKemitraan = (ConstraintLayout) getView().findViewById(R.id.constraintGabungKemitraan);
        ConstraintKelolaKemitraan = (ConstraintLayout) getView().findViewById(R.id.constraintKelolaKemitraan);
        ConstraintKonsultasi = (ConstraintLayout) getView().findViewById(R.id.constraintKonsultasi);
        NameAccountTv = (TextView) getView().findViewById(R.id.nameAccountTv);
        RoleUserTv = (TextView) getView().findViewById(R.id.roleUserTv);
        DateJoinedTv = (TextView) getView().findViewById(R.id.dateJoinedTv);

        pd.setMessage("Memuat data anda");
        pd.show();


        usersRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //retrieve user data from database
                String retrieveImage = snapshot.child("Image").getValue().toString();
                String retrieveName = snapshot.child("Nama").getValue().toString();
                String retrieveDate = snapshot.child("Bergabung pada").getValue().toString();

                //set to field
                DateJoinedTv.setText("Bergabung pada " +retrieveDate);
                NameAccountTv.setText(retrieveName);
                Picasso.get().load(retrieveImage).into(AccImageProfile);

                //check role
                String role = snapshot.child("Role").getValue().toString();
                //if role is user(1)
                if (role.equals("1")) {
                    ConstraintGabungKemitraan.setVisibility(View.VISIBLE);
                    ConstraintKelolaKemitraan.setVisibility(View.GONE);
                    ConstraintKonsultasi.setVisibility(View.GONE);

                    RoleUserTv.setText("Pengguna");

                }
                if (role.equals("2")) {
                    ConstraintGabungKemitraan.setVisibility(View.GONE);
                    ConstraintKelolaKemitraan.setVisibility(View.GONE);
                    ConstraintKonsultasi.setVisibility(View.VISIBLE);

                    RoleUserTv.setText("Petani Mitra");
                }
                if (role.equals("3")) {
                    ConstraintGabungKemitraan.setVisibility(View.GONE);
                    ConstraintKelolaKemitraan.setVisibility(View.VISIBLE);
                    ConstraintKonsultasi.setVisibility(View.GONE);

                    RoleUserTv.setText("Ahli Tani");
                }

                if (role.equals("4")) {
                    ConstraintGabungKemitraan.setVisibility(View.GONE);
                    ConstraintKelolaKemitraan.setVisibility(View.VISIBLE);
                    ConstraintKonsultasi.setVisibility(View.GONE);

                    RoleUserTv.setText("Admin");
                }
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        BtnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingIntent = new Intent(getActivity(), SettingActivity.class);
                startActivity(settingIntent);
            }
        });

        BtnBuatForum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add forum activity here
            }
        });

        ConstraintGabungKemitraan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gabungKemitraanIntent = new Intent(getActivity(), RegisterMitraActivity.class);
                startActivity(gabungKemitraanIntent);
            }
        });

        ConstraintKonsultasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add konsultasiActivity here
                Intent intentKonsultasi = new Intent(requireContext(), ConsultationActivity.class);
                startActivity(intentKonsultasi);
            }
        });

        ConstraintKelolaKemitraan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add KelolaKemitraanActivity here
            }
        });

    }
}