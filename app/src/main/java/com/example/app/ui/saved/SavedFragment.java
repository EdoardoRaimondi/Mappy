package com.example.app.ui.saved;

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;

import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.MainActivity;
import com.example.app.R;
import android.app.AlertDialog;
import com.example.app.factories.ViewModelFactory;
import com.example.app.saved_place_database.SavedPlace;
import com.example.app.sensors.GoogleLocationFinder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SavedFragment extends Fragment {

    // Private members
    private SavedViewModel savedViewModel;
    private SavedListAdapter savedListAdapter;
    private MainActivity activity;

    private GoogleLocationFinder locationFinder = new GoogleLocationFinder();

    /**
     * Callback when the fragment is visible
     * @param inflater           The layout inflater
     * @param container          Root container
     * @param savedInstanceState Bundle for eventual instance to restore
     * @return The fragment view
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(getActivity() != null) {
            activity = (MainActivity) getActivity();
        }
        savedViewModel = ViewModelProviders.of(
                this,
                 new ViewModelFactory(activity.getApplication())
        ).get(SavedViewModel.class);

        View root = inflater.inflate(R.layout.fragment_saved, container, false);

        // Saving current displayed fragment
        activity.setFragment(R.id.navigation_saved);

        final FloatingActionButton saveLocation = root.findViewById(R.id.save_position);
        // Set listener for save location button
        saveLocation.setOnClickListener(v -> {
            locationFinder.setLocationSetListener(location -> {
                SavedPlace place = new SavedPlace(location.getLatitude(), location.getLongitude());
                setEditablePlaceName(place, savedViewModel);
            });
            locationFinder.findCurrentLocation(getContext());
        });

        // 1. get a reference to recyclerView
        RecyclerView mRecyclerView = root.findViewById(R.id.recycler_view_saved);

        // 2. set layoutManger
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 3. create an adapter
        savedListAdapter = new SavedListAdapter(getContext());
        // 4. set adapter
        mRecyclerView.setAdapter(savedListAdapter);

        // Observe the change of the live data
        savedViewModel.getAllPlaces().observe(getActivity(), savedPlaces -> savedListAdapter.setPlace(savedPlaces));


        /*
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
                final ColorDrawable background = new ColorDrawable(getResources().getColor(R.color.warningColor));
                background.setBounds(
                        (int)(viewHolder.itemView.getRight() + dX),
                        viewHolder.itemView.getTop(),
                        viewHolder.itemView.getRight(),
                        viewHolder.itemView.getBottom()
                );
                background.draw(c);

                if(getActivity() != null) {
                    Drawable icon = ContextCompat.getDrawable(getActivity(), R.drawable.ic_delete);
                    if(icon != null) {
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
                        // Compute top and left margin to the view bounds
                        icon.draw(c);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

            /**
             * Delete the place
             * @param viewHolder ViewHolder of the RecyclerView
             * @param direction Integer param as direction of swipe
             */
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // Getting data of deleted object
                final String name = savedListAdapter.getSavedPlaceAt(viewHolder.getAdapterPosition()).getPlaceName();
                final String date = savedListAdapter.getSavedPlaceAt(viewHolder.getAdapterPosition()).getDateSaved();
                final double lat = savedListAdapter.getSavedPlaceAt(viewHolder.getAdapterPosition()).getLatitude();
                final double lng = savedListAdapter.getSavedPlaceAt(viewHolder.getAdapterPosition()).getLongitude();
                savedViewModel.remove(savedListAdapter.getSavedPlaceAt(viewHolder.getAdapterPosition()));
                savedListAdapter.notifyDataSetChanged();
                Snackbar.make(root.findViewById(R.id.delete_box), getString(R.string.place_delete), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.undo), v -> {
                            final SavedPlace place = new SavedPlace(lat, lng);
                            place.setPlaceName(name);
                            place.setDateSaved(date);
                            savedViewModel.insert(place);
                            savedListAdapter.notifyDataSetChanged();
                        })
                        .show();
            }
        }).attachToRecyclerView(mRecyclerView);

        return root;
    }

    /**
     * Open a dialog to let user choose a name for that saved name
     * @param place     The Place saved by user
     * @param viewModel ViewModel of database
     */
    private void setEditablePlaceName(SavedPlace place, SavedViewModel viewModel){
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        @SuppressLint("InflateParams")
        View view = inflater.inflate(R.layout.text_dialog, null);
        EditText inputEditText = view.findViewById(R.id.input_text);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.new_place))
                .setMessage(getString(R.string.new_place_label))
                .setView(view)
                .setPositiveButton(getString(R.string.ok_button), (dialogInterface, i) -> {
                    place.setPlaceName(capitalizeFirstChars(inputEditText.getText().toString()));
                    Date today = Calendar.getInstance().getTime();
                    @SuppressLint("SimpleDateFormat")
                    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    place.setDateSaved(formatter.format(today));
                    viewModel.insert(place);
                    savedListAdapter.notifyDataSetChanged();
                })
                .setNegativeButton(getString(R.string.cancel_button), (dialog1, which) -> {
                    // Like never happened
                    dialog1.dismiss();
                })
                .create();
        dialog.show();
    }

    /**
     * Capitalizer for string, put every first char as capital
     * @param str The String to parse
     */
    public native String capitalizeFirstChars(String str);

    /*
     * Library loading
     */
    static {
        System.loadLibrary("libmain_native_lib");
    }
}
