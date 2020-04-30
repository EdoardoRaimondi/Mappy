package com.example.app.factories;

import android.content.Context;
import android.content.Intent;

import com.example.app.HelpActivity;
import com.example.app.MainActivity;
import com.example.app.MapsActivity;
import com.example.app.finals.NearbyRequestType;

/**
 * Factory class containing a creator method for each type of intent you need
 */
public class IntentFactory {

    /**
     * Method to create a nearby request Intent
     * @param context     of the activity
     * @param requestType The place I'm looking for (disco, restaurant...)
     * @param radius      The research radius
     * @return The intent created
     */
    public static Intent createNearbyRequestIntent(Context context, NearbyRequestType requestType, int radius){
        Intent intent = new Intent(context, MapsActivity.class);
        intent.putExtra(MapsActivity.NEARBY_KEY, requestType);
        intent.putExtra(MapsActivity.RADIUS, radius);
        return intent;
    }

    /**
     * Method to create an help request Intent
     * @param context of the activity
     * @return The intent created
     */
    public static Intent createHelpIntentRequest(Context context){
        Intent intent = new Intent(context, HelpActivity.class);
        return intent;
    }

    /**
     * Method to create a intent in order to return
     * to the main lobby
     * @param context of the current activity
     */
     public static Intent createLobbyReturn(Context context){
         Intent intent = new Intent(context, MainActivity.class);
         return intent;
     }
}
