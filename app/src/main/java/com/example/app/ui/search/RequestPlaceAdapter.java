package com.example.app.ui.search;

import com.example.app.finals.NearbyRequestType;
import com.example.app.finals.SearchPlaces;

import java.util.HashMap;

/**
 * Adapt a {@link SearchPlaces} item in a valid {@link NearbyRequestType}
 * There is a difference between the string that user see on the UI and
 * the same place string used by google. In order to automate the searching process,
 * it is needed to delete that difference with an adapter
 */
public class RequestPlaceAdapter {

    private static HashMap<String, NearbyRequestType> adapter = new HashMap<>();

    /**
     * Adapter constructor.
     * Create a key (place string user see), value (place string google see)
     * {@see SearchPlaces} for more details
     */
    public RequestPlaceAdapter(){
        adapter.putAll(SearchPlaces.getPlacesAdapter());
    }

    /**
     * Get the adapted form of place string
     * @param place a string from {@link SearchPlaces}
     * @return NearbyRequestType ready to be used in a url request
     * @throws NullPointerException if place isn't in {@link SearchPlaces}
     */
    public static NearbyRequestType getAdaptedPlace(String place){
        return adapter.get(place);
    }

}
