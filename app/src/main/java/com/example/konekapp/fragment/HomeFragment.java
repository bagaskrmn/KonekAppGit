package com.example.konekapp.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.konekapp.R;
import com.example.konekapp.activity.ArtikelActivity;
import com.example.konekapp.activity.ArtikelAdapter;
import com.example.konekapp.activity.ArtikelModel;
import com.example.konekapp.activity.ManageMitra;
import com.example.konekapp.activity.MitraProfileActivity;
import com.example.konekapp.activity.ProfileActivity;
import com.example.konekapp.activity.RegisterMitraActivity;
import com.example.konekapp.activity.TanamanActivity;
import com.example.konekapp.activity.chat.consultation.ConsultationActivity;
import com.example.konekapp.activity.chattomitra.ConsultationToMitraActivity;
//import com.example.konekapp.databinding.FragmentHomeBinding;
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

public class HomeFragment extends Fragment {

    private CircleImageView AccImageHome;
    private Button BtnRegisterMitra, BtnKonsultasi, BtnChatMitra, BtnKelolaKemitraan;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference rootRef, usersRef, articleRef;
    private String role, currentUserId;
    private ConstraintLayout ConstraintRegister, ConstraintKonsultasi, ConstraintChatMitra, ConstraintKelolaKemitraan;
    private LinearLayout BtnPenyakitTanaman, BtnObatTanaman;
    private ProgressDialog pd;
    private TextView BtnFullArtikel, TvWaitingConfirmation;

    //Keperluan RecyclerView
    private ArrayList<ArtikelModel> list;
    private ArtikelAdapter adapter;
    private RecyclerView recyclerView;

    AccountFragment accountFragment = new AccountFragment();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        currentUserId = currentUser.getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        usersRef = rootRef.child("users");
        articleRef = rootRef.child("article");

        TvWaitingConfirmation = (TextView)getView().findViewById(R.id.tvWaitingConfirmation);

        AccImageHome = (CircleImageView)getView().findViewById(R.id.accImageHome);
        BtnRegisterMitra= (Button)getView().findViewById(R.id.btnRegisterMitra);
        BtnKonsultasi = (Button)getView().findViewById(R.id.btnKonsultasi);
        BtnFullArtikel = (TextView)getView().findViewById(R.id.btnFullArtikel);
        BtnChatMitra = (Button)getView().findViewById(R.id.btnChatMitra);
        BtnPenyakitTanaman = (LinearLayout)getView().findViewById(R.id.btnPenyakitTanaman);
        BtnObatTanaman = (LinearLayout)getView().findViewById(R.id.btnObatTanaman);
        BtnKelolaKemitraan = (Button)getView().findViewById(R.id.btnKelolaKemitraan);

        ConstraintRegister = (ConstraintLayout)getView().findViewById(R.id.constraintRegister);
        ConstraintKonsultasi = (ConstraintLayout)getView().findViewById(R.id.constraintKonsultasi);
        ConstraintChatMitra = (ConstraintLayout)getView().findViewById(R.id.constraintChatMitra);
        ConstraintKelolaKemitraan = (ConstraintLayout)getView().findViewById(R.id.constraintKelolaKemitraan);

        //RecyclerView
        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);
        list = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new ArtikelAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);

        //init ProgressDialog
        pd = new ProgressDialog(getActivity());
        pd.setTitle("Please Wait...");
        pd.setCanceledOnTouchOutside(false);

        pd.setMessage("Memuat data anda");
        pd.show();

        usersRef.child(currentUserId).addValueEventListener(listener);

        BtnRegisterMitra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerMitraIntent = new Intent(getActivity(), RegisterMitraActivity.class);
                startActivity(registerMitraIntent);
            }
        });

        BtnKonsultasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add konsultasi activity here
                Intent intent = new Intent(requireContext(), ConsultationActivity.class);
                startActivity(intent);
            }
        });

        BtnChatMitra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add chat with mitra activity here
                Intent intent = new Intent(requireContext(), ConsultationToMitraActivity.class);
                startActivity(intent);
            }
        });

        BtnKelolaKemitraan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), ManageMitra.class);
                startActivity(intent);
            }
        });

        AccImageHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if pressed, go to account fragment
