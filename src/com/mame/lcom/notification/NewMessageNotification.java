package com.mame.lcom.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.mame.lcom.R;
import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.ui.FriendListActivity;

public class NewMessageNotification {

	private final static int REQUEST_CODE = 1;

	private final static int LED_INTERVAL = 1000; // 1 sec

	private static NotificationManager mNotificationManager = null;

	public void showNotiofication(Context context, int userId,
			int targetUserId, int notificationId) {

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				context)
				.setSmallIcon(R.drawable.flippy_statusbar_icon)
				.setContentTitle(
						context.getString(R.string.str_notification_app_name))
				.setContentText(
						context.getString(R.string.str_notification_content_text))
				.setTicker(
						context.getString(R.string.str_notification_ticker_text))
				.setLights(Color.MAGENTA, LED_INTERVAL, LED_INTERVAL)
				.setVibrate(new long[] { 1000, 700, 250, 700, 250 })
				.setAutoCancel(true);

		Intent intent = new Intent(context, FriendListActivity.class);
		intent.putExtra(LcomConst.EXTRA_USER_ID, userId);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		stackBuilder.addParentStack(FriendListActivity.class);
		stackBuilder.addNextIntent(intent);

		PendingIntent pIntent = stackBuilder.getPendingIntent(REQUEST_CODE,
				PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(pIntent);
		mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(notificationId, mBuilder.build());
	}

	public void removeNotification() {
		if (mNotificationManager != null) {
			mNotificationManager.cancelAll();
		}
	}
}
