package com.android.covidriskassessmentapplication.firebaseConfigModel;

import java.util.List;

public class MainScreenConfig {
    List<String> city;

    public MainScreenConfig(List<String> city) {
        this.city = city;
    }

    public List<String> getCity() {
        return city;
    }

    public void setSizes(List<String> city) {
        this.city = city;
    }
}
