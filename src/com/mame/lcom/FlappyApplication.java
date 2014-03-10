package com.mame.lcom;

import com.google.android.gcm.GCMRegistrar;
import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.util.DbgUtil;

import android.app.Application;
import android.content.res.Configuration;
import android.text.TextUtils;

public class FlappyApplication extends Application {

	private final String TAG = LcomConst.TAG + "/FlappyApplication";

	@Override
	public void onCreate() {
		DbgUtil.showDebug(TAG, "onCreate");

		// Check for device manifest
		GCMRegistrar.checkDevice(getApplicationContext());
		GCMRegistrar.checkManifest(getApplicationContext());

		String regId = GCMRegistrar.getRegistrationId(getApplicationContext());
		if (TextUtils.isEmpty(regId)) {
			DbgUtil.showDebug(TAG, "Not registered");
			// GCMRegistrar.register(getApplicationContext(), "SENDER_ID");
			GCMRegistrar
					.register(getApplicationContext(), LcomConst.PROJECT_ID);
		} else {
			DbgUtil.showDebug(TAG, "Already registered: " + regId);
		}
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
