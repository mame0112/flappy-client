package com.mame.flappy.notification;

import com.mame.flappy.constant.LcomConst;
import com.mame.flappy.util.DbgUtil;

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
		NewMessageNotificationManager.removeNotification();

		int targetUserId = intent.getIntExtra(LcomConst.EXTRA_TARGET_USER_ID,
				LcomConst.NO_USER);
		int fromUserId = intent.getIntExtra(LcomConst.EXTRA_USER_ID,
				LcomConst.NO_USER);

		DbgUtil.showDebug(TAG, "targetUserId: " + targetUserId);
		DbgUtil.showDebug(TAG, "fromUserId: " + fromUserId);

		// set next notification (AlarmManager)
		NewMessageNotificationManager
				.setNextNotification(getApplicationContext());

		// Send brodcast to Activity
		Intent bcastIntent = new Intent(LcomConst.ACTION_MESSAGE_EXPIRE);
		bcastIntent.putExtra(LcomConst.EXTRA_USER_ID, fromUserId);
		bcastIntent.putExtra(LcomConst.EXTRA_TARGET_USER_ID, targetUserId);
		sendBroadcast(bcastIntent);
	}

}
