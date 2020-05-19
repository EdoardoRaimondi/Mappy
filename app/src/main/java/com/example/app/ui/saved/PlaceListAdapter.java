package com.example.app.ui.saved;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.app.R;
import com.example.app.factories.IntentFactory;
import com.example.app.finals.NearbyRequestType;

import java.util.LinkedList;


public class PlaceListAdapter extends
        RecyclerView.Adapter<PlaceListAdapter.WordViewHolder> {

    private final LinkedList<String> savedPlacesList;
    private RequestPlaceAdapter requestPlaceAdapter;
    private final LayoutInflater mInflater;
    private Context context;

    /**
     * Adapter constructor
     * @param context the context of the caller
     * @param wordList the list to adapt
     */
    public PlaceListAdapter(Context context, LinkedList<String> wordList) {
        mInflater = LayoutInflater.from(context);
        requestPlaceAdapter = new RequestPlaceAdapter();
        this.context = context;
        this.savedPlacesList = wordList;
    }

    class WordViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        public final TextView wordItemView;
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
            this.mAdapter = adapter;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            // Get the position of the item that was clicked.
            int mPosition = getLayoutPosition();

            // Use that to access the affected item in mWordList.
            String element = savedPlacesList.get(mPosition);
            // Change the word in the mWordList
            context.startActivity(IntentFactory.createNearbyRequestIntent(context, RequestPlaceAdapter.getAdaptedPlace(element), 1000));
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
        String mCurrent = savedPlacesList.get(position);
        // Add the data to the view holder.
        holder.wordItemView.setText(mCurrent);
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
