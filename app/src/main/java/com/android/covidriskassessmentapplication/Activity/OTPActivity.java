package com.android.covidriskassessmentapplication.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.covidriskassessmentapplication.R;
import com.chaos.view.PinView;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OTPActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView otpTextView;
    FirebaseAuth mAuth;
    String codesent, phone;
    RelativeLayout progressLayout, successfulLayout;
    LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_t_p);
        initViews();
        mAuth = FirebaseAuth.getInstance();

        phone = getIntent().getStringExtra("phone");
        String sourceString = "Enter the OTP sent to " + "<b>" + phone + "</b> ";
        otpTextView.setText(Html.fromHtml(sourceString));

        sendVerificationCode(phone);

    }

    public void verify(View view) {
        PinView editText = findViewById(R.id.pinView);
        String otp = editText.getText().toString().trim();

        if (otp.isEmpty()) {
            editText.setError("Enter OTP");
            editText.requestFocus();
        } else if (otp.length() < 6) {
            editText.setError("OTP should be 6 Digits");
            editText.requestFocus();
        } else {
            progressLayout.setVisibility(View.VISIBLE);
            verifyCode(otp);
        }

        Log.d("OTP", "otp is " + otp);

    }

    private void sendVerificationCode(String phone) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+91" + phone)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks
            = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                verifyCode(code);
            }
            //signInWithCredential(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(OTPActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d("error", e.getMessage());
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            codesent = s;
        }
    };

    private void verifyCode(String code) {
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codesent, code);
            signInWithCredential(credential);
        } catch (Exception e) {
            Log.i("exception", e.toString());
            Toast.makeText(OTPActivity.this, "Invalid credentials", Toast.LENGTH_LONG).show();
        }
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = firebaseDatabase.getReference("Users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("phoneNumber", phone);
                        updates.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        myRef.setValue(updates).addOnCompleteListener(task1 -> {
                            if (task1.isComplete()) {
                                progressLayout.setVisibility(View.GONE);
                                successfulLayout.setVisibility(View.VISIBLE);
                                new Handler().postDelayed(() -> {
                                    Intent intent = new Intent(OTPActivity.this, QuestionnaireActivity.class);
                                    startActivity(intent);
                                }, 3000);

                            }
                        });
                    } else {
                        progressLayout.setVisibility(View.GONE);
                        Toast.makeText(OTPActivity.this, "Invalid Otp", Toast.LENGTH_SHORT).show();
                        // Sign in failed, display a left_message and update the UI
                        Log.w("signinfail", "signInWithCredential:failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(OTPActivity.this, "Invalid Otp", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        otpTextView = findViewById(R.id.otpTextView);
        lottieAnimationView = findViewById(R.id.successfulLottie);
        progressLayout = findViewById(R.id.rlProgressBar);
        successfulLayout = findViewById(R.id.successfulLayout);

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }
}