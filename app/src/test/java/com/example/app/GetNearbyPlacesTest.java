package com.example.app;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)

public class GetNearbyPlacesTest {

    @Spy
    GetNearbyPlaces dummyGetNearbyPlaces;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);

        dummyGetNearbyPlaces = Mockito.spy(GetNearbyPlaces.class);
        Mockito.doCallRealMethod().when(dummyGetNearbyPlaces).doInBackground(Mockito.anyString());
        Mockito.doNothing().when(dummyGetNearbyPlaces).resultNotSet(Mockito.anyString());
    }

    @Test
    public void createTransferData(){
        String[] expected = {"test"};
        String[] actual = dummyGetNearbyPlaces.createTransferData("test");
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void doInBackground_NotValidUrl(){
        Assert.assertNull(dummyGetNearbyPlaces.doInBackground("test"));
    }

    @Test
    public void onPostExecute_NullParameter(){
        dummyGetNearbyPlaces.onPostExecute(null);
        Mockito.verify(dummyGetNearbyPlaces).resultNotSet(Mockito.anyString());
    }



}