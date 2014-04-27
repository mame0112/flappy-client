package com.mame.lcom.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;

import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.data.NotificationContentData;
import com.mame.lcom.ui.FriendListActivity;
import com.mame.lcom.util.DbgUtil;

public class UserLocalDataHandlerHelper {

	private final static String TAG = LcomConst.TAG
			+ "/UserLocalDataHandlerHelper";

	public ArrayList<NotificationContentData> sortNotificationBasedOnExpireTime(
			ArrayList<NotificationContentData> input) {

		Collections.sort(input, new NotificationTimeComparator());

		return input;
	}

	public static class NotificationTimeComparator implements
			Comparator<NotificationContentData> {

		@Override
		public int compare(NotificationContentData lhs,
				NotificationContentData rhs) {

			long time1 = lhs.getExpireData();
			long time2 = rhs.getExpireData();

			if (time1 > time2) {
				return 1;

			} else if (time1 == time2) {
				return 0;

			} else {
				return -1;

			}
		}
	}

}
