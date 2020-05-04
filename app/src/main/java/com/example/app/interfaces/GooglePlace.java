package com.example.app.interfaces;

/**
 * Interface that define the characteristics
 * of a location in google
 */
public interface GooglePlace {

    /**
     * Method to insert a location. Min information possible
     * @param placeName
     * @param latitude
     * @param longitude
     */
    void insertPlace(String placeName, String latitude, String longitude);

    /**
     * @return place name
     */
    String getName();

    /**
     * @return latitude
     */
    double getLatitude();

    /**
     * @return longitude
     */
    double getLongitude();


}
