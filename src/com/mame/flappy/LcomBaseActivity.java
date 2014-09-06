package com.mame.flappy;

import com.mame.flappy.constant.LcomConst;
import com.mame.flappy.datamanager.FriendDataManager;
import com.mame.flappy.util.DbgUtil;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;

public class LcomBaseActivity extends Activity {

	private final String TAG = LcomConst.TAG + "/LcomDefaultActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onStop();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		DbgUtil.showDebug(TAG, "onConfigurationChanged");
	}

}
