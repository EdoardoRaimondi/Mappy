package com.example.app.ui.saved;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.finals.SearchPlaces;
import com.example.app.ui.search.PlaceListAdapter;

import java.util.LinkedList;

public class SavedFragment extends Fragment {

    private SavedViewModel savedViewModel;
    private LinkedList<String> mSavedList = new LinkedList<>();
    private RecyclerView mRecyclerView;
    private SavedListAdapter savedListAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        savedViewModel =
                ViewModelProviders.of(this).get(SavedViewModel.class);
        View root = inflater.inflate(R.layout.fragment_saved, container, false);

        // 1. get a reference to recyclerView
        mRecyclerView = root.findViewById(R.id.recycler_view_saved);

        // 2. set layoutManger
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 3. create an adapter
        savedListAdapter = new SavedListAdapter(getContext(), mSavedList);
        // 4. set adapter
        mRecyclerView.setAdapter(savedListAdapter);
        // 5. set item animator to DefaultAnimator
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        return root;
    }
}
