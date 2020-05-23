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
import com.example.app.finals.SearchPlaces;

import java.util.LinkedList;

public class SearchFragment extends Fragment {

    private SearchViewModel searchViewModel;
    private RecyclerView mRecyclerView;
    private PlaceListAdapter mAdapter;
    private final LinkedList<String> mPlaceList = new LinkedList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        searchViewModel =
                ViewModelProviders.of(this).get(SearchViewModel.class);
        View root = inflater.inflate(R.layout.fragment_search, container, false);

        //Fill the search list
        mPlaceList.addAll(SearchPlaces.getPlaceSearchList());

        // 1. get a reference to recyclerView
        mRecyclerView = root.findViewById(R.id.recycler_view_search);

        // 2. set layoutManger
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        MainActivity activity = (MainActivity) getActivity();
        assert activity != null;
        // 3. create an adapter
        mAdapter = new PlaceListAdapter(getContext(), mPlaceList, activity.getRadius());
        // 4. set adapter
        mRecyclerView.setAdapter(mAdapter);
        // 5. set item animator to DefaultAnimator
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());


        return root;
    }


}
