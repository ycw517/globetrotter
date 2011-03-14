// this should be revision 21
package com.ece194.globetrotter;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;
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
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class GlobeTrotter extends Activity {
	
	
	public static String timestamp;
	public static String project_name; 

	public static String longitude;
	public static String latitude;
		
	public final static int CAPTURE = 100;
	public final static int VIEWER = 200;
	public final static int LIST = 300;
	
	LocationManager locationManager;
	
	String ns = Context.NOTIFICATION_SERVICE;
	NotificationManager mNotificationManager;
	Notification notification;
	private static final int HELLO_ID = 1;
	private int notificationIcon;
	
	private Intent notificationIntent;
	private PendingIntent contentIntentSuccess;
	private PendingIntent contentIntentFailure;

	private Context context;

	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
    	// Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
	    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
	    //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

	    
	    /* Status Bar Notification Initializations */
    	mNotificationManager = (NotificationManager) getSystemService(ns);
    	mNotificationManager.cancelAll();

    	notificationIcon = R.drawable.view_normal;
    	CharSequence ticker = "Globetrotter Panorama Status";
    	long when = System.currentTimeMillis();
    	notification = new Notification(notificationIcon, ticker, when);
    	context = getApplicationContext();

    }

	@Override
    public void onPause() {
		super.onPause();
    	locationManager.removeUpdates(locationListener);
    }
    
    @Override
    public void onResume() {
    	super.onResume();
	    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }
	
	
    /* Dashboard Actions */
    
   	public void capture(View v){
   		
	    project_name = "globetrotter-" + getDateTime(); //for timestamping the project 
	    

    	notificationIntent = new Intent(this, ViewerActivity.class);
    	notificationIntent.putExtra("filename","/sdcard/globetrotter/mytags/"+ project_name+".jpg");
    	contentIntentSuccess = PendingIntent.getActivity(this, 0, notificationIntent, 0);
    	contentIntentFailure = PendingIntent.getActivity(this, 0, new Intent(this, GlobeTrotter.class), 0);

	        	
	    ((TextView) findViewById(R.id.project_name)).setText(project_name);
	    Intent intent = new Intent(this, CameraActivity.class);
	    startActivityForResult(intent, CAPTURE);
	}
	
	public void view(View v){
	    Intent intent = new Intent(this, TagSelectorActivity.class);
	    startActivityForResult(intent, LIST);
	}
	
	public void debugViewer(View v){
	    Intent intent = new Intent(this, ViewerActivity.class);
	    startActivityForResult(intent, VIEWER);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		
	    switch (requestCode) {
	        case CAPTURE:
	            if (resultCode == RESULT_CANCELED){
	            } 
	            else {
	        		new DownloadPanoramaTask().execute();
	            	Toast.makeText(getApplicationContext(), "We will notify you when the picture is ready!", Toast.LENGTH_LONG).show();
	            }
	        case VIEWER:
	            if (resultCode == RESULT_CANCELED){
	            } 
	            else {
	            //	Toast.makeText(getApplicationContext(), "VIEWER RESULT", Toast.LENGTH_LONG).show();
	            }
	        default:
	            break;
	    }
	}
    
	
	/* GeoLocation Listeners */
	
	LocationListener locationListener = new LocationListener() {
	    public void onLocationChanged(Location location) {
		     longitude = Location.convert(location.getLatitude(), Location.FORMAT_SECONDS);
		     latitude = Location.convert(location.getLongitude(), Location.FORMAT_SECONDS);
			//     Toast.makeText(getApplicationContext(), "Location: " + Double.toString(location.getLatitude()) + ", " + Double.toString(location.getLongitude()) , Toast.LENGTH_LONG).show();
	    }

	    public void onStatusChanged(String provider, int status, Bundle extras) {}
	    public void onProviderEnabled(String provider) {}
	    public void onProviderDisabled(String provider) {}
	    
	  };

	   	public String getDateTime() {
	        DateFormat dateFormat = new SimpleDateFormat("y-M-d-H-m-s");
	        Date date = new Date();
	        return dateFormat.format(date);
	    }

	
	  /* Download the actual image */
	   	
	  public class DownloadPanoramaTask extends AsyncTask<Void, Integer, Void> {
		  boolean fail = false;
		  int attempts = 0;

		  protected Void doInBackground(Void... urls) {
			  int response = 0;
			  
			  while (response != 100) {
	    		
				  try { Thread.currentThread();
				Thread.sleep(2000); }
				  catch (InterruptedException e) { e.printStackTrace(); }
		            
				  try {
					  HttpClient client = new DefaultHttpClient();  
					  String getURL = "http://dragonox.cs.ucsb.edu/Mosaic3D/uploads/"+ project_name +"/status.txt";
					  HttpGet get = new HttpGet(getURL);
					  HttpResponse responseGet = client.execute(get);  
					  HttpEntity resEntityGet = responseGet.getEntity();  
					  if (resEntityGet != null) {  
						  response = Integer.valueOf(EntityUtils.toString(resEntityGet).trim());
						  Log.v("Globetrotter ", response + "%");

						  if (response == -1) {
							  fail = true; 
							  break; 
						  }
					  }
				  	} catch (Exception e) { Log.e("Globetrotter","Attempt: " + attempts++); 
				  	
					  if (attempts == 3) {
						  fail = true; 
						  break; 
					  }
				  }
			  }

			  if (!fail && response == 100) 
				  DownloadFromUrl(); 

			  return null;
		  }
	
	    protected void onPreExecute() {
	    }
	
	    protected void onPostExecute(Void blah) {
	    	
	    	if (fail){
	            Log.e("Globetrotter", "Download failed.");

	        	CharSequence contentTitle = "GlobeTrotter";
	        	CharSequence contentText = "Panorama failed! Please try again.";
	        	notification.setLatestEventInfo(context, contentTitle, contentText, contentIntentFailure);

	    	
	    	}
	    	else {
	            Log.i("Globetrotter", "Download successful.");
	            
	        	CharSequence contentTitle = "GlobeTrotter";
	        	CharSequence contentText = "Your panorama is ready! Touch here to view your panorama!";
	        	notification.setLatestEventInfo(context, contentTitle, contentText, contentIntentSuccess);
	    	}
	    	
    		mNotificationManager.notify(HELLO_ID, notification);

	    	
	    }
	    

	    public void DownloadFromUrl() { 
	    	
    		String filename = "/sdcard/globetrotter/mytags/"+project_name+".jpg";
	    	
            try {
                URL url = new URL("http://dragonox.cs.ucsb.edu/Mosaic3D/uploads/" + project_name + "/mosaic.jpg" );
                File file = new File(filename);

                long startTime = System.currentTimeMillis();
                Log.d("ImageManager", "download begining");
                Log.d("ImageManager", "download url:" + url);
                Log.d("ImageManager", "downloaded file name:" + project_name);
              
                /* Open a connection to that URL. */
                URLConnection ucon = url.openConnection();
                Log.d("ImageManager", "Connection opened");

                /*
                 * Define InputStreams to read from the URLConnection.
                 */
                
                Log.d("ImageManager", "Getting input stream");
                
                InputStream is = ucon.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                
                Log.d("ImageManager", "Creating bytearraybuffer");

                /*
                 * Read bytes to the Buffer until there is nothing more to read(-1).
                 */
                ByteArrayBuffer baf = new ByteArrayBuffer(5000);
                FileOutputStream fos = new FileOutputStream(file);
                Log.d("ImageManager", "Downloading file...");

                int current = 0;
                while ((current = bis.read()) != -1) {
                        baf.append((byte) current);
                        fos.write(baf.toByteArray());
                        baf.clear();
                }

                /* Convert the Bytes read to a String. */
                fos.close();
                Log.d("ImageManager", "download ready in"
                                + ((System.currentTimeMillis() - startTime) / 1000)
                                + " sec");

            } catch (IOException e) {
                    Log.d("ImageManager", "Error: " + e);
            }
            
            try {            	
				ExifInterface exif = new ExifInterface(filename);
				exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, locationBuilder(latitude));
				exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, locationBuilder(longitude));
				exif.saveAttributes();
			} catch (IOException e) {
				e.printStackTrace();
				Log.e("Globetrotter", "ExifInterface Failure.");
				Log.e("Globetrotter", e.getMessage());
			}
	    } 
	    
		private String locationBuilder(String frac) {
			
			String[] location = frac.split(":",3);
			Log.v("Globetrotter", "Degrees: " + location[0]);
			Log.v("Globetrotter", "Minutes: " + location[1]);
			Log.v("Globetrotter", "Seconds: " + location[2]);

			location[2] = Integer.toString((int)(Double.parseDouble(location[2])));

			return String.format("%s/1,%s/1,%s/1", location[0], location[1], location[2]);

		}
	}  
}