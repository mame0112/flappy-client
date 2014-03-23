package com.mame.lcom.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.widget.Toast;

import com.mame.lcom.constant.LcomConst;

public class FriendListActivityUtil {

	public static void startActivityConversationViewByPos(Activity activity,
			int userId, String userName, int position, int targetUserId,
			String targetUserName, String mailAddress, Bitmap thumbnail) {
		Intent intent = new Intent(activity, ConversationActivity.class);
		intent.setAction(Intent.ACTION_VIEW);
		intent.putExtra(LcomConst.EXTRA_USER_ID, userId);
		intent.putExtra(LcomConst.EXTRA_USER_NAME, userName);
		intent.putExtra(LcomConst.EXTRA_TARGET_USER_ID, targetUserId);
		intent.putExtra(LcomConst.EXTRA_TARGET_USER_NAME, targetUserName);
		intent.putExtra(LcomConst.EXTRA_TARGET_MAIL_ADDRESS, mailAddress);
		intent.putExtra(LcomConst.EXTRA_THUMBNAIL, thumbnail);
		// intent.putExtra(LcomConst.EXTRA_TARGET_NEW_MESSAGES, newMessages);
		// intent.putExtra(LcomConst.EXTRA_TARGET_NEW_MESSAGES_DATE, newDates);
		activity.startActivity(intent);
	}

	public static void startActivityForInvitation(Activity activity,
			int userId, String userName, int requestCode) {
		Intent intent = new Intent(activity, StartNewConversationActivity.class);
		intent.setAction(Intent.ACTION_VIEW);
		intent.putExtra(LcomConst.EXTRA_USER_ID, userId);
		intent.putExtra(LcomConst.EXTRA_USER_NAME, userName);
		activity.startActivityForResult(intent, requestCode);
	}

	public static void startActivityForHelp(Context context) {
		Intent intent = new Intent(context, HelpActivity.class);
		intent.setAction(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	public static void startActivityForWelcomeActivity(Activity activity) {
		Intent intent = new Intent(activity, WelcomeActivity.class);
		intent.setAction(Intent.ACTION_VIEW);
		activity.startActivity(intent);
	}

}
