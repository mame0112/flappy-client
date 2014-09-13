package com.mame.flappy.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
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

import com.mame.flappy.LcomBaseActivity;
import com.mame.flappy.R;
import com.mame.flappy.constant.LcomConst;
import com.mame.flappy.data.FriendListData;
import com.mame.flappy.data.MessageItemData;
import com.mame.flappy.data.NotificationContentData;
import com.mame.flappy.datamanager.FriendDataManager;
import com.mame.flappy.datamanager.FriendDataManager.FriendDataManagerListener;
import com.mame.flappy.exception.FriendDataManagerException;
import com.mame.flappy.exception.NewMessageNotificationManagerException;
import com.mame.flappy.notification.NewMessageNotificationManager;
import com.mame.flappy.server.LcomDeviceIdRegisterHelper;
import com.mame.flappy.server.LcomDeviceIdRegisterHelper.LcomPushRegistrationHelperListener;
import com.mame.flappy.ui.dialog.SignoutConfirmationDialog;
import com.mame.flappy.ui.view.FriendListCustomAdapter;
import com.mame.flappy.ui.view.FriendListCustomListView;
import com.mame.flappy.util.DbgUtil;
import com.mame.flappy.util.PreferenceUtil;
import com.mame.flappy.util.TimeUtil;
import com.mame.flappy.util.TrackingUtil;

