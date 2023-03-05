package com.example.konekapp.fragment;

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
import android.widget.Toast;

import com.example.konekapp.R;
import com.example.konekapp.activity.RegisterMitraActivity;
import com.example.konekapp.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {

    private CircleImageView AccImageHome;
    private Button BtnRegisterMitra, BtnKonsultasi;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference rootRef, usersRef;
    private String currentUserId;
    private ConstraintLayout ConstraintRegister, ConstraintKonsultasi;
    private ProgressDialog pd;

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

        AccImageHome = (CircleImageView)getView().findViewById(R.id.accImageHome);
        BtnRegisterMitra= (Button)getView().findViewById(R.id.btnRegisterMitra);
        BtnKonsultasi = (Button)getView().findViewById(R.id.btnKonsultasi);

        ConstraintRegister = (ConstraintLayout)getView().findViewById(R.id.constraintRegister);
        ConstraintKonsultasi = (ConstraintLayout)getView().findViewById(R.id.constraintKonsultasi);

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
                }
                //if role is mitra(2)
                if (role.equals("2")) {
                    ConstraintRegister.setVisibility(View.GONE);
                    ConstraintKonsultasi.setVisibility(View.VISIBLE);
                }

                pd.dismiss();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //error
                Toast.makeText(getActivity(), "Error"+ error.getMessage(), Toast.LENGTH_SHORT).show();
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
            }
        });


    }
}