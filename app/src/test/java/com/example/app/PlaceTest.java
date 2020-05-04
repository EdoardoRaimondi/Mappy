package com.example.app;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;

import static org.junit.Assert.*;

public class PlaceTest {

    private Place testPlace = new Place();

    private static final String NAME = "kebab";
    private static final String LAT  = "22";
    private static final String LNG  = "12.4";


    @Test
    public void insertName() {
        testPlace.insertName(NAME);
        assertEquals(testPlace.getName(), NAME);
    }

    @Test
    public void insertLatitude() {
        testPlace.insertLatitude(LAT);
        assertEquals(testPlace.getLatitude(),Double.parseDouble(LAT), 10);
    }

    @Test
    public void insertLongitude() {
        testPlace.insertLongitude(LNG);
        assertEquals(testPlace.getLongitude(), Double.parseDouble(LNG), 10);
    }

    @Test
    public void insertPlace_nullLongitude() {
        testPlace.insertPlace(NAME, LAT, null);
        assertEquals(testPlace.getLongitude(), -1, 10);
    }

    @Test
    public void insertPlace_nullLatitude() {
        testPlace.insertPlace(NAME, null, LNG);
        assertEquals(testPlace.getLatitude(), -1, 10);
    }

    @Test
    public void insertPlace() {
        testPlace.insertPlace(NAME, LAT, LNG);
        assertEquals(testPlace.getLatitude(), Double.parseDouble(LAT), 10);
        assertEquals(testPlace.getLongitude(), Double.parseDouble(LNG), 10);
        assertEquals(testPlace.getName(), NAME);
    }

}