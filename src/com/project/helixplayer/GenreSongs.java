package com.project.helixplayer;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class GenreSongs extends ListActivity {

	long genreID = 0;
	String dataKey = "";
	String dataName = "";
	Cursor cursor;
	//String[] tracksArray;
	ArrayList<String> tracksList = new ArrayList<String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.playlist);
		Log.d("Genre", "GenreSongs Created");

		Bundle rec_songs = getIntent().getExtras();

		genreID = rec_songs.getLong("genreID");
		Log.d("Genre", "ID " + genreID);
		dataKey = rec_songs.getString("dataKey");
		Log.d("Genre", dataKey);
		dataName = rec_songs.getString("dataName");
		Log.d("Genre", dataName);
		
		ContentResolver resolver = this.getContentResolver();
		Log.d("Genre", "Resolver created");
		Uri uri = MediaStore.Audio.Genres.Members.getContentUri("external", genreID);
		Log.d("Genre", "URI created");

		Cursor tracks = resolver.query(uri, new String[] { dataKey, dataName }, null, null, null);
        Log.d("Genre", "Cursor Created");
        
        int i = 0;
        
        if (tracks != null) {
            tracks.moveToFirst();
            
            do {
            	
            	int dataIndex = tracks.getColumnIndex(dataKey);
                int nameIndex = tracks.getColumnIndex(dataName);
                String dataPath = tracks.getString(dataIndex);
                String songTitle = tracks.getString(nameIndex);
                
                tracksList.add(songTitle);
                Log.d("Genre", "List " + tracksList.get(i));
           
            	i++;
			} while (tracks.moveToNext());
            
        }

        String[] tracksArray = tracksList.toArray(new String[tracksList.size()]);
		
		setListAdapter(new ArrayAdapter<String>(GenreSongs.this,
				android.R.layout.simple_list_item_1, tracksArray));

	}
	
	public  ArrayList<HashMap<String, String>> getGenreSongs() {
		ContentResolver resolver = this.getContentResolver();
		Uri uri = MediaStore.Audio.Genres.Members.getContentUri("external", genreID);
		
		Cursor tracks = resolver .query(uri, new String[] { dataKey, dataName }, null, null, null);
		
		if (tracks.moveToFirst()) {
			
			do {
				HashMap<String, String> song = new HashMap<String, String>();

				try {
					song.put(
							"songTitle",
							tracks.getString(tracks
									.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
					song.put(
							"songPath",
							tracks.getString(tracks
									.getColumnIndex(MediaStore.Audio.Media.DATA)));
					
					Player.G_songs.add(song);

				} catch (Exception h) {
					Log.d("FAILED",
							tracks.getString(tracks
									.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
				}

			} while (tracks.moveToNext());

		}else {
			Log.d("CURSOR FAILURE", "NO DATA WAS RETRIEVED FROM THE CURSOR");
		}
		
		
		return Player.G_songs;

	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Log.d("LIST ITEM CLICK", "onListItemClick has been called");
		int songIndex = position;
		
		getGenreSongs();
		
		Bundle bun = new Bundle();
		bun.putInt("G_songIndex", songIndex);
		bun.putString("Pre_GenreSongs", "Came from GenreSongs.java");
		
		Intent i = new Intent(GenreSongs.this, Player.class);

		i.putExtras(bun);
		startActivity(i);
	}	
	
}

