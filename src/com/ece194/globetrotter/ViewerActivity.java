package com.ece194.globetrotter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.ece194.pan.*;

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
    private int mListener = 0;

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
        
        if (mListener == TOUCH_LISTENER) {
            mPanListener = new PanTouchListener();	
        }
        else if (mListener == COMPASS_LISTENER) {
        	mPanListener = new PanCompassListener(getApplicationContext());
        }
        mPanListener.setPanState(mPanState);

        mImgView = (ImageViewer)findViewById(R.id.zoomview);
        mImgView.setPanState(mPanState);
        mImgView.setImage(mBitmap);
        if (mListener == TOUCH_LISTENER) {
        	mImgView.setOnTouchListener((PanTouchListener)mPanListener);
        }
        else if (mListener == COMPASS_LISTENER) {
        	// TODO: do the compass setup?
        }

        mPanState.resetPanState();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mBitmap.recycle();
        mImgView.setOnTouchListener(null);
        mPanState.deleteObservers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, MENU_TOUCH, 0, R.string.menu_touch);
        menu.add(Menu.NONE, MENU_COMPASS, 1, R.string.menu_compass);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_TOUCH:
                mListener = TOUCH_LISTENER;
                break;

            case MENU_COMPASS:
                mListener = COMPASS_LISTENER;
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}


