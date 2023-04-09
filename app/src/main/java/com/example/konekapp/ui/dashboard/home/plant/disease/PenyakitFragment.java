package com.example.konekapp.ui.dashboard.home.plant.disease;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.konekapp.R;
import com.example.konekapp.ui.dashboard.home.plant.disease.adddisease.AddPenyakitActivity;
import com.example.konekapp.model.PenyakitModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PenyakitFragment extends Fragment {

    private Button BtnAddPenyakit;
    private String plantId;

    private ProgressDialog pd;
    private DatabaseReference diseaseRef, plantRef, rootRef;

    private ArrayList<PenyakitModel> list;
    private PenyakitAdapter adapter;
    private RecyclerView recyclerView;

    public PenyakitFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_penyakit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BtnAddPenyakit = (Button)getView().findViewById(R.id.btnAddPenyakit);

        BtnAddPenyakit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddPenyakitActivity.class);
                intent.putExtra("Key", plantId);
                startActivity(intent);
            }
        });


        plantId = getActivity().getIntent().getStringExtra("Key");

        rootRef = FirebaseDatabase.getInstance().getReference();
        plantRef = rootRef.child("plant");
        diseaseRef = plantRef.child(plantId).child("disease");
        list = new ArrayList<>();
        recyclerView = (RecyclerView)getView().findViewById(R.id.penyakitRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PenyakitAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);

        //init ProgressDialog
        pd = new ProgressDialog(getContext());
        pd.setTitle("Please Wait...");
        pd.setCanceledOnTouchOutside(false);

        pd.setMessage("Memuat data");
        pd.show();

        diseaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pd.dismiss();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    PenyakitModel penyakit = dataSnapshot.getValue(PenyakitModel.class);
                    penyakit.setKey(dataSnapshot.getKey());
                    list.add(penyakit);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}