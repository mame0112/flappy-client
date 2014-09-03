package com.mame.flappy.server;

import com.mame.flappy.constant.LcomConst;
import com.mame.flappy.util.DbgUtil;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

//public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
public class GcmBroadcastReceiver extends BroadcastReceiver {

	private final String TAG = LcomConst.TAG + "/GcmBroadcastReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		DbgUtil.showDebug(TAG, "onReceive");
		// ComponentName comp = new ComponentName(context.getPackageName(),
		// GCMIntentService.class.getName());
		intent.setClass(context, GCMIntentService.class);
		context.startService(intent);

		// startWakefulService(context, (intent.setComponent(comp)));
		// setResultCode(Activity.RESULT_OK);
	}
}
