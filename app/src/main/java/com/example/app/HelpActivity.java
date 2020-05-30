package com.example.app;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
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
import com.example.app.sensors.GoogleLocationFinder;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

public class HelpActivity extends AppCompatActivity {

    private OnPhoneNumberGetListener onPhoneNumberGetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HelpActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
        }
        if(!Places.isInitialized())
            Places.initialize(getApplicationContext(), "AIzaSyCIN8HCmGWXf5lzta5Rv2nu8VdIUV4Jp7s");
        // setting help activity as full screen
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
                Intent callIntent = IntentFactory.createCallIntent(phoneNumber);
                startActivity(callIntent);
            }

            @Override
            public void onFail() {
                phoneNumberMessageError();
            }
        });
    }

    /**
     * Trigger the {@link OnPhoneNumberGetListener} when the
     * nearest phone number is found
     * @param type of phone number you need (need to be a {@link NearbyRequestType})
     */
    private void getPhoneNumber(NearbyRequestType type){
        GoogleLocationFinder googleLocationFinder = new GoogleLocationFinder();
        googleLocationFinder.setOnLocationSetListener(new OnLocationSetListener() {
            @Override
            public void onLocationSet(Location location) {
                String url = UrlFactory.getNearbyRequest(location.getLatitude(), location.getLongitude(), type.toString(), 5000);
                //the request will be downloaded and displayed
                GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();
                getNearbyPlaces.execute(getNearbyPlaces.createTransferData(url));
                getNearbyPlaces.setOnResultSetListener(new OnResultSetListener() {
                    @Override
                    public void onResultSet(StoppablePlaceIterator nearbyPlaceListIterator) {
                        if(!nearbyPlaceListIterator.hasNext()) phoneNumberSearchFailed();
                        while (nearbyPlaceListIterator.hasNext()) {
                            Place currentPlace = nearbyPlaceListIterator.next();
                            PlacesClient placesClient = Places.createClient(getApplicationContext());
                            String placeId = currentPlace.getId();
                            // Specify the fields to return.
                            List<Place.Field> placeFields = Collections.singletonList(Place.Field.PHONE_NUMBER);
                            // Construct a request object, passing the place ID and fields array.
                            FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);
                            placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                                Place place = response.getPlace();
                                //place found
                                if (place.getPhoneNumber() != null) { //If the place has a phone number
                                    loadPhoneNumber(place.getPhoneNumber()); //take it
                                    nearbyPlaceListIterator.stopIteration();
                                }
                            });
                        }
                    }
                });
            }
        });

        googleLocationFinder.findCurrentLocation(this);
    }

    /**
     * Trigger the on success method on the listener
     * @param phoneNumber to pass to caller
     */
    private void loadPhoneNumber(String phoneNumber){
        if(onPhoneNumberGetListener != null){
            onPhoneNumberGetListener.onSuccess(phoneNumber);
        }
    }

    /**
     * Trigger the on fail method on the listener
     */
    private void phoneNumberSearchFailed(){
        if(onPhoneNumberGetListener != null){
            onPhoneNumberGetListener.onFail();
        }
    }

    /**
     * Basic request permission result override
     * @param requestCode 1
     * @param permissions permissions
     * @param grantResults results
     */
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

    /**
     * Toast message error for any type of problem
     */
    private void phoneNumberMessageError(){
        Toast.makeText(getApplicationContext(), "Unable to find any phone number", Toast.LENGTH_LONG).show();
    }

}
