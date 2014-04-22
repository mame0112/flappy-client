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

		Intent i = new Intent(context, NewMessageNotificationService.class);
		context.startService(i);

	}

}
