package com.mame.lcom.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.exception.NewMessageNotificationManagerException;
import com.mame.lcom.notification.NewMessageNotificationManager;
import com.mame.lcom.util.DbgUtil;
import com.mame.lcom.util.HttpClientUtil;
import com.mame.lcom.util.TimeUtil;

public class GCMIntentService extends IntentService {

	private static final String TAG = LcomConst.TAG + "/GCMIntentService";

	public GCMIntentService() {
		super("GCMIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		DbgUtil.showDebug(TAG, "onHandleIntent");
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
					.equals(messageType)) {
				DbgUtil.showDebug(TAG, "messageType: " + messageType + ",body:"
						+ extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {
				DbgUtil.showDebug(TAG, "messageType: " + messageType + ",body:"
						+ extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) {
				DbgUtil.showDebug(TAG, "messageType: " + messageType + ",body:"
						+ extras.toString());

				try {
					String[] parsed = parseJSON(extras.toString());
					// Because parsed[0] is "my id" from friend perspective.
					if (parsed != null) {
						String friendUserId = parsed[0];
						String userId = parsed[1];
						String userName = parsed[2];
						String targetUserName = parsed[3];
						String message = parsed[4];
						String expireDate = parsed[5];
						DbgUtil.showDebug(TAG, "userId: " + userId);
						DbgUtil.showDebug(TAG, "friendUserId: " + friendUserId);
						DbgUtil.showDebug(TAG, "userName: " + userName);
						DbgUtil.showDebug(TAG, "targetUserName: "
								+ targetUserName);
						DbgUtil.showDebug(TAG, "expireDate: " + expireDate);
						DbgUtil.showDebug(TAG, "message: " + message);

						NewMessageNotificationManager
								.handleLastetMessageAndShowNotification(
										getApplicationContext(),
										Integer.valueOf(userId),
										Integer.valueOf(friendUserId), 1,
										Long.valueOf(expireDate));

						sendBroadcast(Integer.valueOf(userId),
								Integer.valueOf(friendUserId), userName,
								targetUserName, message);
					}
				} catch (IndexOutOfBoundsException e) {
					DbgUtil.showDebug(TAG,
							"IndexOutOfBoundsException: " + e.getMessage());
				} catch (NumberFormatException e) {
					DbgUtil.showDebug(TAG,
							"NumberFormatException: " + e.getMessage());
				} catch (NewMessageNotificationManagerException e) {
					DbgUtil.showDebug(
							TAG,
							"NewMessageNotificationManagerException: "
									+ e.getMessage());
				}
			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	private void sendBroadcast(int userId, int targetUserId, String userName,
			String targetUserName, String message) {
		DbgUtil.showDebug(TAG, "sendBroadcast");
		Intent intent = new Intent(LcomConst.ACTION_PUSH_NOTIFICATION);
		intent.putExtra(LcomConst.EXTRA_USER_ID, userId);
		intent.putExtra(LcomConst.EXTRA_USER_NAME, userName);
		intent.putExtra(LcomConst.EXTRA_TARGET_USER_ID, targetUserId);
		intent.putExtra(LcomConst.EXTRA_TARGET_USER_NAME, targetUserName);
		sendBroadcast(intent);

	}

	private String[] parseJSON(String json) {
		String tmp = json.substring(7, json.length());
		try {
			JSONObject rootObject = new JSONObject(tmp);
			if (rootObject != null) {
				String msg = rootObject.getString("msg");
				if (msg != null) {
					String[] parsed = msg.split(LcomConst.SEPARATOR);
					return parsed;
				}
			}

		} catch (JSONException e) {
			Log.e(TAG, "JSONException: " + e.getMessage());
		}
		return null;
	}
}

// public class GCMIntentService extends GCMBaseIntentService {
//
// private static final String TAG = LcomConst.TAG + "/GCMIntentService";
//
// private Handler toaster;
//
// private static final String USER_ID = "TEST_USER";
//
// public GCMIntentService() {
// super(LcomConst.PROJECT_NUMBER);
// }
//
// @Override
// public void onCreate() {
// DbgUtil.showDebug(TAG, "onCreate");
// super.onCreate();
// toaster = new Handler();
// }
//
// @Override
// protected void onRegistered(Context context, String registrationId) {
// DbgUtil.showDebug(TAG, "onRegistered: regId = " + registrationId);
// // GCMから発行された端末IDをアプリサーバに登録する。
// String uri = LcomConst.BASE_URL + "?action=register" + "&userId="
// + USER_ID + "&regId=" + registrationId;
// // Util.doGet(uri);
// HttpClientUtil easyHttpClient = new HttpClientUtil(uri);
// String responseString;
// try {
// // // post の場合
// // responseString = easyHttpClient.doPost();
// // get の場合
// responseString = easyHttpClient.doGet();
// DbgUtil.showDebug(TAG, "responseString: " + responseString);
// } catch (UnsupportedEncodingException e) {
// DbgUtil.showDebug(TAG,
// "UnsupportedEncodingException: " + e.getMessage());
// } catch (ClientProtocolException e) {
// DbgUtil.showDebug(TAG, "ClientProtocolException: " + e.getMessage());
// } catch (IOException e) {
// DbgUtil.showDebug(TAG, "IOException: " + e.getMessage());
// }
// }
//
// @Override
// protected void onMessage(Context context, Intent intent) {
// // アプリサーバから送信されたPushメッセージの受信。
// // Message.data が Intent.extra になるらしい。
// CharSequence msg = intent.getCharSequenceExtra("msg");
// DbgUtil.showDebug(TAG, "onMessage: msg = " + msg);
// toast("Push message: " + msg);
// }
//
// @Override
// protected void onUnregistered(Context context, String registrationId) {
// DbgUtil.showDebug(TAG, "onUnregistered: regId = " + registrationId);
// if (GCMRegistrar.isRegisteredOnServer(context)) {
// String uri = LcomConst.BASE_URL + "?action=unregister" + "&userId="
// + USER_ID;
//
// HttpClientUtil easyHttpClient = new HttpClientUtil(uri);
// String responseString;
// try {
// // post の場合
// responseString = easyHttpClient.doPost();
// // get の場合
// responseString = easyHttpClient.doGet();
// } catch (UnsupportedEncodingException e) {
// DbgUtil.showDebug(TAG,
// "UnsupportedEncodingException: " + e.getMessage());
// } catch (ClientProtocolException e) {
// DbgUtil.showDebug(TAG,
// "ClientProtocolException: " + e.getMessage());
// } catch (IOException e) {
// DbgUtil.showDebug(TAG, "IOException: " + e.getMessage());
// }
//
// // Util.doGet(uri);
// } else {
// DbgUtil.showDebug(TAG, "onUnregistered: ignore");
// }
// }
//
// @Override
// protected void onDeletedMessages(Context context, int total) {
// DbgUtil.showDebug(TAG, "onDeletedMessages total=" + total);
// toast("onDeletedMessages: " + total);
// }
//
// @Override
// public void onError(Context context, String errorId) {
// DbgUtil.showDebug(TAG, "onError: " + errorId);
// toast("onError: " + errorId);
// }
//
// @Override
// protected boolean onRecoverableError(Context context, String errorId) {
// DbgUtil.showDebug(TAG, "onRecoverableError: " + errorId);
// toast("onRecoverableError: " + errorId);
// return super.onRecoverableError(context, errorId);
// }
//
// private void toast(final String msg) {
// toaster.post(new Runnable() {
// @Override
// public void run() {
// Toast.makeText(GCMIntentService.this, msg, Toast.LENGTH_LONG)
// .show();
// }
// });
// }
// }