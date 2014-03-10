package com.project.helixplayer;

import java.io.FileDescriptor;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class SongsList extends ListActivity {

	String[] columns = {};
	String where = "";
	String orderBy = "";
	String[] whereVal = {};
	Cursor cursor;
	ArrayList<Integer> album_ids = new ArrayList<Integer>();


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.playlist);

		Bundle rec_songs = getIntent().getExtras();

		columns = rec_songs.getStringArray("columns");
		where = rec_songs.getString("where");
		orderBy = rec_songs.getString("orderBy");
		whereVal = rec_songs.getStringArray("whereVal");
		

		cursor = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				columns, where, whereVal, orderBy);

		String[] displayFields = new String[] { MediaStore.Audio.Media.DISPLAY_NAME };
		int[] displayViews = new int[] { android.R.id.text1 };

		setListAdapter(new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_1, cursor, displayFields,
				displayViews));

	}
	
	public  ArrayList<HashMap<String, String>> getArtistSongs() {
		Cursor cursor = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				columns, where, whereVal, orderBy);
		Player.SL_songs.clear();
		if (cursor.moveToFirst()) {
			Log.d("CURSOR SUCCESS", "DATA WAS RETRIEVED FROM THE CURSOR");
			Log.d("SONGSLIST", "getArtistSongs has been called from SongsList class.");
			do {
				HashMap<String, String> song = new HashMap<String, String>();
				
				try {
					song.put(
							"songTitle",
							cursor.getString(cursor
									.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
					song.put(
							"songPath",
							cursor.getString(cursor
									.getColumnIndex(MediaStore.Audio.Media.DATA)));
					
					Log.d("SUCCESS",
							cursor.getString(cursor
									.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
					Log.d("PATH",
							cursor.getString(cursor
									.getColumnIndex(MediaStore.Audio.Media.DATA)));
					
					Player.SL_songs.add(song);
					Player.SL_Album_ids.add(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
					
					Log.d("ALBUM ART", "Album ID: " + cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
					
				} catch (Exception h) {
					Log.d("FAILED",
							cursor.getString(cursor
									.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
				}

			} while (cursor.moveToNext());

		}else {
			Log.d("CURSOR FAILURE", "NO DATA WAS RETRIEVED FROM THE CURSOR");
		}
		
		
		return Player.songs;

	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Log.d("LIST ITEM CLICK", "onListItemClick has been called");
		int songIndex = position;
		
		getArtistSongs();
		
		Bundle bun = new Bundle();
		bun.putInt("SL_songIndex", songIndex);
		bun.putString("Pre_SongsList", "Came from SongsList.java");
		
		Intent i = new Intent(SongsList.this, Player.class);

		i.putExtras(bun);
		startActivity(i);
	}	
	
	
	public Bitmap getAlbumart(Long album_id) 
	   {
	        Bitmap bm = null;
	        Log.d("ALBUM ART", "BM IS CREATED AND NULL");
	        try 
	        {
	            final Uri sArtworkUri = Uri
	                .parse("content://media/external/audio/albumart");

	            Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);

	            ParcelFileDescriptor pfd = getApplicationContext().getContentResolver()
	                .openFileDescriptor(uri, "r");

	            if (pfd != null) 
	            {
	                FileDescriptor fd = pfd.getFileDescriptor();
	                bm = BitmapFactory.decodeFileDescriptor(fd);
	                Log.d("ALBUM ART", "PFG IS NOT NULL");
	            }
	    } catch (Exception e) {
	    }
	    return bm;
	}
	
}
