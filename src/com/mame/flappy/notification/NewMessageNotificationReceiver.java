package com.mame.flappy.notification;

import com.mame.flappy.constant.LcomConst;
import com.mame.flappy.util.DbgUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

public class NewMessageNotificationReceiver extends BroadcastReceiver {

	private final String TAG = LcomConst.TAG
			+ "/NewMessageNotificationReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		DbgUtil.showDebug(TAG, "onReceive");

		if (intent != null) {

			// Screen on
			PowerManager pm = (PowerManager) context
					.getSystemService(Context.POWER_SERVICE);
			PowerManager.WakeLock wl = pm.newWakeLock(
					PowerManager.PARTIAL_WAKE_LOCK, "My Tag");
			wl.acquire(1000);

			int targetUserId = intent.getIntExtra(
					LcomConst.EXTRA_TARGET_USER_ID, LcomConst.NO_USER);
			int fromUserId = intent.getIntExtra(LcomConst.EXTRA_USER_ID,
					LcomConst.NO_USER);

			wl.release();

			Intent i = new Intent(context, NewMessageNotificationService.class);
			i.putExtra(LcomConst.EXTRA_USER_ID, fromUserId);
			i.putExtra(LcomConst.EXTRA_TARGET_USER_ID, targetUserId);
			context.startService(i);
		}
	}
}
