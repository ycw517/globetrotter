// this should be revision 21
package com.ece194.globetrotter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class GlobeTrotter extends Activity {
	
	public final static int CAPTURE = 100;
	public final static int VIEWER = 200;
	LocationManager locationManager;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
    	// Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
	    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
	    //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);


        
     // Register the listener with the Location Manager to receive location updates

    }

    
	public void makeToast(View view) {
		Context context = getApplicationContext();
		int duration = Toast.LENGTH_SHORT;
		String text = "Don't touch me!";
		Toast.makeText(context, text, duration).show();
	}
	
	// Open the new screen.
	public void capture(View v){
	    // Start the activity whose result we want to retrieve.  The
	    // result will come back with request code GET_CODE.
	    Intent intent = new Intent(this, CameraActivity.class);
	    startActivityForResult(intent, CAPTURE);
	}

	public void view(View v){
	    // Start the activity whose result we want to retrieve.  The
	    // result will come back with request code GET_CODE.
	    Intent intent = new Intent(this, TagSelectorActivity.class);
	    startActivityForResult(intent, VIEWER);
	}
	
	public void debugViewer(View v){
	    // Start the activity whose result we want to retrieve.  The
	    // result will come back with request code GET_CODE.

	    Intent intent = new Intent(this, ViewerActivity.class);
	    startActivityForResult(intent, VIEWER);
	}

	
	// Listen for results.
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
	    // See which child activity is calling us back.
	    switch (resultCode) {
	        case CAPTURE:
	            // This is the standard resultCode that is sent back if the
	            // activity crashed or didn't doesn't supply an explicit result.
	            if (resultCode == RESULT_CANCELED){
	            	// toast? capture was cancelled
	            } 
	            else {
	            	// do something with (data)
	            }
	        case VIEWER:
	            // This is the standard resultCode that is sent back if the
	            // activity crashed or didn't doesn't supply an explicit result.
	            if (resultCode == RESULT_CANCELED){
	            	// toast? capture was cancelled
	            } 
	            else {
	            	// do something with (data)
	            }

	        default:
	            break;
	    }
	}
    
	
	
	// Define a listener that responds to location updates
	LocationListener locationListener = new LocationListener() {
	    public void onLocationChanged(Location location) {
	      // Called when a new location is found by the network location provider.
	     
	     Context context = getApplicationContext();
	     
	     Toast.makeText(context, "Location: " + Double.toString(location.getLongitude()) + ", " + Double.toString(location.getLatitude()) , Toast.LENGTH_LONG).show();

	    }

	    public void onStatusChanged(String provider, int status, Bundle extras) {}

	    public void onProviderEnabled(String provider) {}

	    public void onProviderDisabled(String provider) {}
	  };

    
}