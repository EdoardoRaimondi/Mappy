package com.example.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.example.app.dialogs.BasicDialog;
import com.example.app.factories.IntentFactory;
import com.example.app.finals.HomeMode;
import com.example.app.finals.MapsParameters;
import com.example.app.finals.MapsUtility;
import com.example.app.listeners.OnHomeSetListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class HomeActivity extends FragmentActivity implements
        OnMapReadyCallback, BasicDialog.BasicDialogListener {

    private FusedLocationProviderClient fusedLocationProviderClient;
    private GoogleMap mMap;
    private MapView mapView;

    private static HomeMode mode;

    public Location homeLocation;
    private LocationCallback locationCallback;

    public static final String SET_KEY = "set_key";

    private static final String HOME_LAT = "home_lat";
    private static final String HOME_LNG = "home_long";
    private static final String HOME = "Home sweet home";

    private double homeLat = 0.0;
    private double homeLng = 0.0;

    private static final String TAG = "HomeActivity";

    private static final String HM_NULL = "hm_null";
    private static final String HM_SET = "hm_set";
    private static final String HM_NOT_SET = "hm_not_set";

    private OnHomeSetListener onHomeSetListener;

    private void setOnHomeSetListener(OnHomeSetListener listener) {
        this.onHomeSetListener = listener;
    }

    /**
     * Callback when the activity is created
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        homeLat = Double.parseDouble(preferences.getString(HOME_LAT, "0.0"));
        homeLng = Double.parseDouble(preferences.getString(HOME_LNG, "0.0"));
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(HomeActivity.this);
    }

    /**
     * Callback when the map is ready
     *
     * @param googleMap the map
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

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Intent intentInfo = getIntent();
                mode = (HomeMode) intentInfo.getSerializableExtra(SET_KEY);
                switch (mode) {
                    case setMode: //User want to set home
                        setHome();
                        break;
                    case viewMode: //User want to view his last home
                        viewHome();
                        break;
                }
            }
        });
        setOnHomeSetListener(new OnHomeSetListener() {
            @Override
            public void onHomeSet() {
                BasicDialog.BasicDialogBuilder homeSetBuilder = new BasicDialog.BasicDialogBuilder(HM_SET);
                homeSetBuilder.setTitle("WONDERFUL");
                homeSetBuilder.setText("You're new home looks gorgeous");
                homeSetBuilder.setTextForOkButton("GREAT");
                homeSetBuilder.build().show(getSupportFragmentManager(), TAG);
            }

            @Override
            public void onHomeSetFailed() {
                BasicDialog.BasicDialogBuilder homeSetFailedBuilder = new BasicDialog.BasicDialogBuilder(HM_NOT_SET);
                homeSetFailedBuilder.setTitle(getString(R.string.ohno));
                homeSetFailedBuilder.setText(getString(R.string.unknown_err));
                homeSetFailedBuilder.setTextForOkButton("RETRY");
                homeSetFailedBuilder.setTextForCancelButton(getString(R.string.cancel_button));
                homeSetFailedBuilder.build().show(getSupportFragmentManager(), TAG);
            }
        });
    }


    /**
     * Set and show the new home position.
     * If called twice, the home will be override
     */
    private void setHome() {
        fusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            homeLocation = task.getResult();
                            if (homeLocation != null) {
                                displayHome(homeLocation);
                                //I advice the listener the home has been set correctly
                                homeSet();
                            } else {
                                final LocationRequest locationRequest = MapsUtility.createLocationRequest();
                                locationCallback = new LocationCallback() {
                                    @Override
                                    public void onLocationResult(LocationResult locationResult) {
                                        super.onLocationResult(locationResult);
                                        if (locationResult == null) {
                                            return;
                                        }
                                        homeLocation = locationResult.getLastLocation();
                                        displayHome(homeLocation);
                                        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                                    }
                                };
                                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
                            }
                        } else {
                            homeSetFailed();
                        }
                    }
                });
    }

    /**
     * Method to see the last home set on the map.
     * If user call this, before a home to be set,
     * nothing is showed
     */
    private void viewHome() {
        if (homeLocation == null) {
            BasicDialog.BasicDialogBuilder homeSetBuilder = new BasicDialog.BasicDialogBuilder(HM_NULL);
            homeSetBuilder.setTitle("ARE YOU A HOMELESS?");
            homeSetBuilder.setText("You need to set a home before, we don't make miracle");
            homeSetBuilder.setTextForOkButton("SET HOME");
            homeSetBuilder.build().show(getSupportFragmentManager(), TAG);
        }
        else {
            MarkerOptions home = new MarkerOptions();
            home.title(HOME);
            home.position(new LatLng(homeLat, homeLng));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(homeLat, homeLng), MapsParameters.DEFAULT_ZOOM));
            mMap.addMarker(home);
        }
    }

    /**
     * Callback when the method is onPause.
     * Save the current home coordinates for future access
     */
    @Override
    protected void onPause() {
        super.onPause();

        if (homeLocation != null) {
            SharedPreferences preferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            String homeLat = String.valueOf(homeLocation.getLatitude());
            String homeLng = String.valueOf(homeLocation.getLongitude());

            editor.putString(HOME_LAT, homeLat);
            editor.putString(HOME_LNG, homeLng);

            editor.apply();
        }
    }

    /**
     * Display the user location as a marker on the map
     * It represent the home set
     *
     * @param homeLocation the location of the user
     */
    private void displayHome(Location homeLocation) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(homeLocation.getLatitude(), homeLocation.getLongitude()), MapsParameters.DEFAULT_ZOOM));
        mMap.addMarker(new MarkerOptions().position(new LatLng(homeLocation.getLatitude(), homeLocation.getLongitude())));
    }

    /**
     * Trigger the listener when everything has been all right
     */
    private void homeSet() {
        if (onHomeSetListener != null)
            onHomeSetListener.onHomeSet();
    }

    /**
     * Trigger the listener when something goes wrong
     */
    private void homeSetFailed() {
        if (onHomeSetListener != null)
            onHomeSetListener.onHomeSetFailed();
    }

    // DIALOGS LISTENERS

    /**
     * BasicDialog common listener
     *
     * @param id     the identifyer of dialog that was dismissed
     * @param option the option choosen by user
     */
    public void onDialogResult(String id, boolean option) {
        switch (id) {
            case HM_SET:
                startActivity(IntentFactory.createLobbyReturn(this));
                break;
            case HM_NOT_SET:
                if (!option) startActivity(IntentFactory.createLobbyReturn(this));
                else {
                    //User click "Retry"
                    setHome();
                }
                break;
            case HM_NULL:
                setHome();
                break;
        }
    }
}

