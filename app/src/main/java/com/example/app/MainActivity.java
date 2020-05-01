package com.example.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.app.factories.DialogFactory;
import com.example.app.factories.IntentFactory;
import com.example.app.finals.NearbyRequestType;
import com.example.app.sensors.GPSManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

/**
 * Main UI activity. Here the user can choose the main actions.
 * (Need to write every button what actually does, when completed)
 */
public class MainActivity extends AppCompatActivity {

    private int radius;

    // constants for restoring instance of views
    private static final String SPINNER_KEY = "spinner_k";
    private static final int INVALID_POSITION = -1;

    private static final int REQUEST_USER_LOCATION_CODE = 99;

    private Spinner radiusSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // creating spinner and filling it
        radiusSpinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> grade = ArrayAdapter.createFromResource(
                this,
                R.array.RADIUS,
                android.R.layout.simple_spinner_item);
        // specify the layout to use when the list of choices appears
        grade.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // applying the adapter to the spinner
        radiusSpinner.setAdapter(grade);
        // setting listener
        radiusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            /**
             * Callback when an item from the radiusSpinner is selected
             * @param parent    adapter view
             * @param view      reference to the spinner widget
             * @param position  of the item
             * @param id        of the spinner
             */
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // An radius was selected. You can retrieve the selected item using
                String radiusString = parent.getItemAtPosition(position).toString();
                //Set the desired radius
                radius = parseRadius(radiusString);
            }

            /**
             * Callback when the user don't select items
             * @param parent adapter view
             */
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // default radius is 1km
                radius = getResources().getInteger(R.integer.default_radius);
            }
        });

        // restoring instance status of views
        if(savedInstanceState != null) {
            int spinnerSelected = savedInstanceState.getInt(SPINNER_KEY, INVALID_POSITION);
            if(spinnerSelected != INVALID_POSITION){
                radiusSpinner.setSelection(spinnerSelected);
            }
        }
    }

    /**
     * Callback to save the state when necessary
     * @param savedInstanceState Bundle where to save places information
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(SPINNER_KEY, radiusSpinner.getSelectedItemPosition());
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Callback when user return here or activity
     * has just been created
     */
    @Override
    protected void onResume() {
        super.onResume();
        // checking Google Play services apk
        GoogleApiAvailability google = GoogleApiAvailability.getInstance();
        int result = google.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS){
            Dialog dial = google.getErrorDialog(this, result, GoogleApiAvailability.GOOGLE_PLAY_SERVICES_VERSION_CODE);
            dial.show();
        }
        else{
            GPSManager gpsManager = new GPSManager(getApplicationContext());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(!gpsManager.hasPermissions()){
                    if(gpsManager.canRequestNow(this)) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_USER_LOCATION_CODE);
                    }
                    else{
                        // show resonable
                    }
                }
                else {
                    if (!gpsManager.isGPSOn()) {
                        DialogFactory.showActivateGPSAlertDialog(this);
                    }
                }
            }
            else{
                // notify user he has to give permissions
            }
        }
    }

    // PERMISSIONS

    /**
     * Callback to check user permission
     * @param requestCode   of the permission
     * @param permissions   of the request
     * @param grantResults  of the permission request
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_USER_LOCATION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, getString(R.string.thank_you), Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, getString(R.string.no_gps_permission), Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Method to send a nearby disco showing request to the Maps activity
     * @param view button {@id nearby_disco}
     */
    public void nearbyDiscoRequest(View view) {
        Intent showNearbyDisco = IntentFactory.createNearbyRequestIntent(this, NearbyRequestType.night_club, radius);
        startActivity(showNearbyDisco);
    }

    /**
     * Method to send a nearby restaurant showing request to the Maps activity
     * @param view button {@id nearby_restaurant}
     */
    public void nearbyRestaurantRequest(View view) {
        Intent showNearbyRestaurant = IntentFactory.createNearbyRequestIntent(this, NearbyRequestType.restaurant, radius);
        startActivity(showNearbyRestaurant);
    }

    /**
     * Method to send an help lobby request
     * @param view button {@id button}
     */
    public void helpRequest(View view){
        Intent helpIntent = IntentFactory.createHelpIntentRequest(this);
        startActivity(helpIntent);
    }

    // NATIVE METHODS

    /**
     * Parser for the radius long
     * @param radius to parse
     */
    public native int parseRadius(String radius);

    /**
     * Library loading
     */
    static {
        System.loadLibrary("libmain_native_lib");
    }

}
