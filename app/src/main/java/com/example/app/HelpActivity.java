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
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.app.factories.IntentFactory;
import com.example.app.finals.CallerReturn;
import com.example.app.finals.NearbyRequestType;
import com.example.app.handlers.HelpActivityHandler;
import com.example.app.listeners.PhoneNumberGetListener;
import com.google.android.libraries.places.api.Places;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
* Help Activity class for sos support
*/
public class HelpActivity extends AppCompatActivity  implements View.OnClickListener{

    // Dictionary for onClick listener
    private Map<String, NearbyRequestType> dictionary;
    private HelpActivityHandler helpActivityHandler = new HelpActivityHandler();
    private TelephonyManager telephonyManager;

    /**
     * Callback when the activity is created
     */
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
        // Setting help activity as full screen
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        setContentView(R.layout.activity_help);

        // Defining dictionary for get nearby request type
        dictionary = new HashMap<>();
        for(int i = 0; i < NearbyRequestType.values().length; i++){
            dictionary.put(NearbyRequestType.values()[i].toString(), NearbyRequestType.values()[i]);
        }
        // Getting all image button ids
        ArrayList<View> allButtons;
        allButtons = findViewById(R.id.full_page).getTouchables();
        for(int i = 0; i < allButtons.size(); i++){
            ImageButton button = (ImageButton) allButtons.get(i);
            button.setOnClickListener(this);
        }
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        findViewById(R.id.sos_button).setOnClickListener(v -> {
            helpActivityHandler.setPhoneNumberGetListener(new PhoneNumberGetListener() {
                @Override
                public void onSuccess(String phoneNumber) {
                    Log.d("HERE!", phoneNumber);
                    Intent callIntent = IntentFactory.createCallIntent(phoneNumber);
                    try{
                        startActivity(callIntent);
                    }
                    catch(ActivityNotFoundException exc){
                        showPhoneNumberMessageError();
                    }
                }

                @RequiresApi(api = Build.VERSION_CODES.Q)
                @Override
                public void onFail() {
                    // Here because no police phone number nearby user has been found
                    if (BuildConfig.VERSION_CODE >= Build.VERSION_CODES.Q) { // Always false but we anticipate the future (Use the emulator to see how it works)
                        EmergencyNumber emergencyNumber = telephonyManager.getEmergencyNumberList().get(EmergencyNumber.EMERGENCY_SERVICE_CATEGORY_POLICE).get(0);
                        if (emergencyNumber == null) showPhoneNumberMessageError();
                        else {
                            Intent callIntent = IntentFactory.createCallIntent(emergencyNumber.getNumber());
                            startActivity(callIntent);
                        }
                    }
                    showPhoneNumberMessageError();
                }

            });

            helpActivityHandler.getPhoneNumber(NearbyRequestType.police, getApplicationContext(), getResources().getString(R.string.google_maps_key));
        });

    }

    /**
     * Basic request permission result override
     * @param requestCode  Integer representing request code
     * @param permissions  String[] representing permissions
     * @param grantResults Integer[] representing results
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

    /**
     * Common OnClick listener
     * @param v The View that called the listener
     */
    @Override
    public void onClick(View v) {
        Log.d("CRAP","");
        // If it is a valid id
        if (v.getId() != View.NO_ID){
            // Getting string variable id
            String stringId = v.getResources().getResourceName(v.getId());
            // Getting string variable id filtered
            stringId = stringId.replace("com.example.app:id/","");
            // Searching NearbyRequestType from dictionary
            NearbyRequestType type;
            try {
                type = dictionary.get(stringId);
            }
            catch (NullPointerException exc){
                type = NearbyRequestType.hospital;
            }
            if(type == null){
                type = NearbyRequestType.hospital;
            }
            // Going to MapsActivity
            Intent intent = IntentFactory.createNearbyRequestIntentIAm(
                    this,
                    type,
                    getResources().getInteger(R.integer.help_radius),
                    CallerReturn.help_activity
            );
            startActivity(intent);
            finish();
        }
    }

}
