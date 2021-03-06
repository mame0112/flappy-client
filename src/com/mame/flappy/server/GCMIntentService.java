package com.mame.flappy.server;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mame.flappy.constant.LcomConst;
import com.mame.flappy.exception.NewMessageNotificationManagerException;
import com.mame.flappy.notification.NewMessageNotificationManager;
import com.mame.flappy.util.DbgUtil;
import com.mame.flappy.util.PreferenceUtil;

public class GCMIntentService extends Service {

	private static final String TAG = LcomConst.TAG + "/GCMIntentService";

	@Override
	public void onCreate() {
		super.onCreate();
		DbgUtil.showDebug(TAG, "onCreate");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		DbgUtil.showDebug(TAG, "onStartCommand Received start id " + startId
				+ ": " + intent);

		PowerManager pm = (PowerManager) getApplicationContext()
				.getSystemService(Context.POWER_SERVICE);

		PowerManager.WakeLock wl = pm.newWakeLock(
				PowerManager.PARTIAL_WAKE_LOCK, "My Tag");
		try {
			wl.acquire();

			if (intent != null) {
				Bundle extras = intent.getExtras();
				GoogleCloudMessaging gcm = GoogleCloudMessaging
						.getInstance(this);
				String messageType = gcm.getMessageType(intent);

				if (!extras.isEmpty()) {
					if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
							.equals(messageType)) {
						DbgUtil.showDebug(TAG, "messageType: " + messageType
								+ ",body:" + extras.toString());
					} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
							.equals(messageType)) {
						DbgUtil.showDebug(TAG, "messageType: " + messageType
								+ ",body:" + extras.toString());
					} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
							.equals(messageType)) {
						DbgUtil.showDebug(TAG, "messageType: " + messageType
								+ ",body:" + extras.toString());

						// If the user already logged in
						// (It not logged in, nothing should happen.)
						if (PreferenceUtil.getUserId(getApplicationContext()) != LcomConst.NO_USER
								&& PreferenceUtil
										.getUserName(getApplicationContext()) != null) {
							try {
								String[] parsed = parseJSON(extras.toString());
								// Because parsed[0] is "my id" from friend
								// perspective.
								if (parsed != null) {
									String friendUserId = parsed[0];
									String userId = parsed[1];
									String userName = parsed[2];
									String targetUserName = parsed[3];
									String message = parsed[4];
									String expireDate = parsed[5];
									DbgUtil.showDebug(TAG, "userId: " + userId);
									DbgUtil.showDebug(TAG, "friendUserId: "
											+ friendUserId);
									DbgUtil.showDebug(TAG, "userName: "
											+ userName);
									DbgUtil.showDebug(TAG, "targetUserName: "
											+ targetUserName);
									DbgUtil.showDebug(TAG, "expireDate: "
											+ expireDate);
									DbgUtil.showDebug(TAG, "message: "
											+ message);

									NewMessageNotificationManager
											.handleLatestMessageAndShowNotification(
													getApplicationContext(),
													Integer.valueOf(friendUserId),
													Integer.valueOf(userId), 1,
													Long.valueOf(expireDate));

									sendBroadcast(Integer.valueOf(userId),
											Integer.valueOf(friendUserId),
											userName, targetUserName, message);
								}
							} catch (IndexOutOfBoundsException e) {
								DbgUtil.showDebug(
										TAG,
										"IndexOutOfBoundsException: "
												+ e.getMessage());
							} catch (NumberFormatException e) {
								DbgUtil.showDebug(
										TAG,
										"NumberFormatException: "
												+ e.getMessage());
							} catch (NewMessageNotificationManagerException e) {
								DbgUtil.showDebug(TAG,
										"NewMessageNotificationManagerException: "
												+ e.getMessage());
							}
						} else {
							DbgUtil.showDebug(TAG, "Not logged in");
						}
					}
				}
			}
		} finally {
			wl.release();
		}
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		DbgUtil.showDebug(TAG, "onDestroy");
	}

	// z public GCMIntentService() {
	// super("GCMIntentService");
	// }

	// @Override
	// protected void onHandleIntent(Intent intent) {
	// DbgUtil.showDebug(TAG, "onHandleIntent");
	//
	// }

	private void sendBroadcast(int userId, int friendUserId, String userName,
			String targetUserName, String message) {
		DbgUtil.showDebug(TAG, "sendBroadcast");
		Intent intent = new Intent(LcomConst.ACTION_PUSH_NOTIFICATION);
		intent.putExtra(LcomConst.EXTRA_USER_ID, friendUserId);
		intent.putExtra(LcomConst.EXTRA_USER_NAME, targetUserName);
		intent.putExtra(LcomConst.EXTRA_TARGET_USER_ID, userId);
		intent.putExtra(LcomConst.EXTRA_TARGET_USER_NAME, userName);
		sendBroadcast(intent);

	}

	private String[] parseJSON(String json) {
		DbgUtil.showDebug(TAG, "parseJSON");
		if (json != null) {
			String tmp = json.substring(7, json.length());
			DbgUtil.showDebug(TAG, "tmp: " + tmp);
			try {
				JSONObject rootObject = new JSONObject(tmp);
				if (rootObject != null) {
					String msg = rootObject.getString("msg");
					if (msg != null) {
						DbgUtil.showDebug(TAG, "msg: " + msg);
						String[] parsed = msg.split(LcomConst.SEPARATOR);
						return parsed;
					}
				}

			} catch (JSONException e) {
				DbgUtil.showDebug(TAG, "JSONException: " + e.getMessage());
			}

		}
		return null;
	}

	@Override
	public IBinder onBind(Intent intent) {
		DbgUtil.showDebug(TAG, "onBind");
		return null;
	}
}
