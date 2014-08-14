package com.mame.flappy.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sqlcipher.database.SQLiteDatabase;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
//import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import com.mame.flappy.constant.LcomConst;
import com.mame.flappy.data.FriendListData;
import com.mame.flappy.data.MessageItemData;
import com.mame.flappy.data.NotificationContentData;
import com.mame.flappy.exception.UserLocalDataHandlerException;
import com.mame.flappy.util.DbgUtil;
import com.mame.flappy.util.ImageUtil;
import com.mame.flappy.util.SecurityUtil;
import com.mame.flappy.util.TimeUtil;
import com.mame.flappy.util.TrackingUtil;

public class UserLocalDataHandler {

	private final String TAG = LcomConst.TAG + "/UserLocalDataHandler";

	private static SQLiteDatabase sDatabase;

	private Context mContext = null;

	private UserLocalDataListener mListener = null;

	private ContentResolver mContentResolver = null;

	public UserLocalDataHandler(Context context) {
		DbgUtil.showDebug(TAG, "UserLocalDataHandler");
		mContext = context;
		mContentResolver = context.getContentResolver();
	}

	private synchronized void setDatabase() {
		if (sDatabase == null || !sDatabase.isOpen()) {
			UserDatabaseHelper helper = new UserDatabaseHelper(mContext);
			sDatabase.loadLibs(mContext);
			String UUID = SecurityUtil.getUniqueId(mContext);
			sDatabase = helper.getWritableDatabase(UUID);
		}
	}

	public synchronized ArrayList<MessageItemData> getLocalMessageDataset(
			int targetUserId) {
		DbgUtil.showDebug(TAG, "getLocalMessageDataset");
		DbgUtil.showDebug(TAG, "targetUserId:  " + targetUserId);
		Cursor cursor = null;
		ArrayList<MessageItemData> datas = new ArrayList<MessageItemData>();
		try {
			// String selection = DatabaseDef.MessageColumns.TO_USER_ID + "=?";
			String selection = DatabaseDef.MessageColumns.FROM_USER_ID + "=?"
					+ " OR " + DatabaseDef.MessageColumns.TO_USER_ID + "=?";
			String selectionArgs[] = { String.valueOf(targetUserId),
					String.valueOf(targetUserId) };
			cursor = mContentResolver.query(DatabaseDef.MessageTable.URI, null,
					selection, selectionArgs, null);
			while (cursor != null && cursor.moveToNext()) {
				String fromUserId = cursor
						.getString(cursor
								.getColumnIndex(DatabaseDef.MessageColumns.FROM_USER_ID));
				String fromUserName = cursor
						.getString(cursor
								.getColumnIndex(DatabaseDef.MessageColumns.FROM_USER_NAME));
				String toUserId = cursor.getString(cursor
						.getColumnIndex(DatabaseDef.MessageColumns.TO_USER_ID));
				String toUserName = cursor
						.getString(cursor
								.getColumnIndex(DatabaseDef.MessageColumns.TO_USER_NAME));
				String message = cursor.getString(cursor
						.getColumnIndex(DatabaseDef.MessageColumns.MESSAGE));
				String date = cursor.getString(cursor
						.getColumnIndex(DatabaseDef.MessageColumns.DATE));
				DbgUtil.showDebug(TAG, "message: " + message);

				// Translate string into int
				int fromUserIdInt = LcomConst.NO_USER;
				int toUserIdInt = LcomConst.NO_USER;
				long date2 = 0L;
				if (fromUserId != null) {
					fromUserIdInt = Integer.valueOf(fromUserId);
					DbgUtil.showDebug(TAG, "fromUserId: " + fromUserId);
				}
				if (toUserId != null) {
					toUserIdInt = Integer.valueOf(toUserId);
					DbgUtil.showDebug(TAG, "toUserId: " + toUserId);
				}
				try {
					date2 = Long.valueOf(date);
				} catch (NumberFormatException e) {
					DbgUtil.showDebug(TAG,
							"NumberFormatException: " + e.getMessage());
					TrackingUtil.trackExceptionMessage(mContext, TAG,
							"NumberFormatException for getLocalMessageDataset: "
									+ e.getMessage());
				}
				// date2 = new SimpleDateFormat(LcomConst.DATE_PATTERN)
				// .parse(date);
				// } catch (ParseException e) {
				// DbgUtil.showDebug(TAG, "parseException: " + e.getMessage());
				// }

				// int fromUserId, int toUserId, String fromUserName,
				// String toUserName, String message, long postedDate

				MessageItemData data = new MessageItemData(fromUserIdInt,
						toUserIdInt, fromUserName, toUserName, message, date2,
						null);
				datas.add(data);
			}
		} catch (SQLException e) {
			DbgUtil.showDebug(TAG, "SQLException: " + e.getMessage());
			TrackingUtil.trackExceptionMessage(mContext, TAG, "SQLExeption: "
					+ e.getMessage());
		}
		return datas;
	}

