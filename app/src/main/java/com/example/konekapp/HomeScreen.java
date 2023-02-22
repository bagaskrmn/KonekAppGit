package com.example.konekapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeScreen extends AppCompatActivity {

    private Button BtnLoginWithPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        BtnLoginWithPhoneNumber = findViewById(R.id.btnLoginWithPhoneNumber);

        BtnLoginWithPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent HomeToLoginPhoneIntent = new Intent(HomeScreen.this, LoginPhone.class);
                startActivity(HomeToLoginPhoneIntent);
            }
        });
    }
}