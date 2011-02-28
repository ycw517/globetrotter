package com.ece194.globetrotter;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

//AsyncTask<params, progress, result>
public class FrameHandler extends AsyncTask<byte[], Void, Void> {
	

	
	// Final Variables
	private final static int WIDTH = 480;
	private final static int HEIGHT = 320;
	private final static int ARRAY_LENGTH = 480*320;
	
	// pre-allocated working arrays
	private int[] argb8888 = new int[ARRAY_LENGTH];
	
	// filename of image
	int quality = 75;
	private String filename;
	
	
	// SHOULD PASS AN INT THAT SPECIFIES WHICH IMAGE THIS IS IN ORDER OF CAPTURE
	@Override
	protected Void doInBackground(byte[]... args) {
		Log.v("GlobeTrotter", "Beginning AsyncTask");
		
		// Receive frame data
		// Decode YUV to ARGB
		// Create Bitmap
		// Post to server

		
		// creates an RGB array in argb8888 from the YUV btye array
		decodeYUV(argb8888, args[0], WIDTH, HEIGHT);
		Bitmap bitmap = Bitmap.createBitmap(argb8888, WIDTH, HEIGHT, Config.ARGB_8888);
		
		try {
			save(bitmap);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}   
	
	
	public void save(Bitmap bmp) throws IOException {
		
		filename = "/sdcard/globetrotter/bufferdump/image.jpg";

		BitmapFactory.Options options=new BitmapFactory.Options();		
		options.inSampleSize = 20;
		
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
        // Create a new HttpClient and Post Header  
        HttpClient httpclient = new DefaultHttpClient();  
        HttpPost httppost = new HttpPost("http://www.yoursite.com/script.php");  
      
        try {  
            // Add your data  
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);  
            nameValuePairs.add(new BasicNameValuePair("id", "12345"));  
            nameValuePairs.add(new BasicNameValuePair("stringdata", "AndDev is Cool!"));  
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));  
      
            // Execute HTTP Post Request  
            HttpResponse response = httpclient.execute(httppost);  
              
        } catch (ClientProtocolException e) {  
            // TODO Auto-generated catch block  
        } catch (IOException e) {  
            // TODO Auto-generated catch block  
        }  
    }





	
	
	
	
}