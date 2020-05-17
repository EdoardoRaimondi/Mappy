package com.example.app.factories;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.app.HelpActivity;
import com.example.app.HomeActivity;
import com.example.app.MainActivity;
import com.example.app.MapsActivity;
import com.example.app.finals.HomeMode;
import com.example.app.finals.NearbyRequestType;
import com.example.app.ui.saved.SavedFragment;
import com.google.android.gms.maps.model.Marker;

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
     * @return the intent
     */
     public static Intent createLobbyReturn(Context context){
         Intent intent = new Intent(context, MainActivity.class);
         return intent;
     }

    /**
     * Method to create a intent in order to set
     * a marker in the current user position
     * @param context of the activity
     * @param mode of the command (view or set)
     * @return the intent
     */
    public static Intent createHomeRequest(Context context, HomeMode mode){
        Intent intent = new Intent(context, HomeActivity.class);
        intent.putExtra(HomeActivity.SET_KEY, mode);
        return intent;
    }

    public static Intent createPlaceToSave(Context context, Marker marker){
        Intent intent = new Intent(context, SavedFragment.class);
        String placeName = marker.getTitle();
        intent.putExtra("NAME", placeName);
        intent.putExtra("LAT", marker.getPosition().latitude);
        intent.putExtra("LONG", marker.getPosition().longitude);
        return intent;
    }
}
