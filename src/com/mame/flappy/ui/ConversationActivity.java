package com.mame.flappy.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mame.flappy.LcomBaseActivity;
import com.mame.flappy.R;
import com.mame.flappy.constant.LcomConst;
import com.mame.flappy.data.ConversationDataComparator;
import com.mame.flappy.data.FriendListData;
import com.mame.flappy.data.FriendListDataComparator;
import com.mame.flappy.data.FriendListUpdateData;
import com.mame.flappy.data.MessageItemData;
import com.mame.flappy.data.NotificationContentData;
import com.mame.flappy.datamanager.FriendDataManager;
import com.mame.flappy.datamanager.FriendDataManager.FriendDataManagerListener;
import com.mame.flappy.exception.FriendDataManagerException;
import com.mame.flappy.ui.view.ConversationCustomListView;
import com.mame.flappy.ui.view.ConversationCustomListView.ConversationListScrollListener;
import com.mame.flappy.util.ButtonUtil;
import com.mame.flappy.util.DbgUtil;
import com.mame.flappy.util.FeedbackUtil;
import com.mame.flappy.util.HttpClientUtil;
import com.mame.flappy.util.NetworkUtil;
import com.mame.flappy.util.PreferenceUtil;
import com.mame.flappy.util.StringUtil;
import com.mame.flappy.util.TimeUtil;
import com.mame.flappy.util.TrackingUtil;
import com.mame.flappy.web.LcomHttpWebAPI;
import com.mame.flappy.web.LcomHttpWebAPI.LcomWebAPIListener;

