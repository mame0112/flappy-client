package com.mame.flappy.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.mame.flappy.LcomBaseActivity;
import com.mame.flappy.R;
import com.mame.flappy.constant.LcomConst;
import com.mame.flappy.util.DbgUtil;
import com.mame.flappy.util.TrackingUtil;

public class FlappyAboutActivity extends LcomBaseActivity {

	private final String TAG = LcomConst.TAG + "/FlappyAboutActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flappy_about);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		Button startButton = (Button) findViewById(R.id.aboutGetStartedButton);
		startButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DbgUtil.showDebug(TAG, "get started button");
				Intent intent = new Intent(getApplicationContext(),
						StartNewConversationActivity.class);
				startActivity(intent);
				finish();
			}

		});

	}

	@Override
	public void onStart() {
		super.onStart();
		TrackingUtil.trackActivityStart(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		TrackingUtil.trackActivityStop(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.about_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return false;
	}
}
