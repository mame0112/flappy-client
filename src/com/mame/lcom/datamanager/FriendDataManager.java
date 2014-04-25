package com.mame.lcom.datamanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;

import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.data.FriendListData;
import com.mame.lcom.data.FriendListUpdateData;
import com.mame.lcom.data.MessageItemData;
import com.mame.lcom.db.UserLocalDataHandler;
import com.mame.lcom.db.UserLocalDataHandler.UserLocalDataListener;
import com.mame.lcom.exception.FriendDataManagerException;
import com.mame.lcom.exception.UserLocalDataHandlerException;
import com.mame.lcom.server.UserServerDataHandler;
import com.mame.lcom.server.UserServerDataHandler.UserServerDataListener;
import com.mame.lcom.util.DbgUtil;
import com.mame.lcom.util.PreferenceUtil;
import com.mame.lcom.util.TimeUtil;
import com.mame.lcom.util.TrackingUtil;

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

		// Load server new data
		if (isForNew) {
			mServerDataHandler.requestNewUserData(userId);
		}

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

	public void requestFriendsNewThumbnail(ArrayList<Integer> targetUserIds)
			throws FriendDataManagerException {
		DbgUtil.showDebug(TAG, "requestFriendsNewThumbnail");
		if (mListeners == null || mListeners.size() == 0) {
			throw new FriendDataManagerException("mListenr is null");
		}

		if (targetUserIds == null) {
			throw new FriendDataManagerException("targetUserIds is null");
		}

		// Once we check thumbnails those are already stored in local DB so that
		// we can avoid unnecessary access to server
		long currentTime = TimeUtil.getCurrentDate();
		long lastTime = PreferenceUtil.getLastThumbnailCheckTime(mContext);
		DbgUtil.showDebug(TAG, "currentTime: " + currentTime);
		DbgUtil.showDebug(TAG, "lastTIme: " + lastTime);

		// if (currentTime > lastTime + LcomConst.THUMBNAIL_CHECK_INTERVAL) {
		try {
			ArrayList<Integer> notRegisteredUserIds = mLocalDataHandler
					.getFriendUseridThumbnailNotRegistered(targetUserIds);

			// Update preference
			PreferenceUtil.setLastThumbnailCheckTime(mContext, currentTime);

			// Check thumbnail
			mServerDataHandler.requestNewFriendThumbnails(notRegisteredUserIds);
		} catch (UserLocalDataHandlerException e) {
			DbgUtil.showDebug(TAG,
					"UserLocalDataHandlerException: " + e.getMessage());
			mServerDataHandler.requestNewFriendThumbnails(targetUserIds);
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
		DbgUtil.showDebug(TAG, "notifyNewLocalUserDataSet");
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
		public void notifyLatestStoredMessage(String message);
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
			if (result != null) {
				DbgUtil.showDebug(TAG, "size: " + result.size());
			}
			for (FriendDataManagerListener listener : mListeners) {
				listener.notifyPresentDataset(result);
			}
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

	private class LoadLatestStoredMessagesAsyncTask extends
			AsyncTask<Integer, Void, String> {

		public LoadLatestStoredMessagesAsyncTask() {
			DbgUtil.showDebug(TAG, "LoadLatestStoredMessagesAsyncTask");
		}

		@Override
		protected String doInBackground(Integer... targetUserId) {
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
		protected void onPostExecute(String result) {
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

	@Override
	public void notifyMessageSend(int result, MessageItemData postedMessageData) {
		DbgUtil.showDebug(TAG, "notifyMessageSend result: " + result);
		switch (Integer.valueOf(result)) {
		case LcomConst.SEND_MESSAGE_RESULT_OK:
			// We have successfully sent message and it has been stored on
			// Server side. Then, we are going to store it to local DB.
			mLocalDataHandler.addNewMessage(postedMessageData);
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
}
