package com.mame.lcom.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.mame.lcom.R;
import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.data.FriendListData;
import com.mame.lcom.data.MessageItemData;
import com.mame.lcom.datamanager.FriendDataManager;
import com.mame.lcom.datamanager.FriendDataManager.FriendDataManagerListener;
import com.mame.lcom.exception.FriendDataManagerException;
import com.mame.lcom.notification.NewMessageNotification;
import com.mame.lcom.server.LcomDeviceIdRegisterHelper;
import com.mame.lcom.server.LcomDeviceIdRegisterHelper.LcomPushRegistrationHelperListener;
import com.mame.lcom.ui.view.FriendListCustomAdapter;
import com.mame.lcom.util.DbgUtil;
import com.mame.lcom.util.PreferenceUtil;
import com.mame.lcom.util.TrackingUtil;

public class FriendListActivity extends Activity implements
		FriendDataManagerListener, LcomPushRegistrationHelperListener {

	private final String TAG = LcomConst.TAG + "/FriendListActivity";

	private FriendDataManager mManager = null;

	private int mUserId = LcomConst.NO_USER;

	private String mUserName = null;

	private ArrayList<FriendListData> mNewUserData = null;

	private ArrayList<FriendListData> mUserData = null;

	private FriendListCustomAdapter mAdapter = null;

	/**
	 * This is for merging local and server data into one
	 */
	private HashMap<Integer, FriendListData> mFriendTmpData = new HashMap<Integer, FriendListData>();

	private ArrayList<FriendListData> mFriendListData = new ArrayList<FriendListData>();

	private ListView mListView = null;

	private Button mFirstAddButton = null;

	private TextView mFirstAddText = null;

	private Activity mActivity = null;

	private final int REQUEST_CODE = 1;

	private Handler mHandler = new Handler();

	private boolean isNewDataAvailable = false;

	private boolean isExistingDataAvailable = false;

	private ProgressDialogFragment mProgressDialog = null;

	private LcomDeviceIdRegisterHelper mHelper = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.friendlist);

		Intent intent = getIntent();
		if (intent != null) {
			mUserId = intent.getIntExtra(LcomConst.EXTRA_USER_ID,
					LcomConst.NO_USER);
			mUserName = intent.getStringExtra(LcomConst.EXTRA_USER_NAME);
		}

		mActivity = this;

		mProgressDialog = ProgressDialogFragment.newInstance(
				getString(R.string.str_friendlist_progress_title),
				getString(R.string.str_friendlist_progress_desc));

		mHelper = new LcomDeviceIdRegisterHelper(this);
		mHelper.setPushRegistrationListener(this);

		FriendDataManager.initializeFriendDataManager(getApplicationContext());
		mManager = FriendDataManager.getInstance();
		mManager.setFriendDataManagerListener(FriendListActivity.this);

		mAdapter = new FriendListCustomAdapter(getApplicationContext(), 0,
				mFriendListData);

		mNewUserData = new ArrayList<FriendListData>();

		mListView = (ListView) findViewById(R.id.friendListView);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				DbgUtil.showDebug(TAG, "onItemClick positon: " + position);
				FriendListData data = mAdapter.getItem(position);
				DbgUtil.showDebug(TAG, "id: " + data.getFriendId() + " name: "
						+ data.getFriendName());
				FriendListActivityUtil.startActivityConversationViewByPos(
						mActivity, mUserId, mUserName, position,
						data.getFriendId(), data.getFriendName(),
						data.getMailAddress(), data.getThumbnail());
			}
		});

		mFirstAddText = (TextView) findViewById(R.id.firstFriendAddText);
		mFirstAddText.setVisibility(View.GONE);

		mFirstAddButton = (Button) findViewById(R.id.firstFriendAddButton);
		mFirstAddButton.setVisibility(View.GONE);
		mFirstAddButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				DbgUtil.showDebug(TAG, "first add button pressed");
				TrackingUtil.trackEvent(getApplicationContext(),
						TrackingUtil.EVENT_CATEGORY_FRIEND_LIST,
						TrackingUtil.EVENT_ACTION_FRIEND_LIST,
						TrackingUtil.EVENT_LABEL_FRIEND_LIST_FIRST_ADD_BUTTON,
						1);
				FriendListActivityUtil.startActivityForInvitation(mActivity,
						mUserId, mUserName, REQUEST_CODE);
			}
		});

		// Try to get device Id for push message
		if (!mHelper.isDeviceIdAvailable(getApplicationContext())) {
			mHelper.checkGPSAndAndRegisterDeviceId(mActivity, mUserId);
		} else {
			// If Google play service and Device id is ready, we try to get
			// actual data.
			requestUserData();
			mProgressDialog.show(getFragmentManager(), "progress");
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		DbgUtil.showDebug(TAG, "onActivityResult");
		switch (requestCode) {
		case REQUEST_CODE:
			if (resultCode == RESULT_OK) {
				DbgUtil.showDebug(TAG, "RESULT_OK");
				requestUserData();
			}
			break;
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		TrackingUtil.trackActivityStart(this);
	}

	public void onStop() {
		super.onStop();
		TrackingUtil.trackActivityStop(this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// If this activity's listener is not registered yet, register. (For
		// resume activity case. in normal case, it should be already registered
		// in onCreate)
		if (!mManager.isListenerAlreadyRegistered(this)) {
			DbgUtil.showDebug(TAG,
					"registered: " + mManager.isListenerAlreadyRegistered(this));
			mManager.setFriendDataManagerListener(this);
		}

		// Initialize flag
		isNewDataAvailable = false;
		isExistingDataAvailable = false;

		// Make invisible for first itmes
		// mFirstAddText.setVisibility(View.GONE);
		// mFirstAddButton.setVisibility(View.GONE);
	}

	@Override
	protected void onPause() {
		super.onPause();

		mManager.removeFriendDataManagerListener(this);

		// Initialize flag
		isNewDataAvailable = false;
		isExistingDataAvailable = false;

		// if (mFriendListData != null) {
		// mFriendListData.clear();
		// }
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mManager != null) {
			mManager.removeFriendDataManagerListener(this);
		}
	}

	private void requestUserData() {
		DbgUtil.showDebug(TAG, "requestUserData");
		try {

			if (!mManager.isListenerAlreadyRegistered(this)) {
				DbgUtil.showDebug(
						TAG,
						"registered: "
								+ mManager.isListenerAlreadyRegistered(this));
				if (mNewUserData != null) {
					mNewUserData.clear();
				}

				if (mUserData != null) {
					mUserData.clear();
				}

				mFriendListData.clear();
				mManager.setFriendDataManagerListener(this);
			}

			mManager.requestFriendListDataset(mUserId, true, true);
		} catch (FriendDataManagerException e) {
			DbgUtil.showDebug(TAG,
					"FriendDataManagerException: " + e.getMessage());
			TrackingUtil.trackExceptionMessage(getApplicationContext(), TAG,
					"FriendDataManagerException: " + e.getMessage());
		}
	}

	@Override
	public void notifyPresentDataset(final ArrayList<FriendListData> userData) {
		DbgUtil.showDebug(TAG, "notifyPresentDataset");

		// If new data is already ready
		if (isNewDataAvailable) {
			if (mNewUserData != null && mNewUserData.size() != 0) {
				for (FriendListData data : mNewUserData) {

					int friendUserId = data.getFriendId();

					// If data for the target user is not in HashMap
					if (mFriendTmpData.get(friendUserId) == null) {
						mFriendTmpData.put(friendUserId, data);
					}
				}
			}

			if (userData != null && userData.size() != 0) {
				for (FriendListData data : userData) {

					int friendUserId = data.getFriendId();

					// If data for the target user is not in HashMap
					if (mFriendTmpData.get(friendUserId) == null) {
						mFriendTmpData.put(friendUserId, data);
					}
				}
			}

			// Put data to list view data
			for (Iterator<?> it = mFriendTmpData.entrySet().iterator(); it
					.hasNext();) {
				Map.Entry entry = (Map.Entry) it.next();
				Integer friendId = (Integer) entry.getKey();
				FriendListData data = (FriendListData) entry.getValue();

				mFriendListData.add(data);
				DbgUtil.showDebug(TAG, "id: " + friendId);
				DbgUtil.showDebug(TAG, "message: " + data.getLastMessage());
			}

			// dismiss progress
			if (mProgressDialog != null && mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			}

			// Initialize flag
			isNewDataAvailable = false;

			// Notify to list view and adapter
			new Thread(new Runnable() {
				@Override
				public void run() {
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							// mFriendListData.addAll(userData);
							// mFriendListData.addAll(userData);
							if (mAdapter != null) {
								mListView.setAdapter(mAdapter);
								mAdapter.notifyDataSetChanged();
							}
						}
					});
				}
			}).start();

			// Show button if necessary
			checkAndShowFirstAddButton();

		} else {
			// If new data is not ready yet, just keep old data
			mUserData = userData;

			// Set flag true
			isExistingDataAvailable = true;
		}
	}

	@Override
	public void notifyNewDataset(ArrayList<FriendListData> newUserData) {
		DbgUtil.showDebug(TAG, "notifyNewDataset");

		// If existing data is already ready
		if (isExistingDataAvailable) {
			if (newUserData != null && newUserData.size() != 0) {
				for (FriendListData data : newUserData) {

					int friendUserId = data.getFriendId();

					// If data for the target user is not in HashMap
					if (mFriendTmpData.get(friendUserId) == null) {
						mFriendTmpData.put(friendUserId, data);
					}
				}
			}

			// Then, try to add old data
			if (mUserData != null && mUserData.size() != 0) {
				for (FriendListData data : mUserData) {

					int friendUserId = data.getFriendId();

					if (mFriendTmpData.get(friendUserId) == null) {
						mFriendTmpData.put(friendUserId, data);
					}
				}
			}

			// Put data to list view data
			for (Iterator<?> it = mFriendTmpData.entrySet().iterator(); it
					.hasNext();) {
				Map.Entry entry = (Map.Entry) it.next();
				Integer friendId = (Integer) entry.getKey();
				FriendListData data = (FriendListData) entry.getValue();

				mFriendListData.add(data);
				DbgUtil.showDebug(TAG, "id: " + friendId);
				DbgUtil.showDebug(TAG, "message: " + data.getLastMessage());
			}

			// dismiss progress
			if (mProgressDialog != null && mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			}

			// Initialize flag
			isExistingDataAvailable = false;

			// Notify to list view and adapter
			new Thread(new Runnable() {
				@Override
				public void run() {
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							// mFriendListData.addAll(userData);
							// mFriendListData.addAll(userData);
							if (mAdapter != null) {
								mListView.setAdapter(mAdapter);
								mAdapter.notifyDataSetChanged();
							}
						}
					});
				}
			}).start();

			// Show button if necessary
			checkAndShowFirstAddButton();

		} else {
			// If existing data is not ready yet, just keep new data
			mNewUserData = newUserData;

			// Set flag true
			isNewDataAvailable = true;
		}

	}

	// @Override
	// public void notifyPresentDataset(final ArrayList<FriendListData>
	// userData) {
	// DbgUtil.showDebug(TAG, "notifyPresentDataset");
	//
	// if (userData != null && userData.size() != 0) {
	// TrackingUtil.trackNumberOfFriend(getApplicationContext(),
	// userData.size());
	// }
	//
	// if (isNewDataAvailable) {
	// DbgUtil.showDebug(TAG, "notifyPresentDataset true");
	// // If new (server) data is already available, initialize data and
	// // dismiss progress
	// if (mProgressDialog != null && mProgressDialog.isShowing()) {
	// mProgressDialog.dismiss();
	// }
	// mUserData = userData;
	// ArrayList<FriendListData> userDatas =
	// mergeNewAndPresentData(mNewUserData);
	//
	// // Initialize
	// mFriendListData.clear();
	//
	// mFriendListData.addAll(userDatas);
	//
	// checkAndShowFirstAddButton();
	//
	// // Initialize flag
	// isNewDataAvailable = false;
	//
	// // If Friend list is not null and not 0
	// if (mFriendListData != null && mFriendListData.size() != 0) {
	// DbgUtil.showDebug(TAG, "mFriendListData size: "
	// + mFriendListData.size());
	// if (mFriendListData != null) {
	//
	// // Add new data
	// new Thread(new Runnable() {
	// @Override
	// public void run() {
	// mHandler.post(new Runnable() {
	// @Override
	// public void run() {
	// // mFriendListData.addAll(userData);
	// // mFriendListData.addAll(userData);
	// if (mAdapter != null) {
	// mListView.setAdapter(mAdapter);
	// mAdapter.notifyDataSetChanged();
	// }
	// }
	// });
	// }
	//
	// }).start();
	// }
	// }
	//
	// } else {
	// DbgUtil.showDebug(TAG, "isNewDataAvailable false");
	// isExistingDataAvailable = true;
	// mUserData = userData;
	// }
	//
	// if (userData != null) {
	// DbgUtil.showDebug(TAG, "useData: " + userData.size());
	// }
	//
	// // If new user data have not arrived
	// // if (mNewUserData == null) {
	// // Just show user data.
	//
	// // }
	// // else {
	// // DbgUtil.showDebug(TAG, "mNewUserData is not null");
	// // // If new user data has already reached
	// // // Show both user data and new user data
	// // // TODO
	// // }
	//
	// }

	// @Override
	// public void notifyNewDataset(ArrayList<FriendListUpdateData> newUserData)
	// {
	// DbgUtil.showDebug(TAG, "notifyNewDataset");
	//
	// // Track the number of new messages
	// if (newUserData != null && newUserData.size() != 0) {
	// TrackingUtil.trackNumberOfNewMessage(getApplicationContext(),
	// newUserData.size());
	// }
	//
	// if (isExistingDataAvailable) {
	// DbgUtil.showDebug(TAG, "isExistingDataAvailable true");
	// // if local (existing) data is already available, Initialize data
	// isExistingDataAvailable = false;
	// if (mProgressDialog != null && mProgressDialog.isShowing()) {
	// DbgUtil.showDebug(TAG, "dismiss dialog");
	// mProgressDialog.dismiss();
	// }
	// mNewUserData = newUserData;
	//
	// // Otherwise (Local data is already available), show New user
	// // data
	// if (newUserData != null && newUserData.size() != 0) {
	// // If we have more than 1 new item
	// // And newUserData has two patterns. One is sender is myself
	// // and
	// // another one is sender is friend (=targetUser is me)
	// DbgUtil.showDebug(TAG,
	// "newUserData size: " + newUserData.size());
	//
	// // Set number of new message (Merge local and sever data)
	// ArrayList<FriendListData> userDatas =
	// mergeNewAndPresentData(newUserData);
	//
	// mFriendListData.clear();
	// mFriendListData.addAll(userDatas);
	//
	// // if (mFriendListData != null && mFriendListData.size() !=
	// // 0) {
	// checkAndShowFirstAddButton();
	// // }
	// } else {
	// DbgUtil.showDebug(TAG, "No new friendData");
	// mFriendListData.clear();
	// mFriendListData.addAll(mUserData);
	// }
	//
	// new Thread(new Runnable() {
	// @Override
	// public void run() {
	// mHandler.post(new Runnable() {
	// @Override
	// public void run() {
	// // mFriendListData.addAll(userData);
	// // mFriendListData.addAll(userData);
	// if (mAdapter != null) {
	// mListView.setAdapter(mAdapter);
	// mAdapter.notifyDataSetChanged();
	// }
	// }
	// });
	// }
	//
	// }).start();
	//
	// checkAndShowFirstAddButton();
	//
	// } else {
	// DbgUtil.showDebug(TAG, "isExistingDataAvailable false");
	// // If local (existing) data is NOT available, set new data as true
	// // and wait for local data
	// isNewDataAvailable = true;
	// mNewUserData = newUserData;
	// }
	// }

	private void checkAndShowFirstAddButton() {
		DbgUtil.showDebug(TAG, "checkAndShowFirstAddButton");

		if (mFriendListData == null || mFriendListData.size() == 0) {
			// If both local and server data is null, show first add text
			// and button
			DbgUtil.showDebug(TAG, "show first add button");
			new Thread(new Runnable() {
				@Override
				public void run() {
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							mFirstAddText.setVisibility(View.VISIBLE);
							mFirstAddButton.setVisibility(View.VISIBLE);
						}
					});
				}
			}).start();
		}
	}

	@SuppressWarnings("unchecked")
	// private ArrayList<FriendListData> mergeNewAndPresentData(
	// ArrayList<FriendListUpdateData> newUserDataArg) {
	// ArrayList<FriendListData> friendDatas = new ArrayList<FriendListData>();
	//
	// // ArrayList for users who doesn't have thumbnail data in local
	// ArrayList<Integer> thumbNotDLUser = new ArrayList<Integer>();
	//
	// if (newUserDataArg != null) {
	// for (FriendListUpdateData data : newUserDataArg) {
	// DbgUtil.showDebug(TAG, "before: " + data.getNewMessageDate());
	// }
	//
	// // Sort by message data in newUserDataArg
	// Collections.sort(newUserDataArg, new FriendListDataComparator());
	//
	// for (FriendListUpdateData data : newUserDataArg) {
	// DbgUtil.showDebug(TAG, "after: " + data.getNewMessageDate());
	// }
	//
	// if (mUserData != null) {
	// DbgUtil.showDebug(TAG, "mUserData size: " + mUserData.size());
	//
	// }
	//
	// for (FriendListData data : mUserData) {
	// int currentSenderId = data.getFriendId();
	// DbgUtil.showDebug(TAG, "currentSenderId: " + currentSenderId);
	//
	// // If local data doesn't have thumbnail data, we need to ask
	// // server to provide it.
	// if (data.getThumbnail() == null) {
	// thumbNotDLUser.add(currentSenderId);
	// }
	//
	// boolean isNewUpdated = false;
	//
	// if (newUserDataArg != null && newUserDataArg.size() != 0) {
	// for (FriendListUpdateData updateData : newUserDataArg) {
	//
	// int updateSenderId = updateData.getNesMassageSenderId();
	// DbgUtil.showDebug(TAG, "updateSenderId: "
	// + updateSenderId);
	//
	// // If the comment is sent by friend (it means )
	// if (currentSenderId == updateSenderId) {
	// isNewUpdated = true;
	// String messageFromServer = updateData
	// .getNewMessage();
	// String messageFromLocal = data.getLastMessage();
	// DbgUtil.showDebug(TAG, "messageFromServer: "
	// + messageFromServer);
	// DbgUtil.showDebug(TAG, "messageFromLocal: "
	// + messageFromLocal);
	// if (messageFromServer != null
	// && messageFromLocal != null
	// && messageFromLocal
	// .contains(messageFromLocal)) {
	// DbgUtil.showDebug(TAG, "messageFromLocal: "
	// + messageFromLocal);
	// // Update lastsender name
	// data.setLastSender(updateData
	// .getNewMessageSenderName());
	//
	// // Update num of new message
	// int numOfMessage = data.getNumOfNewMessage();
	// numOfMessage = numOfMessage + 1;
	// data.setNumOfNewMessage(numOfMessage);
	//
	// // set last message
	// data.setLastMessage(updateData.getNewMessage());
	//
	// // Update user if by using server side
	// String senderName = updateData
	// .getNewMessageSenderName();
	// if (senderName != null
	// && !senderName.equals("null")) {
	// // TODO Need to update DB name in this case
	// // (because
	// // we can get correct user name)
	// data.setFriendName(senderName);
	// }
	//
	// // Set updated info to list data
	// friendDatas.add(data);
	//
	// // Escape from for loop
	// break;
	// }
	// }
	// }
	// }
	//
	// //
	// if (isNewUpdated == false) {
	// DbgUtil.showDebug(TAG, "isNuewUpdated is false");
	// // If there is no same user id data between current and ndw
	// // data, just add (without increasing the number of new
	// // message)
	// friendDatas.add(data);
	// }
	//
	// // Initialize flag
	// isNewUpdated = false;
	// }
	// }
	// HashMap<Integer, FriendListData> tmpData = new HashMap<Integer,
	// FriendListData>();
	// // ArrayList<FriendListData> tmpData = new ArrayList<FriendListData>();
	// FriendListUpdateData latestUpdateData = null;
	//
	// if (newUserDataArg != null && newUserDataArg.size() != 0) {
	// for (FriendListUpdateData updateData : newUserDataArg) {
	//
	// latestUpdateData = updateData;
	// boolean isNew = true;
	// int updateSenderId = updateData.getNesMassageSenderId();
	// DbgUtil.showDebug(TAG, "updateSenderId: " + updateSenderId);
	//
	// // for (FriendListData data : mUserData) {
	// for (FriendListData data : friendDatas) {
	// int currentSenderId = data.getFriendId();
	// DbgUtil.showDebug(TAG, "currentSenderId: "
	// + currentSenderId);
	// if (updateSenderId == currentSenderId) {
	// isNew = false;
	// }
	// }
	//
	// // If new target user data is already in the list data
	// if (isNew == true) {
	// DbgUtil.showDebug(TAG, "isNew is true");
	//
	// // int friendId, String friendName, int lastSenderId,
	// // String lastMessage, int numOfNewMessage, String
	// // mailAddress,
	// // byte[] thumbnail
	//
	// // And sender is myself (it means friend is target, sender
	// // is mine)
	// if (latestUpdateData.getNesMassageSenderId() == PreferenceUtil
	// .getUserId(getApplicationContext())) {
	//
	// // If the data has not been set
	// if (tmpData.get(latestUpdateData
	// .getNesMassageTargetId()) == null) {
	// DbgUtil.showDebug(TAG, "A");
	// DbgUtil.showDebug(TAG, "targetId:: "
	// + latestUpdateData.getNesMassageTargetId());
	// FriendListData newData = new FriendListData(
	// latestUpdateData.getNesMassageTargetId(),
	// latestUpdateData.getNewMessageTargetName(),
	// latestUpdateData.getNesMassageSenderId(),
	// latestUpdateData.getNewMessage(), 1, null,
	// null);
	// tmpData.put(
	// latestUpdateData.getNesMassageTargetId(),
	// newData);
	//
	// } else {
	// // If the data has already been in list
	// DbgUtil.showDebug(TAG, "C");
	// FriendListData newDataTmp = tmpData
	// .get(latestUpdateData
	// .getNesMassageTargetId());
	// DbgUtil.showDebug(TAG, "newDataTmp friendId: "
	// + newDataTmp.getFriendId());
	// tmpData.remove(latestUpdateData
	// .getNesMassageSenderId());
	// int numOfMessage = newDataTmp.getNumOfNewMessage();
	// numOfMessage = numOfMessage + 1;
	// FriendListData newData = new FriendListData(
	// latestUpdateData.getNesMassageTargetId(),
	// latestUpdateData.getNewMessageTargetName(),
	// latestUpdateData.getNesMassageSenderId(),
	// latestUpdateData.getNewMessage(),
	// numOfMessage, null, null);
	// tmpData.put(
	// latestUpdateData.getNesMassageTargetId(),
	// newData);
	// }
	// // If the new message is sent by friend.
	// } else {
	// // If the data has already been set
	// if (tmpData.get(latestUpdateData
	// .getNesMassageSenderId()) == null) {
	// DbgUtil.showDebug(TAG, "B");
	// DbgUtil.showDebug(TAG, "message: "
	// + latestUpdateData.getNewMessage());
	// // if sender is friend (it means friend is sender,
	// // target is mine)
	// FriendListData newData = new FriendListData(
	// latestUpdateData.getNesMassageSenderId(),
	// latestUpdateData.getNewMessageSenderName(),
	// latestUpdateData.getNesMassageSenderId(),
	// latestUpdateData.getNewMessage(), 1, null,
	// null);
	// tmpData.put(
	// latestUpdateData.getNesMassageSenderId(),
	// newData);
	// } else {
	// DbgUtil.showDebug(TAG, "D");
	// DbgUtil.showDebug(TAG, "message: "
	// + latestUpdateData.getNewMessage());
	// // if sender is friend (it means friend is sender,
	// // target is mine)
	// FriendListData newDataTmp = tmpData
	// .get(latestUpdateData
	// .getNesMassageSenderId());
	// tmpData.remove(latestUpdateData
	// .getNesMassageSenderId());
	// int numOfMessage = newDataTmp.getNumOfNewMessage();
	// numOfMessage = numOfMessage + 1;
	// FriendListData newData = new FriendListData(
	// latestUpdateData.getNesMassageSenderId(),
	// latestUpdateData.getNewMessageSenderName(),
	// latestUpdateData.getNesMassageSenderId(),
	// latestUpdateData.getNewMessage(),
	// numOfMessage, null, null);
	// tmpData.put(
	// latestUpdateData.getNesMassageSenderId(),
	// newData);
	// }
	// }
	// DbgUtil.showDebug(TAG, "tmpData size: " + tmpData.size());
	// }
	// // else {
	// // // If new target user data is NOT in the list data
	// // FriendListData registeredData = tmpData.get(updateSenderId);
	// // // And update the number of message in registered data
	// // if (registeredData != null) {
	// // int numOfMessage = registeredData.getNumOfNewMessage();
	// // numOfMessage = numOfMessage + 1;
	// //
	// // tmpData.put(latestUpdateData.getNesMassageSenderId(),
	// // newData);
	// // } else {
	// // DbgUtil.showDebug(TAG, "erro case");
	// // }
	// // }
	// }
	//
	// }
	//
	// // for (FriendListData data : tmpData) {
	// for (Map.Entry<Integer, FriendListData> e : tmpData.entrySet()) {
	// friendDatas.add(e.getValue());
	// }
	//
	// if (newUserDataArg != null) {
	// NewMessageNotification.showNotiofication(getApplicationContext(),
	// 0, newUserDataArg.size());
	// }
	//
	// // ArrayList<Integer> ids = new ArrayList<Integer>();
	//
	// for (FriendListData data : friendDatas) {
	// DbgUtil.showDebug(
	// TAG,
	// "friendId: " + data.getFriendId() + "friendName: "
	// + data.getFriendName() + "message: "
	// + data.getLastMessage() + "lastSender: "
	// + data.getLastSender());
	// }
	//
	// // Get thumbnail data
	// if (thumbNotDLUser != null && thumbNotDLUser.size() != 0) {
	// try {
	// mManager.requestFriendsNewThumbnail(thumbNotDLUser);
	// } catch (FriendDataManagerException e1) {
	// DbgUtil.showDebug(TAG,
	// "FriendDataManagerException: " + e1.getMessage());
	// }
	// }
	//
	// return friendDatas;
	// }
	@Override
	public void notifyAddPresentDataFinished(boolean result,
			MessageItemData messageData) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyPresentMessageDataLoaded(
			ArrayList<MessageItemData> messageData) {
		DbgUtil.showDebug(TAG, "notifyPresentMessageData : not to be used");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.friendlist_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_friendlist_signout:
			PreferenceUtil.removeUserId(getApplicationContext());
			PreferenceUtil.removeUserName(getApplicationContext());
			PreferenceUtil.removePushDeviceId(getApplicationContext());

			TrackingUtil.trackEvent(getApplicationContext(),
					TrackingUtil.EVENT_CATEGORY_FRIEND_LIST,
					TrackingUtil.EVENT_ACTION_FRIEND_LIST_OPTION,
					TrackingUtil.EVENT_LABEL_FRIEND_LIST_SIGN_OUT, 1);

			NewMessageNotification.removeNotification();

			FriendDataManager.removeUserPreferenceData(getApplicationContext(),
					mUserId);

			FriendListActivityUtil.startActivityForWelcomeActivity(this);
			finish();
			return true;
		case R.id.menu_friendlist_add:
			DbgUtil.showDebug(TAG, "menu_friendlist_add");

			TrackingUtil.trackEvent(getApplicationContext(),
					TrackingUtil.EVENT_CATEGORY_FRIEND_LIST,
					TrackingUtil.EVENT_ACTION_FRIEND_LIST_OPTION,
					TrackingUtil.EVENT_LABEL_FRIEND_LIST_ADD_BUTTON, 1);

			FriendListActivityUtil.startActivityForInvitation(mActivity,
					mUserId, mUserName, REQUEST_CODE);

			return true;
		case R.id.menu_friendlist_update:
			DbgUtil.showDebug(TAG, "menu_friendlist_update");
			TrackingUtil.trackEvent(getApplicationContext(),
					TrackingUtil.EVENT_CATEGORY_FRIEND_LIST,
					TrackingUtil.EVENT_ACTION_FRIEND_LIST_OPTION,
					TrackingUtil.EVENT_LABEL_FRIEND_LIST_UPDATE_BUTTON, 1);
			updateFriendList();
			// FriendListActivityUtil.startActivityForInvitation(mActivity,
			// mUserId, mUserName, REQUEST_CODE);
			return true;
		case R.id.menu_friendlist_help:
			DbgUtil.showDebug(TAG, "menu_friendlist_help");
			TrackingUtil.trackEvent(getApplicationContext(),
					TrackingUtil.EVENT_CATEGORY_FRIEND_LIST,
					TrackingUtil.EVENT_ACTION_FRIEND_LIST_OPTION,
					TrackingUtil.EVENT_LABEL_FRIEND_LIST_HELP, 1);
			FriendListActivityUtil
					.startActivityForHelp(getApplicationContext());
			return true;
		}
		return false;
	}

	private void updateFriendList() {
		if (mFriendListData != null) {
			mFriendListData.clear();
		}
		requestUserData();
		mProgressDialog.show(getFragmentManager(), "progress");
	}

	@Override
	public void notifyNewConversationDataLoaded(
			ArrayList<MessageItemData> messageData) {
		DbgUtil.showDebug(TAG,
				"notifyNewConversationDataLoaded - not to be used");

	}

	@Override
	public void notifyFriendThubmailsLoaded(
			List<HashMap<Integer, Bitmap>> thumbnails) {
		DbgUtil.showDebug(TAG, "notifyFriendThubmailsLoaded");
		if (thumbnails != null) {
			handleAndUpdateThumbnailData(thumbnails);
		} else {
			DbgUtil.showDebug(TAG, "thumbnails is null");
		}

	}

	private void handleAndUpdateThumbnailData(
			List<HashMap<Integer, Bitmap>> thumbnails) {
		DbgUtil.showDebug(TAG, "handleAndUpdateThumbnailData");

		if (thumbnails != null) {
			for (HashMap<Integer, Bitmap> thumbnail : thumbnails) {
				for (Iterator<?> it = thumbnail.entrySet().iterator(); it
						.hasNext();) {
					Map.Entry entry = (Map.Entry) it.next();
					Integer friendId = (Integer) entry.getKey();
					Bitmap friendThumb = (Bitmap) entry.getValue();
					// ArrayList<FriendListData> mFriendListData
					if (mFriendListData != null) {
						for (FriendListData data : mFriendListData) {
							int currentId = data.getFriendId();
							if (currentId == friendId) {
								DbgUtil.showDebug(TAG, "currentId: "
										+ currentId);
								data.setThumbnail(friendThumb);
								break;
							}
						}
					}
				}
			}
			new Thread(new Runnable() {
				@Override
				public void run() {
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							if (mAdapter != null) {
								mAdapter.notifyDataSetChanged();
							}
						}
					});
				}

			}).start();
		}
	}

	@Override
	public void onDeviceIdRegistrationFinished(boolean result) {
		DbgUtil.showDebug(TAG, "onDeviceIdRegistrationFinished");
		mProgressDialog.show(getFragmentManager(), "progress");
		requestUserData();
	}
}
