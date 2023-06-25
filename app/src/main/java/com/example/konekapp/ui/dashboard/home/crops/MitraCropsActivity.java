package com.example.konekapp.ui.dashboard.home.crops;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.konekapp.R;
import com.example.konekapp.model.CropsModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class MitraCropsActivity extends AppCompatActivity {
    private Spinner SpinnerCommodity, SpinnerPeriod, SpinnerFertilizer;
    private String[] commodity = {"Kentang", "Cabai"};
    private String[] period = {"Pertama", "Kedua"};
    private String[] fertilizer = {"Kimia", "Organik"};

    private String Commodity, Period, Date, Qty, Location, Fertilizer, Result, Notes;

    private EditText CropsQty, CropsLocation, CropsResult, CropsNotes;
    private TextView CropsDate;
    private Button BtnMitraCropsDone;

    private View decorView;
    private ImageView MitraCropsBackAction;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference rootRef, usersRef, cropsRef;
    private ProgressDialog pd;
    private String currentUserId, cropsId, currentUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mitra_crops);
        SpinnerCommodity = findViewById(R.id.cropsCommodity);
        SpinnerPeriod = findViewById(R.id.cropsPeriod);
        SpinnerFertilizer = findViewById(R.id.cropsFertilizer);
        CropsDate = findViewById(R.id.cropsDate);
        CropsQty = findViewById(R.id.cropsQty);
        CropsLocation = findViewById(R.id.cropsLocation);
        CropsResult = findViewById(R.id.cropsResult);
        CropsNotes = findViewById(R.id.cropsNotes);
        BtnMitraCropsDone = findViewById(R.id.btnMitraCropsDone);

        MitraCropsBackAction = findViewById(R.id.mitraCropsBackAction);

        MitraCropsBackAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MitraCropsActivity.super.onBackPressed();
            }
        });

        BtnMitraCropsDone.setEnabled(false);
        CropsQty.addTextChangedListener(textWatcher);
        CropsLocation.addTextChangedListener(textWatcher);
        CropsResult.addTextChangedListener(textWatcher);
        CropsDate.addTextChangedListener(textWatcher);

        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility==0) {
                    decorView.setSystemUiVisibility(hideSystemBars());
                }
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        currentUserId = currentUser.getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        usersRef = rootRef.child("users");
        cropsRef = rootRef.child("crops");
        cropsId = cropsRef.push().getKey();

        //init pd
        pd = new ProgressDialog(MitraCropsActivity.this);
        pd.setTitle("Please Wait...");
        pd.setCanceledOnTouchOutside(false);


        ArrayAdapter<String> adapterCommodity=new ArrayAdapter<String>(MitraCropsActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, commodity);
        adapterCommodity.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        SpinnerCommodity.setAdapter(adapterCommodity);

        ArrayAdapter<String> adapterPeriod=new ArrayAdapter<String>(MitraCropsActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, period);
        adapterPeriod.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        SpinnerPeriod.setAdapter(adapterPeriod);

        ArrayAdapter<String> adapterFertilizer=new ArrayAdapter<String>(MitraCropsActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, fertilizer);
        adapterFertilizer.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        SpinnerFertilizer.setAdapter(adapterFertilizer);

        SpinnerCommodity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Commodity=parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        SpinnerPeriod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Period=parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        SpinnerFertilizer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Fertilizer = parent.getItemAtPosition(position).toString();
                //the string is in value2 variable
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        CropsDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();

                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        MitraCropsActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                CropsDate.setText(dayOfMonth + "/" + (month +1) + "/" + year);
                            }
                        },
                        year, month, day);
                datePickerDialog.show();
            }
        });

        usersRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentUserName = snapshot.child("name").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        BtnMitraCropsDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MitraCropsDone();
            }
        });

    }

    private void MitraCropsDone() {
        Date = CropsDate.getText().toString();
        Qty = CropsQty.getText().toString();
        Location = CropsLocation.getText().toString();
        Result = CropsResult.getText().toString();
        Notes = CropsNotes.getText().toString();

        //using model
        CropsModel cropsModel = new CropsModel(currentUserId, currentUserName, Commodity, Period, Date,
                Qty, Location, Fertilizer, Result, Notes, "0" );

            cropsRef.child(cropsId).setValue(cropsModel)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(MitraCropsActivity.this, "Data berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(MitraCropsActivity.this, MitraCropsStatusActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                String message = task.getException().toString();
                                Toast.makeText(MitraCropsActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String Date = CropsDate.getText().toString();
            String Qty = CropsQty.getText().toString();
            String Location = CropsLocation.getText().toString();
            String Result = CropsResult.getText().toString();

            BtnMitraCropsDone.setEnabled(!Date.isEmpty() && !Qty.isEmpty() && !Location.isEmpty() && !Result.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            decorView.setSystemUiVisibility(hideSystemBars());
        }
    }
    private int hideSystemBars() {
        return View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    }
}