package com.example.weather_week1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;

import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements LocationListener{

    EditText current_location;
    TextView display_location;
    Button search_;
    TextView temp;
    TextView condi;
    ImageView condiimg;
    protected double latitude,longitude;
    LocationManager locationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        current_location = findViewById(R.id.enterlocation);
        search_ = findViewById(R.id.search);
        display_location = findViewById(R.id.location);
        temp=findViewById(R.id.temperature);
        condi=findViewById(R.id.currentState);
        condiimg=findViewById(R.id.currentStateImage);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},1);
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);

    }
    public void display(View v)
    {

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);
        String op = current_location.getText().toString();
        display_location.setText(op);
        String url="https://api.weatherapi.com/v1/current.json?key=bd84a957b3f04594b5e115614222611&q="+op+"&aqi=no";
        RequestQueue queue =Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, response ->
        {
            try {
                JSONObject main_object = response.getJSONObject("current");
                String temp1 = String.valueOf(main_object.getDouble("temp_c"));
                JSONObject condition = main_object.getJSONObject("condition");
                String text1 = condition.getString("text");
                String img = condition.getString("icon");
                Picasso.get().load("https:"+img).into(condiimg);
                temp.setText(temp1+"°C");
                condi.setText(text1);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this,"LOCATION NOT AVAILABLE CHECK SPELLING",Toast.LENGTH_SHORT).show();
            }
        }
        );
        queue.add(jor);

    }
    @SuppressLint("SetTextI18n")
    public void locate(View v1) throws IOException {
        Toast.makeText(MainActivity.this,"GPS clicked",Toast.LENGTH_SHORT).show();
        Geocoder geo = new Geocoder(this.getApplicationContext(), Locale.getDefault());
        List<Address> addresses=geo.getFromLocation(latitude,longitude,1);
        if (addresses.isEmpty()) {
            display_location.setText("Waiting for Location.... Click Again");
        }
        else {
            String op1 = addresses.get(0).getLocality();
            display_location.setText(op1);
            String url="https://api.weatherapi.com/v1/current.json?key=bd84a957b3f04594b5e115614222611&q="+op1+"&aqi=no";
            RequestQueue queue =Volley.newRequestQueue(MainActivity.this);
            JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, response ->
            {
                try {
                    JSONObject main_object = response.getJSONObject("current");
                    String temp1 = String.valueOf(main_object.getDouble("temp_c"));
                    JSONObject condition = main_object.getJSONObject("condition");
                    String text1 = condition.getString("text");
                    String img = condition.getString("icon");
                    Picasso.get().load("https:"+img).into(condiimg);
                    temp.setText(temp1+"°C");
                    condi.setText(text1);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(MainActivity.this,error.toString(),Toast.LENGTH_SHORT).show();
                }
            }
            );
            queue.add(jor);
        }




    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        latitude=location.getLatitude();
        longitude=location.getLongitude();
        locationManager.removeUpdates(this);

    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        LocationListener.super.onLocationChanged(locations);
    }

    @Override
    public void onFlushComplete(int requestCode) {
        LocationListener.super.onFlushComplete(requestCode);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
