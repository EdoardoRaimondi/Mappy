package com.example.app.factories;

import android.net.Uri;

import com.example.app.finals.MapsUtility;

/**
 * Factory to create all the necessary url and uri
 */
public abstract class UrlFactory {

    /**
     * Create nearby URL request
     * @param latitude    The current latitude
     * @param longitude   The current longitude
     * @param radius      The research radius selected
     * @param nearbyPlace The type of place to search
     * @return The string representing the URL request
     */
   public static String getNearbyRequest(double latitude, double longitude, String nearbyPlace, int radius) {
       String[] label = {"location", "radius", "type", "sensor", "key"};
       String location = "" + latitude + "," + longitude;
       String[] value = {location, Integer.toString(radius), nearbyPlace, "true", "AIzaSyCIN8HCmGWXf5lzta5Rv2nu8VdIUV4Jp7s"};
        StringBuilder url = new StringBuilder(MapsUtility.NEARBY_URL_DOMAIN);
        url.append('?');
        for(int i = 0; i < label.length; i++){
            if(i != 0){
                url.append('&');
            }
            url.append(label[i]);
            url.append('=');
            url.append(value[i]);
        }
        return url.toString();
   }

    /**
     * Create an url direction request from current position to a destination
     * @param latitude  Latitude of the destination
     * @param longitude Longitude of the destination
     * @return The URI wanted
     */
   public static Uri createDirectionsUrl(double latitude, double longitude){
       return Uri.parse("google.navigation:q=" + latitude + "," + longitude);
   }
}
