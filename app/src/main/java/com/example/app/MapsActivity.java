package com.example.app;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;


public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback, RadiusDialog.RadiusDialogListener {

    private static final int MAX_PLACES = 30;
    private static final int DEFAULT_ZOOM = 12;
    private static final String GOOGLE_KEY = "AIzaSyBhUH-chcm8iT5iSYmqzmuEbnZVUt93Mmo";
    private static final String NEARBY_URL_REQUEST = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";

    // activity connectors
    public static final String NEARBY_KEY = "nearby key";
    public static final String RADIUS = "radius";

    // instance state keys
    private static final String TITLES_KEY = "titles_k";
    private static final String LATITUDES_KEY = "lat_k";
    private static final String LONGITUDES_KEY = "lng_k";
    private static final String LAT_KEY = "lat_last_k";
    private static final String LNG_KEY = "lng_last_k";

    public static Context mContext;

    private GoogleMap mMap;
    private LocationCallback locationCallback;

    private MapView mapView;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location myLastLocation;

    private boolean canRestore = false;
    private List<MarkerOptions> restoreMarkers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        restoreMarkers = new ArrayList<>();
        // restoring instance state if any
        if(savedInstanceState != null){
            ArrayList<String> titles = savedInstanceState.getStringArrayList(TITLES_KEY);
            double[] latitudes = savedInstanceState.getDoubleArray(LATITUDES_KEY);
            double[] longitudes = savedInstanceState.getDoubleArray(LONGITUDES_KEY);
            // create a marker list, in order to be display then
            if (titles != null) {
                if(latitudes != null && longitudes != null) {
                    for (int i = 0; i < titles.size(); i++) {
                        MarkerOptions newMarker = new MarkerOptions();
                        newMarker.title(titles.get(i));
                        double lat = latitudes[i];
                        double lng = longitudes[i];
                        LatLng latLng = new LatLng(lat, lng);
                        newMarker.position(latLng);
                        newMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                        //now the marker is created I add it on the marker list
                        restoreMarkers.add(newMarker);
                    }
                    canRestore = true;
                }
            }
        }

        // set the current context so I can show eventual error toasts
        setContext();

        // obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapsActivity.this);
    }


    /**
     * Callback when the map fragemnt ui is ready
     * @param googleMap
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
                if(canRestore){
                    onNeedRestoreState(restoreMarkers);
                }
                else {
                    //act in order to satisfy the request purpose
                    switch (requestType) {
                        case DISCO:
                            showNearbyDisco(radius);
                            break;
                        case RESTAURANT:
                            showNearbyRestaurant(radius);
                            break;
                    }
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
                        } else {
                            return;
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
     * Method activated by the nearby button pressure
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
     * Method activated by the nearby button pressure
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
     * Callback for the activity result. If check is passed, let the method execute
     * @param requestCode
     * @param resultCode
     * @param data
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

    //INSTANCE SAVE

    /**
     * Callback to save the state when necessary
     * @param savedInstanceState Bundle where to save places information
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        // getting the list of found nearby places
        List<MarkerOptions> markerList = GetNearbyPlaces.markerList;
        //creating empty arrays to save the state
        ArrayList<String> titles = new ArrayList<String>();
        // only the array list titles will tell how many places by its size
        double[] lat = new double[MAX_PLACES];
        double[] lng = new double[MAX_PLACES];
        if(markerList != null) {
            for (int currentMarker = 0; currentMarker < markerList.size(); currentMarker++) {
                //fill the arrays
                MarkerOptions marker = markerList.get(currentMarker);
                titles.add(marker.getTitle());
                lat[currentMarker] = marker.getPosition().latitude;
                lng[currentMarker] = marker.getPosition().longitude;
            }
            // now I have all the arrays filled with the information I need
            savedInstanceState.putStringArrayList(TITLES_KEY, titles);
            savedInstanceState.putDoubleArray(LATITUDES_KEY, lat);
            savedInstanceState.putDoubleArray(LONGITUDES_KEY, lng);
            savedInstanceState.putDouble(LAT_KEY, myLastLocation.getLatitude());
            savedInstanceState.putDouble(LNG_KEY, myLastLocation.getLongitude());
        }
        // calling super class method
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Callback when app has been resume from onPause. It recreate the precedent state
     * @param markerList the list of the marker representing the precedent state
     */
    private void onNeedRestoreState(List<MarkerOptions> markerList){
        for(int currentMarker = 0; currentMarker < markerList.size(); currentMarker++) {
            mMap.addMarker(markerList.get(currentMarker));
        }
    }

    private void onResult(String result){
        //Something goes wrong. Let's figure out why
        Log.i("SHIT","Result called");
        switch (result) {

            case ResponseStatus.ZERO_RESULTS:
                openRadiusDialog();
                Log.i("SHIT","NO RESULTS");
                break;
            case ResponseStatus.NOT_FOUND:
                Toast.makeText(MapsActivity.getContext(), "WE CAN'T FIND YOU", Toast.LENGTH_LONG).show();
                Log.i("SHIT","WE CAN'T FIND YOU");
                break;
            case ResponseStatus.INVALID_REQUEST:
                Toast.makeText(MapsActivity.getContext(), "BAD REQUEST. TRY AGAIN", Toast.LENGTH_LONG).show();
                Log.i("SHIT","BAD REQUEST. TRY AGAIN");
                break;
            case ResponseStatus.UNKNOWN_ERROR:
                Toast.makeText(MapsActivity.getContext(), "TRY AGAIN", Toast.LENGTH_LONG).show();
                Log.i("SHIT","TRY AGAIN");
                break;
            case ResponseStatus.REQUEST_DENIED:
                Toast.makeText(MapsActivity.getContext(), "REQUEST DENIED", Toast.LENGTH_LONG).show();
                Log.i("SHIT","REQUEST DENIED");
                break;
            case ResponseStatus.OVER_QUERY_LIMIT:
                Toast.makeText(MapsActivity.getContext(), "WE CAN'T HANDLE ALL THIS REQUESTS. TRY LATER", Toast.LENGTH_LONG).show();
                Log.i("SHIT","WE CAN'T HANDLE ALL THIS REQUESTS. TRY LATER");
                break;
            default: Log.i("SHIT","OK");
                break;
        }
    }

    private void openRadiusDialog(){
        RadiusDialog dialog = new RadiusDialog();
        dialog.show(getSupportFragmentManager(), "example dialog");
    }

    @Override
    public void applyRadius(int radius) {

    }
}
