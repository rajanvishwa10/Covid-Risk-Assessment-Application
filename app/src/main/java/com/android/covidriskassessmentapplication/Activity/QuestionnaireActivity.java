package com.android.covidriskassessmentapplication.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.covidriskassessmentapplication.R;
import com.android.covidriskassessmentapplication.firebaseConfig.RemoteConfig;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class QuestionnaireActivity extends AppCompatActivity {

    private Button vaccineStatusButton, submit;
    private RadioGroup radioGroup;
    private boolean isClicked = true;
    private AutoCompleteTextView autoCompleteTextView;
    private EditText fullNameEditText, age, medical;
    int vaccine_status = 0;
    private List<String> cityModel;
    private ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionaire);

        initViews();
        setRadioGroup();
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
        submitData();
    }


    private void submitData() {
        submit.setOnClickListener(v -> {
            String name = fullNameEditText.getText().toString().trim();
            String medical_condition = medical.getText().toString().trim();
            String city = autoCompleteTextView.getText().toString().trim();

            if (name.isEmpty()) {
                Snackbar.make(constraintLayout, "Name can not be empty", Snackbar.LENGTH_SHORT).show();
            }  else if (!checkAge(age.getText().toString().trim())) {
                Snackbar.make(constraintLayout, "Age should be above 18", Snackbar.LENGTH_SHORT).show();
            } else if (medical_condition.isEmpty()) {
                Snackbar.make(constraintLayout, "Medical Condition can not be empty", Snackbar.LENGTH_SHORT).show();
            } else if (city.isEmpty()) {
                Snackbar.make(constraintLayout, "City can not be empty", Snackbar.LENGTH_SHORT).show();
            } else if (!checkCity(city)) {
                Snackbar.make(constraintLayout, "City is not in the list", Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(constraintLayout, "Thank you for sharing", Snackbar.LENGTH_SHORT).show();
            }

        });
    }

    private boolean checkCity(String city) {
        return cityModel.contains(city);
    }

    private boolean checkAge(String ageText) {
        if(!ageText.isEmpty()){
            int age = Integer.parseInt(ageText);
            return age >= 18 && age < 80;
        }
        return false;
    }

    private void remoteConfig() {
        cityModel = RemoteConfig.getCityConfig(this).getCity();
        ArrayAdapter<String> adapter = new ArrayAdapter<>
                (this, android.R.layout.select_dialog_item, cityModel);
        autoCompleteTextView.setAdapter(adapter);
    }

    private void setRadioGroup() {
        vaccineStatusButton.setOnClickListener(v -> {
            if (isClicked) {
                radioGroup.setVisibility(View.VISIBLE);
                vaccineStatusButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_arrow_drop_up_24, 0);
                isClicked = false;
            } else {
                radioGroup.setVisibility(View.GONE);
                vaccineStatusButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_arrow_drop_down_24, 0);
                isClicked = true;
            }
        });
    }

    private void initViews() {
        vaccineStatusButton = findViewById(R.id.vaccineStatusButton);
        submit = findViewById(R.id.submit);
        radioGroup = findViewById(R.id.radioGroup);
        autoCompleteTextView = findViewById(R.id.cityAutoComplete);

        fullNameEditText = findViewById(R.id.fullNameEditText);
        age = findViewById(R.id.age);
        medical = findViewById(R.id.medical);

        constraintLayout = findViewById(R.id.parent);
    }
}