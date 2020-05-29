package com.example.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.app.factories.IntentFactory;
import com.example.app.factories.UrlFactory;
import com.example.app.finals.NearbyRequestType;
import com.example.app.listeners.OnLocationSetListener;
import com.example.app.listeners.OnPhoneNumberGetListener;
import com.example.app.listeners.OnResultSetListener;
import com.example.app.sensors.LocationFinder;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class HelpActivity extends AppCompatActivity {

    private static final String NEARBY_URL_DOMAIN = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";

    private OnPhoneNumberGetListener onPhoneNumberGetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HelpActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
            // here to request the missing permissions, and then overriding
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        // setting main activity as full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_help);
    }

    /**
     * @param listener to set
     */
    private void setOnPhoneNumberGetListener(OnPhoneNumberGetListener listener){
        onPhoneNumberGetListener = listener;
    }

    /**
     * Method to show the nearby hospitals
     * @param view button {@id hospital}
     */
    public void showNearbyHospital(View view) {
        Intent intent = IntentFactory.createNearbyRequestIntent(this, NearbyRequestType.hospital, 1000);
        startActivity(intent);
    }

    /**
     * Method to show the nearby police station
     * @param view button {@id police}
     */
    public void showNearbyPolice(View view) {
        Intent intent = IntentFactory.createNearbyRequestIntent(this, NearbyRequestType.police, 1000);
        startActivity(intent);
    }

    /**
     * Method to show the nearby taxi stations
     * @param view button {@id taxi}
     */
    public void showNearbyTaxi(View view) {
        Intent intent = IntentFactory.createNearbyRequestIntent(this, NearbyRequestType.taxi_stand, 1000);
        startActivity(intent);
    }

    /**
     * Method to show the nearby taxi stations
     * @param view button {@id taxi}
     */
    public void showNearbyPharmacy(View view) {
        Intent intent = IntentFactory.createNearbyRequestIntent(this, NearbyRequestType.pharmacy, 1000);
        startActivity(intent);
    }

    /**
     * Call the police of the nearest police station
     * @param view button {@id call}
     */
    public void callPolice(View view) {
        getPhoneNumber(NearbyRequestType.police);
        setOnPhoneNumberGetListener(new OnPhoneNumberGetListener() {
            @Override
            public void onSuccess(String phoneNumber) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null));
                startActivity(intent);
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
    private String getUrl(double latitude, double longitude, String nearbyPlace, int radius) {
        String[] label = {"location", "radius", "type", "sensor", "key"};
        String location = "" + latitude + "," + longitude;
        String[] value = {location, Integer.toString(radius), nearbyPlace, "true", getResources().getString(R.string.google_maps_key)};
        String url = UrlFactory.getRequest(NEARBY_URL_DOMAIN, label, value);
        // TODO: remove following line on production
        Log.d("GoogleMapsActivity", "url = " + url);
        return url;
    }

    /**
     * Trigger the {@link OnPhoneNumberGetListener} when the
     * nearest phone number is found
     * @param type of phone number you need
     */
    private void getPhoneNumber(NearbyRequestType type){
        final boolean[] phoneNumberFound = {false};
        LocationFinder locationFinder = new LocationFinder();
        locationFinder.setOnLocationSetListener(new OnLocationSetListener() {
            @Override
            public void onLocationSet(Location location) {
                String url = getUrl(location.getLatitude(), location.getLongitude(), type.toString(), 5000);
                String[] transferData = new String[1];
                transferData[0] = url;
                //the request will be downloaded and displayed
                GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();
                getNearbyPlaces.execute(transferData);
                getNearbyPlaces.setOnResultSetListener(new OnResultSetListener() {
                    @Override
                    public void onResultSet(List<Place> nearbyPlaceList) {
                        for(int i = 0; i < nearbyPlaceList.size(); i++){
                            Place currentPlace = nearbyPlaceList.get(i);
                            if(!Places.isInitialized())
                                //TODO: UNDERSTAND THAT LOCALE.US WTF IT IS?
                                //TODO: CAN I INITIALIZE IT OUTSIDE THIS SHIT?
                               Places.initialize(getApplicationContext(), "AIzaSyCIN8HCmGWXf5lzta5Rv2nu8VdIUV4Jp7s", Locale.US);
                            PlacesClient placesClient = Places.createClient(getApplicationContext());
                            String placeId = currentPlace.getId();
                            // Specify the fields to return.
                            List<Place.Field> placeFields = Collections.singletonList(Place.Field.PHONE_NUMBER);

                            // Construct a request object, passing the place ID and fields array.
                            FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

                            placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                                Place place = response.getPlace();
                                //place found
                                if(place.getPhoneNumber()!=null) {
                                    loadPhoneNumber(place.getPhoneNumber());
                                    phoneNumberFound[0] = true;
                                }
                            }).addOnFailureListener((exception) -> {
                                if (exception instanceof ApiException) {
                                    ApiException apiException = (ApiException) exception;
                                }
                            });
                            if(phoneNumberFound[0]) break;
                        }
                    }
                });
            }
        });
        locationFinder.findCurrentLocation(this);
    }

    /**
     * Trigger the listener
     * @param phoneNumber to pass to caller
     */
    private void loadPhoneNumber(String phoneNumber){
        if(onPhoneNumberGetListener != null){
            onPhoneNumberGetListener.onSuccess(phoneNumber);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(HelpActivity.this, "Permission denied to call", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


}
