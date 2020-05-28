package com.example.app.ui.search;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.app.R;
import com.example.app.factories.IntentFactory;
import com.example.app.finals.SearchablePlace;

import java.util.LinkedList;


public class PlaceListAdapter extends
        RecyclerView.Adapter<PlaceListAdapter.WordViewHolder> {

    private final LinkedList<ItemAdapter> savedPlacesList;
    private final LayoutInflater mInflater;
    private Context context;
    private int radius;

    /**
     * Adapter constructor
     * @param context the context of the caller
     * @param itemList the list to adapt
     * @param radius of research
     */
    public PlaceListAdapter(Context context, LinkedList<ItemAdapter> itemList, int radius) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.radius = radius;
        this.savedPlacesList = itemList;
    }

    class WordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView wordItemView;
        final ImageView imageItemView;
        final PlaceListAdapter mAdapter;

        /**
         * Creates a new custom view holder to hold the view to display in
         * the RecyclerView.
         *
         * @param itemView The view in which to display the data.
         * @param adapter The adapter that manages the the data and views
         *                for the RecyclerView.
         */
        public WordViewHolder(View itemView, PlaceListAdapter adapter) {
            super(itemView);
            wordItemView = itemView.findViewById(R.id.place);
            imageItemView = itemView.findViewById(R.id.img_item);
            this.mAdapter = adapter;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            // Get the position of the item that was clicked.
            int mPosition = getLayoutPosition();
            RequestPlaceAdapter requestPlaceAdapter = new RequestPlaceAdapter();

            //take the recycler view text
            SearchablePlace placeType = savedPlacesList.get(mPosition).getType();
            //nearby searching
            context.startActivity(IntentFactory.createNearbyRequestIntent(context, requestPlaceAdapter.getAdaptedPlace(placeType), radius));
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
     *
     * @param parent   The ViewGroup into which the new View will be added after
     *                 it is bound to an adapter position.
     * @param viewType The view type of the new View. @return A new ViewHolder
     *                 that holds a View of the given view type.
     */
    @Override
    public PlaceListAdapter.WordViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        // Inflate an item view.
        View mItemView = mInflater.inflate(
                R.layout.placelist_item, parent, false);
        return new WordViewHolder(mItemView, this);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * This method should update the contents of the ViewHolder.itemView to
     * reflect the item at the given position.
     *
     * @param holder   The ViewHolder which should be updated to represent
     *                 the contents of the item at the given position in the
     *                 data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(PlaceListAdapter.WordViewHolder holder,
                                 int position) {
        // Retrieve the data for that position.
        String currentName = savedPlacesList.get(position).getType().toString();
        int currentImage   = savedPlacesList.get(position).getImage();
        // Add the data to the view holder.
        holder.wordItemView.setText(currentName);
        holder.imageItemView.setImageResource(currentImage);
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return savedPlacesList.size();
    }
}
