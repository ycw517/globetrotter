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

    /** On touch listener for zoom view */
    private PanListener mPanListener;
    
    /** Determine which sensor to use */
    private int mListener = COMPASS_LISTENER;

    private void setSensorTo(int sensor) {
		if (mPanListener != null) {
			Log.v("setSensorTo", "destroying sensor, GOOD JOB");
			mPanListener.destroy();
			mPanListener = null;
		}
    	switch (sensor) {
    		case TOUCH_LISTENER:
    			mPanListener = new PanTouchListener();
    			break;
    		case COMPASS_LISTENER:
    			mPanListener = new PanCompassListener(getApplicationContext());
            	Configuration currConfig = getResources().getConfiguration();
            	mPanListener.setOrientation(currConfig.orientation);
    			break;
    	}
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
    		mPanListener.setOrientation(Configuration.ORIENTATION_LANDSCAPE);
    	else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
    		mPanListener.setOrientation(Configuration.ORIENTATION_PORTRAIT);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.viewer);
        
        
   //     ImageView image = (ImageView) findViewById(R.id.left_view);
   //     Bitmap bMap = BitmapFactory.decodeFile("/sdcard/globetrotter/panorama.jpg");
   //     image.setImageBitmap(bMap);


        mPanState = new PanState();

   //     mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image800x600);
        mBitmap = BitmapFactory.decodeFile("/sdcard/globetrotter/panorama.jpg");

        mImgView = (ImageViewer)findViewById(R.id.imgview);
        mImgView.setPanState(mPanState);
        mImgView.setImage(mBitmap);
        setSensorTo(mListener);
        
        mPanListener.setPanState(mPanState);
        mPanState.resetPanState();
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
        mPanListener.destroy();
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


