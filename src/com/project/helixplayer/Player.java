package com.project.helixplayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class Player extends Activity implements OnCompletionListener,
		SeekBar.OnSeekBarChangeListener {

	private ImageButton btnPlay;
	private ImageButton btnForward;
	private ImageButton btnBackward;
	private ImageButton btnNext;
	private ImageButton btnPrevious;
	private ImageButton btnPlaylist;
	private ImageButton btnRepeat;
	private ImageButton btnShuffle;
	private ImageButton buttonStream;
	private SeekBar songProgressBar;
	private TextView songTitleLabel;
	private TextView songCurrentDurationLabel;
	private TextView songTotalDurationLabel;
	private ImageView album_art;
	// Media Player
	private MediaPlayer mp;
	// Handler to update UI timer, progress bar etc,.
	private Handler mHandler = new Handler();;
	private SongsManager songManager;
	private Utilities utils;
	private SongsList s_list;
	private int seekForwardTime = 5000; // 5000 milliseconds
	private int seekBackwardTime = 5000; // 5000 milliseconds
	private int currentSongIndex = 0;
	private boolean isShuffle = false;
	private boolean isRepeat = false;
	public ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
	public static ArrayList<HashMap<String, String>> songs = new ArrayList<HashMap<String, String>>();
	public static ArrayList<HashMap<String, String>> SL_songs = new ArrayList<HashMap<String, String>>();
	public static ArrayList<HashMap<String, String>> PLA_songs = new ArrayList<HashMap<String, String>>();
	public static ArrayList<HashMap<String, String>> PL_songs = new ArrayList<HashMap<String, String>>();
	public static ArrayList<HashMap<String, String>> G_songs = new ArrayList<HashMap<String,String>>();
	public static ArrayList<Integer> SL_Album_ids = new ArrayList<Integer>();
	String recTitle = "";
	String songPath = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player);

		// All player buttons
		btnPlay = (ImageButton) findViewById(R.id.btnPlay);
		btnForward = (ImageButton) findViewById(R.id.btnForward);
		btnBackward = (ImageButton) findViewById(R.id.btnBackward);
		btnNext = (ImageButton) findViewById(R.id.btnNext);
		btnPrevious = (ImageButton) findViewById(R.id.btnPrevious);
		btnPlaylist = (ImageButton) findViewById(R.id.btnPlaylist);
		btnRepeat = (ImageButton) findViewById(R.id.btnRepeat);
		btnShuffle = (ImageButton) findViewById(R.id.btnShuffle);
		buttonStream = (ImageButton) findViewById(R.id.buttonStream);
		songProgressBar = (SeekBar) findViewById(R.id.songProgressBar);
		songTitleLabel = (TextView) findViewById(R.id.songTitle);
		songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
		songTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);
		album_art = (ImageView) findViewById(R.id.album_art);
		Bitmap album_bm;

		// Mediaplayer
		mp = new MediaPlayer();
		songManager = new SongsManager();
		

		Log.d("SONGSLIST", "Instance of SongsList has been created");

		utils = new Utilities();

		songsList.clear();

		// Listeners
		songProgressBar.setOnSeekBarChangeListener(this); // Important
		mp.setOnCompletionListener(this); // Important
		mp.reset();
		// Getting all songs list
		//songsList = songManager.getPlayList(songManager.home);

		/**
		 * Play button click event plays a song and changes button to pause
		 * image pauses a song and changes button to play image
		 * */
		btnPlay.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				// check for already playing
				if (mp.isPlaying()) {
					if (mp != null) {
						mp.pause();
						// Changing button image to play button
						btnPlay.setImageResource(R.drawable.btn_play);
					}
				} else {
					// Resume song
					if (mp != null) {
						mp.start();
						// Changing button image to pause button
						btnPlay.setImageResource(R.drawable.btn_pause);
					}
				}

			}
		});

		/**
		 * Forward button click event Forwards song specified seconds
		 * */
		btnForward.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				// get current song position
				int currentPosition = mp.getCurrentPosition();
				// check if seekForward time is lesser than song duration
				if (currentPosition + seekForwardTime <= mp.getDuration()) {
					// forward song
					mp.seekTo(currentPosition + seekForwardTime);
				} else {
					// forward to end position
					mp.seekTo(mp.getDuration());
				}
			}
		});

		/**
		 * Backward button click event Backward song to specified seconds
		 * */
		btnBackward.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				// get current song position
				int currentPosition = mp.getCurrentPosition();
				// check if seekBackward time is greater than 0 sec
				if (currentPosition - seekBackwardTime >= 0) {
					// forward song
					mp.seekTo(currentPosition - seekBackwardTime);
				} else {
					// backward to starting position
					mp.seekTo(0);
				}

			}
		});

		/**
		 * Next button click event Plays next song by taking currentSongIndex +
		 * 1
		 * */
		btnNext.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				// check if next song is there or not
				if (currentSongIndex < (songsList.size() - 1)) {
					playSong(currentSongIndex + 1);
					currentSongIndex = currentSongIndex + 1;
				} else {
					// play first song
					playSong(0);
					currentSongIndex = 0;
				}

			}
		});

		/**
		 * Back button click event Plays previous song by currentSongIndex - 1
		 * */
		btnPrevious.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				if (currentSongIndex > 0) {
					playSong(currentSongIndex - 1);
					currentSongIndex = currentSongIndex - 1;
				} else {
					// play last song
					playSong(songsList.size() - 1);
					currentSongIndex = songsList.size() - 1;
				}

			}
		});

		/**
		 * Button Click event for Repeat button Enables repeat flag to true
		 * */
		btnRepeat.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				if (isRepeat) {
					isRepeat = false;
					Toast.makeText(getApplicationContext(), "Repeat is OFF",
							Toast.LENGTH_SHORT).show();
					btnRepeat.setImageResource(R.drawable.btn_repeat);
				} else {
					// make repeat to true
					isRepeat = true;
					Toast.makeText(getApplicationContext(), "Repeat is ON",
							Toast.LENGTH_SHORT).show();
					// make shuffle to false
					isShuffle = false;
					btnRepeat.setImageResource(R.drawable.btn_repeat_focused);
					btnShuffle.setImageResource(R.drawable.btn_shuffle);
				}
			}
		});

		/**
		 * Button Click event for Shuffle button Enables shuffle flag to true
		 * */
		btnShuffle.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				if (isShuffle) {
					isShuffle = false;
					Toast.makeText(getApplicationContext(), "Shuffle is OFF",
							Toast.LENGTH_SHORT).show();
					btnShuffle.setImageResource(R.drawable.btn_shuffle);
				} else {
					// make repeat to true
					isShuffle = true;
					Toast.makeText(getApplicationContext(), "Shuffle is ON",
							Toast.LENGTH_SHORT).show();
					// make shuffle to false
					isRepeat = false;
					btnShuffle.setImageResource(R.drawable.btn_shuffle_focused);
					btnRepeat.setImageResource(R.drawable.btn_repeat);
				}
			}
		});

		/**
		 * Button Click event for Play list click event Launches list activity
		 * which displays list of songs
		 * */
		btnPlaylist.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
				Intent i = new Intent(getApplicationContext(),
						PlayListActivity.class);
				i.putExtra("Pre_Player", "Player.java");
				startActivityForResult(i, 100);
			}
		});

		buttonStream.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Intent streamIntent = new Intent(getApplicationContext(),
						StreamList.class);
				startActivity(streamIntent);
			}
		});

