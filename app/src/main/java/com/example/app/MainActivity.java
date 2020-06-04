package com.example.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.WindowManager;

import com.example.app.dialogs.BasicDialog;
import com.example.app.sensors.ConnectionManager;
import com.example.app.sensors.GPSManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

/**
 * Main UI activity. Here the user can choose the main actions.
 */
public class MainActivity extends AppCompatActivity implements BasicDialog.BasicDialogListener{

    // Dialogs' ids
    private static final String RATIONALE_ID = "rationale_id";
    // Instance state and intent keys
    private static final String RADIUS_KEY = "radius_k";
    private static final String FRAGMENT_KEY = "fragment_k";
    public static final String FRAGMENT_KEY_INTENT = "fragment_k";
    // Tag for dialogs and logs
    private static final String TAG = "MainActivity";
    // Type of location request
    private static final int REQUEST_USER_LOCATION_CODE = 99;
    // Selected radius
    private int radius;
    // Fragment displayed
    private int displayFragment;


    // BEGIN OF MAIN ACTIVITY'S LIFE CYCLE CALLBACKS
    /**
     * Callback invoked while creating MainActivity
     * @param savedInstanceState the Bundle were previous state has been saved
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Setting main activity as full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Setting appropriate layout
        setContentView(R.layout.activity_main);
        // Initializing navigation controller
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu id as a set of ids because each
        // Menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_utils, R.id.navigation_search, R.id.navigation_saved)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        // If instance state was saved then backup
        if(savedInstanceState != null){
            // Restore previous radius
            radius = savedInstanceState.getInt(RADIUS_KEY, getResources().getInteger(R.integer.default_radius) * 1000);
            // Return to previous fragment
            int fragment = savedInstanceState.getInt(FRAGMENT_KEY, R.id.navigation_search);
            displayFragment = fragment;
        }
        else{
            radius = getResources().getInteger(R.integer.default_radius) * 1000;
            displayFragment = R.id.navigation_search;
        }
        Intent intent = getIntent();
        displayFragment = intent.getIntExtra(FRAGMENT_KEY_INTENT, displayFragment);
        NavGraph navGraph = navController.getGraph();
        navGraph.setStartDestination(displayFragment);
        navController.setGraph(navGraph);
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
        // Checking Google Play services apk
        GoogleApiAvailability google = GoogleApiAvailability.getInstance();
        int result = google.isGooglePlayServicesAvailable(this);
        // If Google Play Services is disabled or not present
        if(result != ConnectionResult.SUCCESS){
            Dialog dial = google.getErrorDialog(this, result, GoogleApiAvailability.GOOGLE_PLAY_SERVICES_VERSION_CODE);
            dial.show();
        }
        else{
            // Checking GPS fine location permissions
            final GPSManager gpsManager = new GPSManager(getApplicationContext());
            if(!gpsManager.hasPermissions()){
                // Request for permissions
                if(gpsManager.canRequestNow(this)) {
                    gpsManager.requirePermissions(this, REQUEST_USER_LOCATION_CODE);
                }
                else{
                    BasicDialog.BasicDialogBuilder overQueryBuilder = new BasicDialog.BasicDialogBuilder(RATIONALE_ID);
                    overQueryBuilder.setTitle(getString(R.string.to_clarify));
                    overQueryBuilder.setText(getString(R.string.rationale));
                    overQueryBuilder.setTextForOkButton(getString(R.string.ok_button));
                    overQueryBuilder.build().show(getSupportFragmentManager(), TAG);
                }
            }
            else {
                // Checking if location provider is enabled
                if (!gpsManager.isGPSOn() || !gpsManager.isProviderEnabled()) {
                    Snackbar.make(findViewById(R.id.coordinator), getString(R.string.no_gps), Snackbar.LENGTH_INDEFINITE)
                        .setAction(getString(R.string.yes), v -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                    .show();
                }
                else{
                    // Checking Internet providers
                    final ConnectionManager connectionManager = new ConnectionManager(getApplicationContext());
                    if(!connectionManager.isNetworkAvailable()){
                        Snackbar.make(findViewById(R.id.coordinator), getString(R.string.no_internet), Snackbar.LENGTH_INDEFINITE)
                                .setAction(getString(R.string.yes), v -> startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS)))
                                .show();
                    }
                }
            }
        }
    }

    /**
     * Callback to save the state when necessary
     * @param savedInstanceState Bundle where to save radius value
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(RADIUS_KEY, radius);
        savedInstanceState.putInt(FRAGMENT_KEY, displayFragment);
        super.onSaveInstanceState(savedInstanceState);
    }

    // PERMISSIONS

    /**
     * Callback to check user permission results
     * @param requestCode   of the permission
     * @param permissions   of the request
     * @param grantResults  of the permission request
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_USER_LOCATION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(findViewById(R.id.coordinator), getString(R.string.thank_you), Snackbar.LENGTH_LONG)
                        .show();
            }
            else {
                Snackbar.make(findViewById(R.id.coordinator), getString(R.string.no_gps_permission), Snackbar.LENGTH_LONG)
                        .show();
            }
        }
    }

    // END OF MAIN ACTIVITY'S LIFE CYCLE CALLBACKS

    // PUBLIC INTERFACE

    /**
     * Getter method of radius
     */
    public int getRadius(){
        return this.radius;
    }

    /**
     * Setter method of radius
     * @param radius the radius to set
     */
    public void setRadius(int radius){
        this.radius = radius;
    }

    /**
     * Setter method of displayed fragment
     * @param fragment the radius to set
     */
    public void setFragment(int fragment){
        this.displayFragment = fragment;
    }

    // DIALOGS RESULT LISTENER
    /**
     * BasicDialog common listener
     * @param id the identifyer of dialog that was dismissed
     * @param option the option choosen by user
     */
    public void onDialogResult(String id, boolean option){
        if(id.equals(RATIONALE_ID)) {
            if (option) {
                (new GPSManager(this)).requirePermissions(this, REQUEST_USER_LOCATION_CODE);
            }
        }
    }

}
