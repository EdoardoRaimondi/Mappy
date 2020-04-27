package com.example.app;

import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.configuration.IMockitoConfiguration;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)

public class GetNearbyPlacesTest {

    private List<HashMap<String, String>> nearbyPlacesList = new ArrayList<>();
    private HashMap<String, String> googlePlaceMap1 = new HashMap<>();
    private HashMap<String, String> googlePlaceMap2 = new HashMap<>();

    private static final int NUM_PLACES = 2;
    @Spy
    GetNearbyPlaces dummyGetNearbyPlaces;

    @Before
    public void setUp() throws JSONException {
        //need to store the data in a List<HashMap<String, String>>
        //nearbyPlacesList = parser.parse(URL_RESPONSE);
        googlePlaceMap1.put("place_name", "Pizzeria");
        googlePlaceMap1.put("vicinity","vicinity");
        googlePlaceMap1.put("lat", "23.2");
        googlePlaceMap1.put("lng", "12.1");
        googlePlaceMap1.put("reference", "ref");
        nearbyPlacesList.add(googlePlaceMap1);

        googlePlaceMap2.put("place_name", "Pizzeria");
        googlePlaceMap2.put("vicinity","vicinity");
        googlePlaceMap2.put("lat", "23.2");
        googlePlaceMap2.put("lng", "12.1");
        googlePlaceMap2.put("reference", "ref");
        nearbyPlacesList.add(googlePlaceMap2);

        dummyGetNearbyPlaces = Mockito.spy(GetNearbyPlaces.class);

        Mockito.doNothing().when(dummyGetNearbyPlaces).animateCamera(Mockito.any(LatLngBounds.Builder.class));
        Mockito.doNothing().when(dummyGetNearbyPlaces).displayNearbyPlace(Mockito.any(MarkerOptions.class));
    }




    @Test
    public void displayNearbyPlacesTest(){
        dummyGetNearbyPlaces.downloadNearbyPlaces(nearbyPlacesList);
        List<MarkerOptions> markerList = dummyGetNearbyPlaces.getMarkerList();
        Assert.assertTrue(markerList.size()==NUM_PLACES);
    }
}