	public ArrayList<FriendListData> getLocalUserDataset()
			throws UserLocalDataHandlerException {
		DbgUtil.showDebug(TAG, "getLocalUserDataset");
		Cursor cursor = null;
		ArrayList<FriendListData> datas = new ArrayList<FriendListData>();
		try {
			cursor = mContentResolver.query(DatabaseDef.FriendshipTable.URI,
					null, null, null, null);
			if (cursor == null) {
				DbgUtil.showDebug(TAG, "cursor is null");
				throw new UserLocalDataHandlerException("Cursor is null");
			}
			try {
				if (cursor != null) {
					if (cursor.moveToFirst()) {
						do {
							DbgUtil.showDebug(TAG, "A: " + cursor.getCount());
							String friendId = cursor
									.getString(cursor
											.getColumnIndex(DatabaseDef.FriendshipColumns.FRIEND_ID));
							String userName = cursor
									.getString(cursor
											.getColumnIndex(DatabaseDef.FriendshipColumns.FRIEND_NAME));
							String lastMessage = cursor
									.getString(cursor
											.getColumnIndex(DatabaseDef.FriendshipColumns.LAST_MESSAGE));
							String lastSenderId = cursor
									.getString(cursor
											.getColumnIndex(DatabaseDef.FriendshipColumns.LAST_SENDER_ID));
							String mailAddress = cursor
									.getString(cursor
											.getColumnIndex(DatabaseDef.FriendshipColumns.MAIL_ADDRESS));

							byte[] thumbnail = cursor
									.getBlob(cursor
											.getColumnIndex(DatabaseDef.FriendshipColumns.THUMBNAIL));
							Bitmap bmp = null;
							if (thumbnail != null) {
								bmp = ImageUtil
										.decodeByteArrayToBitmap(thumbnail);
								if (bmp != null) {
									DbgUtil.showDebug(TAG,
											"bmp size: " + bmp.getWidth()
													+ " / " + bmp.getHeight());
								}
							}

							FriendListData data = new FriendListData(
									Integer.valueOf(friendId), userName,
									Integer.valueOf(lastSenderId), lastMessage,
									0L, 0, mailAddress, bmp);
							datas.add(data);
							DbgUtil.showDebug(TAG, "friendId: " + friendId
									+ " userName: " + userName
									+ " lastSenderId: " + lastSenderId
									+ " lastMessage: " + lastMessage
									+ " mailAddress: " + mailAddress);
						} while (cursor.moveToNext());
					}
				}
				// int userId, String userName, String userThumb,
				// String lastSender, String lastMessage
				// while (cursor.moveToNext()) {

				// }
			} catch (SQLException e) {
				DbgUtil.showDebug(TAG, "SQLException: " + e.getMessage());
				TrackingUtil.trackExceptionMessage(mContext, TAG,
						"SQLExeption: " + e.getMessage());
				TrackingUtil.trackExceptionMessage(
						mContext,
						TAG,
						"SQLExeption for getLocalUserDataset cursor move: "
								+ e.getMessage());
				throw new UserLocalDataHandlerException("SQLException:"
						+ e.getMessage());
			}
		} catch (SQLException e) {
			DbgUtil.showDebug(TAG, "SQLException:" + e.getMessage());
			TrackingUtil.trackExceptionMessage(
					mContext,
					TAG,
					"SQLExeption for getLocalUserDataset query: "
							+ e.getMessage());
			throw new UserLocalDataHandlerException("SQLException:"
					+ e.getMessage());
		}

		return datas;
	}

	public void setUserLocalDataListener(UserLocalDataListener listener) {
		mListener = listener;
	}

