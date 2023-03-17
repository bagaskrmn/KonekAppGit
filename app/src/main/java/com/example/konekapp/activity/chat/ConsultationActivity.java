package com.example.konekapp.activity.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.konekapp.R;
import com.example.konekapp.activity.chat.consultation.RecentConversationAdapter;
import com.example.konekapp.activity.chat.models.ChatMessagesModel;

import java.util.ArrayList;
import java.util.List;

public class ConsultationActivity extends AppCompatActivity {

    ImageView btnBack;
    LinearLayout btnAddNewConsultation;
    RecyclerView rvConversation;

    //recent conversation
    private List<ChatMessagesModel> listConversation;
    private RecentConversationAdapter recentConversationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultation);

        btnBack = findViewById(R.id.btnBack);
        btnAddNewConsultation = findViewById(R.id.btnAddNewConsultation);
        rvConversation = findViewById(R.id.rvConversation);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnAddNewConsultation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ConsultationActivity.this, TambahKonsultasiActivity.class);
                startActivity(intent);
            }
        });
    }

    void init() {
        listConversation = new ArrayList<>();
        recentConversationAdapter = new RecentConversationAdapter(listConversation);
        rvConversation.setAdapter(recentConversationAdapter);
        rvConversation.setHasFixedSize(true);
        rvConversation.setLayoutManager(new LinearLayoutManager(this));
    }
}