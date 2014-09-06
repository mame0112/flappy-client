package com.mame.flappy.notification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.mame.flappy.constant.LcomConst;
import com.mame.flappy.data.FriendListData;
import com.mame.flappy.data.MessageItemData;
import com.mame.flappy.data.NotificationContentData;
import com.mame.flappy.datamanager.FriendDataManager;
import com.mame.flappy.datamanager.FriendDataManager.FriendDataManagerListener;
import com.mame.flappy.exception.NewMessageNotificationManagerException;
import com.mame.flappy.ui.FriendListActivity;
import com.mame.flappy.util.DbgUtil;
import com.mame.flappy.util.PackageUtil;
import com.mame.flappy.util.PreferenceUtil;
import com.mame.flappy.util.TimeUtil;

public class NewMessageNotificationManager implements FriendDataManagerListener {

	private final static String TAG = LcomConst.TAG
			+ "/NewMessageNotificationManager";

	private static NewMessageNotification mNotification = new NewMessageNotification();

	private final static int NOTIFICATION_ID = 1;

	private static NewMessageNotificationManager sManager = new NewMessageNotificationManager();

	private static FriendDataManager mDataManager = null;

	private static Context mContext = null;

	private int mCurrentNotificationNum = 0;

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
			int fromUserId, int toUserId, int number, long expireDate)
			throws NewMessageNotificationManagerException {
		DbgUtil.showDebug(TAG, "handleLastetMessageAndShowNotification");

		if (context == null) {
			throw new NewMessageNotificationManagerException("Context is null");
		}

		if (expireDate <= 0L) {
			throw new NewMessageNotificationManagerException(
					"latestPostedDate is illegal (less than 0)");
		}

		// Set AlarmManager if latestPostedDate is later than current time
		long current = TimeUtil.getCurrentDate();
		if (current < expireDate) {

			mDataManager = FriendDataManager.getInstance();

			if (!mDataManager.isListenerAlreadyRegistered(sManager)) {
				DbgUtil.showDebug(
						TAG,
						"registered: "
								+ mDataManager
										.isListenerAlreadyRegistered(sManager));
				mDataManager.initializeFriendDataManager(toUserId, context);
				mDataManager.setFriendDataManagerListener(sManager);
			}

			// if (PackageUtil.isFriendListForeground(context) == false) {
			// DbgUtil.showDebug(TAG, "false");
			if (mDataManager != null) {
				mDataManager.addNewNotification(fromUserId, toUserId, number,
						expireDate);
				operateNotification(context);
			}
			// } else {
			// DbgUtil.showDebug(TAG, "true");
			// }

		}
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
	// public static void handleLastetMessagesAndShowNotification(Context
	// context,
	// ArrayList<NotificationContentData> datas)
	// throws NewMessageNotificationManagerException {
	// DbgUtil.showDebug(TAG, "handleLastetMessagesAndShowNotification");
	//
	// if (context == null) {
	// throw new NewMessageNotificationManagerException("Context is null");
	// }
	//
	// if (datas == null || datas.size() == 0) {
	// throw new NewMessageNotificationManagerException(
	// "NotificationContentData is null or size 0");
	// }
	//
	// // mDataManager = FriendDataManager.getInstance();
	//
	// if (!mDataManager.isListenerAlreadyRegistered(sManager)) {
	// DbgUtil.showDebug(
	// TAG,
	// "registered: "
	// + mDataManager
	// .isListenerAlreadyRegistered(sManager));
	// // TOOD
	// // Need to consider mDataManager.initialize
	// mDataManager.setFriendDataManagerListener(sManager);
	// }
	//
	// if (mDataManager != null) {
	// long current = TimeUtil.getCurrentDate();
	// boolean isMoreThanOneNew = false;
	// for (NotificationContentData data : datas) {
	// long expireDate = data.getExpireData();
	// // Set AlarmManager if expireDate is later than current
	// // time
	// if (current < expireDate) {
	// int number = data.getNumberOfMesage();
	// if (number != 0) {
	// isMoreThanOneNew = true;
	// int fromUserId = data.getFromUserId();
	// int toUserId = data.getToUserId();
	// mDataManager.addNewNotification(fromUserId, toUserId,
	// number, expireDate);
	// }
	// }
	// }
	//
	// if (isMoreThanOneNew == true) {
	// isMoreThanOneNew = false;
	// operateNotification(context);
	// }
	// }
	//
	// }

	private static void operateNotification(Context context) {
		DbgUtil.showDebug(TAG, "operateNotification");

		mContext = context;

		// Get nearlest expire notification data
		if (mDataManager != null) {
			mDataManager.requestNotificationNearestExpireData();

		}
	}

	/**
	 * API to be called when current AlarmManager expires and need to set next
	 * AlarmManager
	 * 
	 * @param context
	 */
	public static void setNextNotification(Context context) {
		DbgUtil.showDebug(TAG, "setNextNotification");
		operateNotification(context);
	}

	private static void removeCurrentAlarmManager() {
		DbgUtil.showDebug(TAG, "removeCurrentAlarmManager");

	}

	private static void setAlarmManagerForRemoveNotification(Context context,
			int fromUserId, int toUserId, long triggerTime) {
		DbgUtil.showDebug(TAG, "setAlarmManagerForRemoveNotification: "
				+ triggerTime);
		Intent intent = new Intent(context,
				NewMessageNotificationReceiver.class);
		intent.putExtra(LcomConst.EXTRA_USER_ID, fromUserId);
		intent.putExtra(LcomConst.EXTRA_TARGET_USER_ID, toUserId);

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
	}

	@Override
	public void notifyPresentDataset(ArrayList<FriendListData> userData) {

	}

	@Override
	public void notifyNewDataset(ArrayList<FriendListData> newUserData) {

	}

	@Override
	public void notifyAddPresentDataFinished(boolean result,
			MessageItemData messageData) {

	}

	@Override
	public void notifyPresentMessageDataLoaded(
			ArrayList<MessageItemData> messageData) {

	}

	@Override
	public void notifyNewConversationDataLoaded(
			ArrayList<MessageItemData> messageData) {

	}

	@Override
	public void notifyFriendThubmailsLoaded(
			List<HashMap<Integer, Bitmap>> thumbnailsthumbnails) {

	}

	@Override
	public void notifyLatestStoredMessage(FriendListData result) {
		DbgUtil.showDebug(TAG, "notifyLatestStoredMessage");
	}

	/**
	 * This method's argument shall be null all notification has expires.
	 */
	@Override
	public void notifyValidNotificationList(
			ArrayList<NotificationContentData> notifications) {
		DbgUtil.showDebug(TAG, "notifyValidNotificationList");
		DbgUtil.showDebug(TAG, "mCurrentNotificationNum: "
				+ mCurrentNotificationNum);

		if (notifications != null && notifications.size() != 0) {
			// Minus is for just expiring message
			int size = notifications.size() - 1;
			DbgUtil.showDebug(TAG, "size: " + size);

			// If size decreased (meaning some notification expired)
			if (mCurrentNotificationNum > size) {
				if (size <= 0) {
					// If size comes to 0, need to remove notification
					removeNotification();
				} else {
					// Nothing to do
					DbgUtil.showDebug(TAG, "Nothing to do");
				}
			} else {
				// If size increased (meaning some notification added)
				// Get most earlist expire message
				NotificationContentData data = notifications.get(0);
				int fromUserId = data.getFromUserId();
				int toUserId = data.getToUserId();
				long expireDate = data.getExpireData();
				setAlarmManagerForRemoveNotification(mContext, fromUserId,
						toUserId, expireDate);

				showNotification(mContext, fromUserId);
			}
			mCurrentNotificationNum = size;

		} else {
			mCurrentNotificationNum = 0;
			removeNotification();
		}
	}
}
