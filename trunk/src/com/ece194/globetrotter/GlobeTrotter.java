// this should be revision 21
package com.ece194.globetrotter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class GlobeTrotter extends Activity {
	
	public final static int CAPTURE_TAG = 100;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    
	public void makeToast(View view) {
		
		Context context = getApplicationContext();
		int duration = Toast.LENGTH_SHORT;
		String text = "Don't touch me!";
		Toast.makeText(context, text, duration).show();
	}
	
	// Open the new screen.
	public void onClick(View v){
	    // Start the activity whose result we want to retrieve.  The
	    // result will come back with request code GET_CODE.
	    Intent intent = new Intent(this, CameraActivity.class);
	    startActivityForResult(intent, CAPTURE_TAG);
	}

	// Listen for results.
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
	    // See which child activity is calling us back.
	    switch (resultCode) {
	        case CAPTURE_TAG:
	            // This is the standard resultCode that is sent back if the
	            // activity crashed or didn't doesn't supply an explicit result.
	            if (resultCode == RESULT_CANCELED){
	            	// toast? capture was cancelled
	            } 
	            else {
	            	// do something with (data)
	            }
	        default:
	            break;
	    }
	}
    
    
}