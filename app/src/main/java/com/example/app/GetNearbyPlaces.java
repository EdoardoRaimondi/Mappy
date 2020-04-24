package com.example.app;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to get the nearby places
 */
public class GetNearbyPlaces extends AsyncTask<Object, String, String> {

    private String googlePlaceData, url;
    private GoogleMap mMap;

    /**
     * Method to extract the data from the {@link MapsActivity}
     * @param objects
     * @return string representing the data
     */
    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        url = (String) objects[1];

        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googlePlaceData = downloadUrl.readTheUrl(url);
        } catch (IOException e) {
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
        } catch (JSONException e) {
            e.printStackTrace();
        }

        displayNearbyPlaces(nearByPlacesList);
    }

    /**
     * Method to show the nearby places
     * @param nearByPlacesList The list of nearby places
     */
    private void displayNearbyPlaces(List<HashMap<String, String>> nearByPlacesList) {
        if (!nearByPlacesList.isEmpty()) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (int i = 0; i < nearByPlacesList.size(); i++) {
                MarkerOptions markerOptions = new MarkerOptions();
                //extract the data
                HashMap<String, String> googleNearbyPlace = nearByPlacesList.get(i);
                String placeName = googleNearbyPlace.get("place_name");
                String vicinity = googleNearbyPlace.get("vicinity");
                try {
                    double lat = Double.parseDouble(googleNearbyPlace.get("lat"));
                    double lon = Double.parseDouble(googleNearbyPlace.get("lng"));
                    LatLng latLng = new LatLng(lat, lon);
                    builder.include(latLng);


                    //Once get the data, I position the markers of this specific place
                    markerOptions.position(latLng);
                    markerOptions.title(placeName + " : " + vicinity);
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));

                    //create the marker
                    mMap.addMarker(markerOptions);
/**
 //synchronize the visual map
 //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
 mMap.animateCamera(CameraUpdateFactory.zoomBy(14));
 */
                }
                // If I am here, it means I did not have the position for that searching
                catch (NullPointerException e) {
                    //TODO : Show an "error" message
                }
            }
            LatLngBounds bounds = builder.build();
            int padding = 0; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

            mMap.animateCamera(cu);
        }
        else {
            //No type places found
           Toast.makeText(MapsActivity.getContext(), "NO PLACE NEAR YOU OR TRY LATER", Toast.LENGTH_LONG).show();
        }
    }
}
