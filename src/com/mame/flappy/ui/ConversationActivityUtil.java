package com.mame.flappy.ui;

import java.util.ArrayList;

import android.content.Context;

import com.mame.flappy.constant.LcomConst;
import com.mame.flappy.util.DbgUtil;
import com.mame.flappy.util.TrackingUtil;

public class ConversationActivityUtil {

	private final static String TAG = LcomConst.TAG
			+ "/ConversationActivityUtil";

	public static ArrayList<String> parseMessageBasedOnMaxLength(
			Context context, String message) {
		ArrayList<String> result = new ArrayList<String>();
		if (message != null) {
			DbgUtil.showDebug(TAG, "message: " + message);
			// If message length is longer than maximum (16 characters), pares
			// it to ArrayList. Otherwise, nothing to do.
			if (message.length() < LcomConst.MESSAGE_MAX_LENGTH) {
				while (message.length() <= LcomConst.MESSAGE_MAX_LENGTH) {
					String parsed = message.substring(0,
							LcomConst.MESSAGE_MAX_LENGTH);
					DbgUtil.showDebug(TAG, "parsed: " + parsed);
					result.add(parsed);
					message = message.substring(LcomConst.MESSAGE_MAX_LENGTH,
							message.length());
				}
			} else {
				result.add(message);
			}
		} else {
			result = null;
			if (context != null) {
				TrackingUtil.trackExceptionMessage(context, TAG,
						"parseMessageBasedOnMaxLength: " + "message is null");
			}
		}
		return result;
	}

	public static String parseArrayListMessageToString(
			ArrayList<String> messageList) {
		String result = "a";
		if (messageList != null) {
			DbgUtil.showDebug("ConversationActivityUtil", "messageList size: "
					+ messageList.size());
			for (String message : messageList) {
				result = result + message + LcomConst.SEPARATOR;
			}

			if (result != null) {
				// Remove last separator
				result = result.substring(1, result.length()
						- LcomConst.SEPARATOR.length());
			}
		}

		return result;
	}
}
