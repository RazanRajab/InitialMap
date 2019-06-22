package com.example.map_bootcamp.weatherapi;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {
    //Original for reference:
    //https://api.openweathermap.org/data/2.5/weather?lat=35&lon=139
    @GET("/data/2.5/weather")
    Call<WeatherResponse> get(@Query("APPID") String token, @Query("lat") String latitude, @Query("lon") String longitude);

}