//                BottomNavigationView bottomNav = getActivity().findViewById(R.id.bottomNav);
//                bottomNav.setSelectedItemId(R.id.account);
//                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
//                fragmentTransaction.replace(R.id.container, accountFragment).commit();

                //if pressed, go to profile
                if (role.equals("1")) {
                    Intent mitraProfile = new Intent(getActivity(), MitraProfileActivity.class);
                    startActivity(mitraProfile);
                }
                else {
                    Intent profileIntent = new Intent(getActivity(), ProfileActivity.class);
                    startActivity(profileIntent);
                }
            }
        });

        articleRef.addValueEventListener(listener1);

        BtnFullArtikel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fullArtikel = new Intent(getActivity(), ArtikelActivity.class);
                startActivity(fullArtikel);
            }
        });

        BtnPenyakitTanaman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tanamanIntent = new Intent(getActivity(), TanamanActivity.class);
                startActivity(tanamanIntent);
            }
        });

        BtnObatTanaman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tanamanIntent = new Intent(getActivity(), TanamanActivity.class);
                startActivity(tanamanIntent);
            }
        });
    }

    ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            String retrieveImage = snapshot.child("image").getValue().toString();
            Picasso.get().load(retrieveImage).into(AccImageHome);


            //chek role
            role = snapshot.child("role").getValue().toString();

            //if role is user (1)
            if (role.equals("0")) {
                ConstraintRegister.setVisibility(View.VISIBLE);
                ConstraintKonsultasi.setVisibility(View.GONE);
                ConstraintChatMitra.setVisibility(View.GONE);
                ConstraintKelolaKemitraan.setVisibility(View.GONE);


                BtnRegisterMitra.setVisibility(View.VISIBLE);
                TvWaitingConfirmation.setVisibility(View.GONE);
            }

            if (role.equals("4")) {
                ConstraintRegister.setVisibility(View.VISIBLE);
                ConstraintKonsultasi.setVisibility(View.GONE);
                ConstraintChatMitra.setVisibility(View.GONE);
                ConstraintKelolaKemitraan.setVisibility(View.GONE);

                BtnRegisterMitra.setVisibility(View.GONE);
                TvWaitingConfirmation.setVisibility(View.VISIBLE);
            }
            //if role is mitra(2)
            if (role.equals("1")) {
                ConstraintRegister.setVisibility(View.GONE);
                ConstraintKonsultasi.setVisibility(View.VISIBLE);
                ConstraintChatMitra.setVisibility(View.GONE);
                ConstraintKelolaKemitraan.setVisibility(View.GONE);
            }
            //if role is Ahli Tani
            if (role.equals("2")) {
                ConstraintRegister.setVisibility(View.GONE);
                ConstraintKonsultasi.setVisibility(View.GONE);
                ConstraintChatMitra.setVisibility(View.VISIBLE);
                ConstraintKelolaKemitraan.setVisibility(View.GONE);
            }
            //if role is admin
            if (role.equals("3")) {
                ConstraintRegister.setVisibility(View.GONE);
                ConstraintKonsultasi.setVisibility(View.GONE);
                ConstraintChatMitra.setVisibility(View.GONE);
                ConstraintKelolaKemitraan.setVisibility(View.VISIBLE);
            }
            pd.dismiss();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(getActivity(), ""+ error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    ValueEventListener listener1 = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                ArtikelModel artikel = dataSnapshot.getValue(ArtikelModel.class);
                artikel.setKey(dataSnapshot.getKey());
                list.add(artikel);
            }
            adapter.notifyDataSetChanged();
        }
        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(getActivity(), ""+ error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        articleRef.removeEventListener(listener1);
        usersRef.child(currentUserId).removeEventListener(listener);
    }
}