package com.example.app.ui.saved;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.app.saved_place_database.SavedPlace;
import com.example.app.saved_place_database.SavedPlaceRepository;

import java.util.List;

/**
 * ModelView class of {@link SavedFragment }
 */
public class SavedViewModel extends ViewModel {

    // Private members
    private SavedPlaceRepository mRepository;
    private LiveData<List<SavedPlace>> mAllSavedPlaces;

    /**
     * Saved View Model constructor. It access to repository
     * @param application Application where it is running
     */
    public SavedViewModel(Application application) {
        mRepository = new SavedPlaceRepository(application);
        mAllSavedPlaces = mRepository.getAllPLaces();

    }

    /**
     * Get all the saved places
     * @return a List of all Place as LiveData object
     */
    LiveData<List<SavedPlace>> getAllPlaces() {
        return mAllSavedPlaces;
    }

    /**
     * Insert a new place to the database
     * @param place Place to insert into database
     */
    public void insert(SavedPlace place){
        mRepository.insert(place);
    }

    /**
     * @param place Place to remove from the database
     */
    void remove(SavedPlace place){
        mRepository.remove(place);
    }
}