	public void addNewMessageAndFriendIfNecessary(int userId, int friendId,
			String userName, String friendName, int senderId, String message,
			String date, byte[] friendThumb, String mailAddress)
			throws UserLocalDataHandlerException {
		DbgUtil.showDebug(TAG, "addNewMessageAndFriendIfNecessary");
		DbgUtil.showDebug(TAG, "userId: " + userId);
		DbgUtil.showDebug(TAG, "friendId: " + friendId);
		DbgUtil.showDebug(TAG, "friendName: " + friendName);
		DbgUtil.showDebug(TAG, "senderId: " + senderId);
		DbgUtil.showDebug(TAG, "mailAddress: " + mailAddress);
		try {
			setDatabase();
			sDatabase.beginTransaction();

			// Set data to Message DB
			ContentValues valuesForMessage = null;
			// If sender is myself, userId == userId, and friendId is receiver.
			if (userId != senderId) {
				valuesForMessage = getInsertContentValuesForMessage(friendId,
						userId, friendName, userName, message, date);
			} else {
				// If sender is friend
				valuesForMessage = getInsertContentValuesForMessage(userId,
						friendId, userName, friendName, message, date);
			}

			long id = sDatabase.insert(DatabaseDef.MessageTable.TABLE_NAME,
					null, valuesForMessage);
			DbgUtil.showDebug(TAG, "id: " + id);
			if (id < 0) {
				// Failed.
				DbgUtil.showDebug(TAG, "Failed to insert data into Message DB");
				TrackingUtil.trackExceptionMessage(mContext, TAG,
						"illegal id for addNewMessageAndFriendIfNecessary - 1");
				throw new UserLocalDataHandlerException(
						"id is less than 0. Failed to insert data");
			}

			// Set data to Friendship DB
			// Need to check if the target friend has already been in DB
			ContentValues valuesForFriendship = getInsertContentValuesForFriendship(
					friendId, friendName, friendThumb, senderId, message,
					mailAddress);
			long friendshipId = sDatabase.insert(
					DatabaseDef.FriendshipTable.TABLE_NAME, null,
					valuesForFriendship);
			DbgUtil.showDebug(TAG, "friendshipId: " + friendshipId);
			if (id < 0) {
				// Failed.
				DbgUtil.showDebug(TAG,
						"Failed to insert data into Friendship DB");
				TrackingUtil.trackExceptionMessage(mContext, TAG,
						"illegal id for addNewMessageAndFriendIfNecessary - 2");
				throw new UserLocalDataHandlerException(
						"id is less than 0. Failed to insert data");
			}

			// Commit change
			sDatabase.setTransactionSuccessful();

		} catch (SQLException e) {
			DbgUtil.showDebug(TAG, "SQLException: " + e.getMessage());
			TrackingUtil.trackExceptionMessage(mContext, TAG,
					"SQLExeption for  for addNewMessageAndFriendIfNecessary insert: "
							+ e.getMessage());
			throw new UserLocalDataHandlerException("SQLException: "
					+ e.getMessage());
		} finally {
			try {
				if (sDatabase != null) {
					DbgUtil.showDebug(TAG, "endTransaction");
					sDatabase.endTransaction();
				}
			} catch (SQLException e) {
				DbgUtil.showDebug(TAG,
						"SQLException: Database is null" + e.getMessage());
				TrackingUtil.trackExceptionMessage(mContext, TAG,
						"SQLExeption for  for addNewMessageAndFriendIfNecessary endTransition: "
								+ e.getMessage());
				throw new UserLocalDataHandlerException("SQLException: "
						+ e.getMessage());
			}
		}
	}

