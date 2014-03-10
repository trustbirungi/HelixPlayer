package com.project.helixplayer;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class Playlists extends ListActivity {
	Cursor cursor;
	String playListName = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.playlist);

		String[] columns = { MediaStore.Audio.Playlists._ID,
				MediaStore.Audio.Playlists.NAME };

		cursor = getContentResolver().query(
				MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, columns, null,
				null, null);

		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToPosition(i);
			playListName = cursor.getString(cursor.getColumnIndex("name"));
			Log.d("Playlist", "> " + i + "  : " + playListName);
		}

		String[] displayFields = new String[] { MediaStore.Audio.Playlists.NAME };
		int[] displayViews = new int[] { android.R.id.text1 };
		setListAdapter(new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_1, cursor, displayFields,
				displayViews));

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
			
		if (cursor.moveToPosition(position)) {
			long playlistID = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Playlists._ID));
			Log.d("Playlist", " " + playlistID);
			
	        String dataKey = MediaStore.Audio.Media.DATA;
	        String dataName = MediaStore.Audio.Media.DISPLAY_NAME;
	        
			Bundle bun = new Bundle();
			
			bun.putLong("playlistID", playlistID);
			bun.putString("dataKey", dataKey);
			bun.putString("dataName", dataName);
			

			Intent in = new Intent(getApplicationContext(), PlayListSongs.class);
			in.putExtras(bun);
			startActivity(in);

		} else {
			Toast.makeText(getApplicationContext(), "OKEE DOKEE",
					Toast.LENGTH_SHORT).show();
		}

	}
	
	

	@Override
	public void onBackPressed() {
		finish();
	}
}
