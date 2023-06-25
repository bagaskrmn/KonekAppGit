package com.example.konekapp.ui.dashboard.home.crops;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.konekapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.HashMap;

public class CropsDetail extends AppCompatActivity {

    private TextView NameDetailCrops, PeriodDetailCrops, DateDetailCrops, QtyDetailCrops,
            LocationDetailCrops, FertilizerDetailCrops, ResultDetailCrops, NotesDetailCrops;

    private Button BtnApprovedDetailCrops, BtnChatDetailCrops;
    private DatabaseReference rootRef, cropsRef;
    private String cropsId, commodity;
    private ImageView DetailCropsClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crops_detail);

        rootRef = FirebaseDatabase.getInstance().getReference();
        cropsRef = rootRef.child("crops");

        NameDetailCrops = findViewById(R.id.nameDetailCrops);
        PeriodDetailCrops = findViewById(R.id.periodDetailCrops);
        DateDetailCrops = findViewById(R.id.dateDetailCrops);
        QtyDetailCrops = findViewById(R.id.qtyDetailCrops);
        LocationDetailCrops = findViewById(R.id.locationDetailCrops);
        FertilizerDetailCrops = findViewById(R.id.fertilizerDetailCrops);
        ResultDetailCrops = findViewById(R.id.resultDetailCrops);
        NotesDetailCrops = findViewById(R.id.notesDetailCrops);
        BtnApprovedDetailCrops = findViewById(R.id.btnApproveDetailCrops);
        BtnChatDetailCrops = findViewById(R.id.btnChatDetailCrops);
        DetailCropsClose = findViewById(R.id.detailCropsClose);

        Intent i = getIntent();
        cropsId = i.getStringExtra("CropsId");
        commodity = i.getStringExtra("Commodity");

        DetailCropsClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CropsDetail.this, AhliTaniCropsActivity.class);
                i.putExtra("Commodity", commodity);
                startActivity(i);
                finish();
            }
        });

        //getData with cropsId
        cropsRef.child(cropsId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue().toString();
                String period = snapshot.child("period").getValue().toString();
                String date = snapshot.child("date").getValue().toString();
                String qty = snapshot.child("qty").getValue().toString();
                String location = snapshot.child("location").getValue().toString();
                String fertilizer = snapshot.child("fertilizer").getValue().toString();
                String result = snapshot.child("result").getValue().toString();
                String notes = snapshot.child("notes").getValue().toString();

                NameDetailCrops.setText(name);
                PeriodDetailCrops.setText(period);
                DateDetailCrops.setText(date);
                QtyDetailCrops.setText(qty);
                LocationDetailCrops.setText(location);
                FertilizerDetailCrops.setText(fertilizer);
                ResultDetailCrops.setText(result);
                NotesDetailCrops.setText(notes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        BtnApprovedDetailCrops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> cropsMaps = new HashMap<>();
                cropsMaps.put("status", "1");

                cropsRef.child(cropsId).updateChildren(cropsMaps)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Intent i = new Intent(CropsDetail.this, AhliTaniCropsActivity.class);
                                    i.putExtra("Commodity", commodity);
                                    startActivity(i);
                                    finish();
                                    Toast.makeText(CropsDetail.this, "Data Disetujui", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    String message = task.getException().toString();
                                    Toast.makeText(CropsDetail.this, "Error: "+message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}