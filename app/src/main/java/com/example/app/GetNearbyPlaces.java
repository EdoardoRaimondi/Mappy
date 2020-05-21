package com.example.app;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.example.app.factories.MarkerFactory;
import com.example.app.finals.MapsUtility;
import com.example.app.finals.ResponseStatus;
import com.example.app.listeners.OnResultSetListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.Place;

import org.json.JSONException;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


/**
 * Class to get the nearby places
 */
public class GetNearbyPlaces extends AsyncTask<Object, String, String>{

    private String googlePlaceData, url;
    private String result;
    private GoogleMap mMap;

    private OnResultSetListener         onResultSetListener;

    static List<MarkerOptions> markerList = new ArrayList<>(); //to save the state

    /**
     * Constructor in order to set null the listener
     */
    public GetNearbyPlaces(){
        onResultSetListener = null;
        markerList = new ArrayList<>();
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
        url = (String) objects[1];

        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googlePlaceData = downloadUrl.readTheUrl(url);
        } catch(UnknownHostException e){
            result = ResponseStatus.CONNECTION_LOW;
            loadResult();
        } catch (IOException e) {
            result = ResponseStatus.CONNECTION_LOW;
            loadResult();
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
                    Place googleNearbyLocalPlace = nearByPlacesList.get(i);
                    String placeName = googleNearbyLocalPlace.getName();
                    LatLng latLng = googleNearbyLocalPlace.getLatLng();
                    builder.include(latLng);

                    MarkerOptions markerOptions = MarkerFactory.createBasicMarker(latLng, placeName);
                    //Add the marker in order to recreate the state
                    markerList.add(markerOptions);

                    mMap.addMarker(markerOptions);

                }
                animateCamera(builder);
            }
        }

        result = DataParser.STATUS;
        //We have the result status. Let's trigger the listener
        loadResult();
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
     * Method to animate the camera
     */
    void animateCamera(LatLngBounds.Builder builder){
        LatLngBounds bounds = builder.build();
        int padding = 0; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        mMap.animateCamera(cu);
    }

}

