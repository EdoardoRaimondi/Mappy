package com.example.app.interfaces;

import com.example.app.finals.NearbyRequestType;
import com.example.app.finals.SearchablePlace;

/**
 * Implement this to have correct types on a
 * url nearby google maps request
 */
public interface GoogleMapsRequestAdapter {

    /**
     *
     * @param place that has to be a {@link SearchablePlace}
     * @return corresponding google url type {@link NearbyRequestType}
     */
    NearbyRequestType getAdaptedPlace(SearchablePlace place);


}
