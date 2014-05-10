package com.mame.flappy.ui;

import android.content.Context;
import android.content.Intent;

import com.mame.flappy.constant.LcomConst;
import com.mame.flappy.util.PreferenceUtil;

public class LoginActivityUtil {

	public static void startActivityForFriendList(Context context, int userId,
			String userName) {
		Intent intent = new Intent(context, FriendListActivity.class);
		intent.putExtra(LcomConst.EXTRA_USER_ID, userId);
		intent.putExtra(LcomConst.EXTRA_USER_NAME, userName);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	public static void startActivityForWelcome(Context context) {
		Intent intent = new Intent(context, WelcomeActivity.class);
		intent.setAction(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	public static boolean isValidInputString(int minLength, int maxLength,
			String inputString) {
		if (inputString.length() >= minLength
				&& inputString.length() <= maxLength) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isHalfSizeString(String input) {
		if (input != null && input.length() != 0) {
			int length = input.length();
			byte[] bytes = input.getBytes();
			if (length != bytes.length) {
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	public static void storeUserDataToPref(Context context, int userId,
			String userName) {
		PreferenceUtil.setUserId(context, userId);
		PreferenceUtil.setUserName(context, userName);
	}

}
