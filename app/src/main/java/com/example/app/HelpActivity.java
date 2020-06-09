package com.example.app;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.telephony.emergency.EmergencyNumber;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.app.factories.IntentFactory;
import com.example.app.finals.NearbyRequestType;
import com.example.app.handlers.HelpActivityHandler;
import com.example.app.listeners.PhoneNumberGetListener;
import com.google.android.libraries.places.api.Places;

public class HelpActivity extends AppCompatActivity {

    private TelephonyManager  telephonyManager;
    private HelpActivityHandler helpActivityHandler = new HelpActivityHandler();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HelpActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
        }
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(HelpActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 2);
        }
        if(!Places.isInitialized())
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        // setting help activity as full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_help);
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
    }

    /**
     * Method to show the nearby hospitals
     * @param view button {@id hospital}
     */
    public void showNearbyHospital(View view) {
        Intent intent = IntentFactory.createNearbyRequestIntent(this, NearbyRequestType.hospital, 5000);
        startActivity(intent);
    }

    /**
     * Method to show the nearby police station
     * @param view button {@id police}
     */
    public void showNearbyPolice(View view) {
        Intent intent = IntentFactory.createNearbyRequestIntent(this, NearbyRequestType.police, 5000);
        startActivity(intent);
    }

    /**
     * Method to show the nearby taxi stations
     * @param view button {@id taxi}
     */
    public void showNearbyTaxi(View view) {
        Intent intent = IntentFactory.createNearbyRequestIntent(this, NearbyRequestType.taxi_stand, 5000);
        startActivity(intent);
    }

    /**
     * Method to show the nearby taxi stations
     * @param view button {@id taxi}
     */
    public void showNearbyPharmacy(View view) {
        Intent intent = IntentFactory.createNearbyRequestIntent(this, NearbyRequestType.pharmacy, 5000);
        startActivity(intent);
    }

    /**
     * Call the police of the nearest police station
     * @param view button {@id call}
     */
    public void callPolice(View view) {
        helpActivityHandler.setPhoneNumberGetListener(new PhoneNumberGetListener() {
            @Override
            public void onSuccess(String phoneNumber) {
                Intent callIntent = IntentFactory.createCallIntent(phoneNumber);
                try{
                startActivity(callIntent);
                }
                catch(ActivityNotFoundException exc){
                    Toast.makeText(getApplicationContext(), getString(R.string.sorry_no_phone), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFail() {
               showPhoneNumberMessageError();
            }
        });

        helpActivityHandler.getPhoneNumber(NearbyRequestType.police, this);
    }

    /**
     * Basic request permission result override
     * @param requestCode 1
     * @param permissions permissions
     * @param grantResults results
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(HelpActivity.this, getString(R.string.unauthorized_call), Toast.LENGTH_SHORT).show();
                }
            }
            case 2:
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(HelpActivity.this, getString(R.string.unauthorized_call), Toast.LENGTH_SHORT).show();
                }

        }
    }

    /**
     * Toast message error for any type of problem
     */
    private void showPhoneNumberMessageError(){
        Toast.makeText(getApplicationContext(), getString(R.string.no_call), Toast.LENGTH_LONG).show();
    }

}
