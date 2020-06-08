package com.example.app.factories;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public abstract class MarkerFactory {

    /**
     *
     * Create a basic marker
     * @param latLng    LatLng as position
     * @param placeName The name of the place
     * @return Marker option with selected param
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
     * @param title The home title responsive to language
     * @param lat   The latitude
     * @param lng   The longitude
     * @return The MarkerOption with selected param
     */
    public static MarkerOptions createHomeMarker(String title, double lat, double lng){
        MarkerOptions marker = new MarkerOptions();
        marker.title(title);
        LatLng latLng = new LatLng(lat, lng);
        marker.position(latLng);
        return marker;
    }
}
