package com.example.app;

import android.util.JsonReader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * CLass to parse the google place data
 */
public class DataParser implements MapJSonDataParser{

    public static String STATUS = "";

    /**
     * Convert a single json object of the position to an Hash Map
     * @param googlePlaceJSON the place in json format
     * @return the hash map representing the place
     * @throws JSONException if something in the json read goes wrong
     *
     */
    private HashMap<String, String> getSingleNearbyPlace(JSONObject googlePlaceJSON)
    {
        HashMap<String, String> googlePlaceMap = new HashMap<>();
        String NameOfPlace = "-NA-";
        String vicinity = "-NA-";
        String latitude = "";
        String longitude = "";
        String reference = "";

        try
        {
            if (!googlePlaceJSON.isNull("name"))
            {
                NameOfPlace = googlePlaceJSON.getString("name");
            }
            if (!googlePlaceJSON.isNull("vicinity"))
            {
                vicinity = googlePlaceJSON.getString("vicinity");
            }
            latitude = googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lng");
            reference = googlePlaceJSON.getString("reference");


            googlePlaceMap.put("place_name", NameOfPlace);
            googlePlaceMap.put("vicinity", vicinity);
            googlePlaceMap.put("lat", latitude);
            googlePlaceMap.put("lng", longitude);
            googlePlaceMap.put("reference", reference);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return googlePlaceMap;
    }


    /**
     * Create a list of place (in hash map format)
     * @param jsonArray array containing the nearby places
     * @throws JSONException if json reading goes wrong
     */
    private List<HashMap<String, String>> getAllNearbyPlaces(JSONArray jsonArray)
    {
        int count = jsonArray.length();
        List<HashMap<String, String>> NearbyPlacesList = new ArrayList<>();

        HashMap<String, String> NearbyPlaceMap = null;

        for (int i=0; i<count; i++)
        {
            try
            {
                NearbyPlaceMap = getSingleNearbyPlace( (JSONObject) jsonArray.get(i) );
                NearbyPlacesList.add(NearbyPlaceMap);

            }
            catch (JSONException e)
            {
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
    public List<HashMap<String, String>> parse(String JsonData) throws JSONException {
        JSONArray jsonArray = null;
        JSONObject jsonObject = new JSONObject(JsonData);

        jsonArray = jsonObject.getJSONArray("results");
        if(jsonArray.length()==0){
            //Something goes wrong, check the status
            //The status can assume one of the {@link ResponseStatus.class}
            STATUS = jsonObject.getString("status");
        }

        Log.d("ARRAY_LENGTH", String.valueOf(jsonArray.length()));

        return getAllNearbyPlaces(jsonArray);
    }

}
