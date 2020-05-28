package com.example.app;

import com.example.app.finals.NearbyRequestType;
import com.example.app.finals.SearchablePlace;
import com.example.app.ui.search.RequestAdapter;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RequestPlaceAdapterTest {


    private RequestAdapter requestAdapter = new RequestAdapter();

    @Test
    public void getRestaurant(){
        NearbyRequestType actual = requestAdapter.getAdaptedPlace(SearchablePlace.RESTAURANT);
        String expect = NearbyRequestType.restaurant.toString();
        assertEquals(actual.toString(), expect);
    }
}