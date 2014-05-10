package com.mame.flappy.util;

import android.app.Activity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.mame.flappy.constant.LcomConst;

public class LcomGPServiceUtil {

	private final String TAG = LcomConst.TAG + "/LcomGPServiceUtil";

	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	public boolean checkPlayServices(Activity activity) {
		DbgUtil.showDebug(TAG, "checkPlayServices");
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(activity);
		DbgUtil.showDebug(TAG, "resultCode: " + resultCode);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				DbgUtil.showDebug(TAG, "isUserRecoverableError true"
						+ resultCode);
				GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				DbgUtil.showDebug(TAG, "Google play service is not supported");
				// TODO show notification dialog
			}
			return false;
		}
		return true;
	}
}
