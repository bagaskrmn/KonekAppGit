package com.example.konekapp.ui.dashboard.home.plant.drug;


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
import com.example.konekapp.ui.dashboard.home.plant.drug.adddrug.AddObatActivity;
import com.example.konekapp.model.ObatModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ObatFragment extends Fragment {
    private Button BtnAddObat;
    private String plantId;

    private ProgressDialog pd;
    private DatabaseReference drugRef, plantRef, rootRef;

    private ArrayList<ObatModel> list;
    private ObatAdapter adapter;
    private RecyclerView recyclerView;

    public ObatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_obat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BtnAddObat = (Button)getView().findViewById(R.id.btnAddObat);

        BtnAddObat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddObatActivity.class);
                intent.putExtra("Key", plantId);
                startActivity(intent);
            }
        });

        plantId = getActivity().getIntent().getStringExtra("Key");

        plantRef = rootRef.child("plant");
        drugRef = plantRef.child(plantId).child("drug");
        list = new ArrayList<>();
        recyclerView = (RecyclerView)getView().findViewById(R.id.obatRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ObatAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);

        //init ProgressDialog
        pd = new ProgressDialog(getContext());
        pd.setTitle("Please Wait...");
        pd.setCanceledOnTouchOutside(false);

        pd.setMessage("Memuat data");
        pd.show();

        drugRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pd.dismiss();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ObatModel obat = dataSnapshot.getValue(ObatModel.class);
                    obat.setKey(dataSnapshot.getKey());
                    list.add(obat);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}