	public void addMultipleNewMessagesAndFriendIfNecessary(long userId,
			ArrayList<MessageItemData> newMessages)
			throws UserLocalDataHandlerException {
		DbgUtil.showDebug(TAG, "addMultipleNewMessagesAndFriendIfNecessary");
		try {
			if (newMessages != null && newMessages.size() != 0) {

				setDatabase();

				// Handle latest message for friendship table
				MessageItemData data = newMessages.get(newMessages.size() - 1);
				int senderId = data.getFromUserId();
				int toUserId = data.getTargetUserId();
				String senderName = data.getFromUserName();
				String toUserName = data.getToUserName();
				String messageData = data.getMessage();
				String date = String.valueOf(data.getPostedDate());

				// Check if target friend is already registered as friend

				Cursor checkCursor = null;

				// If sender is mine
				if (senderId == userId) {
					String selection = DatabaseDef.FriendshipColumns.FRIEND_ID
							+ "=?";
					String selectionArgs[] = { String.valueOf(toUserId) };
					checkCursor = mContentResolver.query(
							DatabaseDef.FriendshipTable.URI, null, selection,
							selectionArgs, null);
				} else {
					// If sender is friend
					String selection = DatabaseDef.FriendshipColumns.FRIEND_ID
							+ "=?";

					String selectionArgs[] = { String.valueOf(senderId) };
					checkCursor = mContentResolver.query(
							DatabaseDef.FriendshipTable.URI, null, selection,
							selectionArgs, null);
				}

				// If friend info has not been registered yet.
				if (checkCursor == null || checkCursor.getCount() == 0) {

					// If myself is receiver (meaning sender id friend)
					ContentValues valuesForFriendship = null;
					if (userId != senderId) {
						valuesForFriendship = getInsertContentValuesForFriendship(
								senderId, senderName, null, senderId,
								messageData, null);
					} else {
						// If myself is sender (but this case should be
						// eliminated
						// in server side)
						DbgUtil.showDebug(TAG, "something wrong");
						valuesForFriendship = getInsertContentValuesForFriendship(
								toUserId, toUserName, null, senderId,
								messageData, null);
					}

					long friendshipId = sDatabase.insert(
							DatabaseDef.FriendshipTable.TABLE_NAME, null,
							valuesForFriendship);
					DbgUtil.showDebug(TAG, "friendshipId: " + friendshipId);
					if (friendshipId < 0) {
						// Failed.
						DbgUtil.showDebug(TAG,
								"Failed to insert data into Friendship DB");
						TrackingUtil
								.trackExceptionMessage(mContext, TAG,
										"illegal id for addNewMessageAndFriendIfNecessary - 2");
						throw new UserLocalDataHandlerException(
								"id is less than 0. Failed to insert data");
					}
				} else {
					DbgUtil.showDebug(TAG,
							"Cursor is not null or size is not 0. Friend is already registered");

					checkCursor.moveToFirst();

					String friendId = checkCursor
							.getString(checkCursor
									.getColumnIndex(DatabaseDef.FriendshipColumns.FRIEND_ID));
					String friendName = checkCursor
							.getString(checkCursor
									.getColumnIndex(DatabaseDef.FriendshipColumns.FRIEND_NAME));
					// String lastMessage = checkCursor
					// .getString(checkCursor
					// .getColumnIndex(DatabaseDef.FriendshipColumns.LAST_MESSAGE));
					// String lastSenderId = checkCursor
					// .getString(checkCursor
					// .getColumnIndex(DatabaseDef.FriendshipColumns.LAST_SENDER_ID));
					String mailAddress = checkCursor
							.getString(checkCursor
									.getColumnIndex(DatabaseDef.FriendshipColumns.MAIL_ADDRESS));
					byte[] thumbnail = checkCursor
							.getBlob(checkCursor
									.getColumnIndex(DatabaseDef.FriendshipColumns.THUMBNAIL));

					// Then, need to update latest message info in Friendship
					// table

					ContentValues valuesForFriendship = null;
					String where = null;
					if (senderId == userId) {
						valuesForFriendship = getInsertContentValuesForFriendship(
								Integer.valueOf(friendId), friendName,
								thumbnail, senderId, messageData, mailAddress);
						where = DatabaseDef.FriendshipColumns.FRIEND_ID + "="
								+ toUserId;
					} else {
						valuesForFriendship = getInsertContentValuesForFriendship(
								Integer.valueOf(friendId), friendName,
								thumbnail, senderId, messageData, mailAddress);
						where = DatabaseDef.FriendshipColumns.FRIEND_ID + "="
								+ senderId;
					}

					long id = sDatabase.update(
							DatabaseDef.FriendshipTable.TABLE_NAME,
							valuesForFriendship, where, null);
					if (id < 0) {
						// Failed.
						DbgUtil.showDebug(TAG,
								"Failed to update latest message data on Friendship DB");
						TrackingUtil
								.trackExceptionMessage(mContext, TAG,
										"illegal id for addMultipleNewMessagesAndFriendIfNecessary - 1");
						throw new UserLocalDataHandlerException(
								"id is less than 0. Failed to insert data");
					}
				}

				// Set data to Message DB
				for (MessageItemData message : newMessages) {
					int senderIdForMsg = message.getFromUserId();
					int toUserIdForMsg = message.getTargetUserId();
					String senderNameForMsg = message.getFromUserName();
					String toUserNameForMsg = message.getToUserName();
					String messageDataForMsg = message.getMessage();
					String dateForMsg = String.valueOf(message.getPostedDate());

					ContentValues valuesForMessage = null;
					// this case should be removed in server side...
					DbgUtil.showDebug(TAG, "Something wrong...");
					valuesForMessage = getInsertContentValuesForMessage(
							senderIdForMsg, toUserIdForMsg, senderNameForMsg,
							toUserNameForMsg, messageDataForMsg, dateForMsg);

					long id = sDatabase.insert(
							DatabaseDef.MessageTable.TABLE_NAME, null,
							valuesForMessage);
					DbgUtil.showDebug(TAG, "id: " + id);
					if (id < 0) {
						// Failed.
						DbgUtil.showDebug(TAG,
								"Failed to insert data into Message DB");
						TrackingUtil
								.trackExceptionMessage(mContext, TAG,
										"illegal id for addNewMessageAndFriendIfNecessary - 1");
						throw new UserLocalDataHandlerException(
								"id is less than 0. Failed to insert data");
					}
				}

			}
		} catch (IndexOutOfBoundsException e) {
			DbgUtil.showDebug(TAG,
					"IndexOutOfBoundsException: " + e.getMessage());
		} catch (SQLException e) {
			DbgUtil.showDebug(TAG, "SQLException: " + e.getMessage());
			TrackingUtil.trackExceptionMessage(mContext, TAG,
					"SQLExeption for  for addNewMessageAndFriendIfNecessary insert: "
							+ e.getMessage());
			throw new UserLocalDataHandlerException("SQLException: "
					+ e.getMessage());
		}
	}

