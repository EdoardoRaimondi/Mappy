package com.example.app.saved_place_database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Singleton saved places database
 */
@Database(entities = {SavedPlace.class}, version = 1, exportSchema = false)
abstract class SavedPlaceDatabase extends RoomDatabase {

    private static volatile SavedPlaceDatabase INSTANCE;
    abstract SavedPlaceDao SavedPlaceDao();

    // Create a background activity
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    /**
     * The only way I can get a database following singleton design pattern
     */
    static SavedPlaceDatabase getDatabase(final Context context){
        if(INSTANCE == null){
            synchronized (SavedPlaceDatabase.class) { // Ensure nobody will create another instance before me
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), SavedPlaceDatabase.class, "savedPlace_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }



}
