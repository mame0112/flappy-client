package com.mame.lcom.util;

import com.mame.lcom.constant.LcomConst;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtil {

	private static final String PREF_KEY = "loosecom_preference";

	private static final String KEY_FIRST_TIME = "key_first_time";
	private static final String KEY_TEXT_USER_ID = "key_user_id";
	private static final String KEY_TEXT_USER_NAME = "key_user_name";

	final static boolean DEFAULT_FIRST_LAUNCH = true;
	final static int DEFAULT_TEXT_USER_ID = LcomConst.NO_USER;
	final static String DEFAULT_TEXT_USER_NAME = "Someone";

	public static void setFirstTime(Context c, boolean isFirstTime) {
		SharedPreferences pref = c.getSharedPreferences(PREF_KEY,
				Context.MODE_PRIVATE);
		pref.edit().putBoolean(KEY_FIRST_TIME, isFirstTime).commit();
	}

	public static boolean isFirstTimeLaunch(Context c) {
		SharedPreferences pref = c.getSharedPreferences(PREF_KEY,
				Context.MODE_PRIVATE);
		return pref.getBoolean(KEY_FIRST_TIME, DEFAULT_FIRST_LAUNCH);
	}

	public static void setUserId(Context c, int userId) {
		SharedPreferences pref = c.getSharedPreferences(PREF_KEY,
				Context.MODE_PRIVATE);
		pref.edit().putInt(KEY_TEXT_USER_ID, userId).commit();
	}

	public static int getUserId(Context c) {
		SharedPreferences pref = c.getSharedPreferences(PREF_KEY,
				Context.MODE_PRIVATE);
		return pref.getInt(KEY_TEXT_USER_ID, DEFAULT_TEXT_USER_ID);
	}

	public static void removeUserId(Context c) {
		SharedPreferences pref = c.getSharedPreferences(PREF_KEY,
				Context.MODE_PRIVATE);
		pref.edit().remove(KEY_TEXT_USER_ID).commit();
	}

	public static void setUserName(Context c, String userName) {
		SharedPreferences pref = c.getSharedPreferences(PREF_KEY,
				Context.MODE_PRIVATE);
		pref.edit().putString(KEY_TEXT_USER_NAME, userName).commit();
	}

	public static String getUserName(Context c) {
		SharedPreferences pref = c.getSharedPreferences(PREF_KEY,
				Context.MODE_PRIVATE);
		return pref.getString(KEY_TEXT_USER_NAME, DEFAULT_TEXT_USER_NAME);
	}

	public static void removeUserName(Context c) {
		SharedPreferences pref = c.getSharedPreferences(PREF_KEY,
				Context.MODE_PRIVATE);
		pref.edit().remove(KEY_TEXT_USER_NAME).commit();
	}

}
