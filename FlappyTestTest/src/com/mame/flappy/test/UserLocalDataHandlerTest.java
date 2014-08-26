package com.mame.flappy.test;

import java.util.ArrayList;

import net.sqlcipher.database.SQLiteDatabase;
import android.content.ContentValues;
import android.content.Context;
import android.test.AndroidTestCase;

import com.mame.flappy.data.MessageItemData;
import com.mame.flappy.db.DatabaseDef;
import com.mame.flappy.db.UserDatabaseHelper;
import com.mame.flappy.db.UserLocalDataHandler;
import com.mame.flappy.test.util.ReflectionUtil;
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

}
