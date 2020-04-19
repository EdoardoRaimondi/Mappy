package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Main UI activity. Here the user can choose the main actions.
 * (Need to write every button what actually does, when completed)
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Method to send a nearby disco showing request to the Maps activity
     * @param view button {@id nearby_disco}
     *
     */
    public void nearbyDiscoRequest(View view){
        Intent showNearbyDisco = new Intent(this, MapsActivity.class);
        showNearbyDisco.putExtra(MapsActivity.NEARBY_KEY, NearbyRequestType.DISCO);
        showNearbyDisco.putExtra(MapsActivity.RADIUS, 1000);
        //instead of 1000 there should be a reference to the UI
        //Basically the user has to choose the radius
        startActivity(showNearbyDisco);
    }

    /**
     * Method to send a nearby restaurant showing request to the Maps activity
     * @param view button {@id nearby_restaurant}
     */
    public void nearbyRestaurantRequest(View view){
        Intent showNearbyRestaurant = new Intent(this, MapsActivity.class);
        showNearbyRestaurant.putExtra(MapsActivity.NEARBY_KEY, NearbyRequestType.RESTAURANT);
        showNearbyRestaurant.putExtra(MapsActivity.RADIUS, 1000);
        startActivity(showNearbyRestaurant);
    }
}