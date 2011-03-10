package com.ece194.globetrotter;

import android.app.ListActivity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.TabHost;

public class TagSelectorActivity extends TabActivity {
		
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.tagselector);

	    TabHost mTabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	    Intent intent;  // Reusable Intent for each tab

	    // Create an Intent to launch an Activity for the tab (to be reused)
	    intent = new Intent().setClass(this, ListView.class);
	    spec = mTabHost.newTabSpec("yourtags").setIndicator("Your Tags")
	                  .setContent(intent);
	    mTabHost.addTab(spec);

	    // Do the same for the other tabs
	    intent = new Intent().setClass(this, ListView.class);
	    spec = mTabHost.newTabSpec("alltags").setIndicator("All Tags")
	                  .setContent(intent);
	    mTabHost.addTab(spec);

	    mTabHost.setCurrentTab(0);
	    
	}
	
	

}

