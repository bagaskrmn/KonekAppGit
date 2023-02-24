package com.example.konekapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.konekapp.R;

public class RegisterSuccessActivity extends AppCompatActivity {

    private Button BtnRegSuccessToMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_success);

        BtnRegSuccessToMain =findViewById(R.id.btnRegSuccessToMain);

        BtnRegSuccessToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent RegSuccessToMain = new Intent(RegisterSuccessActivity.this, MainActivity.class);
                startActivity(RegSuccessToMain);
            }
        });
    }
}