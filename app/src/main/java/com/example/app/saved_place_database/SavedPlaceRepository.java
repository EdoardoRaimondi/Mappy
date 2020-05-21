package com.example.app.saved_place_database;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;

/**
 * Data is stored here before any type of activity interaction
 * Following the official android app structure guide
 * {@see https://developer.android.com/jetpack/docs/guide}
 */
public class SavedPlaceRepository {

    private SavedPlaceDao mSavedPlaceDao;
    private LiveData<List<SavedPlace>> mAllPlaces;


    /**
     * Constructor of the repository. It access to database
     * @param application where is running
     */
    public SavedPlaceRepository(Application application) {
        SavedPlaceDatabase db = SavedPlaceDatabase.getDatabase(application);
        mSavedPlaceDao = db.SavedPlaceDao();
        mAllPlaces = mSavedPlaceDao.getAll();
    }


    /**
     * @return return all places from the database as Live Data object
     */
    public LiveData<List<SavedPlace>> getAllPLaces(){
        return mAllPlaces;
    }

    /**
     * Insert place method execute on a non-UI separated thread
     * @param place to insert
     */
    public void insert(SavedPlace place) {
        SavedPlaceDatabase.databaseWriteExecutor.execute(() -> {
            mSavedPlaceDao.insertPlace(place);
        });
    }

    /**
     * Remove a place method execute on a non-UI separated thread
     * @param place to remove
     */
    public void remove(SavedPlace place){
        SavedPlaceDatabase.databaseWriteExecutor.execute(() -> {
            mSavedPlaceDao.delete(place);
        });
    }
}
