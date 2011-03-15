package com.ece194.globetrotter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Bundle;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class GMapActivity extends MapActivity {
	
   static File dir = new File("/sdcard/globetrotter/mytags");
   static String[] TAGS = dir.list();
   static int[][] coords = new int[2][TAGS.length];
	int longitude;
	int latitude;	
	@Override
	protected boolean isRouteDisplayed() {
	    return false;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.mapview);
	    
	    MapView mapView = (MapView) findViewById(R.id.mapview);
	    mapView.setBuiltInZoomControls(true);
	    
	    List<Overlay> mapOverlays = mapView.getOverlays();
	    Drawable drawable = this.getResources().getDrawable(R.drawable.androidmarker);
	    ItemizedTagOverlay itemizedoverlay = new ItemizedTagOverlay(drawable);
	    
	    coordBuilder();
	    
	    for (int i = 0; i < TAGS.length; i++) {
		    itemizedoverlay.addOverlay(new OverlayItem(new GeoPoint(coords[1][i], coords[0][i]), "Sweet!", "It works!!" + latitude + " " + longitude));
	    }
	    
	    mapOverlays.add(itemizedoverlay);
	}
	
	
	private void coordBuilder() {
		ExifInterface exif = null;

		for(int i = 0; i < TAGS.length; i++) {
			
			try {
				exif = new ExifInterface("/sdcard/globetrotter/mytags/"+TAGS[i]);
				
				longitude = RotToInt(exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE));
				latitude = RotToInt(exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE));

				Log.v("LAT IS FAT: ", latitude+ "");
				Log.v("LONG IS LONG:", longitude + "");
				
				coords[0][i] = latitude;
				coords[1][i] = longitude;
				

			} catch (IOException e) {
				e.printStackTrace();
			}	
		}
	}
		
	
	private int RotToInt(String rot) {
	
		String[] location = rot.split(",",3);
		String[] degrees = location[0].split("/", 2);
		String[] minutes = location[1].split("/", 2);
		String[] seconds = location[2].split("/", 2);
		
		double d = Double.parseDouble(degrees[0]);
		double m = Double.parseDouble(minutes[1])/60;
		double s = Double.parseDouble(seconds[0])/(3600);

		if (d < 0) {
			d = d*-1;
			d = d + m + s;
			d = d*-1;
		} else {
			d = d + m + s;
		}
		
		Log.v("Globetrotter:", "Location: " + d);
		
		return (int) (d*1000000);
	}

	
	
	public class ItemizedTagOverlay extends ItemizedOverlay<OverlayItem> {

		private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
		private Context mContext;
		private int currentIndex;

		public ItemizedTagOverlay(Drawable defaultMarker) {
			  super(boundCenterBottom(defaultMarker));
			// TODO Auto-generated constructor stub
		}
		
		public ItemizedTagOverlay(Drawable defaultMarker, Context context) {
			  super(defaultMarker);
			  mContext = context;
			}

		@Override
		protected OverlayItem createItem(int i) {
			  return mOverlays.get(i);
		}

		@Override
		public int size() {
			  return mOverlays.size();
		}
		
		@Override
		protected boolean onTap(int index) {
		  OverlayItem item = mOverlays.get(index);
		  AlertDialog.Builder dialog = new AlertDialog.Builder(GMapActivity.this);
		  dialog.setTitle(item.getTitle());
		  dialog.setMessage(item.getSnippet());
		  dialog.setPositiveButton("View Tag", yourListener);
		  dialog.setNegativeButton("Close", null);
		  currentIndex = index;
		  dialog.show();
		  return true;
		}


		public void addOverlay(OverlayItem overlay) {
		    mOverlays.add(overlay);
		    populate();
		}

		
		DialogInterface.OnClickListener yourListener = new DialogInterface.OnClickListener(){
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == DialogInterface.BUTTON_POSITIVE){
			    	Intent intent = new Intent();
			    	intent.setClassName("com.ece194.globetrotter", "com.ece194.globetrotter.ViewerActivity");
			    	intent.putExtra("filename","/sdcard/globetrotter/mytags/"+ TAGS[currentIndex]);
 					startActivity(intent);    
				} 
			}
	    };

		
	}	
}


