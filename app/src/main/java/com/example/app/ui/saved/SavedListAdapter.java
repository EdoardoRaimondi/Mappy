package com.example.app.ui.saved;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.R;
import com.example.app.factories.IntentFactory;
import com.example.app.saved_place_database.SavedPlace;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;

import java.util.List;

/**
 * Adapter of the {@link SavedFragment} recycler view
 */
public class SavedListAdapter extends
        RecyclerView.Adapter<SavedListAdapter.SavedViewHolder> {

    // Private members
    private List<SavedPlace> savedPlacesList;
    private final LayoutInflater mInflater;
    private Context context;

    /**
     * Adapter constructor
     * @param context The Context of the caller
     */
    SavedListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
    }


    /**
     * View holder class.
     * Coordinate all recycler view and items changes
     */
    class SavedViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{
        // Private members
        private final TextView savedItemView;
        private final TextView dateView;
        final SavedListAdapter mAdapter;

        /**
         * Creates a new custom view holder to hold the view to display in
         * the RecyclerView.
         * @param itemView The view in which to display the data.
         * @param adapter  The adapter that manages the the data and views
         *                for the RecyclerView.
         */
        SavedViewHolder(View itemView, SavedListAdapter adapter) {
            super(itemView);
            savedItemView = itemView.findViewById(R.id.place);
            dateView = itemView.findViewById(R.id.date);
            this.mAdapter = adapter;
            itemView.setOnClickListener(this);
        }

        /**
         * Callback when user click an item
         * Start {@link com.example.app.SavedPlaceActivity} and
         * show selected place on them
         * @param view The View clicked
         */
        @Override
        public void onClick(View view) {

            SavedPlace selectedPlace = getCLickedElement();
            Place place = Place.builder()
                    .setName(selectedPlace.getPlaceName())
                    .setLatLng(new LatLng(selectedPlace.getLatitude(), selectedPlace.getLongitude()))
                    .build();
            Intent intent = IntentFactory.createPlaceInfoIntent(context, place);
            context.startActivity(intent);
        }

        /**
         * Callback of the clicked saved Place
         * @return The List reference clicked/long clicked by the user
         */
        private SavedPlace getCLickedElement(){
            // Get the position of the item that was clicked.
            int mPosition = getLayoutPosition();
            // Use that to access the affected item in mWordList.
            return savedPlacesList.get(mPosition);

        }
    }


    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to
     * represent an item.
     *
     * This new ViewHolder should be constructed with a new View that can
     * represent the items of the given type. You can either create a new View
     * manually or inflate it from an XML layout file.
     *
     * The new ViewHolder will be used to display items of the adapter using
     * onBindViewHolder(ViewHolder, int, List). Since it will be reused to
     * display different items in the data set, it is a good idea to cache
     * references to sub views of the View to avoid unnecessary findViewById()
     * calls.
     * @param parent   The ViewGroup into which the new View will be added after
     *                 it is bound to an adapter position.
     * @param viewType The view type of the new View. @return A new ViewHolder
     *                 that holds a View of the given view type.
     */
    @NonNull
    @Override
    public SavedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate an item view.
        View mItemView = mInflater.inflate(R.layout.savedlist_item, parent, false);
        return new SavedViewHolder(mItemView, this);
    }

    /**
     * Method called when the data are modified
     * (user add/delete some places)
     * @param placeList The List of Place
     */
    public void setPlace(List<SavedPlace> placeList){
        savedPlacesList = placeList;
        notifyDataSetChanged();
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * This method should update the contents of the ViewHolder.itemView to
     * reflect the item at the given position.
     * @param holder   The ViewHolder which should be updated to represent
     *                 the contents of the item at the given position in the
     *                 data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull SavedViewHolder holder, int position) {
        if(savedPlacesList != null) {
            // Retrieve the data for that position.
            SavedPlace mCurrent = savedPlacesList.get(position);
            // Add the data to the view holder.
            holder.savedItemView.setText(mCurrent.getPlaceName());
            holder.dateView.setText(mCurrent.getDateSaved());
        }
        else {
            holder.savedItemView.setText(context.getResources().getString(R.string.text_for_save));
        }
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        if (savedPlacesList != null) {
            return savedPlacesList.size();
        }
        else {
            return 0;
        }
    }

    /**
     * @param position Integer position in the View
     * @return the saved place at a determinate position
     */
    SavedPlace getSavedPlaceAt(int position){
        return savedPlacesList.get(position);
    }
}

