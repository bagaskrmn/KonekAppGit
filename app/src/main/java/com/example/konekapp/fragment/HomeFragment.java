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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.konekapp.R;
import com.example.konekapp.activity.ArtikelActivity;
import com.example.konekapp.activity.ArtikelAdapter;
import com.example.konekapp.activity.ArtikelModel;
import com.example.konekapp.activity.RegisterMitraActivity;
import com.example.konekapp.activity.chat.ConsultationActivity;
import com.example.konekapp.databinding.FragmentHomeBinding;
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
    private Button BtnRegisterMitra, BtnKonsultasi, BtnChatMitra;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference rootRef, usersRef, artikelRef;
    private String currentUserId;
    private ConstraintLayout ConstraintRegister, ConstraintKonsultasi, ConstraintChatMitra;
    private ProgressDialog pd;
    private TextView BtnFullArtikel;

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
        usersRef = rootRef.child("Users");
        artikelRef = rootRef.child("Artikel");

        AccImageHome = (CircleImageView)getView().findViewById(R.id.accImageHome);
        BtnRegisterMitra= (Button)getView().findViewById(R.id.btnRegisterMitra);
        BtnKonsultasi = (Button)getView().findViewById(R.id.btnKonsultasi);
        BtnFullArtikel = (TextView)getView().findViewById(R.id.btnFullArtikel);
        BtnChatMitra = (Button)getView().findViewById(R.id.btnChatMitra);

        ConstraintRegister = (ConstraintLayout)getView().findViewById(R.id.constraintRegister);
        ConstraintKonsultasi = (ConstraintLayout)getView().findViewById(R.id.constraintKonsultasi);
        ConstraintChatMitra = (ConstraintLayout)getView().findViewById(R.id.constraintChatMitra);

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



        usersRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String retrieveImage = snapshot.child("Image").getValue().toString();
                Picasso.get().load(retrieveImage).into(AccImageHome);


                //chek role
                String role = snapshot.child("Role").getValue().toString();

                //if role is user (1)
                if (role.equals("1")) {
                    ConstraintRegister.setVisibility(View.VISIBLE);
                    ConstraintKonsultasi.setVisibility(View.GONE);
                    ConstraintChatMitra.setVisibility(View.GONE);
                }
                //if role is mitra(2)
                if (role.equals("2")) {
                    ConstraintRegister.setVisibility(View.GONE);
                    ConstraintKonsultasi.setVisibility(View.VISIBLE);
                    ConstraintChatMitra.setVisibility(View.GONE);
                }
                //if role is Ahli Tani
                if (role.equals("3")) {
                    ConstraintRegister.setVisibility(View.GONE);
                    ConstraintKonsultasi.setVisibility(View.GONE);
                    ConstraintChatMitra.setVisibility(View.VISIBLE);
                }
                //if role is admin
                if (role.equals("4")) {
                    ConstraintRegister.setVisibility(View.GONE);
                    ConstraintKonsultasi.setVisibility(View.GONE);
                    ConstraintChatMitra.setVisibility(View.GONE);
                }


                pd.dismiss();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //TOAST error
//                Toast.makeText(getActivity(), "Error"+ error.getMessage(), Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }
        });

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
            }
        });

        artikelRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ArtikelModel artikel = dataSnapshot.getValue(ArtikelModel.class);
                    list.add(artikel);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
}