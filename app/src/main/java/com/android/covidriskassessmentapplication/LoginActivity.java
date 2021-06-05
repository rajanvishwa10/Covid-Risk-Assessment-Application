package com.android.covidriskassessmentapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
    }

    private void initView() {
        editText = findViewById(R.id.numberEditText);
    }

    public void sendOtp(View view) {
        Intent intent = new Intent(this, OTPActivity.class);
        intent.putExtra("phone", editText.getText().toString());
        startActivity(intent);
    }
}