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

    /**
     * Constructor
     * @param context The Context ConnectionManager instance has to be attached
     */
    public ConnectionManager(Context context){
        this.context = context;
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
        ConnectionType type = ConnectionType.NONE;
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager != null){
            NetworkInfo[] netInfo = connectivityManager.getAllNetworkInfo();
            for (NetworkInfo netinfo : netInfo) {
                if (netinfo.getTypeName().equalsIgnoreCase("WIFI")) {
                    if (netinfo.isConnected()) {
                        type = ConnectionType.WIFI;
                    }
                }
                else if (netinfo.getTypeName().equalsIgnoreCase("MOBILE")){
                    if (netinfo.isConnected()) {
                        type = ConnectionType.MOBILE;
                    }
                }
            }
        }
        return type;
    }

}
