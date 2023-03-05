package com.example.konekapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.konekapp.R;

public class RegisterSuccessActivity extends AppCompatActivity {

    private Button NextRegisterSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_success);

        NextRegisterSuccess = findViewById(R.id.nextRegisterSuccess);

        NextRegisterSuccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextRegisterIntent = new Intent(RegisterSuccessActivity.this, MainActivity.class);
                nextRegisterIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(nextRegisterIntent);
            }
        });
    }
}