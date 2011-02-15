package com.ece194.globetrotter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class GlobeTrotter extends Activity {
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
    
    
}