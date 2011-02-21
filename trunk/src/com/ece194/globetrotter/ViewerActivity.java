package com.ece194.globetrotter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.ece194.pan.*;

public class ViewerActivity extends Activity {
    
    /** Constant used as menu item id for setting zoom control type */
    private static final int MENU_ID_ZOOM = 0;

    /** Constant used as menu item id for setting pan control type */
    private static final int MENU_ID_PAN = 1;

    /** Constant used as menu item id for resetting zoom state */
    private static final int MENU_ID_RESET = 2;

    /** Image zoom view */
    private ImageViewer mImgView;

    /** Zoom state */
    private PanState mPanState;

    /** Decoded bitmap image */
    private Bitmap mBitmap;

    /** On touch listener for zoom view */
    private PanTouchListener mPanListener;

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
        
        mPanListener = new PanTouchListener();
        mPanListener.setPanState(mPanState);

        mImgView = (ImageViewer)findViewById(R.id.zoomview);
        mImgView.setPanState(mPanState);
        mImgView.setImage(mBitmap);
        mImgView.setOnTouchListener(mPanListener);

        resetPanState();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mBitmap.recycle();
        mImgView.setOnTouchListener(null);
        mPanState.deleteObservers();
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, MENU_ID_ZOOM, 0, R.string.menu_zoom);
        menu.add(Menu.NONE, MENU_ID_PAN, 1, R.string.menu_pan);
        menu.add(Menu.NONE, MENU_ID_RESET, 2, R.string.menu_reset);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ID_ZOOM:
                mPanListener.setControlType(ControlType.ZOOM);
                break;

            case MENU_ID_PAN:
                mPanListener.setControlType(ControlType.PAN);
                break;

            case MENU_ID_RESET:
                resetPanState();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
*/
    /**
     * Reset zoom state and notify observers
     */
    private void resetPanState() {
        mPanState.setPanX(0.5f);
        mPanState.setPanY(0.5f);
        mPanState.notifyObservers();
    }
}


