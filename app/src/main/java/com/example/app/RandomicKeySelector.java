package com.example.app;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Random;

/**
 * Class to select a random google api key
 */
public class RandomicKeySelector {

    private ArrayList<String> keys;

    public RandomicKeySelector(@NonNull ArrayList<String> keys){
        this.keys = keys;
    }

    /**
     * Method to get a random key
     * @return the key
     */
    public String selectKey() {
        Random random = new Random();
        int randomIndex = random.nextInt(keys.size());
        return keys.get(randomIndex);

    }
}
