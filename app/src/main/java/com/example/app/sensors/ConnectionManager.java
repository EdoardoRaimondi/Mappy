package com.example.app.sensors;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

import com.example.app.finals.ConnectionType;

public class ConnectionManager {

    private Context context;

    public ConnectionManager(Context context){
        this.context = context;
    }

    public boolean isNetworkAvailable() {
        return getConnectionType() != ConnectionType.NONE;
    }

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
