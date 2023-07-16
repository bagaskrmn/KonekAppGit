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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.konekapp.R;
import com.example.konekapp.model.CropsModel;
import com.example.konekapp.ui.dashboard.home.article.ArtikelActivity;
import com.example.konekapp.model.ArtikelModel;
import com.example.konekapp.ui.dashboard.home.crops.adminandahlitanicrops.CommodityCropsActivity;
import com.example.konekapp.ui.dashboard.home.crops.mitracrops.MitraCropsListActivity;
import com.example.konekapp.ui.dashboard.home.crops.mitracrops.PreMitraCropsActivity;
import com.example.konekapp.ui.dashboard.account.profile.ProfileActivity;
import com.example.konekapp.ui.dashboard.home.plant.TanamanActivity;
//import com.example.konekapp.databinding.FragmentHomeBinding;
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
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {

    private CircleImageView AccImageHome;
    private Button BtnRegisterMitra, BtnKonsultasi, BtnChatMitra, BtnHomeAdmin, BtnWaitingReview;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference rootRef, usersRef, articleRef, cropsRef;
    private String role, currentUserId;
    private ConstraintLayout ConstraintRegister, ConstraintAsMitra, ConstraintAsAhliTani, ConstraintKelolaKemitraan, ConstraintUnregister;
    private LinearLayout BtnDiseaseAndDrug, BtnCrops;
    private ProgressDialog pd;
    private TextView BtnFullArtikel;
    private ArrayList<CropsModel> listCrops;
    private int cropsSize;

    //Keperluan RecyclerView
    private ArrayList<ArtikelModel> list;
    private ArtikelAdapter adapter;
    private RecyclerView recyclerView;

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
        BtnHomeAdmin = (Button)getView().findViewById(R.id.btnHomeAdmin);
        BtnWaitingReview = (Button)getView().findViewById(R.id.btnWaitingReview);

        ConstraintRegister = (ConstraintLayout)getView().findViewById(R.id.constraintRegister);
        ConstraintUnregister = (ConstraintLayout)getView().findViewById(R.id.constraintUnregister);
        ConstraintAsAhliTani = (ConstraintLayout)getView().findViewById(R.id.constraintAsAhliTani);
        ConstraintAsMitra = (ConstraintLayout)getView().findViewById(R.id.constraintAsMitra);
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

        BtnChatMitra.setEnabled(false);
        BtnKonsultasi.setEnabled(false);
        BtnHomeAdmin.setEnabled(false);


        voidLoadData();

//        usersRef.child(currentUserId).addValueEventListener(listener);
//        articleRef.addValueEventListener(listener1);

        BtnRegisterMitra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerMitraIntent = new Intent(getActivity(), ToRegistMitraActivity.class);
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
//                pd.setMessage("Memuat data");
//                pd.show();
                if (role.equals("0")) {
                    Intent intent = new Intent(getActivity(), ToRegistMitraActivity.class);
                    startActivity(intent);
                }

                if (role.equals("4")) {
                    Intent intent = new Intent(getActivity(), WaitingReviewActivity.class);
                    startActivity(intent);
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
                                        Log.d("Crops", "Jml array data monitoring: " +list.size());
                                        cropsSize = listCrops.size();
                                    }
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            if (cropsSize>0) {
                                Intent i = new Intent(getActivity(), MitraCropsListActivity.class);
                                startActivity(i);
                            } else {
                                Intent i = new Intent(getActivity(), PreMitraCropsActivity.class);
                                startActivity(i);
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
                }
            }
        });

        AccImageHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(getActivity(), ProfileActivity.class);
                startActivity(profileIntent);
                //if pressed, go to account fragment
//                BottomNavigationView bottomNav = getActivity().findViewById(R.id.bottomNav);
//                bottomNav.setSelectedItemId(R.id.account);
//                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
//                fragmentTransaction.replace(R.id.container, accountFragment).commit();
            }
        });

        BtnFullArtikel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fullArtikel = new Intent(getActivity(), ArtikelActivity.class);
                startActivity(fullArtikel);
            }
        });
    }

    private void voidLoadData() {
        articleRef.addValueEventListener(listener1);
        usersRef.child(currentUserId).addValueEventListener(listener);
        pd.dismiss();
    }

    ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            String retrieveImage = snapshot.child("image").getValue().toString();
            Picasso.get().load(retrieveImage).into(AccImageHome);
            //chek role
            role = snapshot.child("role").getValue().toString();
            String name = snapshot.child("name").getValue().toString();
            String[] cutName =name.split(" ", 2);
            String firstName =  cutName[0];

            //if role is user (1)
            if (role.equals("0")) {
                ConstraintRegister.setVisibility(View.VISIBLE);
                ConstraintAsAhliTani.setVisibility(View.GONE);
                ConstraintAsMitra.setVisibility(View.GONE);
                ConstraintKelolaKemitraan.setVisibility(View.GONE);
                ConstraintUnregister.setVisibility(View.GONE);
            }

            //if role is waiting review from admin
            if (role.equals("4")) {
                ConstraintRegister.setVisibility(View.GONE);
                ConstraintAsMitra.setVisibility(View.GONE);
                ConstraintAsAhliTani.setVisibility(View.GONE);
                ConstraintKelolaKemitraan.setVisibility(View.GONE);
                ConstraintUnregister.setVisibility(View.VISIBLE);

                //handle daftar userBaru
                //delete user


            }
            //if role is mitra
            if (role.equals("1")) {
                ConstraintRegister.setVisibility(View.GONE);
                ConstraintAsMitra.setVisibility(View.VISIBLE);
                ConstraintAsAhliTani.setVisibility(View.GONE);
                ConstraintKelolaKemitraan.setVisibility(View.GONE);
                ConstraintUnregister.setVisibility(View.GONE);
                BtnKonsultasi.setText("Halo, Mitra "+firstName);
            }
            //if role is Ahli Tani
            if (role.equals("2")) {
                ConstraintRegister.setVisibility(View.GONE);
                ConstraintAsMitra.setVisibility(View.GONE);
                ConstraintAsAhliTani.setVisibility(View.VISIBLE);
                ConstraintKelolaKemitraan.setVisibility(View.GONE);
                ConstraintUnregister.setVisibility(View.GONE);
                ConstraintUnregister.setVisibility(View.GONE);
                BtnChatMitra.setText("Hi, Ahli Tani "+firstName);
            }
            //if role is admin
            if (role.equals("3")) {
                ConstraintRegister.setVisibility(View.GONE);
                ConstraintAsMitra.setVisibility(View.GONE);
                ConstraintAsAhliTani.setVisibility(View.GONE);
                ConstraintKelolaKemitraan.setVisibility(View.VISIBLE);
                ConstraintUnregister.setVisibility(View.GONE);
                BtnHomeAdmin.setText("Hi, Admin "+firstName);
            }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
//            Toast.makeText(getActivity(), ""+ error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    ValueEventListener listener1 = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            list.clear();

            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                ArtikelModel artikel = dataSnapshot.getValue(ArtikelModel.class);
                artikel.setKey(dataSnapshot.getKey());
                list.add(artikel);
            }
            adapter.notifyDataSetChanged();

            //sorting list
            Collections.sort(list, (obj1, obj2) -> obj2.getDate().compareTo(obj1.getDate()));
        }
        @Override
        public void onCancelled(@NonNull DatabaseError error) {
//            Toast.makeText(getActivity(), ""+ error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        articleRef.removeEventListener(listener1);
        usersRef.child(currentUserId).removeEventListener(listener);
    }
}