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
import com.example.app.finals.NearbyRequestType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * App search fragment
 * It contains: a list of places to search group by category.
 */
public class SearchFragment extends Fragment implements View.OnClickListener{

    private Map<String, NearbyRequestType> dictionary;
    /**
     * Callback when the fragment is visible
     * @param inflater layout
     * @param container root container
     * @param savedInstanceState for eventual instance to restore
     * @return the fragment view
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        ViewModelProviders.of(this).get(SearchViewModel.class);
        View root = inflater.inflate(R.layout.fragment_search, container, false);
        // defining dictionary for get nearby request type
        dictionary = new HashMap<>();
        for(int i = 0; i < NearbyRequestType.values().length; i++){
            dictionary.put(NearbyRequestType.values()[i].toString(), NearbyRequestType.values()[i]);
        }
        // getting all image button ids
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
     * @param v the view that called the listener
     */
    @Override
    public void onClick(View v) {
        // if it is a valid id
        if (v.getId() != View.NO_ID){
            // getting string variable id
            String stringId = v.getResources().getResourceName(v.getId());
            // getting string variable id filtered
            stringId = stringId.replace("com.example.app:id/","");
            // searching NearbyRequestType from dictionary
            NearbyRequestType type;
            try {
                type = dictionary.get(stringId);
            }
            catch (NullPointerException exc){
                type = NearbyRequestType.tourist_attraction;
            }
            // getting radius from MainActivity
            int radius;
            if(getActivity() != null) {
                radius = ((MainActivity) getActivity()).getRadius();
            }
            else{
                radius = getResources().getInteger(R.integer.default_radius) * 1000;
            }
            // ging to MapsActivity
            Intent intent = IntentFactory.createNearbyRequestIntent(getActivity(), type, radius);
            startActivity(intent);
        }
    }

}
