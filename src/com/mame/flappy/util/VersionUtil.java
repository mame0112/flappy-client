package com.mame.flappy.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

import com.mame.flappy.constant.LcomConst;

public class VersionUtil {

	private final static String TAG = LcomConst.TAG + "/VersionUtil";

	public static int getCurrentAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			DbgUtil.showDebug(TAG, "NameNotFoundException: " + e.getMessage());
		}
		return -1;
	}

	public static int getStoredAppVersion(Context context) {
		int storedVersion = PreferenceUtil.getCurrentAppVersionForGCM(context);
		return storedVersion;
	}

	public static void storeCurrentAppVersion(Context context, int version) {
		PreferenceUtil.setCurrentAppVersionForGCM(context, version);
	}
}
