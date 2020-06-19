package com.example.app;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.fragment.app.FragmentActivity;

import com.example.app.factories.IntentFactory;
import com.example.app.factories.MarkerFactory;
import com.example.app.finals.MapsParameters;
import com.example.app.finals.MapsUtility;
import com.example.app.listeners.HomeSetListener;
import com.example.app.sensors.GoogleLocationFinder;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

/**
 * Maps where user can interact, set and view his home
 */
public class HomeActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    private MapView mapView;

    public Location homeLocation;

    // Shared preference keys
    public static final String HOME_LAT = "home_lat";
    public static final String HOME_LNG = "home_long";

    private HomeSetListener homeSetListener;
    private GoogleLocationFinder googleLocationFinder = new GoogleLocationFinder();

    private void setHomeSetListener(HomeSetListener listener) {
        this.homeSetListener = listener;
    }

    /**
     * Callback when the activity is created
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_place);

        // Setting full screen activity
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    /**
     * Callback when the map is ready
     * @param googleMap The GoogleMap reference
     */
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

        task.addOnSuccessListener(this, locationSettingsResponse -> {
            setHome();
            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                /**
                 * Listener all session open in order to let the user set
                 * a home manually
                 * @param latLng LatLng position the user clicked
                 */
                @Override
                public void onMapLongClick(LatLng latLng) {
                    mMap.clear();
                    homeLocation.setLatitude(latLng.latitude);
                    homeLocation.setLongitude(latLng.longitude);
                    mMap.addMarker(MarkerFactory.createHomeMarker(getResources().getString(R.string.home_sweet), latLng.latitude, latLng.longitude));
                }
            });
        });
        setHomeSetListener(new HomeSetListener() {
            /**
             * Callback when home is well set
             */
            @Override
            public void onHomeSet() {
                displayHome(homeLocation);
                Snackbar.make(findViewById(android.R.id.content), getString(R.string.set_home_info), Snackbar.LENGTH_INDEFINITE)
                        .setAction(getString(R.string.want_button), v -> {
                            if (homeLocation != null) {
                                SharedPreferences preferences = getSharedPreferences(MapsParameters.SHARED_HOME_PREFERENCE, MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                String homeLat = String.valueOf(homeLocation.getLatitude());
                                String homeLng = String.valueOf(homeLocation.getLongitude());

                                editor.putString(HOME_LAT, homeLat);
                                editor.putString(HOME_LNG, homeLng);

                                editor.apply();
                                startActivity(IntentFactory.createLobbyReturn(getApplicationContext(), R.id.navigation_utils));
                            }
                        })
                        .show();
            }
        });
    }


    /**
     * Set and show the new home position.
     * If called twice, the home will be overwritten
     */
    private void setHome() {
        googleLocationFinder.setLocationSetListener(location -> {
            homeLocation = location;
            homeSet();
        });
        googleLocationFinder.findCurrentLocation(this);
    }

    /**
     * Display the user location as a marker on the map
     * It represent the home set
     *
     * @param homeLocation The Location of the user
     */
    private void displayHome(Location homeLocation) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(homeLocation.getLatitude(), homeLocation.getLongitude()), MapsParameters.SINGLE_PLACE_ZOOM));
        mMap.addMarker(MarkerFactory.createHomeMarker(getResources().getString(R.string.home_sweet), homeLocation.getLatitude(), homeLocation.getLongitude()));
    }


    /**
     * Trigger the listener when everything has been all right
     */
    private void homeSet() {
        if (homeSetListener != null) {
            homeSetListener.onHomeSet();
        }
    }

}

