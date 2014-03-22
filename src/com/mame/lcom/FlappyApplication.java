package com.mame.lcom;

import java.io.IOException;

import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.util.DbgUtil;
import com.mame.lcom.util.TrackingUtil;

import android.app.Application;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.text.TextUtils;

public class FlappyApplication extends Application {

	private final String TAG = LcomConst.TAG + "/FlappyApplication";

	private GoogleCloudMessaging mGcm = null;

	@Override
	public void onCreate() {
		DbgUtil.showDebug(TAG, "onCreate");

		// Track device information
		TrackingUtil.trackModel(getApplicationContext());

		// Check for device manifest
		mGcm = GoogleCloudMessaging.getInstance(this);
		registerInBackground();
	}

	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (mGcm == null) {
						mGcm = GoogleCloudMessaging
								.getInstance(getApplicationContext());
					}
					String regid = mGcm.register(LcomConst.PROJECT_NUMBER);
					msg = "Device registered, registration ID=" + regid;
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
				}
				DbgUtil.showDebug(TAG, "msg: " + msg);
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
			}
		}.execute(null, null, null);
	}

	@Override
	public void onTerminate() {
		DbgUtil.showDebug(TAG, "onTerminate");
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		DbgUtil.showDebug(TAG, "onConfigurationChanged");
	}
}
