package com.example.app.interfaces;

import android.content.Context;

import com.example.app.listeners.LocationSetListener;

public interface LocationFinder {

    /**
     * Void cause need to be manage with {@link LocationSetListener}
     * @param context Context of the activity caller
     */
    void findCurrentLocation(Context context);
}
