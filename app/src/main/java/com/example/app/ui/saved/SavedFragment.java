package com.example.app.ui.saved;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.saved_place_database.SavedPlace;
import com.example.app.saved_place_database.SavedPlaceDatabase;

import java.util.List;

public class SavedFragment extends Fragment {

    private SavedViewModel savedViewModel;
    private RecyclerView mRecyclerView;
    private SavedListAdapter savedListAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
        savedViewModel = new SavedViewModel(getActivity().getApplication());

        View root = inflater.inflate(R.layout.fragment_saved, container, false);

        // 1. get a reference to recyclerView
        mRecyclerView = root.findViewById(R.id.recycler_view_saved);

        // 2. set layoutManger
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 3. create an adapter
        savedListAdapter = new SavedListAdapter(getContext());
        // 4. set adapter
        mRecyclerView.setAdapter(savedListAdapter);

        //observe the change of the live data
        savedViewModel.getAllPlaces().observe(getActivity(), new Observer<List<SavedPlace>>() {
            @Override
            public void onChanged(List<SavedPlace> savedPlaces) {
                savedListAdapter.setPlace(savedPlaces);
            }
        });

        return root;
    }
}
