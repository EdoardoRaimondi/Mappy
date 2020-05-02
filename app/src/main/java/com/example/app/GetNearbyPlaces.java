package com.example.app;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.example.app.finals.ResponseStatus;
import com.example.app.listeners.OnMarkersDownloadedListener;
import com.example.app.listeners.OnResultSetListener;
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
    private String result;

    private OnResultSetListener         onResultSetListener;
    private OnMarkersDownloadedListener onMarkersDownloadedListener;

    static List<MarkerOptions> markerList = new ArrayList<>(); //to save the state

    /**
     * Constructor in order to set null the listener
     */
    public GetNearbyPlaces(){

        onMarkersDownloadedListener = null;
        onResultSetListener         = null;
    }

    /**
     * Set the listener following the {@link OnResultSetListener} interface
     * @param listener to build
     */
    public void setOnResultSetListener(OnResultSetListener listener){
        onResultSetListener = listener;
    }

    /**
     * Set the listener following the {@link OnResultSetListener} interface
     * @param listener to build
     */
    public void setOnMarkersDownloadedListener(OnMarkersDownloadedListener listener){
        onMarkersDownloadedListener = listener;
    }

    /**
     * Method to extract the data from the {@link MapsActivity}
     * @param  objects A two dimension array containing the map and the url request from the {@link MainActivity}
     * @return string representing the data
     */
    @Override
    protected String doInBackground(Object... objects) {
        String url = (String) objects[0];

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
        List<Place> nearByPlacesList = null;
        DataParser parser = new DataParser();
        try {
            nearByPlacesList = parser.parse(s);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        catch(NullPointerException e){
            // Here because no/slow connection
            result = ResponseStatus.NO_CONNECTION;
            loadResult();
        }

        downloadNearbyPlaces(nearByPlacesList);
    }

    /**
     * Create places marker and trigger the listeners on {@link MapsActivity}
     * Package private for testing
     * @param nearByPlacesList The list of nearby places
     *
     */
    void downloadNearbyPlaces(List<Place> nearByPlacesList) {
        if(nearByPlacesList != null) {
            if (!nearByPlacesList.isEmpty()) {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (int i = 0; i < nearByPlacesList.size(); i++) {
                    //Extract the data
                    Place googleNearbyPlace = nearByPlacesList.get(i);
                    String placeName = googleNearbyPlace.getName();
                    double lat = googleNearbyPlace.getLatitude();
                    double lon = googleNearbyPlace.getLongitude();
                    LatLng latLng = new LatLng(lat, lon);
                    builder.include(latLng);

                    MarkerOptions markerOptions = createMarker(latLng, placeName);
                    //Add the marker in order to recreate the state
                    markerList.add(markerOptions);
                }

                //We have all the markers. Let's trigger the listener
                loadMarkers(builder);

            }
        }

        result = DataParser.STATUS;
        //We have the result status. Let's trigger the listener
        loadResult();
    }

    /**
     *
     * @param latLng     position
     * @param placeName  title
     * @return marker ready to be displayed
     */
    MarkerOptions createMarker(@NonNull LatLng latLng, @NonNull String placeName){
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
    protected void loadResult(){
        if(onResultSetListener != null) {
            onResultSetListener.onResultSet(result);
        }
    }

    /**
     * Method to trigger the listener and sens it the result data
     * @param builder to animate the camera
     */
    protected void loadMarkers(LatLngBounds.Builder builder){
        if(onMarkersDownloadedListener != null) {
            onMarkersDownloadedListener.onMarkersDownloaded(getMarkerList(), builder);
        }
    }

}

