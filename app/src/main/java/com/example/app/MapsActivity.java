package com.example.app;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;


public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback, RadiusDialog.RadiusDialogListener {


    private static final int MAX_PLACES            = 30;
    private static final int DEFAULT_ZOOM          = 12;
    private static final String NEARBY_URL_REQUEST = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    private static final String GOOGLE_KEY         = "AIzaSyBhUH-chcm8iT5iSYmqzmuEbnZVUt93Mmo";

    // activity connectors
    public static final String NEARBY_KEY = "nearby key";
    public static final String RADIUS = "radius";

    // instance state keys
    private static final String TITLES_KEY = "titles_k";
    private static final String LATITUDES_KEY = "lat_k";
    private static final String LONGITUDES_KEY = "lng_k";
    private static final String LAT_KEY = "lat_last_k";
    private static final String LNG_KEY = "lng_last_k";

    private GoogleMap mMap;
    private LocationCallback locationCallback;

    private MapView mapView;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location myLastLocation;

    // instance restoring control variables
    private boolean canRestore = false;
    private List<MarkerOptions> restoreMarkers = new ArrayList<>();

    /**
     * Callback when the activity is created
     */
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
            if (titles != null && latitudes != null && longitudes != null) {
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

        // obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapsActivity.this);
    }


    /**
     * Callback when the map fragment ui is ready.
     * If triggered it means surely a button has been pressed
     * @param googleMap the map
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
                setDeviceLocation();
                //Get button pressed information
                Intent requestInfo = getIntent();
                NearbyRequestType requestType = (NearbyRequestType) requestInfo.getSerializableExtra(NEARBY_KEY);
                long radius = requestInfo.getLongExtra(RADIUS, 1000);

                if(canRestore){
                    onNeedRestoreState(restoreMarkers);
                    restoreMarkers.clear();
                }
                else {
                    //act in order to satisfy the request purpose
                    assert requestType != null;
                    switch (requestType) {
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
            }
        });
    }


    /**
     * Method to get user location pointer on the map
     */
    @SuppressLint("MissingPermission")
    private void setDeviceLocation() {
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
        googleURL.append("location=").append(latitude).append(",").append(longitude);
        googleURL.append("&radius=").append(radius);
        googleURL.append("&type=").append(nearbyPlace);
        googleURL.append("&sensor=true");
        googleURL.append("&key=").append(GOOGLE_KEY);

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
                                Object[] transferData = new Object[2];

                                //create the request
                                String urlDisco = getUrl(myLastLocation.getLatitude(), myLastLocation.getLongitude(), "night_club", radius);
                                transferData[0] = mMap;
                                transferData[1] = urlDisco;

                                //the request will be downloaded and displayed
                                GetNearbyPlaces getNearbyDiscoPlaces = new GetNearbyPlaces();
                                getNearbyDiscoPlaces.execute(transferData);
                                getNearbyDiscoPlaces.setOnResultSetListener(new OnResultSetListener() {
                                    @Override
                                    public void onResultSet(String result) {
                                        showResponseInfo(result);
                                    }
                                });
                            }
                        }
                    }
                });
    }

    /**
     * Method activated by the nearby button pressure.
     * Send to {@link GetNearbyPlaces} the command to show the nearby restaurant
     * @param radius the radius research
     */
    private void showNearbyRestaurant(final long radius){
        fusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            myLastLocation = task.getResult();
                            if (myLastLocation != null) {
                                Object[] transferData = new Object[2];

                                //create the request
                                 String urlRisto = getUrl(myLastLocation.getLatitude(), myLastLocation.getLongitude(), "restaurant", radius);
                                 transferData[0] = mMap;
                                 transferData[1] = urlRisto;

                                 //request will be downloaded and displayed
                                 GetNearbyPlaces getNearbyRestaurantPlaces = new GetNearbyPlaces();
                                 getNearbyRestaurantPlaces.execute(transferData);
                                 getNearbyRestaurantPlaces.setOnResultSetListener(new OnResultSetListener() {
                                     @Override
                                     public void onResultSet(String result) {
                                         showResponseInfo(result);
                                     }
                                 });

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
                                Object[] transferData = new Object[2];

                                //create the request
                                String urlTaxi = getUrl(myLastLocation.getLatitude(), myLastLocation.getLongitude(), "restaurant", radius);
                                transferData[0] = mMap;
                                transferData[1] = urlTaxi;

                                //request will be downloaded and displayed
                                GetNearbyPlaces getNearbyTaxiPlaces = new GetNearbyPlaces();
                                getNearbyTaxiPlaces.execute(transferData);
                                getNearbyTaxiPlaces.setOnResultSetListener(new OnResultSetListener() {
                                    @Override
                                    public void onResultSet(String result) {
                                        showResponseInfo(result);
                                    }
                                });

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
                                Object[] transferData = new Object[2];

                                //create the request
                                String urlHospital = getUrl(myLastLocation.getLatitude(), myLastLocation.getLongitude(), "restaurant", radius);
                                transferData[0] = mMap;
                                transferData[1] = urlHospital;

                                //request will be downloaded and displayed
                                GetNearbyPlaces getNearbyHospitals = new GetNearbyPlaces();
                                getNearbyHospitals.execute(transferData);
                                getNearbyHospitals.setOnResultSetListener(new OnResultSetListener() {
                                    @Override
                                    public void onResultSet(String result) {
                                        showResponseInfo(result);
                                    }
                                });

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
                                Object[] transferData = new Object[2];

                                //create the request
                                String urlPolice = getUrl(myLastLocation.getLatitude(), myLastLocation.getLongitude(), "restaurant", radius);
                                transferData[0] = mMap;
                                transferData[1] = urlPolice;

                                //request will be downloaded and displayed
                                GetNearbyPlaces getNearbyPoliceStations = new GetNearbyPlaces();
                                getNearbyPoliceStations.execute(transferData);
                                getNearbyPoliceStations.setOnResultSetListener(new OnResultSetListener() {
                                    @Override
                                    public void onResultSet(String result) {
                                        showResponseInfo(result);
                                    }
                                });

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
                setDeviceLocation();
            }
        }
    }


    //INSTANCE SAVE

    /**
     * Callback to save the state when necessary
     * @param savedInstanceState Bundle where to save places information
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        // calling super class method
        super.onSaveInstanceState(savedInstanceState);
        // getting the list of found nearby places
        List<MarkerOptions> markerList = GetNearbyPlaces.markerList;
        //creating empty arrays to save the state
        ArrayList<String> titles = new ArrayList<>();
        // only the array list titles will tell how many places by its size
        double[] lat = new double[MAX_PLACES];
        double[] lng = new double[MAX_PLACES];
        if(markerList != null) {
            for (int currentMarker = 0; currentMarker < markerList.size(); currentMarker++) {
                // filling the arrays
                MarkerOptions marker = markerList.get(currentMarker);
                titles.add(marker.getTitle());
                lat[currentMarker] = marker.getPosition().latitude;
                lng[currentMarker] = marker.getPosition().longitude;
            }
            // now arrays are filled with the information I need
            savedInstanceState.putStringArrayList(TITLES_KEY, titles);
            savedInstanceState.putDoubleArray(LATITUDES_KEY, lat);
            savedInstanceState.putDoubleArray(LONGITUDES_KEY, lng);
            // saving current position
            savedInstanceState.putDouble(LAT_KEY, myLastLocation.getLatitude());
            savedInstanceState.putDouble(LNG_KEY, myLastLocation.getLongitude());
        }
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

    /**
     * Method to recognize the response status
     * and act accordingly (show different dialog).
     * Act normally if the status is OK
     * To see all the possible status see {@link ResponseStatus}
     * @param status of the response
     */
    private void showResponseInfo(String status){
        switch (status) {
            case ResponseStatus.ZERO_RESULTS:
                openRadiusDialog();
                break;
            case ResponseStatus.NOT_FOUND:
                // intent for get position and refresh of activity
                break;
            case ResponseStatus.OVER_QUERY_LIMIT:
                new AlertDialog.Builder(this)
                        .setTitle("Sorry")
                        .setMessage("It seems there have been too many requests on our service, try later in a bit.")
                        .setPositiveButton(getString(R.string.ok_button), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // shuld open a waiting form
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .create()
                        .show();
                break;
            case ResponseStatus.NO_CONNECTION:
                new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Your device isn't connected to any internet provider. Would you like to activate it now?")
                    .setPositiveButton(getString(R.string.ok_button), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            // intent for connection
                        }
                    })
                    .setNegativeButton(getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })
                    .create()
                    .show();
                break;
                // FOLLOWING STATES SHOULD BE MANAGED BY PROGRAMMERS, THEY ARE NOT USER FAULT
            case ResponseStatus.INVALID_REQUEST:
            case ResponseStatus.UNKNOWN_ERROR:
            case ResponseStatus.REQUEST_DENIED:
                new AlertDialog.Builder(this)
                        .setTitle("What the hell")
                        .setMessage("This error has occured because someone left a bug.")
                        .setPositiveButton(getString(R.string.ok_button), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .create()
                        .show();
                break;
        }
    }

    /**
     * Open the dialog in ZERO RESULT status case
     */
    private void openRadiusDialog(){
        RadiusDialog dialog = new RadiusDialog();
        dialog.show(getSupportFragmentManager(), "example dialog");
    }


    @Override
    public void applyRadius(int radius) {
        // refresh this activity with new radius
    }
}
