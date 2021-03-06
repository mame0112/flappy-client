package com.mame.flappy.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.widget.Toast;

import com.mame.flappy.constant.LcomConst;
import com.mame.flappy.data.FriendListData;
import com.mame.flappy.data.NotificationContentData;
import com.mame.flappy.db.UserLocalDataHandlerHelper.NotificationTimeComparator;
import com.mame.flappy.util.DbgUtil;

public class FriendListActivityUtil {

	private final static String TAG = LcomConst.TAG + "/FriendListActivityUtil";

	public static void startActivityConversationViewByPos(Activity activity,
			int userId, String userName, int position, int targetUserId,
			String targetUserName, String mailAddress, Bitmap thumbnail) {
		Intent intent = new Intent(activity, ConversationActivity.class);
		intent.setAction(Intent.ACTION_VIEW);
		intent.putExtra(LcomConst.EXTRA_USER_ID, userId);
		intent.putExtra(LcomConst.EXTRA_USER_NAME, userName);
		intent.putExtra(LcomConst.EXTRA_TARGET_USER_ID, targetUserId);
		intent.putExtra(LcomConst.EXTRA_TARGET_USER_NAME, targetUserName);
		intent.putExtra(LcomConst.EXTRA_TARGET_MAIL_ADDRESS, mailAddress);
		intent.putExtra(LcomConst.EXTRA_THUMBNAIL, thumbnail);
		// intent.putExtra(LcomConst.EXTRA_TARGET_NEW_MESSAGES, newMessages);
		// intent.putExtra(LcomConst.EXTRA_TARGET_NEW_MESSAGES_DATE, newDates);
		activity.startActivity(intent);
	}

	public static void startActivityForInvitation(Activity activity,
			int userId, String userName, int requestCode) {
		Intent intent = new Intent(activity, StartNewConversationActivity.class);
		intent.setAction(Intent.ACTION_VIEW);
		intent.putExtra(LcomConst.EXTRA_USER_ID, userId);
		intent.putExtra(LcomConst.EXTRA_USER_NAME, userName);
		activity.startActivityForResult(intent, requestCode);
	}

	public static void startActivityForHelp(Context context) {
		Intent intent = new Intent(context, HelpActivity.class);
		intent.setAction(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	public static void startActivityForSetting(Context context) {
		Intent intent = new Intent(context, SettingActivity.class);
		intent.setAction(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	public static void startActivityForWelcomeActivity(Activity activity) {
		Intent intent = new Intent(activity, WelcomeActivity.class);
		intent.setAction(Intent.ACTION_VIEW);
		activity.startActivity(intent);
	}

	public static ArrayList<NotificationContentData> getNotificationDate(
			ArrayList<FriendListData> friendListData, int userId) {
		DbgUtil.showDebug(TAG, "getNotificationDate: " + userId);

		if (friendListData != null && friendListData.size() != 0) {

			ArrayList<NotificationContentData> result = new ArrayList<NotificationContentData>();

			for (FriendListData data : friendListData) {
				if (data != null) {
					int senderId = data.getLastSender();
					DbgUtil.showDebug(TAG, "senderId: " + senderId);

					// If sender is myself, we should not show notification.
					// if (senderId != userId) {
					DbgUtil.showDebug(TAG, "AA");
					int fromUserId = data.getFriendId();
					int numOfMessage = data.getNumOfNewMessage();
					long expireDate = data.getMessagDate();
					NotificationContentData notificationData = new NotificationContentData(
							userId, fromUserId, numOfMessage, expireDate);
					result.add(notificationData);
					// }
				}
			}
			return result;
		}
		return null;
	}

	public static ArrayList<FriendListData> sortMessageByTime(
			ArrayList<FriendListData> data) {
		DbgUtil.showDebug(TAG, "sortMessageByTime");

		if (data != null && data.size() != 0) {
			Collections.sort(data, new TargetFriendMessageComparator());
			return data;
		}

		return null;
	}

	private static class TargetFriendMessageComparator implements
			Comparator<FriendListData> {

		@Override
		public int compare(FriendListData lhs, FriendListData rhs) {

			long time1 = lhs.getMessagDate();
			long time2 = rhs.getMessagDate();

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
