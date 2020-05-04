package com.example.app.sensors;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.content.Context.LOCATION_SERVICE;

public class GPSManager implements LocationListener {

    private static final int REQUEST_USER_LOCATION_CODE = 99;
    private Context context;

    public GPSManager(Context context) {
        super();
        this.context = context;
    }

    public boolean hasPermissions(){
        return ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean canRequestNow(Activity activity){
        return !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    public boolean isGPSOn(){
        if(hasPermissions()){
            try{
                LocationManager manager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
                assert manager != null;
                return manager.isProviderEnabled( LocationManager.GPS_PROVIDER );
            }
            catch(Exception exc){
                // no exception ever thrown

            }
        }
        return false;
    }
/*
    public Location getLocation(){
        if (ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            Log.e("fist","error");
            return null;
        }
        try {
            LocationManager lm = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            assert lm != null;
            boolean isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (isGPSEnabled){
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 6000,10,this);
                return lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            else{
                return null;
            }
        }
        catch (Exception e){

        }
        return null;
    }
*/
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