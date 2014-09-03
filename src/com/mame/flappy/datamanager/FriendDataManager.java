package com.mame.flappy.datamanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;

import com.mame.flappy.constant.LcomConst;
import com.mame.flappy.data.FriendListData;
import com.mame.flappy.data.FriendListUpdateData;
import com.mame.flappy.data.MessageItemData;
import com.mame.flappy.data.NotificationContentData;
import com.mame.flappy.db.UserLocalDataHandler;
import com.mame.flappy.db.UserLocalDataHandler.UserLocalDataListener;
import com.mame.flappy.exception.FriendDataManagerException;
import com.mame.flappy.exception.UserLocalDataHandlerException;
import com.mame.flappy.server.UserServerDataHandler;
import com.mame.flappy.server.UserServerDataHandler.UserServerDataListener;
import com.mame.flappy.util.DbgUtil;
import com.mame.flappy.util.PreferenceUtil;
import com.mame.flappy.util.TimeUtil;
import com.mame.flappy.util.TrackingUtil;

public class FriendDataManager implements UserServerDataListener,
		UserLocalDataListener {

	private final static String TAG = LcomConst.TAG + "/FriendDataManager";

	private static FriendDataManager sDataManager = new FriendDataManager();

	private static UserServerDataHandler mServerDataHandler = null;

	private static UserLocalDataHandler mLocalDataHandler = null;

	private ArrayList<FriendDataManagerListener> mListeners = new ArrayList<FriendDataManagerListener>();

	private Handler mHandler = new Handler();

	private static Context mContext = null;

	private static int mUserId = LcomConst.NO_USER;

	public static FriendDataManager getInstance() {
		return sDataManager;
	}

	public static void initializeFriendDataManager(int userId, Context context) {
		mContext = context;
		mUserId = userId;
		mServerDataHandler = new UserServerDataHandler(context);
		mServerDataHandler.setFriendListUpdateDataListener(sDataManager);
		mLocalDataHandler = new UserLocalDataHandler(context);
		mLocalDataHandler.setUserLocalDataListener(sDataManager);
	}

	private FriendDataManager() {
	}

	public void setFriendDataManagerListener(FriendDataManagerListener listener) {
		mListeners.add(listener);
	}

	public void removeFriendDataManagerListener(
			FriendDataManagerListener listener) {
		DbgUtil.showDebug(TAG, "removeFriendDataManagerListener");
		if (mListeners.contains(listener)) {
			mListeners.remove(listener);
		}
	}

	public boolean isListenerAlreadyRegistered(
			FriendDataManagerListener listener) {
		DbgUtil.showDebug(TAG, "listener num: " + mListeners.size());
		DbgUtil.showDebug(TAG, "listener: " + listener.getClass());
		if (mListeners.contains(listener)) {
			return true;
		} else {
			return false;
		}
	}

	public void onPause() {
		// TODO Need to initialize?
		// mServerDataHandler = null;
		// mLocalDatahandler = null;
	}

	/**
	 * RequestDataset from Database
	 * 
	 * @param userId
	 * @throws FriendDataManagerException
	 */
	public void requestFriendListDataset(int userId, boolean isForExisting,
			boolean isForNew) throws FriendDataManagerException {
		DbgUtil.showDebug(TAG, "requestDataset");
		if (mListeners == null || mListeners.size() == 0) {
			throw new FriendDataManagerException("mListenr is null");
		}

		if (userId == LcomConst.NO_USER) {
			throw new FriendDataManagerException("Illegal user id. Userid:  "
					+ userId);
		}

		// TODO
		// // Load server new data
		// if (isForNew) {
		// mServerDataHandler.requestNewUserData(userId);
		// }

		// Load local data
		if (isForExisting) {
			new LoadLocalFriendListAsyncTask().execute();
		}
	}

	public void requestMessageListDatasetWithTargetUser(int userId,
			int targetUserId, boolean isForExisting, boolean isForNew)
			throws FriendDataManagerException {
		DbgUtil.showDebug(TAG, "requestMessageListDatasetWithTargetUser");
		if (mListeners == null || mListeners.size() == 0) {
			throw new FriendDataManagerException("mListenr is null");
		}

		if (userId == LcomConst.NO_USER) {
			throw new FriendDataManagerException("Illegal user id. Userid:  "
					+ userId);
		}

		// Load server new data
		if (isForNew) {
			mServerDataHandler.requestNewUserDataWithTarget(userId,
					targetUserId);
		}

		// Load local data
		if (isForExisting) {
			new LoadLocalMessagesAsyncTask().execute(targetUserId);
			// new LoadLocalFriendListAsyncTask().execute();
		}
	}

	// public void requestFriendsNewThumbnail(ArrayList<Integer> targetUserIds)
	// throws FriendDataManagerException {
	public void requestFriendsNewThumbnail() throws FriendDataManagerException {
		DbgUtil.showDebug(TAG, "requestFriendsNewThumbnail");
		if (mListeners == null || mListeners.size() == 0) {
			throw new FriendDataManagerException("mListenr is null");
		}

		// Once we check thumbnails those are already stored in local DB so that
		// we can avoid unnecessary access to server
		long currentTime = TimeUtil.getCurrentDate();
		long lastTime = PreferenceUtil.getLastThumbnailCheckTime(mContext);
		DbgUtil.showDebug(TAG, "currentTime: " + currentTime);
		DbgUtil.showDebug(TAG, "lastTIme: " + lastTime);

		if (currentTime > lastTime + LcomConst.THUMBNAIL_CHECK_INTERVAL) {
			// if (currentTime > lastTime + LcomConst.TIME_MIN) {
			DbgUtil.showDebug(TAG, "expired");

			// Update thumbnail load time
			PreferenceUtil.setLastThumbnailCheckTime(mContext, currentTime);
			// new LoadLatestStoredMessagesAsyncTask().execute();
			new LoadLocalUserIdWithoutThumbnailAsyncTask().execute();
		}
	}

	/**
	 * Set data to Local DB. if failed to insert data, return false.
	 * 
	 * @param userId
	 * @param friendId
	 * @param userName
	 * @param friendName
	 * @param senderId
	 * @param message
	 * @param date
	 * @param friendThumb
	 * @return
	 */
	public boolean setFriendListPresentDataset(int userId, int friendId,
			String userName, String friendName, int senderId, String message,
			String date, byte[] friendThumb, String mailAddress) {
		DbgUtil.showDebug(TAG, "setFriendListPresentDataset");
		try {
			mLocalDataHandler.addNewMessageAndFriendIfNecessary(userId,
					friendId, userName, friendName, senderId, message, date,
					friendThumb, mailAddress);
			return true;
		} catch (UserLocalDataHandlerException e) {
			DbgUtil.showDebug(TAG,
					"UserLocalDataHandlerException: " + e.getMessage());
			TrackingUtil.trackExceptionMessage(mContext, TAG,
					"UserLocalDataHandlerException: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Send and register new message. If API Call is done successfully, this
	 * method returns true. Otherwise, returns false. And response data will be
	 * argument of notifyAddPresentDataFinished() method.
	 * 
	 * @param userId
	 * @param targetUserId
	 * @param userName
	 * @param targetUserName
	 * @param message
	 * @param date
	 * @throws FriendDataManagerException
	 */
	public void sendAndRegisterMessage(int userId, int targetUserId,
			String userName, String targetUserName, ArrayList<String> message,
			String date) throws FriendDataManagerException {
		DbgUtil.showDebug(TAG, "sendAndRegisterMessage");

		if (userId == LcomConst.NO_USER) {
			throw new FriendDataManagerException("Illegal user id. Userid:  "
					+ userId);
		}

		if (targetUserId == LcomConst.NO_USER) {
			throw new FriendDataManagerException("Illegal user id. Userid:  "
					+ userId);
		}

		// At first, we try to store/send data in Server side.
		boolean result = mServerDataHandler.sendAndRegiserMessage(userId,
				targetUserId, userName, targetUserName, message, date);

		// If API call is failed, we just notify we failed.
		if (result == false) {
			DbgUtil.showDebug(TAG, "result is false");
			notifyMessageSend(LcomConst.SEND_MESSAGE_CANNOT_BE_SENT_MESSAGE,
					null);
		}
	}

	@Override
	public void notifyNewServerUserDataSet(ArrayList<FriendListData> newUserData) {
		DbgUtil.showDebug(TAG, "notifyNewServerUserDataSet");
		for (FriendDataManagerListener listener : mListeners) {
			listener.notifyNewDataset(newUserData);
		}
	}

	@Override
	public void notifyLocalUserDataSet(ArrayList<FriendListData> userData) {
		DbgUtil.showDebug(TAG, "notifyLocalUserDataSet");
		if (userData != null) {
			DbgUtil.showDebug(TAG, "userData size:: " + userData.size());
		}
		for (FriendDataManagerListener listener : mListeners) {
			listener.notifyPresentDataset(userData);
		}

	}

	public interface FriendDataManagerListener {
		/**
		 * To be called when FriendDataManager get current dataset from DB
		 * 
		 * @param userData
		 */
		public void notifyPresentDataset(ArrayList<FriendListData> userData);

		/**
		 * To be called when FriendDataManager get new data from server. This
		 * method will return all available new message data for the current
		 * user.
		 * 
		 * @param newUserData
		 */
		public void notifyNewDataset(ArrayList<FriendListData> newUserData);

		/**
		 * To be called when FriendDataManager finished to add present (local)
		 * friend listdata.
		 */
		public void notifyAddPresentDataFinished(boolean result,
				MessageItemData messageData);

		/**
		 * To be called when FriendDataManager finished to add present (local)
		 * message data.
		 */
		public void notifyPresentMessageDataLoaded(
				ArrayList<MessageItemData> messageData);

		/**
		 * To be called when FriendDataManager finished to load conversation
		 * data for one friend message
		 */
		public void notifyNewConversationDataLoaded(
				ArrayList<MessageItemData> messageData);

		/**
		 * To be called when FriendDataManager get frien thumbnail data
		 */
		public void notifyFriendThubmailsLoaded(
				List<HashMap<Integer, Bitmap>> thumbnailsthumbnails);

		/**
		 * To be called when FriendDataManager finished to load latest message
		 * for targetUserId
		 */
		public void notifyLatestStoredMessage(FriendListData message);

		/**
		 * To be called when FriendDataManager finished to load nearlest expire
		 * notification data
		 */
		// public void notifiyNearlestExpireNotification(
		// NotificationContentData data);

		public void notifiyValidNotificationList(
				ArrayList<NotificationContentData> notifications);

	}

	private class LoadLocalUserIdWithoutThumbnailAsyncTask extends
			AsyncTask<Void, Void, ArrayList<Long>> {

		public LoadLocalUserIdWithoutThumbnailAsyncTask() {
			DbgUtil.showDebug(TAG, "LoadLocalUserIdWithoutThumbnailAsyncTask");
		}

		@Override
		protected ArrayList<Long> doInBackground(Void... params) {
			DbgUtil.showDebug(TAG, "doInBackground");
			try {
				return mLocalDataHandler
						.getFriendUseridThumbnailNotRegistered();
			} catch (UserLocalDataHandlerException e) {
				DbgUtil.showDebug(TAG,
						"UserLocalDataHandlerException: " + e.getMessage());
				TrackingUtil.trackExceptionMessage(mContext, TAG,
						"UserLocalDataHandlerException: " + e.getMessage());
				return null;
			}

		}

		@Override
		protected void onPostExecute(ArrayList<Long> result) {
			DbgUtil.showDebug(TAG,
					"LoadLocalUserIdWithoutThumbnailAsyncTask onPostExecute");
			if (result != null && result.size() != 0) {
				DbgUtil.showDebug(TAG, "size: " + result.size());
				try {
					mServerDataHandler.requestNewFriendThumbnails(result);
				} catch (FriendDataManagerException e) {
					DbgUtil.showDebug(TAG,
							"FriendDataManagerException: " + e.getMessage());
				}
			} else {
				// If no id returned, we just notify to client it was null
				for (FriendDataManagerListener listener : mListeners) {
					listener.notifyFriendThubmailsLoaded(null);
				}
			}

			// for (FriendDataManagerListener listener : mListeners) {
			// listener.notify
			// }
		}
	}

	private class LoadLocalFriendListAsyncTask extends
			AsyncTask<Void, Void, ArrayList<FriendListData>> {

		public LoadLocalFriendListAsyncTask() {
			DbgUtil.showDebug(TAG, "LoadLocalDataAsyncTask");
		}

		@Override
		protected ArrayList<FriendListData> doInBackground(Void... params) {
			DbgUtil.showDebug(TAG, "doInBackground");
			try {
				return mLocalDataHandler.getLocalUserDataset();
			} catch (UserLocalDataHandlerException e) {
				DbgUtil.showDebug(TAG,
						"UserLocalDataHandlerException: " + e.getMessage());
				TrackingUtil.trackExceptionMessage(mContext, TAG,
						"UserLocalDataHandlerException: " + e.getMessage());
				return null;
			}

		}

		@Override
		protected void onPostExecute(ArrayList<FriendListData> result) {
			DbgUtil.showDebug(TAG, "LoadLocalFriendListAsyncTask onPostExecute");

			// Notifyy to client
			for (FriendDataManagerListener listener : mListeners) {
				listener.notifyPresentDataset(result);
			}

			// Try to get new data
			try {
				// If result is null or size is 0, try to get all data
				if (result == null || result.size() == 0) {
					DbgUtil.showDebug(TAG, "result is null or size 0");
					mServerDataHandler.requestAllNewUserData(mUserId);
				} else {
					// Otherwize, try to get only new data
					DbgUtil.showDebug(TAG, "result size: " + result.size());
					mServerDataHandler.requestNewUserData(mUserId);
				}

			} catch (FriendDataManagerException e) {
				DbgUtil.showDebug(TAG,
						"FriendDataManagerException: " + e.getMessage());
			}

			// if (result != null && result.size() != 0) {
			// DbgUtil.showDebug(TAG, "size: " + result.size());
			// for (FriendDataManagerListener listener : mListeners) {
			// listener.notifyPresentDataset(result);
			// }
			// } else {
			// try {
			// mServerDataHandler.requestNewUserData(mUserId);
			// } catch (FriendDataManagerException e) {
			// DbgUtil.showDebug(TAG,
			// "FriendDataManagerException: " + e.getMessage());
			// }
			// }

		}
	}

	public static synchronized void removeUserPreferenceData(Context context,
			int userId) {
		DbgUtil.showDebug(TAG, "removeUserPreferenceData");
		if (userId != LcomConst.NO_USER) {
			mLocalDataHandler.removeLocalUserPreferenceData(context);
		}
	}

	public void requestLatestStoredMessage(int targetUserId)
			throws FriendDataManagerException {
		DbgUtil.showDebug(TAG, "requestLatestStoredMessage");
		if (targetUserId == LcomConst.NO_USER) {
			throw new FriendDataManagerException(
					"Illegal targetUser id. targetUserId:  " + targetUserId);
		}
		new LoadLatestStoredMessagesAsyncTask().execute(targetUserId);
	}

	public void requestNotificationNearestExpireData() {
		DbgUtil.showDebug(TAG, "getNotificationNearestExpireData");

		new LoadCurrentNotificationListAsyncTask().execute();
	}

	// private class LoadNearlestExpireNotificationAsyncTask extends
	// AsyncTask<Void, Void, NotificationContentData> {
	//
	// public LoadNearlestExpireNotificationAsyncTask() {
	// DbgUtil.showDebug(TAG, "LoadNearlestExpireNotificationAsyncTask");
	// }
	//
	// @Override
	// protected NotificationContentData doInBackground(Void... params) {
	// DbgUtil.showDebug(TAG, "doInBackground");
	// try {
	// if (mLocalDataHandler != null) {
	// return mLocalDataHandler.getNotificationNearestExpireData();
	// }
	// } catch (UserLocalDataHandlerException e) {
	// DbgUtil.showDebug(TAG,
	// "UserLocalDataHandlerException: " + e.getMessage());
	// }
	// return null;
	// }
	//
	// @Override
	// protected void onPostExecute(NotificationContentData result) {
	// DbgUtil.showDebug(TAG, "onPostExecute");
	// if (result != null) {
	// DbgUtil.showDebug(TAG, "result: " + result);
	// }
	//
	// for (FriendDataManagerListener listener : mListeners) {
	// listener.notifiyNearlestExpireNotification(result);
	// }
	// }
	// }

	private class LoadCurrentNotificationListAsyncTask extends
			AsyncTask<Void, Void, ArrayList<NotificationContentData>> {

		public LoadCurrentNotificationListAsyncTask() {
			DbgUtil.showDebug(TAG, "LoadCurrentNotificationListAsyncTask");
		}

		@Override
		protected ArrayList<NotificationContentData> doInBackground(
				Void... params) {
			DbgUtil.showDebug(TAG, "doInBackground");
			try {
				if (mLocalDataHandler != null) {
					return mLocalDataHandler.getCurrentNotificationList();
				}
			} catch (UserLocalDataHandlerException e) {
				DbgUtil.showDebug(TAG,
						"UserLocalDataHandlerException: " + e.getMessage());
			}
			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<NotificationContentData> result) {
			DbgUtil.showDebug(TAG, "onPostExecute");
			if (result != null) {
				DbgUtil.showDebug(TAG, "result: " + result.size());
			}

			for (FriendDataManagerListener listener : mListeners) {
				listener.notifiyValidNotificationList(result);
			}
		}
	}

	private class LoadLatestStoredMessagesAsyncTask extends
			AsyncTask<Integer, Void, FriendListData> {

		public LoadLatestStoredMessagesAsyncTask() {
			DbgUtil.showDebug(TAG, "LoadLatestStoredMessagesAsyncTask");
		}

		@Override
		protected FriendListData doInBackground(Integer... targetUserId) {
			DbgUtil.showDebug(TAG, "doInBackground");
			try {
				return mLocalDataHandler
						.getLatestStoredMessage(targetUserId[0]);
			} catch (UserLocalDataHandlerException e) {
				DbgUtil.showDebug(TAG,
						"UserLocalDataHandlerException: " + e.getMessage());
			}
			return null;
		}

		@Override
		protected void onPostExecute(FriendListData result) {
			DbgUtil.showDebug(TAG, "onPostExecute");
			if (result != null) {
				DbgUtil.showDebug(TAG, "result: " + result);
			}
			for (FriendDataManagerListener listener : mListeners) {
				listener.notifyLatestStoredMessage(result);
			}
		}
	}

	private class LoadLocalMessagesAsyncTask extends
			AsyncTask<Integer, Void, ArrayList<MessageItemData>> {

		public LoadLocalMessagesAsyncTask() {
			DbgUtil.showDebug(TAG, "LoadLocalMessagesAsyncTask");
		}

		@Override
		protected ArrayList<MessageItemData> doInBackground(
				Integer... targetUserId) {
			DbgUtil.showDebug(TAG, "doInBackground");
			return mLocalDataHandler.getLocalMessageDataset(targetUserId[0]);
		}

		@Override
		protected void onPostExecute(ArrayList<MessageItemData> result) {
			DbgUtil.showDebug(TAG, "onPostExecute");
			if (result != null) {
				DbgUtil.showDebug(TAG, "size: " + result.size());
			}
			for (FriendDataManagerListener listener : mListeners) {
				listener.notifyPresentMessageDataLoaded(result);
			}
		}
	}

	public synchronized void addNewNotification(int fromUserId, int toUserId,
			int number, long expireDate) {
		DbgUtil.showDebug(TAG, "addNewNotification");

		if (mLocalDataHandler != null) {
			mLocalDataHandler.addNewNotification(fromUserId, toUserId, number,
					expireDate);
		}
	}

	@Override
	public void notifyMessageSend(int result, MessageItemData postedMessageData) {
		DbgUtil.showDebug(TAG, "notifyMessageSend result: " + result);
		switch (Integer.valueOf(result)) {
		case LcomConst.SEND_MESSAGE_RESULT_OK:
			// We have successfully sent message and it has been stored on
			// Server side. Then, we are going to store it to local DB.
			try {
				mLocalDataHandler.addNewMessage(postedMessageData);
			} catch (UserLocalDataHandlerException e) {
				DbgUtil.showDebug(TAG,
						"UserLocalDataHandlerException: " + e.getMessage());
			}
			for (FriendDataManagerListener listener : mListeners) {
				listener.notifyAddPresentDataFinished(true, postedMessageData);
			}
			break;
		case LcomConst.SEND_MESSAGE_DATE_CANNOT_BE_PARSED:
			TrackingUtil.trackExceptionMessage(mContext, TAG,
					"SEND_MESSAGE_DATE_CANNOT_BE_PARSED");
		case LcomConst.SEND_MESSAGE_UNKNOWN_ERROR:
			TrackingUtil.trackExceptionMessage(mContext, TAG,
					"SEND_MESSAGE_UNKNOWN_ERROR");
		case LcomConst.SEND_MESSAGE_CANNOT_BE_SENT_MESSAGE:
			TrackingUtil.trackExceptionMessage(mContext, TAG,
					"SEND_MESSAGE_CANNOT_BE_SENT_MESSAGE");
		default:
			TrackingUtil.trackExceptionMessage(mContext, TAG,
					"notifyMessageSend switch is unknown case");
			// mLocalDataHandler.addNewMessage(null);
			// Because we can't send message due to some reasons. Then, we avoid
			// to store it to local DB and notify it to the user.
			for (FriendDataManagerListener listener : mListeners) {
				listener.notifyAddPresentDataFinished(false, null);
			}
			break;
		}
	}

	@Override
	public void notifyConversationServerDataSet(
			ArrayList<MessageItemData> messageData) {
		DbgUtil.showDebug(TAG, "notifyConversationServerDataSet");

		// Notify new m data to UI
		for (FriendDataManagerListener listener : mListeners) {
			listener.notifyNewConversationDataLoaded(messageData);
		}

		// Add new message and user data onto local DB
		try {
			if (mLocalDataHandler != null && messageData != null
					&& messageData.size() != 0) {
				mLocalDataHandler.addMultipleNewMessagesAndFriendIfNecessary(
						mUserId, messageData);
			}
		} catch (UserLocalDataHandlerException e) {
			DbgUtil.showDebug(TAG,
					"UserLocalDataHandlerException: " + e.getMessage());
		}

	}

	@Override
	public void notifyNewUserThumbnail(List<HashMap<Integer, Bitmap>> thumbnails) {
		DbgUtil.showDebug(TAG, "notifyNewUserThumbnail");
		for (FriendDataManagerListener listener : mListeners) {
			listener.notifyFriendThubmailsLoaded(thumbnails);
		}

		// If thumbnail is available, try to store it (them) to DBs
		if (thumbnails != null) {
			storeFriendThumbnails(thumbnails);
		}
	}

	private void storeFriendThumbnails(List<HashMap<Integer, Bitmap>> thumbnails) {
		DbgUtil.showDebug(TAG, "storeFriendThumbnails");
		if (mLocalDataHandler != null) {
			mLocalDataHandler.storeFriendThumbnails(thumbnails);
		}
	}

	@Override
	public void notifyUserAllDataSet(ArrayList<FriendListData> allData) {
		DbgUtil.showDebug(TAG, "notifyUserAllDataSet");
		for (FriendDataManagerListener listener : mListeners) {
			listener.notifyNewDataset(allData);
		}

		if (mLocalDataHandler != null) {
			try {
				String userName = PreferenceUtil.getUserName(mContext);
				mLocalDataHandler.addMultipleNewMessages(mUserId, userName,
						allData);
			} catch (UserLocalDataHandlerException e) {
				DbgUtil.showDebug(TAG,
						"UserLocalDataHandlerException: " + e.getMessage());
			}
		}
	}

}
