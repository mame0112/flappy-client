package com.mame.flappy.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sqlcipher.database.SQLiteDatabase;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.CursorJoiner.Result;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.test.AndroidTestCase;

import com.mame.flappy.constant.LcomConst;
import com.mame.flappy.data.FriendListData;
import com.mame.flappy.data.MessageItemData;
import com.mame.flappy.data.NotificationContentData;
import com.mame.flappy.db.DatabaseDef;
import com.mame.flappy.db.UserDatabaseHelper;
import com.mame.flappy.db.UserLocalDataHandler;
import com.mame.flappy.db.UserLocalDataHandlerHelper;
import com.mame.flappy.exception.UserLocalDataHandlerException;
import com.mame.flappy.test.util.ReflectionUtil;
import com.mame.flappy.util.DbgUtil;
import com.mame.flappy.util.ImageUtil;
import com.mame.flappy.util.SecurityUtil;
import com.mame.flappy.util.TimeUtil;
import com.mame.flappy.util.TrackingUtil;

public class UserLocalDataHandlerTest extends AndroidTestCase {

	private final String TAG = LcomConst.TAG + "/UserLocalDataHandlerTest";

	private static SQLiteDatabase sDatabase;

	private synchronized void setDatabase() {
		if (sDatabase == null || !sDatabase.isOpen()) {
			UserDatabaseHelper helper = new UserDatabaseHelper(mContext);
			sDatabase.loadLibs(mContext);
			String UUID = SecurityUtil.getUniqueId(mContext);
			sDatabase = helper.getWritableDatabase(UUID);
		}
	}

	public void testSetDatabase() throws Exception {

		UserLocalDataHandler handler = new UserLocalDataHandler(getContext());
		Context context = (Context) ReflectionUtil.getValue(
				UserLocalDataHandler.class, "mContext", handler);

		assertNotNull(context);
	}

	public void testGetLocalMessageDataset1() throws Exception {

		UserLocalDataHandler handler = new UserLocalDataHandler(getContext());
		setDatabase();
		handler.removeLocalUserPreferenceData(getContext());

		int myUserId = 1;
		int friendUserId = 2;
		String myUserName = "aaaa";
		String friendUserName = "bbbb";
		String message = "test message";
		long date = TimeUtil.getCurrentDate();

		ContentValues valuesForMessage = getInsertContentValuesForMessage(
				myUserId, friendUserId, myUserName, friendUserName, message,
				String.valueOf(date));

		long id = sDatabase.insert(DatabaseDef.MessageTable.TABLE_NAME, null,
				valuesForMessage);

		assertNotSame(id, -1);

		ArrayList<MessageItemData> result = handler
				.getLocalMessageDataset(friendUserId);

		assertNotNull(result);
		assertEquals(result.size(), 1);

		MessageItemData data = result.get(0);

		assertEquals(data.getFromUserId(), myUserId);
		assertEquals(data.getFromUserName(), myUserName);
		assertEquals(data.getTargetUserId(), friendUserId);
		assertEquals(data.getToUserName(), friendUserName);
		assertEquals(data.getMessage(), message);
		assertEquals(data.getThumbnail(), null);
		assertEquals(data.getPostedDate(), date);

		handler.removeLocalUserPreferenceData(getContext());

	}

	// public void testGetLocalMessageDataset2() throws Exception {
	//
	// UserLocalDataHandler handler = new UserLocalDataHandler(getContext());
	// setDatabase();
	// handler.removeLocalUserPreferenceData(getContext());
	//
	// int myUserId = 1;
	// int friendUserId = 2;
	// String myUserName = "aaaa";
	// String friendUserName = "bbbb";
	// String message = "test message";
	// long date = TimeUtil.getCurrentDate();
	//
	// ContentValues valuesForMessage = getInsertContentValuesForMessage(
	// myUserId, friendUserId, myUserName, friendUserName, message,
	// String.valueOf(date));
	//
	// long id = sDatabase.insert(DatabaseDef.MessageTable.TABLE_NAME, null,
	// valuesForMessage);
	//
	// assertNotSame(id, -1);
	//
	// int myUserId2 = 1;
	// int friendUserId2 = 2;
	// String myUserName2 = "aaaa";
	// String friendUserName2 = "bbbb";
	// String message2 = "test message2";
	// long date2 = TimeUtil.getCurrentDate();
	//
	// valuesForMessage = getInsertContentValuesForMessage(myUserId2,
	// friendUserId2, myUserName2, friendUserName2, message2,
	// String.valueOf(date2));
	//
	// long id2 = sDatabase.insert(DatabaseDef.MessageTable.TABLE_NAME, null,
	// valuesForMessage);
	//
	// assertNotSame(id2, -1);
	//
	// ArrayList<MessageItemData> result = handler
	// .getLocalMessageDataset(friendUserId);
	//
	// assertNotNull(result);
	// assertEquals(result.size(), 2);
	//
	// MessageItemData data = result.get(0);
	//
	// assertEquals(data.getFromUserId(), myUserId);
	// assertEquals(data.getFromUserName(), myUserName);
	// assertEquals(data.getTargetUserId(), friendUserId);
	// assertEquals(data.getToUserName(), friendUserName);
	// assertEquals(data.getMessage(), message);
	// assertEquals(data.getThumbnail(), null);
	// assertEquals(data.getPostedDate(), date);
	//
	// MessageItemData data2 = result.get(1);
	//
	// assertEquals(data2.getFromUserId(), myUserId2);
	// assertEquals(data2.getFromUserName(), myUserName2);
	// assertEquals(data2.getTargetUserId(), friendUserId2);
	// assertEquals(data2.getToUserName(), friendUserName2);
	// assertEquals(data2.getMessage(), message2);
	// assertEquals(data2.getThumbnail(), null);
	// assertEquals(data2.getPostedDate(), date2);
	//
	// handler.removeLocalUserPreferenceData(getContext());
	//
	// }

	public void testGetLocalMessageDataset3() throws Exception {

		UserLocalDataHandler handler = new UserLocalDataHandler(getContext());
		setDatabase();
		handler.removeLocalUserPreferenceData(getContext());

		int myUserId = 1;
		int friendUserId = 2;
		String myUserName = "aaaa";
		String friendUserName = "bbbb";
		String message = "test message3";
		long date = TimeUtil.getCurrentDate();

		ContentValues valuesForMessage = getInsertContentValuesForMessage(
				friendUserId, myUserId, friendUserName, myUserName, message,
				String.valueOf(date));

		long id = sDatabase.insert(DatabaseDef.MessageTable.TABLE_NAME, null,
				valuesForMessage);

		assertNotSame(id, -1);

		ArrayList<MessageItemData> result = handler
				.getLocalMessageDataset(friendUserId);

		assertNotNull(result);
		assertEquals(result.size(), 1);

		MessageItemData data = result.get(0);

		assertEquals(data.getFromUserId(), friendUserId);
		assertEquals(data.getFromUserName(), friendUserName);
		assertEquals(data.getTargetUserId(), myUserId);
		assertEquals(data.getToUserName(), myUserName);
		assertEquals(data.getMessage(), message);
		assertEquals(data.getThumbnail(), null);
		assertEquals(data.getPostedDate(), date);

		handler.removeLocalUserPreferenceData(getContext());

	}

	public void testGetLocalUserDataset1() {
		UserLocalDataHandler handler = new UserLocalDataHandler(getContext());
		setDatabase();
		handler.removeLocalUserPreferenceData(getContext());

		int friendId = 2;
		String friendName = "bbbb";
		byte[] friendThumb = null;
		int lastSenderId = 2;
		String lastMessage = "test message here";
		String mailAddress = "a@a";

		ContentValues valuesForFriendship = getInsertContentValuesForFriendship(
				friendId, friendName, friendThumb, lastSenderId, lastMessage,
				mailAddress);
		long friendshipId = sDatabase.insert(
				DatabaseDef.FriendshipTable.TABLE_NAME, null,
				valuesForFriendship);

		assertNotSame(friendshipId, -1);

		try {
			ArrayList<FriendListData> result = handler.getLocalUserDataset(0);
			assertNotNull(result);
			assertEquals(result.size(), 1);

			FriendListData data = result.get(0);

			assertEquals(data.getFriendId(), friendId);
			assertEquals(data.getFriendName(), friendName);
			assertEquals(data.getThumbnail(), null);
			assertEquals(data.getLastMessage(), lastMessage);
			assertEquals(data.getLastSender(), lastSenderId);
			assertNotSame(data.getMessagDate(), 0);
			assertEquals(data.getMailAddress(), mailAddress);

		} catch (UserLocalDataHandlerException e) {
			assertTrue(false);
		}

		handler.removeLocalUserPreferenceData(getContext());
	}

