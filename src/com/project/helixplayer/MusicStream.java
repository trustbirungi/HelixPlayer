package com.project.helixplayer;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class MusicStream extends Activity implements
  MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener,
  MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener {

 private String TAG = getClass().getSimpleName();
 private MediaPlayer mp = null;

 private ImageButton btnPlay;
 private ImageButton btnPause;
 private ImageButton btnStop;
 private TextView header;
 
 String radiolink = "";
 String radio_station = "";
 

 @Override
 public void onCreate(Bundle icicle) {
  super.onCreate(icicle);
  setContentView(R.layout.streaming);
  
  
  
  btnPlay = (ImageButton) findViewById(R.id.btnPlay);
  btnPause = (ImageButton) findViewById(R.id.btnPause);
  btnStop = (ImageButton) findViewById(R.id.btnStop);
  header = (TextView) findViewById(R.id.songTitle);

  btnPlay.setOnClickListener(new View.OnClickListener() {
   public void onClick(View view) {
    play();
   }
  });

  btnPause.setOnClickListener(new View.OnClickListener() {
   public void onClick(View view) {
    pause();
   }
  });

  btnStop.setOnClickListener(new View.OnClickListener() {
   public void onClick(View view) {
    stop();
   }
  });
  
  
  
  Bundle received_link = getIntent().getExtras();
  radiolink = received_link.getString("radiolink");
  radio_station = received_link.getString("radio_station");
  
  header.setText(radio_station);
  
  
  
 }

 private void play() {
  //Uri myUri = Uri.parse("http://fr3.ah.fm:9000/");
	Uri myUri = Uri.parse(radiolink);
  try {
   if (mp == null) {
    this.mp = new MediaPlayer();
   } else {
    mp.stop();
    mp.reset();
   }
   mp.setDataSource(this, myUri); // Go to Initialized state
   mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
   mp.setOnPreparedListener(this);
   mp.setOnBufferingUpdateListener(this);

   mp.setOnErrorListener(this);
   mp.prepareAsync();

   Log.d(TAG, "LoadClip Done");
  } catch (Throwable t) {
   Log.d(TAG, t.toString());
  }
 }

 public void onPrepared(MediaPlayer mp) {
  Log.d(TAG, "Stream is prepared");
  mp.start();
 }

 private void pause() {
  mp.pause();
 }

 private void stop() {
  mp.stop();

 }

 @Override
 public void onDestroy() {
  super.onDestroy();
  stop();

 }

 public void onCompletion(MediaPlayer mp) {
  stop();
 }

 public boolean onError(MediaPlayer mp, int what, int extra) {
  StringBuilder sb = new StringBuilder();
  sb.append("Media Player Error: ");
  switch (what) {
  case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
   sb.append("Not Valid for Progressive Playback");
   break;
  case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
   sb.append("Server Died");
   break;
  case MediaPlayer.MEDIA_ERROR_UNKNOWN:
   sb.append("Unknown");
   break;
  default:
   sb.append(" Non standard (");
   sb.append(what);
   sb.append(")");
  }
  sb.append(" (" + what + ") ");
  sb.append(extra);
  Log.e(TAG, sb.toString());
  return true;
 }

 public void onBufferingUpdate(MediaPlayer mp, int percent) {
  Log.d(TAG, "PlayerService onBufferingUpdate : " + percent + "%");
 }
 
 @SuppressWarnings("rawtypes")
@Override
	public void onBackPressed() {
	 try {
			Class myClass = Class.forName("com.project.helixplayer.StreamList");

			Intent myIntent = new Intent(MusicStream.this, myClass);
			
			startActivity(myIntent);
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		//super.onBackPressed();
	}

    }