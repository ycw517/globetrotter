// this should be revision 21
package com.ece194.globetrotter;

import java.io.IOException;

import android.app.Activity;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
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
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.capture);

	    preview=(SurfaceView)findViewById(R.id.preview);
	    previewHolder=preview.getHolder();
		previewHolder.addCallback(this);
	    previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	    
	    mRecorder = new MediaRecorder();

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

	*/	if (mPreviewRunning) mCamera.stopPreview();

		Camera.Parameters p = mCamera.getParameters();
		p.setPreviewSize(width, height);
		mCamera.setParameters(p);

		try { mCamera.setPreviewDisplay(holder); }
		catch (IOException e) { e.printStackTrace(); }	

		mCamera.startPreview();
		mPreviewRunning = true;
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.capture_menu, menu);
	    return true;
	}

	public void startRecording() {
	//	mRecorder.start();
	}
	
	public void stopRecording() {
	//	mRecorder.stop();
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
	

}
