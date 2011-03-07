// this should be revision 21
package com.ece194.globetrotter;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.media.MediaRecorder;
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

public class CameraActivity<ExampleApp> extends Activity implements SurfaceHolder.Callback, Camera.AutoFocusCallback {

	public String project_name = "globetrotter-test-05";
	
	
	private SurfaceView preview;
	private SurfaceHolder previewHolder;
	
	private MediaRecorder mRecorder;
	private Camera mCamera;
	private boolean mPreviewRunning = false;
	private boolean mCaptureFrame = false;
	private int frame_number = 0;
	private byte[] frame = new byte[1];
	
	private byte[] progressBuffer = new byte[4];
	
	private TextView mPreviewText;
	
	ProgressDialog  pDialogue;
	
	
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
	    //getDateTime() //for timestamping the project 
	    pDialogue = new ProgressDialog( CameraActivity.this );
	    pDialogue.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	    pDialogue.setMessage("Loading...");
	    pDialogue.setCancelable(false);
    	pDialogue.setProgress(0);



	   	}

	@Override
	public void onResume() {
		super.onResume();		
	}

	@Override
	public void onPause() {
  		super.onPause();
  		
		if (mPreviewRunning) {
			mCamera.stopPreview();
			mPreviewRunning = false;
		}
		mCamera.setPreviewCallback(null); // this is necessary to prevent the callback from trying to access an empty surfaceview
		 mCamera.release();		
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.capture_menu, menu);
	    return true;
	}

	public void startRecording() {
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
            nameValuePairs.add(new BasicNameValuePair("loop", "1"));  
            nameValuePairs.add(new BasicNameValuePair("email", "brian.dunlay@gmail.com"));  
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
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.startRecording:
	        startRecording();
	        return true;
	    case R.id.stopRecording:
	        stopRecording();
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mCamera = Camera.open();
	}


	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mPreviewRunning = false;

	}
	


	@Override
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
				frame[0] = (byte)frame_number;
				new FrameHandler().execute(data, frame);
				frame_number++;
				
			    mPreviewText.setText("Frames Captured: " + frame_number);
			}
		}
	};
	
	
	@Override
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
	
    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("H,H,m,m,s,s");
        Date date = new Date();
        return dateFormat.format(date);
    }
    
    public void toast(View v){
    	stopRecording();
    	
		mPreviewRunning = false;
		mCamera.stopPreview();

		pDialogue.show();

		new DownloadFilesTask().execute();
    }
    
    private class DownloadFilesTask extends AsyncTask<Void, Integer, Void> {
    	

        protected Void doInBackground(Void... urls) {
        	int response = 0;
        	// should clean this up so it doesn't create so many objects
        	while (response != 100) {
	        	try {
	                HttpClient client = new DefaultHttpClient();  
	                String getURL = "http://dragonox.cs.ucsb.edu/Mosaic3D/uploads/"+ project_name +"/status.txt";
	                HttpGet get = new HttpGet(getURL);
	                HttpResponse responseGet = client.execute(get);  
	                HttpEntity resEntityGet = responseGet.getEntity();  
	                if (resEntityGet != null) {  
	                    //do something with the response
	                	response = Integer.valueOf(EntityUtils.toString(resEntityGet).trim());
	                    Log.i("GET RESPONSE","RESPONSE IS = " + response);
	                    publishProgress(response);
	                }
		        } catch (Exception e) {
		            Log.i("Y U NO WORK?",e.toString());
	
		        }
        	}
        	        	
			return null;
        }

        protected void onProgressUpdate(Integer... progress) {     
            Log.i("UPDATE","UPDATE = ");
 
        	pDialogue.setProgress(progress[0]);

        }

        protected void onPostExecute(Void blah) {
            Log.i("COMPLETE","COMPLETE");

        	pDialogue.dismiss();
        	finish(); // this could also start an intent to another activity, like a list, and then finish here
        }
    }

    

}
