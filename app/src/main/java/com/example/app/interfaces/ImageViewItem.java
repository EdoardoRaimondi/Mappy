package com.example.app.interfaces;

/**
 * Item of a recycler view that need an image view
 */
public interface ImageViewItem {

    /**
     * @param imageResource in drawable index
     */
    void setImage(int imageResource);

    /**
     * @return index of the image
     */
    int getImage();
}
