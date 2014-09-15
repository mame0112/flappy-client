package com.mame.flappy.tool;

import net.sqlcipher.database.SQLiteDatabase;

import com.mame.flappy.constant.LcomConst;
import com.mame.flappy.db.DatabaseDef;
import com.mame.flappy.db.UserDatabaseHelper;
import com.mame.flappy.db.UserLocalDataHandler;
import com.mame.flappy.exception.UserLocalDataHandlerException;
import com.mame.flappy.util.DbgUtil;
import com.mame.flappy.util.SecurityUtil;
import com.mame.flappy.util.TimeUtil;
import com.mame.flappy.util.TrackingUtil;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;

public class DebugLocalDataHandler extends UserLocalDataHandler {

	private final String TAG = LcomConst.TAG + "/DebugLocalDataHandler";

	private static SQLiteDatabase sDatabase;

	private Context mContext = null;

	public DebugLocalDataHandler(Context context) {
		super(context);
		mContext = context;
	}

	private synchronized void setDatabase() {
		if (sDatabase == null || !sDatabase.isOpen()) {
			UserDatabaseHelper helper = new UserDatabaseHelper(mContext);
			sDatabase.loadLibs(mContext);
			String UUID = SecurityUtil.getUniqueId(mContext);
			sDatabase = helper.getWritableDatabase(UUID);
		}
	}

	public void saveDummy1000DummyData() {
		try {
			setDatabase();
			sDatabase.beginTransaction();

			// Set data to Message DB
			ContentValues valuesForMessage = null;

			int friendId = 2;
			int userId = 1;
			String friendName = "bbbb";
			String userName = "aaaa";
			String message = "test message";

			int diff = 1000 * 60 * 60; // 1hour

			long date = TimeUtil.getCurrentDate() - diff * 500;

			for (int i = 0; i < 500; i++) {

				valuesForMessage = getInsertContentValuesForMessage(friendId,
						userId, friendName, userName, message + i,
						String.valueOf(date + diff * i));

				long id = sDatabase.insert(DatabaseDef.MessageTable.TABLE_NAME,
						null, valuesForMessage);
				DbgUtil.showDebug(TAG, "id: " + id);
				if (id < 0) {
					// Failed.
					DbgUtil.showDebug(TAG,
							"Failed to insert data into Message DB");
				}
			}

			// Set data to Friendship DB
			// Need to check if the target friend has already been in DB
			ContentValues valuesForFriendship = getInsertContentValuesForFriendship(
					friendId, friendName, null, friendId, message, null);
			long friendshipId = sDatabase.insert(
					DatabaseDef.FriendshipTable.TABLE_NAME, null,
					valuesForFriendship);
			DbgUtil.showDebug(TAG, "friendshipId: " + friendshipId);
			if (friendshipId < 0) {
				// Failed.
				DbgUtil.showDebug(TAG,
						"Failed to insert data into Friendship DB");
			}

			// Commit change
			sDatabase.setTransactionSuccessful();

		} catch (SQLException e) {
			DbgUtil.showDebug(TAG, "SQLException: " + e.getMessage());
		} finally {
			try {
				if (sDatabase != null) {
					DbgUtil.showDebug(TAG, "endTransaction");
					sDatabase.endTransaction();
				}
			} catch (SQLException e) {
				DbgUtil.showDebug(TAG,
						"SQLException: Database is null" + e.getMessage());

			}
		}
	}

	public void saveDummy1000DummyFriendshipData() {

		int friendId = 2;
		int userId = 1;
		String friendName = "bbbb";
		String userName = "aaaa";
		String message = "test message";
		long date = TimeUtil.getCurrentDate();

		setDatabase();

		for (int i = 0; i < 500; i++) {
			ContentValues valuesForFriendship = getInsertContentValuesForFriendship(
					friendId + i, friendName + i, null, friendId, message + i,
					null);
			long friendshipId = sDatabase.insert(
					DatabaseDef.FriendshipTable.TABLE_NAME, null,
					valuesForFriendship);
			DbgUtil.showDebug(TAG, "friendshipId: " + friendshipId);
			if (friendshipId < 0) {
				// Failed.
				DbgUtil.showDebug(TAG,
						"Failed to insert data into Friendship DB");
			}
		}

	}

}
