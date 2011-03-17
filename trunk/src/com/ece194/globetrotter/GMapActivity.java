package com.ece194.globetrotter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class GMapActivity extends MapActivity {
	
	private File dir = new File("/sdcard/globetrotter/mytags");
	private String[] TAGS = dir.list();
	private int[][] coords = new int[2][TAGS.length];
	private int currentIndex;
   
	List<Overlay> mapOverlays;
   
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
	    registerForContextMenu(mapView);
	    updateOverlays();
	}
	
	@Override
	public void onResume() {
		updateOverlays();
		super.onResume();
	}
	
	private void updateOverlays() {
	    TAGS = dir.list();
	    MapView mapView = (MapView) findViewById(R.id.mapview);
	    if (mapOverlays != null && !mapOverlays.isEmpty()) {
	    	mapOverlays.clear();
	    	mapView.invalidate();
	    }
	    
	    mapOverlays = mapView.getOverlays();
	    Drawable drawable = this.getResources().getDrawable(R.drawable.androidmarker);
	    ItemizedTagOverlay itemizedoverlay = new ItemizedTagOverlay(drawable);

	    coordBuilder();
	    
	    for (int i = 0; i < TAGS.length; i++) {
		    itemizedoverlay.addOverlay(new OverlayItem(new GeoPoint(coords[1][i], coords[0][i]), TAGS[i],  coords[1][i]/1000000.f + ", " + coords[0][i]/1000000.f));
	    }
	    mapOverlays.add(itemizedoverlay);
	    
	    //mapView.invalidate();
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
	
		Log.v("globetrotter", rot);		
		String[] location = rot.split(",", 3);
		String[] degrees = location[0].split("/", 2);
		String[] minutes = location[1].split("/", 2);
		String[] seconds = location[2].split("/", 2);
		
		double d = Double.parseDouble(degrees[0]);
		double m = Double.parseDouble(minutes[0])/60;
		double s = Double.parseDouble(seconds[0])/(3600*10000);

		if (d < 0) {
			d = 0-d;
			d = d + m + s;
			d = 0-d;
		} else {
			d = d + m + s;
		}
		
		Log.v("Globetrotter:", "Location: " + d);
		
		return (int) (d*1000000);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.setHeaderTitle(TAGS[currentIndex]);
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.gmap_context_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		  switch (item.getItemId()) {
		  case R.id.contextView:
			  Intent intent = new Intent();
			  intent.setClassName("com.ece194.globetrotter", "com.ece194.globetrotter.ViewerActivity");
			  intent.putExtra("filename","/sdcard/globetrotter/mytags/"+ TAGS[currentIndex]);
			  startActivity(intent);
			  return true;
		  case R.id.contextRename:
			  renameTag();
			  return true;
		  case R.id.contextShare:
			  share();
			  return true;
		  case R.id.contextDelete:
			  deleteTag();
			  return true;
		  default:
		    return super.onContextItemSelected(item);
		  }
	}
	
	private String filename;
	private void renameTag() {
		filename = TAGS[currentIndex];
    	
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);

    	alert.setTitle("Rename");
    	alert.setMessage("Enter a new filename");

    	// Set an EditText view to get user input 
    	final EditText input = new EditText(this);
    	input.setText(filename);
    	alert.setView(input);

    	// Rename file
    	alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
    	public void onClick(DialogInterface dialog, int whichButton) {
    		String filename_new = input.getText().toString();
    		File orig = new File("/sdcard/globetrotter/mytags/"+filename);
    		File file_new = new File("/sdcard/globetrotter/mytags/"+filename_new);
    		if (orig.renameTo(file_new)) {
    			Toast toast = Toast.makeText(getApplicationContext(), "Successfully renamed to " + filename_new, Toast.LENGTH_SHORT);
    			toast.show();
    			updateOverlays();
    		}
    		else {
    			Toast toast = Toast.makeText(getApplicationContext(), "Error renaming", Toast.LENGTH_SHORT);
    			toast.show();
    		}
    	  }
    	});

    	alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    	  public void onClick(DialogInterface dialog, int whichButton) {
    	    // Canceled.
    	  }
    	});
    	
    	alert.show();
    }
	
	private void deleteTag() {
		filename = TAGS[currentIndex];
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);

    	alert.setTitle("Delete panorama");
    	alert.setMessage("Are you sure you want to delete this image?");

    	// Delete file file
    	alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
    	public void onClick(DialogInterface dialog, int whichButton) {
    		File orig = new File("/sdcard/globetrotter/mytags/"+filename);
    		if (orig.delete()) {
    			Toast toast = Toast.makeText(getApplicationContext(), "Successfully deleted " + filename, Toast.LENGTH_SHORT);
    			toast.show();
    			updateOverlays();
    		}
    		else {
    			Toast toast = Toast.makeText(getApplicationContext(), "Error deleting", Toast.LENGTH_SHORT);
    			toast.show();
    		}
    	  }
    	});

    	alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    	  public void onClick(DialogInterface dialog, int whichButton) {
    	    // Canceled.
    	  }
    	});

    	alert.show();
    }
	
	private void share() {
	    Intent share = new Intent(Intent.ACTION_SEND);
	    share.setType("image/png");
	    share.putExtra(Intent.EXTRA_STREAM,
	    Uri.parse("file://" + filename));
	    startActivity(Intent.createChooser(share, "Share Tag"));
	}
	
	
	public class ItemizedTagOverlay extends ItemizedOverlay<OverlayItem> {

		private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();

		public ItemizedTagOverlay(Drawable defaultMarker) {
			  super(boundCenterBottom(defaultMarker));
		}
		
		public ItemizedTagOverlay(Drawable defaultMarker, Context context) {
			  super(defaultMarker);
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
		  currentIndex = index;
		  openContextMenu(findViewById(R.id.mapview));
		  return true;
		}


		public void addOverlay(OverlayItem overlay) {
		    mOverlays.add(overlay);
		    populate();
		}
		
	}	
}


