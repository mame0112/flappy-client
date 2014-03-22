package com.mame.lcom.server;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.util.DbgUtil;

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
			}
		}
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

}