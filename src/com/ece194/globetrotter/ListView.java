package com.ece194.globetrotter;

import java.io.File;
import java.io.IOException;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.media.ExifInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;


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
        public View getView(final int position, View convertView, ViewGroup parent) {
        	

        	
            // A ViewHolder keeps references to children views to avoid unneccessary calls
            // to findViewById() on each row.
            final ViewHolder holder;

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
            
            
            convertView.setFocusable(true);
        //    convertView.setFocusableInTouchMode(convertView.isInTouchMode ());
            convertView.setClickable(true);
            
            
            convertView.setOnClickListener(
 	          new OnClickListener() {
            	
        		 public void onClick(View view) {
            		  Intent myIntent = new Intent(view.getContext(), ViewerActivity.class);
            		  myIntent.putExtra("filename", "/sdcard/globetrotter/mytags/"+ TAGS[position]);
            		  view.getContext().startActivity(myIntent);
        		 }
        		 
        		});

            try {
				ExifInterface exif = new ExifInterface("/sdcard/globetrotter/mytags/"+ TAGS[position]);

				holder.text.setText(TAGS[position]);
			} catch (IOException e) {
				e.printStackTrace();
			}

            return convertView;
        }

        static class ViewHolder {
            TextView text;
        }
    }

    private static String getExifTag(ExifInterface exif,String tag){
        String attribute = exif.getAttribute(tag);
        
        return (null != attribute ? attribute : "");
}

    
    
    private Float convertToDegree(String stringDMS){
    	 Float result = null;
    	 String[] DMS = stringDMS.split(",", 3);

    	 String[] stringD = DMS[0].split("/", 2);
    	    Double D0 = new Double(stringD[0]);
    	    Double D1 = new Double(stringD[1]);
    	    Double FloatD = D0/D1;

    	 String[] stringM = DMS[1].split("/", 2);
    	 Double M0 = new Double(stringM[0]);
    	 Double M1 = new Double(stringM[1]);
    	 Double FloatM = M0/M1;
    	  
    	 String[] stringS = DMS[2].split("/", 2);
    	 Double S0 = new Double(stringS[0]);
    	 Double S1 = new Double(stringS[1]);
    	 Double FloatS = S0/S1;
    	  
    	    result = new Float(FloatD + (FloatM/60) + (FloatS/3600));
    	  
    	 return result;


    	};
    
    
    
    
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setListAdapter(new EfficientAdapter(this));
        
        registerForContextMenu(getListView());

    }

	
	
	
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
      super.onCreateContextMenu(menu, v, menuInfo);

      
      menu.add("Make Toast")
      .setOnMenuItemClickListener(new OnMenuItemClickListener() {
    	public boolean onMenuItemClick(MenuItem item) {
        String toastText = "HERRO";
        Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT).show();
        return true;
       }
      });
    } 

    //  MenuInflater inflater = getMenuInflater();
     // inflater.inflate(R.menu.tag_context_menu, menu);
      

    @Override
    public boolean onContextItemSelected(MenuItem item) {
      AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
      

      switch (item.getItemId()) {
      case R.id.tagView:
      //  editNote(info.id);
        return true;
      case R.id.tagRename:
       // deleteNote(info.id);
        return true;
      case R.id.tagDelete:
	       // deleteNote(info.id);
	        return true;
      default:
        return super.onContextItemSelected(item);
      }
    }

	
	
	
}