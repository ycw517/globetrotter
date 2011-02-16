// this should be revision 21
package com.ece194.globetrotter;
import java.io.IOException;

import android.app.Activity;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class CameraActivity extends Activity {
	
	private SurfaceView preview=null;
	private SurfaceHolder previewHolder=null;
	private Camera camera=null;
	private boolean inPreview=false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.capture);

		


		
		preview=(SurfaceView)findViewById(R.id.preview);
		
		

		previewHolder=preview.getHolder();
		previewHolder.addCallback(surfaceCallback);
		previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	public void onResume() {
		super.onResume();

		camera=Camera.open();
	}

	@Override
	public void onPause() {
		if (inPreview) {
			camera.stopPreview();
		}

		camera.release();
		camera=null;
		inPreview=false;

		super.onPause();
	}

	SurfaceHolder.Callback surfaceCallback=new SurfaceHolder.Callback() {
		public void surfaceCreated(SurfaceHolder holder) {
			try {
				camera.setPreviewDisplay(previewHolder);
			}
			catch (Throwable t) {
				Log.e("PreviewDemo-surfaceCallback",
							"Exception in setPreviewDisplay()", t);
				Toast
					.makeText(CameraActivity.this, t.getMessage(), Toast.LENGTH_LONG)
					.show();
			}
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			Camera.Parameters p=camera.getParameters();

			p.set("rotation", 180); 
			previewHolder.getSurface(); 
			Surface.setOrientation(Display.DEFAULT_DISPLAY, 
			Surface.ROTATION_90); 

			p.setPreviewSize(width, height);
			camera.setParameters(p);
			camera.startPreview();
			inPreview=true;
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			// no-op
		}
		
		public void frameCapture() {
			MediaRecorder recorder = new MediaRecorder();
			recorder.setCamera(camera);
			recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
			recorder.setOutputFormat(MediaRecorder.VideoEncoder.H264);
			recorder.setVideoSize(320,240);
	        try {
				recorder.prepare();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        recorder.start();

			
		}
		
	};
}
