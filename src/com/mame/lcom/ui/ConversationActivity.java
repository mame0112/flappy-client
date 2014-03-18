package com.mame.lcom.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mame.lcom.R;
import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.data.ConversationDataComparator;
import com.mame.lcom.data.FriendListData;
import com.mame.lcom.data.FriendListDataComparator;
import com.mame.lcom.data.FriendListUpdateData;
import com.mame.lcom.data.MessageItemData;
import com.mame.lcom.datamanager.FriendDataManager;
import com.mame.lcom.datamanager.FriendDataManager.FriendDataManagerListener;
import com.mame.lcom.exception.FriendDataManagerException;
import com.mame.lcom.util.DbgUtil;
import com.mame.lcom.util.FeedbackUtil;
import com.mame.lcom.util.PreferenceUtil;
import com.mame.lcom.util.TimeUtil;
import com.mame.lcom.util.TrackingUtil;
import com.mame.lcom.web.LcomWebAPI;
import com.mame.lcom.web.LcomWebAPI.LcomWebAPIListener;

public class ConversationActivity extends Activity implements
		FriendDataManagerListener {

	private final String TAG = LcomConst.TAG + "/ConversationActivity";

	private int mUserId = LcomConst.NO_USER;

	private String mUserName = null;

	private int mTargetUserId = LcomConst.NO_USER;

	private String mTargetUserName = null;

	private FriendDataManager mManager = null;

	private EditText mConversationEditText = null;

	private Button mConversationSendButton = null;

	private Handler mHandler = new Handler();

	private ArrayList<MessageItemData> mConversationData = new ArrayList<MessageItemData>();

	private ConversationListCustonAdapter mAdapter = null;

	private ListView mListView = null;

	private ProgressDialogFragment mProgressDialog = null;

	private boolean mIsPresentDataReady = false;

	private boolean mIsNewDataReady = false;

	private TextView mNumOfMessage = null;

	private String mPageNum = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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

			if (mTargetUserName != null) {
				DbgUtil.showDebug(TAG, "mTargetUserName: " + mTargetUserName);

				// Set target user name as activity title
				setTitle(mTargetUserName);
			}

			DbgUtil.showDebug(TAG, "mTargetUserId: " + mTargetUserId);
		}

		mUserId = PreferenceUtil.getUserId(getApplicationContext());
		mUserName = PreferenceUtil.getUserName(getApplicationContext());

		ActionBar actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);

		mProgressDialog = ProgressDialogFragment.newInstance(
				getString(R.string.str_conversation_progress_title),
				getString(R.string.str_conversation_progress_desc));

		mAdapter = new ConversationListCustonAdapter(getApplicationContext(),
				0, mConversationData);
		// mAdapter.addAll(mConversationData);

		mListView = (ListView) findViewById(R.id.conversationListView);
		mListView.setAdapter(mAdapter);

		FriendDataManager.initializeFriendDataManager(getApplicationContext());
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
					changePageNumber(s.length());
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

				TrackingUtil.trackEvent(getApplicationContext(),
						TrackingUtil.EVENT_CATEGORY_CONVERSATION,
						TrackingUtil.EVENT_ACTION_CONVERSATION,
						TrackingUtil.EVENT_LABEL_CONVERSATION_SEND_BUTTON, 1);

				SpannableStringBuilder sbMessage = (SpannableStringBuilder) mConversationEditText
						.getText();
				String message = sbMessage.toString();
				if (message != null) {

					// Track the number of texts in one message
					TrackingUtil.trackNumberOfCharInOneMessage(
							getApplicationContext(), message.length());

					mProgressDialog.show(getFragmentManager(), "progress");

					long date = TimeUtil.getCurrentDate();
					sendAndRegisterMessage(mUserId, mTargetUserId, mUserName,
							mTargetUserName, message, String.valueOf(date));

				} else {
					Toast.makeText(getApplicationContext(),
							R.string.str_conversation_no_text_input,
							Toast.LENGTH_SHORT).show();
				}

			}
		});

		mPageNum = getString(R.string.str_conversation_message_page, 1);

		mNumOfMessage = (TextView) findViewById(R.id.conversationMsgNum);
		mNumOfMessage.setText(mPageNum);
		mNumOfMessage.setVisibility(View.GONE);

		try {
			mManager.requestMessageListDatasetWithTargetUser(mUserId,
					mTargetUserId, true, true);
		} catch (FriendDataManagerException e) {
			DbgUtil.showDebug(TAG,
					"FriendDataManagerException: " + e.getMessage());
		}

	}

	private void changePageNumber(int length) {
		int pageNum = (int) (length / LcomConst.MAX_MESSAGE_LENGTH) + 1;
		if (pageNum > 1) {
			mPageNum = getString(R.string.str_conversation_message_page,
					pageNum);
			mNumOfMessage = (TextView) findViewById(R.id.conversationMsgNum);
			mNumOfMessage.setText(mPageNum);
			mNumOfMessage.setVisibility(View.VISIBLE);
		} else {
			// If the user is in first page, we shall make it invisible.
			mNumOfMessage.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// if(mConversationSendButton != null){
		// mConversationSendButton.setEnabled(enabled)
		// }
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
				.parseMessageBasedOnMaxLength(message);

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
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mManager != null) {
			mManager.removeFriendDataManagerListener(this);
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
	public void notifyNewDataset(ArrayList<FriendListUpdateData> newUserData) {
		DbgUtil.showDebug(TAG, "notifyNewDataset");
		if (newUserData != null && newUserData.size() != 0) {
			DbgUtil.showDebug(TAG, "newUserData: " + newUserData.size());
		}

	}

	@Override
	public void notifyAddPresentDataFinished(boolean result,
			final MessageItemData messageData) {
		DbgUtil.showDebug(TAG, "notifyAddPresentDataFinished");

		DbgUtil.showDebug(TAG, "fromUserName: " + messageData.getFromUserName());
		DbgUtil.showDebug(TAG, "fromUserId: " + messageData.getFromUserId());
		DbgUtil.showDebug(TAG, "message: " + messageData.getMessage());
		DbgUtil.showDebug(TAG, "targetUserId: " + messageData.getTargetUserId());
		DbgUtil.showDebug(TAG, "toUserName: " + messageData.getToUserName());
		DbgUtil.showDebug(TAG, "postDate: " + messageData.getPostedDate());

		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
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
							mConversationData.add(messageData);
							mAdapter.notifyDataSetChanged();
							mConversationEditText.getEditableText().clear();
							DbgUtil.showDebug(TAG, "mConversationData: "
									+ mConversationData.size());
						}
					});
				}

			}).start();
		} else {
			FeedbackUtil.showFeedbackToast(getApplicationContext(), mHandler,
					R.string.str_conversation_message_fail_sent);
		}

	}

	@Override
	public void notifyPresentMessageDataLoaded(
			ArrayList<MessageItemData> messageData) {
		DbgUtil.showDebug(TAG, "notifyPresentMessageDataLoaded");

		if (messageData != null) {
			DbgUtil.showDebug(TAG, "messageData: " + messageData.size());
		}

		// If new data is already available
		if (mIsNewDataReady) {
			DbgUtil.showDebug(TAG, "New data available");
			// Keep data
			if (messageData != null && messageData.size() != 0) {
				DbgUtil.showDebug(TAG,
						"present data size: " + messageData.size());
				mConversationData.addAll(messageData);
			}

			if (mConversationData != null && mConversationData.size() != 0) {

				// Sort by message post data in mConversationData
				Collections.sort(mConversationData,
						new ConversationDataComparator());
			}

			// Notify to adapter
			mAdapter.notifyDataSetChanged();
			mListView.setAdapter(mAdapter);

			// Initialize flag
			mIsNewDataReady = false;

		} else {
			// If new data is not available yet.
			DbgUtil.showDebug(TAG, "New data not available");

			// Set present
			mIsPresentDataReady = true;

			// Keep present data but not to notify adapter.
			if (messageData != null && messageData.size() != 0) {
				DbgUtil.showDebug(TAG,
						"present data size: " + messageData.size());
				for (MessageItemData data : messageData) {
					DbgUtil.showDebug(TAG, "data::: " + data.getFromUserId());
				}

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
				mConversationData.addAll(messageData);
			}

			if (mConversationData != null && mConversationData.size() != 0) {
				// Sort by message post data in mConversationData
				Collections.sort(mConversationData,
						new ConversationDataComparator());

				for (MessageItemData data : mConversationData) {
					DbgUtil.showDebug(TAG, "data: " + data.getFromUserId());
				}

			}

			// Notify to adapter
			mAdapter.notifyDataSetChanged();

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
				mConversationData.addAll(messageData);
			}
		}
	}
}
