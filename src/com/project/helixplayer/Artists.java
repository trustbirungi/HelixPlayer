package com.project.helixplayer;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class Artists extends ListActivity {
	Cursor cursor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.playlist);

		String[] columns = { android.provider.MediaStore.Audio.Artists._ID,
				android.provider.MediaStore.Audio.Artists.ARTIST };

		cursor = managedQuery(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
				columns, null, null, null);

		String[] displayFields = new String[] { MediaStore.Audio.Artists.ARTIST };
		int[] displayViews = new int[] { android.R.id.text1 };
		setListAdapter(new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_1, cursor, displayFields,
				displayViews));

	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		if (cursor.moveToPosition(position)) {
			//Log.d("CURSOR SUCCESS", "DATA WAS RETRIEVED FROM THE CURSOR");
			String[] columns = { MediaStore.Audio.Media.DATA,
					MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE,
					MediaStore.Audio.Media.DISPLAY_NAME,
					MediaStore.Audio.Media.MIME_TYPE, MediaStore.Audio.Media.ALBUM_ID};

			String where = android.provider.MediaStore.Audio.Media.ARTIST + "=?";

			String whereVal[] = { cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Artists.ARTIST)) };

			String orderBy = android.provider.MediaStore.Audio.Media.TITLE;
			
			
			
			Bundle bun = new Bundle();
			bun.putStringArray("columns", columns);
			bun.putString("where", where);
			bun.putStringArray("whereVal", whereVal);
			bun.putString("orderby", orderBy);
			
			Intent in = new Intent(getApplicationContext(), SongsList.class);
			in.putExtras(bun);
			startActivity(in);
			

		}else {
			Toast.makeText(getApplicationContext(), "OKEE DOKEE", Toast.LENGTH_SHORT).show();
		}

		
	}
	
	@Override
	public void onBackPressed() {
		finish();
	}
}