//		Handling bundles received through intents
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			if (extras.containsKey("Pre_SongsList")) {
				Log.d("BUNDLE", "Bundle has extras");

				currentSongIndex = extras.getInt("SL_songIndex");

				songsList.clear();

				for (int i = 0; i < SL_songs.size(); i++) {
					songsList.add(SL_songs.get(i));
				}

				Log.d("SONGSLIST", "songsList has been populated");

				if (songsList.isEmpty()) {
					Log.d("SONGSLIST", "ArrayList has no songs");
				} else {
					Log.d("SONGSLIST", "ArrayList has songs");
				}
				
				mp.reset();
				long x_id = 4;
				try {
				//album_bm = s_list.getAlbumart(SL_Album_ids.get(currentSongIndex).longValue());
				album_bm = s_list.getAlbumart(x_id);
				Log.d ("ALBUM ART", "Album_bm has been set");
				
					album_bm =  Bitmap.createScaledBitmap(album_bm, 30, 30, true);
					album_art.setImageBitmap(album_bm);
					
				
				}catch(Exception ex) {
					Log.d("ALBUM ART", ex.toString());
				}
				
				playSong(currentSongIndex);
			} else if (extras.containsKey("Pre_PlayListActivity")) {
				currentSongIndex = extras.getInt("PLA_songIndex");

				songsList.clear();

				for (int i = 0; i < PLA_songs.size(); i++) {
					songsList.add(PLA_songs.get(i));
				}

				Log.d("SONGSLIST", "songsList has been populated");

				if (songsList.isEmpty()) {
					Log.d("SONGSLIST", "ArrayList has no songs");
				} else {
					Log.d("SONGSLIST", "ArrayList has songs");
				}
				mp.reset();
				playSong(currentSongIndex);
			} else if (extras.containsKey("Pre_PlayListSongs")) {
				currentSongIndex = extras.getInt("PL_songIndex");

				songsList.clear();

				for (int i = 0; i < PL_songs.size(); i++) {
					songsList.add(PL_songs.get(i));
				}

				Log.d("SONGSLIST", "songsList has been populated");

				if (songsList.isEmpty()) {
					Log.d("SONGSLIST", "ArrayList has no songs");
				} else {
					Log.d("SONGSLIST", "ArrayList has songs");
				}
				mp.reset();
				playSong(currentSongIndex);
				
			} else if (extras.containsKey("Pre_GenreSongs")) {
				currentSongIndex = extras.getInt("G_songIndex");

				songsList.clear();

				for (int i = 0; i < G_songs.size(); i++) {
					songsList.add(G_songs.get(i));
				}

				Log.d("SONGSLIST", "songsList has been populated");

				if (songsList.isEmpty()) {
					Log.d("SONGSLIST", "ArrayList has no songs");
				} else {
					Log.d("SONGSLIST", "ArrayList has songs");
				}
				mp.reset();
				playSong(currentSongIndex);
				
			} else if (extras.containsKey("Pre_Finish")) {
				currentSongIndex = extras.getInt("songIndex");

				songsList.clear();

				songsList = songManager.getPlayList(songManager.home);

				Log.d("SONGSLIST", "songsList has been populated");

				if (songsList.isEmpty()) {
					Log.d("SONGSLIST", "ArrayList has no songs");
				} else {
					Log.d("SONGSLIST", "ArrayList has songs");
				}
				mp.reset();
				playSong(currentSongIndex);
				
			} else {
				Log.d("BUNDLE", "Bundle has no extras");
				songsList = songManager.getPlayList(songManager.home);
			}
		}

	}

	/**
	 * Receiving song index from playlist view and play the song
	 * */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 100) {
			currentSongIndex = data.getExtras().getInt("songIndex");
			songsList.clear();
			songsList = songManager.getPlayList(songManager.home);
			mp.reset();
			// play selected song
			playSong(currentSongIndex);
		}

	}

	/**
	 * Function to play a song
	 * 
	 * @param songIndex
	 *            - index of song
	 * */
	public void playSong(int songIndex) {
		mp.reset();
		// Play song
		try {
			if(songsList.isEmpty()) {
				songsList = songManager.getPlayList(songManager.home);
			}
			mp.reset();
			mp.setDataSource(songsList.get(songIndex).get("songPath"));
			mp.prepare();
			mp.start();
			// Displaying Song title
			String songTitle = songsList.get(songIndex).get("songTitle");
			songTitleLabel.setText(songTitle);

			// Changing Button Image to pause image
			btnPlay.setImageResource(R.drawable.btn_pause);

			// set Progress bar values
			songProgressBar.setProgress(0);
			songProgressBar.setMax(100);

			// Updating progress bar
			updateProgressBar();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Update timer on seekbar
	 * */
	public void updateProgressBar() {
		mHandler.postDelayed(mUpdateTimeTask, 100);
	}

	/**
	 * Background Runnable thread
	 * */
	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {
			long totalDuration = mp.getDuration();
			long currentDuration = mp.getCurrentPosition();

			// Displaying Total Duration time
			songTotalDurationLabel.setText(""
					+ utils.milliSecondsToTimer(totalDuration));
			// Displaying time completed playing
			songCurrentDurationLabel.setText(""
					+ utils.milliSecondsToTimer(currentDuration));

			// Updating progress bar
			int progress = (int) (utils.getProgressPercentage(currentDuration,
					totalDuration));
			// Log.d("Progress", ""+progress);
			songProgressBar.setProgress(progress);

			// Running this thread after 100 milliseconds
			mHandler.postDelayed(this, 100);
		}
	};

	/**
	 * 
	 * */
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromTouch) {

	}

	/**
	 * When user starts moving the progress handler
	 * */
	public void onStartTrackingTouch(SeekBar seekBar) {
		// remove message Handler from updating progress bar
		mHandler.removeCallbacks(mUpdateTimeTask);
	}

	/**
	 * When user stops moving the progress hanlder
	 * */
	public void onStopTrackingTouch(SeekBar seekBar) {
		mHandler.removeCallbacks(mUpdateTimeTask);
		int totalDuration = mp.getDuration();
		int currentPosition = utils.progressToTimer(seekBar.getProgress(),
				totalDuration);

		// forward or backward to certain seconds
		mp.seekTo(currentPosition);

		// update timer progress again
		updateProgressBar();
	}

	/**
	 * On Song Playing completed if repeat is ON play same song again if shuffle
	 * is ON play random song
	 * */
	public void onCompletion(MediaPlayer arg0) {

		// check for repeat is ON or OFF
		if (isRepeat) {
			// repeat is on play same song again
			playSong(currentSongIndex);
		} else if (isShuffle) {
			// shuffle is on - play a random song
			Random rand = new Random();
			currentSongIndex = rand.nextInt((songsList.size() - 1) - 0 + 1) + 0;
			playSong(currentSongIndex);
		} else {
			// no repeat or shuffle ON - play next song
			if (currentSongIndex < (songsList.size() - 1)) {
				playSong(currentSongIndex + 1);
				currentSongIndex = currentSongIndex + 1;
			} else {
				// play first song
				playSong(0);
				currentSongIndex = 0;
			}
		}
	}

	@Override
	public void onBackPressed() {
		Intent myIntent = new Intent(getApplicationContext(), Starter.class);
		startActivity(myIntent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mp.release();
	}

}