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

public class Albums extends ListActivity {
	Cursor cursor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.playlist);

		String[] columns = { android.provider.MediaStore.Audio.Albums._ID,
				android.provider.MediaStore.Audio.Albums.ALBUM };

		cursor = managedQuery(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
				columns, null, null, null);

		String[] displayFields = new String[] { MediaStore.Audio.Albums.ALBUM };
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
					MediaStore.Audio.Media.MIME_TYPE};

			String where = android.provider.MediaStore.Audio.Media.ALBUM + "=?";

			String whereVal[] = { cursor.getString(cursor
					.getColumnIndex(MediaStore.Audio.Albums.ALBUM)) };

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

