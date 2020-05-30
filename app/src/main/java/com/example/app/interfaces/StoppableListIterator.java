package com.example.app.interfaces;

import java.util.Iterator;

public interface StoppableListIterator<T> extends Iterator<T> {

    /**
     * Stop the iteration in a while
     */
    void stopIteration();
}
