package com.example.app.ui.search;

import com.example.app.finals.SearchablePlace;
import com.example.app.interfaces.ImageViewItem;

/**
 * Search recycler view element
 * Place name + icon
 */
public class SearchItem implements ImageViewItem {
    private int image;
    private SearchablePlace type;

    public void setImage(int image) {
        this.image = image;
    }
    public void setType(SearchablePlace type) {
        this.type = type;

    }
    public int getImage() {
        return image;
    }
    public SearchablePlace getType() {
        return type;
    }
}