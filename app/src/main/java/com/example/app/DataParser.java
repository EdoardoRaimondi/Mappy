package com.example.app;

import com.example.app.interfaces.MapJSonDataParser;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * CLass to parse the google place data
 */
public class DataParser implements MapJSonDataParser {

    public static String STATUS = "";

    /**
     * Convert a single json object of the position to an Hash Map
     * @param googlePlaceJSON the place in json format
     * @return the hash map representing the place
     * @throws JSONException if something in the json read goes wrong
     *
     */
    private Place getSingleNearbyPlace(JSONObject googlePlaceJSON) {
        Place place = null;
        String placeName = "-NA-";
        String id = "-NA-";
        double latitude;
        double longitude;
        int priceLevel;

        try {
            placeName = googlePlaceJSON.getString("name");
            id = googlePlaceJSON.getString("place_id");
            latitude = Double.parseDouble(googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lat"));
            longitude = Double.parseDouble(googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lng"));
            //reference = googlePlaceJSON.getString("reference");
            if(googlePlaceJSON.has("price")) {
                priceLevel = googlePlaceJSON.getInt("price");
                place = Place.builder()
                        .setLatLng(new LatLng(latitude, longitude))
                        .setName(placeName)
                        .setPriceLevel(priceLevel)
                        .setId(id)
                        .build();
            }
            else {
                place = Place.builder()
                        .setLatLng(new LatLng(latitude, longitude))
                        .setName(placeName)
                        .setId(id)
                        .build();
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return place;
    }


    /**
     * Create a list of place (in hash map format)
     * @param jsonArray array containing the nearby places
     * @throws JSONException if json reading goes wrong
     */
    private List<Place> getAllNearbyPlaces(JSONArray jsonArray) {
        int count = 0;
        // changed code here because if there is no connection null pointer exc is thrown
        if(jsonArray != null) {
            count = jsonArray.length();
        }
        List<Place> nearbyPlacesList = new ArrayList<>();

        Place nearbyLocalPlaceMap;

        for (int i=0; i<count; i++) {
            try {
                nearbyLocalPlaceMap = getSingleNearbyPlace( (JSONObject) jsonArray.get(i) );
                nearbyPlacesList.add(nearbyLocalPlaceMap);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return nearbyPlacesList;
    }



    /**
     * Method to parse the JsonData
     * @param JsonData to parse. Has to be compatible with JSON
     * @return List of nearby places (in HashMap format)
     * @throws JSONException if Json computation goes wrong
     */
    public List<Place> parse(String JsonData) throws JSONException {
        JSONObject jsonObject = new JSONObject(JsonData);
        JSONArray jsonArray = jsonObject.getJSONArray("results");
        //The status can assume one of the {@link ResponseStatus.class}
        STATUS = jsonObject.getString("status");
        return getAllNearbyPlaces(jsonArray);
    }

}
