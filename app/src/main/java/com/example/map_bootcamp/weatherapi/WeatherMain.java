package com.example.map_bootcamp.weatherapi;

import com.google.gson.annotations.SerializedName;

public class WeatherMain {
    @SerializedName("temp")
    double mTemp;
    @SerializedName("temp_min")
    double mTempMin;
    @SerializedName("temp_max")
    double getmTempMax;

    public double getmTemp() {
        return mTemp;
    }
}
