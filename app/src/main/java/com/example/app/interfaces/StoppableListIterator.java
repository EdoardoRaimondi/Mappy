package com.example.app.interfaces;

import java.util.Iterator;

public interface StoppableListIterator<T> extends Iterator<T> {

    /**
     * Stop the iterator
     */
    void stopIteration();

    /**
     * @return true if it has been stopped
     */
    boolean hasBeenStopped();
}
