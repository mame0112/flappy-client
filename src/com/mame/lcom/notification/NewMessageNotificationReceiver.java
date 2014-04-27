package com.mame.lcom.notification;

import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.util.DbgUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NewMessageNotificationReceiver extends BroadcastReceiver {

	private final String TAG = LcomConst.TAG
			+ "/NewMessageNotificationReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		DbgUtil.showDebug(TAG, "onReceive");

		if (intent != null) {
			int targetUserId = intent.getIntExtra(
					LcomConst.EXTRA_TARGET_USER_ID, LcomConst.NO_USER);
			int fromUserId = intent.getIntExtra(LcomConst.EXTRA_USER_ID,
					LcomConst.NO_USER);

			Intent i = new Intent(context, NewMessageNotificationService.class);
			i.putExtra(LcomConst.EXTRA_USER_ID, fromUserId);
			i.putExtra(LcomConst.EXTRA_TARGET_USER_ID, targetUserId);
			context.startService(i);
		}
	}
}
