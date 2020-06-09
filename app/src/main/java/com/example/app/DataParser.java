package com.example.app;

import com.example.app.interfaces.MapJSonDataParser;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * CLass to parse the Google Place data
 */
public class DataParser implements MapJSonDataParser {

    // Status of parser
    static String STATUS = "";

    /**
     * Convert a single json object of the position to an Hash Map
     * @param googlePlaceJSON JSONObject of Place(s) in json format
     * @return The HashMap representing found Place(s)
     * @throws JSONException if something in the json read goes wrong TODO: can remove ?
     *
     */
    private Place getSingleNearbyPlace(JSONObject googlePlaceJSON) {
        Place place = null;
        String placeName;
        String id;
        double latitude;
        double longitude;

        try {
            placeName = googlePlaceJSON.getString("name");
            id = googlePlaceJSON.getString("place_id");
            latitude = Double.parseDouble(googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lat"));
            longitude = Double.parseDouble(googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lng"));
            // reference = googlePlaceJSON.getString("reference") TODO: Edo ?
            place = Place.builder()
                    .setLatLng(new LatLng(latitude, longitude))
                    .setName(placeName)
                    .setId(id)
                    .build();
        }
        catch (JSONException e) {
            e.printStackTrace();
            // TODO: STATUS = ResponseStatus.UNKNOWN_ERROR ?
        }

        return place;
    }


    /**
     * Create a list of place (in hash map format)
     * @param jsonArray array containing the nearby places
     * @throws JSONException if json reading goes wrong TODO: can remove ?
     */
    private List<Place> getAllNearbyPlaces(JSONArray jsonArray) {
        int count = 0;
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
                // TODO: STATUS = ResponseStatus.UNKNOWN_ERROR ?
            }
        }

        return nearbyPlacesList;
    }



    /**
     * Method to parse the JsonData
     * @param JsonData String rapresenting JSONObject data to parse. Has to be compatible with JSON
     * @return List of nearby places (in HashMap format)
     * @throws JSONException If Json computation goes wrong
     */
    public List<Place> parse(String JsonData) throws JSONException {
        JSONObject jsonObject = new JSONObject(JsonData);
        JSONArray jsonArray = jsonObject.getJSONArray("results");
        /** The status can assume one of the {@link com.example.app.finals.ResponseStatus} */
        STATUS = jsonObject.getString("status");
        return getAllNearbyPlaces(jsonArray);
    }

}
