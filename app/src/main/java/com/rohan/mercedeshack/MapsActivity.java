package com.rohan.mercedeshack;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private Double mLatitude;
    private Double mLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_maps);
        mLatitude = getIntent().getExtras().getDouble("Latitude");
        mLongitude = getIntent().getExtras().getDouble("Longitude");
        Log.d("Maps Debugging", String.valueOf(mLatitude));
        Log.d("Maps Debugging", String.valueOf(mLongitude));
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        // Add a marker in Sydney, Australia, and move the camera.
        LatLng source = new LatLng(12.978054, 77.712876);
        LatLng destination = new LatLng(mLatitude, mLongitude);

        mMap.addMarker(new MarkerOptions().position(destination).title("Your Destination"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(destination));
        mMap.addMarker(new MarkerOptions().position(source).title("Your Location"));

        PolylineOptions line=
                new PolylineOptions()
                        .add(source, destination)
                        .width(5).color(Color.RED);

        mMap.addPolyline(line);

    }

    public void goHome(View view) {
        Intent openHomeActivity = new Intent(getBaseContext(), HomePageActivity.class);
        startActivity(openHomeActivity);
    }
}