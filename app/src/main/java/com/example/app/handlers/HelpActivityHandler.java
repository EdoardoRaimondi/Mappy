package com.example.app.handlers;

import android.content.Context;

import com.example.app.GetNearbyPlaces;
import com.example.app.HelpActivity;
import com.example.app.R;
import com.example.app.factories.UrlFactory;
import com.example.app.finals.NearbyRequestType;
import com.example.app.iterators.StoppablePlaceIterator;
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

    public HelpActivityHandler(){ phoneNumberGetListener = null; }

    public void setPhoneNumberGetListener(PhoneNumberGetListener phoneNumberGetListener){
        this.phoneNumberGetListener = phoneNumberGetListener;
    }

    /**
     * Trigger the {@link PhoneNumberGetListener} when the
     * nearest phone number is found
     * @param type    Type of phone number you need (need to be a {@link NearbyRequestType})
     * @param context The caller's Context
     * @param apiKey  The Google API key
     */
     public void getPhoneNumber(NearbyRequestType type, Context context, String apiKey){
        GoogleLocationFinder googleLocationFinder = new GoogleLocationFinder();
        googleLocationFinder.setLocationSetListener(location -> {
            String url = UrlFactory.getNearbyRequest(
                    apiKey,
                    location.getLatitude(),
                    location.getLongitude(),
                    type.toString(), 
                    context.getResources().getInteger(R.integer.help_radius)
            );
            // The request will be downloaded and displayed
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
                        if(placeId != null) {
                            FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);
                            placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                                Place place = response.getPlace();
                                // Place found
                                if (place.getPhoneNumber() != null) { // If the place has a phone number
                                    loadPhoneNumber(place.getPhoneNumber()); // Take it
                                    nearbyPlaceListIterator.stopIteration();
                                }
                            });
                        }
                        else{
                            nearbyPlaceListIterator.stopIteration();
                        }
                    }
                }
                @Override
                public void onResultNotSet(String error) {
                    phoneNumberSearchFailed();
                }
            });
        });

        googleLocationFinder.findCurrentLocation(context);
    }

    /**
     * Trigger the on success method on the listener
     * @param phoneNumber Phone number to pass to caller
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
