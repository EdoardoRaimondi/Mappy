package com.example.app.finals;

import com.example.app.R;
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

    private static final String RESTAURANT = "RESTAURANT";
    private static final String MOVIE_THEATER = "MOVIE THEATER";
    private static final String ZOO = "ZOO";
    private static final String MUSEUM = "MUSEUM";
    private static final String ART_GALLERY = "ART GALLERY";
    private static final String PARK = "PARK";
    private static final String TOURIST_ATTRACTION = "LOCAL ATTRACTIONS";
    private static final String SUPERMARKET = "SUPERMARKET";
    private static final String PARKING = "PARKING";
    private static final String GAS_STATION = "GAS STATION";
    private static final String PHARMACY   = "PHARMACY";

    /**
     * @return a collection of all the searchable places item
     */
    public static Collection<ItemAdapter> getPlaceSearchList(){
        Collection<ItemAdapter> mPlaceList = new LinkedList<>();
        ItemAdapter itemR = new ItemAdapter();
        itemR.setText(RESTAURANT);
        itemR.setImage(R.drawable.restaurant);
        mPlaceList.add(itemR);
        ItemAdapter itemPh = new ItemAdapter();
        itemPh.setText(PHARMACY);
        itemPh.setImage(R.drawable.pharmacy);
        mPlaceList.add(itemPh);
        ItemAdapter itemPa = new ItemAdapter();
        itemPa.setText(PARKING);
        itemPa.setImage(R.drawable.parking);
        mPlaceList.add(itemPa);
        ItemAdapter itemG = new ItemAdapter();
        itemG.setText(GAS_STATION);
        itemG.setImage(R.drawable.gas_station);
        mPlaceList.add(itemG);
        ItemAdapter itemT = new ItemAdapter();
        itemT.setText(TOURIST_ATTRACTION);
        itemT.setImage(R.drawable.tourist_attraction);
        mPlaceList.add(itemT);
        ItemAdapter itemM = new ItemAdapter();
        itemM.setText(MUSEUM);
        itemM.setImage(R.drawable.museum);
        mPlaceList.add(itemM);
        ItemAdapter itemMo = new ItemAdapter();
        itemMo.setText(MOVIE_THEATER);
        itemMo.setImage(R.drawable.movie);
        mPlaceList.add(itemMo);
        ItemAdapter itemK = new ItemAdapter();
        itemK.setText(PARK);
        itemK.setImage(R.drawable.park);
        mPlaceList.add(itemK);
        ItemAdapter itemS = new ItemAdapter();
        itemS.setText(SUPERMARKET);
        itemS.setImage(R.drawable.supermarket);
        mPlaceList.add(itemS);
        ItemAdapter itemZ = new ItemAdapter();
        itemZ.setText(ZOO);
        itemZ.setImage(R.drawable.zoo);
        mPlaceList.add(itemZ);
        ItemAdapter itemA = new ItemAdapter();
        itemA.setText(ART_GALLERY);
        itemA.setImage(R.drawable.art_gallery);
        mPlaceList.add(itemA);
        return mPlaceList;
    }

    /**
     * A map with
     * key   = place user see on UI
     * value = place used by google search
     * @return that map
     */
    public static Map<String, NearbyRequestType> getPlacesAdapter(){
        Map<String, NearbyRequestType> map = new HashMap<>();
        map.put(RESTAURANT, NearbyRequestType.restaurant);
        map.put(PHARMACY, NearbyRequestType.pharmacy);
        map.put(PARKING, NearbyRequestType.parking);
        map.put(GAS_STATION, NearbyRequestType.gas_station);
        map.put(TOURIST_ATTRACTION, NearbyRequestType.tourist_attraction);
        map.put(MUSEUM, NearbyRequestType.museum);
        map.put(MOVIE_THEATER, NearbyRequestType.movie_theater);
        map.put(PARK, NearbyRequestType.park);
        map.put(SUPERMARKET, NearbyRequestType.supermarket);
        map.put(ZOO, NearbyRequestType.zoo);
        map.put(ART_GALLERY, NearbyRequestType.art_gallery);
        return map;
    }
}
