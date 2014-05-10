package com.mame.flappy.ui;

import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.util.Base64;
import android.widget.Toast;

import com.mame.flappy.constant.LcomConst;
import com.mame.flappy.util.DbgUtil;
import com.mame.flappy.util.PreferenceUtil;

public class CreateAccountActivityUtil {
	public static boolean isHalfSizeString(String input) {
		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);

			// "From 0 to 9" or "From @ to Z" or "From a to z" or "-" or "_"
			if (((c >= '\u0030') && ((c <= '\u0039')))
					|| ((c >= '\u0040') && ((c <= '\u005a')))
					|| ((c >= '\u0061') && ((c <= '\u007a')))
					|| (c == '\u002d') || (c == '\u005f')) {
			} else {
				return false;
			}
		}
		return true;
	}

	public static boolean isInputtedPasswordSame(String firstPassword,
			String secondPassword) {
		if (firstPassword != null && secondPassword != null) {
			if (firstPassword.equals(secondPassword)) {
				return true;
			}
		}
		return false;
	}

	public static void startActivityForFriendList(Activity activity,
			int userId, String userName) {
		Intent intent = new Intent(activity, FriendListActivity.class);
		intent.setAction(Intent.ACTION_VIEW);
		intent.putExtra(LcomConst.EXTRA_USER_ID, userId);
		intent.putExtra(LcomConst.EXTRA_USER_NAME, userName);
		activity.startActivity(intent);
	}

	public static void startActivityForCreateAccountComplete(Activity activity,
			String userName, Bitmap thumbnail) {
		Intent intent = new Intent(activity,
				CreateAccountCompleteActivity.class);
		intent.setAction(Intent.ACTION_VIEW);
		intent.putExtra(LcomConst.EXTRA_USER_NAME, userName);
		intent.putExtra(LcomConst.EXTRA_THUMBNAIL, thumbnail);
		activity.startActivity(intent);
	}

	public static void openURL(Context c, String targetURL) {
		Uri uri = Uri.parse(LcomConst.BASE_URL + targetURL);
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		c.startActivity(intent);
	}

	public static void storeUserDataToPref(Context context, int userId,
			String userName) {
		PreferenceUtil.setUserId(context, userId);
		PreferenceUtil.setUserName(context, userName);
	}

	public static void showErrorToast(final Context context,
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

	public static void launchPhotoPicker(Activity activity, int requestCode) {
		Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT, null);
		galleryIntent.setType("image/*");
		galleryIntent.setAction(Intent.ACTION_PICK);
		activity.startActivityForResult(galleryIntent, requestCode);
	}

}
