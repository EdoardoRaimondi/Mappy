package com.example.app.ui.search;

/**
 * Search recycler view element
 * Place name + icon
 */
public class ItemAdapter {
    private int image;
    private String text;

    public void setImage(int image) {
        this.image = image;
    }
    public void setText(String text) {
        this.text = text;
    }
    public int getImage() {
        return image;
    }
    public String getText() {
        return text;
    }
}