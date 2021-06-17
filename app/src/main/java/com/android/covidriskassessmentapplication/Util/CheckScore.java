package com.android.covidriskassessmentapplication.Util;

public class CheckScore {

    private int positivity_rate = 6, age_score,
            medical_score, vaccine_score, positivity_score;

    /*
            if age 18-44 1
            45-60 2
            60+ 4

            cormorbidity score yes = 2 no = 0

            vaccine score both doses = 0
            single dose = 2
            not vaccinated = 6

            city positivity score
            positivity rate <= 5  = 0
            positivity rate > 5   = 2

            score range 1-14

            low vulnerability = score < 4
            high vulnerability = score between 5 and 14

            Out of home situation score will be added to individual's score

             if score <= 11 Low probability of infection
             if score > 11 High probability of infection
     */


    public int getScore(int age, int medical_condition, int vaccine_status) {
        int total = calculate_age(age) + calculate_comorbidity(medical_condition) + calculate_vaccine(vaccine_status) + calculate_pos_rate();
        return total;
    }

    private int calculate_age(int age) {
        if (age >= 18 && age <= 44)
            age_score = 1;
        else if (age >= 45 && age <= 60)
            age_score = 2;
        else if (age > 60) {
            age_score = 4;
        }
        return age_score;
    }

    private int calculate_comorbidity(int medical_condition) {
        if (medical_condition == 0) {
            medical_score = 0;
        } else {
            medical_score = 2;
        }
        return medical_score;
    }

    private int calculate_vaccine(int vaccine_status) {
        if (vaccine_status == 2) {
            vaccine_score = 0;
        } else if (vaccine_status == 1) {
            vaccine_score = 2;
        } else {
            vaccine_score = 6;
        }
        return vaccine_score;
    }

    private int calculate_pos_rate() {
        if (positivity_rate <= 5) {
            positivity_score = 0;
        } else {
            positivity_score = 2;
        }
        return positivity_score;
    }

}
