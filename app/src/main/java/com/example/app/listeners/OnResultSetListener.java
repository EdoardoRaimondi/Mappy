package com.example.app.listeners;

/**
 * Query result listener interface
 */
public interface OnResultSetListener {

    /**
     * Callback when the result is loaded
     * @param result to get
     */
    void onResultSet(String result);
}
