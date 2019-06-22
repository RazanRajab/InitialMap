package com.example.map_bootcamp.weatherapi;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherResponse {
    @SerializedName("main")
    WeatherMain mMain;
    @SerializedName("weather")
    List<Weather> mWeather;

    public WeatherMain getmMain() {
        return mMain;
    }

    public List<Weather> getmWeather() {
        return mWeather;
    }
}
