package com.example.konekapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.konekapp.R;

public class HomeScreenActivity extends AppCompatActivity {

    private Button BtnLoginWithPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        BtnLoginWithPhoneNumber = findViewById(R.id.btnLoginWithPhoneNumber);

        BtnLoginWithPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent HomeToLoginPhoneIntent = new Intent(HomeScreenActivity.this, LoginPhoneActivity.class);
                startActivity(HomeToLoginPhoneIntent);
            }
        });
    }
}