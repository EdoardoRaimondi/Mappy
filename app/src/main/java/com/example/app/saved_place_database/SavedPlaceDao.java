package com.example.app.saved_place_database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

/**
 * Dao layer to interact with the database.
 * As Room required
 */
@Dao
public interface SavedPlaceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPlace(SavedPlace place);

    @Query("SELECT * FROM SavedPlace")
    LiveData<List<SavedPlace>> getAll();

    @Delete
    void delete(SavedPlace place);


}
