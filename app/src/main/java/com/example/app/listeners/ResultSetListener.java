package com.example.app.listeners;

import com.example.app.iterators.StoppablePlaceIterator;

/**
 * Query result listener interface
 */
public interface ResultSetListener {

    /**
     * Callback when the result is loaded
     * @param nearbyPlaceListIterator Iterator to the places' list
     */
    void onResultSet(StoppablePlaceIterator nearbyPlaceListIterator);

    /**
     * Callback when unable to get the result
     * @param error The error String if called
     */
    void onResultNotSet(String error);
}
