package com.mame.flappy.util;

import com.mame.flappy.R;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public class FeedbackUtil {
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

	public static void showFeedbackToast(final Context context,
			final Handler handler, final int resId) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				handler.post(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(context, context.getString(resId),
								Toast.LENGTH_SHORT).show();
					}
				});
			}

		}).start();
	}

	public static void showTimeoutToast(final Context context,
			final Handler handler) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				handler.post(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(
								context,
								context.getString(R.string.str_generic_server_timeout),
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		}).start();
	}
}
