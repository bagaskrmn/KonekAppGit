package com.example.konekapp.ui.dashboard.account;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.example.konekapp.ui.dashboard.home.consultation.consultationmitra.ConsultationToAhliTaniActivity;
import com.example.konekapp.ui.dashboard.home.managemitra.ManageMitra;
import com.example.konekapp.ui.dashboard.home.registermitra.RegisterMitraActivity;
import com.example.konekapp.ui.dashboard.account.setting.SettingActivity;
import com.example.konekapp.ui.dashboard.home.consultation.consultationahlitani.ConsultationToMitraActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountFragment extends Fragment {

    private CircleImageView AccImageProfile;
    private Button BtnBuatForum;
    private ConstraintLayout ConstraintGabungKemitraan, ConstraintKelolaKemitraan, ConstraintKonsultasi, ConstraintChatMitra;
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
        usersRef = rootRef.child("users");

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
        ConstraintChatMitra = (ConstraintLayout)getView().findViewById(R.id.constraintChatMitra);
        NameAccountTv = (TextView) getView().findViewById(R.id.nameAccountTv);
        RoleUserTv = (TextView) getView().findViewById(R.id.roleUserTv);
        DateJoinedTv = (TextView) getView().findViewById(R.id.dateJoinedTv);

        pd.setMessage("Memuat data anda");
        pd.show();


        usersRef.child(currentUserId).addValueEventListener(listener);

        BtnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usersRef.child(currentUserId).removeEventListener(listener);
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
                Intent intentKonsultasi = new Intent(requireContext(), ConsultationToAhliTaniActivity.class);
                startActivity(intentKonsultasi);
            }
        });

        ConstraintChatMitra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), ConsultationToMitraActivity.class);
                startActivity(intent);
            }
        });

        ConstraintKelolaKemitraan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), ManageMitra.class);
                startActivity(intent);
            }
        });

    }
    ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            pd.dismiss();
            //retrieve user data from database
            String retrieveImage = snapshot.child("image").getValue().toString();
            String retrieveName = snapshot.child("name").getValue().toString();
            String retrieveDate = snapshot.child("dateJoined").getValue().toString();

            //set to field
            DateJoinedTv.setText("Bergabung pada " +retrieveDate);
            NameAccountTv.setText(retrieveName);
            Picasso.get().load(retrieveImage).into(AccImageProfile);

            //check role
            String role = snapshot.child("role").getValue().toString();

            if (role.equals("0")) {
                ConstraintGabungKemitraan.setVisibility(View.VISIBLE);
                ConstraintKonsultasi.setVisibility(View.GONE);
                ConstraintChatMitra.setVisibility(View.GONE);
                ConstraintKelolaKemitraan.setVisibility(View.GONE);

                RoleUserTv.setText("Pengguna");
            }

            if (role.equals("4")) {
                ConstraintGabungKemitraan.setVisibility(View.GONE);
                ConstraintKonsultasi.setVisibility(View.GONE);
                ConstraintChatMitra.setVisibility(View.GONE);
                ConstraintKelolaKemitraan.setVisibility(View.GONE);

                RoleUserTv.setText("Pengguna");
            }

            if (role.equals("1")) {
                ConstraintGabungKemitraan.setVisibility(View.GONE);
                ConstraintKonsultasi.setVisibility(View.VISIBLE);
                ConstraintChatMitra.setVisibility(View.GONE);
                ConstraintKelolaKemitraan.setVisibility(View.GONE);

                RoleUserTv.setText("Petani Mitra");
            }
            if (role.equals("2")) {
                ConstraintGabungKemitraan.setVisibility(View.GONE);
                ConstraintKonsultasi.setVisibility(View.GONE);
                ConstraintChatMitra.setVisibility(View.VISIBLE);
                ConstraintKelolaKemitraan.setVisibility(View.GONE);

                RoleUserTv.setText("Ahli Tani");
            }

            if (role.equals("3")) {
                ConstraintGabungKemitraan.setVisibility(View.GONE);
                ConstraintKonsultasi.setVisibility(View.GONE);
                ConstraintChatMitra.setVisibility(View.GONE);
                ConstraintKelolaKemitraan.setVisibility(View.VISIBLE);

                RoleUserTv.setText("Admin");
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        usersRef.child(currentUserId).removeEventListener(listener);
    }
}