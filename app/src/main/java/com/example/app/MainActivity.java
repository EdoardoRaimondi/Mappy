package com.example.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app.dialogs.BasicDialog;
import com.example.app.sensors.GPSManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Main UI activity. Here the user can choose the main actions.
 */
public class MainActivity extends AppCompatActivity implements BasicDialog.BasicDialogListener{

    // CONSTS
    // basic dialogs ids
    private static final String NO_CONN_ID = "no_conn_id";
    private static final String NO_GPS_ID = "no_gps_id";
    // tag for dialogs and logs
    private static final String TAG = "MainActivity";
    // type of location request
    private static final int REQUEST_USER_LOCATION_CODE = 99;

    // BEGIN OF MAIN ACTIVITY'S LIFE CYCLE CALLBACKS
    /**
     * Callback invoked while creating MainActivity
     * @param savedInstanceState the Bundle were previous state has been saved
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // passing each menu id as a set of ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    /**
     * Callback when user return here or activity
     * has just been created
     */
    @Override
    protected void onResume() {
        super.onResume();
        // checking Google Play services apk
        GoogleApiAvailability google = GoogleApiAvailability.getInstance();
        int result = google.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS){
            Dialog dial = google.getErrorDialog(this, result, GoogleApiAvailability.GOOGLE_PLAY_SERVICES_VERSION_CODE);
            dial.show();
        }
        else{
            GPSManager gpsManager = new GPSManager(getApplicationContext());
            if(!gpsManager.hasPermissions()){
                if(gpsManager.canRequestNow(this)) {
                    gpsManager.requirePermissions(this, REQUEST_USER_LOCATION_CODE);
                }
                else{
                    // TODO: show reasonable
                }
            }
            else {
                if (!gpsManager.isGPSOn() || !gpsManager.isProviderEnabled()) {
                    BasicDialog.BasicDialogBuilder noGpsBuilder = new BasicDialog.BasicDialogBuilder(NO_GPS_ID);
                    noGpsBuilder.setTitle(getString(R.string.hey));
                    noGpsBuilder.setText(getString(R.string.no_gps));
                    noGpsBuilder.setTextForOkButton(getString(R.string.ok_button));
                    noGpsBuilder.setTextForCancelButton(getString(R.string.cancel_button));
                    noGpsBuilder.build().show(getSupportFragmentManager(), TAG);
                }
            }
        }
    }

    // PERMISSIONS
    /**
     * Callback to check user permission
     * @param requestCode   of the permission
     * @param permissions   of the request
     * @param grantResults  of the permission request
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_USER_LOCATION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, getString(R.string.thank_you), Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, getString(R.string.no_gps_permission), Toast.LENGTH_LONG).show();
            }
        }
    }

    // END OF MAIN ACTIVITY'S LIFE CYCLE CALLBACKS

    // DIALOGS RESULT LISTENER
    /**
     * BasicDialog common listener
     * @param id the identifyer of dialog that was dismissed
     * @param option the option choosen by user
     */
    public void onDialogResult(String id, boolean option){
        switch(id){
            case NO_CONN_ID:
                if(option){
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
                break;
            case NO_GPS_ID:
                if(option){
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
                break;
        }
    }

}
