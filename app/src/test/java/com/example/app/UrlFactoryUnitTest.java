package com.example.app;

import com.example.app.factories.UrlFactory;
import com.example.app.finals.NearbyRequestType;

import org.junit.Assert;
import org.junit.Test;

public class UrlFactoryUnitTest {

    private static final double LAT = 1.1;
    private static final double LNG = 2.2;
    private static final String TYPE = NearbyRequestType.police.toString();
    private static final int RADIUS = 1000;
    private static final int RADIUS_NEGATIVE = -1000;

    @Test
    public void getNearbyRequest_setUp(){
        String expected = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=1.1,2.2&radius=1000&type=police&sensor=true&key=AIzaSyCIN8HCmGWXf5lzta5Rv2nu8VdIUV4Jp7s";
        String actual = UrlFactory.getNearbyRequest(LAT, LNG, TYPE, RADIUS);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void getNearbyRequest_NegativeRadius(){
        String expected = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=1.1,2.2&radius=-1000&type=police&sensor=true&key=AIzaSyCIN8HCmGWXf5lzta5Rv2nu8VdIUV4Jp7s";
        String actual = UrlFactory.getNearbyRequest(LAT, LNG, TYPE, RADIUS_NEGATIVE);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void getNearbyRequest_ZeroRadius(){
        String expected = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=1.1,2.2&radius=0&type=police&sensor=true&key=AIzaSyCIN8HCmGWXf5lzta5Rv2nu8VdIUV4Jp7s";
        String actual = UrlFactory.getNearbyRequest(LAT, LNG, TYPE, 0);
        Assert.assertEquals(expected, actual);
    }
    
}
