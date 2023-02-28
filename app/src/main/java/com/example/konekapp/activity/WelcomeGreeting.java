package com.example.konekapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.konekapp.R;

public class WelcomeGreeting extends AppCompatActivity {

    private Button BtnGreetingNext;
    private ImageView WelcomeBackAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_greeting);

        BtnGreetingNext = findViewById(R.id.btnGreetingNext);
        WelcomeBackAction = findViewById(R.id.welcomeBackAction);

        BtnGreetingNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent WelcomeToMain = new Intent(WelcomeGreeting.this, MainActivity.class);
                WelcomeToMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(WelcomeToMain);
            }
        });

        WelcomeBackAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent WelcomeToMain = new Intent(WelcomeGreeting.this, MainActivity.class);
                WelcomeToMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(WelcomeToMain);
            }
        });

    }
}