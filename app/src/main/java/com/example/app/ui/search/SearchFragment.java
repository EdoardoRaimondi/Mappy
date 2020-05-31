package com.example.app.ui.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import com.example.app.R;

/**
 * App search fragment
 * It contains: a list of places to search group by category.
 */
public class SearchFragment extends Fragment implements View.OnClickListener{


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

        return root;
    }

    /**
     * Common listener for all buttons
     * @param v the View that called the listener
     */
    @Override
    public void onClick(View v) {

    }
}
