package com.project.helixplayer;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileInfo;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;

public class DropboxBackup extends Activity {

	private static final String appKey = "appKey"; //Replace with your Dropbox app key
	private static final String appSecret = "appSecret"; //Replace with your Dropbox app secret

	private static final int REQUEST_LINK_TO_DBX = 0;

	private TextView mTestOutput;
	private Button mLinkButton;
	private DbxAccountManager mDbxAcctMgr;
	String title = "";
	String path = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hello_dropbox);
		mTestOutput = (TextView) findViewById(R.id.test_output);
		mLinkButton = (Button) findViewById(R.id.link_button);
		mLinkButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onClickLinkToDropbox();
			}
		});

		mDbxAcctMgr = DbxAccountManager.getInstance(getApplicationContext(),
				appKey, appSecret);
		
		Bundle uploadData = getIntent().getExtras();
		title = uploadData.getString("title");
		path = uploadData.getString("path");
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mDbxAcctMgr.hasLinkedAccount()) {
			showLinkedView();
			doDropboxTest();
		} else {
			showUnlinkedView();
		}
	}

	private void showLinkedView() {
		mLinkButton.setVisibility(View.GONE);
		mTestOutput.setVisibility(View.VISIBLE);
	}

	public void showUnlinkedView() {
		mLinkButton.setVisibility(View.VISIBLE);
		mTestOutput.setVisibility(View.GONE);
	}

	private void onClickLinkToDropbox() {
		mDbxAcctMgr.startLink((Activity) this, REQUEST_LINK_TO_DBX);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_LINK_TO_DBX) {
			if (resultCode == Activity.RESULT_OK) {
				doDropboxTest();
			} else {
				mTestOutput.setText("Link to Dropbox failed or was cancelled.");
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	public void doDropboxTest() {
		try {
			
			final String TEST_FILE_NAME = title + ".mp3";
			
			DbxPath testPath = new DbxPath(DbxPath.ROOT, TEST_FILE_NAME);

			// Create DbxFileSystem for synchronized file access.
			DbxFileSystem dbxFs = DbxFileSystem.forAccount(mDbxAcctMgr
					.getLinkedAccount());

			// Print the contents of the root folder. This will block until we
			// can
			// sync metadata the first time.
			List<DbxFileInfo> infos = dbxFs.listFolder(DbxPath.ROOT);
			mTestOutput.setText("\nContents of app folder:\n");
			for (DbxFileInfo info : infos) {
				mTestOutput.append("    " + info.path + ", "
						+ info.modifiedTime + '\n');
			}

			// Create a test file only if it doesn't already exist.
			if (!dbxFs.exists(testPath)) {
				DbxFile testFile = dbxFs.create(testPath);
				try {

//					File myFile = new File("/mnt/sdcard/alarms/be like that.mp3");
							
					File myFile = new File(path);
					
					// testFile.writeString(TEST_DATA);
					testFile.writeFromExistingFile(myFile, false);
				} finally {
					testFile.close();
				}
				mTestOutput.append(title + " was successfully added to your Dropbox.");
			}
			
			Bundle resultMsg = new Bundle();
			resultMsg.putString("success", title + " was successfully added to your Dropbox.");
	
			Intent playlistIntent = new Intent(getApplicationContext(), PlayListActivity.class);
			playlistIntent.putExtras(resultMsg);
			startActivity(playlistIntent);
			
		} catch (IOException e) {
			mTestOutput.setText("Dropbox upload failed: " + e);
		}
	}
}
