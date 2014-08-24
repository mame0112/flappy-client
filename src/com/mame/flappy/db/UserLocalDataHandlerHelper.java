package com.mame.flappy.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;

import com.mame.flappy.constant.LcomConst;
import com.mame.flappy.data.FriendListData;
import com.mame.flappy.data.MessageItemData;
import com.mame.flappy.data.NotificationContentData;
import com.mame.flappy.ui.FriendListActivity;
import com.mame.flappy.util.DbgUtil;

public class UserLocalDataHandlerHelper {

	private final static String TAG = LcomConst.TAG
			+ "/UserLocalDataHandlerHelper";

	public ArrayList<NotificationContentData> sortNotificationBasedOnExpireTime(
			ArrayList<NotificationContentData> input) {

		Collections.sort(input, new NotificationTimeComparator());

		return input;
	}

	public ArrayList<MessageItemData> convertFormatFriendListToMessageItem(
			long userId, String userName, ArrayList<FriendListData> itemData) {
		DbgUtil.showDebug(TAG, "convertFormatFriendListToMessageItem");

		if (itemData != null && itemData.size() != 0) {

			ArrayList<MessageItemData> result = new ArrayList<MessageItemData>();

			for (FriendListData data : itemData) {

				// Extract from FriendListData
				int friendId = data.getFriendId();
				String friendName = data.getFriendName();
				int lastSenderId = data.getLastSender();
				String lastMessage = data.getLastMessage();
				long lastMsgDate = data.getMessagDate();
				Bitmap thumbnail = data.getThumbnail();

				// Conver to NewMessageData

				// If friend is last sender
				if (friendId == lastSenderId) {
					// TODO need to care about userId (int / long)
					MessageItemData item = new MessageItemData(friendId,
							(int) userId, friendName, userName, lastMessage,
							lastMsgDate, thumbnail);
					result.add(item);
				} else {
					// If user itself is last sender
					// TODO need to care about userId (int / long)
					MessageItemData item = new MessageItemData((int) userId,
							friendId, userName, friendName, lastMessage,
							lastMsgDate, thumbnail);
					result.add(item);
				}
			}

			return result;

		}

		return null;
	}

	public static class NotificationTimeComparator implements
			Comparator<NotificationContentData> {

		@Override
		public int compare(NotificationContentData lhs,
				NotificationContentData rhs) {

			long time1 = lhs.getExpireData();
			long time2 = rhs.getExpireData();

			if (time1 > time2) {
				return 1;

			} else if (time1 == time2) {
				return 0;

			} else {
				return -1;

			}
		}
	}

}
