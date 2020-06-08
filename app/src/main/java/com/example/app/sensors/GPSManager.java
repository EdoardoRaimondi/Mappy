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
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class GPSManager implements LocationListener {

    private Context context;

    public GPSManager(Context context) {
        super();
        this.context = context;
    }

    public boolean hasPermissions(){
        return ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean canRequestNow(Activity activity){
        return !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    public void requirePermissions(Activity activity, int reqCode){
        if(canRequestNow(activity) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, reqCode);
        }
    }

    public boolean isGPSOn(){
        if(hasPermissions()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                    if (manager != null) {
                        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    }
                }
                catch (Exception exc) {
                    return false;
                }
            }
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                int locationMode = 0;
                try {
                    locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
                }
                catch (Settings.SettingNotFoundException e) {
                    return false;
                }
                return locationMode != Settings.Secure.LOCATION_MODE_OFF;
            }
        }
        return false;
    }

    public boolean isProviderEnabled(){
        if(hasPermissions()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                    if (manager != null) {
                        return manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                    }
                }
                catch (Exception exc) {
                    return false;
                }
            }
        }
        else{
            return false;
        }
        return true;
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