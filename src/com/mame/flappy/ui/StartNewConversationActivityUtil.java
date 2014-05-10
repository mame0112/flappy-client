package com.mame.flappy.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

public class StartNewConversationActivityUtil {

	public static void showFeedbackToast(final Context context,
			final Handler handler, final String errorText) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				handler.post(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(context, errorText, Toast.LENGTH_SHORT)
								.show();
					}
				});
			}

		}).start();
	}

	public static void startActivityForContactsList(Activity activity,
			int requestCode) {
		Intent intent = new Intent(activity.getApplicationContext(),
				ContactListActivity.class);
		intent.setAction(Intent.ACTION_VIEW);
//		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		activity.startActivityForResult(intent, requestCode);
	}

}
