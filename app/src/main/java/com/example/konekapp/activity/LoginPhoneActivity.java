package com.example.konekapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.konekapp.R;
import com.example.konekapp.databinding.ActivityLoginPhoneBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class LoginPhoneActivity extends AppCompatActivity {

    private ActivityLoginPhoneBinding binding;




    private ImageView LoginBackAction;

    //used to resend OTP
    private PhoneAuthProvider.ForceResendingToken forceResendingToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private static final String TAG = "MAIN_TAG";
    private FirebaseAuth firebaseAuth;
    private DatabaseReference rootRef, usersRef, currentUserRef;
    private ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone);


        binding = ActivityLoginPhoneBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.phoneLl.setVisibility(View.VISIBLE);
        binding.codeLl.setVisibility(View.GONE);

        LoginBackAction = findViewById(R.id.loginBackAction);

        //init progress dialog
        pd = new ProgressDialog(this);
        pd.setTitle("Please wait...");
        pd.setCanceledOnTouchOutside(false);

        //firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        //database ref
        rootRef = FirebaseDatabase.getInstance().getReference();
        usersRef = rootRef.child("Users");

        LoginBackAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toHomeIntent = new Intent(LoginPhoneActivity.this, HomeScreenActivity.class);
                startActivity(toHomeIntent);
            }
        });

        binding.loginPhoneNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                if (s.toString().equals("")) {
                    binding.btnSendOTP.setEnabled(false);
                } else {
                    binding.btnSendOTP.setEnabled(true);
                }
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    binding.btnSendOTP.setEnabled(false);
                } else {
                    binding.btnSendOTP.setEnabled(true);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        binding.enterOTPCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.toString().equals("")) {
                    binding.btnVerifyOTP.setEnabled(false);
                } else {
                    binding.btnVerifyOTP.setEnabled(true);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    binding.btnVerifyOTP.setEnabled(false);
                } else {
                    binding.btnVerifyOTP.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            //for 2 situations :
            //1. Instant verification, some phones can instantly verified without entering the OTP code
            //2. Auto-retrieval, some phones can auto-detect incoming SMS verification without user action
                Log.d(TAG, "onVerificationCompleted" + phoneAuthCredential);
                signInWithPhoneAuthCredential(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
            //for invalid request or phone number format is invalid
                Log.d(TAG, "onVerificationFailed", e);
                if (e instanceof FirebaseTooManyRequestsException) {
                    //the SMS quota for the project has been exceeded
                    pd.dismiss();
                    Toast.makeText(LoginPhoneActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    pd.dismiss();
                    Toast.makeText(LoginPhoneActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                //OTP verification has been sent
                Log.d(TAG, "onCodeSent" + s);

                // Save verification ID and resending token so we can use them later
                // s change to verificationId
                mVerificationId = s;
                //forceResendingToken = token;
                pd.dismiss();

                //hide phone Layout
                //show OTP code Layout
                binding.phoneLl.setVisibility(View.GONE);
                binding.codeLl.setVisibility(View.VISIBLE);

                Toast.makeText(LoginPhoneActivity.this, "Verification code sent", Toast.LENGTH_SHORT).show();

                String phone = binding.loginPhoneNo.getText().toString().trim();
                String phoneNumber = "+62" + phone;

                binding.descOTPCodeSent.setText("Masukkan SMS OTP yang telah kami kirim " +
                        "\nke " + phoneNumber);

            }
        };

        binding.btnSendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.loginPhoneNo.requestFocus();

                //get phone number
                String phone = binding.loginPhoneNo.getText().toString().trim();

                //function to remove leading zeros from string
                ;

                String phoneNumber = "+62" + removeLeadingZeros(phone);
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(LoginPhoneActivity.this, "Masukkan nomor HP anda", Toast.LENGTH_SHORT).show();
                } else {
                    startPhoneNumberVerification(phoneNumber);
                }
            }
        });

        binding.resendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = binding.loginPhoneNo.getText().toString().trim();
                String phoneNumber = "+62" + phone;
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(LoginPhoneActivity.this, "Masukkan nomor HP anda", Toast.LENGTH_SHORT).show();
                } else {
                    resendVerificationCode(phoneNumber, forceResendingToken);
                }
            }
        });

        binding.btnVerifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.enterOTPCode.requestFocus();
                String code = binding.enterOTPCode.getText().toString().trim();
                if (TextUtils.isEmpty(code)) {
                    Toast.makeText(LoginPhoneActivity.this, "Masukkan kode OTP yang telah dikirim", Toast.LENGTH_SHORT).show();
                } else {
                    verifyPhoneNumberWithCode(mVerificationId, code);
                }
            }
        });
    }

    //check user first
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser !=null) {
            //error
            pd.setMessage("Memeriksa akun");
            pd.show();

            String currentUserId = currentUser.getUid();
            usersRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild("Nama")) {
                        pd.dismiss();
                        Intent registeredUserIntent = new Intent(LoginPhoneActivity.this, MainActivity.class);
                        usersRef.removeEventListener(this);
                        registeredUserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(registeredUserIntent);
                    } else {
                        pd.dismiss();
                        Intent newUserIntent = new Intent(LoginPhoneActivity.this, CompleteProfileActivity.class);
                        usersRef.removeEventListener(this);
                        newUserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(newUserIntent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    //Start Phone Number Auth
    private void startPhoneNumberVerification(String phoneNumber) {
        pd.setMessage("Verifying Phone Number");
        pd.show();

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNumber)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // Activity (for callback binding)
                .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    //Resend Verification Code
    //forceResendingToken change to token
    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
        pd.setMessage("Resending Code");
        pd.show();

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNumber)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // Activity (for callback binding)
                .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                .setForceResendingToken(forceResendingToken)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    //Verifying Number with Code
    //s change to verificationId
    private void verifyPhoneNumberWithCode(String s, String code) {
        pd.setMessage("Verifying Code");
        pd.show();
        //error here
        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(s, code);
        signInWithPhoneAuthCredential(phoneAuthCredential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        firebaseAuth.signInWithCredential(phoneAuthCredential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        pd.setMessage("Logging In");
                        pd.show();
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        String phoneNumber = user.getPhoneNumber();
                        Toast.makeText(LoginPhoneActivity.this, "Success login as " + phoneNumber, Toast.LENGTH_SHORT).show();

                        String currentUserId = user.getUid();

                        //root database reference
                        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
//                        rootRef.child("Users").child(currentUserId).setValue(""); //value for Uid is ""

                        //getReference().child("Users")
                        //database child Users reference
                        //getReference("Users")
                        DatabaseReference usersRef = rootRef.child("Users");
                        HashMap<String, Object> profileMap = new HashMap<>();
                        profileMap.put("Role", "1");
                        usersRef.child(currentUserId).updateChildren(profileMap).
                                addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                DatabaseReference currentUserRef = usersRef.child(currentUserId);
                                currentUserRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.hasChild("Nama")) {
                                            Intent registeredUserIntent = new Intent(LoginPhoneActivity.this, MainActivity.class);
                                            currentUserRef.removeEventListener(this);
                                            registeredUserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(registeredUserIntent);
                                        } else {
                                            Intent newUserIntent = new Intent(LoginPhoneActivity.this, CompleteProfileActivity.class);
                                            currentUserRef.removeEventListener(this);
                                            newUserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(newUserIntent);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(LoginPhoneActivity.this, ""+ error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(LoginPhoneActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String removeLeadingZeros(String phone) {
        //0+ means = replace one or more zero on begining of the string
        String regex = "0+(?!$)";
        phone = phone.replace(regex,"");
        return phone;
    }
}