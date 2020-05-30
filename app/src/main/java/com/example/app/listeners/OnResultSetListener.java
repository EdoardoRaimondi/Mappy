package com.example.app.listeners;

import com.example.app.StoppablePlaceIterator;
import com.google.android.libraries.places.api.model.Place;

import java.util.ListIterator;

/**
 * Query result listener interface
 */
public interface OnResultSetListener {

    /**
     * Callback when the result is loaded
     * @param nearbyPlaceListIterator to the place list
     */
    void onResultSet(StoppablePlaceIterator nearbyPlaceListIterator);
}
