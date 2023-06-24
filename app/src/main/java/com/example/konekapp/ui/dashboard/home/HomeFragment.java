package com.example.konekapp.ui.dashboard.home;


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
import com.example.konekapp.model.CropsModel;
import com.example.konekapp.ui.dashboard.home.article.ArtikelActivity;
import com.example.konekapp.model.ArtikelModel;
import com.example.konekapp.ui.dashboard.home.consultation.consultationmitra.ConsultationToAhliTaniActivity;
import com.example.konekapp.ui.dashboard.home.crops.CommodityCropsActivity;
import com.example.konekapp.ui.dashboard.home.crops.MitraCropsStatusActivity;
import com.example.konekapp.ui.dashboard.home.crops.PreMitraCropsActivity;
import com.example.konekapp.ui.dashboard.home.managemitra.ManageMitra;
import com.example.konekapp.ui.dashboard.account.setting.accountsetting.MitraProfileActivity;
import com.example.konekapp.ui.dashboard.account.setting.accountsetting.ProfileActivity;
import com.example.konekapp.ui.dashboard.home.registermitra.RegisterMitraActivity;
import com.example.konekapp.ui.dashboard.home.plant.TanamanActivity;
import com.example.konekapp.ui.dashboard.home.consultation.consultationahlitani.ConsultationToMitraActivity;
//import com.example.konekapp.databinding.FragmentHomeBinding;
import com.example.konekapp.ui.dashboard.account.AccountFragment;
import com.example.konekapp.ui.dashboard.home.waitingreview.WaitingReviewActivity;
import com.example.konekapp.ui.toregistmitra.ToRegistMitraActivity;
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
    private Button BtnRegisterMitra, BtnKonsultasi, BtnChatMitra, BtnKelolaKemitraan, BtnWaitingReview;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference rootRef, usersRef, articleRef, cropsRef;
    private String role, currentUserId;
    private ConstraintLayout ConstraintRegister, ConstraintKonsultasi, ConstraintChatMitra, ConstraintKelolaKemitraan, ConstraintUnregister;
    private LinearLayout BtnDiseaseAndDrug, BtnCrops;
    private ProgressDialog pd;
    private TextView BtnFullArtikel;
    private ArrayList<CropsModel> listCrops;

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
        cropsRef = rootRef.child("crops");

        AccImageHome = (CircleImageView)getView().findViewById(R.id.accImageHome);
        BtnRegisterMitra= (Button)getView().findViewById(R.id.btnRegisterMitra);
        BtnKonsultasi = (Button)getView().findViewById(R.id.btnKonsultasi);
        BtnFullArtikel = (TextView)getView().findViewById(R.id.btnFullArtikel);
        BtnChatMitra = (Button)getView().findViewById(R.id.btnChatMitra);
        BtnDiseaseAndDrug = (LinearLayout)getView().findViewById(R.id.btnDiseaseAndDrug);
        BtnCrops = (LinearLayout)getView().findViewById(R.id.btnCrops);
        BtnKelolaKemitraan = (Button)getView().findViewById(R.id.btnKelolaKemitraan);
        BtnWaitingReview = (Button)getView().findViewById(R.id.btnWaitingReview);

        ConstraintRegister = (ConstraintLayout)getView().findViewById(R.id.constraintRegister);
        ConstraintUnregister = (ConstraintLayout)getView().findViewById(R.id.constraintUnregister);
        ConstraintKonsultasi = (ConstraintLayout)getView().findViewById(R.id.constraintKonsultasi);
        ConstraintChatMitra = (ConstraintLayout)getView().findViewById(R.id.constraintChatMitra);
        ConstraintKelolaKemitraan = (ConstraintLayout)getView().findViewById(R.id.constraintKelolaKemitraan);

        //RecyclerView
        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);
        list = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new ArtikelAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);

        listCrops = new ArrayList<>();

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

        BtnWaitingReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent waitingReviewIntent = new Intent(getActivity(), WaitingReviewActivity.class);
                startActivity(waitingReviewIntent);
            }
        });

        BtnKonsultasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add konsultasi activity here
                Intent intent = new Intent(requireContext(), ConsultationToAhliTaniActivity.class);
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

        BtnDiseaseAndDrug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TanamanActivity.class);
                startActivity(intent);
            }
        });

        BtnCrops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.setMessage("Memuat data");
                pd.show();
                if (role.equals("0")) {
                    Intent intent = new Intent(getActivity(), ToRegistMitraActivity.class);
                    startActivity(intent);
                    pd.dismiss();
                }

                if (role.equals("4")) {
                    Intent intent = new Intent(getActivity(), WaitingReviewActivity.class);
                    startActivity(intent);
                    pd.dismiss();
                }

                //if role is petani mitra
                if (role.equals("1")) {

                    cropsRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            listCrops.clear();

                            for (DataSnapshot ds : snapshot.getChildren()) {
                                CropsModel cropsModel = ds.getValue(CropsModel.class);
                                cropsModel.setCropsId(ds.getKey());
                                try {
                                    if (cropsModel.getUserId().equals(currentUserId)) {
                                        listCrops.add(cropsModel);
                                        if (listCrops.size()>0) {
                                            Intent i = new Intent(getActivity(), MitraCropsStatusActivity.class);
                                            startActivity(i);
                                            pd.dismiss();
                                        }
                                        else {
                                            Intent i = new Intent(getActivity(), PreMitraCropsActivity.class);
                                            startActivity(i);
                                            pd.dismiss();
                                        }
                                    }

                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                //if role is ahli tani or admin
                if (role.equals("2") || role.equals("3")) {
                    Intent intent = new Intent(getActivity(), CommodityCropsActivity.class);
                    startActivity(intent);
                    pd.dismiss();
                }
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
                ConstraintUnregister.setVisibility(View.GONE);
            }

            //if role is waiting review from admin
            if (role.equals("4")) {
                ConstraintRegister.setVisibility(View.GONE);
                ConstraintKonsultasi.setVisibility(View.GONE);
                ConstraintChatMitra.setVisibility(View.GONE);
                ConstraintKelolaKemitraan.setVisibility(View.GONE);
                ConstraintUnregister.setVisibility(View.VISIBLE);

            }
            //if role is mitra(2)
            if (role.equals("1")) {
                ConstraintRegister.setVisibility(View.GONE);
                ConstraintKonsultasi.setVisibility(View.VISIBLE);
                ConstraintChatMitra.setVisibility(View.GONE);
                ConstraintKelolaKemitraan.setVisibility(View.GONE);
                ConstraintUnregister.setVisibility(View.GONE);
            }
            //if role is Ahli Tani
            if (role.equals("2")) {
                ConstraintRegister.setVisibility(View.GONE);
                ConstraintKonsultasi.setVisibility(View.GONE);
                ConstraintChatMitra.setVisibility(View.VISIBLE);
                ConstraintKelolaKemitraan.setVisibility(View.GONE);
                ConstraintUnregister.setVisibility(View.GONE);
            }
            //if role is admin
            if (role.equals("3")) {
                ConstraintRegister.setVisibility(View.GONE);
                ConstraintKonsultasi.setVisibility(View.GONE);
                ConstraintChatMitra.setVisibility(View.GONE);
                ConstraintKelolaKemitraan.setVisibility(View.VISIBLE);
                ConstraintUnregister.setVisibility(View.GONE);
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