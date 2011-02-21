package com.ece194.pan;

import java.util.Observable;

/**
 * A PanState holds pan values and allows the user to read and listen
 * to changes. Clients that modify PanState should call notifyObservers()
 */
public class PanState extends Observable {

    /**
     * Pan position x-coordinate X-coordinate of zoom window center position,
     * relative to the width of the content.
     */
    private float mPanX;

    /**
     * Pan position y-coordinate Y-coordinate of zoom window center position,
     * relative to the height of the content.
     */
    private float mPanY;

    // Public methods

    /**
     * Get current x-pan
     * 
     * @return current x-pan
     */
    public float getPanX() {
        return mPanX;
    }

    /**
     * Get current y-pan
     * 
     * @return Current y-pan
     */
    public float getPanY() {
        return mPanY;
    }

    /**
     * Set pan-x
     * 
     * @param panX Pan-x value to set
     */
    public void setPanX(float panX) {
        if (panX != mPanX) {
            mPanX = panX;
            setChanged();
        }
    }

    /**
     * Set pan-y
     * 
     * @param panY Pan-y value to set
     */
    public void setPanY(float panY) {
        if (panY != mPanY) {
            mPanY = panY;
            setChanged();
        }
    }
}