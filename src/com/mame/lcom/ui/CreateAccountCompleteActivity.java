package com.mame.lcom.ui;

import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mame.lcom.R;
import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.exception.WebAPIException;
import com.mame.lcom.util.DbgUtil;
import com.mame.lcom.util.FeedbackUtil;
import com.mame.lcom.util.FileUtil;
import com.mame.lcom.util.ImageUtil;
import com.mame.lcom.util.NetworkUtil;
import com.mame.lcom.util.StringUtil;
import com.mame.lcom.util.TrackingUtil;
import com.mame.lcom.web.LcomWebAPI;
import com.mame.lcom.web.LcomWebAPI.LcomWebAPIListener;

public class CreateAccountCompleteActivity extends Activity implements
		LcomWebAPIListener {
	private final String TAG = LcomConst.TAG + "/CreateAccountCompleteActivity";

	private EditText mPasswordEditText = null;

	private EditText mPasswordAgainEditText = null;

	private EditText mMailEditText = null;

	private Button mCreateAccountButton = null;

	private Button mTeamOfServiceButtoon = null;

	private Button mPrivacyPolicyButtoon = null;

	private LcomWebAPI mWebAPI = null;

	private Handler mHandler = new Handler();

	private int mPasswordLength = 0;

	private int mPasswordAgainLength = 0;

	private int mMailAddressLength = 0;

	private ProgressDialogFragment mProgressDialog = null;

	private String mUserName = null;

	private int mUserId = LcomConst.NO_USER;

	private TextView mCreateResultView = null;

	private Bitmap mThumbnailData = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		DbgUtil.showDebug(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_account_complete);

		Intent intent = getIntent();
		if (intent != null) {
			mUserName = intent.getStringExtra(LcomConst.EXTRA_USER_NAME);
			if (mUserName != null) {
				DbgUtil.showDebug(TAG, "mUserName: " + mUserName);
			}
			Bundle bundle = intent.getExtras();
			mThumbnailData = (Bitmap) bundle.get(LcomConst.EXTRA_THUMBNAIL);
			if (mThumbnailData != null) {
				DbgUtil.showDebug(TAG,
						"thumbBitmap: " + mThumbnailData.getWidth() + " / "
								+ mThumbnailData.getHeight());

			}
		} else {
			// Initialize thumbnail data.
			mThumbnailData = null;
		}

		ActionBar actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);

		final Activity activity = this;

		mProgressDialog = ProgressDialogFragment.newInstance(
				getString(R.string.str_login_progress_title),
				getString(R.string.str_generic_wait_desc));

		mCreateResultView = (TextView) findViewById(R.id.createCompleteResultView);
		mCreateResultView.setVisibility(View.GONE);

		mPasswordEditText = (EditText) findViewById(R.id.createCompletePassword);
		mPasswordEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				mPasswordLength = s.length();
				changeCreateButtonState();
			}
		});

		mPasswordAgainEditText = (EditText) findViewById(R.id.createCompletePasswordAgain);
		mPasswordAgainEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				mPasswordAgainLength = s.length();
				changeCreateButtonState();
			}
		});

		mMailEditText = (EditText) findViewById(R.id.createCompleteMailAddress);
		mMailEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				mMailAddressLength = s.length();
				changeCreateButtonState();
			}
		});

		mCreateAccountButton = (Button) findViewById(R.id.createAccountDoneButton);
		mCreateAccountButton.setEnabled(false);
		mCreateAccountButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				DbgUtil.showDebug(TAG, "Create account button pressed");
				TrackingUtil.trackEvent(getApplicationContext(),
						TrackingUtil.EVENT_CATEGORY_CREATE_ACCOUNT,
						TrackingUtil.EVENT_ACTION_CREATE_ACCOUNT_EXECUTION,
						TrackingUtil.EVENT_LABEL_CREATE_ACCOUNT_EXEC_BUTTON, 1);
				if (NetworkUtil.isNetworkAvailable(activity, mHandler)) {
					boolean checkResult = checkAndCreateAccountOrShowError(activity);

					// If password and mail address is correct, show progress
					// dalog.
					if (checkResult) {
						mProgressDialog.show(getFragmentManager(), "progress");
					}
				}
			}
		});

		mTeamOfServiceButtoon = (Button) findViewById(R.id.createTearmsOfService);
		mTeamOfServiceButtoon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				DbgUtil.showDebug(TAG, "Terms of service pressed");
				TrackingUtil.trackEvent(getApplicationContext(),
						TrackingUtil.EVENT_CATEGORY_CREATE_ACCOUNT,
						TrackingUtil.EVENT_ACTION_CREATE_ACCOUNT_EXECUTION,
						TrackingUtil.EVENT_LABEL_TOC_BUTTON, 1);
				CreateAccountActivityUtil.openURL(getApplicationContext(),
						LcomConst.TOC_AUTOHRITY);
			}

		});

		mPrivacyPolicyButtoon = (Button) findViewById(R.id.createPrivacyPolicy);
		mPrivacyPolicyButtoon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				DbgUtil.showDebug(TAG, "Privacy policy pressed");
				TrackingUtil.trackEvent(getApplicationContext(),
						TrackingUtil.EVENT_CATEGORY_CREATE_ACCOUNT,
						TrackingUtil.EVENT_ACTION_CREATE_ACCOUNT_EXECUTION,
						TrackingUtil.EVENT_LABEL_PRIVACY_BUTTON, 1);
				CreateAccountActivityUtil.openURL(getApplicationContext(),
						LcomConst.PRIVACY_AUTOHRITY);
			}

		});

		mWebAPI = new LcomWebAPI();
		mWebAPI.setListener(this);

	}

	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onResponseReceived(List<String> respList) {
		DbgUtil.showDebug(TAG, "onResponseReceived");

		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}

		if (respList != null) {
			parseAndHandleResponse(respList);
		} else {
			DbgUtil.showDebug(TAG, "respList is null");
		}
	}

	private void parseAndHandleResponse(List<String> respList) {
		DbgUtil.showDebug(TAG, "parseAndHandleResponse");

		try {
			String origin = respList.get(0);
			String result = respList.get(1);
			int userId = Integer.valueOf(respList.get(2));
			String userName = respList.get(3);
			if (origin != null && origin.equals(TAG)) {
				if (result != null) {
					switch (Integer.valueOf(result)) {
					case LcomConst.CREATE_ACCOUNT_RESULT_OK:
						DbgUtil.showDebug(TAG, "CREATE_ACCOUNT_RESULT_OK");
						CreateAccountActivityUtil.storeUserDataToPref(
								getApplicationContext(), userId, userName);
						FileUtil.storeBitmap(getApplicationContext(),
								mThumbnailData, LcomConst.PROFILE_THUMBNAIL);
						CreateAccountActivityUtil.startActivityForFriendList(
								this, userId, userName);
						// TODO Need to check Bitmap is stored.
						finish();
						break;
					case LcomConst.CREATE_ACCOUNT_USER_ALREADY_EXIST:
						DbgUtil.showDebug(TAG,
								"CREATE_ACCOUNT_USER_ALREADY_EXIST");
						FeedbackUtil
								.showFeedbackToast(
										getApplicationContext(),
										mHandler,
										getString(R.string.str_create_account_fail_user_exist));
						break;
					case LcomConst.CREATE_ACCOUNT_PARAMETER_NULL:
						DbgUtil.showDebug(TAG, "CREATE_ACCOUNT_PARAMETER_NULL");
						FeedbackUtil
								.showFeedbackToast(
										getApplicationContext(),
										mHandler,
										getString(R.string.str_create_account_fail_server_error));
						break;
					case LcomConst.CREATE_ACCOUNT_RESULT_OK_WITH_ADDRESS_REGISTERED:
						DbgUtil.showDebug(TAG,
								"CREATE_ACCOUNT_RESULT_OK_WITH_ADDRESS_REGISTERED");
						CreateAccountActivityUtil.storeUserDataToPref(
								getApplicationContext(), userId, userName);
						FileUtil.storeBitmap(getApplicationContext(),
								mThumbnailData, LcomConst.PROFILE_THUMBNAIL);
						CreateAccountActivityUtil.startActivityForFriendList(
								this, userId, userName);
						finish();
						break;
					case LcomConst.CREATE_ACCOUNT_UNKNOWN_ERROR:
						DbgUtil.showDebug(TAG, "CREATE_ACCOUNT_UNKNOWN_ERROR");
					default:
						FeedbackUtil
								.showFeedbackToast(
										getApplicationContext(),
										mHandler,
										getString(R.string.str_create_account_fail_server_error));

						break;

					}
				}
			} else {
				DbgUtil.showDebug(TAG, "invalid origin");
				FeedbackUtil
						.showFeedbackToast(
								getApplicationContext(),
								mHandler,
								getString(R.string.str_create_account_fail_server_error));
			}
		} catch (IndexOutOfBoundsException e) {
			DbgUtil.showDebug(TAG,
					"IndexOutOfBoundsException: " + e.getMessage());
			FeedbackUtil.showFeedbackToast(getApplicationContext(), mHandler,
					getString(R.string.str_create_account_fail_server_error));
		}
	}

	@Override
	public void onAPITimeout() {
		DbgUtil.showDebug(TAG, "onAPITimeoput");
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.getDialog().dismiss();
		}
		FeedbackUtil.showTimeoutToast(getApplicationContext(), mHandler);
	}

	private boolean checkAndCreateAccountOrShowError(Activity activity) {
		DbgUtil.showDebug(TAG, "checkAndCreateAccountOrShowError");

		SpannableStringBuilder sbPassword = (SpannableStringBuilder) mPasswordEditText
				.getText();
		String password = sbPassword.toString();
		String resultPassword = checkAndShowErrorForPassword(password);
		if (resultPassword == null) {
			SpannableStringBuilder sbPasswordAgain = (SpannableStringBuilder) mPasswordAgainEditText
					.getText();
			String passwordAgain = sbPasswordAgain.toString();
			String resultPasswordAgain = checkAndShowErrorForPassword(passwordAgain);
			DbgUtil.showDebug(TAG, "resultPasswordAgain: "
					+ resultPasswordAgain);
			if (resultPasswordAgain == null) {
				if (CreateAccountActivityUtil.isInputtedPasswordSame(password,
						passwordAgain)) {
					SpannableStringBuilder sbMailAddress = (SpannableStringBuilder) mMailEditText
							.getText();
					String mailAddress = sbMailAddress.toString();
					String mailResult = checkAndShowErrorForMailAddress(mailAddress);
					if (mailResult == null) {
						DbgUtil.showDebug(TAG, "OK for mail address");
						try {
							sendCreateAccountData(mUserName, password,
									mailAddress, mThumbnailData);
							return true;
						} catch (WebAPIException e) {
							DbgUtil.showDebug(TAG, e.getMessage());
							mCreateResultView.setVisibility(View.VISIBLE);
							mCreateResultView
									.setText(R.string.str_generic_unknown_error);
						}
					} else {
						DbgUtil.showDebug(TAG, "Invalid mail address");
						mCreateResultView.setVisibility(View.VISIBLE);
						mCreateResultView.setText(mailResult);
					}
				} else {
					// Password is not same between first and second
					DbgUtil.showDebug(TAG, "Password is not same");
					mCreateResultView.setVisibility(View.VISIBLE);
					mCreateResultView
							.setText(R.string.str_create_account_fail_invalid_password);
				}
			} else {
				// Passward second time is invalid.
				mCreateResultView.setVisibility(View.VISIBLE);
				mCreateResultView.setText(resultPasswordAgain);
				DbgUtil.showDebug(TAG, "PasswordAgain is Invalid.");
			}
		} else {
			mCreateResultView.setVisibility(View.VISIBLE);
			mCreateResultView.setText(resultPassword);
			DbgUtil.showDebug(TAG, "Password is Invalid2.");
		}

		return false;

	}

	private String checkAndShowErrorForPassword(String password) {
		DbgUtil.showDebug(TAG, "checkAndShowErrorForPassword: " + password);
		String result = null;
		if (password != null) {
			if (LoginActivityUtil.isValidInputString(
					LcomConst.MIN_USER_NAME_LENGTH,
					LcomConst.MAX_USER_NAME_LENGTH, password)) {
				if (LoginActivityUtil.isHalfSizeString(password)) {
					DbgUtil.showDebug(TAG, "OK for password");
					result = null;
				} else {
					DbgUtil.showDebug(TAG, "Password not half size");
					result = getString(R.string.str_create_account_fail_password_not_half_char);
				}
			} else {
				DbgUtil.showDebug(TAG, "Password invalid length");
				result = getString(R.string.str_create_account_fail_password_invalid_length);
			}
		} else {
			DbgUtil.showDebug(TAG, "Password null");
			result = getString(R.string.str_create_account_fail_password_null);
		}
		return result;
	}

	private String checkAndShowErrorForMailAddress(String address) {
		String result = null;
		if (address != null) {
			if (StringUtil.isValidCharsForAddress(address)) {
				if (StringUtil.checkMailAddress(address)) {
					result = null;
				} else {
					DbgUtil.showDebug(TAG, "invalid mail address");
					result = getString(R.string.str_create_account_fail_address_invalid_char);
				}
			} else {
				DbgUtil.showDebug(TAG,
						"Mail address contains invalid character for mail address");
				result = getString(R.string.str_create_account_fail_address_invalid_char_code);
			}
		} else {
			// address is null
			DbgUtil.showDebug(TAG, "Mail address is null");
			result = getString(R.string.str_create_account_fail_address_null);
		}
		return result;
	}

	private void sendCreateAccountData(String userName, String password,
			String mailAddress, Bitmap thumbnailData) throws WebAPIException {
		DbgUtil.showDebug(TAG, "sendCreateAccountData");

		String thumbnailString = ImageUtil.encodeTobase64(thumbnailData);

		String origin = TAG;
		String key[] = { LcomConst.SERVLET_ORIGIN, LcomConst.SERVLET_USER_NAME,
				LcomConst.SERVLET_PASSWORD, LcomConst.SERVLET_MAILADDRESS,
				LcomConst.SERVLET_THUMBNAIL };

		String value[] = { origin, userName, password, mailAddress,
				thumbnailString };
		mWebAPI.sendData(LcomConst.SERVLET_NAME_CREATE_ACCOUNT, key, value);
	}

	private void changeCreateButtonState() {
		if (mPasswordLength != 0 && mPasswordAgainLength != 0
				&& mMailAddressLength != 0) {
			mCreateAccountButton.setEnabled(true);
		} else {
			mCreateAccountButton.setEnabled(false);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.signup_complete_menu, menu);
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

}
