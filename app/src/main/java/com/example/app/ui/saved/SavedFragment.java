package com.example.app.ui.saved;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.factories.ViewModelFactory;
import com.example.app.saved_place_database.SavedPlace;

import java.util.List;

public class SavedFragment extends Fragment {

    private SavedViewModel savedViewModel;
    private RecyclerView mRecyclerView;
    private SavedListAdapter savedListAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
        savedViewModel = ViewModelProviders.of(this, new ViewModelFactory(getActivity().getApplication())).get(SavedViewModel.class);

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


        /**
         * Manage user moves on the recycler view items
         */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            /**
             * Delete the place
             * @param viewHolder of the recycler view
             * @param direction of the swipe
             */
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                savedViewModel.remove(savedListAdapter.getSavedPlaceAt(viewHolder.getAdapterPosition()));
                savedListAdapter.notifyDataSetChanged();
            }
        }).attachToRecyclerView(mRecyclerView);

        return root;
    }
}
