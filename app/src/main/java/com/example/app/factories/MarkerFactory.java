package com.example.app.factories;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public abstract class MarkerFactory {

    private static final String HOME    = "Home sweet home";

    /**
     *
     * Create a basic marker
     * @param latLng position
     * @param placeName name of the place
     * @return a marker option with selected param
     */
    public static MarkerOptions createBasicMarker(@NonNull LatLng latLng, @NonNull String placeName) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(placeName);
        markerOptions.position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        return markerOptions;
    }

    /**
     * Method to create an home marker
     * @param lat latitude
     * @param lng longitude
     * @return markerOption with selected param
     */
    public static MarkerOptions createHomeMarker(double lat, double lng){
        MarkerOptions marker = new MarkerOptions();
        marker.title(HOME);
        LatLng latLng = new LatLng(lat, lng);
        marker.position(latLng);
        return marker;
    }
}
