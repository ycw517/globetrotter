package com.ece194.globetrotter;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.content.res.Resources;

public class TagSelectorActivity extends TabActivity {
		
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.tagselector);

	    Resources res = getResources(); // Resource object to get Drawables
	    TabHost mTabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	    Intent intent;  // Reusable Intent for each tab

	    // Create an Intent to launch an Activity for the tab (to be reused)
	    intent = new Intent().setClass(this, ListView.class);
	    spec = mTabHost.newTabSpec("yourtags")
	                  .setIndicator("List Tags", res.getDrawable(R.drawable.ic_menu_tags))
	                  .setContent(intent);
	    mTabHost.addTab(spec);

	/*    // Do the same for the other tabs
	    intent = new Intent().setClass(this, ListView.class);
	    spec = mTabHost.newTabSpec("alltags").setIndicator("All Tags")
	                  .setContent(intent);
	    mTabHost.addTab(spec);
*/
	    intent = new Intent().setClass(this, GMapActivity.class);
	    spec = mTabHost.newTabSpec("mapview")
	                  .setIndicator("Map Tags", res.getDrawable(R.drawable.ic_menu_globe))
  	                  .setContent(intent);
	    mTabHost.addTab(spec);

	    
	    mTabHost.setCurrentTab(0);
	    
	}
	
	

}

