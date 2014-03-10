package com.project.helixplayer;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

public class Starter extends Activity {
	
	String soundName = "";
	
	public String[] activitynames = {"Songs", "Artists", "Albums", "Genres", "Playlists", "Internet Radio"};
	
	int[] sound_index = {0, 1, 2, 3, 4, 5};
	
	
	public GridView gridview;
	
	public String[] Sounds;
	
	Button playerButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher);
        
        gridview = (GridView) findViewById(R.id.gridView1);
        gridview.setAdapter(new ButtonAdapter(this));
        registerForContextMenu(gridview);
        
        playerButton = (Button) findViewById(R.id.buttonPlayer);
        playerButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent myIntent = new Intent(getApplicationContext(), Player.class);
				startActivity(myIntent);
			}
		});
        
		
		gridview.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				switch (sound_index[arg2]) {
		  		case 0:
		  			Intent songsIntent = new Intent(getApplicationContext(), PlayListActivity.class);
					startActivityForResult(songsIntent, 100);
		  			break;
		  			
		  		case 1:
		  			Intent artistsIntent = new Intent(getApplicationContext(), Artists.class);
					startActivityForResult(artistsIntent, 100);
		  			break;
		  			
		  		case 2:
		  			Intent albumsIntent = new Intent(getApplicationContext(), Albums.class);
					startActivityForResult(albumsIntent, 100);
		  			break;
		  			
		  		case 3:
		  			Intent genresIntent = new Intent(getApplicationContext(), Genres.class);
					startActivityForResult(genresIntent, 100);
		  			break;
		  			
		  		case 4:
		  			Intent playlistsIntent = new Intent(getApplicationContext(), Playlists.class);
					startActivityForResult(playlistsIntent, 100);
			  		
		  			break;
		  			
		  		case 5:
		  			Intent streamIntent = new Intent(getApplicationContext(), StreamList.class);
					startActivity(streamIntent);
		  			break;
		  			
		  		}
				

			}
		});
		
		
	
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.activity_home, menu);
        return true;
    }
    
    
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
	super.onCreateContextMenu(menu, v, menuInfo);
	}
    
    
    @Override
	public boolean onContextItemSelected(MenuItem item) {
    	    
	    
    	return true;
	}
    
   
    public class ButtonAdapter extends BaseAdapter {
   	 private Context mContext;

   	 // Gets the context so it can be used later
   	 public ButtonAdapter(Context c) {
   	  mContext = c;
   	 }
   	 

   	 // Total number of things contained within the adapter
   	 public int getCount() {
   	  return activitynames.length;
   	 }

   	  // Require for structure, not really used in my code.
   	 public Object getItem(int position) {
   	  return null;
   	 }

   	 
   	 // Can be used to get the id of an item in the adapter for manual control.
   	 public long getItemId(int position) {
   	  return position;
   	 }
   	 
   	 public View getView(int position, View convertView, ViewGroup parent) {
   	  Button btn;
   	  if (convertView == null) {
   	   // if it's not recycled, initialize some attributes
   	   btn = new Button(mContext);
   	   btn.setLayoutParams(new GridView.LayoutParams(220, 160));
   	   btn.setPadding(15, 15, 15, 15);
   	   btn.setHeight(50);
   	   btn.setFocusable(false);
   	   btn.setClickable(false);
   	   }
   	  else {
   	   btn = (Button) convertView;
   	  }
 
   	  btn.setText(activitynames[position]);
   	  btn.setTextColor(Color.WHITE);
   	  btn.setBackgroundResource(R.drawable.black_button);
   	  btn.setId(position);
   	  btn.setTextSize(20);
   	  return btn;
   	}
   	 
  }




    
}

