package com.example.app.sensors;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.location.LocationManagerCompat;


/*
 * Class for managing GPS providers
 */
public class GPSManager implements LocationListener {

    // Object params
    private Context context;

    /**
    * Constructor
    * @param context The Context GPSManager instance has to be attached
    */
    public GPSManager(Context context) {
        super();
        this.context = context;
    }

    /*
    * Method to know if app has GPS permissions granted
    */
    public boolean hasPermissions(){
        return ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;
    }

    /**
    * Method to know if app can request GPS permissions
    * @param activity The Activity to attach
    */
    public boolean canRequestNow(Activity activity){
        return !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    /**
    * Method to require GPS permissions
    * @param activity The Activity required permissions
    * @param reqCode  The request code (int)
    */
    public void requirePermissions(Activity activity, int reqCode){
        if(canRequestNow(activity) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, reqCode);
        }
    }

    /*
    * Method to know if GPS Sensor is enabled
    */
    public boolean isGPSOn(){
        if(hasPermissions()) {
            try {
                LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                if (manager != null) {
                    /*List<String> enabledProviders = manager.getProviders(true);
                    return !enabledProviders.isEmpty();*/
                    return LocationManagerCompat.isLocationEnabled(manager);
                }
            }
            catch (Exception exc) {
                return false;
            }
        }
        return false;
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

}