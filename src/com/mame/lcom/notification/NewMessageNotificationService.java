package com.mame.lcom.notification;

import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.util.DbgUtil;

import android.app.IntentService;
import android.content.Intent;

public class NewMessageNotificationService extends IntentService {

	private final String TAG = LcomConst.TAG + "/NewMessageNotificationService";

	public NewMessageNotificationService() {
		super("NewMessageNotificationService");
	}

	public NewMessageNotificationService(String name) {
		super(name);
		DbgUtil.showDebug(TAG, "NewMessageNotificationService");

	}

	@Override
	protected void onHandleIntent(Intent intent) {
		DbgUtil.showDebug(TAG, "onHandleIntent");
		int userId = intent.getIntExtra(LcomConst.EXTRA_USER_ID,
				LcomConst.NO_USER);
		NewMessageNotificationManager.setNextNotification(userId);
	}

}
