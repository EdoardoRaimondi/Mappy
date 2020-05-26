package com.example.app.factories;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

/**
 * Factory to create all the necessary url and uri
 */
public abstract class UrlFactory {

    /**
     * Create nearby url request
     * @param domain nearby request
     * @param label label to append
     * @param value value to append
     * @return the string representing the url request
     */
   public static String createNearbyUrl(String domain, String[] label, String[] value) {
        StringBuilder url = new StringBuilder(domain);
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
