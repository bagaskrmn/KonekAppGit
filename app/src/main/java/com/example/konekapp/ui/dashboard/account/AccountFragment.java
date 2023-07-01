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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.konekapp.R;
import com.example.konekapp.ui.dashboard.account.setting.accountsetting.MitraProfileActivity;
import com.example.konekapp.ui.dashboard.account.setting.accountsetting.ProfileActivity;
import com.example.konekapp.ui.dashboard.home.consultation.consultationmitra.ConsultationToAhliTaniActivity;
import com.example.konekapp.ui.dashboard.home.managemitra.ManageMitra;
import com.example.konekapp.ui.dashboard.home.registermitra.RegisterMitraActivity;
import com.example.konekapp.ui.dashboard.account.setting.SettingActivity;
import com.example.konekapp.ui.dashboard.home.consultation.consultationahlitani.ConsultationToMitraActivity;
import com.example.konekapp.ui.login.LoginPhoneActivity;
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
    private ConstraintLayout ConstraintGabungKemitraan, ConstraintKelolaKemitraan, ConstraintKonsultasi, ConstraintChatMitra
            , BtnKelolaAkun, BtnKelolaNotifikasi, BtnPrivacyPolicy;
    private LinearLayout BtnLogout;
    private TextView NameAccountTv, RoleUserTv, DateJoinedTv;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference rootRef, usersRef;
    private String currentUserId, role;
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
        ConstraintGabungKemitraan = (ConstraintLayout) getView().findViewById(R.id.constraintGabungKemitraan);
        ConstraintKelolaKemitraan = (ConstraintLayout) getView().findViewById(R.id.constraintKelolaKemitraan);
        ConstraintKonsultasi = (ConstraintLayout) getView().findViewById(R.id.constraintKonsultasi);
        ConstraintChatMitra = (ConstraintLayout)getView().findViewById(R.id.constraintChatMitra);
        BtnKelolaAkun = (ConstraintLayout)getView().findViewById(R.id.btnKelolaAkun);
        BtnKelolaNotifikasi = (ConstraintLayout)getView().findViewById(R.id.btnKelolaNotifikasi);
        BtnPrivacyPolicy = (ConstraintLayout)getView().findViewById(R.id.btnPrivacyPolicy);
        NameAccountTv = (TextView) getView().findViewById(R.id.nameAccountTv);
        RoleUserTv = (TextView) getView().findViewById(R.id.roleUserTv);
        DateJoinedTv = (TextView) getView().findViewById(R.id.dateJoinedTv);
        BtnLogout=(LinearLayout)getView().findViewById(R.id.btnLogout);

        BtnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usersRef.child(currentUserId).removeEventListener(listener);
                firebaseAuth.signOut();

                Intent signOutIntent = new Intent(getContext(), LoginPhoneActivity.class);
                signOutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(signOutIntent);
                Toast.makeText(getContext(), "Logout Berhasil", Toast.LENGTH_SHORT).show();
            }
        });

        pd.setMessage("Memuat data anda");
        pd.show();


        usersRef.child(currentUserId).addValueEventListener(listener);

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

        BtnKelolaAkun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (role!=null) {
                    if (role.equals("1")) {
                        Intent mitraProfileIntent = new Intent(getContext(), MitraProfileActivity.class);
                        startActivity(mitraProfileIntent);
                    }

                    else {
                        Intent userProfileIntent = new Intent(getContext(), ProfileActivity.class);
                        startActivity(userProfileIntent);
                    }
                }
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

            role = snapshot.child("role").getValue().toString();

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