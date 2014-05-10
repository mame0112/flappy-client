package com.mame.flappy.util;

import android.content.Context;
import android.provider.Settings;

public class SecurityUtil {

	public static String getUniqueId(Context context) {
		String UUID = Settings.Secure.getString(context.getContentResolver(),
				Settings.System.ANDROID_ID);
		return UUID;
	}

}
