// this should be revision 21
package com.ece194.globetrotter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraActivity extends Activity implements SurfaceHolder.Callback, Camera.AutoFocusCallback {

	private SurfaceView preview;
	private SurfaceHolder previewHolder;
	
	private MediaRecorder mRecorder;
	private Camera mCamera;
	private boolean mPreviewRunning = false;
	private boolean mCaptureFrame = false;
	private int frame_number = 0;
	private byte[] frame = new byte[1];
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.capture);

	    preview=(SurfaceView)findViewById(R.id.preview);
	    previewHolder=preview.getHolder();
		previewHolder.addCallback(this);
	    previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	    
	    
	    
	//    mRecorder = new MediaRecorder();

	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}
	


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.capture_menu, menu);
	    return true;
	}

	public void startRecording() {
		mCaptureFrame = true;
		
	//	mRecorder.start();
	}
	
	public void stopRecording() {
	//	mRecorder.stop();
        HttpClient httpclient = new DefaultHttpClient();  
        HttpPost httppost = new HttpPost("http://dragonox.cs.ucsb.edu/Mosaic3D/process.php");
      
        try {  
            // Add your data  
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);  
            nameValuePairs.add(new BasicNameValuePair("project", "globetrotter-test-02"));  
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
		 mCamera.stopPreview();
		 mPreviewRunning = false;
		 mCamera.release();		
		 
	//	 mRecorder.reset();
	//	 mRecorder.release();

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
			}
			
			
		}
	};
	
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		/*
		mRecorder.reset();
	    mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
	    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
	    mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
	    mRecorder.setOutputFile("/sdcard/globetrotter/video.mp4");
	    mRecorder.setVideoFrameRate(30);
	    
		mRecorder.setPreviewDisplay(previewHolder.getSurface());
	    try {
			mRecorder.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	*/	
		if (mPreviewRunning) mCamera.stopPreview();

		Camera.Parameters p = mCamera.getParameters();
		p.setPreviewSize(width, height);
		mCamera.setParameters(p);

		try { mCamera.setPreviewDisplay(holder); }
		catch (IOException e) { e.printStackTrace(); }	

		mCamera.setPreviewCallback(previewCallback);
		
		
		
		mCamera.startPreview();
		mPreviewRunning = true;
		
	}
	

}
