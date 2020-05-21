package com.example.app.finals;


import com.google.android.gms.location.LocationRequest;

/**
 * Class containing all the methods shared by the maps activity
 */
public class MapsUtility {


    /**
     * Perform a location request
     */
    public static LocationRequest createLocationRequest(){
        final LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

}