	public void addNewMessage(MessageItemData messageData) {
		DbgUtil.showDebug(TAG, "addNewMessage (with MessageData class)");
		if (messageData != null) {
			int userId = messageData.getFromUserId();
			int friendId = messageData.getTargetUserId();
			String userName = messageData.getFromUserName();
			String friendName = messageData.getToUserName();
			int senderId = userId;
			String message = messageData.getMessage();
			long date = messageData.getPostedDate();
			String date2 = null;
			// try {
			date2 = String.valueOf(date);
			// date2 = TimeUtil.parseDateInDateToString(date);
			// } catch (ParseException e) {
			// DbgUtil.showDebug(TAG, "ParseException: " + e.getMessage());
			// }
			addNewMessage(userId, friendId, userName, friendName, senderId,
					message, date2);
		} else {
			// If no data is passed, nothing to do.
		}

	}

	/**
	 * This method is for storing message to local DB without adding new user.
	 * 
	 * @param userId
	 * @param friendId
	 * @param userName
	 * @param friendName
	 * @param senderId
	 * @param message
	 * @param date
	 */
	public void addNewMessage(int userId, int friendId, String userName,
			String friendName, int senderId, String message, String date) {
		DbgUtil.showDebug(TAG, "addNewMessage");
		DbgUtil.showDebug(TAG, "userId: " + userId);
		DbgUtil.showDebug(TAG, "friendId: " + friendId);
		DbgUtil.showDebug(TAG, "friendName: " + friendName);
		DbgUtil.showDebug(TAG, "senderIde: " + senderId);
		try {
			setDatabase();
			// sDatabase.beginTransaction();

			// Set data to Message DB
			ContentValues valuesForMessage = null;
			// If sender is myself, userId == userId, and friendId is receiver.
			if (userId == senderId) {
				valuesForMessage = getInsertContentValuesForMessage(userId,
						friendId, userName, friendName, message, date);
			} else {
				// If sender is friend
				valuesForMessage = getInsertContentValuesForMessage(friendId,
						userId, friendName, userName, message, date);
			}

			long id = sDatabase.insert(DatabaseDef.MessageTable.TABLE_NAME,
					null, valuesForMessage);
			DbgUtil.showDebug(TAG, "id: " + id);
			if (id < 0) {
				// Failed.
				DbgUtil.showDebug(TAG, "Failed to insert data into Message DB");
				TrackingUtil.trackExceptionMessage(mContext, TAG,
						"illegal id for addNewMessage");
			}

			// Commit change
			// sDatabase.setTransactionSuccessful();

		} catch (SQLException e) {
			DbgUtil.showDebug(TAG, "SQLException: " + e.getMessage());
			TrackingUtil.trackExceptionMessage(mContext, TAG,
					"SQLExeption for addNewMessage insert: " + e.getMessage());
		} finally {
			// try {
			// if (sDatabase != null) {
			// DbgUtil.showDebug(TAG, "endTransaction");
			// sDatabase.endTransaction();
			// }
			// } catch (SQLException e) {
			// DbgUtil.showDebug(TAG,
			// "SQLException: Database is null" + e.getMessage());
			// TrackingUtil.trackExceptionMessage(
			// mContext,
			// TAG,
			// "SQLExeption for addNewMessage endTransition: "
			// + e.getMessage());
			// }
		}
	}

