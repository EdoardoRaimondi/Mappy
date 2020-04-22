package com.example.app;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

/**
 * Inner class to define the radius spinner activity
 */
public class SpinnerActivity extends Activity implements AdapterView.OnItemSelectedListener {

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        String radiusString = parent.getItemAtPosition(pos).toString();
        MainActivity.RADIUS = parseRadius(radiusString);
        Log.d("RADIUS", String.valueOf(MainActivity.RADIUS));
    }

    public void onNothingSelected(AdapterView<?> parent) {
        //default radius is 2km
        MainActivity.RADIUS = 2000;
    }

    //NATIVE METHODS

    /**
     * Parser for the radius integer
     * @param radius to parse
     */
    public native int parseRadius(String radius);

    /**
     * Library loading
     */
    static {
        System.loadLibrary("libmain_native_lib");
    }
}
