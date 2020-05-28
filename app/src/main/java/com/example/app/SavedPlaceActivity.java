package com.example.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.fragment.app.FragmentActivity;

import com.example.app.factories.MarkerFactory;
import com.example.app.finals.MapsParameters;
import com.example.app.finals.MapsUtility;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;

import java.util.Objects;

/**
 * Map where a single saved place it is showed when clicked
 */
public class SavedPlaceActivity extends FragmentActivity implements
        OnMapReadyCallback {

    private FusedLocationProviderClient fusedLocationProviderClient;
    private GoogleMap mMap;
    private MapView mapView;


    /**
     * Callback when the activity is created
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(SavedPlaceActivity.this);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 40, 180);
        }

        final LocationRequest locationRequest = MapsUtility.createLocationRequest();

        final LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Intent placeInfo = getIntent();
                Place placeToShow = placeInfo.getParcelableExtra("place");
                assert placeToShow != null;
                showPlace(placeToShow);
            }
        });
    }


    /**
     * Show the place on the map
     * @param place to show
     */
    private void showPlace(Place place){
        MarkerOptions marker = MarkerFactory.createBasicMarker(Objects.requireNonNull(place.getLatLng()), Objects.requireNonNull(place.getName()));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), MapsParameters.SINGLE_PLACE_ZOOM));
        mMap.addMarker(marker);
    }





}
