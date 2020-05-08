package com.example.app;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.app.dialogs.RadiusDialog;
import com.example.app.factories.DialogFactory;
import com.example.app.finals.NearbyRequestType;
import com.example.app.finals.ResponseStatus;
import com.example.app.listeners.OnLocationSetListener;
import com.example.app.listeners.OnMarkersDownloadedListener;
import com.example.app.listeners.OnResultSetListener;
import com.example.app.ui_tools.ProgressAnimation;
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
        OnMapReadyCallback{

    private static final int DEFAULT_ZOOM          = 12;
    private static final String NEARBY_URL_REQUEST = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    private static final String GOOGLE_KEY         = "AIzaSyCIN8HCmGWXf5lzta5Rv2nu8VdIUV4Jp7s";

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

    private NearbyRequestType requestType;
    private int radius; //max radius can be 50000m

    // instance restoring control variables
    private boolean canRestore = false;
    private List<MarkerOptions> restoreMarkers = new ArrayList<>();

    private OnLocationSetListener onLocationSetListener;

    /**
     * Method to set the location listener.
     * @param listener to set
     */
    private void setOnLocationSetListener(OnLocationSetListener listener){
        onLocationSetListener = listener;
    }

    /**
     * Callback when the activity is created
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        restoreMarkers = new ArrayList<>();

        // restoring instance state if any
        if(savedInstanceState != null){
            String[] titles = savedInstanceState.getStringArray(TITLES_KEY);
            Log.d("MapsActivity","restoring " + titles.length + " markers");
            double[] latitudes = savedInstanceState.getDoubleArray(LATITUDES_KEY);
            double[] longitudes = savedInstanceState.getDoubleArray(LONGITUDES_KEY);
            // create a marker list, in order to be display then
            if (titles != null && latitudes != null && longitudes != null) {
                for (int i = 0; i < titles.length; i++) {
                    MarkerOptions newMarker = new MarkerOptions();
                    newMarker.title(titles[i]);
                    double lat = latitudes[i];
                    double lng = longitudes[i];
                    LatLng latLng = new LatLng(lat, lng);
                    newMarker.position(latLng);
                    newMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                    // now the marker is created I add it on the marker list
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

        ProgressBar progressBar = findViewById(R.id.prog_bar);
        ProgressAnimation anim = new ProgressAnimation(progressBar, 0, 20);
        anim.setDuration(1000);
        progressBar.startAnimation(anim);
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        final LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                setDeviceLocation();
                //Get button pressed information
                Intent requestInfo = getIntent();
                requestType = (NearbyRequestType) requestInfo.getSerializableExtra(NEARBY_KEY);
                radius = requestInfo.getIntExtra(RADIUS, 1000);

                if(canRestore){
                    mMap.clear();
                    displayMarkers(restoreMarkers);
                    restoreMarkers.clear();
                }
                else {
                    //act in order to satisfy the request purpose
                    assert requestType != null;
                    switch (requestType) {
                        case night_club:
                            showNearbyDisco(radius);
                            break;
                        case restaurant:
                            showNearbyRestaurant(radius);
                            break;
                        case taxi_stand:
                            showNearbyTaxi(radius);
                            break;
                        case hospital:
                            showNearbyHospital(radius);
                            break;
                        case police:
                            showNearbyPolice(radius);
                            break;
                        case museum:
                            showNearbyMuseum(radius);
                            break;
                        case art_gallery:
                            showNearbyArtGallery(radius);
                            break;
                        case tourist_attraction:
                            showNearbyAttraction(radius);
                            break;
                        case zoo:
                            showNearbyZoo(radius);
                            break;
                        case movie_theater:
                            showNearbyCinema(radius);
                            break;
                        case park:
                            showNearbyPark(radius);
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
                                //trigger the listeners
                                loadLocation(myLastLocation);
                                ProgressBar progressBar = findViewById(R.id.prog_bar);
                                ProgressAnimation anim = new ProgressAnimation(progressBar, 20, 100);
                                anim.setDuration(1000);
                                progressBar.startAnimation(anim);
                                progressBar.setVisibility(View.GONE);
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLastLocation.getLatitude(), myLastLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                            else {
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
                                        loadLocation(myLastLocation);
                                        ProgressBar progressBar = findViewById(R.id.prog_bar);
                                        ProgressAnimation anim = new ProgressAnimation(progressBar, 20, 100);
                                        anim.setDuration(1000);
                                        progressBar.startAnimation(anim);
                                        progressBar.setVisibility(View.GONE);
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
    private String getUrl(double latitude, double longitude, String nearbyPlace, int radius)
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
    private void showNearbyDisco(final int radius) {
        setOnLocationSetListener(new OnLocationSetListener() {
            /**
             * Callback when the position is ready
             * @param location the centre of the research
             */
            @Override
            public void onLocationSet(Location location) {
                //create the request
                String urlDisco = getUrl(myLastLocation.getLatitude(), myLastLocation.getLongitude(), NearbyRequestType.night_club.toString(), radius);
                Object[] transferData = new Object[2];
                transferData[0] = mMap;
                transferData[1] = urlDisco;
                //the request will be downloaded and displayed
                GetNearbyPlaces getNearbyDiscoPlaces = new GetNearbyPlaces();
                getNearbyDiscoPlaces.execute(transferData);
                getNearbyDiscoPlaces.setOnResultSetListener(new OnResultSetListener() {
                    /**
                     * Callback when the response result is ready
                     * @param result to get
                     */
                    @Override
                    public void onResultSet(String result) {
                        showResponseInfo(result);
                    }
                });
            }
        });
    }


    /**
     * Method activated by the nearby button pressure.
     * Send to {@link GetNearbyPlaces} the command to show the nearby restaurant
     * @param radius the radius research
     */
    private void showNearbyRestaurant(final int radius) {
        setOnLocationSetListener(new OnLocationSetListener() {
            /**
             * Callback when the position is ready
             * @param location the centre of the research
             */
            @Override
            public void onLocationSet(Location location) {
                String urlRestaurant = getUrl(location.getLatitude(), location.getLongitude(), NearbyRequestType.restaurant.toString(), radius);
                Object[] transferData = new Object[2];
                transferData[0] = mMap;
                transferData[1] = urlRestaurant;
                //request will be downloaded and displayed
                GetNearbyPlaces getNearbyRestaurantPlaces = new GetNearbyPlaces();
                getNearbyRestaurantPlaces.execute(transferData);
                getNearbyRestaurantPlaces.setOnResultSetListener(new OnResultSetListener() {
                    /**
                     * Callback when the response result is ready
                     * @param result to get
                     */
                    @Override
                    public void onResultSet(String result) {
                        showResponseInfo(result);
                    }
                });
            }
        });
    }

    /**
     * Method activated by the relative nearby button pressure
     * @param radius of research
     */
    private void showNearbyTaxi(final int radius){
        setOnLocationSetListener(new OnLocationSetListener() {
            /**
             * Callback when the position is ready
             * @param location the centre of the research
             */
            @Override
            public void onLocationSet(Location location) {
                //create the request
                String urlTaxi = getUrl(myLastLocation.getLatitude(), myLastLocation.getLongitude(), NearbyRequestType.taxi_stand.toString(), radius);
                Object[] transferData = new Object[2];
                transferData[0] = mMap;
                transferData[1] = urlTaxi;
                //request will be downloaded and displayed
                GetNearbyPlaces getNearbyTaxiPlaces = new GetNearbyPlaces();
                getNearbyTaxiPlaces.execute(transferData);
                getNearbyTaxiPlaces.setOnResultSetListener(new OnResultSetListener() {
                    /**
                     * Callback when the response result is ready
                     * @param result to get
                     */
                    @Override
                    public void onResultSet(String result) {
                        showResponseInfo(result);
                    }
                });
            }
        });
    }


    /**
     * Method activated by the relative nearby button pressure
     * @param radius of research
     */
    private void showNearbyHospital(final int radius) {
        setOnLocationSetListener(new OnLocationSetListener() {
            /**
             * Callback when the position is ready
             * @param location the centre of the research
             */
            @Override
            public void onLocationSet(Location location) {
                //create the request
                String urlHospital = getUrl(myLastLocation.getLatitude(), myLastLocation.getLongitude(), NearbyRequestType.hospital.toString(), radius);
                Object[] transferData = new Object[2];
                transferData[0] = mMap;
                transferData[1] = urlHospital;
                //request will be downloaded and displayed
                GetNearbyPlaces getNearbyHospitals = new GetNearbyPlaces();
                getNearbyHospitals.execute(transferData);
                getNearbyHospitals.setOnResultSetListener(new OnResultSetListener() {
                    /**
                     * Callback when the response result is ready
                     * @param result to get
                     */
                    @Override
                    public void onResultSet(String result) {
                        showResponseInfo(result);
                    }
                });
            }
        });
    }


    /**
     * Method activated by the relative nearby button pressure
     * @param radius of research
     */
    private void showNearbyPolice(final int radius){
        setOnLocationSetListener(new OnLocationSetListener() {
            /**
             * Callback when the position is ready
             * @param location the centre of the research
             */
            @Override
            public void onLocationSet(Location location) {
                //create the request
                String urlPolice = getUrl(myLastLocation.getLatitude(), myLastLocation.getLongitude(), NearbyRequestType.police.toString(), radius);
                Object[] transferData = new Object[2];
                transferData[0] = mMap;
                transferData[1] = urlPolice;
                //request will be downloaded and displayed
                GetNearbyPlaces getNearbyPoliceStations = new GetNearbyPlaces();
                getNearbyPoliceStations.execute(transferData);
                getNearbyPoliceStations.setOnResultSetListener(new OnResultSetListener() {
                    /**
                     * Callback when the response result is ready
                     * @param result to get
                     */
                    @Override
                    public void onResultSet(String result) {
                        showResponseInfo(result);
                    }
                });
            }
        });
    }

    /**
     * Method activated by the relative nearby button pressure
     * @param radius of research
     */
    private void showNearbyZoo(final int radius){
        setOnLocationSetListener(new OnLocationSetListener() {
            /**
             * Callback when the position is ready
             * @param location the centre of the research
             */
            @Override
            public void onLocationSet(Location location) {
                //create the request
                String urlZoo = getUrl(myLastLocation.getLatitude(), myLastLocation.getLongitude(), NearbyRequestType.zoo.toString(), radius);

                Object[] transferData = new Object[2];
                transferData[0] = mMap;
                transferData[1] = urlZoo;
                //request will be downloaded and displayed
                GetNearbyPlaces getNearbyZoo = new GetNearbyPlaces();
                getNearbyZoo.execute(transferData);
                getNearbyZoo.setOnResultSetListener(new OnResultSetListener() {
                    /**
                     * Callback when the response result is ready
                     * @param result to get
                     */
                    @Override
                    public void onResultSet(String result) {
                        showResponseInfo(result);
                    }
                });
            }
        });
    }

    /**
     * Method activated by the relative nearby button pressure
     * @param radius of research
     */
    private void showNearbyPark(final int radius){
        setOnLocationSetListener(new OnLocationSetListener() {
            /**
             * Callback when the position is ready
             * @param location the centre of the research
             */
            @Override
            public void onLocationSet(Location location) {
                //create the request
                String urlPark = getUrl(myLastLocation.getLatitude(), myLastLocation.getLongitude(), NearbyRequestType.park.toString(), radius);

                Object[] transferData = new Object[2];
                transferData[0] = mMap;
                transferData[1] = urlPark;
                //request will be downloaded and displayed
                GetNearbyPlaces getNearbyParks = new GetNearbyPlaces();
                getNearbyParks.execute(transferData);
                getNearbyParks.setOnResultSetListener(new OnResultSetListener() {
                    /**
                     * Callback when the response result is ready
                     * @param result to get
                     */
                    @Override
                    public void onResultSet(String result) {
                        showResponseInfo(result);
                    }
                });
            }
        });
    }

    /**
     * Method activated by the relative nearby button pressure
     * @param radius of research
     */
    private void showNearbyMuseum(final int radius){
        setOnLocationSetListener(new OnLocationSetListener() {
            /**
             * Callback when the position is ready
             * @param location the centre of the research
             */
            @Override
            public void onLocationSet(Location location) {
                //create the request
                String urlMuseum = getUrl(myLastLocation.getLatitude(), myLastLocation.getLongitude(), NearbyRequestType.museum.toString(), radius);
                Object[] transferData = new Object[2];
                transferData[0] = mMap;
                transferData[1] = urlMuseum;
                //request will be downloaded and displayed
                GetNearbyPlaces getNearbyMuseum = new GetNearbyPlaces();
                getNearbyMuseum.execute(transferData);
                getNearbyMuseum.setOnResultSetListener(new OnResultSetListener() {
                    /**
                     * Callback when the response result is ready
                     * @param result to get
                     */
                    @Override
                    public void onResultSet(String result) {
                        showResponseInfo(result);
                    }
                });
            }
        });
    }

    /**
     * Method activated by the relative nearby button pressure
     * @param radius of research
     */
    private void showNearbyArtGallery(final int radius){
        setOnLocationSetListener(new OnLocationSetListener() {
            /**
             * Callback when the position is ready
             * @param location the centre of the research
             */
            @Override
            public void onLocationSet(Location location) {
                //create the request
                String urlGallery = getUrl(myLastLocation.getLatitude(), myLastLocation.getLongitude(), NearbyRequestType.art_gallery.toString(), radius);
                Object[] transferData = new Object[2];
                transferData[0] = mMap;
                transferData[1] = urlGallery;
                //request will be downloaded and displayed
                GetNearbyPlaces getNearbyArtGallery = new GetNearbyPlaces();
                getNearbyArtGallery.execute(transferData);
                getNearbyArtGallery.setOnResultSetListener(new OnResultSetListener() {
                    /**
                     * Callback when the response result is ready
                     * @param result to get
                     */
                    @Override
                    public void onResultSet(String result) {
                        showResponseInfo(result);
                    }
                });
            }
        });
    }

    /**
     * Method activated by the relative nearby button pressure
     * @param radius of research
     */
    private void showNearbyAttraction(final int radius){
        setOnLocationSetListener(new OnLocationSetListener() {
            /**
             * Callback when the position is ready
             * @param location the centre of the research
             */
            @Override
            public void onLocationSet(Location location) {
                //create the request
                String urlAttraction = getUrl(myLastLocation.getLatitude(), myLastLocation.getLongitude(), NearbyRequestType.tourist_attraction.toString(), radius);
                Object[] transferData = new Object[2];
                transferData[0] = mMap;
                transferData[1] = urlAttraction;
                //request will be downloaded and displayed
                GetNearbyPlaces getNearbyAttractions = new GetNearbyPlaces();
                getNearbyAttractions.execute(transferData);
                getNearbyAttractions.setOnResultSetListener(new OnResultSetListener() {
                    /**
                     * Callback when the response result is ready
                     * @param result to get
                     */
                    @Override
                    public void onResultSet(String result) {
                        showResponseInfo(result);
                    }
                });
            }
        });
    }

    /**
     * Method activated by the relative nearby button pressure
     * @param radius of research
     */
    private void showNearbyCinema(final int radius){
        setOnLocationSetListener(new OnLocationSetListener() {
            /**
             * Callback when the position is ready
             * @param location the centre of the research
             */
            @Override
            public void onLocationSet(Location location) {
                //create the request
                String urlCinema = getUrl(myLastLocation.getLatitude(), myLastLocation.getLongitude(), NearbyRequestType.movie_theater.toString(), radius);
                Object[] transferData = new Object[2];
                transferData[0] = mMap;
                transferData[1] = urlCinema;
                //request will be downloaded and displayed
                GetNearbyPlaces getNearbyAttractions = new GetNearbyPlaces();
                getNearbyAttractions.execute(transferData);
                getNearbyAttractions.setOnResultSetListener(new OnResultSetListener() {
                    /**
                     * Callback when the response result is ready
                     * @param result to get
                     */
                    @Override
                    public void onResultSet(String result) {
                        showResponseInfo(result);
                    }
                });
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
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // calling super class method
        super.onSaveInstanceState(savedInstanceState);
        // getting the list of found nearby places
        List<MarkerOptions> markerList = GetNearbyPlaces.markerList;
        //creating empty arrays to save the state
        ArrayList<String> titles = new ArrayList<>();
        Log.d("","saving " + markerList.size() + " places");
        // only the array list titles will tell how many places by its size
        if(markerList.size() > 0) {
            String[] title = new String[markerList.size()];
            double[] lat = new double[markerList.size()];
            double[] lng = new double[markerList.size()];
            for (int currentMarker = 0; currentMarker < markerList.size(); currentMarker++) {
                // filling the arrays
                MarkerOptions marker = markerList.get(currentMarker);
                title[currentMarker] = marker.getTitle();
                lat[currentMarker] = marker.getPosition().latitude;
                lng[currentMarker] = marker.getPosition().longitude;
            }
            // now arrays are filled with the information I need
            savedInstanceState.putStringArray(TITLES_KEY, title);
            savedInstanceState.putDoubleArray(LATITUDES_KEY, lat);
            savedInstanceState.putDoubleArray(LONGITUDES_KEY, lng);
            // saving current position
            if (myLastLocation != null) {
                savedInstanceState.putDouble(LAT_KEY, myLastLocation.getLatitude());
                savedInstanceState.putDouble(LNG_KEY, myLastLocation.getLongitude());
            }
        }
    }

    /**
     * Callback when app has been resume from onPause. It recreate the precedent state
     * @param markerList the list of the marker representing the precedent state
     */
    private void displayMarkers(List<MarkerOptions> markerList){
        for(int currentMarker = 0; currentMarker < markerList.size(); currentMarker++) {
            mMap.addMarker(markerList.get(currentMarker));
        }    }

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
                DialogFactory.showNotFoundAlertDialog(this);
                break;
            case ResponseStatus.OVER_QUERY_LIMIT:
                DialogFactory.showOverQueryAlertDialog(this);
                break;
            case ResponseStatus.NO_CONNECTION:
                DialogFactory.showNoConnectionAlertDialog(this);
                break;
                // FOLLOWING STATES SHOULD BE MANAGED BY PROGRAMMERS, THEY ARE NOT USER FAULT
            case ResponseStatus.INVALID_REQUEST:
            case ResponseStatus.CONNECTION_LOW:
                DialogFactory.showNoConnectionAlertDialog(this);
                break;
            case ResponseStatus.UNKNOWN_ERROR:
                DialogFactory.showUnknownErrorAlertDialog(this);
                break;
            case ResponseStatus.REQUEST_DENIED:
                DialogFactory.showRequestDeniedAlertDialog(this);
                break;
        }
    }

    /**
     * Open the dialog in ZERO RESULT status case
     */
    private void openRadiusDialog(){
        RadiusDialog dialog = new RadiusDialog(radius, requestType);
        try {
            dialog.show(getSupportFragmentManager(), "example dialog");
        }
        catch(IllegalStateException e){
            //just ignore it
        }
    }

    /**
     * Method that triggered the {@link OnLocationSetListener}
     * @param location my last location
     */
    private void loadLocation(Location location){
        if(onLocationSetListener != null){
            onLocationSetListener.onLocationSet(location);
        }
    }
}
