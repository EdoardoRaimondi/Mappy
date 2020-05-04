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

    private List<Place> nearbyPlacesList = new ArrayList<>();
    private Place googlePlaceMap1 = new Place();
    private Place googlePlaceMap2 = new Place();

    private static final int NUM_PLACES = 2;
    private static final LatLng LAT_LNG = new LatLng(1.1, 1.2);
    private static String PLACE_NAME    = "Da Cracco";

    @Spy
    GetNearbyPlaces dummyGetNearbyPlaces;


    @Before
    public void setUp() throws JSONException {
        MarkerOptions marker = new MarkerOptions();
        MockitoAnnotations.initMocks(this);

        //need to store the data in a List<PLace>
        googlePlaceMap1.insertPlace("pizzeria", "11.2", "34.7");
        nearbyPlacesList.add(googlePlaceMap1);

        googlePlaceMap2.insertPlace("daCracco", "44.5", "99.3");
        nearbyPlacesList.add(googlePlaceMap2);

        dummyGetNearbyPlaces = Mockito.spy(GetNearbyPlaces.class);

        //Mock
        Mockito.doNothing().when(dummyGetNearbyPlaces).animateCamera(Mockito.any(LatLngBounds.Builder.class));
        Mockito.doNothing().when(dummyGetNearbyPlaces).displayMarkers(Mockito.any(MarkerOptions.class));
        Mockito.doReturn(marker).when(dummyGetNearbyPlaces).createMarker(Mockito.any(LatLng.class), Mockito.anyString());
    }


    @Test
    public void downloadNearbyPlacesTest_fillTheMarkerList(){
        dummyGetNearbyPlaces.downloadNearbyPlaces(nearbyPlacesList);
        List<MarkerOptions> markerList = dummyGetNearbyPlaces.getMarkerList();
        Assert.assertEquals(markerList.size(), NUM_PLACES);
    }

    @Test
    public void downloadNearbyPlacesTest_noPlaces(){
        List<Place> emptyNearbyPlacesList = new ArrayList<>();
        dummyGetNearbyPlaces.downloadNearbyPlaces(emptyNearbyPlacesList);
        Assert.assertEquals(dummyGetNearbyPlaces.getMarkerList().size(), 0);
    }

}