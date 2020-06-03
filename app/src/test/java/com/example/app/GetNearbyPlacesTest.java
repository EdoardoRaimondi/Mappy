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


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)

public class GetNearbyPlacesTest {

    @Spy
    GetNearbyPlaces dummyGetNearbyPlaces;


    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);

        dummyGetNearbyPlaces = Mockito.spy(GetNearbyPlaces.class);

        Mockito.doCallRealMethod().when(dummyGetNearbyPlaces).doInBackground(Mockito.anyString());

    }

    @Test
    public void createTransferData(){
        String[] expected = {"test"};
        String[] actual = dummyGetNearbyPlaces.createTransferData("test");
        Assert.assertEquals(expected, actual);
    }

    @Test (expected = IOException.class)
    public void execute(){
        dummyGetNearbyPlaces.execute("test");
    }



}