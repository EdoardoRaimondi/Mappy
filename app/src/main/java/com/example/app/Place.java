package com.example.app;

import androidx.annotation.NonNull;

import com.example.app.interfaces.GooglePlace;

import java.util.HashMap;

/**
 * Class representing a place in google maps
 */
public class Place implements GooglePlace {

    private static final String PLACE_NAME = "place_name";
    private static final String LATITUDE   = "lat";
    private static final String LONGITUDE  = "lng";

    private HashMap<String, String> place = new HashMap<>();

    /**
     * Method to insert a name
     * @param placeName the name of the place
     */
    public void insertName(@NonNull String placeName){
        place.put(PLACE_NAME, placeName);
    }

    /**
     * Method to insert a name
     * @param latitude the latitude of the place
     */
    public void insertLatitude(@NonNull String latitude){
        place.put(LATITUDE, latitude);
    }

    /**
     * Method to insert a name
     * @param longitude the longitude of the place
     */
    public void insertLongitude(@NonNull String longitude){
        place.put(LONGITUDE, longitude);
    }

    /**
     * Method to insert a place. The min necessary
     * @param placeName the name of place
     * @param latitude  its latitude
     * @param longitude its longitude
     */
    public void insertPlace(@NonNull String placeName, @NonNull String latitude, @NonNull String longitude){
        insertName(placeName);
        insertLatitude(latitude);
        insertLongitude(longitude);
    }

    /**
     * Method to get the place name
     * @return String representing the place name
     */
    public String getName(){
        return place.get(PLACE_NAME);
    }

    /**
     * Method to get the place latitude
     * @return Double representing the latitude. -1 if lat is null
     */
    public double getLatitude(){
        String lat = place.get(LATITUDE);
        if(lat == null) return -1;
        return Double.parseDouble(lat);
    }

    /**
     * Method to get the place longitude
     * @return Double representing the longitude. -1 if lng is null
     */
    public double getLongitude(){
        String lng = place.get(LONGITUDE);
        if(lng == null) return -1;
        return Double.parseDouble(lng);
    }


}
