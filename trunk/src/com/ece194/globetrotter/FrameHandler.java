package com.ece194.globetrotter;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.os.AsyncTask;
import android.util.Log;

//AsyncTask<params, progress, result>
public class FrameHandler extends AsyncTask<byte[], Void, Boolean> {
	

	
	// Final Variables
	private final static int WIDTH = 480;
	private final static int HEIGHT = 320;
	private final static int ARRAY_LENGTH = 480*320*3/2;
	private String projectName = "globetrotter-test-07";
	
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
		
		// Receive frame data
		// Decode YUV to ARGB
		// Create Bitmap
		// Post to server

		
		// creates an RGB array in argb8888 from the YUV btye array
		decodeYUV(argb8888, args[0], WIDTH, HEIGHT);
		Bitmap bitmap = Bitmap.createBitmap(argb8888, WIDTH, HEIGHT, Config.ARGB_8888);
		
		filename = "/sdcard/globetrotter/bufferdump/" + (args[1][0]) +".jpg";

		
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
		

//		BitmapFactory.Options options=new BitmapFactory.Options();		
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
	             
	             reqEntity.addPart("project", new StringBody(projectName));
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