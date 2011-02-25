package com.ece194.globetrotter;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ece194.pan.ImageViewer;
import com.ece194.pan.PanCompassListener;
import com.ece194.pan.PanListener;
import com.ece194.pan.PanState;
import com.ece194.pan.PanTouchListener;

public class ViewerActivity extends Activity {
    
	/** Private const that names the touch listener */
    private static final int TOUCH_LISTENER = 0;

    /** Private const that names the compass listener */
    private static final int COMPASS_LISTENER = 1;

    /** Menu item touch */
    private static final int MENU_TOUCH = 0;
    
    /** Menu item compass */
    private static final int MENU_COMPASS = 1;
    
    /** Image zoom view */
    private ImageViewer mImgView;

    /** Zoom state */
    private PanState mPanState;

    /** Decoded bitmap image */
    private Bitmap mBitmap;

    /** Touch and compass listener for zoom view */
    private PanTouchListener mPanTouchListener;
    private PanCompassListener mPanCompassListener;
    
    /** Determine which sensor to use */
    private int mListener = COMPASS_LISTENER;

    private void setSensorTo(int sensor) {
    	switch (sensor) {
    		case TOUCH_LISTENER:
    			// if a compass listener exists, stop it from sensing
    			if (mPanCompassListener != null)
    				mPanCompassListener.stop();
    			// if a touch listener doesn't exist, create one
    			if (mPanTouchListener == null)
    				mPanTouchListener = new PanTouchListener();
    			// turn on the listener and set the current pan state
    			mImgView.setOnTouchListener(mPanTouchListener);
    			mPanTouchListener.setPanState(mPanState);
    			break;
    		case COMPASS_LISTENER:
    			// turn off the touch listener
    			mImgView.setOnTouchListener(null);
    			// if a compass listener doesn't exist, create one
    			if (mPanCompassListener == null) {
    				mPanCompassListener = new PanCompassListener(getApplicationContext());
            		Configuration currConfig = getResources().getConfiguration();
            		mPanCompassListener.setOrientation(currConfig.orientation);
    			}
    			else // otherwise, just resume the old one
    				mPanCompassListener.resume();
    			// set the current pan state
    			mPanCompassListener.setPanState(mPanState);
    			break;
    	}
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
    	if (mPanCompassListener != null) {
    		if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
    			mPanCompassListener.setOrientation(Configuration.ORIENTATION_PORTRAIT);
    		else
    			mPanCompassListener.setOrientation(Configuration.ORIENTATION_LANDSCAPE);
    	}
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.viewer);
        
   //     ImageView image = (ImageView) findViewById(R.id.left_view);
   //     Bitmap bMap = BitmapFactory.decodeFile("/sdcard/globetrotter/panorama.jpg");
   //     image.setImageBitmap(bMap);
        
        mBitmap = BitmapFactory.decodeFile("/sdcard/globetrotter/panorama.jpg");
        
        // create a new pan state
        mPanState = new PanState();
        mPanState.resetPanState();
        // set up the image viewer 
        mImgView = (ImageViewer)findViewById(R.id.imgview);
        mImgView.setPanState(mPanState);
        mImgView.setImage(mBitmap);
    	mImgView.setPanState(mPanState);
        setSensorTo(mListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, MENU_TOUCH, 0, R.string.menu_touch);
        menu.add(Menu.NONE, MENU_COMPASS, 1, R.string.menu_compass);
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();

        mBitmap.recycle();
        mImgView.setOnTouchListener(null);
        mPanState.deleteObservers();
        if (mPanCompassListener != null)
        	mPanCompassListener.destroy();
        mPanCompassListener = null;
        mPanTouchListener = null;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_TOUCH:
                setSensorTo(TOUCH_LISTENER);
                break;
            case MENU_COMPASS:
                setSensorTo(COMPASS_LISTENER);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}


