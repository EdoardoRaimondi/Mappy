package com.example.app.sensors;

import android.content.Context;
import android.location.Location;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.app.interfaces.LocationFinder;
import com.example.app.listeners.LocationSetListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * Provide an agile location finder.
 * If you need to get user location, just implement a {@link LocationSetListener}
 * in your class and then call findCurrentLocation method. We will give it to you only when (and if) ready.
 * We also handle all the errors.
 * Is there something easier?
 */
public class GoogleLocationFinder implements LocationFinder {


    private LocationSetListener locationSetListener;

    public GoogleLocationFinder(){
        locationSetListener = null;
    }

    public void setLocationSetListener(LocationSetListener listener){
        locationSetListener = listener;
    }

    /**
     * Trigger the listener when the location has been found
     * @param context of the activity caller
     */
    public void findCurrentLocation(Context context){

        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(context);
        client.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location != null){
                            locationSet(location);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Unable to reach your position", Toast.LENGTH_LONG).show();
                    }
                });
    }


    /**
     * Method that trigger the listener
     * @param location set
     */
    private void locationSet(Location location){
        if(locationSetListener != null){
            locationSetListener.onLocationSet(location);
        }
    }
}
