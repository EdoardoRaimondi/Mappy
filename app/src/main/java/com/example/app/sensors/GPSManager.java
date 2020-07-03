package com.example.app.sensors;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.location.LocationManagerCompat;

import java.util.List;


/*
 * Class for managing GPS providers
 */
public class GPSManager {

    // Object params
    private Context context;
    private LocationManager locationManager;

    /**
     * Constructor
     * @param context The Context GPSManager instance has to be attached
     */
    public GPSManager(Context context) {
        super();
        this.context = context;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    /*
     * Method to know if app has GPS permissions granted
     */
    public boolean hasPermissions() {
        return ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Method to know if app can request GPS permissions
     * @param activity The Activity to attach
     */
    public boolean canRequestNow(Activity activity) {
        return !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    /**
     * Method to require GPS permissions
     * @param activity The Activity required permissions
     * @param reqCode  The request code (int)
     */
    public void requirePermissions(Activity activity, int reqCode) {
        if (canRequestNow(activity) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, reqCode);
        }
    }

    /*
     * Method to know if GPS Sensor is enabled
     */
    public boolean isGPSOn() {
        if (hasPermissions()) {
            try {
                return LocationManagerCompat.isLocationEnabled(locationManager);
            } catch (Exception exc) {
                return false;
            }
        }
        return false;
    }

    /**
     * Method to register callback for GPS Providers updates
     * @param locationListener The LocationListener to call
     * @param interval         The interval check time
     */
    public void requireUpdates(LocationListener locationListener, int interval) {
        if (
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED

                        &&

                ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            return;
        }
        boolean found = false;
        List<String> providers = getActiveProviders();
        for(int i=0; i<providers.size(); i++){
            if(providers.get(i).equalsIgnoreCase(LocationManager.GPS_PROVIDER)){
                found = true;
            }
        }
        if(found) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    interval,
                    10, locationListener
            );
        }
        else{
            locationManager.requestLocationUpdates(
                    LocationManager.PASSIVE_PROVIDER,
                    interval,
                    10, locationListener
            );
        }
    }

    public void removeCallback(LocationListener locationListener){
        locationManager.removeUpdates(locationListener);
    }

    /**
     * Getter method to find all enabled Location providers
     */
    private List<String> getActiveProviders(){
        return locationManager.getProviders(true);
    }

}