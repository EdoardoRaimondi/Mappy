package com.example.app;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

/**
 * Main UI activity. Here the user can choose the main actions.
 * (Need to write every button what actually does, when completed)
 */
public class MainActivity extends AppCompatActivity {

    private long radius;

    // constants for restoring instance of views
    private static final String SPINNER_KEY = "spinner_k";
    private static final int INVALID_INDEX = -1;
    private Spinner radiusSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //create the spinner and fill it
        radiusSpinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> grade = ArrayAdapter.createFromResource(
                this,
                R.array.RADIUS,
                android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        grade.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        radiusSpinner.setAdapter(grade);
        //set the listener
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
                //Default radius is 1km
                radius = 1000;
            }
        });

        // restoring instance status of views
        if(savedInstanceState != null) {
            int spinnerSelected = savedInstanceState.getInt(SPINNER_KEY, INVALID_INDEX);
            if(spinnerSelected != INVALID_INDEX){
                radiusSpinner.setSelection(spinnerSelected);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstance) {
        savedInstance.putInt(SPINNER_KEY, radiusSpinner.getSelectedItemPosition());
        super.onSaveInstanceState(savedInstance);
    }

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
    }

    /**
     * Method to send a nearby disco showing request to the Maps activity
     * @param view button {@id nearby_disco}
     */
    public void nearbyDiscoRequest(View view) {
        Intent showNearbyDisco = IntentFactory.createNearbyRequestIntent(this, NearbyRequestType.DISCO, radius);
        startActivity(showNearbyDisco);
    }

    /**
     * Method to send a nearby restaurant showing request to the Maps activity
     * @param view button {@id nearby_restaurant}
     */
    public void nearbyRestaurantRequest(View view) {
        Intent showNearbyRestaurant = IntentFactory.createNearbyRequestIntent(this, NearbyRequestType.RESTAURANT, radius);
        Log.d("RADIUS", String.valueOf(radius));
        startActivity(showNearbyRestaurant);
    }



    //NATIVE METHODS

    /**
     * Parser for the radius long
     * @param radius to parse
     */
    public native long parseRadius(String radius);

    /**
     * Library loading
     */
    static {
        System.loadLibrary("libmain_native_lib");
    }

}
