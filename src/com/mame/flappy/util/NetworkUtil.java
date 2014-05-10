package com.mame.flappy.util;

import com.mame.flappy.R;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.widget.Toast;

public class NetworkUtil {

	public static boolean isNetworkAvailable(final Activity activity,
			final Handler handler) {
		ConnectivityManager cm = (ConnectivityManager) activity
				.getSystemService(Activity.CONNECTIVITY_SERVICE);
		NetworkInfo nInfo = cm.getActiveNetworkInfo();
		if (nInfo == null) {
			showNoNetworkToast(activity.getApplicationContext(), handler);
			return false;
		} else {
			if (nInfo.isConnected()) {
				return true;
			} else {
				showNoNetworkToast(activity.getApplicationContext(), handler);
				return false;
			}
		}
	}

	public static void showNoNetworkToast(final Context context,
			final Handler handler) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				handler.post(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(context,
								R.string.str_generic_no_network_error,
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		}).start();
	}
	
}
