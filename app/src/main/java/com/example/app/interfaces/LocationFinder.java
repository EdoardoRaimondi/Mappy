package com.example.app.interfaces;

import android.content.Context;

public interface LocationFinder {

    /**
     * Void cause need to be manage with {@link com.example.app.listeners.OnLocationSetListener}
     * @param context of the activity caller
     */
    void findCurrentLocation(Context context);
}
