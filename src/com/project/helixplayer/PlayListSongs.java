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

public class PlayListSongs extends ListActivity {

	long playlistID = 0;
	String dataKey = "";
	String dataName = "";
	Cursor cursor;
	//String[] tracksArray;
	ArrayList<String> tracksList = new ArrayList<String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.playlist);
		Log.d("Playlist", "PlayListSongs Created");

		Bundle rec_songs = getIntent().getExtras();

		playlistID = rec_songs.getLong("playlistID");
		Log.d("Playlist", "ID " + playlistID);
		dataKey = rec_songs.getString("dataKey");
		Log.d("Playlist", dataKey);
		dataName = rec_songs.getString("dataName");
		Log.d("Playlist", dataName);
		
		ContentResolver resolver = this.getContentResolver();
		Log.d("Playlist", "Resolver created");
		Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistID);
		Log.d("Playlist", "URI created");

		Cursor tracks = resolver.query(uri, new String[] { dataKey, dataName }, null, null, null);
        Log.d("Playlist", "Cursor Created");
        
        int i = 0;
        
        if (tracks != null) {
            tracks.moveToFirst();
            
            do {
            	
            	int dataIndex = tracks.getColumnIndex(dataKey);
                int nameIndex = tracks.getColumnIndex(dataName);
                String dataPath = tracks.getString(dataIndex);
                String songTitle = tracks.getString(nameIndex);
                
                tracksList.add(songTitle);
                Log.d("Playlist", "List " + tracksList.get(i));
           
            	i++;
			} while (tracks.moveToNext());
            
        }

        String[] tracksArray = tracksList.toArray(new String[tracksList.size()]);
		
		setListAdapter(new ArrayAdapter<String>(PlayListSongs.this,
				android.R.layout.simple_list_item_1, tracksArray));

	}
	
	public  ArrayList<HashMap<String, String>> getPlaylistSongs() {
		ContentResolver resolver = this.getContentResolver();
		Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistID);
		
		Cursor tracks = resolver .query(uri, new String[] { dataKey, dataName }, null, null, null);
		
		if (tracks.moveToFirst()) {
			Log.d("CURSOR SUCCESS", "DATA WAS RETRIEVED FROM THE CURSOR");
			Log.d("SONGSLIST", "getArtistSongs has been called from SongsList class.");
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
					
					Player.PL_songs.add(song);

				} catch (Exception h) {
					Log.d("FAILED",
							tracks.getString(tracks
									.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
				}

			} while (tracks.moveToNext());

		}else {
			Log.d("CURSOR FAILURE", "NO DATA WAS RETRIEVED FROM THE CURSOR");
		}
		
		
		return Player.PL_songs;

	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Log.d("LIST ITEM CLICK", "onListItemClick has been called");
		int songIndex = position;
		
		getPlaylistSongs();
		
		Bundle bun = new Bundle();
		bun.putInt("PL_songIndex", songIndex);
		bun.putString("Pre_PlayListSongs", "Came from PlayListSongs.java");
		
		Intent i = new Intent(PlayListSongs.this, Player.class);

		i.putExtras(bun);
		startActivity(i);
	}	
	
}
