package com.mame.lcom.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.exception.NewMessageNotificationManagerException;
import com.mame.lcom.util.DbgUtil;
import com.mame.lcom.util.PreferenceUtil;
import com.mame.lcom.util.TimeUtil;

public class NewMessageNotificationManager {

	private final static String TAG = LcomConst.TAG
			+ "/NewMessageNotificationManager";

	private static NewMessageNotification mNotification = new NewMessageNotification();

	private final static int NOTIFICATION_ID = 1;

	private static NewMessageNotificationManager sManager = new NewMessageNotificationManager();

	// private static ArrayList<Long> mAlarmCandidates = new ArrayList<Long>();

	/**
	 * Singletone
	 * 
	 * @param context
	 */
	private NewMessageNotificationManager() {

	}

	public static NewMessageNotificationManager getInstance() {
		return sManager;
	}

	/**
	 * Show notification depends on arguments. If the client calls this method,
	 * this NewMessageNotificationManager will handle not only showing
	 * notification, but alos remove it if the message is expires. And
	 * targetUserId and targetUserName is not LcomConst.NoUser and null, this
	 * manager will launch ConversationActivity. Otherwise, it shall launch
	 * FriendListActivity
	 * 
	 * @param context
	 * @param userId
	 * @param targetUserId
	 * @param latestPostedDate
	 */
	public static void handleLastetMessageAndShowNotification(Context context,
			int userId, long latestPostedDate)
			throws NewMessageNotificationManagerException {
		DbgUtil.showDebug(TAG, "handleLastetMessageAndShowNotification");

		if (context == null) {
			throw new NewMessageNotificationManagerException("Context is null");
		}

		if (latestPostedDate <= 0L) {
			throw new NewMessageNotificationManagerException(
					"latestPostedDate is illegal (less than 0)");
		}

		// Set AlarmManager if latestPostedDate is later than current time
		long current = TimeUtil.getCurrentDate();
		if (current < latestPostedDate) {
			operateNotification(context, latestPostedDate, userId);
		}

	}

	private static void operateNotification(Context context,
			long latestPostedDate, int userId) {

		DbgUtil.showDebug(TAG, "operateNotification");

		// If inputted date is much more later than current latest message, we
		// update Shared preference so that we can update expire timing for
		// Notification
		long currentLatestDate = PreferenceUtil
				.getLatestMessagePostedTime(context);

		DbgUtil.showDebug(TAG, "currentLatestDate: " + currentLatestDate);

		if (currentLatestDate < latestPostedDate) {
			DbgUtil.showDebug(TAG, "latestPostedDate: " + latestPostedDate);
			// Update Share Preference
			PreferenceUtil.updateLatestMessagePostedTime(context,
					latestPostedDate);

			// Cancel current AlarmManager
			// removeCurrentAlarmManager();

			// Set new AlarmManager.
			setAlarmManagerForRemoveNotification(context, userId,
					latestPostedDate);

			// Finally show notification
			// TODO we need to show message in case of ConversationActivity
			showNotification(context, userId);
		}
	}

	private static void removeCurrentAlarmManager() {
		DbgUtil.showDebug(TAG, "removeCurrentAlarmManager");

	}

	private static void setAlarmManagerForRemoveNotification(Context context,
			int userId, long triggerTime) {
		DbgUtil.showDebug(TAG, "setAlarmManagerForRemoveNotification");
		Intent intent = new Intent(context,
				NewMessageNotificationReceiver.class);

		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
				intent, 0);
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
	}

	private static void showNotification(Context context, int userId) {
		DbgUtil.showDebug(TAG, "showNotification");
		if (mNotification != null) {
			mNotification.showNotiofication(context, userId, NOTIFICATION_ID);
		}
	}

	/**
	 * If the client calls this API, all notification shall be cancelled and all
	 * alarm managers shall be removed
	 */
	public static void removeNotification() {
		DbgUtil.showDebug(TAG, "removeNotification");

		// Remove all notification
		if (mNotification != null) {
			mNotification.removeNotification();
		}

		// Remove currentAlamrManager
		removeCurrentAlarmManager();
	}

	// public static class NotificationTimeComparator implements
	// Comparator<Long> {
	//
	// @Override
	// public int compare(Long lhs, Long rhs) {
	// if (lhs > rhs) {
	// return 1;
	//
	// } else if (lhs == rhs) {
	// return 0;
	//
	// } else {
	// return -1;
	//
	// }
	// }
	//
	// }
}
