package com.mame.flappy.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;

import com.mame.flappy.constant.LcomConst;
import com.mame.flappy.data.FriendListData;
import com.mame.flappy.data.MessageItemData;
import com.mame.flappy.exception.FriendDataManagerException;
import com.mame.flappy.ui.ConversationActivityUtil;
import com.mame.flappy.util.DbgUtil;
import com.mame.flappy.util.ImageUtil;
import com.mame.flappy.util.TrackingUtil;
import com.mame.flappy.web.LcomHttpWebAPI;
import com.mame.flappy.web.LcomServerAccessor;
import com.mame.flappy.web.LcomHttpWebAPI.LcomWebAPIListener;

public class UserServerDataHandler implements
		LcomServerAccessor.LcomServerAccessorListener {

	private final String TAG = LcomConst.TAG + "/UserServerDataHandler";

	// Updated date list for this access time
	// private List<FriendListUpdateData> mUdateData = new
	// ArrayList<FriendListUpdateData>();

	private UserServerDataListener mDataListener = null;

	private LcomServerAccessor mWebAPI = null;

	private Handler mHandler = new Handler();

	private final static String REQUEST_NEW_USER_DATA = "request_new_user_data";

	private final static String REQUEST_CONVERSATION_DATA = "request_conversation_data";

	private final static String SEND_AND_ADD_DATA = "send_add_data";

	private final static String REQUEST_FRIEND_THUMBNAILS = "request_friend_thumbnails";

	private LcomConst.ServerRequestContext mRequestContext = LcomConst.ServerRequestContext.none;

	private Context mContext = null;

	public UserServerDataHandler(Context context) {
		// public UserServerDataHandler() {
		mWebAPI = new LcomServerAccessor();
		mWebAPI.setListener(this);
		mContext = context;
	}

	/**
	 * Return true if Web API call is success. Otherwise, return false.
	 * 
	 * @param userId
	 * @param targetUserId
	 * @param userName
	 * @param targetUserName
	 * @param message
	 * @param date
	 * @return
	 * @throws FriendDataManagerException
	 */
	public boolean sendAndRegiserMessage(int userId, int targetUserId,
			String userName, String targetUserName, ArrayList<String> message,
			String date) throws FriendDataManagerException {
		DbgUtil.showDebug(TAG, "sendAndRegisterMessage");
		if (mDataListener == null) {
			throw new FriendDataManagerException(
					"UserServerDataListener is null");
		}

		// Set request context
		mRequestContext = LcomConst.ServerRequestContext.sendAndRegiserMessage;

		String parsedMessage = null;
		if (message != null) {
			DbgUtil.showDebug(TAG, "message: " + message);
			parsedMessage = ConversationActivityUtil
					.parseArrayListMessageToString(message);
			DbgUtil.showDebug(TAG, "parsedMessage: " + parsedMessage);
		}

		if (parsedMessage != null) {
			DbgUtil.showDebug(TAG, "parsed message is not null");
			String origin = TAG + SEND_AND_ADD_DATA;
			String key[] = { LcomConst.SERVLET_ORIGIN,
					LcomConst.SERVLET_USER_ID, LcomConst.SERVLET_USER_NAME,
					LcomConst.SERVLET_TARGET_USER_ID,
					LcomConst.SERVLET_TARGET_USER_NAME,
					LcomConst.SERVLET_MESSAGE_BODY,
					LcomConst.SERVLET_MESSAGE_DATE, LcomConst.SERVLET_API_LEVEL };
			String value[] = { origin, String.valueOf(userId), userName,
					String.valueOf(targetUserId), targetUserName,
					parsedMessage, date, String.valueOf(LcomConst.API_LEVEL) };
			mWebAPI.sendData(LcomConst.SERVLET_NAME_SEND_ADD_MESSAGE, key,
					value);
			return true;
		} else {
			DbgUtil.showDebug(TAG, "parsed message is null");
			TrackingUtil.trackExceptionMessage(mContext, TAG,
					"parsedMessage is null");
			return false;
		}
	}

	public void requestNewUserData(int userId)
			throws FriendDataManagerException {
		DbgUtil.showDebug(TAG, "requestNewUserData");
		if (mDataListener == null) {
			throw new FriendDataManagerException(
					"UserServerDataListener is null");
		}

		// Set context
		mRequestContext = LcomConst.ServerRequestContext.requestNewUserData;

		String origin = TAG + REQUEST_NEW_USER_DATA;
		String key[] = { LcomConst.SERVLET_ORIGIN, LcomConst.SERVLET_USER_ID,
				LcomConst.SERVLET_API_LEVEL };
		String value[] = { origin, String.valueOf(userId),
				String.valueOf(LcomConst.API_LEVEL) };
		mWebAPI.sendData(LcomConst.SERVLET_NAME_NEW_MESSAGE, key, value);

	}

	public void requestNewUserDataWithTarget(int userId, int targetUserId)
			throws FriendDataManagerException {
		DbgUtil.showDebug(TAG, "requestNewUserDataWithTarget");
		if (mDataListener == null) {
			throw new FriendDataManagerException(
					"UserServerDataListener is null");
		}

		// Set context
		mRequestContext = LcomConst.ServerRequestContext.requestConversationData;

		String origin = TAG + REQUEST_CONVERSATION_DATA;
		String key[] = { LcomConst.SERVLET_ORIGIN, LcomConst.SERVLET_USER_ID,
				LcomConst.SERVLET_TARGET_USER_ID, LcomConst.SERVLET_API_LEVEL };
		String value[] = { origin, String.valueOf(userId),
				String.valueOf(targetUserId),
				String.valueOf(LcomConst.API_LEVEL) };
		mWebAPI.sendData(LcomConst.SERVLET_CONVERSATION_DATA, key, value);

	}

	public void requestNewFriendThumbnails(ArrayList<Integer> targetUserIds)
			throws FriendDataManagerException {
		if (mDataListener == null) {
			throw new FriendDataManagerException(
					"UserServerDataListener is null");
		}

		// Set context
		mRequestContext = LcomConst.ServerRequestContext.requestFriendThumbnails;

		// Parse ArrayList to string
		if (targetUserIds != null) {
			String parsedId = "a";
			boolean isFirst = true;
			for (int id : targetUserIds) {
				if (isFirst) {
					parsedId = parsedId + id + LcomConst.SEPARATOR;
					isFirst = false;
				} else {
					parsedId = parsedId + id + LcomConst.SEPARATOR;
				}
			}

			if (parsedId != null) {

				// Remove first "a"
				parsedId = parsedId.substring(1, parsedId.length());

				DbgUtil.showDebug(TAG, "parsedId: " + parsedId);

				// Remove first "a" and last separetor
				String origin = TAG + REQUEST_FRIEND_THUMBNAILS;
				String key[] = { LcomConst.SERVLET_ORIGIN,
						LcomConst.SERVLET_TARGET_USER_ID,
						LcomConst.SERVLET_API_LEVEL };
				String value[] = { origin, parsedId,
						String.valueOf(LcomConst.API_LEVEL) };
				mWebAPI.sendData(LcomConst.SERVLET_FRIEBD_THUMBNAILS, key,
						value);
			}

		} else {
			TrackingUtil.trackExceptionMessage(mContext, TAG,
					"targetUserIds is null");
			notifyNewFriendThumbnails(null);
		}

	}

	public void setFriendListUpdateDataListener(UserServerDataListener listener) {
		mDataListener = listener;
	}

	@Override
	public void onResponseReceived(List<String> respList) {
		DbgUtil.showDebug(TAG, "onResponseReceved");
		if (respList != null && respList.size() != 0) {
			try {
				String origin = respList.get(0);
				if (origin != null) {
					if (origin.equals(TAG + REQUEST_NEW_USER_DATA)) {
						DbgUtil.showDebug(TAG, "REQUEST_NEW_USER_DATA");
						String response = respList.get(1);
						if (response != null) {
							DbgUtil.showDebug(TAG, "response: " + response);
							final ArrayList<FriendListData> newUserDatas = parseResponse(response);
							notifyNewDataset(newUserDatas);
						} else {
							notifyNewDataset(null);
						}
					} else if (origin.equals(TAG + SEND_AND_ADD_DATA)) {
						DbgUtil.showDebug(TAG, "SEND_AND_ADD_DATA");
						String result = respList.get(1);
						String userId = respList.get(2);
						String userName = respList.get(3);
						String targetUserId = respList.get(4);
						String targetUserName = respList.get(5);
						String message = respList.get(6);
						String date = respList.get(7);

						DbgUtil.showDebug(TAG, "userId" + userId);
						DbgUtil.showDebug(TAG, "userName" + userName);
						DbgUtil.showDebug(TAG, "targetUserId" + targetUserId);
						DbgUtil.showDebug(TAG, "targetUserName"
								+ targetUserName);
						DbgUtil.showDebug(TAG, "message" + message);
						DbgUtil.showDebug(TAG, "date" + date);

						if (userId != null && targetUserId != null) {
							long date2 = Long.valueOf(date);

							MessageItemData mesageData = new MessageItemData(
									Integer.valueOf(userId),
									Integer.valueOf(targetUserId), userName,
									targetUserName, message, date2, null);
							mDataListener.notifyMessageSend(
									Integer.valueOf(result), mesageData);
						} else {
							mDataListener.notifyMessageSend(
									Integer.valueOf(result), null);
						}
					} else if (origin.equals(TAG + REQUEST_CONVERSATION_DATA)) {
						DbgUtil.showDebug(TAG, "REQUEST_CONVERSATION_DATA");
						String response = respList.get(1);
						if (response != null) {
							DbgUtil.showDebug(TAG, "response: " + response);
							final ArrayList<MessageItemData> messageDatas = parseResponseForConveersation(response);
							notifyConversationDataset(messageDatas);
						} else {
							notifyConversationDataset(null);
						}
					} else if (origin.equals(TAG + REQUEST_FRIEND_THUMBNAILS)) {
						DbgUtil.showDebug(TAG, "REQUEST_FRIEND_THUMBNAILS");
						String response = respList.get(1);
						if (response != null && !response.equals("null")) {
							DbgUtil.showDebug(TAG, "thumbnail response: "
									+ response);
							List<HashMap<Integer, Bitmap>> result = parseNewFriendThumbnailData(response);
							notifyNewFriendThumbnails(result);
						} else {
							notifyNewFriendThumbnails(null);
						}
					} else {
						handleErrorCase();
						TrackingUtil.trackExceptionMessage(mContext, TAG,
								"origin is unexpected case: " + origin);
					}
				} else {
					handleErrorCase();
					TrackingUtil.trackExceptionMessage(mContext, TAG,
							"origin is null");
				}
			} catch (IndexOutOfBoundsException e) {
				DbgUtil.showDebug(TAG,
						"IndexOutOfBoundsException: " + e.getMessage());
				TrackingUtil.trackExceptionMessage(mContext, TAG,
						"IndexOutOfBoundsException: " + e.getMessage());

				// Based on request code, we will handle error.
				handleErrorCase();
			}
		} else {
			DbgUtil.showDebug(TAG, "respList is null or size is 0");
			handleErrorCase();
			TrackingUtil.trackExceptionMessage(mContext, TAG,
					"respList is null or size is 0");
			handleErrorCase();
		}

		// Initialize request context
		mRequestContext = LcomConst.ServerRequestContext.none;

	}

	private ArrayList<FriendListData> parseResponse(String response) {
		DbgUtil.showDebug(TAG, "parseResponse: " + response);
		ArrayList<FriendListData> newUserData = new ArrayList<FriendListData>();
		if (response != null) {
			try {
				String[] newMessage = response.split(LcomConst.ITEM_SEPARATOR);
				for (int i = 0; i < newMessage.length; i++) {
					if (newMessage[i] != null) {

						// + LcomConst.SEPARATOR + numOfMessage;

						String[] parsed = newMessage[i]
								.split(LcomConst.SEPARATOR);

						// String userId = parsed[0];
						// String userName = parsed[1];
						String friendId = parsed[0];
						String friendName = parsed[1];
						String message = parsed[2];
						String date = parsed[3];
						// String numOfMessage = parsed[6];

						DbgUtil.showDebug(TAG, "parsed:" + friendId + " "
								+ friendName + " " + message + " " + date);

						String[] msgArray = null;
						String[] dateArray = null;

						if (message != null
								&& message
										.contains(LcomConst.MESSAGE_SEPARATOR)) {
							msgArray = message
									.split(LcomConst.MESSAGE_SEPARATOR);

						}

						if (date != null
								&& date.contains(LcomConst.MESSAGE_SEPARATOR)) {
							dateArray = date.split(LcomConst.MESSAGE_SEPARATOR);
						}

						// int friendId, String friendName, int lastSenderId,
						// String lastMessage, long lastMsgDate, int
						// numOfNewMessage,
						// String mailAddress, Bitmap thumbnail

						// If message is more than 2
						if (msgArray != null && msgArray.length != 0) {
							for (int j = 0; j < msgArray.length; j++) {
								DbgUtil.showDebug(TAG, "dateArray: "
										+ dateArray[j]);
								// If message is only 1
								FriendListData data = new FriendListData(
										Integer.valueOf(friendId), friendName,
										Integer.valueOf(friendId), msgArray[j],
										Long.valueOf(dateArray[j]), 1, null,
										null);
								newUserData.add(data);
							}
						} else {
							DbgUtil.showDebug(TAG, "B");
							// If message is only 1
							FriendListData data = new FriendListData(
									Integer.valueOf(friendId), friendName,
									Integer.valueOf(friendId), message,
									Long.valueOf(date), 1, null, null);
							newUserData.add(data);
						}

						// data.setNewMessage(message);
						// data.setNewMessageDate(date);
						// data.setNewMessageSender(userName);
						// data.setNewMessageSenderId(Integer.valueOf(senderId));
						// newUserData.add(data);
					}
				}
			} catch (IndexOutOfBoundsException e) {
				DbgUtil.showDebug(TAG,
						"IndexOutOfBoundException: " + e.getMessage());
				TrackingUtil.trackExceptionMessage(mContext, TAG,
						"IndexOutOfBoundsException: " + e.getMessage());
			} catch (NumberFormatException e) {
				DbgUtil.showDebug(TAG,
						"NumberFormatException: " + e.getMessage());
				TrackingUtil.trackExceptionMessage(mContext, TAG,
						"NumberFormatException: " + e.getMessage());
			}
		} else {
			TrackingUtil.trackExceptionMessage(mContext, TAG,
					"respList is null");
		}

		return newUserData;
	}

	private List<HashMap<Integer, Bitmap>> parseNewFriendThumbnailData(
			String response) {
		DbgUtil.showDebug(TAG, "parseNewFriendThumbnail");

		List<HashMap<Integer, Bitmap>> result = new ArrayList<HashMap<Integer, Bitmap>>();

		if (response != null) {
			String[] items = response.split(LcomConst.ITEM_SEPARATOR);
			if (items != null && items.length != 0) {
				for (int i = 0; i < items.length; i++) {
					String[] item = items[i].split(LcomConst.SEPARATOR);
					String userId = item[0];
					String thumbStr = item[1];
					if (userId != null && thumbStr != null) {
						// DbgUtil.showDebug(TAG, "userId: " + userId
						// + " thumgStr: " + thumbStr);
						Bitmap bitmap = ImageUtil
								.decodeBase64ToBitmap(thumbStr);
						if (bitmap != null) {
							DbgUtil.showDebug(TAG,
									"bitmap size:" + bitmap.getWidth());
						} else {
							DbgUtil.showDebug(TAG, "bitmap is null");
						}
						HashMap<Integer, Bitmap> map = new HashMap<Integer, Bitmap>();
						map.put(Integer.valueOf(userId), bitmap);
						result.add(map);
					}

				}
				return result;
			}
		} else {
			TrackingUtil.trackExceptionMessage(mContext, TAG,
					"respList is null");
		}

		return null;
	}

	private ArrayList<MessageItemData> parseResponseForConveersation(
			String response) {
		DbgUtil.showDebug(TAG, "parseResponse: " + response);
		ArrayList<MessageItemData> messageData = new ArrayList<MessageItemData>();
		if (response != null) {
			String[] newMessage = response.split(LcomConst.ITEM_SEPARATOR);
			for (int i = 0; i < newMessage.length; i++) {
				if (newMessage[i] != null) {
					String[] parsed = newMessage[i].split(LcomConst.SEPARATOR);
					String userId = parsed[0];
					// String firstName = parsed[1];
					String friendUserId = parsed[1];
					String friendUserName = parsed[2];
					String message = parsed[3];
					String date = parsed[4];

					DbgUtil.showDebug(TAG, "parsed:" + userId + " "
							+ friendUserId + " " + friendUserName + " "
							+ message + " " + date);

					if (message != null && date != null) {
						String[] msgArray = message
								.split(LcomConst.MESSAGE_SEPARATOR);
						String[] dateArray = date
								.split(LcomConst.MESSAGE_SEPARATOR);
						for (int j = 0; j < msgArray.length; j++) {
							try {
								if (msgArray[j] != null && dateArray[j] != null) {
									MessageItemData data = new MessageItemData(
											Integer.valueOf(friendUserId),
											Integer.valueOf(userId),
											friendUserName, null, msgArray[j],
											Long.valueOf(dateArray[j]), null);
									messageData.add(data);
								}
							} catch (NumberFormatException e) {
								DbgUtil.showDebug(
										TAG,
										"NumberFormatException: "
												+ e.getMessage());
							}

						}
					}
				}
			}
		}

		return messageData;
	}

	private void notifyNewDataset(final ArrayList<FriendListData> newUserDatas) {
		DbgUtil.showDebug(TAG, "notifyNewDataset");
		new Thread(new Runnable() {
			@Override
			public void run() {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						mDataListener.notifyNewServerUserDataSet(newUserDatas);
					}
				});
			}

		}).start();
	}

	private void notifyConversationDataset(
			final ArrayList<MessageItemData> messageDatas) {
		DbgUtil.showDebug(TAG, "notifyConversationDataset");
		new Thread(new Runnable() {
			@Override
			public void run() {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						mDataListener
								.notifyConversationServerDataSet(messageDatas);
					}
				});
			}

		}).start();
	}

	private void notifyNewFriendThumbnails(
			final List<HashMap<Integer, Bitmap>> result) {
		DbgUtil.showDebug(TAG, "notifyNewFriendThumbnails");
		new Thread(new Runnable() {
			@Override
			public void run() {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						mDataListener.notifyNewUserThumbnail(result);
					}
				});
			}

		}).start();
	}

	@Override
	public void onAPITimeout() {
		DbgUtil.showDebug(TAG, "onAPITimeout");
		TrackingUtil.trackExceptionMessage(mContext, TAG, "API call timeout");
		handleErrorCase();
	}

	private void handleErrorCase() {
		DbgUtil.showDebug(TAG, "handleErrorcase");
		if (mDataListener != null) {
			switch (mRequestContext) {
			case none:
				// Nothing to do (because default value)
				DbgUtil.showDebug(TAG, "requestContext is none");
				break;
			case requestNewUserData:
				// Notify new dataset is none
				DbgUtil.showDebug(TAG, "requestContext is requestNewUserdata");
				notifyNewDataset(null);
				break;
			case sendAndRegiserMessage:
				DbgUtil.showDebug(TAG,
						"requestContext is sendAndRegisterMessage");
				mDataListener.notifyMessageSend(
						LcomConst.SEND_MESSAGE_CANNOT_BE_SENT_MESSAGE, null);
				break;
			case requestConversationData:
				DbgUtil.showDebug(TAG,
						"requestContext is requestConversationData");
				notifyConversationDataset(null);
				break;
			case requestFriendThumbnails:
				DbgUtil.showDebug(TAG,
						"requestContext is requestFriendThumbnails");
				notifyNewFriendThumbnails(null);
				break;
			default:
				DbgUtil.showDebug(TAG, "requestContext is unknown case");
				TrackingUtil.trackExceptionMessage(mContext, TAG,
						"request context is unknown");
				break;
			// mDataListener.notifyAPICallTimeOut(mRequestContext);
			}
		} else {
			TrackingUtil.trackExceptionMessage(mContext, TAG,
					"mDataListener is null");
		}
	}

	// Interface to notify new user data to client of this class
	public interface UserServerDataListener {
		public void notifyNewServerUserDataSet(
				ArrayList<FriendListData> newUserData);

		public void notifyMessageSend(int result, MessageItemData mesageData);

		public void notifyConversationServerDataSet(
				ArrayList<MessageItemData> messageData);

		public void notifyNewUserThumbnail(
				List<HashMap<Integer, Bitmap>> thumbnails);
	}

}
