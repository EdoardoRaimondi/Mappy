package com.example.app;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.app.dialogs.BasicDialog;
import com.example.app.dialogs.RadiusDialog;
import com.example.app.factories.IntentFactory;
import com.example.app.factories.MarkerFactory;
import com.example.app.factories.UrlFactory;
import com.example.app.finals.MapsParameters;
import com.example.app.finals.MapsUtility;
import com.example.app.finals.NearbyRequestType;
import com.example.app.finals.ResponseStatus;
import com.example.app.iterators.StoppablePlaceIterator;
import com.example.app.listeners.LocationSetListener;
import com.example.app.listeners.ResultSetListener;
import com.example.app.saved_place_database.SavedPlace;
import com.example.app.ui.saved.SavedViewModel;
import com.example.app.factories.ViewModelFactory;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MapsActivity
        extends FragmentActivity
        implements OnMapReadyCallback,
                   RadiusDialog.RadiusDialogListener,
                   BasicDialog.BasicDialogListener,
                   GoogleMap.OnInfoWindowLongClickListener,
                   GoogleMap.OnInfoWindowClickListener{

    private static final String TAG = "MapsActivity";

    // Activity connectors
    public static final String NEARBY_KEY = "nearby_k";
    public static final String RADIUS = "radius_k";

    // Instance state keys
    private static final String TITLES_KEY = "titles_k";
    private static final String LATITUDES_KEY = "lat_k";
    private static final String LONGITUDES_KEY = "lng_k";
    private static final String LAT_KEY = "lat_last_k";
    private static final String LNG_KEY = "lng_last_k";

    // Basic Dialogs' ids
    private static final String OQL_ID = "oql_id";

    // PRIVATE MEMBERS
    // Widgets
    private GoogleMap mMap;
    private MapView mapView;
    private ProgressBar progressBar;
    // Inrefcaces
    private LocationCallback locationCallback;
    private LocationSetListener locationSetListener;
    private FusedLocationProviderClient fusedLocationProviderClient;
    // Other
    private NearbyRequestType requestType;
    private int radius; //max radius can be 50000m
    private Location myLastLocation;

    // ViewModel to database access
    private SavedViewModel mSavedViewModel;

    // Instance restoring control variables
    private boolean canRestore = false;
    private List<MarkerOptions> restoreMarkers;


    // BEGIN OF ACTIVITY'S LIFE CYCLE CALLBACKS

    /**
     * Callback when the activity is created
     * @param savedInstanceState the Bundle of previous instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Setting Activity full screen
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.activity_maps);
        // Getting Widgets
        progressBar = findViewById(R.id.prog_bar);
        restoreMarkers = new ArrayList<>();

        // Restoring instance state if any
        if(savedInstanceState != null){
            String[] titles = savedInstanceState.getStringArray(TITLES_KEY);
            double[] latitudes = savedInstanceState.getDoubleArray(LATITUDES_KEY);
            double[] longitudes = savedInstanceState.getDoubleArray(LONGITUDES_KEY);
            // Create a marker list, in order to be display then
            if (titles != null && latitudes != null && longitudes != null) {
                for (int i = 0; i < titles.length; i++) {
                    MarkerOptions newMarker = new MarkerOptions();
                    newMarker.title(titles[i]);
                    double lat = latitudes[i];
                    double lng = longitudes[i];
                    LatLng latLng = new LatLng(lat, lng);
                    newMarker.position(latLng);
                    newMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                    // Now the marker is created I add it on the marker list
                    restoreMarkers.add(newMarker);
                }
                canRestore = true;
            }
            animateProgress(0,20, 1000);
        }

        mSavedViewModel =  ViewModelProviders.of(this, new ViewModelFactory(getApplication())).get(SavedViewModel.class);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapsActivity.this);
        animateProgress(20,30, 500);
    }

    /**
     * Callback to save the state when necessary
     * @param savedInstanceState Bundle where to save places information
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        // Calling super class method
        super.onSaveInstanceState(savedInstanceState);
        // Getting the list of found nearby places
        List<MarkerOptions> markerList = GetNearbyPlaces.markerList;
        if (markerList.size() > 0) {
            String[] title = new String[markerList.size()];
            double[] lat = new double[markerList.size()];
            double[] lng = new double[markerList.size()];
            for (int currentMarker = 0; currentMarker < markerList.size(); currentMarker++) {
                // Filling the arrays
                MarkerOptions marker = markerList.get(currentMarker);
                title[currentMarker] = marker.getTitle();
                lat[currentMarker] = marker.getPosition().latitude;
                lng[currentMarker] = marker.getPosition().longitude;
            }
            // Now arrays are filled with the information I need
            savedInstanceState.putStringArray(TITLES_KEY, title);
            savedInstanceState.putDoubleArray(LATITUDES_KEY, lat);
            savedInstanceState.putDoubleArray(LONGITUDES_KEY, lng);
            // Saving current position
            if (myLastLocation != null) {
                savedInstanceState.putDouble(LAT_KEY, myLastLocation.getLatitude());
                savedInstanceState.putDouble(LNG_KEY, myLastLocation.getLongitude());
            }
        }
    }

    // END OF ACTIVITY'S LIFE CYCLE CALLBACKS

    /**
     * Method to set the location listener.
     * @param listener The Listener to set
     */
    private void setLocationSetListener(LocationSetListener listener){
        locationSetListener = listener;
    }

    /**
     * Callback when the map fragment ui is ready.
     * If triggered it means surely a button has been pressed
     * @param googleMap The GoogleMap reference
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setOnInfoWindowLongClickListener(this);
        mMap.setOnInfoWindowClickListener(this);

        if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 40, 180);
        }
        animateProgress(30,50, 500);
        final LocationRequest locationRequest = MapsUtility.createLocationRequest();

        final LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, locationSettingsResponse -> {
            setDeviceLocation();
            // Get button pressed information
            Intent requestInfo = getIntent();
            requestType = (NearbyRequestType) requestInfo.getSerializableExtra(NEARBY_KEY);
            radius = requestInfo.getIntExtra(RADIUS, getResources().getInteger(R.integer.default_radius) * MapsUtility.KM_TO_M);
            // Can I restore previous state ?
            if(canRestore){
                mMap.clear();
                displayMarkers(restoreMarkers);
                restoreMarkers.clear();
            }
            else {
                showPlaces(radius, requestType);
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
                                // Trigger the listeners
                                loadLocation(myLastLocation);
                                mMap.moveCamera(
                                        CameraUpdateFactory.newLatLngZoom(
                                                new LatLng(myLastLocation.getLatitude(), myLastLocation.getLongitude()),
                                                MapsParameters.DEFAULT_ZOOM
                                        )
                                );
                                animateProgress(50,100,500);
                            }
                            else {
                                final LocationRequest locationRequest = MapsUtility.createLocationRequest();
                                locationCallback = new LocationCallback() {
                                    @Override
                                    public void onLocationResult(LocationResult locationResult) {
                                        super.onLocationResult(locationResult);
                                        if (locationResult == null) {
                                            return;
                                        }
                                        myLastLocation = locationResult.getLastLocation();
                                        loadLocation(myLastLocation);
                                        mMap.moveCamera(
                                                CameraUpdateFactory.newLatLngZoom(
                                                        new LatLng(myLastLocation.getLatitude(), myLastLocation.getLongitude()),
                                                        MapsParameters.DEFAULT_ZOOM)
                                        );
                                        animateProgress(50,100,500);
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
     * Method to show markers on the map
     * @param radius Integer as radius to search
     * @param type   The type of place to display
     */
     private void showPlaces(final int radius, final NearbyRequestType type) {
         setLocationSetListener(new LocationSetListener() {
             /**
              * Callback when the position is ready
              * @param location The centre of the research
              */
             @Override
             public void onLocationSet(Location location) {
                 // Create the request
                 String url = UrlFactory.getNearbyRequest(myLastLocation.getLatitude(), myLastLocation.getLongitude(), type.toString(), radius);
                 // The request will be downloaded and displayed
                 GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();
                 getNearbyPlaces.setResultSetListener(new ResultSetListener() {
                     @Override
                     public void onResultSet(StoppablePlaceIterator nearbyPlaceListIterator) {
                         displayPlaces(nearbyPlaceListIterator);
                     }
                     @Override
                     public void onResultNotSet(String error) {
                         showResponseInfo(error);
                     }
                 });
                 getNearbyPlaces.execute(getNearbyPlaces.createTransferData(url));
             }
         });
     }

    /**
     * Callback for the activity result. If check is passed, let the method execute
     * @param requestCode Activity request code
     * @param resultCode  Activity result code
     * @param data        Intent representing the data
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

    /**
     * Callback when app has been resume from onPause. It recreate the precedent state
     * @param markerList The List of marker representing the precedent state
     */
    private void displayMarkers(List<MarkerOptions> markerList){
        for(int currentMarker = 0; currentMarker < markerList.size(); currentMarker++) {
            mMap.addMarker(markerList.get(currentMarker));
        }
        animateProgress(50,100,500);
    }

    /**
     * Method to recognize the response status
     * and act accordingly (show different dialog).
     * Act normally if the status is OK
     * To see all the possible status see {@link ResponseStatus}
     * @param status String as status of the response
     */
    private void showResponseInfo(String status){
        try{
            switch (status) {
                // No result found
                case ResponseStatus.ZERO_RESULTS:
                    RadiusDialog dialog = new RadiusDialog(radius);
                    dialog.show(getSupportFragmentManager(), TAG);
                    break;
                // Exceeded daily quota of API key
                case ResponseStatus.OVER_QUERY_LIMIT:
                    BasicDialog.BasicDialogBuilder overQueryBuilder = new BasicDialog.BasicDialogBuilder(OQL_ID);
                    overQueryBuilder.setTitle(getString(R.string.sorry));
                    overQueryBuilder.setText(getString(R.string.sorry_message));
                    overQueryBuilder.setTextForOkButton(getString(R.string.ok_button));
                    overQueryBuilder.build().show(getSupportFragmentManager(), TAG);
                    break;
                // GPS is disabled or no Internet provider
                case ResponseStatus.NOT_FOUND:
                case ResponseStatus.NO_CONNECTION:
                case ResponseStatus.CONNECTION_LOW:
                    startActivity(IntentFactory.createLobbyReturn(this));
                    break;
                // Strange errors
                case ResponseStatus.INVALID_REQUEST:
                case ResponseStatus.UNKNOWN_ERROR:
                case ResponseStatus.REQUEST_DENIED:
                    BasicDialog.BasicDialogBuilder requestDeniedBuilder = new BasicDialog.BasicDialogBuilder(OQL_ID);
                    requestDeniedBuilder.setTitle(getString(R.string.oh_no));
                    requestDeniedBuilder.setText(getString(R.string.unknown_err));
                    requestDeniedBuilder.setTextForOkButton(getString(R.string.ok_button));
                    requestDeniedBuilder.build().show(getSupportFragmentManager(), TAG);
                    break;
            }
        }
        catch(IllegalStateException exc){
            // If app on background with Dialog, nothing happened
        }
    }

    /**
     * Method that triggered the {@link LocationSetListener}
     * @param location My last Location
     */
    private void loadLocation(Location location){
        if(locationSetListener != null){
            locationSetListener.onLocationSet(location);
        }
    }

    /**
     * Method to animate progress bar
     * @param from     Percentage starting point
     * @param to       Percentage end point
     * @param duration Animation duration
     */
    private void animateProgress(int from, int to, int duration){
        Animation anim = new ProgressAnimation(progressBar, from, to);
        anim.setDuration(duration);
        if(to == 100){
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}
                @Override
                public void onAnimationRepeat(Animation animation) {}
                @Override
                public void onAnimationEnd(Animation animation) {
                    animateProgress(0,0,0);
                }

            });
        }
        progressBar.setAnimation(anim);
    }

    // DIALOGS LISTENERS

    /**
     * BasicDialog common listener
     * @param id the identifier of dialog that was dismissed
     * @param positiveButton the option choosen by user
     */
    public void onDialogResult(String id, boolean positiveButton) {
        // Just go back to the main activity
        startActivity(IntentFactory.createLobbyReturn(this));
        finish();
    }

    /**
     * RadiusDialog listener
     * @param radius New radius of research
     */
    public void onRadiusDialogResult(int radius){
        startActivity(IntentFactory.createNearbyRequestIntent(this, requestType, radius));
        // We don't want user to perform multiple back stack press to go back home
        finish();
    }

    // OTHER LISTENERS

    /**
     * Callback when the user perform a click on a Marker's InfoWindow
     * @param marker The Marker selected by the user
     */
    @Override
    public void onInfoWindowClick(final Marker marker){
        // Launch google maps app
        Uri gmmIntentUri = UrlFactory.createDirectionsUrl(marker.getPosition().latitude, marker.getPosition().longitude);
        startActivity(IntentFactory.createGoogleMapsDirectionsIntent(gmmIntentUri));
    }

    /**
     * Callback when the user perform a long click on a marker info window
     * @param marker the marker selected by the user
     */
    @Override
    public void onInfoWindowLongClick(final Marker marker) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.save_place) + marker.getTitle() + " ?")
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    /**
                     * Save the place
                     * @param dialog The selected Dialog
                     * @param id     Dialog id
                     */
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Create entity to add
                        SavedPlace place = new SavedPlace(marker.getPosition().latitude, marker.getPosition().longitude);
                        Date today = Calendar.getInstance().getTime();
                        @SuppressLint("SimpleDateFormat")
                        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        place.setDateSaved(formatter.format(today));
                        place.setPlaceName(marker.getTitle());
                        // Add it to the database
                        mSavedViewModel.insert(place);
                        Toast.makeText(getApplicationContext(), getText(R.string.saved), Toast.LENGTH_SHORT)
                            .show();
                    }
                })
                .setNegativeButton(getString(R.string.no), (dialog, id) -> dialog.cancel())
                .create()
                .show();
    }

    // UTILITY METHODS

    /**
     * Display nearby places
     * @param nearbyPlaceListIterator List of places to display
     */
    private void displayPlaces(StoppablePlaceIterator nearbyPlaceListIterator){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        if(nearbyPlaceListIterator.hasNext()) {
            while (nearbyPlaceListIterator.hasNext()) {
                // Extract the data
                Place googleNearbyLocalPlace = nearbyPlaceListIterator.next();
                String placeName = googleNearbyLocalPlace.getName();
                LatLng latLng = googleNearbyLocalPlace.getLatLng();
                if(latLng != null && placeName != null) {
                    builder.include(latLng);

                    MarkerOptions markerOptions = MarkerFactory.createBasicMarker(latLng, placeName);
                    GetNearbyPlaces.markerList.add(markerOptions);
                    mMap.addMarker(markerOptions);
                }
            }
            animateCamera(builder, mMap);
        }
        showResponseInfo(DataParser.STATUS);
    }

    /**
     * Method to animate the camera
     */
    private void animateCamera(LatLngBounds.Builder builder, GoogleMap map){
        LatLngBounds bounds = builder.build();
        int padding = 0; // Offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        map.animateCamera(cu);
    }

}
