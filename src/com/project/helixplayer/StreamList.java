package com.project.helixplayer;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class StreamList extends ListActivity {
	String[] radio_stations = { "SKY.fm - Hard Rock",
			"SKY.fm - Alternative Rock", "SKY.fm - Metal",
			"SKY.fm - Indie Rock", "SKY.fm - Pop Punk" };

	String[] radioLinks = { "http://pub6.sky.fm/sky_hardrock",
			"http://pub4.sky.fm/sky_altrock", "http://pub3.sky.fm/sky_metal",
			"http://pub2.sky.fm/sky_indierock",
			"http://pub3.sky.fm/sky_poppunk" };

	Button home;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	

		// Create an ArrayAdapter for the list items
		setListAdapter(new ArrayAdapter<String>(StreamList.this,
				android.R.layout.simple_list_item_1, radio_stations));
		
		setContentView(R.layout.streamlist);
		
		home = (Button) findViewById(R.id.homeButton);
		home.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent myIntent = new Intent(getApplicationContext(), Player.class);
				setResult(100, myIntent);
				startActivityForResult(myIntent, 100);
			}
		});

	}

	@SuppressWarnings("rawtypes")
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		// Starting an activity using an intent that takes a variable of type
		// Class
		try {
			Class myClass = Class
					.forName("com.project.helixplayer.MusicStream");

			Bundle radio = new Bundle();
			radio.putString("radiolink", radioLinks[position]);
			radio.putString("radio_station", radio_stations[position]);

			Intent myIntent = new Intent(StreamList.this, myClass);

			myIntent.putExtras(radio);

			startActivity(myIntent);

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onBackPressed() {

		Intent myIntent = new Intent(getApplicationContext(), Player.class);
		setResult(100, myIntent);
		startActivityForResult(myIntent, 100);

	}

	
	

}
