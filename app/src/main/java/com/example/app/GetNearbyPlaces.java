package com.example.app;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


/**
 * Class to get the nearby places
 */
public class GetNearbyPlaces extends AsyncTask<Object, String, String>{

    private String googlePlaceData;
    private GoogleMap mMap;
    private String result;
    private OnResultSetListener onResultSetListener;

    static List<MarkerOptions> markerList = new ArrayList<>(); //to save the state

    /**
     * Constructor in order to set null the listener
     */
    public GetNearbyPlaces(){
        onResultSetListener = null;
    }

    /**
     * Set the listener following the {@link OnResultSetListener} interface
     * @param listener to build
     */
    public void setOnResultSetListener(OnResultSetListener listener){
        onResultSetListener = listener;
    }

    /**
     * Method to extract the data from the {@link MapsActivity}
     * @param  objects A two dimension array containing the map and the url request from the {@link MainActivity}
     * @return string representing the data
     */
    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        String url = (String) objects[1];

        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googlePlaceData = downloadUrl.readTheUrl(url);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return googlePlaceData;
    }

    /**
     * Callback to show the nearby places
     * @param s string representing the data {@see DataParser for more details}
     */
    @Override
    protected void onPostExecute(String s) {
        List<HashMap<String, String>> nearByPlacesList = null;
        DataParser parser = new DataParser();
        try {
            nearByPlacesList = parser.parse(s);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        catch(NullPointerException e){
            // here because no connection
            Log.d("GetNearbyPlaces","You are seeing this message because there is no connection");
        }

        downloadNearbyPlaces(nearByPlacesList);
    }

    /**
     * It takes the information of the nearby places from the URL request.
     * Other methods encapsulate them in markers and display them
     * Package private for testing
     * @param nearByPlacesList The list of nearby places
     *
     */
    void downloadNearbyPlaces(List<HashMap<String, String>> nearByPlacesList) {
        if(nearByPlacesList != null) {
            if (!nearByPlacesList.isEmpty()) {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (int i = 0; i < nearByPlacesList.size(); i++) {
                    //Extract the data
                    HashMap<String, String> googleNearbyPlace = nearByPlacesList.get(i);
                    String placeName = googleNearbyPlace.get("place_name");
                    double lat = Double.parseDouble(Objects.requireNonNull(googleNearbyPlace.get("lat")));
                    double lon = Double.parseDouble(Objects.requireNonNull(googleNearbyPlace.get("lng")));
                    LatLng latLng = new LatLng(lat, lon);
                    builder.include(latLng);

                    MarkerOptions markerOptions = createMarker(latLng, placeName);

                    displayMarkers(markerOptions);
                }

                animateCamera(builder);

            }
        }

        result = DataParser.STATUS;
        //We have the result status. Let's trigger the listener
        loadResult();
    }

    /**
     * Method to display nearby a single marker on the map and add it on
     * the restore marker list
     * @param marker to add
     */
    void displayMarkers(@NonNull MarkerOptions marker){
        //Create the marker
        mMap.addMarker(marker);
        //Add the marker in order to recreate the state
        markerList.add(marker);
    }

    /**
     * Method to animate the camera
     */
    void animateCamera(@NonNull LatLngBounds.Builder builder){
        LatLngBounds bounds = builder.build();
        int padding = 0; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        mMap.animateCamera(cu);
    }

    /**
     *
     * @param latLng     position
     * @param placeName  title
     * @return marker ready to be displayed
     */
    protected MarkerOptions createMarker(@NonNull LatLng latLng,@NonNull String placeName){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(placeName);
        markerOptions.position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        return markerOptions;
    }

    /**
     * @return current markerList
     */
    List<MarkerOptions> getMarkerList(){
        return markerList;
    }

    /**
     * Method that trigger the listener and send
     * it the result data
     */
    private void loadResult(){
        if(onResultSetListener != null) {
            onResultSetListener.onResultSet(result);
        }
    }

}
}
