package com.mame.flappy.util;

import java.util.List;

import android.app.ActivityManager;
import android.content.Context;

import com.mame.flappy.ui.FriendListActivity;

public class PackageUtil {
	public static boolean isFriendListForeground(Context context) {

		if (context == null) {
			return false;
		}

		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);

		List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);

		// DbgUtil.showDebug(TAG, "CURRENT Activity ::"
		// + taskInfo.get(0).topActivity.getClassName());
		String targetName = taskInfo.get(0).topActivity.getClassName();

		String friendActivityName = FriendListActivity.class.getName();

		if (friendActivityName != null && targetName != null) {
			if (friendActivityName.equals(targetName)) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}
}
