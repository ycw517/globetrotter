/*
 * Copyright (c) 2010, Sony Ericsson Mobile Communication AB. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 *
 *    * Redistributions of source code must retain the above copyright notice, this 
 *      list of conditions and the following disclaimer.
 *    * Redistributions in binary form must reproduce the above copyright notice,
 *      this list of conditions and the following disclaimer in the documentation
 *      and/or other materials provided with the distribution.
 *    * Neither the name of the Sony Ericsson Mobile Communication AB nor the names
 *      of its contributors may be used to endorse or promote products derived from
 *      this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.ece194.pan;

import android.view.MotionEvent;
import android.view.View;

/**
 * Simple on touch listener for zoom view
 */
public class PanTouchListener implements View.OnTouchListener, PanListener {

	/** Scaling factor to make touch less senstive */
    private static final float TOUCH_SCALE_FACTOR = 2.5f;
    
    /** State being controlled by touch events */
    private PanState mState;

    /** X-coordinate of previously handled touch event */
    private float mX;

    /** Y-coordinate of previously handled touch event */
    private float mY;

    /**
     * Sets the zoom state that should be controlled
     * 
     * @param state Zoom state
     */
    public void setPanState(PanState state) {
        mState = state;
    }

    // implements View.OnTouchListener
    public boolean onTouch(View v, MotionEvent event) {
        final int action = event.getAction();
        final float x = event.getX();
        final float y = event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mX = x;
                mY = y;
                break;

            case MotionEvent.ACTION_MOVE: {
                final float dx = (x - mX) / (v.getWidth() * TOUCH_SCALE_FACTOR);
                final float dy = (y - mY) / (v.getHeight() * TOUCH_SCALE_FACTOR);

                mState.setPanX(mState.getPanX() - dx);
                mState.setPanY(mState.getPanY() - dy);
                mState.notifyObservers();

                mX = x;
                mY = y;
                break;
            }

        }

        return true;
    }
    
    public void setOrientation(int orientconst) {
    	// do nothing
    }
    
    public void destroy() {
    	mState = null;
    }

}