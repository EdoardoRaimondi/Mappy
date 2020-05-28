package com.example.app.ui.search;

import com.example.app.finals.NearbyRequestType;
import com.example.app.finals.SearchablePlace;
import com.example.app.interfaces.GoogleMapsRequestAdapter;
import java.util.Map;

public class RequestAdapter implements GoogleMapsRequestAdapter {

    Map<SearchablePlace, NearbyRequestType> placeAdapter;

    public RequestAdapter(){
        placeAdapter = SearchPlacesData.getPlacesAdapter();
    }

    /**
     * @param place that has to be a {@link SearchablePlace}
     * @return corresponding {@link NearbyRequestType}
     */
    public NearbyRequestType getAdaptedPlace(SearchablePlace place){
        return placeAdapter.get(place);
    }
}
