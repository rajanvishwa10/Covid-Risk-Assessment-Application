package com.android.covidriskassessmentapplication.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.covidriskassessmentapplication.R;
import com.android.covidriskassessmentapplication.Util.CheckScore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private CheckScore checkScore;
    private RelativeLayout progressLayout;
    private CardView groceryCv, schoolCv, mallCv, restaurantCv, autoCv, busCv;
    private ScrollView scrollView;
    int TOTAL_SCORE;
    Dialog dialog;
    private TextView stayHomeTV;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        progressLayout.setVisibility(View.VISIBLE);

        getData();
        checkScore = new CheckScore();

        onClickListener();
    }

    private void showStartPrompt() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_select_option);
        dialog.setCancelable(false);
        Window window = dialog.getWindow();
        window.getAttributes().windowAnimations = R.style.DialogAnimation;

        CardView stayHome = window.findViewById(R.id.stay_home);
        CardView stepOutside = window.findViewById(R.id.step_outside);

        stayHome.setOnClickListener(v -> {
            dialog.dismiss();
            scrollView.setVisibility(View.GONE);
            stayHomeTV.setVisibility(View.VISIBLE);
        });

        stepOutside.setOnClickListener(v -> {
            dialog.dismiss();
            showAlertDialog();
        });

        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Be careful when out of home. Wear a clean and safe mask properly. Ensure you maintain physical distancing from others, avoid touching surfaces and avoid going into crowded non-venilated places. Places which follow all norms are called GAG - Good Adherence to Guidance and Places which do not follow norms fully are LAG, Low Adherence to Guidance");
        builder.setPositiveButton("I understand", (dialog, which) -> {
            dialog.dismiss();
            //scrollView.setVisibility(View.VISIBLE);
            stayHomeTV.setVisibility(View.GONE);
            showVehicleDialog();
        });
        builder.setNegativeButton("I want to Stay Home", (dialog1, which) -> {
            dialog1.dismiss();
            scrollView.setVisibility(View.GONE);
            stayHomeTV.setVisibility(View.VISIBLE);
        });
        builder.create().show();
    }

    private void showVehicleDialog() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_vehicle);
        dialog.setCancelable(false);
        Window window = dialog.getWindow();
        window.getAttributes().windowAnimations = R.style.DialogAnimation;

        CardView autoCv = window.findViewById(R.id.autoCv);
        CardView busCv = window.findViewById(R.id.busCv);
        Button button = window.findViewById(R.id.button);

        autoCv.setOnClickListener(v -> {
            showLaG_GAG_Dialog("auto", 15, 30);
            dialog.dismiss();
        });

        busCv.setOnClickListener(v -> {
            showTimeDialog(30, 60, -1, "bus");
            dialog.dismiss();
        });

        button.setOnClickListener(v -> {
            scrollView.setVisibility(View.VISIBLE);
            dialog.dismiss();
        });

        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    private void onClickListener() {
        groceryCv.setOnClickListener(v -> {
            showTimeDialog(15, 60, -1, "grocery");
        });
        schoolCv.setOnClickListener(v -> {
            showLaG_GAG_Dialog("school", 180, 360);
        });
        mallCv.setOnClickListener(v -> {
            showLaG_GAG_Dialog("mall", 60, 120);
        });
        restaurantCv.setOnClickListener(v -> {
            showLaG_GAG_Dialog("restaurant", 60, 120);
        });
//        autoCv.setOnClickListener(v -> {
//            showLaG_GAG_Dialog("auto", 15, 30);
//        });
//        busCv.setOnClickListener(v -> {
//            showTimeDialog(30, 60, -1, "bus");
//        });

    }

    private void showTimeDialog(int time1, int time2, int adherence, String activity) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_show_time);
        dialog.setCancelable(false);
        Window window = dialog.getWindow();

        CardView time1cv = window.findViewById(R.id.time1cv);
        CardView time2cv = window.findViewById(R.id.time2cv);

        TextView time1Tv = window.findViewById(R.id.time1);
        TextView time2Tv = window.findViewById(R.id.time2);

        time1Tv.setText(time1 + " mins");
        time2Tv.setText(time2 + " mins");

        time1cv.setOnClickListener(v -> {
            int score;
            switch (activity) {
                case "school":
                case "mall":
                case "restaurant":
                    if (adherence == 0) {
                        score = TOTAL_SCORE + 3;
                        Toast.makeText(this, "" + score, Toast.LENGTH_SHORT).show();

                    } else {
                        score = TOTAL_SCORE + 6;
                        Toast.makeText(this, "" + score, Toast.LENGTH_SHORT).show();

                    }
                    showWarningDialog(score);
                    break;
                case "auto":
                    if (adherence == 0) {
                        TOTAL_SCORE = TOTAL_SCORE + 2;
                    } else {
                        TOTAL_SCORE = TOTAL_SCORE + 4;
                    }
                    scrollView.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "" + TOTAL_SCORE, Toast.LENGTH_SHORT).show();

                    //showWarningDialog(score);
                    break;
                case "bus":
                    TOTAL_SCORE = TOTAL_SCORE + 4;
                    scrollView.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "" + TOTAL_SCORE, Toast.LENGTH_SHORT).show();
                    //showWarningDialog(score);
                    break;
                case "grocery":
                    score = TOTAL_SCORE + 2;
                    showWarningDialog(score);
                    break;


            }
            dialog.dismiss();
        });

        time2cv.setOnClickListener(v -> {
            switch (activity) {
                case "school":
                case "mall":
                case "restaurant":
                    int score;
                    if (adherence == 0) {
                        score = TOTAL_SCORE + 6;
                    } else {
                        score = TOTAL_SCORE + 12;
                    }
                    Toast.makeText(this, "" + score, Toast.LENGTH_SHORT).show();
                    showWarningDialog(score);
                    break;
                case "auto":
                    if (adherence == 0) {
                        TOTAL_SCORE = TOTAL_SCORE + 6;
                    } else {
                        TOTAL_SCORE = TOTAL_SCORE + 10;
                    }
                    scrollView.setVisibility(View.VISIBLE);
//                    showWarningDialog(score);
                    break;
                case "bus":
                    TOTAL_SCORE = TOTAL_SCORE + 8;
                    scrollView.setVisibility(View.VISIBLE);
//                    showWarningDialog(score);
                    break;
                case "grocery":
                    score = TOTAL_SCORE + 4;
                    showWarningDialog(score);
                    break;
            }
            dialog.dismiss();
        });

        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    private void showWarningDialog(int score) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_warn_user);
        dialog.setCancelable(true);
        Window window = dialog.getWindow();

        TextView textView = window.findViewById(R.id.textView);
        if (score <= 11) {
            textView.setText("Low probability of infection");
            textView.setTextColor(ContextCompat.getColor(window.getContext(), R.color.green_400));
        } else {
            textView.setText("High probability of infection");
            textView.setTextColor(ContextCompat.getColor(window.getContext(), R.color.red_400));
        }

        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    private void showLaG_GAG_Dialog(String activity, int s, int s1) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_select_lag_gag);
        dialog.setCancelable(false);
        Window window = dialog.getWindow();
        window.getAttributes().windowAnimations = R.style.DialogAnimation;

        CardView gag = window.findViewById(R.id.gag);
        CardView lag = window.findViewById(R.id.lag);

        gag.setOnClickListener(v -> {
            showTimeDialog(s, s1, 0, activity);
            dialog.dismiss();
        });

        lag.setOnClickListener(v -> {
            showTimeDialog(s, s1, 1, activity);
            dialog.dismiss();
        });

        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    private void initViews() {
        progressLayout = findViewById(R.id.rlProgressBar);
        groceryCv = findViewById(R.id.groceryCv);
        schoolCv = findViewById(R.id.schoolCv);
        mallCv = findViewById(R.id.mallCv);
        restaurantCv = findViewById(R.id.restaurantCv);
        autoCv = findViewById(R.id.autoCv);
        busCv = findViewById(R.id.busCv);
        scrollView = findViewById(R.id.scrollView);
        stayHomeTV = findViewById(R.id.stayHomeTv);
        toolbar = findViewById(R.id.score);
        toolbar.setOnMenuItemClickListener(this::onOptionsItemSelected);
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

                    TOTAL_SCORE = checkScore.getScore(age, medical_condition, vaccine_status);

                    progressLayout.setVisibility(View.GONE);
                    showStartPrompt();
                    //textView.setText(""+checkScore.getScore(age, medical_condition, vaccine_status));
                    Toast.makeText(MainActivity.this, "" + checkScore.getScore(age, medical_condition, vaccine_status), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sign_out, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.signOut) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finishAndRemoveTask();
        }
            return super.onOptionsItemSelected(item);

    }
}