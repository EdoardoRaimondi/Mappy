package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void showMap(View view){
        Intent showMap = new Intent(this, MapsActivity.class);
        startActivity(showMap);
    }

    /**
     * Method to send an nearby disco showing request to the Maps activity
     * @param view button {@id nearby_disco}
     * @TODO this method implementation is on the way. NOW IT DOESN'T WORK!!
     */
    public void nearbyDiscoRequest(View view){
        Intent showNearbyDisco = new Intent(this, MapsActivity.class);
        //showNearbyDisco.putExtra(MapsActivity.NEARBY_KEY, NearbyRequestType.DISCO);
        //showNearbyDisco.putExtra(MapsActivity.RADIUS, 1000);
        //instead of 1000 there should be a reference to the UI
        //Basically the user has to choose the radius
        startActivity(showNearbyDisco);
    }
}