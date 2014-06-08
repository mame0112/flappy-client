package com.mame.flappy;

import android.app.Application;
import android.content.res.Configuration;

import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mame.flappy.constant.LcomConst;
import com.mame.flappy.util.DbgUtil;
import com.mame.flappy.util.TrackingUtil;

public class FlappyApplication extends Application {

	private final String TAG = LcomConst.TAG + "/FlappyApplication";

	private GoogleCloudMessaging mGcm = null;

	@Override
	public void onCreate() {
		DbgUtil.showDebug(TAG, "onCreate");

		// Track device information
		TrackingUtil.trackModel(getApplicationContext());
	}

	@Override
	public void onTerminate() {
		DbgUtil.showDebug(TAG, "onTerminate");
//		GCMRegistrar.onDestroy(this);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		DbgUtil.showDebug(TAG, "onConfigurationChanged");
	}
}
