package com.example.app.factories;

import android.net.Uri;

/**
 * Factory to create all the necessary url and uri
 */
public abstract class UrlFactory {

    private static final String NEARBY_URL_DOMAIN = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";

    /**
     * Create nearby url request
     * @param latitude lat
     * @param longitude  lng
     * @param radius int
     * @param nearbyPlace the type of place to search
     * @return the string representing the url request
     */
   public static String getNearbyRequest(double latitude, double longitude, String nearbyPlace, int radius) {
       String[] label = {"location", "radius", "type", "sensor", "key"};
       String location = "" + latitude + "," + longitude;
       String[] value = {location, Integer.toString(radius), nearbyPlace, "true", "AIzaSyCIN8HCmGWXf5lzta5Rv2nu8VdIUV4Jp7s"};
        StringBuilder url = new StringBuilder(NEARBY_URL_DOMAIN);
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
     * @param latitude of the destination
     * @param longitude of the destination
     * @return the uri
     */
   public static Uri createDirectionsUrl(double latitude, double longitude){
       return Uri.parse("google.navigation:q=" + latitude + "," + longitude);
   }
}
