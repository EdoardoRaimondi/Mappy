package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
    }

    /**
     * Method to show the nearby hospitals
     * @param view button {@id hospital}
     */
    public void showNearbyHospital(View view){
        Intent intent = IntentFactory.createNearbyRequestIntent(this, NearbyRequestType.HOSPITAL, 1000);
        startActivity(intent);
    }

    /**
     * Method to show the nearby police station
     * @param view button {@id police}
     */
    public void showNearbyPolice(View view){
        Intent intent = IntentFactory.createNearbyRequestIntent(this, NearbyRequestType.POLICE, 1000);
        startActivity(intent);
    }

    /**
     * Method to show the nearby taxi stations
     * @param view button {@id taxi}
     */
    public void showNearbyTaxi(View view){
        Intent intent = IntentFactory.createNearbyRequestIntent(this, NearbyRequestType.TAXI, 1000);
        startActivity(intent);
    }
}
