package com.example.app;


import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;



public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback{

    private static final int DEFAULT_ZOOM = 12;
    private static final String GOOGLE_KEY = "AIzaSyC3Xv1AKbmZ3wJ6VXz56BKIwxBguangcQA";
    private static final String NEARBY_URL_REQUEST = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    //Activity connectors
    public static final String NEARBY_KEY = "nearby key";
    public static final String RADIUS = "radius";

    public static Context mContext;

    private GoogleMap mMap;
    private LocationCallback locationCallback;

    private MapView mapView;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location myLastLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //I set the current context so I can show eventual error toasts
        setContext();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapsActivity.this);
    }


    /**
     * Callback when the map fragemnt ui is ready
     * @param googleMap the map ready
     */
    @SuppressLint("MissingPermission")
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

        //check if gps is enabled or not and then request user to enable it
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getDeviceLocation();
                //Get information of what button has been pressed
                Intent requestInfo = getIntent();
                NearbyRequestType requestType = (NearbyRequestType) requestInfo.getSerializableExtra(NEARBY_KEY);
                long radius = requestInfo.getLongExtra(RADIUS, 1000);

                //act in order to satisfy the request purpose
                assert requestType != null;
                switch(requestType){
                    case DISCO:
                        showNearbyDisco(radius);
                        break;
                    case RESTAURANT:
                        showNearbyRestaurant(radius);
                        break;
                    case TAXI:
                        showNearbyTaxi(radius);
                        break;
                    case HOSPITAL:
                        showNearbyHospital(radius);
                        break;
                    case POLICE:
                        showNearbyPolice(radius);
                        break;
                }
            }
        });
    }


    /**
     * Method to get user location pointer on the map
     */
    @SuppressLint("MissingPermission")
    private void getDeviceLocation() {
        fusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            myLastLocation = task.getResult();
                            if (myLastLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLastLocation.getLatitude(), myLastLocation.getLongitude()), DEFAULT_ZOOM));
                            } else {
                                final LocationRequest locationRequest = LocationRequest.create();
                                locationRequest.setInterval(10000);
                                locationRequest.setFastestInterval(5000);
                                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                locationCallback = new LocationCallback() {
                                    @Override
                                    public void onLocationResult(LocationResult locationResult) {
                                        super.onLocationResult(locationResult);
                                        if (locationResult == null) {
                                            return;
                                        }
                                        myLastLocation = locationResult.getLastLocation();
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLastLocation.getLatitude(), myLastLocation.getLongitude()), DEFAULT_ZOOM));
                                        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                                    }
                                };
                                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);

                            }
                        }
                    }
                });
    }



    /**
     * method to get the url string containing all the place information
     * @param latitude     of the searching centre position
     * @param longitude    of the searching centre position
     * @param nearbyPlace  to search for
     * @param radius       of the research
     * @return URL string
     */
    private String getUrl(double latitude, double longitude, String nearbyPlace, double radius)
    {
        StringBuilder googleURL = new StringBuilder(NEARBY_URL_REQUEST);
        googleURL.append("location=" + latitude + "," + longitude);
        googleURL.append("&radius=" + radius);
        googleURL.append("&type=" + nearbyPlace);
        googleURL.append("&sensor=true");
        googleURL.append("&key=" + GOOGLE_KEY);

        Log.d("GoogleMapsActivity", "url = " + googleURL.toString());

        return googleURL.toString();
    }

    /**
     * Method activated by the relative nearby button pressure
     * @param radius of research
     */
    private void showNearbyDisco(final long radius){
        fusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            myLastLocation = task.getResult();
                            if (myLastLocation != null) {
                                Object transferData[] = new Object[2];

                                //create the request
                                String urlDisco = getUrl(myLastLocation.getLatitude(), myLastLocation.getLongitude(), "night_club", radius);
                                transferData[0] = mMap;
                                transferData[1] = urlDisco;

                                //the request will be downloaded and displayed
                                GetNearbyPlaces getNearbyDiscoPlaces = new GetNearbyPlaces();
                                getNearbyDiscoPlaces.execute(transferData);

                            }
                        }
                    }
                });
    }

    /**
     * Method activated by the relative nearby button pressure
     * @param radius of research
     */
    private void showNearbyRestaurant(final long radius){
        fusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            myLastLocation = task.getResult();
                            if (myLastLocation != null) {
                                Object transferData[] = new Object[2];

                                //create the request
                                 String urlRisto = getUrl(myLastLocation.getLatitude(), myLastLocation.getLongitude(), "restaurant", radius);
                                 transferData[0] = mMap;
                                 transferData[1] = urlRisto;

                                 //request will be downloaded and displayed
                                 GetNearbyPlaces getNearbyRestaurantPlaces = new GetNearbyPlaces();
                                 getNearbyRestaurantPlaces.execute(transferData);


                            }
                        }
                    }
                });
    }

    /**
     * Method activated by the relative nearby button pressure
     * @param radius of research
     */
    private void showNearbyTaxi(final long radius){
        fusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            myLastLocation = task.getResult();
                            if (myLastLocation != null) {
                                Object transferData[] = new Object[2];

                                //create the request
                                String urlTaxi = getUrl(myLastLocation.getLatitude(), myLastLocation.getLongitude(), "restaurant", radius);
                                transferData[0] = mMap;
                                transferData[1] = urlTaxi;

                                //request will be downloaded and displayed
                                GetNearbyPlaces getNearbyTaxiPlaces = new GetNearbyPlaces();
                                getNearbyTaxiPlaces.execute(transferData);


                            }
                        }
                    }
                });
    }

    /**
     * Method activated by the relative nearby button pressure
     * @param radius of research
     */
    private void showNearbyHospital(final long radius){
        fusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            myLastLocation = task.getResult();
                            if (myLastLocation != null) {
                                Object transferData[] = new Object[2];

                                //create the request
                                String urlHospital = getUrl(myLastLocation.getLatitude(), myLastLocation.getLongitude(), "restaurant", radius);
                                transferData[0] = mMap;
                                transferData[1] = urlHospital;

                                //request will be downloaded and displayed
                                GetNearbyPlaces getNearbyHospitals = new GetNearbyPlaces();
                                getNearbyHospitals.execute(transferData);


                            }
                        }
                    }
                });
    }

    /**
     * Method activated by the relative nearby button pressure
     * @param radius of research
     */
    private void showNearbyPolice(final long radius){
        fusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            myLastLocation = task.getResult();
                            if (myLastLocation != null) {
                                Object transferData[] = new Object[2];

                                //create the request
                                String urlPolice = getUrl(myLastLocation.getLatitude(), myLastLocation.getLongitude(), "restaurant", radius);
                                transferData[0] = mMap;
                                transferData[1] = urlPolice;

                                //request will be downloaded and displayed
                                GetNearbyPlaces getNearbyPoliceStations = new GetNearbyPlaces();
                                getNearbyPoliceStations.execute(transferData);


                            }
                        }
                    }
                });
    }

    /**
     * Callback for the activity result. If check is passed, let the method execute
     * @param requestCode activity request code
     * @param resultCode  activity result code
     * @param data        intent representing the data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 51) {
            if (resultCode == RESULT_OK) {
                getDeviceLocation();
            }
        }
    }

    /**
     * Method to set the context
     */
    private void setContext(){
        mContext = getApplicationContext();
    }

    /**
     * @return the activity context
     */
    public static Context getContext(){
        return mContext;
    }
}
