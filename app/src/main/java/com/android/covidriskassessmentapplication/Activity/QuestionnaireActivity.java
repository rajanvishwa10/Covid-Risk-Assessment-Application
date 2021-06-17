package com.android.covidriskassessmentapplication.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.covidriskassessmentapplication.R;
import com.android.covidriskassessmentapplication.firebaseConfig.RemoteConfig;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionnaireActivity extends AppCompatActivity {

    private Button vaccineStatusButton, submit, medical_conditions;
    private RadioGroup radioGroup, radioGroup2;
    private AutoCompleteTextView autoCompleteTextView;
    private EditText fullNameEditText, age;
    int vaccine_status = 0, conditions = 0;
    private List<String> cityModel;
    private RelativeLayout progressLayout;
    private ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionaire);

        initViews();
        remoteConfig();

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.noVac:
                    vaccine_status = 0;
                    break;
                case R.id.halfVac:
                    vaccine_status = 1;
                    break;
                case R.id.fullVac:
                    vaccine_status = 2;
                    break;
            }
        });

        radioGroup2.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.no:
                    conditions = 0;
                    break;
                case R.id.yes:
                    conditions = 1;
                    break;
            }
        });
        submitData();
    }


    private void submitData() {
        submit.setOnClickListener(v -> {
            String name = fullNameEditText.getText().toString().trim();
            String city = autoCompleteTextView.getText().toString().trim();

            if (name.isEmpty()) {
                Snackbar.make(constraintLayout, "Name can not be empty", Snackbar.LENGTH_SHORT).show();
            } else if (!checkAge(age.getText().toString().trim())) {
                Snackbar.make(constraintLayout, "Age should be above 18 and below 80", Snackbar.LENGTH_SHORT).show();
            } else if (city.isEmpty()) {
                Snackbar.make(constraintLayout, "City can not be empty", Snackbar.LENGTH_SHORT).show();
            } else if (!checkCity(city)) {
                Snackbar.make(constraintLayout, "City is not in the list", Snackbar.LENGTH_SHORT).show();
            } else {
                progressLayout.setVisibility(View.VISIBLE);
                addToDatabase(name, Integer.parseInt(age.getText().toString()), vaccine_status, conditions, city);
            }

        });
    }

    private void addToDatabase(String name, int age, int vaccine_status, int conditions, String city) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("age", age);
        updates.put("vaccine_status", vaccine_status);
        updates.put("medical_condition", conditions);
        updates.put("city", city);

        databaseReference.updateChildren(updates).addOnSuccessListener(aVoid -> {
            progressLayout.setVisibility(View.GONE);
            Snackbar.make(constraintLayout, "Thank you for sharing", Snackbar.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }).addOnFailureListener(e -> {
            progressLayout.setVisibility(View.GONE);
            Snackbar.make(constraintLayout, "Error", Snackbar.LENGTH_SHORT).show();
        });
    }

    private boolean checkCity(String city) {
        return cityModel.contains(city);
    }

    private boolean checkAge(String ageText) {
        if (!ageText.isEmpty()) {
            int age = Integer.parseInt(ageText);
            return age >= 18 && age < 80;
        }
        return false;
    }

    private void remoteConfig() {
        cityModel = RemoteConfig.getCityConfig(this).getCity();
        List<String> city = new ArrayList<>();
        city.add("Mumbai");
        ArrayAdapter<String> adapter = new ArrayAdapter<>
                (this, android.R.layout.simple_list_item_1, city);
        autoCompleteTextView.setAdapter(adapter);
    }


    private void initViews() {
        vaccineStatusButton = findViewById(R.id.vaccineStatusButton);
        medical_conditions = findViewById(R.id.medicalRadioGroup);
        submit = findViewById(R.id.submit);
        radioGroup = findViewById(R.id.radioGroup);
        radioGroup2 = findViewById(R.id.radioGroup2);
        autoCompleteTextView = findViewById(R.id.cityAutoComplete);
        progressLayout = findViewById(R.id.rlProgressBar);

        fullNameEditText = findViewById(R.id.fullNameEditText);
        age = findViewById(R.id.age);

        constraintLayout = findViewById(R.id.parent);
    }
}