package com.example.map_bootcamp.weatherapi;

import com.google.gson.annotations.SerializedName;

public class Weather {
    @SerializedName("main")
    String mMain;
    @SerializedName("description")
    String mDescription;

    public String getmMain() {
        return mMain;
    }

    public String getmDescription() {
        return mDescription;
    }
}
