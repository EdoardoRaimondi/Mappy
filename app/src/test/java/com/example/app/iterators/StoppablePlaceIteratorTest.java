package com.example.app.iterators;

import com.google.android.libraries.places.api.model.Place;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class StoppablePlaceIteratorTest {

    private final static String ID_1 = "id_1";
    private final static String ID_2 = "id_2";
    private final static String ID_3 = "id_3";
    private static Place PLACE_1 = Place.builder()
            .setId(ID_1)
            .build();
    private static Place PLACE_2 = Place.builder()
            .setId(ID_2)
            .build();
    private static Place PLACE_3 = Place.builder()
            .setId(ID_3)
            .build();
    private final List<Place> dummyList = new ArrayList<>();
    private StoppablePlaceIterator iterator = new StoppablePlaceIterator(dummyList);

    @Before
    public void setUp(){
        dummyList.add(PLACE_1);
        dummyList.add(PLACE_2);
        dummyList.add(PLACE_3);
    }

    /**
     * Assert iteration has been stopped in the middle of that
     */
    @Test
    public void stopIteration() {
        int i = 0;
        while(iterator.hasNext() && !iterator.hasBeenStopped()){
            if(i++ == 1) iterator.stopIteration();
        }
        assertEquals(i, 2);
    }

    @Test
    public void hasBeenStopped_Stopped() {
        boolean stopped = false;
        iterator.stopIteration();
        if(iterator.hasBeenStopped()) stopped = true;
        assertTrue(stopped);
    }

    @Test
    public void hasBeenStopped_NotStopped() {
        boolean stopped = false;
        if(iterator.hasBeenStopped()) stopped = true;
        assertFalse(stopped);
    }

    @Test
    public void hasNext() {
        assertTrue(iterator.hasNext());
    }

    @Test
    public void hasNext_No() {
        StoppablePlaceIterator iterator_1 = new StoppablePlaceIterator(dummyList);
        while (iterator_1.hasNext()) {
            iterator_1.next();
        }
        assertFalse(iterator_1.hasNext());
    }

    @Test
    public void next_AfterStopped(){
        StoppablePlaceIterator iterator_1 = new StoppablePlaceIterator(dummyList);
        iterator_1.stopIteration();
        assertNull(iterator_1.next());
    }

    @Test
    public void next_BeforeStopped(){
        StoppablePlaceIterator iterator_1 = new StoppablePlaceIterator(dummyList);
        Place first_place = iterator_1.next();
        iterator_1.stopIteration();
        assertEquals(first_place.getId(), PLACE_1.getId());
    }

    @Test
    public void next_DifferentIterators_SameList(){
        StoppablePlaceIterator iterator_1 = new StoppablePlaceIterator(dummyList);
        StoppablePlaceIterator iterator_2 = new StoppablePlaceIterator(dummyList);
        assertEquals(iterator_1.next().getId(), iterator_2.next().getId());
    }

    @Test
    public void next_DifferentIterators_OneStopped(){
        StoppablePlaceIterator iterator_1 = new StoppablePlaceIterator(dummyList);
        StoppablePlaceIterator iterator_2 = new StoppablePlaceIterator(dummyList);
        iterator_2.stopIteration();
        assertEquals(iterator_1.next().getId(), PLACE_1.getId());
    }

    @Test
    public void next_DifferentIterators_TwoStopped(){
        StoppablePlaceIterator iterator_1 = new StoppablePlaceIterator(dummyList);
        StoppablePlaceIterator iterator_2 = new StoppablePlaceIterator(dummyList);
        iterator_2.stopIteration();
        iterator_1.stopIteration();
        assertNull(iterator_1.next());
        assertNull(iterator_2.next());
    }

}