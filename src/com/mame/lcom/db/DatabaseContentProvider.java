package com.mame.lcom.db;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.util.DbgUtil;
import com.mame.lcom.util.SecurityUtil;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class DatabaseContentProvider extends ContentProvider {

	private final String TAG = LcomConst.TAG + "/DatabaseContentProvider";

	private static UriMatcher sUriMatcher;

	private SQLiteDatabase mDatabase;

	@Override
	public boolean onCreate() {
		if (mDatabase == null) {
			SQLiteOpenHelper helper = new UserDatabaseHelper(getContext());
			SQLiteDatabase.loadLibs(getContext());
			String UUID = SecurityUtil.getUniqueId(getContext());
			mDatabase = helper.getWritableDatabase(UUID);
		}

		return true;
	}

	public synchronized SQLiteDatabase getWritableDatabase() {
		return mDatabase;
	}

	// @Override
	// public ContentProviderResult[] applyBatch(
	// ArrayList<ContentProviderOperation> operations)
	// throws OperationApplicationException {
	// SQLiteDatabase db = getWritableDatabase();
	// db.beginTransaction();
	// ContentProviderResult[] results;
	// try {
	// results = super.applyBatch(operations);
	// db.setTransactionSuccessful();
	// } finally {
	// db.endTransaction();
	// }
	// return results;
	// }

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		DbgUtil.showDebug(TAG, "delete");
		int match = sUriMatcher.match(uri);
		if (match == -1) {
			throw new IllegalArgumentException("unknown URI: " + uri);
		}

		String table = tableFromMatch(match);
		if (table != null) {
			int id = 0;
			SQLiteDatabase database = getWritableDatabase();
			id = database.delete(table, selection, selectionArgs);
			if (id > 0) {
				Uri resultUri = ContentUris.withAppendedId(uri, id);
				getContext().getContentResolver().notifyChange(resultUri, null);
				return id;
			}
		}
		return 0;
	}

	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		DbgUtil.showDebug(TAG, "insert");
		int match = sUriMatcher.match(uri);
		if (match == -1) {
			throw new IllegalArgumentException("unknown URI: " + uri);
		}

		String table = tableFromMatch(match);
		if (table != null) {
			long id = -1;
			SQLiteDatabase database = getWritableDatabase();
			// Don't start a transaction if we already are in one
			// boolean useTransaction = !database.inTransaction();
			// if (useTransaction) {
			// database.beginTransaction();
			// }
			id = database.insert(table, null, values);
			if (id > 0) {
				Uri resultUri = ContentUris.withAppendedId(uri, id);
				getContext().getContentResolver().notifyChange(resultUri, null);
			}
		}
		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		DbgUtil.showDebug(TAG, "query");
		int match = sUriMatcher.match(uri);
		String table = tableFromMatch(match);
		if (table != null) {
			SQLiteDatabase database = getWritableDatabase();
			Cursor cursor = database.query(table, projection, selection,
					selectionArgs, null, null, sortOrder);
			return cursor;

		}
		throw new SQLException("Invalid uri for this content provider. "
				+ uri.toString());
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return 0;
	}

	private String tableFromMatch(int match) {
		String table = null;
		switch (match) {
		case DatabaseDef.Constants.FRIENDSHIP_MATCH:
			table = DatabaseDef.FriendshipTable.TABLE_NAME;
			break;
		case DatabaseDef.Constants.MESSAGE_MATCH:
			table = DatabaseDef.MessageTable.TABLE_NAME;
			break;
		case DatabaseDef.Constants.NOTIFICATION_MATCH:
			table = DatabaseDef.NotificationTable.TABLE_NAME;
			break;
		}
		return table;

	}

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(DatabaseDef.AUTHORITY,
				DatabaseDef.FriendshipTable.FRIENDSHIP_PATH,
				DatabaseDef.Constants.FRIENDSHIP_MATCH);
		sUriMatcher.addURI(DatabaseDef.AUTHORITY,
				DatabaseDef.MessageTable.MESSAGE_PATH,
				DatabaseDef.Constants.MESSAGE_MATCH);
		sUriMatcher.addURI(DatabaseDef.AUTHORITY,
				DatabaseDef.NotificationTable.NOTIFICATION_PATH,
				DatabaseDef.Constants.NOTIFICATION_MATCH);
	}
}
