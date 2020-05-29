package com.example.app.listeners;

import com.google.android.libraries.places.api.model.Place;

import java.util.List;

/**
 * Query result listener interface
 */
public interface OnResultSetListener {

    /**
     * Callback when the result is loaded
     * @param nearbyPlaceList to get
     */
    void onResultSet(List<Place> nearbyPlaceList);
}
