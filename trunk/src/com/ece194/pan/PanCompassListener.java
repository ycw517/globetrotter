package com.ece194.pan;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class PanCompassListener implements SensorEventListener {
	
	/** Scaling factor to make touch less sensitive */
    private static final float COMPASS_THRESHOLD_FACTOR = 5.f;
    
    /** State being controlled by touch events */
    private PanState mState;

    /** Heading (in degrees) of previously handled touch event */
    private float mHeading = 180.f;

    /** Sensor manager */
    private SensorManager mSensorManager;
    
    /** Determine which sensor reading to use */
    private int mScreenOrientation;

    /** Consts for mScreenOrientation */
    private static final int ORIENTATION_LANDSCAPE = 2;
    private static final int ORIENTATION_PORTRAIT = 1;
    
    /** Data holders */
    private float[] mGData = new float[3];
    private float[] mMData = new float[3];
    private float[] mR = new float[16];
    private float[] mI = new float[16];
    private float[] mOrientation = new float[3];
    private final float rad2deg = (float)(180.0f/Math.PI);
    
    public PanCompassListener(Context cntxt) {
        mSensorManager = (SensorManager)cntxt.getSystemService(Context.SENSOR_SERVICE);
        //resume();
    }
    
    public void resume() {
        Sensor gsensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor msensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorManager.registerListener(this, gsensor, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, msensor, SensorManager.SENSOR_DELAY_UI);
    }
    
    public void stop() {
        mSensorManager.unregisterListener(this);
        Log.v("PanCompassListener", "unregistered the listener, GOOD JOB");
    }
    
    public void destroy() {
    	stop();
    	//mState = null;
    	mSensorManager = null;
    	mGData = null;
    	mR = null;
    	mR = null;
    	mOrientation = null;
    }
    
    public void setPanState(PanState state) {
        mState = state;
    }

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// do nothing
	}
	
	public void setOrientation(int orient) {
		if (orient == ORIENTATION_LANDSCAPE)
			mScreenOrientation = ORIENTATION_LANDSCAPE;
		else if (orient == ORIENTATION_PORTRAIT)
			mScreenOrientation = ORIENTATION_PORTRAIT;
	}

	public void onSensorChanged(SensorEvent event) {
		int type = event.sensor.getType();
        float[] data;
        if (type == Sensor.TYPE_ACCELEROMETER) {
            data = mGData;
        } else if (type == Sensor.TYPE_MAGNETIC_FIELD) {
            data = mMData;
        } else {
            // we should not be here.
            return;
        }
        for (int i=0 ; i<3 ; i++)
            data[i] = event.values[i];
        

		// get sensor data
        SensorManager.getRotationMatrix(mR, mI, mGData, mMData);
        SensorManager.getOrientation(mR, mOrientation);
        //float incl = SensorManager.getInclination(mI);

        /*Log.d("Compass", "yaw: " + (int)(mOrientation[0]*rad2deg) +
        	"  pitch: " + (int)(mOrientation[1]*rad2deg) +
        	"  roll: " + (int)(mOrientation[2]*rad2deg)
         	);*/
        
        float newHeading;
        if (mScreenOrientation == ORIENTATION_LANDSCAPE) {
        	newHeading = mOrientation[0]*rad2deg + 180.f;
        	//Log.v("PanCompassListener", "landscape heading: "+mHeading);
        }
        else {
        	newHeading = mOrientation[2]*rad2deg + 180.f;
        	//Log.v("PanCompassListener", "portrait heading: "+mHeading);
        }
        
        if (Math.abs(newHeading-mHeading) > COMPASS_THRESHOLD_FACTOR) {
        	//Log.v("heading", "new heading is: " + newHeading);
        	mHeading = newHeading;
        	mState.setPanX(mHeading/360.f);
        	mState.notifyObservers();
        }
	}
}