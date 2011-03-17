package com.ece194.globetrotter;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import com.ece194.pan.PanCompassListener;
import com.ece194.pan.PanState;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class CameraActivity<ExampleApp> extends Activity implements SurfaceHolder.Callback, Camera.AutoFocusCallback, Observer {

	public String project_name = GlobeTrotter.project_name;

	// Assorted text
	public static String loop = "0";
	public static String email ="brian.dunlay@gmail.com";
	
	// Surface vars
	private SurfaceView preview;
	private SurfaceHolder previewHolder;
	
	// Camera vars
	private Camera mCamera;
	private boolean mPreviewRunning = false;
	private boolean mCaptureFrame = false;
	private int frame_number = 0;
	private byte[] frame = new byte[1];
	
	// Handle to preview text
	private TextView mPreviewText;
	
	private AlertDialog.Builder builder;
	
	// Notification vars
	String ns = Context.NOTIFICATION_SERVICE;
	NotificationManager mNotificationManager;
	private static final int HELLO_ID = 1;
	Notification notification;

	// Progress vars
	private byte[] progressBuffer = new byte[4];
	ProgressDialog  pDialogue;
	
	// Compass listener
	private PanCompassListener mCompassListener;
	private PanState mPanState;
	private float mInitialHeading;
	private boolean isRecording = false;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
		setContentView(R.layout.capture);
	    preview=(SurfaceView)findViewById(R.id.preview);
	    previewHolder=preview.getHolder();
		previewHolder.addCallback(this);
	    previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	    
	    mPreviewText=(TextView)findViewById(R.id.previewText);
	    mPreviewText.setText("Frames Captured: " + frame_number);
	    
	    pDialogue = new ProgressDialog( CameraActivity.this );
	    pDialogue.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	    pDialogue.setMessage("Starting processing...");
	    pDialogue.setCancelable(false);
    	pDialogue.setProgress(0);

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
 
    	builder = new AlertDialog.Builder(this);
    	builder.setTitle("Failed!");
    	builder.setMessage("Sorry, but we couldn't process your images. Please try again! ")
    	       .setCancelable(false)
    	       .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	                CameraActivity.this.finish();
    	           }
    	       });
		builder.create();
		
		// Init compass listeners
		mCompassListener = new PanCompassListener(getApplicationContext());
		mPanState = new PanState();
		mPanState.resetPanState();		
		mCompassListener.setPanState(mPanState);
		mPanState.addObserver(this);
		
		mCompassListener.setOrientation(PanCompassListener.ORIENTATION_LANDSCAPE);
		
	   	}

	@Override
	public void onResume() {
		super.onResume();
		if (isRecording)
			mCompassListener.resume();
	}

	@Override
	public void onPause() {
  		super.onPause();
		mCompassListener.stop();

		if (mPreviewRunning) {
			mCamera.stopPreview();
			mPreviewRunning = false;
		}
		mCamera.setPreviewCallback(null); // this is necessary to prevent the callback from trying to access an empty surfaceview
		 mCamera.release();		
	}
	
/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.capture_menu, menu);
	    return true;
	}
*/
	public void startRecording() {
		isRecording = true;
		mCaptureFrame = true;
	}
	
	public void captureFrame(View v) {
		mCaptureFrame = true;
	}

	
	
	public void stopRecording() {
		
		
	//	mRecorder.stop();
        HttpClient httpclient = new DefaultHttpClient();  
        HttpPost httppost = new HttpPost("http://dragonox.cs.ucsb.edu/Mosaic3D/process.php");
      
        try {  
            // Add your data  
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);  
            nameValuePairs.add(new BasicNameValuePair("project", project_name));  
            nameValuePairs.add(new BasicNameValuePair("width", "480"));  
            nameValuePairs.add(new BasicNameValuePair("height", "320"));  
            nameValuePairs.add(new BasicNameValuePair("eFocal", "36"));  
            nameValuePairs.add(new BasicNameValuePair("loop", loop));  
            nameValuePairs.add(new BasicNameValuePair("email", email));  
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));  
      
            // Execute HTTP Post Request  
            HttpResponse response = httpclient.execute(httppost);  
              
            
            HttpEntity resEntity = response.getEntity();  
            if (resEntity != null) {    
                      Log.i("BEGIN RESPONSE",EntityUtils.toString(resEntity));
                }
        } catch (ClientProtocolException e) {  
            // TODO Auto-generated catch block  
        } catch (IOException e) {  
            // TODO Auto-generated catch block  
        }  

		
	}
