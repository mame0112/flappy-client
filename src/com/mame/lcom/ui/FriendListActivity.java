package com.mame.lcom.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.mame.lcom.exception.NewMessageNotificationManagerException;
import com.mame.lcom.notification.NewMessageNotification;
import com.mame.lcom.notification.NewMessageNotificationManager;
import com.mame.lcom.server.LcomDeviceIdRegisterHelper;
import com.mame.lcom.server.LcomDeviceIdRegisterHelper.LcomPushRegistrationHelperListener;
import com.mame.lcom.ui.ConversationActivity.ConversationBroadcastReceiver;
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

	private FriendListBroadcastReceiver mPushReceiver = null;

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

		FriendDataManager.initializeFriendDataManager(mUserId,
				getApplicationContext());
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

	}

	private void checkGPSAndRequestUserData() {
		// Try to get device Id for push message
		if (!mHelper.isDeviceIdAvailable(getApplicationContext())) {
			mHelper.checkGPSAndAndRegisterDeviceId(mActivity, mUserId);
		} else {
			// If Google play service and Device id is ready, we try to get
			// actual data.
			requestUserData();

			if (mProgressDialog != null) {
				mProgressDialog.show(getFragmentManager(), "progress");
			}

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

		checkGPSAndRequestUserData();

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

		IntentFilter filter = new IntentFilter(
				LcomConst.PUSH_NOTIFICATION_IDENTIFIER);
		mPushReceiver = new FriendListBroadcastReceiver();
		registerReceiver(mPushReceiver, filter);
	}

	@Override
	protected void onPause() {
		super.onPause();

		mManager.removeFriendDataManagerListener(this);

		// To avoid showing more than 2 dialog, we try to dismiss dialog
		if (!mActivity.isFinishing() && mProgressDialog != null
				&& mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}

		// Initialize flag
		isNewDataAvailable = false;
		isExistingDataAvailable = false;

		unregisterReceiver(mPushReceiver);
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

				// Clear data
				if (mFriendListData != null) {
					mFriendListData.clear();
				}

				if (mAdapter != null) {
					mAdapter.notifyDataSetChanged();
				}

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

			// Initialize
			if (mFriendTmpData != null) {
				mFriendTmpData.clear();
			}

			if (mFriendListData != null) {
				mFriendListData.clear();
			}

			if (mAdapter != null) {
				mAdapter.notifyDataSetChanged();
			}

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

			// Debug
			if (mFriendTmpData != null) {
				DbgUtil.showDebug(TAG,
						"mFriendTmpData: " + mFriendTmpData.size());
			} else {
				DbgUtil.showDebug(TAG, "mFriendTmpData is null");
			}

			// Id list for getting thumbnail
			ArrayList<Integer> targetUserIds = new ArrayList<Integer>();

			// Put data to list view data
			for (Iterator<?> it = mFriendTmpData.entrySet().iterator(); it
					.hasNext();) {
				Map.Entry entry = (Map.Entry) it.next();
				Integer friendId = (Integer) entry.getKey();
				FriendListData data = (FriendListData) entry.getValue();

				mFriendListData.add(data);

				// TODO We need to check if thumbnail is available in local
				// before accessing server
				DbgUtil.showDebug(TAG, "friendId: " + friendId);
				targetUserIds.add(friendId);

				DbgUtil.showDebug(TAG, "id: " + friendId);
				DbgUtil.showDebug(TAG, "message: " + data.getLastMessage());
			}

			// dismiss progress
			if (!mActivity.isFinishing() && mProgressDialog != null
					&& mProgressDialog.isShowing()) {
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

			// Debug
			if (targetUserIds != null) {
				for (int id : targetUserIds) {
					DbgUtil.showDebug(TAG, "id::: " + id);
				}
			}

			try {
				mManager.requestFriendsNewThumbnail(targetUserIds);
			} catch (FriendDataManagerException e) {
				DbgUtil.showDebug(TAG,
						"FriendDataManagerException: " + e.getMessage());
			}

			// Show button if necessary
			checkAndShowFirstAddButton();

			// Handle notification
			handleNotification();

		} else {
			// If new data is not ready yet, just keep old data
			if (mUserData != null) {
				mUserData.clear();
			}

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

			// Initialize
			if (mFriendTmpData != null) {
				mFriendTmpData.clear();
			}

			if (mFriendListData != null) {
				mFriendListData.clear();
			}

			if (mAdapter != null) {
				mAdapter.notifyDataSetChanged();
			}

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

			// Id list for getting thumbnail
			ArrayList<Integer> targetUserIds = new ArrayList<Integer>();

			// Put data to list view data
			for (Iterator<?> it = mFriendTmpData.entrySet().iterator(); it
					.hasNext();) {
				Map.Entry entry = (Map.Entry) it.next();
				Integer friendId = (Integer) entry.getKey();
				FriendListData data = (FriendListData) entry.getValue();

				// TODO We need to check if thumbnail is available in local
				// before accessing server
				DbgUtil.showDebug(TAG, "friendId: " + friendId);
				targetUserIds.add(friendId);

				mFriendListData.add(data);
				DbgUtil.showDebug(TAG, "id: " + friendId);
				DbgUtil.showDebug(TAG, "message: " + data.getLastMessage());
			}

			// dismiss progress
			if (!mActivity.isFinishing() && mProgressDialog != null
					&& mProgressDialog.isShowing()) {
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

			try {
				mManager.requestFriendsNewThumbnail(targetUserIds);
			} catch (FriendDataManagerException e) {
				DbgUtil.showDebug(TAG,
						"FriendDataManagerException: " + e.getMessage());
			}

			// Show button if necessary
			checkAndShowFirstAddButton();

			// Handle notification
			handleNotification();

		} else {
			// If existing data is not ready yet, just keep new data
			if (mNewUserData != null) {
				mNewUserData.clear();
			}

			mNewUserData = newUserData;

			// Set flag true
			isNewDataAvailable = true;
		}

	}

	private void handleNotification() {
		DbgUtil.showDebug(TAG, "handleNotification");
		// Before get notification, we check latest message date
		long lastMessageDate = FriendListActivityUtil
				.getLatestMessageDate(mFriendListData);

		// Show Notification if necessary
		try {
			NewMessageNotificationManager
					.handleLastetMessageAndShowNotification(
							getApplicationContext(), Integer.valueOf(mUserId),
							LcomConst.NO_USER, lastMessageDate);
		} catch (NewMessageNotificationManagerException e) {
			DbgUtil.showDebug(TAG, "NewMessageNotificationManagerException: "
					+ e.getMessage());
		}
	}

	private void checkAndShowFirstAddButton() {
		DbgUtil.showDebug(TAG, "checkAndShowFirstAddButton");

		new Thread(new Runnable() {
			@Override
			public void run() {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						if (mFriendListData == null
								|| mFriendListData.size() == 0) {
							// If both local and server data is null, show first
							// add text and button
							DbgUtil.showDebug(TAG, "show first add button");
							mFirstAddText.setVisibility(View.VISIBLE);
							mFirstAddButton.setVisibility(View.VISIBLE);
						} else {
							mFirstAddText.setVisibility(View.GONE);
							mFirstAddButton.setVisibility(View.GONE);
						}

					}
				});
			}
		}).start();

	}

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
			// Clear all preference data
			PreferenceUtil.removeAllPreferenceData(getApplicationContext());

			TrackingUtil.trackEvent(getApplicationContext(),
					TrackingUtil.EVENT_CATEGORY_FRIEND_LIST,
					TrackingUtil.EVENT_ACTION_FRIEND_LIST_OPTION,
					TrackingUtil.EVENT_LABEL_FRIEND_LIST_SIGN_OUT, 1);

			NewMessageNotificationManager.removeNotification();

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

	public class FriendListBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			DbgUtil.showDebug(TAG, "onReceive");
			checkGPSAndRequestUserData();
		}
	}
}
