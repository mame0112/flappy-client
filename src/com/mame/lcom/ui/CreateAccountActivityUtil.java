package com.mame.lcom.ui;

import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Handler;
import android.util.Base64;
import android.widget.Toast;

import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.util.DbgUtil;
import com.mame.lcom.util.PreferenceUtil;

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
		galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
		activity.startActivityForResult(galleryIntent, requestCode);
	}

	public static String transcodeBitmap2String(Bitmap bitmap) {
		String imageBinary = null;

		if (bitmap != null) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.JPEG, 90, bos);
			byte[] byteArray = bos.toByteArray();
			String image64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
			imageBinary = "data:image/jpeg:base64, " + image64;
		}
		return imageBinary;

	}
}
