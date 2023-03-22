package com.example.konekapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.konekapp.R;

public class CompleteProfileSuccess extends AppCompatActivity {

    private Button NextCompleteSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile_success);

        NextCompleteSuccess = findViewById(R.id.nextCompleteSuccess);

        NextCompleteSuccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextCompleteIntent = new Intent(CompleteProfileSuccess.this, MainActivity.class);
                nextCompleteIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(nextCompleteIntent);
            }
        });
    }
}