package com.example.konekapp.ui.dashboard.home.crops;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class ReviewedCropsFragment extends Fragment {

    private String commodity;

    private ProgressDialog pd;
    private DatabaseReference cropsRef, rootRef;

    private ArrayList<CropsModel> list;
    private ReviewedCropsAdapter adapter;
    private RecyclerView ReviewedCropsRv;

    public ReviewedCropsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reviewed_crops, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //getString Commodity from Activity
        commodity = getActivity().getIntent().getStringExtra("Commodity");
        rootRef = FirebaseDatabase.getInstance().getReference();
        cropsRef = rootRef.child("crops");

        list = new ArrayList<>();
        ReviewedCropsRv = (RecyclerView)getView().findViewById(R.id.reviewedCropsRv);
        ReviewedCropsRv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ReviewedCropsAdapter(getContext(), list);
        ReviewedCropsRv.setAdapter(adapter);

        //init ProgressDialog
        pd = new ProgressDialog(getContext());
        pd.setTitle("Please Wait...");
        pd.setCanceledOnTouchOutside(false);

        cropsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pd.dismiss();
                list.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    CropsModel crops= ds.getValue(CropsModel.class);
                    crops.setCropsId(ds.getKey());

                    if (crops.getCommodity().equals(commodity) && crops.getStatus().equals("0")) {
                        list.add(crops);
                        Log.d("DataReviewed", Arrays.toString(new ArrayList[]{list}));
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}