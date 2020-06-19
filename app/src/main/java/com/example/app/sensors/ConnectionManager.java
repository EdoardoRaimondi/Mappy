package com.example.app.sensors;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.app.finals.ConnectionType;

/*
* Class for managing Internet connections
*/
public class ConnectionManager {

    // Object params
    private Context context;
    private ConnectivityManager connectivityManager;

    /**
     * Constructor
     * @param context The Context ConnectionManager instance has to be attached
     */
    public ConnectionManager(Context context){
        this.context = context;
        this.connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    /*
    * Method to know if Internet is available
    */
    public boolean isNetworkAvailable() {
        return getConnectionType() != ConnectionType.NONE;
    }

    /*
    * Method to get Internet connection type based on active providers
    */
    public ConnectionType getConnectionType(){
        if(connectivityManager != null){
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if(netInfo != null && netInfo.isConnected()){
                if(netInfo.getTypeName().equalsIgnoreCase("WIFI")){
                    return ConnectionType.WIFI;
                }
                else if(netInfo.getTypeName().equalsIgnoreCase("MOBILE")){
                    return ConnectionType.MOBILE;
                }
                else{
                    return ConnectionType.NONE;
                }
            }
        }
        return ConnectionType.NONE;
    }
}
