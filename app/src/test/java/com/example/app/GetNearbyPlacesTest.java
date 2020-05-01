package com.example.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.app.listeners.OnResultSetListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
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
    private static final LatLng LAT_LNG = new LatLng(1.1, 1.2);
    private static String PLACE_NAME    = "Da Cracco";

    @Spy
    GetNearbyPlaces dummyGetNearbyPlaces;


    @Before
    public void setUp() throws JSONException {
        MockitoAnnotations.initMocks(this);
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
        Mockito.doNothing().when(dummyGetNearbyPlaces).displayMarkers(Mockito.any(MarkerOptions.class));
    }


    @Test
    /**
     * Test if the restore marker list, is filled correctly
     * N.B. In order to make every test work, it is necessary to
     * disable (comment) the method icon() in createMarker() method
     */
    public void downloadNearbyPlacesTest_fillTheMarkerList(){
        dummyGetNearbyPlaces.downloadNearbyPlaces(nearbyPlacesList);
        List<MarkerOptions> markerList = dummyGetNearbyPlaces.getMarkerList();
        Assert.assertEquals(markerList.size(), NUM_PLACES);
    }

    @Test
    public void downloadNearbyPlacesTest_noPlaces(){
        List<HashMap<String, String>> emptyNearbyPlacesList = new ArrayList<>();
        dummyGetNearbyPlaces.downloadNearbyPlaces(emptyNearbyPlacesList);
        Assert.assertEquals(dummyGetNearbyPlaces.getMarkerList().size(), 0);
    }


    @Test
    public void createMarker_checkEqualNames(){
        MarkerOptions testMarkerOptions = new MarkerOptions();
        testMarkerOptions.title(PLACE_NAME);
        testMarkerOptions.position(LAT_LNG);
        //testMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        MarkerOptions markerOptions = dummyGetNearbyPlaces.createMarker(LAT_LNG, PLACE_NAME);
        Assert.assertEquals(markerOptions.getTitle(), testMarkerOptions.getTitle());
    }

    @Test
    public void createMarker_checkEqualPosition(){
        MarkerOptions testMarkerOptions = new MarkerOptions();
        testMarkerOptions.title(PLACE_NAME);
        testMarkerOptions.position(LAT_LNG);
        //testMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        MarkerOptions markerOptions = dummyGetNearbyPlaces.createMarker(LAT_LNG, PLACE_NAME);
        Assert.assertEquals(markerOptions.getPosition(), testMarkerOptions.getPosition());
    }

}