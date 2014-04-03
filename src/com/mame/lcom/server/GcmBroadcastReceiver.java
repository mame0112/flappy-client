package com.mame.lcom.server;

import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.util.DbgUtil;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
	
	private final String TAG = LcomConst.TAG + "/GcmBroadcastReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		DbgUtil.showDebug(TAG, "onReceive");
		ComponentName comp = new ComponentName(context.getPackageName(),
				GCMIntentService.class.getName());
		startWakefulService(context, (intent.setComponent(comp)));
		setResultCode(Activity.RESULT_OK);
	}
}
