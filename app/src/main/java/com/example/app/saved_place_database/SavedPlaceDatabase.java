package com.example.app.saved_place_database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Singleton saved places database
 */
@Database(entities = {SavedPlace.class}, version = 1, exportSchema = false)
public abstract class SavedPlaceDatabase extends RoomDatabase {

    private static volatile SavedPlaceDatabase INSTANCE;
    abstract SavedPlaceDao SavedPlaceDao();

    //Create a background activity
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    /**
     * The only way I can get a database following singleton design pattern
     */
    public static SavedPlaceDatabase getDatabase(final Context context){
        if(INSTANCE == null){
            synchronized (SavedPlaceDatabase.class) { //ensure nobody will create another instance before me
                if(INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), SavedPlaceDatabase.class, "savedPlace_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }



}
