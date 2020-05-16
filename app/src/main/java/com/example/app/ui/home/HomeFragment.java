package com.example.app.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.app.HomeActivity;
import com.example.app.R;
import com.example.app.factories.IntentFactory;
import com.example.app.finals.HomeMode;
import com.example.app.finals.MapsParameters;
import com.example.app.finals.NearbyRequestType;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Random;

/**
 * App default fragment.
 * It contains: wheel, radius selection bar and floating buttons.
 * Floating buttons: setHome, viewHome, sos
 */
public class HomeFragment extends Fragment {

    private int degree = 0;
    private boolean fineRadius = false;

    private static final String BAR_KEY = "bar_k";

    private static final double M_TO_KM_DIVIDER = 1000.0;
    // considering a 360 degree circle divided in 6 sections and
    // I start from an half of one. I got 360 / 6 / 2.
    // (so 1 section will be 2 FACTOR large)
    private static final float FACTOR = 30f;
    // view components
    private SeekBar bar;
    private TextView txt;
    private ImageView wheel;

    private boolean isViewMode = false;
    private Random random;

    /**
     * Callback when the fragment is visible
     * @param inflater layout
     * @param container root container
     * @param savedInstanceState for eventual instance to restore
     * @return the fragment view
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        //Get the widgets references
        wheel = root.findViewById(R.id.wheel);
        FloatingActionButton sos = root.findViewById(R.id.sos);
        final FloatingActionButton home = root.findViewById(R.id.home);

        //Get the eventual home coordinate set in a previous app usage
        SharedPreferences shared = this.getActivity().getSharedPreferences(MapsParameters.SHARED_HOME_PREFERENCE, Context.MODE_PRIVATE);
        double homeLat = Double.parseDouble(shared.getString(HomeActivity.HOME_LAT, "0.0"));
        double homeLng = Double.parseDouble(shared.getString(HomeActivity.HOME_LNG, "0.0"));
        //If user has no house set yet, I show a + button. A directions button is showed otherwise.
        if(homeLat != 0 && homeLng != 0){
            //Directions button
            setDirectionsButton(home);
        }
        else{
            //+ button
            setHomeButton(home);
        }

        //Get the radius bar
        bar = root.findViewById(R.id.seek);
        //Restore the last radius research
        if(savedInstanceState != null){
            int savedRadius = savedInstanceState.getInt(BAR_KEY, getResources().getInteger(R.integer.default_radius));
            bar.setProgress(savedRadius);
        }
        else{
            bar.setProgress(getResources().getInteger(R.integer.default_radius));
        }
        //Get the text
        txt = root.findViewById(R.id.text);

        random = new Random();

        //User click the wheel and it starts rotate
        wheel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
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
                    }

                    /**
                     * Send a request depending on its final position
                     * @param animation the animation
                     */
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        sendRequest(360 - (degree % 360));
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                wheel.startAnimation(rotate);
            }
        });

        /**
         * User can have one home at time set
         *     +     button -> user click to set a home.
         * Direction button -> user click to view his home. (I'm in viewMode)
         * Direction button -> user long click to delete current home and reset + button.
         * {@link HomeActivity} for details
         */
        home.setOnClickListener(new View.OnClickListener(){
            /**
             * User can have one home at time set
             *  !viewMode  -> user click to set a home.
             *  viewMode -> user click to view his home.
             * @param v the button
             */
            @Override
            public void onClick(View v) {
                if(isViewMode) { //if the button has direction image
                    Intent viewHomeIntent = IntentFactory.createHomeRequest(getActivity(), HomeMode.viewMode);
                    startActivity(viewHomeIntent);
                }
                else{ //the button has + image
                    Intent setHomeIntent = IntentFactory.createHomeRequest(getActivity(), HomeMode.setMode);
                    startActivity(setHomeIntent);
                }
            }
        });

        home.setOnLongClickListener(new View.OnLongClickListener() {
            /**
             * Delete the old home and reset the button in !notViewMode (+ image)
             * @param v the button
             * @return true because no longer actions are excepted
             */
            @Override
            public boolean onLongClick(View v) {
                setHomeButton(home);
                return true;
            }
        });

        //User click to open the {@link HelpActivity}
        sos.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent helpIntent = IntentFactory.createHelpIntentRequest(getActivity());
                startActivity(helpIntent);
            }
        });

        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /**
             * Callback when user tracks the bar
             * @param seekBar  the bar displayed
             * @param progress int representing the position of the user touch on the bar
             * @param fromUser boolean to check if the progress is from the user
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String display;
                if(progress >= M_TO_KM_DIVIDER){
                    display = (int) Math.ceil(progress / M_TO_KM_DIVIDER) + " km";
                }
                else{
                    display = progress + " m";
                }
                txt.setText(display);
            }

            /**
             * Callback when the user start tracking the bar
             * @param seekBar the bar displayed
             */
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                fineRadius = false;
            }

            /**
             * Callback when the user stop tracking the bar
             * @param seekBar the bar displayed
             */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        root.findViewById(R.id.more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!fineRadius){
                    adjustFine();
                }
                if(bar.getProgress() + getResources().getInteger(R.integer.radius_increment) <= getResources().getInteger(R.integer.max_radius)){
                    bar.setProgress(bar.getProgress() + getResources().getInteger(R.integer.radius_increment));
                    fineIncrement();
                }
                else{
                    bar.setProgress(getResources().getInteger(R.integer.max_radius));
                }
            }
        });

        root.findViewById(R.id.less).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!fineRadius){
                    adjustFine();
                }
                if(bar.getProgress() - getResources().getInteger(R.integer.radius_increment) >= getResources().getInteger(R.integer.radius_increment)){
                    bar.setProgress(bar.getProgress() - getResources().getInteger(R.integer.radius_increment));
                    fineIncrement();
                }
                else{
                    bar.setProgress(getResources().getInteger(R.integer.radius_increment));
                }
            }
        });
        return root;

    }

    /**
     * Callback to save the state when necessary
     * @param savedInstanceState Bundle where to save places information
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(BAR_KEY, bar.getProgress());
    }

    // END OF HOME FRAGMENT LIFE CYCLE

    // UTILITY METHODS

    /**
     * Procedure to override bar onProgressChanged to
     * show fine increment on text view
     */
    private void fineIncrement(){
        fineRadius = true;
        int progress = bar.getProgress();
        int decimals = progress % (int) M_TO_KM_DIVIDER;
        int km = (int) Math.floor(progress / M_TO_KM_DIVIDER);
        if(decimals != 0){
            StringBuilder str = new StringBuilder();
            if(km != 0){
                str.append(km);
                str.append(" km ");
            }
            str.append(decimals);
            str.append(" m");
            txt.setText(str.toString());
        }
    }

    /**
     *
     */
    private void adjustFine(){
         if(bar.getProgress() >= M_TO_KM_DIVIDER){
             bar.setProgress((int) (Math.floor(bar.getProgress()/M_TO_KM_DIVIDER) * M_TO_KM_DIVIDER));
         }
    }

    /**
     * Analise the wheel position and send the corresponding command
     * @param position the result position
     */
    private void sendRequest(int position) {
        int radius;
        if(fineRadius){
            radius = bar.getProgress();
        }
        else{
            radius = (int) (Math.ceil(bar.getProgress() / M_TO_KM_DIVIDER) * M_TO_KM_DIVIDER);
        }
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
    }


    // METHODS TO CHANGE BUTTON IMAGE AND MODE

    /**
     * Set the direction home button image
     * @param home the button
     */
    private void setDirectionsButton(FloatingActionButton home){
        home.setImageResource(R.drawable.ic_direction);
        isViewMode = true;
    }


    /**
     * Set the plus home button image
     * @param home the button
     */
    private void setHomeButton(FloatingActionButton home){
        home.setImageResource(R.drawable.ic_add_location);
        isViewMode = false;
    }

}
