package com.example.app.handlers;

import android.content.Context;
import android.location.Location;

import com.example.app.GetNearbyPlaces;
import com.example.app.HelpActivity;
import com.example.app.factories.UrlFactory;
import com.example.app.finals.NearbyRequestType;
import com.example.app.iterators.StoppablePlaceIterator;
import com.example.app.listeners.LocationSetListener;
import com.example.app.listeners.PhoneNumberGetListener;
import com.example.app.listeners.ResultSetListener;
import com.example.app.sensors.GoogleLocationFinder;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Collections;
import java.util.List;

/**
 * Chain of responsibility for {@link HelpActivity}
 */
public class HelpActivityHandler {

    private PhoneNumberGetListener phoneNumberGetListener;

    public HelpActivityHandler(){ }

    public void setPhoneNumberGetListener(PhoneNumberGetListener phoneNumberGetListener){
        this.phoneNumberGetListener = phoneNumberGetListener;
    }

    /**
     * Trigger the {@link PhoneNumberGetListener} when the
     * nearest phone number is found
     * @param type of phone number you need (need to be a {@link NearbyRequestType})
     */
     public void getPhoneNumber(NearbyRequestType type, Context context){
        GoogleLocationFinder googleLocationFinder = new GoogleLocationFinder();
        googleLocationFinder.setLocationSetListener(new LocationSetListener() {
            @Override
            public void onLocationSet(Location location) {
                String url = UrlFactory.getNearbyRequest(location.getLatitude(), location.getLongitude(), type.toString(), 5000);
                //the request will be downloaded and displayed
                GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();
                getNearbyPlaces.execute(getNearbyPlaces.createTransferData(url));
                getNearbyPlaces.setResultSetListener(new ResultSetListener() {
                    @Override
                    public void onResultSet(StoppablePlaceIterator nearbyPlaceListIterator) {
                        if(!nearbyPlaceListIterator.hasNext()) phoneNumberSearchFailed();
                        while (nearbyPlaceListIterator.hasNext() && !nearbyPlaceListIterator.hasBeenStopped()) {
                            Place currentPlace = nearbyPlaceListIterator.next();
                            PlacesClient placesClient = Places.createClient(context);
                            String placeId = currentPlace.getId();
                            // Specify the fields to return.
                            List<Place.Field> placeFields = Collections.singletonList(Place.Field.PHONE_NUMBER);
                            // Construct a request object, passing the place ID and fields array.
                            FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);
                            placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                                Place place = response.getPlace();
                                //place found
                                if (place.getPhoneNumber() != null) { //If the place has a phone number
                                    loadPhoneNumber(place.getPhoneNumber()); //take it
                                    nearbyPlaceListIterator.stopIteration();
                                }
                            });
                        }
                    }
                });
            }
        });

        googleLocationFinder.findCurrentLocation(context);
    }

    /**
     * Trigger the on success method on the listener
     * @param phoneNumber to pass to caller
     */
    private void loadPhoneNumber(String phoneNumber){
        if(phoneNumberGetListener != null){
            phoneNumberGetListener.onSuccess(phoneNumber);
        }
    }

    /**
     * Trigger the on fail method on the listener
     */
    private void phoneNumberSearchFailed(){
        if(phoneNumberGetListener != null){
            phoneNumberGetListener.onFail();
        }
    }
}
