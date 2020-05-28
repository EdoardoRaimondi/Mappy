package com.example.app.ui.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.MainActivity;
import com.example.app.R;

import java.util.LinkedList;

/**
 * App search fragment
 * It contains: a list of places to search group by category.
 */
public class SearchFragment extends Fragment {

    private final LinkedList<ItemAdapter> mPlaceList = new LinkedList<>();

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

        //Fill the search list
        mPlaceList.addAll(SearchPlaces.getPlaceSearchList());

        // 1. get a reference to recyclerView
        RecyclerView mRecyclerView = root.findViewById(R.id.recycler_view_search);

        // 2. set layoutManger
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        MainActivity activity = (MainActivity) getActivity();
        assert activity != null;
        // 3. create an adapter
        PlaceListAdapter mAdapter = new PlaceListAdapter(getContext(), mPlaceList, activity.getRadius());
        // 4. set adapter
        mRecyclerView.setAdapter(mAdapter);
        // 5. set item animator to DefaultAnimator
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        return root;
    }


}
