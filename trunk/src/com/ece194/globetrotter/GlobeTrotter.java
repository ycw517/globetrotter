// this should be revision 21
package com.ece194.globetrotter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;



public class GlobeTrotter extends Activity {
	
	public String project_name = CameraActivity.project_name;
	
//	public String project_name = "globetrotter-test-08";
	
	public final static int CAPTURE = 100;
	public final static int VIEWER = 200;
	public final static int LIST = 300;
	
	LocationManager locationManager;
	
	String ns = Context.NOTIFICATION_SERVICE;
	NotificationManager mNotificationManager;
	Notification notification;
	private static final int HELLO_ID = 1;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
    	// Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
	    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
	    //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

    	mNotificationManager = (NotificationManager) getSystemService(ns);
    	int icon = R.drawable.view_normal;
    	CharSequence tickerText = "Your panorama is ready";
    	long when = System.currentTimeMillis();
    	notification = new Notification(icon, tickerText, when);
    	Context context = getApplicationContext();
    	CharSequence contentTitle = "GlobeTrotter";
    	CharSequence contentText = "Your panorama is ready! Touch to view.";
    	Intent notificationIntent = new Intent(this, CameraActivity.class);
    	PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
    	notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

        
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
	    startActivityForResult(intent, LIST);
	}
	
	public void debugViewer(View v){
	    // Start the activity whose result we want to retrieve.  The
	    // result will come back with request code GET_CODE.

	    Intent intent = new Intent(this, ViewerActivity.class);
	    startActivityForResult(intent, VIEWER);
	}

	
	// Listen for results.
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		
	    switch (requestCode) {
	        case CAPTURE:
	            if (resultCode == RESULT_CANCELED){
	            } 
	            else {
	            	Toast.makeText(getApplicationContext(), "We will notify you when the picture is ready!", Toast.LENGTH_LONG).show();
	            }
	        case VIEWER:
	            if (resultCode == RESULT_CANCELED){
	            } 
	            else {
	            	Toast.makeText(getApplicationContext(), "VIEWER RESULT", Toast.LENGTH_LONG).show();
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

    
	
	
	private class DownloadFilesTask extends AsyncTask<Void, Integer, Void> {
		boolean fail = false;
	
	    protected Void doInBackground(Void... urls) {
	    	int response = 0;
	    	while (response != 100) {
	    		
	            try {
	                Thread.currentThread().sleep(2000);
	                }
	              catch (InterruptedException e) {
	                e.printStackTrace();
	                }
	    		
	    	try {
	            HttpClient client = new DefaultHttpClient();  
	            String getURL = "http://dragonox.cs.ucsb.edu/Mosaic3D/uploads/"+ project_name +"/status.txt";
	            HttpGet get = new HttpGet(getURL);
	            HttpResponse responseGet = client.execute(get);  
	            HttpEntity resEntityGet = responseGet.getEntity();  
	            if (resEntityGet != null) {  
	            	response = Integer.valueOf(EntityUtils.toString(resEntityGet).trim());
	                Log.i("GET RESPONSE","RESPONSE IS = " + response);
	       //         publishProgress(response);
	            }
	        } catch (Exception e) {
	            Log.i("Y U NO WORK?",e.toString());
	        }
	        
	    	}
			return null;
	    }
	
	    protected void onPreExecute() {
	    }
	
	    protected void onPostExecute(Void blah) {
	    	
	    	mNotificationManager.notify(HELLO_ID, notification);
	
	    	//finish(); // this could also start an intent to another activity, like a list, and then finish here
	    }
	}
	
		  
	  
}