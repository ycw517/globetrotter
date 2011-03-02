package com.ece194.globetrotter;

import android.app.TabActivity;
import android.os.Bundle;
import android.widget.TabHost;

public class TagSelectorActivity extends TabActivity {
	
	private TabHost mTabHost;
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.tagselector);

	    mTabHost = getTabHost();
	    
	    mTabHost.addTab(mTabHost.newTabSpec("tab_test1").setIndicator(getString(R.string.yourTags)).setContent(R.id.yourTags_lv));
	    mTabHost.addTab(mTabHost.newTabSpec("tab_test2").setIndicator(getString(R.string.allTags)).setContent(R.id.allTags_lv));
	    
	    mTabHost.setCurrentTab(0);
	}

}