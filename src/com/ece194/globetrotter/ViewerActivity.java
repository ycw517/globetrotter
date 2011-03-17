package com.ece194.globetrotter;

import java.io.File;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.ece194.pan.ImageViewer;
import com.ece194.pan.PanCompassListener;
import com.ece194.pan.PanState;
import com.ece194.pan.PanTouchListener;

public class ViewerActivity extends Activity {
    
	/** Private const that names the touch listener */
    private static final int TOUCH_LISTENER = 0;

    /** Private const that names the compass listener */
    private static final int COMPASS_LISTENER = 1;
    
    /** Image zoom view */
    private ImageViewer mImgView;

    /** Zoom state */
    private PanState mPanState;

    /** Filename of picture being viewed */
	private String filename;
	
    /** Decoded bitmap image */
    private Bitmap mBitmap;

    /** Touch and compass listener for zoom view */
    private PanTouchListener mPanTouchListener;
    private PanCompassListener mPanCompassListener;
    
    /** Determine which sensor to use */
    private int mListener = TOUCH_LISTENER;

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
    	        // change listener variable
    	        mListener = TOUCH_LISTENER;
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
    			// always resume() to start listening
    			mPanCompassListener.resume();
    			// set the current pan state
    			mPanCompassListener.setPanState(mPanState);
    	        // change listener variable
    	        mListener = COMPASS_LISTENER;
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
        
    	String ns = Context.NOTIFICATION_SERVICE;
    	NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
    	mNotificationManager.cancelAll();

        
        
   //     ImageView image = (ImageView) findViewById(R.id.left_view);
   //     Bitmap bMap = BitmapFactory.decodeFile("/sdcard/globetrotter/panorama.jpg");
   //     image.setImageBitmap(bMap);
        
    	Bundle extras = getIntent().getExtras(); 
    	String userName;
    	if (extras != null) {
    	    filename = extras.getString("filename");
    	    // and get whatever type user account id is
    	} else {
    		filename = "/sdcard/globetrotter/mytags/mosaic.jpg";
    	}

    	
        mBitmap = BitmapFactory.decodeFile(filename);
        
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
    	MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.image_viewer_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBitmap != null)
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
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.toggleInput:
	    	if (mListener == COMPASS_LISTENER)
	    		setSensorTo(TOUCH_LISTENER);
	    	else
	    		setSensorTo(COMPASS_LISTENER);
	        break;
	    case R.id.tagRename:
	    	renameTag();
	        break;
	    case R.id.tagDelete:
	    	deleteTag();
	        break;
	    case R.id.sharePic:
	    	share();
	    case R.id.trotMenuButton:
	    	trot();
	    	break;
	    default:
	        break;
	    }
        return super.onOptionsItemSelected(item);
	}
    
    private void share() {
    	
    	Intent share = new Intent(Intent.ACTION_SEND);
    	share.setType("image/png");

    	share.putExtra(Intent.EXTRA_STREAM,
    	  Uri.parse("file://" + filename));

    	startActivity(Intent.createChooser(share, "Share Tag"));

    }
    
    private void renameTag() {
    	// get the base file name (not the full path)
    	String[] splitstr = filename.split("/");
    	String basefname = splitstr[splitstr.length-1];
    	
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);

    	alert.setTitle("Rename");
    	alert.setMessage("Enter a new filename");

    	// Set an EditText view to get user input 
    	final EditText input = new EditText(this);
    	input.setText(basefname);
    	alert.setView(input);

    	// Rename file
    	alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
    	public void onClick(DialogInterface dialog, int whichButton) {
    		String filename_new = input.getText().toString();
    		File orig = new File(filename);
    		File file_new = new File("/sdcard/globetrotter/mytags/"+filename_new);
    		if (orig.renameTo(file_new)) {
    			Toast toast = Toast.makeText(getApplicationContext(), "Successfully renamed to " + filename_new, Toast.LENGTH_SHORT);
    			toast.show();
    		}
    		else {
    			Toast toast = Toast.makeText(getApplicationContext(), "Error renaming", Toast.LENGTH_SHORT);
    			toast.show();
    		}
    	  }
    	});

    	alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    	  public void onClick(DialogInterface dialog, int whichButton) {
    	    // Canceled.
    	  }
    	});

    	alert.show();
    }
    
    private void deleteTag() {
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);

    	alert.setTitle("Delete panorama");
    	alert.setMessage("Are you sure you want to delete this image?");

    	// Delete file file
    	alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
    	public void onClick(DialogInterface dialog, int whichButton) {
    		File orig = new File(filename);
    		if (orig.delete()) {
    			Toast toast = Toast.makeText(getApplicationContext(), "Successfully deleted " + filename, Toast.LENGTH_SHORT);
    			toast.show();
    		}
    		else {
    			Toast toast = Toast.makeText(getApplicationContext(), "Error deleting", Toast.LENGTH_SHORT);
    			toast.show();
    		}
    	  }
    	});

    	alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    	  public void onClick(DialogInterface dialog, int whichButton) {
    	    // Canceled.
    	  }
    	});

    	alert.show();
    }
    
    public void trot() {
    	File dir = new File("/sdcard/globetrotter/mytags");
    	String[] TAGS = dir.list();
    	Random randgen = new Random();
    	filename = "/sdcard/globetrotter/mytags/" + TAGS[randgen.nextInt(TAGS.length)];
    	mBitmap = BitmapFactory.decodeFile(filename);
        // reset pan state
        mPanState.resetPanState();
    }
}


