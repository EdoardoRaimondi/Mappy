package com.example.app.ui.search;

import com.example.app.R;
import com.example.app.finals.NearbyRequestType;
import com.example.app.finals.SearchablePlace;
import com.example.app.interfaces.ImageViewItem;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Provide recycler view data structures
 */
public class SearchPlacesData {

    /**
     * @return a collection of all the search item with corresponding icon
     */
    public static Collection<SearchItem> getPlaceSearchList(){
        Collection<SearchItem> mPlaceList = new LinkedList<>();
        mPlaceList.add(buildImageViewItem(SearchablePlace.RESTAURANT, R.drawable.restaurant));
        mPlaceList.add(buildImageViewItem(SearchablePlace.PHARMACY, R.drawable.pharmacy));
        mPlaceList.add(buildImageViewItem(SearchablePlace.PARKING, R.drawable.parking));
        mPlaceList.add(buildImageViewItem(SearchablePlace.GAS_STATION,R.drawable.gas_station));
        mPlaceList.add(buildImageViewItem(SearchablePlace.TOURIST_ATTRACTION,R.drawable.tourist_attraction));
        mPlaceList.add(buildImageViewItem(SearchablePlace.MUSEUM, R.drawable.museum));
        mPlaceList.add(buildImageViewItem(SearchablePlace.MOVIE_THEATER,R.drawable.movie));
        mPlaceList.add(buildImageViewItem(SearchablePlace.PARK, R.drawable.park));
        mPlaceList.add(buildImageViewItem(SearchablePlace.SUPERMARKET, R.drawable.supermarket));
        mPlaceList.add(buildImageViewItem(SearchablePlace.ZOO, R.drawable.zoo));
        mPlaceList.add(buildImageViewItem(SearchablePlace.ART_GALLERY, R.drawable.art_gallery));
        return mPlaceList;
    }

    /**
     * Create the correspond between {@link SearchablePlace} and {@link NearbyRequestType}
     * A map with
     * key   = place user see on UI {@link SearchablePlace}
     * value = place used by google search {@link NearbyRequestType}
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
        map.put(SearchablePlace.NIGHT_CLUB, NearbyRequestType.night_club);
        return map;
    }

    /**
     * Build an itemAdapter for our recycler view
     * @return item build
     */
    private static <T extends ImageViewItem> T buildImageViewItem(SearchablePlace type, int imageResource){
        SearchItem item = new SearchItem();
        item.setImage(imageResource);
        item.setType(type);
        return (T) item;
    }
}
