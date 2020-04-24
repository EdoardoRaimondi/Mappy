package com.example.app;

import android.widget.Spinner;

/**
 * Class containing all the possible status for a place response
 */
public class ResponseStatus {

    public static final String OK = "OK";

    public static final String UNKNOWN_ERROR    = "UNKNOWN_ERROR";
    public static final String OVER_QUERY_LIMIT = "OVER_QUERY_LIMIT";
    public static final String REQUEST_DENIED   = "REQUEST_DENIED";
    public static final String ZERO_RESULTS     = "ZERO_REQUEST";
    public static final String NOT_FOUND        = "NOT_FOUND";
    public static final String INVALID_REQUEST  = "INVALID _REQUEST";
}
