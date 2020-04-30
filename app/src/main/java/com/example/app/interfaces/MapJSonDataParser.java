package com.example.app.interfaces;

import org.json.JSONException;

import java.util.HashMap;
import java.util.List;

/**
 * To implement data parser for json google maps requests
 */
public interface MapJSonDataParser {

    /**
     * Method to parse the Json string data
     * @param JSonData in string form
     * @return List<HashMap<String, String>> where every Hash Map in the list is a place with
     * its information (name, latitude, longitude ...)
     * @throws JSONException if Json computation goes wrong
     */
    List<HashMap<String, String>> parse(String JSonData) throws JSONException;


}
