package com.example.app.listeners;

import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

/**
 * Listener to check when all the markers from the Json
 * google response have been downloaded/created
 */
public interface OnMarkersDownloadedListener {

    /**
     * Callback when the markers are ready
     * @param markers the list of markers
     */
    void onMarkersDownloaded(List<MarkerOptions> markers, LatLngBounds.Builder builder);

}
