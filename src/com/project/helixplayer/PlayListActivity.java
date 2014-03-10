package com.project.helixplayer;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class PlayListActivity extends ListActivity {

	String title = "";
	String path = "";
	String success = "";
	boolean cameFromPlayer = false;

	// Songs list
	public ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.playlist);

		final ArrayList<HashMap<String, String>> songsListData = new ArrayList<HashMap<String, String>>();

		Bundle extras = getIntent().getExtras();
		if (extras != null && extras.containsKey("Pre_Player")) {
			cameFromPlayer = true;
		}

		SongsManager plm = new SongsManager();
		// get all songs from sdcard
		this.songsList = plm.getPlayList(plm.home);

		// looping through playlist
		for (int i = 0; i < songsList.size(); i++) {
			// creating new HashMap
			HashMap<String, String> song = songsList.get(i);

			// adding HashList to ArrayList
			songsListData.add(song);
			Player.PLA_songs.add(song);
		}

		// lv = (ListView) findViewById(R.id.list);

		// Adding menuItems to ListView
		ListAdapter adapter = new SimpleAdapter(this, songsListData,
				R.layout.playlist_item, new String[] { "songTitle" },
				new int[] { R.id.songTitle });

		setListAdapter(adapter);
		// lv.setAdapter(adapter);

		// selecting single ListView item
		final ListView lv = getListView();
		registerForContextMenu(lv);
		lv.setClickable(true);

		lv.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {

				title = songsListData.get(arg2).get("songTitle");
				path = songsListData.get(arg2).get("songPath");

				openContextMenu(lv);

				return true;
			}
		});

		// listening to single listitem click
		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				if (cameFromPlayer) {

					// getting listitem index
					int songIndex = position;

					// Starting new intent
					Intent in = new Intent(getApplicationContext(),
							Player.class);

					// Sending songIndex to PlayerActivity
					in.putExtra("songIndex", songIndex);
					in.putExtra("Pre_Finish", "Just finished PlayListActivity.java");
					setResult(100, in);

					// Closing PlayListView
					finish();
				} else {
					int songIndex = position;

					Bundle bun = new Bundle();
					bun.putInt("PLA_songIndex", songIndex);
					bun.putString("Pre_PlayListActivity",
							"Came from PlayListActivity.java");

					Intent i = new Intent(PlayListActivity.this, Player.class);

					i.putExtras(bun);
					startActivity(i);

				}
			}
		});

	}

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		menu.setHeaderTitle(title);

		String[] menuItems = getResources().getStringArray(R.array.menu);
		for (int i = 0; i < menuItems.length; i++) {
			menu.add(Menu.NONE, i, i, menuItems[i]);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		int menuItemIndex = item.getItemId();
		String[] menuItems = getResources().getStringArray(R.array.menu);
		String menuItemName = menuItems[menuItemIndex];

		if (menuItemName.equals("Add To Dropbox")) {
			Bundle uploadData = new Bundle();
			uploadData.putString("title", title);
			uploadData.putString("path", path);

			Intent dropIntent = new Intent(getApplicationContext(),
					DropboxBackup.class);

			dropIntent.putExtras(uploadData);
			startActivity(dropIntent);

		} else if (menuItemName.equals("Set As Ringtone")
				|| menuItemName.equals("Set As Alarm Sound")
				|| menuItemName.equals("Set As Notification Sound")) {

			setSongAs(path, title, menuItemName);
		}

		return true;
	}

	public boolean setSongAs(String songPath, String songTitle,
			String notif_type) {

		ContentValues values = new ContentValues();
		values.put(MediaStore.MediaColumns.DATA, songPath);
		values.put(MediaStore.MediaColumns.TITLE, songTitle);
		values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
		values.put(MediaStore.Audio.Media.ARTIST, "");
		values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
		values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
		values.put(MediaStore.Audio.Media.IS_ALARM, true);
		values.put(MediaStore.Audio.Media.IS_MUSIC, true);

		// Insert it into the database and set as active ringtone
		Uri uri = MediaStore.Audio.Media.getContentUriForPath(songPath);
		Uri newUri = getContentResolver().insert(uri, values);

		if (notif_type.equals("Set As Ringtone")) {
			try {
				RingtoneManager.setActualDefaultRingtoneUri(
						PlayListActivity.this, RingtoneManager.TYPE_RINGTONE,
						newUri);

				Toast.makeText(getApplicationContext(),
						songTitle + " has been set as ringtone",
						Toast.LENGTH_SHORT).show();
			} catch (Exception ex) {
				Log.d("Exception", ex.getMessage());
			}
		} else if (notif_type.equals("Set As Alarm Sound")) {
			try {
				RingtoneManager.setActualDefaultRingtoneUri(
						PlayListActivity.this, RingtoneManager.TYPE_ALARM,
						newUri);

				Toast.makeText(getApplicationContext(),
						songTitle + " has been set as alarm sound",
						Toast.LENGTH_SHORT).show();
			} catch (Exception ex) {
				Log.d("Exception", ex.getMessage());
			}
		} else if (notif_type.equals("Set As Notification Sound")) {
			try {
				RingtoneManager.setActualDefaultRingtoneUri(
						PlayListActivity.this,
						RingtoneManager.TYPE_NOTIFICATION, newUri);

				Toast.makeText(getApplicationContext(),
						songTitle + " has been set as notification sound",
						Toast.LENGTH_SHORT).show();
			} catch (Exception ex) {
				Log.d("Exception", ex.getMessage());
			}
		}

		return true;
	}

	@Override
	public void onBackPressed() {

		Intent myIntent = new Intent(getApplicationContext(), Starter.class);
		startActivity(myIntent);

	}

}
