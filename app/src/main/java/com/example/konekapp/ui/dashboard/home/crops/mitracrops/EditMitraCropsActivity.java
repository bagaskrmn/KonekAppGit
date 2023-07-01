package com.example.konekapp.ui.dashboard.home.crops.mitracrops;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

public class EditMitraCropsActivity extends AppCompatActivity {

    private ArrayList<CropsModel> list;
    private ImageView EditMitraCropsBackAction;
    private View decorView;
    private ProgressDialog pd;

    private Spinner SpinnerCommodity, SpinnerPeriod, SpinnerFertilizer;
    private String[] commodity = {"Pilih Komoditi", "Kentang", "Cabai"};
    private String[] period = {"Pilih Periode", "Pertama", "Kedua"};
    private String[] fertilizer = {"Pilih Pupuk", "Kimia", "Organik"};

    private ArrayList listCommodity = new ArrayList();
    private ArrayList listPeriod = new ArrayList();
    private ArrayList listFertilizer = new ArrayList();

    private String Commodity, Period, Date, Qty, Location, Fertilizer, Result, Notes;
    private EditText CropsQty, CropsLocation, CropsResult, CropsNotes;
    private TextView CropsDate;
    private Button BtnEditMitraCropsDone;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference rootRef, usersRef, cropsRef;

    private String currentUserId, cropsId, currentUserName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_mitra_crops);

        SpinnerCommodity = findViewById(R.id.editCropsCommodity);
        SpinnerPeriod = findViewById(R.id.editCropsPeriod);
        SpinnerFertilizer = findViewById(R.id.editCropsFertilizer);
        CropsDate = findViewById(R.id.editCropsDate);
        CropsQty = findViewById(R.id.editCropsQty);
        CropsLocation = findViewById(R.id.editCropsLocation);
        CropsResult = findViewById(R.id.editCropsResult);
        CropsNotes = findViewById(R.id.editCropsNotes);
        BtnEditMitraCropsDone = findViewById(R.id.btnEditMitraCropsDone);

        listCommodity.addAll(Arrays.asList(commodity));
        listPeriod.addAll(Arrays.asList(period));
        listFertilizer.addAll(Arrays.asList(fertilizer));

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        currentUserId = currentUser.getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        usersRef = rootRef.child("users");
        cropsRef = rootRef.child("crops");

        list = new ArrayList<>();

        Intent i = getIntent();
        cropsId = i.getStringExtra("CropsId");
        Log.d("EditMitraCrops", "cropsId: "+ cropsId);

        EditMitraCropsBackAction = findViewById(R.id.editMitraCropsBackAction);
        EditMitraCropsBackAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditMitraCropsActivity.super.onBackPressed();
            }
        });

        //init pd
        pd = new ProgressDialog(EditMitraCropsActivity.this);
        pd.setTitle("Please Wait...");
        pd.setCanceledOnTouchOutside(false);

        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility==0) {
                    decorView.setSystemUiVisibility(hideSystemBars());
                }
            }
        });

        BtnEditMitraCropsDone.setEnabled(false);
        CropsQty.addTextChangedListener(textWatcher);
        CropsLocation.addTextChangedListener(textWatcher);
        CropsResult.addTextChangedListener(textWatcher);
        CropsDate.addTextChangedListener(textWatcher);

        ArrayAdapter<String> adapterCommodity=new SpinnerAdapter(this, listCommodity);
        adapterCommodity.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        SpinnerCommodity.setAdapter(adapterCommodity);

        ArrayAdapter<String> adapterPeriod=new SpinnerAdapter(this, listPeriod);
        adapterPeriod.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        SpinnerPeriod.setAdapter(adapterPeriod);

        ArrayAdapter<String> adapterFertilizer=new SpinnerAdapter(this, listFertilizer);
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
                        EditMitraCropsActivity.this,
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

        //getData
        pd.setMessage("Memuat Data Monitoring");
        pd.show();
        cropsRef.addListenerForSingleValueEvent(listener);

        //uploadData
        BtnEditMitraCropsDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditMitraCropsDone();
            }
        });
    }

    private void EditMitraCropsDone() {
        Date = CropsDate.getText().toString();
        Qty = CropsQty.getText().toString();
        Location = CropsLocation.getText().toString();
        Result = CropsResult.getText().toString();
        Notes = CropsNotes.getText().toString();

        HashMap<String, Object> cropsMaps = new HashMap<>();
        cropsMaps.put("commodity", Commodity);
        cropsMaps.put("period", Period);
        cropsMaps.put("date", Date);
        cropsMaps.put("qty", Qty);
        cropsMaps.put("location", Location);
        cropsMaps.put("fertilizer", Fertilizer);
        cropsMaps.put("result", Result);
        cropsMaps.put("notes", Notes);

        cropsRef.child(cropsId).updateChildren(cropsMaps)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            EditMitraCropsActivity.super.onBackPressed();
                            Toast.makeText(EditMitraCropsActivity.this, "Data berhasil diubah", Toast.LENGTH_SHORT).show();
                        } else {
                            String message = task.getException().toString();
                            Toast.makeText(EditMitraCropsActivity.this, " : "+message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            list.clear();

            for (DataSnapshot ds : snapshot.getChildren()) {

                CropsModel cropsModel = ds.getValue(CropsModel.class);
                cropsModel.setCropsId(ds.getKey());
                //ini
                try {
                    if (cropsModel.getCropsId().equals(cropsId)) {
                        list.add(cropsModel);

                        if (cropsModel.getCommodity().equals("Kentang")) {
                            SpinnerCommodity.setSelection(1);
                        } else {
                            SpinnerCommodity.setSelection(2);
                        }

                        if (cropsModel.getPeriod().equals("Pertama")) {
                            SpinnerPeriod.setSelection(1);
                        }else {
                            SpinnerPeriod.setSelection(2);
                        }

                        if (cropsModel.getFertilizer().equals("Kimia")) {
                            SpinnerFertilizer.setSelection(1);
                        }else {
                            SpinnerFertilizer.setSelection(2);
                        }

                        CropsDate.setText(cropsModel.getDate());
                        CropsQty.setText(cropsModel.getQty());
                        CropsLocation.setText(cropsModel.getLocation());
                        CropsResult.setText(cropsModel.getResult());
                        CropsNotes.setText(cropsModel.getNotes());

                    }
                    pd.dismiss();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

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

            BtnEditMitraCropsDone.setEnabled(!Date.isEmpty() && !Qty.isEmpty() && !Location.isEmpty() && !Result.isEmpty());
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