package com.example.app.handlers;

import com.example.app.DataParser;
import com.example.app.GetNearbyPlaces;
import com.example.app.factories.MarkerFactory;
import com.example.app.iterators.StoppablePlaceIterator;
import com.example.app.ui_tools.CustomInfoWindow;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.Place;

/**
 * Chain of responsibility for Maps Activity.
 */
public class MapsActivityHandler {

    /**
     * Display nearby places
     * @param nearbyPlaceListIterator list of places to display
     */
    public static String displayPlaces(StoppablePlaceIterator nearbyPlaceListIterator, GoogleMap map){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        if(nearbyPlaceListIterator.hasNext()) {
            while (nearbyPlaceListIterator.hasNext()) {
                //Extract the data
                Place googleNearbyLocalPlace = nearbyPlaceListIterator.next();
                String placeName = googleNearbyLocalPlace.getName();
                LatLng latLng = googleNearbyLocalPlace.getLatLng();
                builder.include(latLng);

                MarkerOptions markerOptions = MarkerFactory.createBasicMarker(latLng, placeName);
                GetNearbyPlaces.markerList.add(markerOptions);
                map.addMarker(markerOptions);
            }
            animateCamera(builder, map);
        }
        return DataParser.STATUS;
    }

    /**
     * Method to animate the camera
     */
    static void animateCamera(LatLngBounds.Builder builder, GoogleMap map){
        LatLngBounds bounds = builder.build();
        int padding = 0; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        map.animateCamera(cu);
    }
}
