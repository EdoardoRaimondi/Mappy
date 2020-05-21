package com.example.app.saved_place_database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

/**
 * Represents a saved place entity in our database
 */
@Entity(primaryKeys = {"latitude", "longitude"}, tableName = "saved_place_table")
public class SavedPlace {

    @ColumnInfo (name = "latitude")
    private double latitude;
    @ColumnInfo (name = "longitude")
    private double longitude;
    @ColumnInfo (name = "place_name")
    private String placeName;

    /**
     * Constructor
     * @param latitude  key
     * @param longitude key
     */
    public SavedPlace(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setPlaceName(String name){
        this.placeName = name;
    }


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
