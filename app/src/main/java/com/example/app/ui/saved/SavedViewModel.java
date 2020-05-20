package com.example.app.ui.saved;

import android.app.Application;
import android.media.AsyncPlayer;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.app.saved_place_database.SavedPlace;
import com.example.app.saved_place_database.SavedPlaceDatabase;
import com.example.app.saved_place_database.SavedPlaceRepository;

import java.util.List;

public class SavedViewModel extends AndroidViewModel {

    private SavedPlaceRepository mRepository;
    private LiveData<List<SavedPlace>> mAllSavedPlaces;

    public SavedViewModel(Application application) {
        super(application);
        mRepository = new SavedPlaceRepository(application);
        mAllSavedPlaces = mRepository.getAllPLaces();
    }

    /**
     * Get all the saved places
     * @return
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
}