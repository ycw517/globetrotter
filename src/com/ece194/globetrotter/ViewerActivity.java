package com.ece194.globetrotter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.ece194.zoom.ImageZoomView;
import com.ece194.zoom.SimpleZoomListener;
import com.ece194.zoom.SimpleZoomListener.ControlType;
import com.ece194.zoom.ZoomState;

public class ViewerActivity extends Activity {
    
    /** Constant used as menu item id for setting zoom control type */
    private static final int MENU_ID_ZOOM = 0;

    /** Constant used as menu item id for setting pan control type */
    private static final int MENU_ID_PAN = 1;

    /** Constant used as menu item id for resetting zoom state */
    private static final int MENU_ID_RESET = 2;

    /** Image zoom view */
    private ImageZoomView mZoomView;

    /** Zoom state */
    private ZoomState mZoomState;

    /** Decoded bitmap image */
    private Bitmap mBitmap;

    /** On touch listener for zoom view */
    private SimpleZoomListener mZoomListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.viewer);
        
        
   //     ImageView image = (ImageView) findViewById(R.id.left_view);
   //     Bitmap bMap = BitmapFactory.decodeFile("/sdcard/globetrotter/panorama.jpg");
   //     image.setImageBitmap(bMap);


        mZoomState = new ZoomState();

   //     mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image800x600);
        mBitmap = BitmapFactory.decodeFile("/sdcard/globetrotter/panorama.jpg");
        
        mZoomListener = new SimpleZoomListener();
        mZoomListener.setZoomState(mZoomState);

        mZoomView = (ImageZoomView)findViewById(R.id.zoomview);
        mZoomView.setZoomState(mZoomState);
        mZoomView.setImage(mBitmap);
        mZoomView.setOnTouchListener(mZoomListener);

        resetZoomState();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mBitmap.recycle();
        mZoomView.setOnTouchListener(null);
        mZoomState.deleteObservers();
    }

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
                mZoomListener.setControlType(ControlType.ZOOM);
                break;

            case MENU_ID_PAN:
                mZoomListener.setControlType(ControlType.PAN);
                break;

            case MENU_ID_RESET:
                resetZoomState();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Reset zoom state and notify observers
     */
    private void resetZoomState() {
        mZoomState.setPanX(0.5f);
        mZoomState.setPanY(0.5f);
        mZoomState.setZoom(1f);
        mZoomState.notifyObservers();
    }
}


