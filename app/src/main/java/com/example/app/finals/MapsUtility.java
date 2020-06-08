package com.example.app.finals;

import com.google.android.gms.location.LocationRequest;

/**
 * Class containing all the methods shared by the maps activity
 */
public class MapsUtility {

    // Constants
    public static final int KM_TO_M = 1000;
    public static final int DEFAULT_INCREMENT = 1;

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
