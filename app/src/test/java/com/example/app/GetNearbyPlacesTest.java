package com.example.app;

import android.net.Uri;
import android.os.Parcel;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.AddressComponents;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlusCode;

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
import java.util.List;

@RunWith(MockitoJUnitRunner.class)

public class GetNearbyPlacesTest {

    private List<Place> nearbyPlacesList = new ArrayList<>();
    private Place googleLocalPlaceMap1;
    private Place googleLocalPlaceMap2;
    private Place googleLocalPlace_same;

    private static final int NUM_PLACES = 2;
    private static final LatLng LAT_LNG = new LatLng(1.1, 1.2);
    private static final String PLACE_NAME    = "Da Cracco";

    private static final LatLng LAT_LNG_2 = new LatLng(2.3, 4.9);
    private static final String PLACE_NAME_2 = "Da Gino";

    @Spy
    GetNearbyPlaces dummyGetNearbyPlaces;


    @Before
    public void setUp() throws JSONException {
        MarkerOptions marker = new MarkerOptions();
        MockitoAnnotations.initMocks(this);

        //need to store the data in a List<PLace>
        googleLocalPlaceMap1 = Place.builder()
                .setLatLng(LAT_LNG)
                .setName(PLACE_NAME)
                .build();
        nearbyPlacesList.add(googleLocalPlaceMap1);

        googleLocalPlaceMap2 = Place.builder()
                .setLatLng(LAT_LNG_2)
                .setName(PLACE_NAME_2)
                .build();
        nearbyPlacesList.add(googleLocalPlaceMap2);

        googleLocalPlace_same = Place.builder()
                .setLatLng(LAT_LNG)
                .setName(PLACE_NAME)
                .build();



        dummyGetNearbyPlaces = Mockito.spy(GetNearbyPlaces.class);

        //Mock
        Mockito.doNothing().when(dummyGetNearbyPlaces).animateCamera(Mockito.any(LatLngBounds.Builder.class));
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

    @Test
    public void downloadNearbyPlaceTest_sameCoordinate(){
        List<Place> sameNearbyPlaceList = new ArrayList<>();
        sameNearbyPlaceList.add(googleLocalPlaceMap1);
        sameNearbyPlaceList.add(googleLocalPlace_same);
        dummyGetNearbyPlaces.downloadNearbyPlaces(sameNearbyPlaceList);
        Assert.assertEquals(dummyGetNearbyPlaces.getMarkerList().size(), 2);
    }

}