package com.example.app.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.app.R;
import com.example.app.factories.IntentFactory;
import com.example.app.finals.HomeMode;
import com.example.app.finals.NearbyRequestType;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Random;

public class HomeFragment extends Fragment {

    private int radius;
    private int degree = 0;
    // considering a 360 degree circle divided in 6 sections and
    // I start from an half of one. I got 360 / 6 / 2.
    // (so 1 section will be 2 FACTOR large)
    private static final float FACTOR = 30f;
    // view components
    private ImageView wheel;
    private FloatingActionButton sos;
    private FloatingActionButton home;

    private Random random;
    private HomeViewModel homeViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        wheel = root.findViewById(R.id.wheel);
        sos = root.findViewById(R.id.sos);
        home = root.findViewById(R.id.home);

        random = new Random();

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

        home.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent homeIntent = IntentFactory.createHomeRequest(getActivity(), HomeMode.setMode);
                startActivity(homeIntent);
            }
        });

        sos.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent helpIntent = IntentFactory.createHelpIntentRequest(getActivity());
                startActivity(helpIntent);
            }
        });

        return root;
    }

    // END OF HOME FRAGMENT LIFE CYCLE

    // UTILITY METHODS
    /**
     * Analise the wheel position and send the corresponding command
     * @param position the result position
     */
    private void sendRequest(int position) {
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

}