	public void testGetLocalUserDataset2() {
		UserLocalDataHandler handler = new UserLocalDataHandler(getContext());
		setDatabase();
		handler.removeLocalUserPreferenceData(getContext());

		Drawable d = getContext().getResources().getDrawable(
				R.drawable.flappy_default_thumbnail_large);
		Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
		byte[] friendThumb = ImageUtil.encodeBitmapToByteArray(bitmap);
		int friendId = 2;
		String friendName = "bbbb";
		int lastSenderId = 2;
		String lastMessage = "test message here";
		String mailAddress = "a@a";

		int friendId2 = 3;
		String friendName2 = "cccc";
		byte[] friendThumb2 = null;
		int lastSenderId2 = 3;
		String lastMessage2 = "test message here2";
		String mailAddress2 = "b@b";

		ContentValues valuesForFriendship = getInsertContentValuesForFriendship(
				friendId, friendName, friendThumb, lastSenderId, lastMessage,
				mailAddress);
		long friendshipId = sDatabase.insert(
				DatabaseDef.FriendshipTable.TABLE_NAME, null,
				valuesForFriendship);

		assertNotSame(friendshipId, -1);

		ContentValues valuesForFriendship2 = getInsertContentValuesForFriendship(
				friendId2, friendName2, friendThumb2, lastSenderId2,
				lastMessage2, mailAddress2);
		long friendshipId2 = sDatabase.insert(
				DatabaseDef.FriendshipTable.TABLE_NAME, null,
				valuesForFriendship2);

		assertNotSame(friendshipId2, -1);

		try {
			ArrayList<FriendListData> result = handler.getLocalUserDataset(0);
			assertNotNull(result);
			assertEquals(result.size(), 2);

			FriendListData data = result.get(0);

			assertEquals(data.getFriendId(), friendId);
			assertEquals(data.getFriendName(), friendName);
			assertEquals(data.getThumbnail().getByteCount(),
					bitmap.getByteCount());
			assertEquals(data.getLastMessage(), lastMessage);
			assertEquals(data.getLastSender(), lastSenderId);
			assertNotSame(data.getMessagDate(), 0);
			assertEquals(data.getMailAddress(), mailAddress);

			FriendListData data2 = result.get(1);

			assertEquals(data2.getFriendId(), friendId2);
			assertEquals(data2.getFriendName(), friendName2);
			assertEquals(data2.getThumbnail(), null);
			assertEquals(data2.getLastMessage(), lastMessage2);
			assertEquals(data2.getLastSender(), lastSenderId2);
			assertNotSame(data2.getMessagDate(), 0);
			assertEquals(data2.getMailAddress(), mailAddress2);

		} catch (UserLocalDataHandlerException e) {
			assertTrue(false);
		}

		handler.removeLocalUserPreferenceData(getContext());
	}