	public ArrayList<Integer> getFriendUseridThumbnailNotRegistered(
			ArrayList<Integer> targetIds) throws UserLocalDataHandlerException {
		DbgUtil.showDebug(TAG, "getTargetUseridThumbnailNotRegistered");

		ArrayList<Integer> result = new ArrayList<Integer>();

		if (targetIds != null && targetIds.size() != 0) {
			for (int id : targetIds) {
				DbgUtil.showDebug(TAG, "id: " + id);

				boolean isRegistered = false;

				Cursor cursor = null;
				try {
					String projection[] = {
							DatabaseDef.FriendshipColumns.FRIEND_ID,
							DatabaseDef.FriendshipColumns.THUMBNAIL };
					String selection = DatabaseDef.FriendshipColumns.FRIEND_ID
							+ "=?";
					String selectionArgs[] = { String.valueOf(id) };

					cursor = mContentResolver.query(
							DatabaseDef.FriendshipTable.URI, projection,
							selection, selectionArgs, null);
					if (cursor == null) {
						DbgUtil.showDebug(TAG, "cursor is null");
						throw new UserLocalDataHandlerException(
								"Cursor is null");
					}
					try {
						if (cursor != null) {
							if (cursor.moveToFirst()) {
								do {
									DbgUtil.showDebug(TAG,
											"A: " + cursor.getCount());

									byte[] thumbnail = cursor
											.getBlob(cursor
													.getColumnIndex(DatabaseDef.FriendshipColumns.THUMBNAIL));
									Bitmap bmp = null;
									if (thumbnail != null) {
										bmp = ImageUtil
												.decodeByteArrayToBitmap(thumbnail);

										if (bmp != null && bmp.getWidth() != 0
												&& bmp.getHeight() != 0) {
											result.add(id);
											DbgUtil.showDebug(TAG, "Add");
											isRegistered = true;
										}
									}
								} while (cursor.moveToNext());
							}
						}
						// }
					} catch (SQLException e) {
						DbgUtil.showDebug(TAG,
								"SQLException: " + e.getMessage());
						TrackingUtil.trackExceptionMessage(mContext, TAG,
								"SQLExeption: " + e.getMessage());
						TrackingUtil.trackExceptionMessage(mContext, TAG,
								"SQLExeption for getLocalUserDataset cursor move: "
										+ e.getMessage());
						throw new UserLocalDataHandlerException("SQLException:"
								+ e.getMessage());
					}
				} catch (SQLException e) {
					DbgUtil.showDebug(TAG, "SQLException:" + e.getMessage());
					TrackingUtil.trackExceptionMessage(
							mContext,
							TAG,
							"SQLExeption for getLocalUserDataset query: "
									+ e.getMessage());
					throw new UserLocalDataHandlerException("SQLException:"
							+ e.getMessage());
				}

				if (!isRegistered) {
					result.add(id);
					isRegistered = false;
				}

			}
			return result;
		}
		return null;
	}

	public synchronized FriendListData getLatestStoredMessage(int friendUserId)
			throws UserLocalDataHandlerException {
		DbgUtil.showDebug(TAG, "getLatestStoredMessage");
		if (friendUserId != LcomConst.NO_USER) {
			Cursor cursor = null;
			try {
				String projection[] = {
						DatabaseDef.MessageColumns.FROM_USER_ID,
						DatabaseDef.MessageColumns.TO_USER_ID,
						DatabaseDef.MessageColumns.MESSAGE,
						DatabaseDef.MessageColumns.DATE };
				String selection = DatabaseDef.MessageColumns.FROM_USER_ID
						+ "=?" + " OR " + DatabaseDef.MessageColumns.TO_USER_ID
						+ "=?";
				String selectionArgs[] = { String.valueOf(friendUserId),
						String.valueOf(friendUserId) };
				String sortOrder = DatabaseDef.MessageColumns.DATE + " LIMIT 1";
				cursor = mContentResolver.query(DatabaseDef.MessageTable.URI,
						projection, selection, selectionArgs, sortOrder);
				if (cursor == null) {
					DbgUtil.showDebug(TAG, "cursor is null");
					throw new UserLocalDataHandlerException("Cursor is null");
				}
				try {
					if (cursor != null) {
						long latestMessageTime = 0L;
						String latestMessage = null;
						int lastFromId = LcomConst.NO_USER;
						int lastToId = LcomConst.NO_USER;
						if (cursor.moveToFirst()) {
							do {
								String fromUserId = cursor
										.getString(cursor
												.getColumnIndex(DatabaseDef.MessageColumns.FROM_USER_ID));
								String toUserId = cursor
										.getString(cursor
												.getColumnIndex(DatabaseDef.MessageColumns.TO_USER_ID));
								String message = cursor
										.getString(cursor
												.getColumnIndex(DatabaseDef.MessageColumns.MESSAGE));
								String time = cursor
										.getString(cursor
												.getColumnIndex(DatabaseDef.MessageColumns.DATE));
								if (message != null) {
									DbgUtil.showDebug(TAG, "message: "
											+ message);
									DbgUtil.showDebug(TAG, "time: " + time);
									long tmpTime = Long.valueOf(time);
									if (latestMessageTime < tmpTime) {
										latestMessageTime = tmpTime;
										latestMessage = message;
										lastFromId = Integer
												.valueOf(fromUserId);
										lastToId = Integer.valueOf(toUserId);
									}
								} else {
									DbgUtil.showDebug(TAG, "message is null");
								}

							} while (cursor.moveToNext());
							// int friendId, String friendName, int
							// lastSenderId,
							// String lastMessage, long lastMsgDate,
							// int numOfNewMessage,
							// String mailAddress, Bitmap thumbnail

							DbgUtil.showDebug(TAG, "latestMessage: "
									+ latestMessage);

							FriendListData data = null;
							// If last sender is friend
							if (friendUserId == lastFromId) {
								data = new FriendListData(lastFromId, null,
										lastFromId, latestMessage,
										latestMessageTime, 0, null, null);
							} else {
								// If last sender is myself
								data = new FriendListData(lastFromId, null,
										lastFromId, latestMessage,
										latestMessageTime, 0, null, null);
							}
							return data;
						}
					}
					// }
				} catch (SQLException e) {
					DbgUtil.showDebug(TAG, "SQLException: " + e.getMessage());
					TrackingUtil.trackExceptionMessage(mContext, TAG,
							"SQLExeption: " + e.getMessage());
					TrackingUtil.trackExceptionMessage(mContext, TAG,
							"SQLExeption for getLatestStoredMessage cursor move: "
									+ e.getMessage());
					throw new UserLocalDataHandlerException("SQLException:"
							+ e.getMessage());
				}
			} catch (SQLException e) {
				DbgUtil.showDebug(TAG, "SQLException:" + e.getMessage());
				TrackingUtil.trackExceptionMessage(
						mContext,
						TAG,
						"SQLExeption for getLatestStoredMessage query: "
								+ e.getMessage());
				throw new UserLocalDataHandlerException("SQLException:"
						+ e.getMessage());
			}
		}
		return null;
	}

