package com.android.covidriskassessmentapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText editText;
    FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth= FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null){
            Intent intent=new Intent(this,MainActivity.class);
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
    }

    public void sendOtp(View view) {
        Intent intent = new Intent(this, OTPActivity.class);
        intent.putExtra("phone", editText.getText().toString());
        startActivity(intent);
    }
}