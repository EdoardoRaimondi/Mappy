package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 * Main UI activity. Here the user can choose the main actions.
 * (Need to write every button what actually does, when completed)
 */
public class MainActivity extends AppCompatActivity {

    public long radius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //create the spinner and fill it
        Spinner radiusSpinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> grade = ArrayAdapter.createFromResource(
                this,
                R.array.RADIUS,
                android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        grade.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        radiusSpinner.setAdapter(grade);
        radiusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // An item was selected. You can retrieve the selected item using
                String radiusString = parent.getItemAtPosition(position).toString();
                radius = parseRadius(radiusString);
                Log.d("RADIUS", String.valueOf(radius));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //default radius is 2km
                radius = 2000;
            }
        });
    }


    /**
     * Method to send a nearby disco showing request to the Maps activity
     * @param view button {@id nearby_disco}
     *
     */
    public void nearbyDiscoRequest(View view){
        Intent showNearbyDisco = createRequestIntent(this, NearbyRequestType.DISCO, radius);
        startActivity(showNearbyDisco);
    }

    /**
     * Method to send a nearby restaurant showing request to the Maps activity
     * @param view button {@id nearby_restaurant}
     */
    public void nearbyRestaurantRequest(View view){
        Intent showNearbyRestaurant = createRequestIntent(this, NearbyRequestType.RESTAURANT, radius);
        Log.d("RADIUS", String.valueOf(radius));
        startActivity(showNearbyRestaurant);
    }


    /**
     * Method to create a request Intent
     * @param context
     * @param requestType The place I'm looking for (disco, restaurant...)
     * @param radius The research radius
     * @return The intent
     */
    private static Intent createRequestIntent(Context context, NearbyRequestType requestType, long radius){
        Intent intent = new Intent(context, MapsActivity.class);
        intent.putExtra(MapsActivity.NEARBY_KEY, requestType);
        intent.putExtra(MapsActivity.RADIUS, radius);
        return intent;
    }


    //NATIVE METHODS

    /**
     * Parser for the radius integer
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
