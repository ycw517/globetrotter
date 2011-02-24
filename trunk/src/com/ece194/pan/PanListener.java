package com.ece194.pan;

public interface PanListener {
    public void destroy();
    public void setOrientation(int orientconst);
    public void setPanState(PanState state);
}