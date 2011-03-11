package com.ece194.globetrotter;

import java.io.File;

import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


	public class ListView extends ListActivity {
		
		
		   static File dir = new File("/sdcard/globetrotter/mytags");
		   static String[] TAGS = dir.list();
		
		
	    private static class EfficientAdapter extends BaseAdapter {
	        private LayoutInflater mInflater;

	        public EfficientAdapter(Context context) {
	            // Cache the LayoutInflate to avoid asking for a new one each time.
	            mInflater = LayoutInflater.from(context);
	        }

	        /**
	         * The number of items in the list is determined by the number of speeches
	         * in our array.
	         *
	         * @see android.widget.ListAdapter#getCount()
	         */
	        public int getCount() {
	            return TAGS.length;
	        }

	        /**
	         * Since the data comes from an array, just returning the index is
	         * sufficent to get at the data. If we were using a more complex data
	         * structure, we would return whatever object represents one row in the
	         * list.
	         *
	         * @see android.widget.ListAdapter#getItem(int)
	         */
	        public Object getItem(int position) {
	            return position;
	        }

	        /**
	         * Use the array index as a unique id.
	         *
	         * @see android.widget.ListAdapter#getItemId(int)
	         */
	        public long getItemId(int position) {
	            return position;
	        }

	        /**
	         * Make a view to hold each row.
	         *
	         * @see android.widget.ListAdapter#getView(int, android.view.View,
	         *      android.view.ViewGroup)
	         */
	        public View getView(int position, View convertView, ViewGroup parent) {
	            // A ViewHolder keeps references to children views to avoid unneccessary calls
	            // to findViewById() on each row.
	            ViewHolder holder;

	            // When convertView is not null, we can reuse it directly, there is no need
	            // to reinflate it. We only inflate a new View when the convertView supplied
	            // by ListView is null.
	            if (convertView == null) {
	                convertView = mInflater.inflate(R.layout.list_view, null);

	                         
	                // Creates a ViewHolder and store references to the two children views
	                // we want to bind data to.
	                holder = new ViewHolder();
	                holder.text = (TextView) convertView.findViewById(R.id.list_item);



	                
	                
	                convertView.setTag(holder);
	            } else {
	                // Get the ViewHolder back to get fast access to the TextView
	                // and the ImageView.
	                holder = (ViewHolder) convertView.getTag();
	            }

	            // Bind the data efficiently with the holder.
	            holder.text.setText(TAGS[position]);

	            return convertView;
	        }
	        
	        
	        
	        
	        
	        

	        static class ViewHolder {
	            TextView text;
	            ImageView icon;
	        }
	    }

	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setListAdapter(new EfficientAdapter(this));
	    }

		
		
		
		
		
		
		
	}
		/*
		@Override
		public void onCreate(Bundle savedInstanceState) {
		  super.onCreate(savedInstanceState);
		  
		  File dir = new File("/sdcard/globetrotter/mytags");
		  String[] TAGS = dir.list();
		  
		  setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, TAGS));
		  getListView().setTextFilterEnabled(true);
		  
		  
	    	//Intent notificationIntent = new Intent(this, ViewerActivity.class);
	    	//notificationIntent.putExtra("filename","/sdcard/globetrotter/mytags/mosaic.jpg");

		}
	}
		
	public class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}		
		
		
	}
	*/