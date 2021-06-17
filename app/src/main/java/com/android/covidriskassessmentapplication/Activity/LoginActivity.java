package com.android.covidriskassessmentapplication.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.covidriskassessmentapplication.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText editText;
    private FirebaseAuth mAuth;
    private ConstraintLayout constraintLayout;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
    }

    private void initView() {
        editText = findViewById(R.id.numberEditText);
        constraintLayout = findViewById(R.id.parent);
    }

    public void sendOtp(View view) {
        String phone = editText.getText().toString().trim();
        if (phone.isEmpty()) {
            Toast.makeText(this, "Contact Number can not be empty", Toast.LENGTH_SHORT).show();
            Snackbar.make(constraintLayout, "Contact Number can not be empty", Snackbar.LENGTH_SHORT).show();
        } else if (phone.length() < 10) {
            Toast.makeText(this, "Incorrect Contact Number", Toast.LENGTH_SHORT).show();
            Snackbar.make(constraintLayout, "Incorrect Contact Number", Snackbar.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this, OTPActivity.class);
            intent.putExtra("phone", editText.getText().toString());
            startActivity(intent);
        }

    }
}