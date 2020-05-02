package com.example.app;

import com.example.app.interfaces.MapJSonDataParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
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
        Place googlePlaceMap = new Place();
        String placeName = "-NA-";
        String vicinity = "-NA-";
        String latitude = "";
        String longitude = "";
        String reference = "";

        try {
            placeName = googlePlaceJSON.getString("name");
            latitude = googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lng");
            //reference = googlePlaceJSON.getString("reference");

            googlePlaceMap.insertPlace(placeName, latitude, longitude);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return googlePlaceMap;
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
        List<Place> NearbyPlacesList = new ArrayList<>();

        Place NearbyPlaceMap;

        for (int i=0; i<count; i++) {
            try {
                NearbyPlaceMap = getSingleNearbyPlace( (JSONObject) jsonArray.get(i) );
                NearbyPlacesList.add(NearbyPlaceMap);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return NearbyPlacesList;
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
