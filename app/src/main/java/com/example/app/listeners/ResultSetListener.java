package com.example.app.listeners;

import com.example.app.finals.ResponseStatus;
import com.example.app.iterators.StoppablePlaceIterator;

/**
 * Query result listener interface
 */
public interface ResultSetListener {

    /**
     * Callback when the result is loaded
     * @param nearbyPlaceListIterator to the place list
     */
    void onResultSet(StoppablePlaceIterator nearbyPlaceListIterator);

    /**
     * Callback when unable to get the result
     * @param error called
     */
    void onResultNotSet(String error);
}
