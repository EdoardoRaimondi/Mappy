package com.example.app.listeners;

import com.example.app.iterators.StoppablePlaceIterator;

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
