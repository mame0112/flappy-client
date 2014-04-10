package com.mame.lcom.notification;

import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.exception.NewMessageNotificationManagerException;
import com.mame.lcom.util.DbgUtil;
import com.mame.lcom.util.PreferenceUtil;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class NewMessageNotificationManager {

	private final static String TAG = LcomConst.TAG
			+ "/NewMessageNotificationManager";

	private static NewMessageNotification mNotification = new NewMessageNotification();

	private final static int NOTIFICATION_ID = 1;

	private static NewMessageNotificationManager sManager = new NewMessageNotificationManager();

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
			int userId, int friendUserId, long latestPostedDate)
			throws NewMessageNotificationManagerException {
		DbgUtil.showDebug(TAG, "handleLastetMessageAndShowNotification");

		if (context == null) {
			throw new NewMessageNotificationManagerException("Context is null");
		}

		if (latestPostedDate <= 0L) {
			throw new NewMessageNotificationManagerException(
					"latestPostedDate is illegal (less than 0)");
		}

		// First, we update latest message expire date.
		long currentLatestDate = PreferenceUtil
				.getLatestMessagePostedTime(context);
		DbgUtil.showDebug(TAG, "currentLatestDate: " + currentLatestDate);
		DbgUtil.showDebug(TAG, "latestPostedDate: " + latestPostedDate);

		// If inputted date is much more later than current latest message, we
		// update Shared preference so that we can update expire timing for
		// Notification
		if (currentLatestDate < latestPostedDate) {
			DbgUtil.showDebug(TAG, "latestPostedDate: " + latestPostedDate);
			// Update Share Preference
			PreferenceUtil.updateLatestMessagePostedTime(context,
					latestPostedDate);

			// TODO Update Notification expire date by using AlarmManager.

			// Cancel current AlarmManager
			removeCurrentAlarmManager();

			// Set new AlarmManager.
			setAlarmManagerForRemoveNotification(context, latestPostedDate);

			// Finally show notification
			// TODO we need to show message in case of ConversationActivity
			showNotification(context, userId, friendUserId);
		}
	}

	private static void removeCurrentAlarmManager() {
		DbgUtil.showDebug(TAG, "removeCurrentAlarmManager");
	}

	private static void setAlarmManagerForRemoveNotification(Context context,
			long triggerTime) {
		DbgUtil.showDebug(TAG, "setAlarmManagerForRemoveNotification");
		Intent intent = new Intent(context, NewMessageNotificationService.class);
		// TODO Need to add service int AndroidManifest

		PendingIntent pendingIntent = PendingIntent.getService(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);

	}

	private static void showNotification(Context context, int userId,
			int targetUserId) {
		DbgUtil.showDebug(TAG, "showNotification");
		if (mNotification != null) {
			mNotification.showNotiofication(context, userId, targetUserId,
					NOTIFICATION_ID);
		}
	}

	/**
	 * If the client calls this API, all notification shall be cancelled and all
	 * alarm managers shall be removed
	 */
	public static void removeNotification() {

		// Remove all notification
		if (mNotification != null) {
			mNotification.removeNotification();
		}

		// Remove currentAlamrManager
		removeCurrentAlarmManager();
	}
}
