package com.project.helixplayer;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

import android.os.Environment;

public class SongsManager {
	// SDCard Path
	// final String MEDIA_PATH = new String("/mnt/sdcard/DCIM/MUSIC");
	//final String MEDIA_PATH = new String("/mnt/sdcard/");
	
	final String MEDIA_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();

	// final String MEDIA_PATH = new
	// String(MediaStore.Audio.Media.getContentUri("external").toString());

	private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
	File home = new File(MEDIA_PATH);

	// Constructor
	public SongsManager() {

	}

	/**
	 * Function to read all mp3 files from sdcard and store the details in
	 * ArrayList
	 * */


	public ArrayList<HashMap<String, String>> getPlayList(File dir) {
		String Pattern = ".mp3";
		File listFile[] = dir.listFiles();
		if (listFile != null) {
			for (int i = 0; i < listFile.length; i++) {
				if (listFile[i].isDirectory()) {
					getPlayList(listFile[i]);
				} else {
					if (listFile[i].getName().endsWith(Pattern)) {
						// Do what ever u want
						// add the path to hash map

						HashMap<String, String> song = new HashMap<String, String>();
						song.put(
								"songTitle",
								listFile[i].getName().substring(0,
										(listFile[i].getName().length() - 4)));
						song.put("songPath", listFile[i].getPath());

						// Adding each song to SongList
						songsList.add(song);

					}
				}
			}
		}

		return songsList;

	}

	/**
	 * Class to filter files which are having .mp3 extension
	 * */
	class FileExtensionFilter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			return (name.endsWith(".mp3") || name.endsWith(".MP3"));
		}
	}
}
