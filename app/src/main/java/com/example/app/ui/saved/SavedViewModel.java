package com.example.app.ui.saved;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.app.saved_place_database.SavedPlace;
import com.example.app.saved_place_database.SavedPlaceRepository;

import java.util.List;

public class SavedViewModel extends ViewModel {

    private SavedPlaceRepository mRepository;
    private LiveData<List<SavedPlace>> mAllSavedPlaces;

    /**
     * Saved View Model constructor. It access to repository
     * @param application where is running
     */
    public SavedViewModel(Application application) {
        mRepository = new SavedPlaceRepository(application);
        mAllSavedPlaces = mRepository.getAllPLaces();
    }

    /**
     * Get all the saved places
     * @return all the place as LiveData object
     */
    public LiveData<List<SavedPlace>> getAllPlaces() {
        return mAllSavedPlaces;
    }

    /**
     * Insert a new place to the database
     * @param place to insert
     */
    public void insert(SavedPlace place){
        mRepository.insert(place);
    }

    /**
     * @param place to remove from the database
     */
    public void remove(SavedPlace place){
        mRepository.remove(place);
    }
}