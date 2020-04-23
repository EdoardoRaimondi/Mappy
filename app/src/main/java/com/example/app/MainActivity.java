package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

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

    public static long RADIUS;

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
                RADIUS = parseRadius(radiusString);
                Log.d("RADIUS", String.valueOf(MainActivity.RADIUS));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //default radius is 2km
                RADIUS = 2000;
            }
        });
    }


    /**
     * Method to send a nearby disco showing request to the Maps activity
     * @param view button {@id nearby_disco}
     *
     */
    public void nearbyDiscoRequest(View view){
        Intent showNearbyDisco = new Intent(this, MapsActivity.class);
        showNearbyDisco.putExtra(MapsActivity.NEARBY_KEY, NearbyRequestType.DISCO);
        showNearbyDisco.putExtra(MapsActivity.RADIUS, RADIUS);
        startActivity(showNearbyDisco);
    }

    /**
     * Method to send a nearby restaurant showing request to the Maps activity
     * @param view button {@id nearby_restaurant}
     */
    public void nearbyRestaurantRequest(View view){
        Intent showNearbyRestaurant = new Intent(this, MapsActivity.class);
        showNearbyRestaurant.putExtra(MapsActivity.NEARBY_KEY, NearbyRequestType.RESTAURANT);
        showNearbyRestaurant.putExtra(MapsActivity.RADIUS, RADIUS);
        Log.d("RADIUS", String.valueOf(RADIUS));
        startActivity(showNearbyRestaurant);
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
