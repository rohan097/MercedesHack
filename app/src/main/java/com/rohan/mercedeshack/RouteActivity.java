package com.rohan.mercedeshack;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;

public class RouteActivity extends Activity {

    private LatLng mLatLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_route);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("Route Logging", "Place: " + place.getName());
                Log.i("Route Logging", "PlaceID: " + place.getLatLng().latitude);
                mLatLong = place.getLatLng();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("Route Logging", "An error occurred: " + status);
            }
        });

    }

    public void beginRide(View view)
    {
        Intent openMaps = new Intent(getBaseContext(), MapsActivity.class);
        Log.d("Route Debugging", String.valueOf(mLatLong.latitude));
        Log.d("Route Debuggin", String.valueOf(mLatLong.longitude));
        openMaps.putExtra("Latitude", mLatLong.latitude);
        openMaps.putExtra("Longitude", mLatLong.longitude);
        setResult(Activity.RESULT_OK, openMaps);
        finish();
        startActivityForResult(openMaps, 200);
    }

    public void goHome(View view) {
        Intent goHome = new Intent(getBaseContext(), HomePageActivity.class);
        startActivity(goHome);
    }
}