	/**
	 * Regular case
	 */
	public void testAddNewMessageAndFriendIfNecessary1() {

		UserLocalDataHandler handler = new UserLocalDataHandler(getContext());
		setDatabase();
		handler.removeLocalUserPreferenceData(getContext());

		int userId = 1;
		int friendId = 2;
		String userName = "aaaa";
		int senderId = 2;
		String message = "test message";
		String date = String.valueOf(TimeUtil.getCurrentDate());
		String friendName = "bbbb";

		Drawable d = getContext().getResources().getDrawable(
				R.drawable.flappy_default_thumbnail_large);
		Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
		byte[] friendThumb = ImageUtil.encodeBitmapToByteArray(bitmap);

		String mailAddress = "d@d";

		try {
			handler.addNewMessageAndFriendIfNecessary(userId, friendId,
					userName, friendName, senderId, message, date, friendThumb,
					mailAddress);
		} catch (UserLocalDataHandlerException e) {
			assertTrue(false);
		}

		ContentResolver mContentResolver = (ContentResolver) ReflectionUtil
				.getValue(UserLocalDataHandler.class, "mContentResolver",
						handler);

		Cursor cursor = mContentResolver.query(DatabaseDef.MessageTable.URI,
				null, null, null, null);
		assertNotNull(cursor);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				do {
					String fromUserIdResult = cursor
							.getString(cursor
									.getColumnIndex(DatabaseDef.MessageColumns.FROM_USER_ID));
					String fromUserNameResult = cursor
							.getString(cursor
									.getColumnIndex(DatabaseDef.MessageColumns.FROM_USER_NAME));
					String toUserIdResult = cursor
							.getString(cursor
									.getColumnIndex(DatabaseDef.MessageColumns.TO_USER_ID));
					String toUserNameResult = cursor
							.getString(cursor
									.getColumnIndex(DatabaseDef.MessageColumns.TO_USER_NAME));
					String messageResult = cursor
							.getString(cursor
									.getColumnIndex(DatabaseDef.MessageColumns.MESSAGE));
					String dateResult = cursor.getString(cursor
							.getColumnIndex(DatabaseDef.MessageColumns.DATE));

					assertEquals(String.valueOf(userId), toUserIdResult);
					assertEquals(String.valueOf(senderId), fromUserIdResult);
					assertEquals(friendName, fromUserNameResult);
					assertEquals(userName, toUserNameResult);
					assertEquals(message, messageResult);
					assertEquals(date, dateResult);

				} while (cursor.moveToNext());
			}
		}

		Cursor cursor2 = mContentResolver.query(
				DatabaseDef.FriendshipTable.URI, null, null, null, null);
		assertNotNull(cursor);
		if (cursor2 != null) {
			if (cursor2.moveToFirst()) {
				do {
					String friendIdResult = cursor2
							.getString(cursor2
									.getColumnIndex(DatabaseDef.FriendshipColumns.FRIEND_ID));
					String friendNameResult = cursor2
							.getString(cursor2
									.getColumnIndex(DatabaseDef.FriendshipColumns.FRIEND_NAME));
					String lastMessageResult = cursor2
							.getString(cursor2
									.getColumnIndex(DatabaseDef.FriendshipColumns.LAST_MESSAGE));
					String lastSenderIdResult = cursor2
							.getString(cursor2
									.getColumnIndex(DatabaseDef.FriendshipColumns.LAST_SENDER_ID));
					String mailAddressResult = cursor2
							.getString(cursor2
									.getColumnIndex(DatabaseDef.FriendshipColumns.MAIL_ADDRESS));

					byte[] thumbnail = cursor2
							.getBlob(cursor2
									.getColumnIndex(DatabaseDef.FriendshipColumns.THUMBNAIL));

					assertEquals(message, lastMessageResult);
					assertEquals(String.valueOf(senderId), lastSenderIdResult);
					assertEquals(String.valueOf(friendId), friendIdResult);
					assertEquals(friendName, friendNameResult);
					assertEquals(mailAddress, mailAddressResult);
					assertNotNull(thumbnail);
					assertEquals(thumbnail.length, friendThumb.length);

				} while (cursor2.moveToNext());
			}
		}

		handler.removeLocalUserPreferenceData(getContext());
	}

	public void testAddMultipleNewMessages1() {

		UserLocalDataHandler handler = new UserLocalDataHandler(getContext());
		setDatabase();
		handler.removeLocalUserPreferenceData(getContext());

		int userId = 1;
		String userName = "aaaa";
		ArrayList<FriendListData> newMessages = new ArrayList<FriendListData>();

		int friendId = 2;
		String friendName = "bbbb";
		int lastSenderId = 2;
		String lastMessage = "test message";
		long lastMsgDate = TimeUtil.getCurrentDate();
		int numOfNewMessage = 3;
		String mailAddress = "a@a";
		Drawable d = getContext().getResources().getDrawable(
				R.drawable.flappy_default_thumbnail_large);
		Bitmap thumbnail = ((BitmapDrawable) d).getBitmap();
		byte[] thumbByte = ImageUtil.encodeBitmapToByteArray(thumbnail);

		FriendListData data = new FriendListData(friendId, friendName,
				lastSenderId, lastMessage, lastMsgDate, numOfNewMessage,
				mailAddress, thumbnail);
		newMessages.add(data);

		try {
			handler.addMultipleNewMessages(userId, userName, newMessages);
		} catch (UserLocalDataHandlerException e) {
			assertTrue(false);
		}

		ContentResolver mContentResolver = (ContentResolver) ReflectionUtil
				.getValue(UserLocalDataHandler.class, "mContentResolver",
						handler);

		Cursor cursor = mContentResolver.query(DatabaseDef.FriendshipTable.URI,
				null, null, null, null);
		assertNotNull(cursor);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				do {
					String friendIdResult = cursor
							.getString(cursor
									.getColumnIndex(DatabaseDef.FriendshipColumns.FRIEND_ID));
					String friendNameResult = cursor
							.getString(cursor
									.getColumnIndex(DatabaseDef.FriendshipColumns.FRIEND_NAME));
					String lastMessageResult = cursor
							.getString(cursor
									.getColumnIndex(DatabaseDef.FriendshipColumns.LAST_MESSAGE));
					String lastSenderIdResult = cursor
							.getString(cursor
									.getColumnIndex(DatabaseDef.FriendshipColumns.LAST_SENDER_ID));
					String mailAddressResult = cursor
							.getString(cursor
									.getColumnIndex(DatabaseDef.FriendshipColumns.MAIL_ADDRESS));

					byte[] thumb = cursor
							.getBlob(cursor
									.getColumnIndex(DatabaseDef.FriendshipColumns.THUMBNAIL));

					assertEquals(lastMessage, lastMessageResult);
					assertEquals(String.valueOf(lastSenderId),
							lastSenderIdResult);
					assertEquals(String.valueOf(friendId), friendIdResult);
					assertEquals(friendName, friendNameResult);
					assertNull(mailAddressResult);
					assertNotNull(thumbnail);
					assertNull(thumb);

				} while (cursor.moveToNext());
			}
		}

		handler.removeLocalUserPreferenceData(getContext());
	}

	public void testAddMultipleNewMessages2() {

		UserLocalDataHandler handler = new UserLocalDataHandler(getContext());
		setDatabase();
		handler.removeLocalUserPreferenceData(getContext());

		int userId = 1;
		String userName = "aaaa";
		ArrayList<FriendListData> newMessages = new ArrayList<FriendListData>();

		int friendId = 2;
		String friendName = "bbbb";
		int lastSenderId = 2;
		String lastMessage = "test message";
		long lastMsgDate = TimeUtil.getCurrentDate();
		int numOfNewMessage = 3;
		String mailAddress = "a@a";
		Drawable d = getContext().getResources().getDrawable(
				R.drawable.flappy_default_thumbnail_large);
		Bitmap thumbnail = ((BitmapDrawable) d).getBitmap();
		byte[] thumbByte = ImageUtil.encodeBitmapToByteArray(thumbnail);

		FriendListData data = new FriendListData(friendId, friendName,
				lastSenderId, lastMessage, lastMsgDate, numOfNewMessage,
				mailAddress, thumbnail);
		newMessages.add(data);

		int friendId2 = 3;
		String friendName2 = "cccc";
		int lastSenderId2 = 1;
		String lastMessage2 = "test message2";
		long lastMsgDate2 = TimeUtil.getCurrentDate();
		int numOfNewMessage2 = 1;
		String mailAddress2 = "b@b";
		Drawable d2 = getContext().getResources().getDrawable(
				R.drawable.flappy_default_thumbnail_large);
		Bitmap thumbnail2 = ((BitmapDrawable) d2).getBitmap();

		FriendListData data2 = new FriendListData(friendId2, friendName2,
				lastSenderId2, lastMessage2, lastMsgDate2, numOfNewMessage2,
				mailAddress2, thumbnail2);
		newMessages.add(data2);

		try {
			handler.addMultipleNewMessages(userId, userName, newMessages);
		} catch (UserLocalDataHandlerException e) {
			assertTrue(false);
		}

		ContentResolver mContentResolver = (ContentResolver) ReflectionUtil
				.getValue(UserLocalDataHandler.class, "mContentResolver",
						handler);

		Cursor cursor = mContentResolver.query(DatabaseDef.FriendshipTable.URI,
				null, null, null, null);
		assertNotNull(cursor);

		int count = 0;

		if (cursor != null) {
			if (cursor.moveToFirst()) {
				do {

					String friendIdResult = cursor
							.getString(cursor
									.getColumnIndex(DatabaseDef.FriendshipColumns.FRIEND_ID));
					String friendNameResult = cursor
							.getString(cursor
									.getColumnIndex(DatabaseDef.FriendshipColumns.FRIEND_NAME));
					String lastMessageResult = cursor
							.getString(cursor
									.getColumnIndex(DatabaseDef.FriendshipColumns.LAST_MESSAGE));
					String lastSenderIdResult = cursor
							.getString(cursor
									.getColumnIndex(DatabaseDef.FriendshipColumns.LAST_SENDER_ID));
					String mailAddressResult = cursor
							.getString(cursor
									.getColumnIndex(DatabaseDef.FriendshipColumns.MAIL_ADDRESS));

					byte[] thumb = cursor
							.getBlob(cursor
									.getColumnIndex(DatabaseDef.FriendshipColumns.THUMBNAIL));

					switch (count) {
					case 0:
						assertEquals(lastMessage, lastMessageResult);
						assertEquals(String.valueOf(lastSenderId),
								lastSenderIdResult);
						assertEquals(String.valueOf(friendId), friendIdResult);
						assertEquals(friendName, friendNameResult);
						assertNull(mailAddressResult);
						assertNotNull(thumbnail);
						assertNull(thumb);
						break;
					case 1:
						assertEquals(lastMessage2, lastMessageResult);
						assertEquals(String.valueOf(lastSenderId2),
								lastSenderIdResult);
						assertEquals(String.valueOf(friendId2), friendIdResult);
						assertEquals(friendName2, friendNameResult);
						assertNull(mailAddressResult);
						assertNotNull(thumbnail2);
						assertNull(thumb);
						break;
					default:
						assertTrue(false);
						break;
					}

					count++;

				} while (cursor.moveToNext());
			}
		}

		handler.removeLocalUserPreferenceData(getContext());
	}

	/**
	 * No preset data case
	 */
	public void testAddMultipleNewMessagesAndFriendIfNecessary() {
		UserLocalDataHandler handler = new UserLocalDataHandler(getContext());
		setDatabase();
		handler.removeLocalUserPreferenceData(getContext());

		int userId = 1;
		String userName = "aaaa";
		ArrayList<MessageItemData> newMessages = new ArrayList<MessageItemData>();

		int fromUserId = 2;
		int toUserId = 1;
		String fromUserName = "bbbb";
		String toUserName = "aaaa";

		String lastMessage = "test message";
		long postedDate = TimeUtil.getCurrentDate();

		Drawable d = getContext().getResources().getDrawable(
				R.drawable.flappy_default_thumbnail_large);
		Bitmap thumbnail = ((BitmapDrawable) d).getBitmap();

		MessageItemData data = new MessageItemData(fromUserId, toUserId,
				fromUserName, toUserName, lastMessage, postedDate, thumbnail);
		newMessages.add(data);

		int fromUserId2 = 3;
		int toUserId2 = 1;
		String fromUserName2 = "cccc";
		String toUserName2 = "aaaa";

		String lastMessage2 = "test message2";
		long postedDate2 = TimeUtil.getCurrentDate() - 10000;
		Drawable d2 = getContext().getResources().getDrawable(
				R.drawable.flappy_default_thumbnail_large);
		Bitmap thumbnail2 = ((BitmapDrawable) d2).getBitmap();

		MessageItemData data2 = new MessageItemData(fromUserId2, toUserId2,
				fromUserName2, toUserName2, lastMessage2, postedDate2,
				thumbnail2);
		newMessages.add(data2);

		try {
			handler.addMultipleNewMessagesAndFriendIfNecessary(userId,
					newMessages);
		} catch (UserLocalDataHandlerException e) {
			assertTrue(false);
		}

		ContentResolver mContentResolver = (ContentResolver) ReflectionUtil
				.getValue(UserLocalDataHandler.class, "mContentResolver",
						handler);

		String order = DatabaseDef.FriendshipColumns._ID + " ASC";

		Cursor cursor = mContentResolver.query(DatabaseDef.FriendshipTable.URI,
				null, null, null, order);

		if (cursor != null) {
			if (cursor.moveToFirst()) {
				do {
					String friendIdResult = cursor
							.getString(cursor
									.getColumnIndex(DatabaseDef.FriendshipColumns.FRIEND_ID));
					String friendNameResult = cursor
							.getString(cursor
									.getColumnIndex(DatabaseDef.FriendshipColumns.FRIEND_NAME));
					String lastMessageResult = cursor
							.getString(cursor
									.getColumnIndex(DatabaseDef.FriendshipColumns.LAST_MESSAGE));
					String lastSenderIdResult = cursor
							.getString(cursor
									.getColumnIndex(DatabaseDef.FriendshipColumns.LAST_SENDER_ID));
					String mailAddressResult = cursor
							.getString(cursor
									.getColumnIndex(DatabaseDef.FriendshipColumns.MAIL_ADDRESS));

					byte[] thumb = cursor
							.getBlob(cursor
									.getColumnIndex(DatabaseDef.FriendshipColumns.THUMBNAIL));

					DbgUtil.showDebug(TAG, "friendIdResult: " + friendIdResult);

					assertEquals(lastMessage2, lastMessageResult);
					assertEquals(String.valueOf(fromUserId2),
							lastSenderIdResult);
					assertEquals(String.valueOf(fromUserId2), friendIdResult);
					assertEquals(fromUserName2, friendNameResult);
					assertNull(mailAddressResult);
					assertNotNull(thumbnail2);
					assertNull(thumb);
					break;

				} while (cursor.moveToNext());
			}
		}

		Cursor cursor2 = mContentResolver.query(DatabaseDef.MessageTable.URI,
				null, null, null, null);

		int count = 0;

		if (cursor2 != null) {
			if (cursor2.moveToFirst()) {
				do {
					String fromUserIdResult = cursor2
							.getString(cursor2
									.getColumnIndex(DatabaseDef.MessageColumns.FROM_USER_ID));
					String toUserIdResult = cursor2
							.getString(cursor2
									.getColumnIndex(DatabaseDef.MessageColumns.TO_USER_ID));
					String messageResult = cursor2
							.getString(cursor2
									.getColumnIndex(DatabaseDef.MessageColumns.MESSAGE));
					String timeResult = cursor2.getString(cursor2
							.getColumnIndex(DatabaseDef.MessageColumns.DATE));

					switch (count) {
					case 0:
						assertEquals(String.valueOf(fromUserId),
								fromUserIdResult);
						assertEquals(String.valueOf(toUserId), toUserIdResult);
						assertEquals(lastMessage, messageResult);
						assertEquals(String.valueOf(postedDate), timeResult);
						break;
					case 1:
						assertEquals(String.valueOf(fromUserId2),
								fromUserIdResult);
						assertEquals(String.valueOf(toUserId2), toUserIdResult);
						assertEquals(lastMessage2, messageResult);
						assertEquals(String.valueOf(postedDate2), timeResult);
						break;
					default:
						assertTrue(false);
						break;
					}

					count = count + 1;

				} while (cursor2.moveToNext());
			}
		}

		handler.removeLocalUserPreferenceData(getContext());

	}

	/**
	 * With preset data case (friendship info and message info)
	 */
	public void testAddMultipleNewMessagesAndFriendIfNecessary2() {
		UserLocalDataHandler handler = new UserLocalDataHandler(getContext());
		setDatabase();
		handler.removeLocalUserPreferenceData(getContext());

		int userId = 1;
		String userName = "aaaa";

		int fromUserId = 2;
		int toUserId = 1;
		String fromUserName = "bbbb";
		String toUserName = "aaaa";

		String lastMessage = "test message";
		long postedDate = TimeUtil.getCurrentDate();

		Drawable d = getContext().getResources().getDrawable(
				R.drawable.flappy_default_thumbnail_large);
		Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
		byte[] friendThumb = ImageUtil.encodeBitmapToByteArray(bitmap);

		// int friendId,
		// String friendName, byte[] friendThumbnail, int lastSenderId,
		// String lastMessage, String mailAddress

		ContentValues valuesForFriendship = getInsertContentValuesForFriendship(
				fromUserId, fromUserName, friendThumb, fromUserId, lastMessage,
				null);

		long friendshipId = sDatabase.insert(
				DatabaseDef.FriendshipTable.TABLE_NAME, null,
				valuesForFriendship);

		assertNotSame(friendshipId, -1);

		// int fromUserId,
		// int toUserId, String fromUserName, String toUserName,
		// String message, String date

		ContentValues valuesForMessage = getInsertContentValuesForMessage(
				fromUserId, userId, fromUserName, userName, lastMessage,
				String.valueOf(postedDate));

		long id = sDatabase.insert(DatabaseDef.MessageTable.TABLE_NAME, null,
				valuesForMessage);

		assertNotSame(id, -1);

		String lastMessage2 = "test message2";
		long postedDate2 = TimeUtil.getCurrentDate() - 10000;
		ContentValues valuesForMessage2 = getInsertContentValuesForMessage(
				fromUserId, userId, fromUserName, userName, lastMessage2,
				String.valueOf(postedDate2));
		long id2 = sDatabase.insert(DatabaseDef.MessageTable.TABLE_NAME, null,
				valuesForMessage2);
		assertNotSame(id2, -1);

		// ---

		ArrayList<MessageItemData> newMessages = new ArrayList<MessageItemData>();

		String lastMessage3 = "test message3";
		long postedDate3 = TimeUtil.getCurrentDate() - 200;

		MessageItemData data = new MessageItemData(fromUserId, toUserId,
				fromUserName, toUserName, lastMessage3, postedDate3, bitmap);
		newMessages.add(data);

		String lastMessage4 = "test message4";
		long postedDate4 = TimeUtil.getCurrentDate() - 300;

		MessageItemData data2 = new MessageItemData(fromUserId, toUserId,
				fromUserName, toUserName, lastMessage4, postedDate4, bitmap);
		newMessages.add(data2);

		try {
			handler.addMultipleNewMessagesAndFriendIfNecessary(userId,
					newMessages);
		} catch (UserLocalDataHandlerException e) {
			assertTrue(false);
		}

		ContentResolver mContentResolver = (ContentResolver) ReflectionUtil
				.getValue(UserLocalDataHandler.class, "mContentResolver",
						handler);

		String order = DatabaseDef.FriendshipColumns._ID + " ASC";

		Cursor cursor = mContentResolver.query(DatabaseDef.FriendshipTable.URI,
				null, null, null, order);

		if (cursor != null) {
			if (cursor.moveToFirst()) {
				do {
					String friendIdResult = cursor
							.getString(cursor
									.getColumnIndex(DatabaseDef.FriendshipColumns.FRIEND_ID));
					String friendNameResult = cursor
							.getString(cursor
									.getColumnIndex(DatabaseDef.FriendshipColumns.FRIEND_NAME));
					String lastMessageResult = cursor
							.getString(cursor
									.getColumnIndex(DatabaseDef.FriendshipColumns.LAST_MESSAGE));
					String lastSenderIdResult = cursor
							.getString(cursor
									.getColumnIndex(DatabaseDef.FriendshipColumns.LAST_SENDER_ID));
					String mailAddressResult = cursor
							.getString(cursor
									.getColumnIndex(DatabaseDef.FriendshipColumns.MAIL_ADDRESS));

					byte[] thumb = cursor
							.getBlob(cursor
									.getColumnIndex(DatabaseDef.FriendshipColumns.THUMBNAIL));

					DbgUtil.showDebug(TAG, "friendIdResult: " + friendIdResult);

					assertEquals(lastMessage4, lastMessageResult);
					assertEquals(String.valueOf(fromUserId), lastSenderIdResult);
					assertEquals(String.valueOf(fromUserId), friendIdResult);
					assertEquals(fromUserName, friendNameResult);
					assertNull(mailAddressResult);
					assertNotNull(thumb);

				} while (cursor.moveToNext());
			}
		}

		Cursor cursor2 = mContentResolver.query(DatabaseDef.MessageTable.URI,
				null, null, null, null);

		int count = 0;

		if (cursor2 != null) {
			if (cursor2.moveToFirst()) {
				do {
					String fromUserIdResult = cursor2
							.getString(cursor2
									.getColumnIndex(DatabaseDef.MessageColumns.FROM_USER_ID));
					String toUserIdResult = cursor2
							.getString(cursor2
									.getColumnIndex(DatabaseDef.MessageColumns.TO_USER_ID));
					String messageResult = cursor2
							.getString(cursor2
									.getColumnIndex(DatabaseDef.MessageColumns.MESSAGE));
					String timeResult = cursor2.getString(cursor2
							.getColumnIndex(DatabaseDef.MessageColumns.DATE));

					switch (count) {
					case 0:
						assertEquals(String.valueOf(fromUserId),
								fromUserIdResult);
						assertEquals(String.valueOf(toUserId), toUserIdResult);
						assertEquals(lastMessage, messageResult);
						assertEquals(String.valueOf(postedDate), timeResult);
						break;
					case 1:
						assertEquals(String.valueOf(fromUserId),
								fromUserIdResult);
						assertEquals(String.valueOf(toUserId), toUserIdResult);
						assertEquals(lastMessage2, messageResult);
						assertEquals(String.valueOf(postedDate2), timeResult);
						break;
					case 2:
						assertEquals(String.valueOf(fromUserId),
								fromUserIdResult);
						assertEquals(String.valueOf(toUserId), toUserIdResult);
						assertEquals(lastMessage3, messageResult);
						assertEquals(String.valueOf(postedDate3), timeResult);
						break;
					case 3:
						assertEquals(String.valueOf(fromUserId),
								fromUserIdResult);
						assertEquals(String.valueOf(toUserId), toUserIdResult);
						assertEquals(lastMessage4, messageResult);
						assertEquals(String.valueOf(postedDate4), timeResult);
						break;
					default:
						assertTrue(false);
						break;
					}

					count = count + 1;

				} while (cursor2.moveToNext());
			}
		}

		handler.removeLocalUserPreferenceData(getContext());
	}

	/**
	 * Without preset data
	 */
	public void testAddNewMessage() {
		UserLocalDataHandler handler = new UserLocalDataHandler(getContext());
		setDatabase();
		handler.removeLocalUserPreferenceData(getContext());

		int userId = 1;
		int friendId = 2;
		String userName = "aaaa";
		String friendName = "bbbb";
		int senderId = 1;
		String message = "test message";
		String date = String.valueOf(TimeUtil.getCurrentDate());

		try {
			handler.addNewMessage(userId, friendId, userName, friendName,
					senderId, message, date);
		} catch (UserLocalDataHandlerException e) {
			assertTrue(false);
		}

		ContentResolver mContentResolver = (ContentResolver) ReflectionUtil
				.getValue(UserLocalDataHandler.class, "mContentResolver",
						handler);

		String order = DatabaseDef.FriendshipColumns._ID + " ASC";

		Cursor cursor = mContentResolver.query(DatabaseDef.FriendshipTable.URI,
				null, null, null, order);

		if (cursor != null) {
			if (cursor.moveToFirst()) {
				do {
					String friendIdResult = cursor
							.getString(cursor
									.getColumnIndex(DatabaseDef.FriendshipColumns.FRIEND_ID));
					String friendNameResult = cursor
							.getString(cursor
									.getColumnIndex(DatabaseDef.FriendshipColumns.FRIEND_NAME));
					String lastMessageResult = cursor
							.getString(cursor
									.getColumnIndex(DatabaseDef.FriendshipColumns.LAST_MESSAGE));
					String lastSenderIdResult = cursor
							.getString(cursor
									.getColumnIndex(DatabaseDef.FriendshipColumns.LAST_SENDER_ID));
					String mailAddressResult = cursor
							.getString(cursor
									.getColumnIndex(DatabaseDef.FriendshipColumns.MAIL_ADDRESS));

					byte[] thumb = cursor
							.getBlob(cursor
									.getColumnIndex(DatabaseDef.FriendshipColumns.THUMBNAIL));

					DbgUtil.showDebug(TAG, "friendIdResult: " + friendIdResult);

					assertEquals(message, lastMessageResult);
					assertEquals(String.valueOf(senderId), lastSenderIdResult);
					assertEquals(String.valueOf(friendId), friendIdResult);
					assertEquals(friendName, friendNameResult);
					assertNull(mailAddressResult);
					assertNull(thumb);

				} while (cursor.moveToNext());
			}
		}

		Cursor cursor2 = mContentResolver.query(DatabaseDef.MessageTable.URI,
				null, null, null, null);

		if (cursor2 != null) {
			if (cursor2.moveToFirst()) {
				do {
					String fromUserIdResult = cursor2
							.getString(cursor2
									.getColumnIndex(DatabaseDef.MessageColumns.FROM_USER_ID));
					String toUserIdResult = cursor2
							.getString(cursor2
									.getColumnIndex(DatabaseDef.MessageColumns.TO_USER_ID));
					String messageResult = cursor2
							.getString(cursor2
									.getColumnIndex(DatabaseDef.MessageColumns.MESSAGE));
					String timeResult = cursor2.getString(cursor2
							.getColumnIndex(DatabaseDef.MessageColumns.DATE));

					assertEquals(String.valueOf(senderId), fromUserIdResult);
					assertEquals(String.valueOf(friendId), toUserIdResult);
					assertEquals(message, messageResult);
					assertEquals(date, timeResult);

				} while (cursor2.moveToNext());
			}
		}

		handler.removeLocalUserPreferenceData(getContext());
	}

	/**
	 * With preset data
	 */
	public void testAddNewMessage2() {
		UserLocalDataHandler handler = new UserLocalDataHandler(getContext());
		setDatabase();
		handler.removeLocalUserPreferenceData(getContext());

		// Input preset data
		int friendId = 2;
		String friendName = "bbbb";
		byte[] friendThumb = null;
		int lastSenderId = 2;
		String lastMessage = "test message here";
		String mailAddress = "a@a";

		ContentValues valuesForFriendship = getInsertContentValuesForFriendship(
				friendId, friendName, friendThumb, lastSenderId, lastMessage,
				mailAddress);
		long friendshipId = sDatabase.insert(
				DatabaseDef.FriendshipTable.TABLE_NAME, null,
				valuesForFriendship);

		int myUserId3 = 1;
		int friendUserId3 = 2;
		String myUserName3 = "aaaa";
		String friendUserName3 = "bbbb";
		String message3 = "test message";
		long date3 = TimeUtil.getCurrentDate();

		ContentValues valuesForMessage = getInsertContentValuesForMessage(
				myUserId3, friendUserId3, myUserName3, friendUserName3,
				message3, String.valueOf(date3));

		long idMsg = sDatabase.insert(DatabaseDef.MessageTable.TABLE_NAME,
				null, valuesForMessage);

		// ---

		int userId2 = 1;
		int friendId2 = 2;
		String userName2 = "aaaa";
		String friendName2 = "bbbb";
		int senderId2 = 1;
		String message2 = "test message";
		String date2 = String.valueOf(TimeUtil.getCurrentDate());

		try {
			handler.addNewMessage(userId2, friendId2, userName2, friendName2,
					senderId2, message2, date2);
		} catch (UserLocalDataHandlerException e) {
			assertTrue(false);
		}

		ContentResolver mContentResolver = (ContentResolver) ReflectionUtil
				.getValue(UserLocalDataHandler.class, "mContentResolver",
						handler);

		String order = DatabaseDef.FriendshipColumns._ID + " ASC";

		Cursor cursor = mContentResolver.query(DatabaseDef.FriendshipTable.URI,
				null, null, null, order);

		if (cursor != null) {
			if (cursor.moveToFirst()) {
				do {
					String friendIdResult = cursor
							.getString(cursor
									.getColumnIndex(DatabaseDef.FriendshipColumns.FRIEND_ID));
					String friendNameResult = cursor
							.getString(cursor
									.getColumnIndex(DatabaseDef.FriendshipColumns.FRIEND_NAME));
					String lastMessageResult = cursor
							.getString(cursor
									.getColumnIndex(DatabaseDef.FriendshipColumns.LAST_MESSAGE));
					String lastSenderIdResult = cursor
							.getString(cursor
									.getColumnIndex(DatabaseDef.FriendshipColumns.LAST_SENDER_ID));
					String mailAddressResult = cursor
							.getString(cursor
									.getColumnIndex(DatabaseDef.FriendshipColumns.MAIL_ADDRESS));

					byte[] thumb = cursor
							.getBlob(cursor
									.getColumnIndex(DatabaseDef.FriendshipColumns.THUMBNAIL));

					DbgUtil.showDebug(TAG, "friendIdResult: " + friendIdResult);

					assertEquals(message2, lastMessageResult);
					assertEquals(String.valueOf(senderId2), lastSenderIdResult);
					assertEquals(String.valueOf(friendId2), friendIdResult);
					assertEquals(friendName2, friendNameResult);
					assertNull(mailAddressResult);
					assertNull(thumb);

				} while (cursor.moveToNext());
			}
		}

		Cursor cursor2 = mContentResolver.query(DatabaseDef.MessageTable.URI,
				null, null, null, null);

		int count = 0;

		if (cursor2 != null) {
			if (cursor2.moveToFirst()) {
				do {
					String fromUserIdResult = cursor2
							.getString(cursor2
									.getColumnIndex(DatabaseDef.MessageColumns.FROM_USER_ID));
					String toUserIdResult = cursor2
							.getString(cursor2
									.getColumnIndex(DatabaseDef.MessageColumns.TO_USER_ID));
					String messageResult = cursor2
							.getString(cursor2
									.getColumnIndex(DatabaseDef.MessageColumns.MESSAGE));
					String timeResult = cursor2.getString(cursor2
							.getColumnIndex(DatabaseDef.MessageColumns.DATE));

					switch (count) {
					case 0:
						assertEquals(String.valueOf(myUserId3),
								fromUserIdResult);
						assertEquals(String.valueOf(friendUserId3),
								toUserIdResult);
						assertEquals(message3, messageResult);
						assertEquals(String.valueOf(date3), timeResult);
						break;
					case 1:
						assertEquals(String.valueOf(senderId2),
								fromUserIdResult);
						assertEquals(String.valueOf(friendId2), toUserIdResult);
						assertEquals(message2, messageResult);
						assertEquals(date2, timeResult);
						break;
					default:
						assertTrue(false);
						break;
					}
					count = count + 1;
				} while (cursor2.moveToNext());
			}
		}

		handler.removeLocalUserPreferenceData(getContext());
	}

	/**
	 * with no not-registered id
	 */
	public void testGetFriendUseridThumbnailNotRegistered1() {
		UserLocalDataHandler handler = new UserLocalDataHandler(getContext());
		setDatabase();
		handler.removeLocalUserPreferenceData(getContext());

		Drawable d = getContext().getResources().getDrawable(
				R.drawable.flappy_default_thumbnail_large);
		Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
		byte[] friendThumb = ImageUtil.encodeBitmapToByteArray(bitmap);
		int friendId = 2;
		String friendName = "bbbb";
		int lastSenderId = 2;
		String lastMessage = "test message here";
		String mailAddress = "a@a";

		ContentValues valuesForFriendship = getInsertContentValuesForFriendship(
				friendId, friendName, friendThumb, lastSenderId, lastMessage,
				mailAddress);
		long friendshipId = sDatabase.insert(
				DatabaseDef.FriendshipTable.TABLE_NAME, null,
				valuesForFriendship);

		ArrayList<Long> result = null;

		try {
			result = handler.getFriendUseridThumbnailNotRegistered();
		} catch (UserLocalDataHandlerException e) {
			assertTrue(false);
		}

		assertNotNull(result);
		assertEquals(result.size(), 0);

		handler.removeLocalUserPreferenceData(getContext());
	}

	/**
	 * With not registered id
	 */
	public void testGetFriendUseridThumbnailNotRegistered2() {
		UserLocalDataHandler handler = new UserLocalDataHandler(getContext());
		setDatabase();
		handler.removeLocalUserPreferenceData(getContext());

		Drawable d = getContext().getResources().getDrawable(
				R.drawable.flappy_default_thumbnail_large);
		Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
		byte[] friendThumb = ImageUtil.encodeBitmapToByteArray(bitmap);
		int friendId = 2;
		String friendName = "bbbb";
		int lastSenderId = 2;
		String lastMessage = "test message here";
		String mailAddress = "a@a";

		int friendId2 = 3;
		String friendName2 = "cccc";
		byte[] friendThumb2 = null;
		int lastSenderId2 = 3;
		String lastMessage2 = "test message here2";
		String mailAddress2 = "b@b";

		ContentValues valuesForFriendship = getInsertContentValuesForFriendship(
				friendId, friendName, friendThumb, lastSenderId, lastMessage,
				mailAddress);
		long friendshipId = sDatabase.insert(
				DatabaseDef.FriendshipTable.TABLE_NAME, null,
				valuesForFriendship);

		ContentValues valuesForFriendship2 = getInsertContentValuesForFriendship(
				friendId2, friendName2, friendThumb2, lastSenderId2,
				lastMessage2, mailAddress2);
		long friendshipId2 = sDatabase.insert(
				DatabaseDef.FriendshipTable.TABLE_NAME, null,
				valuesForFriendship2);

		ArrayList<Long> result = null;

		try {
			result = handler.getFriendUseridThumbnailNotRegistered();
		} catch (UserLocalDataHandlerException e) {
			assertTrue(false);
		}

		assertNotNull(result);
		assertEquals(result.size(), 1);

		Long notRegisteredId = result.get(0);
		assertEquals(String.valueOf(notRegisteredId), String.valueOf(friendId2));

		handler.removeLocalUserPreferenceData(getContext());
	}

	/**
	 * Without preset data
	 */
	public void testGetLatestStoredMessage1() {
		UserLocalDataHandler handler = new UserLocalDataHandler(getContext());
		setDatabase();
		handler.removeLocalUserPreferenceData(getContext());

		int friendUserId = 2;

		try {
			FriendListData data = handler.getLatestStoredMessage(friendUserId);
			assertNull(data);
		} catch (UserLocalDataHandlerException e) {
			assertTrue(false);
		}

		handler.removeLocalUserPreferenceData(getContext());
	}

	/**
	 * With preset data
	 */
	public void testGetLatestStoredMessage2() {
		UserLocalDataHandler handler = new UserLocalDataHandler(getContext());
		setDatabase();
		handler.removeLocalUserPreferenceData(getContext());

		int myUserId = 1;
		int friendUserId = 2;
		String myUserName = "aaaa";
		String friendUserName = "bbbb";
		String message = "test message";
		long date = TimeUtil.getCurrentDate();

		ContentValues valuesForMessage = getInsertContentValuesForMessage(
				myUserId, friendUserId, myUserName, friendUserName, message,
				String.valueOf(date));

		long idMsg = sDatabase.insert(DatabaseDef.MessageTable.TABLE_NAME,
				null, valuesForMessage);

		String message2 = "test message2";
		long date2 = TimeUtil.getCurrentDate() + 10000;

		ContentValues valuesForMessage2 = getInsertContentValuesForMessage(
				myUserId, friendUserId, myUserName, friendUserName, message2,
				String.valueOf(date2));

		long idMsg2 = sDatabase.insert(DatabaseDef.MessageTable.TABLE_NAME,
				null, valuesForMessage2);

		String message3 = "test message3";
		long date3 = TimeUtil.getCurrentDate() - 20000;

		ContentValues valuesForMessage3 = getInsertContentValuesForMessage(
				myUserId, friendUserId, myUserName, friendUserName, message3,
				String.valueOf(date3));

		long idMsg3 = sDatabase.insert(DatabaseDef.MessageTable.TABLE_NAME,
				null, valuesForMessage3);

		int targetFriendUserId = 2;

		try {
			FriendListData data = handler
					.getLatestStoredMessage(targetFriendUserId);
			assertNotNull(data);

			String messageResult = data.getLastMessage();
			assertEquals(message2, messageResult);
			assertEquals(date2, data.getMessagDate());

		} catch (UserLocalDataHandlerException e) {
			assertTrue(false);
		}

		handler.removeLocalUserPreferenceData(getContext());
	}

	public void testAddNewNotification() {
		UserLocalDataHandler handler = new UserLocalDataHandler(getContext());
		setDatabase();
		handler.removeLocalUserPreferenceData(getContext());

		int fromUserId = 2;
		int toUserId = 1;
		int number = 3;
		long expireDate = TimeUtil.getCurrentDate() + 10000;

		handler.addNewNotification(fromUserId, toUserId, number, expireDate);

		ContentResolver mContentResolver = (ContentResolver) ReflectionUtil
				.getValue(UserLocalDataHandler.class, "mContentResolver",
						handler);

		Cursor cursor = null;
		try {
			cursor = mContentResolver.query(DatabaseDef.NotificationTable.URI,
					null, null, null, null);
			if (cursor == null) {
				assertTrue(false);
			}
			try {
				if (cursor != null) {

					ArrayList<NotificationContentData> tmpDatas = new ArrayList<NotificationContentData>();

					if (cursor.moveToFirst()) {
						do {
							DbgUtil.showDebug(TAG, "A: " + cursor.getCount());

							int fromUserIdResult = cursor
									.getInt(cursor
											.getColumnIndex(DatabaseDef.NotificationColumns.FROM_USER_ID));
							int toUserIdResult = cursor
									.getInt(cursor
											.getColumnIndex(DatabaseDef.NotificationColumns.TO_USER_ID));
							int numberResult = cursor
									.getInt(cursor
											.getColumnIndex(DatabaseDef.NotificationColumns.NUMBER));
							long expireDateResult = cursor
									.getLong(cursor
											.getColumnIndex(DatabaseDef.NotificationColumns.EXPIRE_DATE));

							NotificationContentData data = new NotificationContentData(
									toUserId, fromUserId, number, expireDate);
							tmpDatas.add(data);
						} while (cursor.moveToNext());
					}

					assertNotNull(tmpDatas);
					assertEquals(tmpDatas.size(), 1);
					assertEquals(fromUserId, tmpDatas.get(0).getFromUserId());
					assertEquals(toUserId, tmpDatas.get(0).getToUserId());
					assertEquals(number, tmpDatas.get(0).getNumberOfMesage());

				} else {
					assertTrue(false);
				}
			} catch (Exception e) {
				assertTrue(false);
			}

		} catch (Exception e1) {
			assertTrue(false);
		}

		handler.removeLocalUserPreferenceData(getContext());
	}

	public void testGetNotificationNearestExpireData1() {
		UserLocalDataHandler handler = new UserLocalDataHandler(getContext());
		setDatabase();
		handler.removeLocalUserPreferenceData(getContext());

		int toUserId = 1;
		int fromUserId = 2;
		String userName = "aaaa";
		String friendName = "bbbb";
		String message = "test message";
		int number = 4;
		long expireDate = TimeUtil.getCurrentDate() + 10000;

		ContentValues valuesForMessage = null;

		valuesForMessage = getInsertContentValuesForNotification(fromUserId,
				toUserId, number, expireDate);

		long id = sDatabase.insert(DatabaseDef.NotificationTable.TABLE_NAME,
				null, valuesForMessage);

		try {
			NotificationContentData result = handler
					.getNotificationNearestExpireData();
			assertNotNull(result);

			assertEquals(fromUserId, result.getFromUserId());
			assertEquals(toUserId, result.getToUserId());
			assertEquals(number, result.getNumberOfMesage());
			assertEquals(expireDate, result.getExpireData());

		} catch (UserLocalDataHandlerException e) {
			assertTrue(false);
		}

		handler.removeLocalUserPreferenceData(getContext());
	}

	/**
	 * With multiple message case
	 */
	public void testGetNotificationNearestExpireData2() {
		UserLocalDataHandler handler = new UserLocalDataHandler(getContext());
		setDatabase();
		handler.removeLocalUserPreferenceData(getContext());

		int toUserId = 1;
		int fromUserId = 2;
		String message = "test message";
		int number = 1;
		long expireDate = TimeUtil.getCurrentDate() + 100000;

		ContentValues valuesForMessage = getInsertContentValuesForNotification(
				fromUserId, toUserId, number, expireDate);

		long id = sDatabase.insert(DatabaseDef.NotificationTable.TABLE_NAME,
				null, valuesForMessage);

		int toUserId2 = 1;
		int fromUserId2 = 3;
		String message2 = "test message";
		int number2 = 1;
		long expireDate2 = TimeUtil.getCurrentDate() + 50000;

		ContentValues valuesForMessage2 = getInsertContentValuesForNotification(
				fromUserId2, toUserId2, number2, expireDate2);

		long id2 = sDatabase.insert(DatabaseDef.NotificationTable.TABLE_NAME,
				null, valuesForMessage2);

		int toUserId3 = 1;
		int fromUserId3 = 4;
		String message3 = "test message";
		int number3 = 1;
		long expireDate3 = TimeUtil.getCurrentDate() + 150000;

		ContentValues valuesForMessage3 = getInsertContentValuesForNotification(
				fromUserId3, toUserId3, number3, expireDate3);

		long id3 = sDatabase.insert(DatabaseDef.NotificationTable.TABLE_NAME,
				null, valuesForMessage3);

		try {
			NotificationContentData result = handler
					.getNotificationNearestExpireData();
			assertNotNull(result);

			assertEquals(fromUserId2, result.getFromUserId());
			assertEquals(toUserId2, result.getToUserId());
			assertEquals(number2, result.getNumberOfMesage());
			assertEquals(expireDate2, result.getExpireData());

		} catch (UserLocalDataHandlerException e) {
			assertTrue(false);
		}

		handler.removeLocalUserPreferenceData(getContext());
	}

	/**
	 * With obsolete message case
	 */
	public void testGetNotificationNearestExpireData3() {
		UserLocalDataHandler handler = new UserLocalDataHandler(getContext());
		setDatabase();
		handler.removeLocalUserPreferenceData(getContext());

		int toUserId = 1;
		int fromUserId = 2;
		String message = "test message";
		int number = 1;
		long expireDate = TimeUtil.getCurrentDate() - 100000;
		DbgUtil.showDebug(TAG, "expireDate: " + expireDate);

		ContentValues valuesForMessage = getInsertContentValuesForNotification(
				fromUserId, toUserId, number, expireDate);

		long id = sDatabase.insert(DatabaseDef.NotificationTable.TABLE_NAME,
				null, valuesForMessage);
		assertNotSame(id, -1);

		int toUserId2 = 1;
		int fromUserId2 = 3;
		String message2 = "test message";
		int number2 = 1;
		long expireDate2 = TimeUtil.getCurrentDate() + 200000;
		DbgUtil.showDebug(TAG, "expireDate2: " + expireDate2);

		ContentValues valuesForMessage2 = getInsertContentValuesForNotification(
				fromUserId2, toUserId2, number2, expireDate2);

		long id2 = sDatabase.insert(DatabaseDef.NotificationTable.TABLE_NAME,
				null, valuesForMessage2);
		assertNotSame(id2, -1);

		int toUserId3 = 1;
		int fromUserId3 = 4;
		String message3 = "test message";
		int number3 = 1;
		long expireDate3 = TimeUtil.getCurrentDate() + 150000;
		DbgUtil.showDebug(TAG, "expireDate3: " + expireDate3);

		ContentValues valuesForMessage3 = getInsertContentValuesForNotification(
				fromUserId3, toUserId3, number3, expireDate3);

		long id3 = sDatabase.insert(DatabaseDef.NotificationTable.TABLE_NAME,
				null, valuesForMessage3);
		assertNotSame(id3, -1);

		try {
			NotificationContentData result = handler
					.getNotificationNearestExpireData();
			assertNotNull(result);

			assertEquals(fromUserId3, result.getFromUserId());
			assertEquals(toUserId3, result.getToUserId());
			assertEquals(number3, result.getNumberOfMesage());
			assertEquals(expireDate3, result.getExpireData());

			try {
				ContentResolver mContentResolver = (ContentResolver) ReflectionUtil
						.getValue(UserLocalDataHandler.class,
								"mContentResolver", handler);

				Cursor cursor = mContentResolver.query(
						DatabaseDef.NotificationTable.URI, null, null, null,
						null);
				if (cursor == null) {
					DbgUtil.showDebug(TAG, "cursor is null");
					assertTrue(false);
				}
				try {
					if (cursor != null) {
						if (cursor.moveToFirst()) {
							assertEquals(cursor.getCount(), 2);

							int count = 0;

							do {
								long expireDateResult = cursor
										.getLong(cursor
												.getColumnIndex(DatabaseDef.NotificationColumns.EXPIRE_DATE));
								switch (count) {
								case 0:
									DbgUtil.showDebug(TAG, "count: " + count);
									assertEquals(expireDate2, expireDateResult);
									break;
								case 1:
									assertEquals(expireDate3, expireDateResult);
									break;
								case 2:
								default:
									assertTrue(false);
									break;
								}
								count = count + 1;
							} while (cursor.moveToNext());
						}
					}
				} catch (SQLException e) {
					assertTrue(false);
				}
			} catch (SQLException e) {
				assertTrue(false);
			}

		} catch (UserLocalDataHandlerException e) {
			assertTrue(false);
		}

		handler.removeLocalUserPreferenceData(getContext());
	}

	public void testStoreFriendThumbnails() {
		UserLocalDataHandler handler = new UserLocalDataHandler(getContext());
		setDatabase();
		handler.removeLocalUserPreferenceData(getContext());

		int friendId = 2;
		String friendName = "bbbb";
		byte[] friendThumb = null;
		int lastSenderId = 2;
		String lastMessage = "test message here";
		String mailAddress = "a@a";

		ContentValues valuesForFriendship = getInsertContentValuesForFriendship(
				friendId, friendName, friendThumb, lastSenderId, lastMessage,
				mailAddress);
		long friendshipId = sDatabase.insert(
				DatabaseDef.FriendshipTable.TABLE_NAME, null,
				valuesForFriendship);

		int friendId2 = 3;
		String friendName2 = "bbbb2";
		byte[] friendThumb2 = null;
		int lastSenderId2 = 3;
		String lastMessage2 = "test message here2";
		String mailAddress2 = "a@a2";

		ContentValues valuesForFriendship2 = getInsertContentValuesForFriendship(
				friendId2, friendName2, friendThumb2, lastSenderId2,
				lastMessage2, mailAddress2);
		long friendshipId2 = sDatabase.insert(
				DatabaseDef.FriendshipTable.TABLE_NAME, null,
				valuesForFriendship2);

		int friendId3 = 4;
		String friendName3 = "bbbb3";
		byte[] friendThumb3 = null;
		int lastSenderId3 = 4;
		String lastMessage3 = "test message here3";
		String mailAddress3 = "a@a3";

		ContentValues valuesForFriendship3 = getInsertContentValuesForFriendship(
				friendId3, friendName3, friendThumb3, lastSenderId3,
				lastMessage3, mailAddress3);
		long friendshipId3 = sDatabase.insert(
				DatabaseDef.FriendshipTable.TABLE_NAME, null,
				valuesForFriendship3);

		List<HashMap<Integer, Bitmap>> thumbnails = new ArrayList<HashMap<Integer, Bitmap>>();

		Drawable d = getContext().getResources().getDrawable(
				R.drawable.flappy_default_thumbnail_large);
		Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
		HashMap<Integer, Bitmap> input = new HashMap<Integer, Bitmap>();
		input.put(2, bitmap);

		// Drawable d2 = getContext().getResources().getDrawable(
		// R.drawable.flappy_default_thumbnail_large);
		// Bitmap bitmap2 = ((BitmapDrawable) d2).getBitmap();
		Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
		HashMap<Integer, Bitmap> input2 = new HashMap<Integer, Bitmap>();
		input2.put(3, bitmap2);

		// Drawable d3 = getContext().getResources().getDrawable(
		// R.drawable.flappy_new_message_number_1);
		Bitmap bitmap3 = Bitmap.createScaledBitmap(bitmap, 120, 120, false);
		HashMap<Integer, Bitmap> input3 = new HashMap<Integer, Bitmap>();
		input3.put(4, bitmap3);

		thumbnails.add(input);
		thumbnails.add(input2);
		thumbnails.add(input3);

		handler.storeFriendThumbnails(thumbnails);

		ContentResolver mContentResolver = (ContentResolver) ReflectionUtil
				.getValue(UserLocalDataHandler.class, "mContentResolver",
						handler);

		Cursor cursor = mContentResolver.query(DatabaseDef.FriendshipTable.URI,
				null, null, null, null);

		int count = 0;

		if (cursor != null) {
			if (cursor.moveToFirst()) {
				do {
					byte[] thumbnail = cursor
							.getBlob(cursor
									.getColumnIndex(DatabaseDef.FriendshipColumns.THUMBNAIL));
					Bitmap output = ImageUtil
							.decodeByteArrayToBitmap(thumbnail);

					switch (count) {
					case 0:
						assertEquals(bitmap.getWidth(), output.getWidth());
						assertEquals(bitmap.getHeight(), output.getHeight());
						break;
					case 1:
						assertEquals(bitmap2.getWidth(), output.getWidth());
						assertEquals(bitmap2.getHeight(), output.getHeight());
						break;
					case 2:
						assertEquals(bitmap3.getWidth(), output.getWidth());
						assertEquals(bitmap3.getHeight(), output.getHeight());
						break;
					default:
						assertTrue(false);
						break;
					}
					count = count + 1;
				} while (cursor.moveToNext());
			}
		}

		handler.removeLocalUserPreferenceData(getContext());
	}

	private ContentValues getInsertContentValuesForMessage(int fromUserId,
			int toUserId, String fromUserName, String toUserName,
			String message, String date) {
		ContentValues values = new ContentValues();

		values.put(DatabaseDef.MessageColumns.FROM_USER_ID, fromUserId);
		values.put(DatabaseDef.MessageColumns.TO_USER_ID, toUserId);
		values.put(DatabaseDef.MessageColumns.FROM_USER_NAME, fromUserName);
		values.put(DatabaseDef.MessageColumns.TO_USER_NAME, toUserName);
		values.put(DatabaseDef.MessageColumns.MESSAGE, message);
		values.put(DatabaseDef.MessageColumns.DATE, date);

		return values;
	}

	protected ContentValues getInsertContentValuesForFriendship(int friendId,
			String friendName, byte[] friendThumbnail, int lastSenderId,
			String lastMessage, String mailAddress) {
		ContentValues values = new ContentValues();

		values.put(DatabaseDef.FriendshipColumns.FRIEND_ID, friendId);
		values.put(DatabaseDef.FriendshipColumns.FRIEND_NAME, friendName);
		values.put(DatabaseDef.FriendshipColumns.LAST_SENDER_ID, lastSenderId);
		values.put(DatabaseDef.FriendshipColumns.LAST_MESSAGE, lastMessage);
		values.put(DatabaseDef.FriendshipColumns.MAIL_ADDRESS, mailAddress);
		values.put(DatabaseDef.FriendshipColumns.THUMBNAIL, friendThumbnail);

		return values;
	}

	private ContentValues getInsertContentValuesForNotification(int fromUserId,
			int toUserId, int number, long expireDate) {
		ContentValues values = new ContentValues();

		values.put(DatabaseDef.NotificationColumns.FROM_USER_ID, fromUserId);
		values.put(DatabaseDef.NotificationColumns.TO_USER_ID, toUserId);
		values.put(DatabaseDef.NotificationColumns.NUMBER, number);
		values.put(DatabaseDef.NotificationColumns.EXPIRE_DATE, expireDate);

		return values;
	}

}
