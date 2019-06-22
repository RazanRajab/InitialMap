package com.example.map_bootcamp;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.map_bootcamp.weatherapi.WeatherResponse;
import com.example.map_bootcamp.weatherapi.WeatherService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private Snackbar sb;

    TextView latlng;
    TextView weather;
    TextView temp;
    TextView city;
    Marker marker;
    ProgressBar progressBar;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        latlng=findViewById(R.id.location);
        weather=findViewById(R.id.weather);
        temp=findViewById(R.id.temperature);
        city=findViewById(R.id.city);
        progressBar=findViewById(R.id.progress);
        progressBar.setVisibility(View.INVISIBLE);
        linearLayout=findViewById(R.id.layout);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        latlng.setText(sydney.toString());

        marker= mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setOnMapClickListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        latlng.setText(latLng.toString());
        linearLayout.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);


        String cityName ="";
        String stateName ="";
        String countryName="";
        if(isNetworkAvailable()) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                cityName = addresses.get(0).getAddressLine(0);
                stateName = addresses.get(0).getAddressLine(1);
                countryName = addresses.get(0).getAddressLine(2);

            } catch (IOException e) {
                e.printStackTrace();
            }

            fetchWeather(latLng);
            city.setText(cityName + ", " + stateName + ", " + countryName);
        }
        else{
            sb=Snackbar.make(findViewById(R.id.map),"Failed to get weather and cityName",Snackbar.LENGTH_LONG);
            sb.show();
            city.setText("Offline");
            weather.setText("");
            temp.setText("");
        }

        if (marker != null) {
            marker.remove();
            marker = mMap.addMarker(new MarkerOptions().position(latLng).title(cityName+", "+stateName+", "+countryName));
            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10.0f));
        } else {
            marker = mMap.addMarker(new MarkerOptions().position(latLng).title(latLng.latitude + " : " + latLng.longitude));
            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10.0f));
        }
    }
    private void fetchWeather(LatLng latLng){

        //Generate the service
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //status code header
        //200-300 successful
        //400 bad request
        //401/403 unauthorized/forbidden
        //500+ server error

        WeatherService service = retrofit.create(WeatherService.class);
        //Run the Request
        service.get("917e820ffd562cba3594e46a3312aae1",latLng.latitude+"",latLng.longitude+"")
                .enqueue(new Callback<WeatherResponse>() {
                    @Override
                    public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                        // HINT: response.body() contains the weather data
                        if (response.body() != null) {

                            Toast.makeText(MapsActivity.this,response.body().getmWeather().get(0).getmMain() + "", Toast.LENGTH_LONG).show();
                            linearLayout.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);

                            weather.setText(response.body().getmWeather().get(0).getmMain());
                            temp.setText(KelvinToCelsius(response.body().getmMain().getmTemp()));

                           /* try {

                                JSONObject j = new JSONObject();
                                JSONArray w = new JSONArray(j.getJSONArray("weather"));
                                weather.setText(w.getJSONObject(0).getString("main"));
                                temp.setText(j.getJSONObject("main").getString("temp") + "");

                            } catch (JSONException e) {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }*/
                        }
                    }
                    @Override
                    public void onFailure(Call<WeatherResponse> call, Throwable t) {
                        sb=Snackbar.make(findViewById(R.id.map),"Failed to get weather",Snackbar.LENGTH_LONG);
                        sb.show();
                    }
                });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    private String FahrenheitToCelsius(Double temp){
        double t=(temp-32.0)*(5.0/9.0);
        return (Math.round(t))+"°C";
    }
    private String KelvinToCelsius(Double temp){
        double t= temp-273.15;
        return (Math.round(t))+"°C";
    }
}
