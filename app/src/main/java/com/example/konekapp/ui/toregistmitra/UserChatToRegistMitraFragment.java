package com.example.konekapp.ui.toregistmitra;

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
import com.example.konekapp.ui.dashboard.home.registermitra.RegisterMitraActivity;

public class UserChatToRegistMitraFragment extends Fragment {

    private Button BtnChatToRegistMitra;


    public UserChatToRegistMitraFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_chat_to_regist_mitra, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BtnChatToRegistMitra = (Button)getView().findViewById(R.id.btnChatToRegistMitra);
        BtnChatToRegistMitra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), RegisterMitraActivity.class);
                startActivity(i);
            }
        });
    }
}