package com.example.app.saved_place_database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

/**
 * Represents a saved place entity in our database
 */
@Entity(primaryKeys = {"latitude", "longitude"} )
public class SavedPlace {

    private double latitude;
    private double longitude;

    @ColumnInfo (name = "place_name")
    private String placeName;

    public void setLatitude(double lat){
        this.latitude = lat;
    }

    public void setLongitude(double lng){
        this.longitude = lng;
    }

    /**
     * Set the name
     * @param name of the place
     */
    public void setPlaceName(String name){
        this.placeName = name;
    }

    /**
     * @return place name
     */
    public String getPlaceName(){
        return this.placeName;
    }

    public double getLatitude(){
        return latitude;
    }

    public double getLongitude(){
        return longitude;
    }


}
