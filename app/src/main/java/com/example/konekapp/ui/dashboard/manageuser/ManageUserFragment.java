package com.example.konekapp.ui.dashboard.manageuser;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.konekapp.R;
import com.example.konekapp.ui.dashboard.manageuser.managemitra.ManageMitra;
import com.example.konekapp.ui.dashboard.manageuser.manageuser.ManageUserActivity;
import com.example.konekapp.ui.dashboard.manageuser.upgrademitra.UpgradeMitraActivity;

public class ManageUserFragment extends Fragment {

    Button BtnManageMitra, BtnManageUser, BtnUpgradeMitra;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BtnManageMitra = (Button) getView().findViewById(R.id.btnManageMitra);
        BtnManageUser = (Button) getView().findViewById(R.id.btnManageUser);
        BtnUpgradeMitra = (Button) getView().findViewById(R.id.btnUpgradeMitra);

        BtnManageMitra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ManageMitra.class);
                startActivity(i);
            }
        });

        BtnManageUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ManageUserActivity.class);
                startActivity(i);
            }
        });

        BtnUpgradeMitra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), UpgradeMitraActivity.class);
                startActivity(i);
            }
        });
    }
}