	public synchronized void addNewNotification(int fromUserId, int toUserId,
			int number, long expireDate) {
		DbgUtil.showDebug(TAG, "addNewNotification");
		try {
			setDatabase();
			// sDatabase.beginTransaction();

			// Set data to Message DB
			ContentValues valuesForMessage = null;

			valuesForMessage = getInsertContentValuesForNotification(
					fromUserId, toUserId, number, expireDate);

			long id = sDatabase.insert(
					DatabaseDef.NotificationTable.TABLE_NAME, null,
					valuesForMessage);
			DbgUtil.showDebug(TAG, "id: " + id);
			if (id < 0) {
				// Failed.
				DbgUtil.showDebug(TAG,
						"Failed to insert data into notification DB");
				TrackingUtil.trackExceptionMessage(mContext, TAG,
						"illegal id for addNewMessage");
			}

		} catch (SQLException e) {
			DbgUtil.showDebug(TAG, "SQLException: " + e.getMessage());
			TrackingUtil.trackExceptionMessage(
					mContext,
					TAG,
					"SQLExeption for addNewNotification insert: "
							+ e.getMessage());
		} finally {

		}
	}

	public NotificationContentData getNotificationNearestExpireData()
			throws UserLocalDataHandlerException {
		DbgUtil.showDebug(TAG, "getNotificationNearestExpireData");
		Cursor cursor = null;
		try {
			cursor = mContentResolver.query(DatabaseDef.NotificationTable.URI,
					null, null, null, null);
			if (cursor == null) {
				DbgUtil.showDebug(TAG, "cursor is null");
				throw new UserLocalDataHandlerException("Cursor is null");
			}
			try {
				if (cursor != null) {

					ArrayList<NotificationContentData> tmpDatas = new ArrayList<NotificationContentData>();

					if (cursor.moveToFirst()) {
						do {
							DbgUtil.showDebug(TAG, "A: " + cursor.getCount());

							int fromUserId = cursor
									.getInt(cursor
											.getColumnIndex(DatabaseDef.NotificationColumns.FROM_USER_ID));
							int toUserId = cursor
									.getInt(cursor
											.getColumnIndex(DatabaseDef.NotificationColumns.TO_USER_ID));
							int number = cursor
									.getInt(cursor
											.getColumnIndex(DatabaseDef.NotificationColumns.NUMBER));
							long expireDate = cursor
									.getLong(cursor
											.getColumnIndex(DatabaseDef.NotificationColumns.EXPIRE_DATE));
							DbgUtil.showDebug(TAG, "expireDate:: " + expireDate);

							NotificationContentData data = new NotificationContentData(
									toUserId, fromUserId, number, expireDate);
							tmpDatas.add(data);
						} while (cursor.moveToNext());
					}

					// First, sort notificatons based on time
					UserLocalDataHandlerHelper helper = new UserLocalDataHandlerHelper();
					helper.sortNotificationBasedOnExpireTime(tmpDatas);

					// Try to return most nearlest but i has not expired
					// notification data
					long current = TimeUtil.getCurrentDate();

					for (NotificationContentData data : tmpDatas) {
						if (data != null) {
							long expire = data.getExpireData();
							if (current < expire) {
								DbgUtil.showDebug(TAG, "valid notification: "
										+ expire);
								return data;
							} else {
								// If expire time is less than current time, we
								// remove it from Database
								removeObsoleteNotification(expire);
							}
						}
					}
				}

			} catch (SQLException e) {
				DbgUtil.showDebug(TAG, "SQLException: " + e.getMessage());
				TrackingUtil.trackExceptionMessage(mContext, TAG,
						"SQLExeption: " + e.getMessage());
				TrackingUtil.trackExceptionMessage(mContext, TAG,
						"SQLExeption for getNotificationNearestExpireData cursor move: "
								+ e.getMessage());
				throw new UserLocalDataHandlerException("SQLException:"
						+ e.getMessage());
			}
		} catch (SQLException e) {
			DbgUtil.showDebug(TAG, "SQLException:" + e.getMessage());
			TrackingUtil.trackExceptionMessage(mContext, TAG,
					"SQLExeption for getNotificationNearestExpireData query: "
							+ e.getMessage());
			throw new UserLocalDataHandlerException("SQLException:"
					+ e.getMessage());
		}
		return null;
	}