public class ConversationActivity extends LcomBaseActivity implements
		FriendDataManagerListener, ConversationListScrollListener {

	private final String TAG = LcomConst.TAG + "/ConversationActivity";

	private int mUserId = LcomConst.NO_USER;

	private String mUserName = null;

	private String mMailAddress = null;

	private int mTargetUserId = LcomConst.NO_USER;

	private String mTargetUserName = null;

	private FriendDataManager mManager = null;

	private EditText mConversationEditText = null;

	private Button mConversationSendButton = null;

	private Handler mHandler = new Handler();

	private ArrayList<MessageItemData> mConversationData = new ArrayList<MessageItemData>();

	private ConversationListCustonAdapter mAdapter = null;

	private ConversationCustomListView mListView = null;

	private boolean mIsPresentDataReady = false;

	private boolean mIsNewDataReady = false;

	private String mPageNum = null;

	private Bitmap mThumbnail = null;

	private Activity mActivity = null;

	private ConversationBroadcastReceiver mPushReceiver = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DbgUtil.showDebug(TAG, "onCreate");

		getWindow().setSoftInputMode(
				LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		setContentView(R.layout.conversation);
		Intent intent = getIntent();
		if (intent != null) {
			mUserId = intent.getIntExtra(LcomConst.EXTRA_USER_ID,
					LcomConst.NO_USER);
			mUserName = intent.getStringExtra(LcomConst.EXTRA_USER_NAME);
			mTargetUserId = intent.getIntExtra(LcomConst.EXTRA_TARGET_USER_ID,
					LcomConst.NO_USER);
			mTargetUserName = intent
					.getStringExtra(LcomConst.EXTRA_TARGET_USER_NAME);
			mThumbnail = intent.getParcelableExtra(LcomConst.EXTRA_THUMBNAIL);
			mMailAddress = intent
					.getStringExtra(LcomConst.EXTRA_TARGET_MAIL_ADDRESS);

			if (mThumbnail != null) {
				DbgUtil.showDebug(TAG,
						"thumbnail size: " + mThumbnail.getWidth() + " / "
								+ mThumbnail.getHeight());
			}

			if (mTargetUserName != null && !mTargetUserName.equals("null")) {
				DbgUtil.showDebug(TAG, "mTargetUserName: " + mTargetUserName);

				// Set target user name as activity title
				setTitle(mTargetUserName);
			} else {
				// If target user name is null (could be after invite friend)
				// Try to show mail address instead of target user name
				if (mMailAddress != null && !mMailAddress.equals("null")) {
					setTitle(mMailAddress);
				} else {
					// If mail addrss is null as well
					setTitle(R.string.str_conversation_title);
				}
			}

			DbgUtil.showDebug(TAG, "mTargetUserId: " + mTargetUserId);
		}

		mUserId = PreferenceUtil.getUserId(getApplicationContext());
		mUserName = PreferenceUtil.getUserName(getApplicationContext());

		ActionBar actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);

		mActivity = this;

		mAdapter = new ConversationListCustonAdapter(getApplicationContext(),
				0, mConversationData);
		mAdapter.setFriendThumbnail(mThumbnail);

		mListView = (ConversationCustomListView) findViewById(R.id.conversationListView);
		mListView.setAdapter(mAdapter);
		mListView.setListener(this);

		FriendDataManager.initializeFriendDataManager(mUserId,
				getApplicationContext());
		mManager = FriendDataManager.getInstance();
		mManager.setFriendDataManagerListener(this);

		mConversationEditText = (EditText) findViewById(R.id.conversationEditText);

		// Set width of EditText.
		mConversationEditText.setWidth(mConversationEditText.getWidth());
		mConversationEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s.length() >= 1) {
					mConversationSendButton.setEnabled(true);
				} else {
					mConversationSendButton.setEnabled(false);
				}
			}
		});

		mConversationSendButton = (Button) findViewById(R.id.conversationSendButton);
		mConversationSendButton.setEnabled(false);
		mConversationSendButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				DbgUtil.showDebug(TAG, "onClick");

				if (ButtonUtil.isClickable()) {
					TrackingUtil.trackEvent(getApplicationContext(),
							TrackingUtil.EVENT_CATEGORY_CONVERSATION,
							TrackingUtil.EVENT_ACTION_CONVERSATION,
							TrackingUtil.EVENT_LABEL_CONVERSATION_SEND_BUTTON,
							1);

					if (NetworkUtil.isNetworkAvailable(mActivity, mHandler)) {
						SpannableStringBuilder sbMessage = (SpannableStringBuilder) mConversationEditText
								.getText();
						String message = sbMessage.toString();
						if (message != null) {
							if (!StringUtil
									.isContainPreservedCharacters(message)) {
								// Track the number of texts in one message
								TrackingUtil.trackNumberOfCharInOneMessage(
										getApplicationContext(),
										message.length());

								setProgressBarIndeterminateVisibility(true);

								mConversationSendButton.setEnabled(false);
								mConversationEditText.setEnabled(false);

								long date = TimeUtil.getCurrentDate();
								sendAndRegisterMessage(mUserId, mTargetUserId,
										mUserName, mTargetUserName, message,
										String.valueOf(date));
							} else {
								Toast.makeText(
										getApplicationContext(),
										R.string.str_generic_preserved_char_error,
										Toast.LENGTH_SHORT).show();
							}
						} else {
							Toast.makeText(getApplicationContext(),
									R.string.str_conversation_no_text_input,
									Toast.LENGTH_SHORT).show();
						}
					}
				}
			}
		});

		mPageNum = getString(R.string.str_conversation_message_page, 1);

		requestThreadData();

	}

	private void requestThreadData() {
		try {
			setProgressBarIndeterminateVisibility(true);

			// Initialize flag
			mIsNewDataReady = false;
			mIsPresentDataReady = false;

			// setProgressBarIndeterminateVisibility(true);

			mManager.requestMessageListDatasetWithTargetUser(mUserId,
					mTargetUserId, true, true);
		} catch (FriendDataManagerException e) {
			setProgressBarIndeterminateVisibility(false);
			DbgUtil.showDebug(TAG,
					"FriendDataManagerException: " + e.getMessage());
			TrackingUtil.trackExceptionMessage(getApplicationContext(), TAG,
					"FriendDataManagerException: " + e.getMessage());
		}

	}

	@Override
	protected void onResume() {
		super.onResume();

		IntentFilter filter = new IntentFilter(
				LcomConst.ACTION_PUSH_NOTIFICATION);
		mPushReceiver = new ConversationBroadcastReceiver();
		registerReceiver(mPushReceiver, filter);

		// Show most bottom item in ListView
		DbgUtil.showDebug(TAG, "count: " + mListView.getCount());
		mListView.setSelection(mListView.getCount() - 1);

		mListView.listViewUIReady();

	}

	@Override
	protected void onPause() {
		super.onPause();

		setProgressBarIndeterminateVisibility(false);

		if (mManager != null) {
			mManager.interruptOperation();
		}

		unregisterReceiver(mPushReceiver);
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

	private void sendAndRegisterMessage(int userId, int targetUserId,
			String userName, String targetUserName, String message, String date) {
		DbgUtil.showDebug(TAG, "sendAndRegisterMessage");

		DbgUtil.showDebug(TAG, "userId: " + userId);
		DbgUtil.showDebug(TAG, "targetUserId: " + targetUserId);
		DbgUtil.showDebug(TAG, "userName: " + userName);
		DbgUtil.showDebug(TAG, "targetuserId: " + targetUserId);
		DbgUtil.showDebug(TAG, "targetUserName: " + targetUserName);
		DbgUtil.showDebug(TAG, "message: " + message);

		// Store data to local
		ArrayList<String> parsedMessage = ConversationActivityUtil
				.parseMessageBasedOnMaxLength(getApplicationContext(), message);

		if (LcomConst.IS_DEBUG) {
			for (String msg : parsedMessage) {
				DbgUtil.showDebug(TAG, "msg: " + msg);
			}
		}

		try {
			mManager.sendAndRegisterMessage(userId, targetUserId, userName,
					targetUserName, parsedMessage, date);
		} catch (FriendDataManagerException e) {
			DbgUtil.showDebug(TAG,
					"FriendDataManagerException: " + e.getMessage());
			TrackingUtil.trackExceptionMessage(getApplicationContext(), TAG,
					"FriendDataManagerException: " + e.getMessage());
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mManager != null) {
			mManager.removeFriendDataManagerListener(this);
			// mManager.destroyFriendDataManager();
		}
	}

	@Override
	public void notifyPresentDataset(ArrayList<FriendListData> userData) {
		DbgUtil.showDebug(TAG, "notifyPresentDataset");
		if (userData != null) {
			DbgUtil.showDebug(TAG, "userData size: " + userData.size());
		}

	}

	@Override
	public void notifyNewDataset(ArrayList<FriendListData> newUserData) {
		DbgUtil.showDebug(TAG, "notifyNewDataset");
		if (newUserData != null && newUserData.size() != 0) {
			DbgUtil.showDebug(TAG, "newUserData: " + newUserData.size());
		}

	}

	@Override
	public void notifyAddPresentDataFinished(boolean result,
			final MessageItemData messageData) {
		DbgUtil.showDebug(TAG, "notifyAddPresentDataFinished");

		if (messageData != null) {
			DbgUtil.showDebug(TAG,
					"fromUserName: " + messageData.getFromUserName());
			DbgUtil.showDebug(TAG, "fromUserId: " + messageData.getFromUserId());
			DbgUtil.showDebug(TAG, "message: " + messageData.getMessage());
			DbgUtil.showDebug(TAG,
					"targetUserId: " + messageData.getTargetUserId());
			DbgUtil.showDebug(TAG, "toUserName: " + messageData.getToUserName());
			DbgUtil.showDebug(TAG, "postDate: " + messageData.getPostedDate());
		}

		if (result) {
			FeedbackUtil.showFeedbackToast(getApplicationContext(), mHandler,
					R.string.str_conversation_message_successfuly_sent);

			// Add posted messageItem to ListView
			// (Not to get from DB to make performance better.
			new Thread(new Runnable() {
				@Override
				public void run() {
					mHandler.post(new Runnable() {
						@Override
						public void run() {

							setProgressBarIndeterminateVisibility(false);

							if (messageData != null) {
								mConversationData.add(messageData);
							}

							mConversationSendButton.setEnabled(true);
							mConversationEditText.setEnabled(true);

							mAdapter.notifyDataSetChanged();
							mListView.setSelection(mListView.getCount() - 1);
							mConversationEditText.getEditableText().clear();
							DbgUtil.showDebug(TAG, "mConversationData: "
									+ mConversationData.size());
						}
					});
				}

			}).start();
		} else {
			// If message can not be sent
			FeedbackUtil.showFeedbackToast(getApplicationContext(), mHandler,
					R.string.str_conversation_message_fail_sent);

			new Thread(new Runnable() {
				@Override
				public void run() {
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							mConversationSendButton.setEnabled(true);
							mConversationEditText.setEnabled(true);
						}
					});
				}

			}).start();

		}

	}

	@Override
	public void notifyPresentMessageDataLoaded(
			ArrayList<MessageItemData> messageData) {
		DbgUtil.showDebug(TAG, "notifyPresentMessageDataLoaded");

		// If new data is already available
		if (mIsNewDataReady) {
			DbgUtil.showDebug(TAG, "New data available");
			// Keep data
			if (messageData != null && messageData.size() != 0) {
				DbgUtil.showDebug(TAG,
						"present data size: " + messageData.size());
				if (mConversationData != null) {
					mConversationData.addAll(messageData);
				}
			}

			if (mConversationData != null && mConversationData.size() != 0) {

				// Sort by message post data in mConversationData
				Collections.sort(mConversationData,
						new ConversationDataComparator());
			}

			// Notify to adapter
			mAdapter.notifyDataSetChanged();
			mListView.setAdapter(mAdapter);
			mListView.setSelection(mListView.getCount() - 1);

			// Initialize flag
			mIsNewDataReady = false;

			new Thread(new Runnable() {
				@Override
				public void run() {
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							setProgressBarIndeterminateVisibility(false);
						}
					});
				}

			}).start();

		} else {
			// If new data is not available yet.
			DbgUtil.showDebug(TAG, "New data not available");

			// Set present
			mIsPresentDataReady = true;

			// Keep present data but not to notify adapter.
			if (messageData != null && messageData.size() != 0) {
				DbgUtil.showDebug(TAG,
						"present data size: " + messageData.size());
				mConversationData.clear();
				mConversationData.addAll(messageData);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return false;
	}

	@Override
	public void notifyNewConversationDataLoaded(
			ArrayList<MessageItemData> messageData) {
		DbgUtil.showDebug(TAG, "notifyNewConversationDataLoaded");

		// If present data is already available
		if (mIsPresentDataReady) {
			DbgUtil.showDebug(TAG, "Present data available");

			// Keep data
			if (messageData != null && messageData.size() != 0) {
				DbgUtil.showDebug(TAG, "new data size: " + messageData.size());
				if (mConversationData != null) {
					mConversationData.addAll(messageData);
				}
			}

			if (mConversationData != null && mConversationData.size() != 0) {
				// Sort by message post data in mConversationData
				Collections.sort(mConversationData,
						new ConversationDataComparator());
			}

			// Notify to adapter
			mAdapter.notifyDataSetChanged();
			mListView.setAdapter(mAdapter);
			mListView.setSelection(mListView.getCount() - 1);

			// if (mProgressDialog != null && mProgressDialog.isShowing()) {
			// mProgressDialog.getDialog().dismiss();
			// }

			new Thread(new Runnable() {
				@Override
				public void run() {
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							setProgressBarIndeterminateVisibility(false);
						}
					});
				}

			}).start();

			// Initialize flag
			mIsPresentDataReady = false;

		} else {
			// If new data is not available yet.
			DbgUtil.showDebug(TAG, "Present data not available");

			// Set present
			mIsNewDataReady = true;

			// Keep new data but not to notify adapter.
			if (messageData != null && messageData.size() != 0) {
				DbgUtil.showDebug(TAG, "new data size: " + messageData.size());
				mConversationData.clear();
				mConversationData.addAll(messageData);
			}
		}
	}

	@Override
	public void notifyFriendThubmailsLoaded(
			List<HashMap<Integer, Bitmap>> thumbnailsthumbnails) {
		DbgUtil.showDebug(TAG, "notifyFriendThubmailsLoaded - not to be used");

	}

	public class ConversationBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			DbgUtil.showDebug(TAG, "onReceive");
			requestThreadData();
		}
	}

	@Override
	public void notifyLatestStoredMessage(FriendListData result) {
		DbgUtil.showDebug(TAG, "notifyLatestStoredMessage - not to be used");
	}

	@Override
	public void notifyValidNotificationList(
			ArrayList<NotificationContentData> notifications) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyAdditionalLocalUserDataLoaded(
			ArrayList<FriendListData> userData) {

	}

	@Override
	public void onNotifyScrollPositionChanged(int pageNum) {
		DbgUtil.showDebug(TAG, "onNotifyScrollPositionChanged");
		mManager.reloadConversationData(mTargetUserId, pageNum);
	}

	@Override
	public void notifyAdditionalLocalConversationDataLoaded(
			final ArrayList<MessageItemData> messageData) {
		DbgUtil.showDebug(TAG, "notifyAdditionalLocalConversationDataLoaded");
		if (mConversationData != null) {

			new Thread(new Runnable() {
				@Override
				public void run() {
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							if (mAdapter != null) {
								// First, current scroll position
								int scrollNum = mConversationData.size();
								DbgUtil.showDebug(TAG, "scrollNum: "
										+ scrollNum);

								// Switch order of additional data come from
								// Local by date
								Collections.sort(messageData,
										new ConversationDataComparator());

								// Add them to data object on this Activity
								mConversationData.addAll(0, messageData);

								mListView.setAdapter(mAdapter);

								// Set scroll position
								mListView
										.setSelection(LcomConst.ITEM_ON_SCREEN);

								// If we reaches to bottom of screen (meaning
								// all data is already loaded), we don't notify
								// list view
								if (messageData != null
										&& messageData.size() >= LcomConst.ITEM_ON_SCREEN) {
									mListView.listViewUIReady();
								}

								// Notify to adapter
								mAdapter.notifyDataSetChanged();
							}
						}
					});
				}
			}).start();

		}
	}
}
