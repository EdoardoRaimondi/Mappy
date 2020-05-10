package com.example.app.listeners;

public interface OnHomeSetListener {

    /**
     * Callback when the home is set correctly
     */
    void onHomeSet();

    /**
     * Callback when the home has not been set
     */
    void onHomeSetFailed();
}
