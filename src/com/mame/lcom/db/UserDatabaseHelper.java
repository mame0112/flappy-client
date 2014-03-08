package com.mame.lcom.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.util.DbgUtil;

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
			+ DatabaseDef.FriendshipColumns.THUMBNAIL + " BLOB)";

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

	public UserDatabaseHelper(Context context) {
		super(context, DatabaseDef.DATABASE_NAME, null,
				DatabaseDef.DATABASE_VERSION);
		DbgUtil.showDebug(TAG, "UserLocalDataHandler constructor");
	}

	@Override
	public void onCreate(SQLiteDatabase sqliteDatabase) {
		DbgUtil.showDebug(TAG, "onCreate");
		try {
			sqliteDatabase.execSQL(FRIENDSHIP_DATA_SQL);
			sqliteDatabase.execSQL(MESSAGE_DATA_SQL);
		} catch (SQLException e) {
			DbgUtil.showDebug(TAG, "SQLException: " + e.getMessage());
		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}
