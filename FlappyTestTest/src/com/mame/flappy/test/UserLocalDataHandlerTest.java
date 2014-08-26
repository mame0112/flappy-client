package com.mame.flappy.test;

import java.util.ArrayList;

import net.sqlcipher.database.SQLiteDatabase;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.test.AndroidTestCase;

import com.mame.flappy.data.FriendListData;
import com.mame.flappy.data.MessageItemData;
import com.mame.flappy.db.DatabaseDef;
import com.mame.flappy.db.UserDatabaseHelper;
import com.mame.flappy.db.UserLocalDataHandler;
import com.mame.flappy.exception.UserLocalDataHandlerException;
import com.mame.flappy.test.util.ReflectionUtil;
import com.mame.flappy.util.ImageUtil;
import com.mame.flappy.util.SecurityUtil;
import com.mame.flappy.util.TimeUtil;

public class UserLocalDataHandlerTest extends AndroidTestCase {

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

	public void testGetLocalMessageDataset2() throws Exception {

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

		int myUserId2 = 1;
		int friendUserId2 = 2;
		String myUserName2 = "aaaa";
		String friendUserName2 = "bbbb";
		String message2 = "test message2";
		long date2 = TimeUtil.getCurrentDate();

		valuesForMessage = getInsertContentValuesForMessage(myUserId2,
				friendUserId2, myUserName2, friendUserName2, message2,
				String.valueOf(date2));

		long id2 = sDatabase.insert(DatabaseDef.MessageTable.TABLE_NAME, null,
				valuesForMessage);

		assertNotSame(id2, -1);

		ArrayList<MessageItemData> result = handler
				.getLocalMessageDataset(friendUserId);

		assertNotNull(result);
		assertEquals(result.size(), 2);

		MessageItemData data = result.get(0);

		assertEquals(data.getFromUserId(), myUserId);
		assertEquals(data.getFromUserName(), myUserName);
		assertEquals(data.getTargetUserId(), friendUserId);
		assertEquals(data.getToUserName(), friendUserName);
		assertEquals(data.getMessage(), message);
		assertEquals(data.getThumbnail(), null);
		assertEquals(data.getPostedDate(), date);

		MessageItemData data2 = result.get(1);

		assertEquals(data2.getFromUserId(), myUserId2);
		assertEquals(data2.getFromUserName(), myUserName2);
		assertEquals(data2.getTargetUserId(), friendUserId2);
		assertEquals(data2.getToUserName(), friendUserName2);
		assertEquals(data2.getMessage(), message2);
		assertEquals(data2.getThumbnail(), null);
		assertEquals(data2.getPostedDate(), date2);

		handler.removeLocalUserPreferenceData(getContext());

	}

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
			ArrayList<FriendListData> result = handler.getLocalUserDataset();
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
				R.drawable.ic_launcher);
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
			ArrayList<FriendListData> result = handler.getLocalUserDataset();
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
				R.drawable.ic_launcher);
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

}
