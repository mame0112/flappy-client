package com.mame.lcom.datamanager;

import java.util.ArrayList;

import android.content.Context;
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

public class FriendDataManager implements UserServerDataListener,
		UserLocalDataListener {

	private final static String TAG = LcomConst.TAG + "/FriendDataManager";

	private static FriendDataManager sDataManager = new FriendDataManager();

	private static UserServerDataHandler mServerDataHandler = null;

	private static UserLocalDataHandler mLocalDataHandler = null;

	private ArrayList<FriendDataManagerListener> mListeners = new ArrayList<FriendDataManagerListener>();

	private Handler mHandler = new Handler();

	public static FriendDataManager getInstance() {
		return sDataManager;
	}

	public static void initializeFriendDataManager(Context context) {
		if (sDataManager != null) {
			new FriendDataManagerException(
					"InitializeFriendDataManager should not be called 2 times.");
		}
		mServerDataHandler = new UserServerDataHandler();
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

	public void requestFriendListDatasetWithTargetUser(int userId,
			int targetUserId, boolean isForExisting, boolean isForNew)
			throws FriendDataManagerException {
		DbgUtil.showDebug(TAG, "requestFriendListDatasetWithTargetUser");
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
			new LoadLocalFriendListAsyncTask().execute();
		}
	}

	/**
	 * Load data for current user
	 * 
	 * @param targetUserId
	 * @throws FriendDataManagerException
	 */
	// public void requestDataForCurrentUser(int userId, int targetUserId)
	// throws FriendDataManagerException {
	//
	// if (targetUserId == LcomConst.NO_USER) {
	// throw new FriendDataManagerException(
	// "Illegal user id. targetUserId:  " + targetUserId);
	// }
	//
	// // Request server data
	// mServerDataHandler.requestConversationData(userId, targetUserId);
	//
	// // Get local data
	// new LoadLocalMessagesAsyncTask().execute();
	// }

	// public void setFriendListAllDataset(int userId, int friendId,
	// String friendName, int senderId, String message, String date,
	// byte[] friendThumb) {
	// DbgUtil.showDebug(TAG, "setFriendAllDataset");
	// setFriendListPresentDataset(userId, friendId, friendName, senderId,
	// message, date, null);
	// }

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
	public void notifyNewServerUserDataSet(
			ArrayList<FriendListUpdateData> newUserData) {
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
		public void notifyNewDataset(ArrayList<FriendListUpdateData> newUserData);

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

	private class LoadLocalMessagesAsyncTask extends
			AsyncTask<Void, Void, ArrayList<MessageItemData>> {

		public LoadLocalMessagesAsyncTask() {
			DbgUtil.showDebug(TAG, "LoadLocalMessagesAsyncTask");
		}

		@Override
		protected ArrayList<MessageItemData> doInBackground(Void... params) {
			DbgUtil.showDebug(TAG, "doInBackground");
			return mLocalDataHandler.getLocalMessageDataset();
		}

		@Override
		protected void onPostExecute(ArrayList<MessageItemData> result) {
			DbgUtil.showDebug(TAG, "onPostExecute");
			if (result != null) {
				DbgUtil.showDebug(TAG, "size: " + result.size());
			}
			for (FriendDataManagerListener listener : mListeners) {
				// TODO Need to check if this change has impact on other client.
				listener.notifyPresentMessageDataLoaded(result);
			}
		}
	}

	@Override
	public void notifyMessageSend(int result, MessageItemData postedMessageData) {
		DbgUtil.showDebug(TAG, "notifyMessageSend");
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
		case LcomConst.SEND_MESSAGE_UNKNOWN_ERROR:
		case LcomConst.SEND_MESSAGE_CANNOT_BE_SENT_MESSAGE:
		default:
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
		DbgUtil.showDebug(TAG, "messageData.size(): " + messageData.size());
		for (FriendDataManagerListener listener : mListeners) {
			listener.notifyNewConversationDataLoaded(messageData);
		}
	}
}