public class FriendListActivity extends LcomBaseActivity implements
		FriendDataManagerListener, LcomPushRegistrationHelperListener,
		SignoutConfirmationDialog.SignoutConfirmationListener {

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

	private FriendListCustomListView mListView = null;
	// private ListView mListView = null;

	private Button mFirstAddButton = null;

	private TextView mFirstAddText = null;

	private Activity mActivity = null;

	private final int REQUEST_CODE = 1;

	private Handler mHandler = new Handler();

	private boolean isNewDataAvailable = false;

	private boolean isExistingDataAvailable = false;

	/**
	 * Flag to judge whether now loading data or not. True while loading.
	 */
	private boolean isNowLoading = false;

	// private ProgressDialogFragment mProgressDialog = null;

	private LcomDeviceIdRegisterHelper mHelper = null;

	private ProgressDialogFragmentHelper mProgressHelper = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		DbgUtil.showDebug(TAG, "onCreate");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.friendlist);

		Intent intent = getIntent();
		if (intent != null) {
			mUserId = intent.getIntExtra(LcomConst.EXTRA_USER_ID,
					LcomConst.NO_USER);
			mUserName = intent.getStringExtra(LcomConst.EXTRA_USER_NAME);
		}

		mActivity = this;

		// mProgressDialog = ProgressDialogFragment.newInstance(
		// getString(R.string.str_friendlist_progress_title),
		// getString(R.string.str_friendlist_progress_desc));

		mHelper = new LcomDeviceIdRegisterHelper(this);
		mHelper.setPushRegistrationListener(this);

		FriendDataManager.initializeFriendDataManager(mUserId,
				getApplicationContext());
		mManager = FriendDataManager.getInstance();

		mManager.setFriendDataManagerListener(FriendListActivity.this);

		// mAdapter = new FriendListCustomAdapter(getApplicationContext(), 0,
		// mFriendListData);
		mAdapter = new FriendListCustomAdapter(getApplicationContext(),
				mFriendListData);

		mNewUserData = new ArrayList<FriendListData>();

		mListView = (FriendListCustomListView) findViewById(R.id.friendListView);
		// mListView = (ListView) findViewById(R.id.friendListView);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				DbgUtil.showDebug(TAG, "onItemClick positon: " + position);
				FriendListData data = (FriendListData) mAdapter
						.getItem(position);
				DbgUtil.showDebug(
						TAG,
						"id: " + data.getFriendId() + " name: "
								+ data.getFriendName() + "last message: "
								+ data.getLastMessage() + " mail address: "
								+ data.getMailAddress());
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

		mProgressHelper = new ProgressDialogFragmentHelper();

	}

	private void checkGPSAndRequestUserData() {
		// Try to get device Id for push message
		if (!mHelper.isDeviceIdAvailable(getApplicationContext())) {
			mHelper.checkGPSAndAndRegisterDeviceId(mActivity, mUserId);
		} else {
			// If Google play service and Device id is ready, we try to get
			// actual data.
			requestUserData();

			// TODO
			// if (mProgressDialog != null) {
			// mProgressDialog.show(getFragmentManager(), "progress");
			// }
			mProgressHelper.showProgressDialog(this,
					getString(R.string.str_friendlist_progress_title),
					getString(R.string.str_friendlist_progress_desc), TAG);
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
				// Because we call requestUseData() when FriendList's onStart()
				// is
				// called, then need not to call here.
				// requestUserData();
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
	public void onResume() {
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

		isNowLoading = false;
		invalidateOptionsMenu();

		IntentFilter filter = new IntentFilter(
				LcomConst.ACTION_PUSH_NOTIFICATION);
		filter.addAction(LcomConst.ACTION_MESSAGE_EXPIRE);
		mPushReceiver = new FriendListBroadcastReceiver();
		registerReceiver(mPushReceiver, filter);
	}

	@Override
	protected void onPause() {
		super.onPause();

		// mManager.removeFriendDataManagerListener(this);

		// To avoid showing more than 2 dialog, we try to dismiss dialog
		// if (!mActivity.isFinishing() && mProgressDialog != null) {
		// mProgressDialog.dismiss();
		// }
		mProgressHelper.dismissDialog(this, TAG);

		if (mManager != null) {
			mManager.interruptOperation();
		}

		// Initialize flag
		isNewDataAvailable = false;
		isExistingDataAvailable = false;

		isNowLoading = false;
		invalidateOptionsMenu();

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

			// Initialize flag before requesting data
			isNewDataAvailable = false;
			isExistingDataAvailable = false;

			isNowLoading = true;

			new Thread(new Runnable() {
				@Override
				public void run() {
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							invalidateOptionsMenu();
						}
					});
				}
			}).start();

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
			// ArrayList<Integer> targetUserIds = new ArrayList<Integer>();
			final ArrayList<FriendListData> friendListData = new ArrayList<FriendListData>();

			// Put data to list view data
			for (Iterator<?> it = mFriendTmpData.entrySet().iterator(); it
					.hasNext();) {
				Map.Entry entry = (Map.Entry) it.next();
				Integer friendId = (Integer) entry.getKey();
				FriendListData data = (FriendListData) entry.getValue();

				friendListData.add(data);

				// mFriendListData.add(data);

				// TODO We need to check if thumbnail is available in local
				// before accessing server
				// DbgUtil.showDebug(TAG, "friendId: " + friendId);
				// targetUserIds.add(friendId);
				// DbgUtil.showDebug(TAG, "id: " + friendId);
				// DbgUtil.showDebug(TAG, "message: " + data.getLastMessage());
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
								mFriendListData.addAll(friendListData);
								mListView.setAdapter(mAdapter);
								mAdapter.notifyDataSetChanged();
							}
						}
					});
				}
			}).start();

			// dismiss progress
			mProgressHelper.dismissDialog(this, TAG);
			// if (!mActivity.isFinishing() && mProgressDialog != null) {
			// mProgressDialog.dismiss();
			// }

			// Debug
			// if (targetUserIds != null) {
			// for (int id : targetUserIds) {
			// DbgUtil.showDebug(TAG, "id::: " + id);
			// }
			// }

			try {
				mManager.requestFriendsNewThumbnail();
			} catch (FriendDataManagerException e) {
				DbgUtil.showDebug(TAG,
						"FriendDataManagerException: " + e.getMessage());
			}

			// Show button if necessary
			checkAndShowFirstAddButton();

			// Handle notification
			// if (mNewUserData != null && mNewUserData.size() != 0) {
			// DbgUtil.showDebug(TAG, "mNewUserdata is null or 0");
			// handleNotification(mNewUserData);
			// }

			isNowLoading = false;
			new Thread(new Runnable() {
				@Override
				public void run() {
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							invalidateOptionsMenu();
						}
					});
				}
			}).start();

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

			mNewUserData = newUserData;

			// Initialize
			if (mFriendTmpData != null) {
				mFriendTmpData.clear();
			}

			if (mFriendListData != null) {
				mFriendListData.clear();
			}

			if (newUserData != null && newUserData.size() != 0) {
				for (FriendListData data : newUserData) {

					int friendUserId = data.getFriendId();

					// If data for the target user is not in HashMap
					if (mFriendTmpData.get(friendUserId) == null) {
						mFriendTmpData.put(friendUserId, data);
					} else {
						// Update the number of new message
						FriendListData d = mFriendTmpData.get(friendUserId);
						int numOfNewMessage = d.getNumOfNewMessage();
						int newNumOfMsg = numOfNewMessage + 1;
						d.setNumOfNewMessage(newNumOfMsg);
						mFriendTmpData.put(friendUserId, d);
					}
				}
			}

			// Then, try to add old data
			if (mUserData != null && mUserData.size() != 0) {
				for (FriendListData data : mUserData) {

					int friendUserId = data.getFriendId();

					FriendListData registeredData = mFriendTmpData
							.get(friendUserId);
					if (registeredData == null) {
						mFriendTmpData.put(friendUserId, data);
					} else {
						// If new data is already in tmpData, we try to put
						// thumbnail.

						// First, we check if local data has thumbnail data
						if (data.getThumbnail() != null) {
							registeredData.setThumbnail(data.getThumbnail());
							mFriendTmpData.put(friendUserId, registeredData);
						}
					}
				}
			}

			// Id list for getting thumbnail
			// ArrayList<Integer> targetUserIds = new ArrayList<Integer>();
			final ArrayList<FriendListData> friendListData = new ArrayList<FriendListData>();

			// Put data to list view data
			for (Iterator<?> it = mFriendTmpData.entrySet().iterator(); it
					.hasNext();) {
				Map.Entry entry = (Map.Entry) it.next();
				Integer friendId = (Integer) entry.getKey();
				FriendListData data = (FriendListData) entry.getValue();

				// TODO We need to check if thumbnail is available in local
				// before accessing server
				// DbgUtil.showDebug(TAG, "friendId: " + friendId);
				// if (data != null) {
				// Bitmap bm = data.getThumbnail();
				// // In case bitmap is null (it means bitmap is null in
				// // local), we need to request thumbnail data
				// if (bm == null) {
				// targetUserIds.add(friendId);
				// }
				// }
				friendListData.add(data);

				// mFriendListData.add(data);
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
								mFriendListData.addAll(friendListData);
								mListView.setAdapter(mAdapter);
								mAdapter.notifyDataSetChanged();
							}
						}
					});
				}
			}).start();

			// dismiss progress

			mProgressHelper.dismissDialog(this, TAG);
			// if (!mActivity.isFinishing() && mProgressDialog != null) {
			// mProgressDialog.dismiss();
			// }

			try {
				mManager.requestFriendsNewThumbnail();
			} catch (FriendDataManagerException e) {
				DbgUtil.showDebug(TAG,
						"FriendDataManagerException: " + e.getMessage());
			}

			// Show button if necessary
			checkAndShowFirstAddButton();

			// Handle notification
			// if (mNewUserData != null && mNewUserData.size() != 0) {
			// DbgUtil.showDebug(TAG,
			// "newUserData size: " + mNewUserData.size());
			// handleNotification(mNewUserData);
			// }

			isNowLoading = false;
			invalidateOptionsMenu();

		} else {
			// If existing data is not ready yet, just keep new data
			if (mNewUserData != null) {
				mNewUserData.clear();
			}

			mNewUserData = newUserData;

			if (mNewUserData != null) {
				DbgUtil.showDebug(TAG, "mNewUserData: " + mNewUserData.size());
			} else {
				DbgUtil.showDebug(TAG, "mNewUserData is null");
			}

			// Set flag true
			isNewDataAvailable = true;
		}

	}

	// private void handleNotification(ArrayList<FriendListData> newUserData) {
	// DbgUtil.showDebug(TAG, "handleNotification");
	//
	// // Before get notification, we check latest message date
	// ArrayList<NotificationContentData> notifications = FriendListActivityUtil
	// .getNotificationDate(newUserData, mUserId);
	//
	// // Show Notification if necessary
	// if (notifications != null && notifications.size() != 0) {
	// try {
	// NewMessageNotificationManager
	// .handleLastetMessagesAndShowNotification(
	// getApplicationContext(), notifications);
	// } catch (NewMessageNotificationManagerException e) {
	// DbgUtil.showDebug(
	// TAG,
	// "NewMessageNotificationManagerException: "
	// + e.getMessage());
	// }
	// }
	// }

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
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem refreshButton = menu.findItem(R.id.menu_friendlist_update);

		// Disable refresh button while loading item
		if (isNowLoading) {
			DbgUtil.showDebug(TAG, "disable");
			refreshButton.setEnabled(false);
		} else {
			refreshButton.setEnabled(true);
			DbgUtil.showDebug(TAG, "enable");
		}
		super.onPrepareOptionsMenu(menu);
		return true;
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

			SignoutConfirmationDialog dialog = new SignoutConfirmationDialog();
			dialog.show(getFragmentManager(), "SignoutConfirmation");
			dialog.setSignoutConfirmationListener(this);

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

	private void removeAllUserData() {
		DbgUtil.showDebug(TAG, "removeAllUserData");
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
	}

	private void updateFriendList() {
		if (mFriendListData != null) {
			mFriendListData.clear();
		}
		requestUserData();
		mProgressHelper.showProgressDialog(this,
				getString(R.string.str_friendlist_progress_title),
				getString(R.string.str_friendlist_progress_desc), TAG);
		// TODO
		// if (!mActivity.isFinishing() && mProgressDialog != null) {
		// mProgressDialog.show(getFragmentManager(), "progress");
		// }
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
		DbgUtil.showDebug(TAG, "notifyFriendThumbnailsLoaded");
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
		// TODO
		// if (!mActivity.isFinishing() && mProgressDialog != null) {
		// mProgressDialog.show(getFragmentManager(), "progress");
		// }
		mProgressHelper.showProgressDialog(this,
				getString(R.string.str_friendlist_progress_title),
				getString(R.string.str_friendlist_progress_desc), TAG);
		requestUserData();
	}

	@Override
	public void notifyLatestStoredMessage(FriendListData input) {
		DbgUtil.showDebug(TAG, "notifyLatestStoredMessage");
		if (input != null) {
			String message = input.getLastMessage();
			int friendId = input.getFriendId();
			// DbgUtil.showDebug(TAG, "message: " + message);
			if (message != null && friendId != LcomConst.NO_USER) {
				// If friendListData exist
				if (mFriendListData != null && mFriendListData.size() != 0) {
					for (FriendListData data : mFriendListData) {
						int currentFriendId = data.getFriendId();
						int numOfMessage = input.getNumOfNewMessage();
						if (friendId == currentFriendId) {
							data.setLastMessage(message);
							data.setNumOfNewMessage(numOfMessage);
						}
					}
				} else {
					// If no friendListData exist
					if (mFriendListData == null) {
						mFriendListData = new ArrayList<FriendListData>();
						mFriendListData.add(input);
					} else {
						mFriendListData.add(input);
					}
				}

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
			}
		}
	}

	private void revertLatestMessage(int targetUserId) {
		DbgUtil.showDebug(TAG, "revertLatestMessage: " + targetUserId);

		long current = TimeUtil.getCurrentDate();
		DbgUtil.showDebug(TAG, "currente:" + current);

		if (mFriendListData != null && mFriendListData.size() != 0) {
			for (FriendListData data : mFriendListData) {
				if (data != null) {
					int targetId = data.getFriendId();
					DbgUtil.showDebug(TAG, "targetId: " + targetId);
					DbgUtil.showDebug(TAG, "targetUserId: " + targetUserId);
					// Update target user Id list item data.
					if (targetId == targetUserId) {
						DbgUtil.showDebug(TAG, "Disappear");
						data.setLastMessage(getString(R.string.str_friendlist_message_disappeared));

						// Update num of message
						int numOfMessage = data.getNumOfNewMessage();
						numOfMessage = numOfMessage - 1;
						if (numOfMessage <= 0) {
							DbgUtil.showDebug(TAG, "numOfMessage: "
									+ numOfMessage);
							data.setNumOfNewMessage(numOfMessage);
						} else {
							DbgUtil.showDebug(TAG, "numOfMessage is 0");
							data.setNumOfNewMessage(numOfMessage);
						}

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

					}

				}
			}

		} else {
			DbgUtil.showDebug(TAG, "mFriendListData is null or size 0");
			getLatestLocalStoredMessage(targetUserId);
		}

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

	}

	private void getLatestLocalStoredMessage(int targetUserId) {
		try {
			mManager.requestLatestStoredMessage(targetUserId);
		} catch (FriendDataManagerException e) {
			DbgUtil.showDebug(TAG,
					"FriendDataManagerException: " + e.getMessage());
		}
	}

	public class FriendListBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			DbgUtil.showDebug(TAG, "onReceive");
			if (intent != null) {
				String action = intent.getAction();
				if (action != null) {
					if (action.equals(LcomConst.ACTION_PUSH_NOTIFICATION)) {
						DbgUtil.showDebug(TAG, "PUSH_NOTIFICATION_IDENTIFIER");
						checkGPSAndRequestUserData();
					} else if (action.equals(LcomConst.ACTION_MESSAGE_EXPIRE)) {
						DbgUtil.showDebug(TAG, "ACTION_MESSAGE_EXPIRE");

						int fromUserId = intent.getIntExtra(
								LcomConst.EXTRA_USER_ID, LcomConst.NO_USER);
						int targetUserId = intent.getIntExtra(
								LcomConst.EXTRA_TARGET_USER_ID,
								LcomConst.NO_USER);
						if (fromUserId != LcomConst.NO_USER) {

							// Get back to previous recieved message or latest
							// stored message
							revertLatestMessage(fromUserId);
						}

					}
				}
			}

		}
	}

	// @Override
	// public void notifiyNearlestExpireNotification(NotificationContentData
	// data) {
	// DbgUtil.showDebug(TAG, "notifiyNearlestExpireNotification");
	//
	// }

	@Override
	public void onSignoutConfirmationSelected(boolean isAccepted) {
		DbgUtil.showDebug(TAG, "onSignoutConfirmationSelected");

		if (isAccepted) {
			removeAllUserData();
		}

	}

	@Override
	public void notifyValidNotificationList(
			ArrayList<NotificationContentData> notifications) {
		// TODO Auto-generated method stub

	}

	// private void showProgressDialog() {
	// FragmentTransaction ft = getFragmentManager().beginTransaction();
	// Fragment prev = getFragmentManager().findFragmentByTag("progress");
	// if (prev != null) {
	// ft.remove(prev);
	// }
	// ft.addToBackStack(null);
	//
	// // Create and show the dialog.
	// Fragment newFragment = ProgressDialogFragment.newInstance(
	// getString(R.string.str_friendlist_progress_title),
	// getString(R.string.str_friendlist_progress_desc));
	// ((DialogFragment) newFragment).show(ft, "progress");
	// }
	//
	// private void dismissDialog() {
	// Fragment prev = getFragmentManager().findFragmentByTag("progress");
	// if (prev != null) {
	// // if (prev instanceof ProgressDialogFragment) {
	// ((ProgressDialogFragment) prev).dismiss();
	// // }
	// }
	// }
}
