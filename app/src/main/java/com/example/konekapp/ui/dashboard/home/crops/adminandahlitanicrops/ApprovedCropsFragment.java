package com.example.konekapp.ui.dashboard.home.crops.adminandahlitanicrops;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.konekapp.R;
import com.example.konekapp.model.CropsModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class ApprovedCropsFragment extends Fragment {
    private String commodity;

    private ProgressDialog pd;
    private DatabaseReference cropsRef, rootRef, usersRef;

    private ArrayList<CropsModel> list;
    private ApprovedCropsAdapter adapter;
    private RecyclerView ApprovedCropsRv;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private String currentUserId;

    BottomSheetDialog dialog;
    public ApprovedCropsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_approved_crops, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //getString Commodity from Activity
        commodity = getActivity().getIntent().getStringExtra("Commodity");
        //users
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        currentUserId = currentUser.getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        usersRef = rootRef.child("users");
        cropsRef = rootRef.child("crops");

        //init ProgressDialog
        pd = new ProgressDialog(getContext());
        pd.setTitle("Please Wait...");
        pd.setCanceledOnTouchOutside(false);

        pd.setMessage("Memuat data");
        pd.show();

        list = new ArrayList<>();
        ApprovedCropsRv = (RecyclerView)getView().findViewById(R.id.approvedCropsRv);
        ApprovedCropsRv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ApprovedCropsAdapter(getContext(), list);
        ApprovedCropsRv.setAdapter(adapter);

        cropsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    CropsModel crops= ds.getValue(CropsModel.class);
                    crops.setCropsId(ds.getKey());

                    if (crops.getCommodity().equals(commodity) && crops.getStatus()==1) {
                        list.add(crops);

                        Log.d("DataApproved", Arrays.toString(new ArrayList[]{list}));
                    }
                    pd.dismiss();
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}