/*	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.startRecording:
	    	isRecording = true;
	        startRecording();
	        mCompassListener.resume();
	        return true;
	    case R.id.stopRecording:
	    	isRecording = false;
	        stopRecording();
	        mCompassListener.stop();
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	*/
	// implements SurfaceHolder.Callback
	public void surfaceCreated(SurfaceHolder holder) {
		mCamera = Camera.open();
	}

	// implements SurfaceHolder.Callback
	public void surfaceDestroyed(SurfaceHolder holder) {
		mPreviewRunning = false;

	}
	
	// implements Camera.AutoFocusCallback
	public void onAutoFocus(boolean success, Camera camera) {
		// TODO Auto-generated method stub
		
	}

	
	/* PreviewCallback()
	 * 
	 * this callback captures the preview at every frame
	 * and puts it in a byte buffer. we will evaluate if 
	 * this is a frame that we want to process, and if so,
	 * we will send it to an asynchronous thread that will
	 * process it to an ARGB Bitmap and POST it to the server
	 * 
	*/
	PreviewCallback previewCallback = new PreviewCallback () {
		public void onPreviewFrame(byte[] data, Camera camera) {
			if (mCaptureFrame) {
				mCaptureFrame = false;
				frame[0] = (byte)frame_number++;
				new FrameHandler().execute(data, frame);
				
				
			    mPreviewText.setText("Frames Captured: " + frame_number);
			}
		}
	};
	
	// implements SurfaceHolder.Callback
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	
		if (mPreviewRunning) {
			mCamera.stopPreview();
		}
		

		Camera.Parameters p = mCamera.getParameters();

		p.setPreviewSize(width, height);
		p.setPreviewSize(480, 320);
		mCamera.setParameters(p);

		try { mCamera.setPreviewDisplay(holder); }
		catch (IOException e) { e.printStackTrace(); }	

		mCamera.setPreviewCallback(previewCallback);

		// 0 = landscape
		// 90 = portrait

		    int rotation = getWindowManager().getDefaultDisplay().getRotation();
		    int degrees = 0;
		    switch (rotation) {
		        case Surface.ROTATION_0: degrees = 90; break;
		        case Surface.ROTATION_90: degrees = 0; break;
		        case Surface.ROTATION_180: degrees = 270; break;
		        case Surface.ROTATION_270: degrees = 180; break;
		        
		    }
	        Log.i("DEGREES ARE WHAT??", Integer.toString(degrees));

		mCamera.setDisplayOrientation(degrees);		

		
		mCamera.startPreview();
		mPreviewRunning = true;
		
	}
	
    
    public void toast(View v){
    	stopRecording();
    	
		mPreviewRunning = false;
		mCamera.stopPreview();
	//	pDialogue.show();

		
    //	Toast.makeText(getApplicationContext(), "Your panorama is processing.", Toast.LENGTH_LONG);
    //	finish();

		new DownloadFilesTask().execute();
    }
    
    public void toggle360(View v) {
    	if (loop.contentEquals("0"))
    		loop = "1";
    	else
    		loop = "0";
    }
    
    private class DownloadFilesTask extends AsyncTask<Void, Integer, Void> {
    	 boolean fail = false;

        protected Void doInBackground(Void... urls) {
        	int response = 0;
        	
        	try {
                HttpClient client = new DefaultHttpClient();  
                String getURL = "http://dragonox.cs.ucsb.edu/Mosaic3D/uploads/"+ project_name +"/status.txt";
                HttpGet get = new HttpGet(getURL);
                HttpResponse responseGet = client.execute(get);  
                HttpEntity resEntityGet = responseGet.getEntity();  
                if (resEntityGet != null) {  
                	response = Integer.valueOf(EntityUtils.toString(resEntityGet).trim());
                    Log.i("GET RESPONSE","RESPONSE IS = " + response);
                    publishProgress(response);
                }
	        } catch (Exception e) {
	            Log.i("Y U NO WORK?",e.toString());
	            fail = true;
	        }
			return null;
        }

        protected void onPreExecute() {
        }
        
        
        protected void onProgressUpdate(Integer... progress) {     
            Log.i("UPDATE","UPDATE = " + progress[0]);
            pDialogue.dismiss();
             switch (progress[0]) {
                case -1:  break;			
                default: break;
            }
        }

        protected void onPostExecute(Void blah) {
            Log.i("COMPLETE","COMPLETE");
        	if (fail) {
        	}
        	
        	 setResult(RESULT_OK);
              finish();
        }
    }
    
    // implements Observer
    // captures a frame when the compass listener says it is appropriate
    public void update(Observable observable, Object data) {
    	
    	// TODO: determine conditions for capture
    	float heading = mPanState.getPanX();
    	if (frame_number == 0) {
    		mInitialHeading = heading;
    		mCaptureFrame = true;
    		
        	Log.e("globetrotter update", "Frame number: " + frame_number );
        	Log.e("globetrotter update", "heading delta: " + (heading-mInitialHeading) );

    		
    	}
    	else if (Math.abs(heading-mInitialHeading) >= frame_number*45.f/360.f) {
        	mCaptureFrame = true;
        	Log.e("globetrotter update", "Frame number: " + frame_number );
        	Log.e("globetrotter update", "heading delta: " + (heading-mInitialHeading) );

    	}

    }

    public class FrameHandler extends AsyncTask<byte[], Void, Boolean> {

    	// Final Variables
    	private final static int WIDTH = 480;
    	private final static int HEIGHT = 320;
    	private final static int ARRAY_LENGTH = 480*320*3/2;
    	
    	// pre-allocated working arrays
    	private int[] argb8888 = new int[ARRAY_LENGTH];
    	
    	// filename of image
    	int quality = 75;
    	private String filename;
    	int imageIndex = 0;
    	
    	@Override
    	protected Boolean doInBackground(byte[]... args) {
    		Log.v("GlobeTrotter", "Beginning AsyncTask");
    		
    		imageIndex = args[1][0];
    		filename = "/sdcard/globetrotter/bufferdump/" + (args[1][0]) +".jpg";

    		// creates an RGB array in argb8888 from the YUV btye array
    		decodeYUV(argb8888, args[0], WIDTH, HEIGHT);
    		Bitmap bitmap = Bitmap.createBitmap(argb8888, WIDTH, HEIGHT, Config.ARGB_8888);
    		
    		// save a jpeg file locally
    		try {
    			save(bitmap);
    		} catch (IOException e) {
    			e.printStackTrace();
    		}

    		// upload that file to the server
    		postData();
    		
    		return true;
    	}   
    	
    	public void save(Bitmap bmp) throws IOException {
    		//  BitmapFactory.Options options=new BitmapFactory.Options();		
    		//	options.inSampleSize = 20;
    		
    		FileOutputStream fos = new FileOutputStream(filename);
    		
    		BufferedOutputStream bos = new BufferedOutputStream(fos);
    		bmp.compress(CompressFormat.JPEG, quality, bos);

    		bos.flush();
    		bos.close();		
    	}
    	
    	// decode Y, U, and V values on the YUV 420 buffer described as YCbCr_422_SP by Android 
    	// David Manpearl 081201 
    	public void decodeYUV(int[] out, byte[] fg, int width, int height)
    	        throws NullPointerException, IllegalArgumentException {
    	    int sz = width * height;
    	    if (out == null)
    	        throw new NullPointerException("buffer out is null");
    	    if (out.length < sz)
    	        throw new IllegalArgumentException("buffer out size " + out.length
    	                + " < minimum " + sz);
    	    if (fg == null)
    	        throw new NullPointerException("buffer 'fg' is null");
    	    if (fg.length < sz)
    	        throw new IllegalArgumentException("buffer fg size " + fg.length
    	                + " < minimum " + sz * 3 / 2);
    	    int i, j;
    	    int Y, Cr = 0, Cb = 0;
    	    for (j = 0; j < height; j++) {
    	        int pixPtr = j * width;
    	        final int jDiv2 = j >> 1;
    	        for (i = 0; i < width; i++) {
    	            Y = fg[pixPtr];
    	            if (Y < 0)
    	                Y += 255;
    	            if ((i & 0x1) != 1) {
    	                final int cOff = sz + jDiv2 * width + (i >> 1) * 2;
    	                Cb = fg[cOff];
    	                if (Cb < 0)
    	                    Cb += 127;
    	                else
    	                    Cb -= 128;
    	                Cr = fg[cOff + 1];
    	                if (Cr < 0)
    	                    Cr += 127;
    	                else
    	                    Cr -= 128;
    	            }
    	            int R = Y + Cr + (Cr >> 2) + (Cr >> 3) + (Cr >> 5);
    	            if (R < 0)
    	                R = 0;
    	            else if (R > 255)
    	                R = 255;
    	            int G = Y - (Cb >> 2) + (Cb >> 4) + (Cb >> 5) - (Cr >> 1)
    	                    + (Cr >> 3) + (Cr >> 4) + (Cr >> 5);
    	            if (G < 0)
    	                G = 0;
    	            else if (G > 255)
    	                G = 255;
    	            int B = Y + Cb + (Cb >> 1) + (Cb >> 2) + (Cb >> 6);
    	            if (B < 0)
    	                B = 0;
    	            else if (B > 255)
    	                B = 255;
    	            out[pixPtr++] = 0xff000000 + (B << 16) + (G << 8) + R;
    	        }
    	    }

    	}

        public void postData() {  
        	//http://www.softwarepassion.com/android-series-get-post-and-multipart-post-requests/
            File f = new File(filename);
            try {
                     HttpClient client = new DefaultHttpClient();  
                     String postURL = "http://dragonox.cs.ucsb.edu/Mosaic3D/clientupload.php";

                     HttpPost post = new HttpPost(postURL); 

    	             FileBody bin = new FileBody(f);
    	             MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);  
    	             reqEntity.addPart("file", bin);
    	             
    	             // http://stackoverflow.com/questions/2607020/help-constructing-a-post-request-with-multipartentity-newbie-question
    	             
    	             reqEntity.addPart("project", new StringBody(project_name));
    	             reqEntity.addPart("name", new StringBody(Integer.toString(imageIndex)));

    	             post.setEntity(reqEntity); 
    	             
    	             HttpResponse response = client.execute(post);  
    	             HttpEntity resEntity = response.getEntity();  
    	             if (resEntity != null) {    
    	                       Log.i("RESPONSE",EntityUtils.toString(resEntity));
    	                 }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }    	
    }
}
