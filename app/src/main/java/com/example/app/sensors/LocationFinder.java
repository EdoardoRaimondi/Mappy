package com.example.app.sensors;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.app.listeners.OnLocationSetListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

/**
 * Provide an agile (less controlled) location finder
 */
public class LocationFinder {


    private OnLocationSetListener onLocationSetListener;

    public LocationFinder(){
        onLocationSetListener = null;
    }

    public void setOnLocationSetListener(OnLocationSetListener listener){
        onLocationSetListener = listener;
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
        if(onLocationSetListener != null){
            onLocationSetListener.onLocationSet(location);
        }
    }
}
