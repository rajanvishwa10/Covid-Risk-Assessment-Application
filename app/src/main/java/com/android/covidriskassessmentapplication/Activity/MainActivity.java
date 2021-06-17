package com.android.covidriskassessmentapplication.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.covidriskassessmentapplication.R;
import com.android.covidriskassessmentapplication.Util.CheckScore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private CheckScore checkScore;
    private RelativeLayout progressLayout;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.score);
        progressLayout =  findViewById(R.id.rlProgressBar);
        progressLayout.setVisibility(View.VISIBLE);

        getData();
        checkScore = new CheckScore();
    }

    private void getData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //String name = snapshot.child("name").getValue(String.class);
                    int age = snapshot.child("age").getValue(Integer.class);
                    String city = snapshot.child("city").getValue(String.class);
                    int medical_condition = snapshot.child("medical_condition").getValue(Integer.class);
                    int vaccine_status = snapshot.child("vaccine_status").getValue(Integer.class);

                    progressLayout.setVisibility(View.GONE);
                    textView.setText(""+checkScore.getScore(age, medical_condition, vaccine_status));
                    Toast.makeText(MainActivity.this, ""+checkScore.getScore(age, medical_condition, vaccine_status), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}