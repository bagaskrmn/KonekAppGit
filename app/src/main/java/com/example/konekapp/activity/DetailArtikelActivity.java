package com.example.konekapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.konekapp.R;
import com.squareup.picasso.Picasso;

public class DetailArtikelActivity extends AppCompatActivity {

    private ImageView BackActionDetailArtikel, DetailArtikelImage;
    private TextView DetailArtikelTitle, DetailArtikelSource, DetailArtikelDate, DetailArtikelDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_artikel);

        DetailArtikelImage = findViewById(R.id.detailArtikelImage);
        DetailArtikelTitle = findViewById(R.id.detailArtikelTitle);
        DetailArtikelSource = findViewById(R.id.detailArtikelSource);
        DetailArtikelDate = findViewById(R.id.detailArtikelDate);
        DetailArtikelDescription = findViewById(R.id.detailArtikelDescription);
        BackActionDetailArtikel = findViewById(R.id.backActionDetailArtikel);

        BackActionDetailArtikel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailArtikelActivity.super.onBackPressed();
            }
        });


        //get Data from the previous Activity Intent
        Intent intent = getIntent();
        String DetailTitle = intent.getStringExtra("Title");
        String DetailImage = intent.getStringExtra("Image");
        String DetailSource = intent.getStringExtra("Source");
        String DetailDate = intent.getStringExtra("Date");
        String DetailDescription = intent.getStringExtra("Description");

        //set Data to the item
        DetailArtikelTitle.setText(DetailTitle);
        Picasso.get().load(DetailImage).into(DetailArtikelImage);
        DetailArtikelSource.setText(DetailSource);
        DetailArtikelDate.setText(DetailDate);
        DetailArtikelDescription.setText(DetailDescription);

    }
}