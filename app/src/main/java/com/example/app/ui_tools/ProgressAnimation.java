package com.example.app.ui_tools;

import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ProgressBar;

/**
 * ProgressAnimation class for basic progress animation
 */
public class ProgressAnimation extends Animation {

    // Object params
    private ProgressBar progressBar;
    private float from;
    private float  to;

    /**
     * @param progressBar The ProgressBar to animate
     * @param from        Integer percentage to start
     * @param to          Integer percentage to stop
     */
    public ProgressAnimation(ProgressBar progressBar, float from, float to) {
        super();
        this.progressBar = progressBar;
        this.from = from;
        this.to = to;
    }

    /**
     * @param interpolatedTime The time in millis from one step to another
     * @param t Transformation selected or default
     */
    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        float value = from + (to - from) * interpolatedTime;
        progressBar.setProgress((int) value);
    }

}
