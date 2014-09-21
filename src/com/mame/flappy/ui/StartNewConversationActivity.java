package com.mame.flappy.ui;

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
import android.widget.TextView;

import com.mame.flappy.LcomBaseActivity;
import com.mame.flappy.R;
import com.mame.flappy.constant.LcomConst;
import com.mame.flappy.exception.WebAPIException;
import com.mame.flappy.ui.dialog.InvitationConfirmDialog;
import com.mame.flappy.ui.dialog.InvitationConfirmDialog.InvitationConfirmationListener;
import com.mame.flappy.util.DbgUtil;
import com.mame.flappy.util.FeedbackUtil;
import com.mame.flappy.util.NetworkUtil;
import com.mame.flappy.util.PreferenceUtil;
import com.mame.flappy.util.StringUtil;
import com.mame.flappy.util.TrackingUtil;
import com.mame.flappy.web.LcomHttpWebAPI;
import com.mame.flappy.web.LcomServerAccessor;
import com.mame.flappy.web.LcomHttpWebAPI.LcomWebAPIListener;

public class StartNewConversationActivity extends LcomBaseActivity implements
		LcomServerAccessor.LcomServerAccessorListener,
		InvitationConfirmationListener {

	private final String TAG = LcomConst.TAG + "/StartNewConversationActivity";

	private EditText mMailEditText = null;

	// private EditText mNewInviMessageEditText = null;

	private Button mInputMessageButton = null;

	private Button mContactsListButton = null;

	private TextView mResultView = null;

	private TextView mConfirmViewFromContacts = null;

	private TextView mUserNameFromContacts = null;

	private TextView mMailFromContacts = null;

	private TextView mDescriptionFromContacts = null;

	private LcomServerAccessor mWebAPI = null;

	private int mUserId = LcomConst.NO_USER;

	private String mUserName = null;

	private Handler mHanler = new Handler();

	private int REQUEST_CODE = 1;

	private Activity mActivity = null;

	private Handler mHandler = new Handler();

	private String mMailAddressFromContacts = null;

	private InvitationConfirmDialog mConfirmDialog = null;

	private ProgressDialogFragmentHelper mProgressHelper = null;

	// private StartNewConversationListener mListener = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.startconversation);

		Intent intent = getIntent();
		if (intent != null) {
			mUserId = intent.getIntExtra(LcomConst.EXTRA_USER_ID,
					LcomConst.NO_USER);
			mUserName = intent.getStringExtra(LcomConst.EXTRA_USER_NAME);
		}

		mProgressHelper = new ProgressDialogFragmentHelper();

		mActivity = this;

		mConfirmDialog = new InvitationConfirmDialog();
		mConfirmDialog.setInvitationConfirmationListener(this);

		ActionBar actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);

		final Activity activity = this;

		mResultView = (TextView) findViewById(R.id.newInviResultView);
		mResultView.setVisibility(View.GONE);

		mUserNameFromContacts = (TextView) findViewById(R.id.usreNameFromContacts);
		mUserNameFromContacts.setVisibility(View.GONE);

		mMailFromContacts = (TextView) findViewById(R.id.mailFromContacts);
		mMailFromContacts.setVisibility(View.GONE);

		// mConfirmViewFromContacts = (TextView)
		// findViewById(R.id.confirmForFromContacts);
		// mConfirmViewFromContacts.setVisibility(View.GONE);

		mDescriptionFromContacts = (TextView) findViewById(R.id.confirmForFromContactsDesc);
		mDescriptionFromContacts.setVisibility(View.GONE);

		mResultView = (TextView) findViewById(R.id.newInviResultView);
		mResultView.setVisibility(View.GONE);

		mMailEditText = (EditText) findViewById(R.id.newMailAddressEditText);
		mMailEditText.addTextChangedListener(new TextWatcher() {

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
					mContactsListButton.setEnabled(false);
					mInputMessageButton.setEnabled(true);
				} else {
					mContactsListButton.setEnabled(true);
					mInputMessageButton.setEnabled(false);
				}
			}
		});

		// mNewInviMessageEditText = (EditText)
		// findViewById(R.id.newInvMessageEditText);

		mInputMessageButton = (Button) findViewById(R.id.newConversationStartButton);
		mInputMessageButton.setEnabled(false);
		mInputMessageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				DbgUtil.showDebug(TAG, "Start conversation button pressed");

				// Track Input message button pressed event
				TrackingUtil.trackEvent(getApplicationContext(),
						TrackingUtil.EVENT_CATEGORY_START_CONVERSATION,
						TrackingUtil.EVENT_ACTION_START_CONVERSATION,
						TrackingUtil.EVENT_LABEL_INPUT_MESSAGE, 1);

				SpannableStringBuilder sbMailAddress = (SpannableStringBuilder) mMailEditText
						.getText();
				final String mailAddress = sbMailAddress.toString();
				DbgUtil.showDebug(TAG, "mailAddress: " + mailAddress);

				if (NetworkUtil.isNetworkAvailable(activity, mHandler)) {
					if (mailAddress != null && mailAddress.length() != 0) {
						DbgUtil.showDebug(TAG, "String from edit text");
						// If mail address from edit text is available, we will
						// use it.
						checkInputInfoAndSendData(mailAddress);
					} else {
						DbgUtil.showDebug(TAG, "String from contacts list");
						// If mail address from edit text is null, we will use
						// address from Contacts list
						checkInputInfoAndSendData(mMailAddressFromContacts);
					}
				}
			}

		});

		mContactsListButton = (Button) findViewById(R.id.contactsListButton);
		mContactsListButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				DbgUtil.showDebug(TAG, "contacts list button pressed");

				// Track Contact list button pressed event
				TrackingUtil.trackEvent(getApplicationContext(),
						TrackingUtil.EVENT_CATEGORY_START_CONVERSATION,
						TrackingUtil.EVENT_ACTION_START_CONVERSATION,
						TrackingUtil.EVENT_LABEL_CONTACT_LIST, 1);

				//TODO
				// StartNewConversationActivityUtil.startActivityForContactsList(
				// mActivity, REQUEST_CODE);

			}

		});

		mWebAPI = new LcomServerAccessor();
		mWebAPI.setListener(this);

	}

	@Override
	public void onResume() {
		super.onResume();
		DbgUtil.showDebug(TAG, "onResume");
		if (mResultView != null) {
			mResultView.setVisibility(View.GONE);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		DbgUtil.showDebug(TAG, "onPause");

		if (mWebAPI != null) {
			mWebAPI.interrupt();
		}

		if (mResultView != null) {
			mResultView.setVisibility(View.GONE);
		}

		if (mMailEditText != null) {
			mMailEditText.setEnabled(true);
			mMailEditText.setText(null);
		}

		if (mContactsListButton != null) {
			mContactsListButton.setEnabled(true);
		}

		if (mUserNameFromContacts != null) {
			mUserNameFromContacts.setText(null);
			mUserNameFromContacts.setVisibility(View.GONE);
		}

		if (mMailFromContacts != null) {
			mMailFromContacts.setText(null);
			mMailFromContacts.setVisibility(View.GONE);
		}

		if (mInputMessageButton != null) {
			mInputMessageButton.setEnabled(false);
		}

		if (mDescriptionFromContacts != null) {
			mDescriptionFromContacts.setVisibility(View.GONE);
		}

		mMailAddressFromContacts = null;
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		DbgUtil.showDebug(TAG, "onActivityResult");
		if (data != null) {
			Bundle bundle = data.getExtras();
			if (requestCode == REQUEST_CODE) {
				String name = bundle
						.getString(LcomConst.RESULT_EXTRA_CONTACT_NAME);
				String address = bundle
						.getString(LcomConst.RESULT_EXTRA_CONTACT_ADDRESS);
				DbgUtil.showDebug(TAG, "name: " + name + " address: " + address);
				if (name != null) {
					mUserNameFromContacts.setVisibility(View.VISIBLE);
					mUserNameFromContacts.setText(name);
				}
				if (address != null) {
					mMailAddressFromContacts = address;
					mMailFromContacts.setVisibility(View.VISIBLE);
					mDescriptionFromContacts.setVisibility(View.VISIBLE);
					mMailFromContacts.setText(address);
					mMailEditText.setEnabled(false);
					mInputMessageButton.setEnabled(true);
				}
			} else {
				DbgUtil.showDebug(TAG, "Illegal requestCode: " + requestCode);
			}
		}
	}

	/**
	 * Show message input dialog for selected target user. targetUserId and
	 * targetUserName shall be null if target address has not been registered to
	 * this service.
	 * 
	 * @param targetUserId
	 * @param targetUserName
	 * @param mailAddress
	 */
	private void showMessageInputDialog(final String targetUserId,
			final String targetUserName, final String mailAddress) {
		DbgUtil.showDebug(TAG, "showMessageInputDialog");
		DbgUtil.showDebug(TAG, "targetUserId: " + targetUserId);
		new Thread(new Runnable() {
			@Override
			public void run() {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						Bundle bundle = new Bundle();
						bundle.putString(LcomConst.EXTRA_USER_ID,
								String.valueOf(mUserId));
						bundle.putString(LcomConst.EXTRA_USER_NAME, mUserName);
						bundle.putString(LcomConst.EXTRA_TARGET_USER_ID,
								targetUserId);
						bundle.putString(LcomConst.EXTRA_TARGET_USER_NAME,
								targetUserName);
						bundle.putString(LcomConst.EXTRA_TARGET_MAIL_ADDRESS,
								mailAddress);
						// bundle.putString(LcomConst.EXTRA_TARGET_MESSAGE,
						// message);
						mConfirmDialog.setArguments(bundle);
						mConfirmDialog.show(getFragmentManager(), TAG);
					}
				});
			}

		}).start();
	}

	private void checkInputInfoAndSendData(String address) {
		DbgUtil.showDebug(TAG, "checkInputInfoAndSendData: " + address);
		if (StringUtil.isValidCharsForAddress(address)) {
			if (StringUtil.checkMailAddress(address)) {
				try {
					if (mProgressHelper != null) {
						mProgressHelper
								.showProgressDialog(
										mActivity,
										getString(R.string.str_invitation_mail_address_check_title),
										getString(R.string.str_generic_wait_desc),
										TAG);
					}

					sendDataForTargetUser(mUserId, mUserName, address);
				} catch (WebAPIException e) {
					DbgUtil.showDebug(TAG, "WebAPIException: " + e.getMessage());
					TrackingUtil.trackExceptionMessage(getApplicationContext(),
							TAG, "WebAPIException: " + e.getMessage());
				}
			} else {
				DbgUtil.showDebug(TAG, "mail address character check failed");
				// If mail address contains invalid character for mail
				// address
				mResultView.setVisibility(View.VISIBLE);
				mResultView
						.setText(R.string.str_invitation_invalid_mail_address);
			}
		} else {
			DbgUtil.showDebug(TAG, "Invalid character for  mail address");
			// If message contains invalid character
			mResultView.setVisibility(View.VISIBLE);
			mResultView.setText(R.string.str_invitation_invalid_mail_address);
		}
	}

	/**
	 * Check if target user has already been registered to this service
	 * 
	 * @param userId
	 * @param userName
	 * @param mailAddress
	 * @throws WebAPIException
	 */
	private void sendDataForTargetUser(int userId, String userName,
			String mailAddress) throws WebAPIException {
		DbgUtil.showDebug(TAG, "sendCreateAccountData");
		DbgUtil.showDebug(TAG, "Mail address: " + mailAddress);
		String origin = TAG;
		String key[] = { LcomConst.SERVLET_ORIGIN, LcomConst.SERVLET_USER_ID,
				LcomConst.SERVLET_USER_NAME, LcomConst.SERVLET_MAILADDRESS,
				LcomConst.SERVLET_API_LEVEL };
		String value[] = { origin, String.valueOf(userId), userName,
				mailAddress, String.valueOf(LcomConst.API_LEVEL) };
		mWebAPI.sendData(LcomConst.SERVLET_NAME_NEW_INVITATION, key, value,
				userName);
	}

	/**
	 * Send message to selected target user
	 * 
	 * @param userId
	 * @param userName
	 * @param mailAddress
	 * @throws WebAPIException
	 */
	private void sendMessageForTargetUser(int userId, String userName,
			String mailAddress, String message) throws WebAPIException {
		DbgUtil.showDebug(TAG, "sendCreateAccountData");
		DbgUtil.showDebug(TAG, "Mail address: " + mailAddress);
		String origin = TAG;
		String key[] = { LcomConst.SERVLET_ORIGIN, LcomConst.SERVLET_USER_ID,
				LcomConst.SERVLET_USER_NAME, LcomConst.SERVLET_MAILADDRESS,
				LcomConst.SERVLET_MESSAGE_BODY, LcomConst.SERVLET_API_LEVEL };
		String value[] = { origin, String.valueOf(userId), userName,
				mailAddress, message, String.valueOf(LcomConst.API_LEVEL) };
		mWebAPI.sendData(LcomConst.SERVLET_NAME_NEW_INVITATION, key, value,
				userName);
	}

	private void parseAndShowResponse(List<String> respList) {
		DbgUtil.showDebug(TAG, "parseAndShowResponse");
		if (respList != null) {
			try {
				String result = respList.get(1);
				if (result != null) {
					switch (Integer.valueOf(result)) {
					case LcomConst.INVITATION_NEW_USER_RESULT_OK:
						// If the target mail address is new user
						DbgUtil.showDebug(TAG, "INVITATION_NEW_USER_RESULT_OK");
						String mailAddress = respList.get(4);
						showMessageInputDialog(null, null, mailAddress);
						// FeedbackUtil.showFeedbackToast(getApplicationContext(),
						// mHanler,
						// R.string.str_invitation_successfully_invited);
						break;
					case LcomConst.INVITATION_EXISTING_USER_RESULT_OK:
						DbgUtil.showDebug(TAG,
								"INVITATION_EXISTING_USER_RESULT_OK");
						String existUserId = respList.get(2);
						String existUserName = respList.get(3);
						mailAddress = respList.get(4);
						// String message = respList.get(5);
						DbgUtil.showDebug("userId: ", existUserId);
						DbgUtil.showDebug("userName: ", existUserName);
						DbgUtil.showDebug("mailAddress: ", mailAddress);
						// DbgUtil.showDebug("message: ", message);
						// If the target mail address is existing user, we need
						// to
						// confirm to user.
						showMessageInputDialog(existUserId, existUserName,
								mailAddress);
						break;
					// case LcomConst.INVITATION_MAIL_CANNOT_BE_SENT:
					// FeedbackUtil.showFeedbackToast(getApplicationContext(),
					// mHanler,
					// R.string.str_invitation_mail_cannot_be_sent);
					// break;
					case LcomConst.INVITATION_UNKNOWN_ERROR:
						// If error occurred
					default:
						FeedbackUtil.showFeedbackToast(getApplicationContext(),
								mHanler, R.string.str_invitation_unknown_error);
						TrackingUtil
								.trackExceptionMessage(getApplicationContext(),
										TAG,
										"Unknown error - switch sentence is unexpected case");
						break;
					}
				} else {
					FeedbackUtil.showFeedbackToast(getApplicationContext(),
							mHanler, R.string.str_invitation_unknown_error);
					TrackingUtil.trackExceptionMessage(getApplicationContext(),
							TAG, "result is null");
				}
			} catch (IndexOutOfBoundsException e) {
				DbgUtil.showDebug(TAG,
						"IndexOutOfBoundException: " + e.getMessage());
				TrackingUtil.trackExceptionMessage(getApplicationContext(),
						TAG, "IndexOutOfBoundsException: " + e.getMessage());
				FeedbackUtil.showFeedbackToast(getApplicationContext(),
						mHanler, R.string.str_invitation_unknown_error);
			}
		} else {
			DbgUtil.showDebug(TAG, "respList is null");
			FeedbackUtil.showFeedbackToast(getApplicationContext(), mHanler,
					R.string.str_invitation_unknown_error);
			TrackingUtil.trackExceptionMessage(getApplicationContext(), TAG,
					"respList is null");
		}

		// Dismiss dialog
		if (mProgressHelper != null) {
			mProgressHelper.dismissDialog(mActivity, TAG);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.start_conversation_menu, menu);
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
	public void onResponseReceived(List<String> respList) {
		DbgUtil.showDebug(TAG, "onResponseReceived");
		if (respList != null && respList.size() != 0) {
			String origin = respList.get(0);
			if (origin != null && origin.equals(TAG)) {
				parseAndShowResponse(respList);
			} else {
				DbgUtil.showDebug(TAG, "origin is null");
				FeedbackUtil.showFeedbackToast(getApplicationContext(),
						mHanler, R.string.str_invitation_unknown_error);
			}
		} else {
			DbgUtil.showDebug(TAG, "respList is null");
			FeedbackUtil.showFeedbackToast(getApplicationContext(), mHanler,
					R.string.str_invitation_unknown_error);
			TrackingUtil.trackExceptionMessage(getApplicationContext(), TAG,
					"respList is null");
		}

	}

	@Override
	public void onAPITimeout() {
		DbgUtil.showDebug(TAG, "onAPITimeout");
		FeedbackUtil.showFeedbackToast(getApplicationContext(), mHanler,
				R.string.str_invitation_unknown_error);
		TrackingUtil.trackExceptionMessage(getApplicationContext(), TAG,
				"API call timeout");

	}

	@Override
	public void onNotifyNewFriendAdded() {
		DbgUtil.showDebug(TAG, "onNotifyNewFriendAdded");
		Intent data = new Intent();
		Bundle bundle = new Bundle();
		data.putExtras(bundle);

		setResult(RESULT_OK, data);
		finish();
	}
}
