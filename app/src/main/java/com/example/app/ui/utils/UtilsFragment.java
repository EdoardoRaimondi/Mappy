package com.example.app.ui.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.app.HelpActivity;
import com.example.app.HomeActivity;
import com.example.app.MainActivity;
import com.example.app.R;
import com.example.app.factories.IntentFactory;
import com.example.app.factories.UrlFactory;
import com.example.app.finals.MapsParameters;
import com.example.app.finals.MapsUtility;
import com.example.app.finals.NearbyRequestType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Random;

/**
 * App utility fragment
 * It contains: wheel, radius selection bar and floating buttons.
 * Floating buttons: setHome, viewHome, sos, saveLocation
 */
public class UtilsFragment extends Fragment {

    // Private members
    private int degree = 0;
    private boolean isViewMode = false;
    private boolean isSpinning = false;

    // Considering a 360 degree circle divided in 6 sections and
    // I start from an half of one. I got 360 / 6 / 2.
    // (so 1 section will be 2 FACTOR large)
    private static final float FACTOR = 30f;

    // Widgets and Activity
    private SeekBar bar;
    private TextView txt;
    private ImageView wheel;
    private MainActivity activity;
    // Java objects
    private Random random;

    /**
     * Callback when the fragment is visible
     * @param inflater  The layout Inflater
     * @param container The root container
     * @param savedInstanceState Bundle for eventual instance to restore
     * @return The fragment View
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ViewModelProviders.of(this).get(UtilsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_utils, container, false);

        if(getActivity() != null){
            activity = (MainActivity) getActivity();
        }

        // Saving current displayed fragment
        activity.setFragment(R.id.navigation_utils);

        // Get the widgets references
        wheel = root.findViewById(R.id.wheel);
        // Floating buttons
        final FloatingActionButton sos = root.findViewById(R.id.sos);
        final FloatingActionButton home = root.findViewById(R.id.home);

        LatLng pos = getHomeLocation();
        // If user has no home set yet, I show a home button. A directions button is showed otherwise.
        if (pos.latitude != 0 && pos.longitude != 0) {
            // Directions button
            setDirectionsButton(home);
        }
        else {
            // Home button
            setHomeButton(home);
        }

        // Get the radius bar
        bar = root.findViewById(R.id.seek);
        // Restore the last radius research
        bar.setProgress(activity.getRadius() / MapsUtility.KM_TO_M);
        // Get the text
        txt = root.findViewById(R.id.text);
        String display = "" + (activity.getRadius() / MapsUtility.KM_TO_M) + " " + getResources().getString(R.string.measure_unit);
        txt.setText(display);
        random = new Random();

        // User click the wheel and it starts rotate
        wheel.setOnClickListener(new View.OnClickListener() {
            /**
             * Called when wheel has been clicked.
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                if(!isSpinning) {
                    wheel.setClickable(false);
                    isSpinning = true;
                    int oldDegree = degree % 360;
                    degree = random.nextInt(360) + 720;
                    RotateAnimation rotate = new RotateAnimation(oldDegree, degree,
                            RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                            RotateAnimation.RELATIVE_TO_SELF, 0.5f
                    );
                    rotate.setDuration(3600);
                    rotate.setFillAfter(true);
                    rotate.setInterpolator(new DecelerateInterpolator());
                    rotate.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            wheel.setClickable(false);
                        }

                        /**
                         * Send a request depending on its final position
                         * @param animation The Animation selected
                         */
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            sendRequest(360 - (degree % 360));
                            isSpinning = false;
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                    wheel.startAnimation(rotate);
                }
            }
        });

        /**
         * User can have one home at time set
         *   Home    button -> user click to set a home.
         * Direction button -> user click to view his home. (I'm in viewMode)
         * Direction button -> user long click to delete current home and reset + button.
         * {@link HomeActivity} for details
         */
        home.setOnClickListener(new View.OnClickListener(){
            /**
             * User can have one home at time set
             *  !viewMode  -> user click to set a home.
             *  viewMode -> user click to view his home.
             * @param v The button View
             */
            @Override
            public void onClick(View v) {
                home.setClickable(false);
                if(isViewMode) {
                    // If the button has direction image
                    // Launch google maps app
                    Uri gmmIntentUri = UrlFactory.createDirectionsUrl(pos.latitude, pos.longitude);
                    startActivity(IntentFactory.createGoogleMapsDirectionsIntent( gmmIntentUri));
                }
                else{
                    // The button has home image
                    Intent setHomeIntent = IntentFactory.createHomeRequest(getActivity());
                    startActivity(setHomeIntent);
                }
            }
        });

        home.setOnLongClickListener(new View.OnLongClickListener() {
            /**
             * Delete the old home and reset the button in !isViewMode
             * @param v The button View
             * @return true because no longer actions are excepted
             */
            @Override
            public boolean onLongClick(View v) {
                final boolean[] hasUndo = {false};
                setHomeButton(home);
                if(getActivity() != null) {
                    Snackbar.make(root.findViewById(R.id.box_for_undo), getString(R.string.home_delete), Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.undo), v1 -> {
                                setDirectionsButton(home);
                                hasUndo[0] = true;
                            })
                            .show();
                }
                if(!hasUndo[0]) deleteHomeLocation();
                return true;
            }
        });

        /**
         * User click to open the {@link HelpActivity}
         */
        sos.setOnClickListener(v -> {
            Intent helpIntent = IntentFactory.createHelpIntentRequest(getActivity());
            startActivity(helpIntent);
        });

        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /**
             * Callback when user tracks the bar
             * @param seekBar  The bar displayed
             * @param progress Integer representing the position of the user touch on the bar
             * @param fromUser The boolean to check if the progress is from the user
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String display = "" + (progress + getResources().getInteger(R.integer.default_radius)) + " " + getResources().getString(R.string.measure_unit);
                txt.setText(display);
            }

            /**
             * Callback when the user start tracking the bar
             * @param seekBar The bar displayed
             */
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            /**
             * Callback when the user stop tracking the bar
             * @param seekBar The bar displayed
             */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                activity.setRadius((bar.getProgress() + getResources().getInteger(R.integer.default_radius)) * MapsUtility.KM_TO_M);
            }
        });

        return root;

    }

    @Override
    public void onResume() {
        super.onResume();
        wheel.setClickable(true);
    }

    // END OF HOME FRAGMENT LIFE CYCLE

    // UTILITY METHODS

    /**
     * Analise the wheel position and send the corresponding command
     * @param position The result position
     */
    private void sendRequest(int position) {
        int radius = activity.getRadius();
        if ((position >= FACTOR * 1) && (position < FACTOR * 3)) {
            Intent intent = IntentFactory.createNearbyRequestIntent(getActivity(), NearbyRequestType.art_gallery, radius);
            startActivity(intent);
        }
        if ((position >= FACTOR * 3) && (position < FACTOR * 5)){
            Intent intent = IntentFactory.createNearbyRequestIntent(getActivity(), NearbyRequestType.museum, radius);
            startActivity(intent);
        }
        if((position >= FACTOR * 5) && (position < FACTOR * 7)){
            Intent intent = IntentFactory.createNearbyRequestIntent(getActivity(), NearbyRequestType.zoo, radius);
            startActivity(intent);
        }
        if((position >= FACTOR * 7) && (position < FACTOR * 9)){
            Intent intent = IntentFactory.createNearbyRequestIntent(getActivity(), NearbyRequestType.movie_theater, radius);
            startActivity(intent);
        }
        if((position >= FACTOR * 9) && (position < FACTOR * 11)){
            Intent intent = IntentFactory.createNearbyRequestIntent(getActivity(), NearbyRequestType.tourist_attraction, radius);
            startActivity(intent);
        }
        if((position >= FACTOR * 11) && (position < FACTOR * 13)){
            Intent intent = IntentFactory.createNearbyRequestIntent(getActivity(), NearbyRequestType.park, radius);
            startActivity(intent);
        }
        else{
            // Something goes wrong. Make another wheel spin
            wheel.performClick();
        }
    }

    // METHODS TO CHANGE BUTTON IMAGE AND MODE

    /**
     * Set the direction home button image
     * @param home The button FloatingActionButton
     */
    private void setDirectionsButton(FloatingActionButton home){
        home.setImageResource(R.drawable.ic_direction);
        isViewMode = true;
    }


    /**
     * Set the plus home button image
     * @param home The button FloatingActionButton
     */
    private void setHomeButton(FloatingActionButton home){
        home.setImageResource(R.drawable.ic_home);
        isViewMode = false;
    }

    // SHARED PREFERENCE METHODS

    /**
     * @return Current LatLng home coordinates ,(0,0) if not valid
     */
    private LatLng getHomeLocation(){
        // Getting eventual home coordinate set in a previous app usage
        SharedPreferences shared = activity.getSharedPreferences(MapsParameters.SHARED_HOME_PREFERENCE, Context.MODE_PRIVATE);
        double homeLat = Double.parseDouble(shared.getString(HomeActivity.HOME_LAT, "0.0"));
        double homeLng = Double.parseDouble(shared.getString(HomeActivity.HOME_LNG, "0.0"));
        return new LatLng(homeLat,homeLng);
    }

    /**
     * Delete home location
     */
    private void deleteHomeLocation(){
        SharedPreferences shared = activity.getSharedPreferences(MapsParameters.SHARED_HOME_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.remove(HomeActivity.HOME_LAT);
        editor.remove(HomeActivity.HOME_LNG);
        editor.apply();
    }

}
