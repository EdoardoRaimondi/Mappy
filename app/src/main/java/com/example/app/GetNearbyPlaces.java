package com.example.app;

import android.os.AsyncTask;

import com.example.app.finals.ResponseStatus;
import com.example.app.iterators.StoppablePlaceIterator;
import com.example.app.listeners.ResultSetListener;
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
public class GetNearbyPlaces extends AsyncTask<String, String, String>{

    private String googlePlaceData, url;

    private ResultSetListener resultSetListener;

    public static List<MarkerOptions> markerList = new ArrayList<>();

    /**
     * Constructor in order to set null the listener
     */
    public GetNearbyPlaces(){
        resultSetListener = null;
    }

    /**
     * Set the listener following the {@link ResultSetListener} interface
     * @param listener to build
     */
    public void setResultSetListener(ResultSetListener listener){
        resultSetListener = listener;
    }

    /**
     * @return a string[] containing valid data for this task
     */
    public String[] createTransferData(String string){
        String[] transferData = new String[1];
        transferData[0] = string;
        return transferData;
    }

    /**
     * Download the url response on a work separated thread
     * @param  strings url request from the {@link MainActivity}
     * @return string representing the data
     */
    @Override
    protected String doInBackground(String... strings) {
        url = strings[0];

        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googlePlaceData = downloadUrl.readTheUrl(url);
        } catch(UnknownHostException e){
            DataParser.STATUS = ResponseStatus.CONNECTION_LOW;
        } catch (IOException e) {
            DataParser.STATUS = ResponseStatus.UNKNOWN_ERROR;
        }
        return googlePlaceData;
    }

    /**
     * Callback to show the nearby places
     * @param s string representing the data {@see DataParser for more details}
     */
    @Override
    protected void onPostExecute(String s) {
        List<Place> nearByPlacesList;
        if(s==null) nearByPlacesList = null;
        else {
            DataParser parser = new DataParser();
            try {
                nearByPlacesList = parser.parse(s);
            } catch (JSONException e) {
                e.printStackTrace();
                nearByPlacesList = null;
            } catch (NullPointerException e) {
                // Here because no/slow connection
                DataParser.STATUS = ResponseStatus.NO_CONNECTION;
                nearByPlacesList = null;
            }
        }
        if(nearByPlacesList != null) {
            StoppablePlaceIterator iterator = new StoppablePlaceIterator(nearByPlacesList);
            loadResult(iterator);
        }
        else resultNotSet(DataParser.STATUS);
    }


    /**
     * Method that trigger the listener and send
     * it the result data
     */
    protected void loadResult(StoppablePlaceIterator nearbyPlaces){
        if(resultSetListener != null) {
            resultSetListener.onResultSet(nearbyPlaces);
        }
    }

    /**
     * Method that trigger the listener and send
     * it the
     * @param error done
     */
    protected void resultNotSet(String error){
        if(resultSetListener != null){
            resultSetListener.onResultNotSet(error);
        }
    }

}

