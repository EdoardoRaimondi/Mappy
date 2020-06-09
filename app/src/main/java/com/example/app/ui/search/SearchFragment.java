package com.example.app.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.app.MainActivity;
import com.example.app.R;
import com.example.app.factories.IntentFactory;
import com.example.app.finals.MapsUtility;
import com.example.app.finals.NearbyRequestType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * App search fragment
 * It contains: a list of places to search group by category.
 */
public class SearchFragment extends Fragment implements View.OnClickListener{

    // Dictionary for onClick listener
    private Map<String, NearbyRequestType> dictionary;

    /**
     * Callback when the fragment is visible
     * @param inflater           The layout inflater
     * @param container          Root container
     * @param savedInstanceState Bundle for eventual instance to restore
     * @return The fragment view
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        ViewModelProviders.of(this).get(SearchViewModel.class);
        View root = inflater.inflate(R.layout.fragment_search, container, false);
        // Defining dictionary for get nearby request type
        dictionary = new HashMap<>();
        for(int i = 0; i < NearbyRequestType.values().length; i++){
            dictionary.put(NearbyRequestType.values()[i].toString(), NearbyRequestType.values()[i]);
        }
        // Getting all image button ids
        ArrayList<View> allButtons;
        allButtons = root.findViewById(R.id.full_page).getTouchables();
        for(int i = 0; i < allButtons.size(); i++){
            ImageButton button = (ImageButton) allButtons.get(i);
            button.setOnClickListener(this);
        }

        return root;
    }

    /**
     * Common OnClick listener
     * @param v The View that called the listener
     */
    @Override
    public void onClick(View v) {
        // If it is a valid id
        if (v.getId() != View.NO_ID){
            // Getting string variable id
            String stringId = v.getResources().getResourceName(v.getId());
            // Getting string variable id filtered
            stringId = stringId.replace("com.example.app:id/","");
            // Searching NearbyRequestType from dictionary
            NearbyRequestType type;
            try {
                type = dictionary.get(stringId);
            }
            catch (NullPointerException exc){
                type = NearbyRequestType.tourist_attraction;
            }
            if(type == null){
                /* Default place is a place attraction as we expect mainly
                   tourists to use our app but this should never happen if
                   managed correctly image buttons' ids
                 */
                type = NearbyRequestType.tourist_attraction;
            }
            // Getting radius from MainActivity
            int radius;
            if(getActivity() != null) {
                radius = ((MainActivity) getActivity()).getRadius();
            }
            else{
                radius = getResources().getInteger(R.integer.default_radius) * MapsUtility.KM_TO_M;
            }
            // Going to MapsActivity
            Intent intent = IntentFactory.createNearbyRequestIntent(getActivity(), type, radius);
            startActivity(intent);
        }
    }

}
