package com.example.app;

import com.example.app.interfaces.StoppableListIterator;
import com.google.android.libraries.places.api.model.Place;

import java.util.List;
import java.util.ListIterator;

/**
 * A normal iterator that can be stopped in the middle of an iteration
 */
public class StoppablePlaceIterator implements StoppableListIterator {

    ListIterator<Place> iterator;

    public StoppablePlaceIterator(List<Place> list){
        iterator = list.listIterator();
    }

    /**
     * Simply consume the list
     */
    @Override
    public void stopIteration() {
        //terrible but I really don't know what else do
        while(iterator.hasNext()){
            iterator.next();
        }
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Place next() {
        return iterator.next();
    }
}

