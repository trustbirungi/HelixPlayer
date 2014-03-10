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

public class Genres extends ListActivity {
	Cursor cursor;
	String genreName = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.playlist);

		String[] columns = { MediaStore.Audio.Genres._ID,
				MediaStore.Audio.Genres.NAME };

		cursor = getContentResolver().query(
				MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI, columns, null,
				null, null);

		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToPosition(i);
			genreName = cursor.getString(cursor.getColumnIndex("name"));
			Log.d("Genre", "> " + i + "  : " + genreName);
		}

		String[] displayFields = new String[] { MediaStore.Audio.Genres.NAME };
		int[] displayViews = new int[] { android.R.id.text1 };
		setListAdapter(new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_1, cursor, displayFields,
				displayViews));

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
			
		if (cursor.moveToPosition(position)) {
			long genreID = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Genres._ID));
			Log.d("Genre", " " + genreID);
			
	        String dataKey = MediaStore.Audio.Media.DATA;
	        String dataName = MediaStore.Audio.Media.DISPLAY_NAME;
	     	
			Bundle bun = new Bundle();
			
			bun.putLong("genreID", genreID);
			bun.putString("dataKey", dataKey);
			bun.putString("dataName", dataName);
			

			Intent in = new Intent(getApplicationContext(), GenreSongs.class);
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
