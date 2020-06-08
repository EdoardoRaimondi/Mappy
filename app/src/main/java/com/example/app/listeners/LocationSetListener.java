package com.example.app.listeners;

import android.location.Location;

/**
 * Location listener interface
 */
public interface LocationSetListener {

    /**
     * Callback when the user position is set
     * @param location The user Location
     */
    void onLocationSet(Location location);
}
