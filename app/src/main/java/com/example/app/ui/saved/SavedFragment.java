package com.example.app.ui.saved;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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


            @Override
            public void onChildDraw (@NonNull Canvas c,
                                     @NonNull RecyclerView recyclerView,
                                     @NonNull RecyclerView.ViewHolder viewHolder,
                                     float dX,
                                     float dY,
                                     int actionState,
                                     boolean isCurrentlyActive){
                int itemHeight = viewHolder.itemView.getBottom() - viewHolder.itemView.getTop();

                // Draw the red delete background
                final ColorDrawable background = new ColorDrawable(Color.RED);
                background.setBounds(
                        (int)(viewHolder.itemView.getRight() + dX),
                        viewHolder.itemView.getTop(),
                        viewHolder.itemView.getRight(),
                        viewHolder.itemView.getBottom()
                );
                background.draw(c);

                Drawable icon = ContextCompat.getDrawable(getActivity(), R.drawable.ic_delete);
                int intrinsicHeight = icon.getIntrinsicHeight();
                int intrinsicWidth = icon.getIntrinsicWidth();

                // Calculate position of delete icon
                int iconTop = viewHolder.itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
                int iconMargin = (itemHeight - intrinsicHeight) / 2;
                int iconLeft = viewHolder.itemView.getRight() - iconMargin - intrinsicWidth;
                int iconRight = viewHolder.itemView.getRight() - iconMargin;
                int iconBottom = iconTop + intrinsicHeight;

                // Draw the delete icon
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                // compute top and left margin to the view bounds
                icon.draw(c);
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
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
