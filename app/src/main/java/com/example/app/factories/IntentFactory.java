package com.example.app.factories;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.example.app.HelpActivity;
import com.example.app.HomeActivity;
import com.example.app.MainActivity;
import com.example.app.MapsActivity;
import com.example.app.SavedPlaceActivity;
import com.example.app.finals.NearbyRequestType;
import com.google.android.libraries.places.api.model.Place;

/**
 * Factory class containing a creator method for each type of intent you need
 */
public abstract class IntentFactory {

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
     * @return the intent
     */
    public static Intent createHomeRequest(Context context){
        return new Intent(context, HomeActivity.class);
    }

    /**
     * Method to create a intent in order to
     * send place information
     * @param context of the activity
     * @param place to display
     * @return the intent
     */
    public static Intent createPlaceInfoIntent(Context context, Place place){
        Intent intent = new Intent(context, SavedPlaceActivity.class);
        intent.putExtra("place", place);
        return intent;
    }

    /**
     * Method to create an intent in order to
     * open google maps and show a place directions
     * @param context of the activity
     * @param uri for google maps
     * @return the intent
     */
    public static Intent createGoogleMapsDirectionsIntent(Context context, Uri uri){
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
        mapIntent.setPackage("com.google.android.apps.maps");
        return mapIntent;
    }
}
