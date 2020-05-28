package com.example.app.ui.search;

import com.example.app.R;
import com.example.app.finals.NearbyRequestType;
import com.example.app.finals.SearchablePlace;
import com.example.app.ui.search.ItemAdapter;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * There is a difference between the string that user see on the UI and
 * the same place string used by google. In order to automate the searching process,
 * it is needed to delete that difference with an adapter
 */
public class SearchPlaces {

    /**
     * @return a collection of all the searchable places item
     */
    public static Collection<ItemAdapter> getPlaceSearchList(){
        Collection<ItemAdapter> mPlaceList = new LinkedList<>();
        mPlaceList.add(buildAdapter(SearchablePlace.RESTAURANT, R.drawable.restaurant));
        mPlaceList.add(buildAdapter(SearchablePlace.PHARMACY, R.drawable.pharmacy));
        mPlaceList.add(buildAdapter(SearchablePlace.PARKING, R.drawable.parking));
        mPlaceList.add(buildAdapter(SearchablePlace.GAS_STATION,R.drawable.gas_station));
        mPlaceList.add(buildAdapter(SearchablePlace.TOURIST_ATTRACTION,R.drawable.tourist_attraction));
        mPlaceList.add(buildAdapter(SearchablePlace.MUSEUM, R.drawable.museum));
        mPlaceList.add(buildAdapter(SearchablePlace.MOVIE_THEATER,R.drawable.movie));
        mPlaceList.add(buildAdapter(SearchablePlace.PARK, R.drawable.park));
        mPlaceList.add(buildAdapter(SearchablePlace.SUPERMARKET, R.drawable.supermarket));
        mPlaceList.add(buildAdapter(SearchablePlace.ZOO, R.drawable.zoo));
        mPlaceList.add(buildAdapter(SearchablePlace.ART_GALLERY, R.drawable.art_gallery));
        return mPlaceList;
    }

    /**
     * A map with
     * key   = place user see on UI
     * value = place used by google search
     * @return that map
     */
    public static Map<SearchablePlace, NearbyRequestType> getPlacesAdapter(){
        Map<SearchablePlace, NearbyRequestType> map = new HashMap<>();
        map.put(SearchablePlace.RESTAURANT, NearbyRequestType.restaurant);
        map.put(SearchablePlace.PHARMACY, NearbyRequestType.pharmacy);
        map.put(SearchablePlace.PARKING, NearbyRequestType.parking);
        map.put(SearchablePlace.GAS_STATION, NearbyRequestType.gas_station);
        map.put(SearchablePlace.TOURIST_ATTRACTION, NearbyRequestType.tourist_attraction);
        map.put(SearchablePlace.MUSEUM, NearbyRequestType.museum);
        map.put(SearchablePlace.MOVIE_THEATER, NearbyRequestType.movie_theater);
        map.put(SearchablePlace.PARK, NearbyRequestType.park);
        map.put(SearchablePlace.SUPERMARKET, NearbyRequestType.supermarket);
        map.put(SearchablePlace.ZOO, NearbyRequestType.zoo);
        map.put(SearchablePlace.ART_GALLERY, NearbyRequestType.art_gallery);
        return map;
    }

    /**
     * Build an itemAdapter for our recycler view
     * @return item build
     */
    private static ItemAdapter buildAdapter(SearchablePlace place, int imageResource){
        ItemAdapter item = new ItemAdapter();
        item.setImage(imageResource);
        item.setText(place.toString());
        return item;
    }
}
