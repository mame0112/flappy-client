package com.mame.flappy.db;

import android.content.Context;
//import android.database.SQLException;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;

import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteOpenHelper;

import com.mame.flappy.constant.LcomConst;
import com.mame.flappy.util.DbgUtil;

public class UserDatabaseHelper extends SQLiteOpenHelper {

	private final String TAG = LcomConst.TAG + "/UserDatabaseHelper";

	static final String FRIENDSHIP_DATA_SQL = "CREATE TABLE IF NOT EXISTS "
			+ DatabaseDef.FriendshipTable.TABLE_NAME + " ("
			+ DatabaseDef.FriendshipColumns._ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ DatabaseDef.FriendshipColumns.FRIEND_ID + " INTEGER DEFAULT 0, "
			+ DatabaseDef.FriendshipColumns.FRIEND_NAME + " TEXT, "
			+ DatabaseDef.FriendshipColumns.LAST_SENDER_ID
			+ " INTEGER DEFAULT 0, "
			+ DatabaseDef.FriendshipColumns.LAST_MESSAGE + " TEXT, "
			+ DatabaseDef.FriendshipColumns.MAIL_ADDRESS + " TEXT, "
			+ DatabaseDef.FriendshipColumns.THUMBNAIL + " BLOB, "
			+ DatabaseDef.FriendshipColumns.LAST_CONTACTED_DATE
			+ " LONG DEFAULT 0)";

	static final String MESSAGE_DATA_SQL = "CREATE TABLE IF NOT EXISTS "
			+ DatabaseDef.MessageTable.TABLE_NAME + " ("
			+ DatabaseDef.MessageColumns._ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ DatabaseDef.MessageColumns.FROM_USER_ID + " INTEGER DEFAULT 0, "
			+ DatabaseDef.MessageColumns.TO_USER_ID + " INTEGER DEFAULT 0, "
			+ DatabaseDef.MessageColumns.FROM_USER_NAME + " TEXT, "
			+ DatabaseDef.MessageColumns.TO_USER_NAME + " TEXT, "
			+ DatabaseDef.MessageColumns.MESSAGE + " TEXT, "
			+ DatabaseDef.MessageColumns.DATE + " TEXT)";

	static final String NOTIFICATION_SQL = "CREATE TABLE IF NOT EXISTS "
			+ DatabaseDef.NotificationTable.TABLE_NAME + " ("
			+ DatabaseDef.NotificationColumns._ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ DatabaseDef.NotificationColumns.FROM_USER_ID
			+ " INTEGER DEFAULT 0, "
			+ DatabaseDef.NotificationColumns.TO_USER_ID
			+ " INTEGER DEFAULT 0, " + DatabaseDef.NotificationColumns.NUMBER
			+ " INTEGER DEFAULT 0, "
			+ DatabaseDef.NotificationColumns.EXPIRE_DATE + " LONG DEFAULT 0)";

	public UserDatabaseHelper(Context context) {
		super(context, DatabaseDef.DATABASE_NAME, null,
				DatabaseDef.DATABASE_VERSION);
		DbgUtil.showDebug(TAG, "UserLocalDataHandler constructor");
		// try {
		// sqliteDatabase.execSQL(FRIENDSHIP_DATA_SQL);
		// sqliteDatabase.execSQL(MESSAGE_DATA_SQL);
		// } catch (SQLException e) {
		// DbgUtil.showDebug(TAG, "SQLException: " + e.getMessage());
		// }
		//
	}

	// @Override
	// public void onCreate(SQLiteDatabase sqliteDatabase) {
	// DbgUtil.showDebug(TAG, "onCreate");
	// try {
	// sqliteDatabase.execSQL(FRIENDSHIP_DATA_SQL);
	// sqliteDatabase.execSQL(MESSAGE_DATA_SQL);
	// } catch (SQLException e) {
	// DbgUtil.showDebug(TAG, "SQLException: " + e.getMessage());
	// }
	//
	// }

	// @Override
	// public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
	// // TODO Auto-generated method stub
	//
	// }

	@Override
	public void onCreate(net.sqlcipher.database.SQLiteDatabase sqliteDatabase) {
		DbgUtil.showDebug(TAG, "onCreate");
		try {
			sqliteDatabase.execSQL(FRIENDSHIP_DATA_SQL);
			sqliteDatabase.execSQL(MESSAGE_DATA_SQL);
			sqliteDatabase.execSQL(NOTIFICATION_SQL);
		} catch (SQLException e) {
			DbgUtil.showDebug(TAG, "SQLException: " + e.getMessage());
		}

	}

	@Override
	public void onUpgrade(net.sqlcipher.database.SQLiteDatabase arg0, int arg1,
			int arg2) {
		// TODO Auto-generated method stub

	}

}
