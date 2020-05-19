package com.example.app;

import com.example.app.finals.NearbyRequestType;
import com.example.app.ui.search.RequestPlaceAdapter;

import org.junit.Test;

import static org.junit.Assert.*;

public class RequestPlaceAdapterTest {

    private final static String RESTAURANT = "RESTAURANT";
    private final static String NOT_EXIST  = "FLEX";

    @Test
    public void getRestaurant(){
        NearbyRequestType actual = RequestPlaceAdapter.getAdaptedPlace(RESTAURANT);
        String expect = NearbyRequestType.restaurant.toString();
        assertEquals(actual.toString(), expect);
    }

    @Test (expected = NullPointerException.class)
    public void getPlace_notExist(){
        RequestPlaceAdapter.getAdaptedPlace(NOT_EXIST);
    }

}