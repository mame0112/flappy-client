package com.mame.flappy.util;

import com.mame.flappy.constant.LcomConst;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtil {

	private static final String PREF_KEY = "loosecom_preference";

	private static final String KEY_FIRST_TIME = "key_first_time";
	private static final String KEY_TEXT_USER_ID = "key_user_id";
	private static final String KEY_TEXT_USER_NAME = "key_user_name";
	private static final String KEY_PUSH_DEVICE_ID = "key_push_device_id";
	private static final String KEY_PUSH_APP_VERSION = "key_app_version";
	private static final String KEY_LATEST_MESSAGE_POSTED_TIME = "key_latest_message_post_time";
	private static final String KEY_THUMBNAIL_CHECK = "key_thumbnail_check";
	private static final String KEY_PLAY_NOTIFICATION_SOUND = "key_play_notification_sound";
	private static final String KEY_PLAY_NOTIFICATION_VIBRATION = "key_play_notification_vibration";

	final static boolean DEFAULT_FIRST_LAUNCH = true;
	final static int DEFAULT_TEXT_USER_ID = LcomConst.NO_USER;
	final static String DEFAULT_TEXT_USER_NAME = "Someone";
	final static String DEFAULT_PUSH_DEVICE_ID = null;
	final static long DEFAULT_LATEST_MESSAGE_POSTED_TIME = 0L;
	final static long DEFAULT_THUMBNAIL_CHECK_TIME = 0L;

	final static int DEFAULT_APP_VERSION = 1;
	final static boolean DEFAULT_PLAY_NOTIFICATION_SOUND = true;
	final static boolean DEFAULT_PLAY_NOTIFICATION_VIBRATION = true;

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

	public static void setPushDeviceId(Context c, String deviceId) {
		SharedPreferences pref = c.getSharedPreferences(PREF_KEY,
				Context.MODE_PRIVATE);
		pref.edit().putString(KEY_PUSH_DEVICE_ID, deviceId).commit();
	}

	public static String getPushDeviceId(Context c) {
		SharedPreferences pref = c.getSharedPreferences(PREF_KEY,
				Context.MODE_PRIVATE);
		return pref.getString(KEY_PUSH_DEVICE_ID, DEFAULT_PUSH_DEVICE_ID);
	}

	public static void removePushDeviceId(Context c) {
		SharedPreferences pref = c.getSharedPreferences(PREF_KEY,
				Context.MODE_PRIVATE);
		pref.edit().remove(KEY_PUSH_DEVICE_ID).commit();
	}

	/**
	 * This preference shall be used for checking thumbnail in certain interval
	 * (Meaning to avoid check thumbnail every time FriendListActivity shown)
	 * 
	 * @param c
	 * @param time
	 */
	public static void setLastThumbnailCheckTime(Context c, long time) {
		SharedPreferences pref = c.getSharedPreferences(PREF_KEY,
				Context.MODE_PRIVATE);
		pref.edit().putLong(KEY_THUMBNAIL_CHECK, time).commit();
	}

	public static long getLastThumbnailCheckTime(Context c) {
		SharedPreferences pref = c.getSharedPreferences(PREF_KEY,
				Context.MODE_PRIVATE);
		return pref.getLong(KEY_THUMBNAIL_CHECK, DEFAULT_THUMBNAIL_CHECK_TIME);
	}

	public static void removeLastThumbnailCheckTime(Context c) {
		SharedPreferences pref = c.getSharedPreferences(PREF_KEY,
				Context.MODE_PRIVATE);
		pref.edit().remove(KEY_THUMBNAIL_CHECK).commit();
	}

	/**
	 * This is for GCM Device ID. This should be called if app version is
	 * updated and after we refresh device is .
	 * 
	 * @param c
	 * @param version
	 */
	public static void setCurrentAppVersionForGCM(Context c, int version) {
		SharedPreferences pref = c.getSharedPreferences(PREF_KEY,
				Context.MODE_PRIVATE);
		pref.edit().putInt(KEY_PUSH_APP_VERSION, version).commit();
	}

	public static int getCurrentAppVersionForGCM(Context c) {
		SharedPreferences pref = c.getSharedPreferences(PREF_KEY,
				Context.MODE_PRIVATE);
		return pref.getInt(KEY_PUSH_APP_VERSION, DEFAULT_APP_VERSION);
	}

	public static void removeCurrentAppVersionForGCM(Context c) {
		SharedPreferences pref = c.getSharedPreferences(PREF_KEY,
				Context.MODE_PRIVATE);
		pref.edit().remove(KEY_PUSH_APP_VERSION).commit();
	}

	/**
	 * This is for Notification. Currently, we just need to store data for
	 * latest message. But once we have to have more than 2 message, we should
	 * use SQLiteDatabase
	 * 
	 * @param c
	 * @param time
	 */
	public static void updateLatestMessagePostedTime(Context c, long time) {
		SharedPreferences pref = c.getSharedPreferences(PREF_KEY,
				Context.MODE_PRIVATE);
		pref.edit().putLong(KEY_LATEST_MESSAGE_POSTED_TIME, time).commit();
	}

	public static long getLatestMessagePostedTime(Context c) {
		SharedPreferences pref = c.getSharedPreferences(PREF_KEY,
				Context.MODE_PRIVATE);
		return pref.getLong(KEY_LATEST_MESSAGE_POSTED_TIME,
				DEFAULT_LATEST_MESSAGE_POSTED_TIME);
	}

	public static void removeLatestMessagePostedTime(Context c) {
		SharedPreferences pref = c.getSharedPreferences(PREF_KEY,
				Context.MODE_PRIVATE);
		pref.edit().remove(KEY_LATEST_MESSAGE_POSTED_TIME).commit();
	}

	public static void setCurrentSoundSetting(Context c, boolean isPlay) {
		SharedPreferences pref = c.getSharedPreferences(PREF_KEY,
				Context.MODE_PRIVATE);
		pref.edit().putBoolean(KEY_PLAY_NOTIFICATION_SOUND, isPlay).commit();
	}

	public static boolean getCurrentSoundSetting(Context c) {
		SharedPreferences pref = c.getSharedPreferences(PREF_KEY,
				Context.MODE_PRIVATE);
		return pref.getBoolean(KEY_PLAY_NOTIFICATION_SOUND,
				DEFAULT_PLAY_NOTIFICATION_SOUND);
	}

	public static void removeCurrentSoundSetting(Context c) {
		SharedPreferences pref = c.getSharedPreferences(PREF_KEY,
				Context.MODE_PRIVATE);
		pref.edit().remove(KEY_PLAY_NOTIFICATION_SOUND).commit();
	}

	public static void setCurrentVibrationSetting(Context c, boolean isPlay) {
		SharedPreferences pref = c.getSharedPreferences(PREF_KEY,
				Context.MODE_PRIVATE);
		pref.edit().putBoolean(KEY_PLAY_NOTIFICATION_VIBRATION, isPlay)
				.commit();
	}

	public static boolean getCurrentVibrationSetting(Context c) {
		SharedPreferences pref = c.getSharedPreferences(PREF_KEY,
				Context.MODE_PRIVATE);
		return pref.getBoolean(KEY_PLAY_NOTIFICATION_VIBRATION,
				DEFAULT_PLAY_NOTIFICATION_VIBRATION);
	}

	public static void removeCurrentVibrationSetting(Context c) {
		SharedPreferences pref = c.getSharedPreferences(PREF_KEY,
				Context.MODE_PRIVATE);
		pref.edit().remove(KEY_PLAY_NOTIFICATION_VIBRATION).commit();
	}

	/**
	 * Remove all preference data. This shall be used for sigining out from this
	 * application
	 * 
	 * @param c
	 */
	public static void removeAllPreferenceData(Context c) {
		setFirstTime(c, true);
		removeUserId(c);
		removeUserName(c);
		removePushDeviceId(c);
		removeLastThumbnailCheckTime(c);
		removeCurrentAppVersionForGCM(c);
		removeLatestMessagePostedTime(c);
		// removeCurrentSoundSetting(c);
		// removeCurrentVibrationSetting(c);
	}

}
