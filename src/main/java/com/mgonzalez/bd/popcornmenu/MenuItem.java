package com.mgonzalez.bd.popcornmenu;

import android.widget.ImageView;

/**
 * Created by mgonzalez on 15/12/15.
 */
public class MenuItem {

    private ImageView imageView;
    private float offset;

    public MenuItem() {
        this.imageView = null;
        this.offset = 0.0f;
    }

    public MenuItem(ImageView imageView) {
        this.imageView = imageView;
        this.offset = 0.0f;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public float getOffset() {
        return offset;
    }

    public void setOffset(float offset) {
        this.offset = offset;
    }
}
