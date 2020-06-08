package com.example.app.sensors;

import android.content.Context;
import android.location.Location;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.app.R;
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
 */
public class GoogleLocationFinder implements LocationFinder {

    // Object params
    private LocationSetListener locationSetListener;

    /*
    * Constructor
    */
    public GoogleLocationFinder(){
        locationSetListener = null;
    }

    /**
    * Setter method of listener
    * @param listener The Listener
    */
    public void setLocationSetListener(LocationSetListener listener){
        locationSetListener = listener;
    }

    /**
     * Trigger the listener when the location has been found
     * @param context Context of the activity caller
     */
    public void findCurrentLocation(Context context){

        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(context);
        client.getLastLocation()
                .addOnSuccessListener(location -> {
                    if(location != null){
                        locationSet(location);
                    }
                })
                .addOnFailureListener(
                        e -> Toast.makeText(
                                context,
                                context.getResources().getString(R.string.no_position),
                                Toast.LENGTH_LONG
                        ).show());
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
