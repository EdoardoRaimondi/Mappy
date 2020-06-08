package com.example.app.iterators;

import com.example.app.interfaces.StoppableListIterator;
import com.google.android.libraries.places.api.model.Place;

import java.util.List;
import java.util.ListIterator;

/**
 * A normal iterator that can be stopped in the middle of an iteration
 */
public class StoppablePlaceIterator implements StoppableListIterator {

    private ListIterator<Place> iterator;
    private boolean hasBeenStopped = false;


    public StoppablePlaceIterator(List<Place> list){
        iterator = list.listIterator();
    }

    /**
     * Simply consume the list
     */
    @Override
    public void stopIteration() {
        hasBeenStopped = true;
    }

    @Override
    public boolean hasBeenStopped() {
        return hasBeenStopped;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    /**
     * @return The Place if the iterator has not been stopped, null otherwise
     */
    @Override
    public Place next() {
        if(!hasBeenStopped) return iterator.next();
        return null;
    }
}