	private void removeObsoleteNotification(long expireDate)
			throws UserLocalDataHandlerException {
		DbgUtil.showDebug(TAG, "removeObsoleteNotification: " + expireDate);

		String where = DatabaseDef.NotificationColumns.EXPIRE_DATE + "="
				+ expireDate;

		int id = mContentResolver.delete(DatabaseDef.NotificationTable.URI,
				where, null);
		DbgUtil.showDebug(TAG, "id: " + id);
		if (id < 0) {
			DbgUtil.showDebug(TAG, "delete id is less than 0");
			throw new UserLocalDataHandlerException("delete id is less than 0");
		}
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

	private ContentValues getInsertContentValuesForFriendship(int friendId,
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

	private ContentValues getInsertContentValuesForThubmnail(
			Bitmap thumbnailData) {
		DbgUtil.showDebug(TAG, "getInsertContentValuesForThubmnail");
		ContentValues values = new ContentValues();

		if (thumbnailData != null) {
			DbgUtil.showDebug(TAG,
					"thumbnailData is not null: " + thumbnailData.getWidth()
							+ " / " + thumbnailData.getHeight());
			byte[] bytes = ImageUtil.encodeBitmapToByteArray(thumbnailData);
			DbgUtil.showDebug(TAG, "bytes size: " + bytes.length);
			values.put(DatabaseDef.FriendshipColumns.THUMBNAIL, bytes);
		}

		return values;
	}

	public void removeLocalUserPreferenceData(Context context) {
		DbgUtil.showDebug(TAG, "removeLocalUserPreferenceData");
		setDatabase();
		sDatabase.delete(DatabaseDef.FriendshipTable.TABLE_NAME, null, null);
		sDatabase.delete(DatabaseDef.MessageTable.TABLE_NAME, null, null);
		sDatabase.delete(DatabaseDef.NotificationTable.TABLE_NAME, null, null);
		doVacuum();

		// File path = context.getDatabasePath(LcomConst.DATABASE_NAME);
		// DbgUtil.showDebug(TAG, "path: " + path);
		// File file = new File(path + "/databases/friendship.db");
		// file.delete();

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

	private void doVacuum() {
		setDatabase();
		sDatabase.execSQL("vacuum");
	}

	public void storeFriendThumbnails(List<HashMap<Integer, Bitmap>> thumbnails) {
		DbgUtil.showDebug(TAG, "storeFriendThumbnails");
		try {
			setDatabase();

			for (HashMap<Integer, Bitmap> thumbnail : thumbnails) {
				for (Iterator<?> it = thumbnail.entrySet().iterator(); it
						.hasNext();) {
					Map.Entry entry = (Map.Entry) it.next();
					Integer friendId = (Integer) entry.getKey();
					Bitmap friendThumb = (Bitmap) entry.getValue();
					// ArrayList<FriendListData> mFriendListData
					ContentValues valuesForThumbnail = getInsertContentValuesForThubmnail(friendThumb);
					String where = DatabaseDef.FriendshipColumns.FRIEND_ID
							+ "=" + friendId;
					DbgUtil.showDebug(TAG, "where: " + where);
					long id = sDatabase.update(
							DatabaseDef.FriendshipTable.TABLE_NAME,
							valuesForThumbnail, where, null);
					DbgUtil.showDebug(TAG, "id: " + id);
					if (id < 0) {
						// Failed.
						DbgUtil.showDebug(TAG,
								"Failed to insert data into Message DB");
						TrackingUtil.trackExceptionMessage(mContext, TAG,
								"illegal id for storeFriendThumbnails");
					}
				}
			}
		} catch (SQLException e) {
			DbgUtil.showDebug(TAG, "SQLException: " + e.getMessage());
			TrackingUtil.trackExceptionMessage(mContext, TAG,
					"SQLExeption for storeFriendThumbnails: " + e.getMessage());
		}
	}

	// Interface to notify new user data to client of this class
	public interface UserLocalDataListener {
		public void notifyLocalUserDataSet(ArrayList<FriendListData> userData);
	}

}
