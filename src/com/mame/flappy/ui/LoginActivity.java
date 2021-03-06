package com.mame.flappy.ui;

import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
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
import com.mame.flappy.sound.FlappySoundManager;
import com.mame.flappy.util.ButtonUtil;
import com.mame.flappy.util.DbgUtil;
import com.mame.flappy.util.FeedbackUtil;
import com.mame.flappy.util.NetworkUtil;
import com.mame.flappy.util.TrackingUtil;
import com.mame.flappy.web.LcomServerAccessor;

public class LoginActivity extends LcomBaseActivity implements
		LcomServerAccessor.LcomServerAccessorListener {

	private final String TAG = LcomConst.TAG + "/LoginActivity";

	private Button mLoginButton = null;

	private EditText mUserNameEditBox = null;

	private EditText mPasswordEditBox = null;

	private TextView mSignInResultView = null;

	private LcomServerAccessor mWebAPI = null;

	private Handler mHandler = new Handler();

	private int mUserNamelength = 0;

	private int mPasswordLength = 0;

	private ProgressDialogFragmentHelper mProgressHelper = null;

	private Activity mActivity = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		final Activity activity = this;

		mWebAPI = new LcomServerAccessor();
		mWebAPI.setListener(this);

		ActionBar actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);

		mActivity = this;

		mProgressHelper = new ProgressDialogFragmentHelper();
		// mProgressDialog = ProgressDialogFragment.newInstance(
		// getString(R.string.str_login_progress_title),
		// getString(R.string.str_generic_wait_desc));

		mSignInResultView = (TextView) findViewById(R.id.signinResult);
		mSignInResultView.setVisibility(View.GONE);

		mUserNameEditBox = (EditText) findViewById(R.id.signinUserName);
		mUserNameEditBox.addTextChangedListener(new TextWatcher() {

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
				mUserNamelength = s.length();
				checkInputAndChangeButtonState();
			}

		});
		mPasswordEditBox = (EditText) findViewById(R.id.signinPassword);
		mPasswordEditBox.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				mPasswordLength = s.length();
				checkInputAndChangeButtonState();
			}

		});

		mLoginButton = (Button) findViewById(R.id.loginButton);
		mLoginButton.setEnabled(false);
		mLoginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (ButtonUtil.isClickable()) {

					TrackingUtil.trackEvent(getApplicationContext(),
							TrackingUtil.EVENT_CATEGORY_LOGIN,
							TrackingUtil.EVENT_ACTION_LOGIN_EXECUTION,
							TrackingUtil.EVENT_LABEL_LOGIN_EXEC_BUTTON, 1);

					DbgUtil.showDebug(TAG, "Login Button pressed");
					if (NetworkUtil.isNetworkAvailable(activity, mHandler)) {
						SpannableStringBuilder sbUsername = (SpannableStringBuilder) mUserNameEditBox
								.getText();
						String userName = sbUsername.toString();
						String result = checkAndShowErrorForUserName(userName);
						if (result == null) {
							SpannableStringBuilder sbPassword = (SpannableStringBuilder) mPasswordEditBox
									.getText();
							String password = sbPassword.toString();
							DbgUtil.showDebug(TAG, "password: " + password);
							String resultPassword = checkAndShowErrorForPassword(password);
							if (resultPassword == null) {
								try {
									DbgUtil.showDebug(TAG, "sendLoginData");

									if (mProgressHelper != null) {
										mProgressHelper
												.showProgressDialog(
														mActivity,
														getString(R.string.str_login_progress_title),
														getString(R.string.str_generic_wait_desc),
														TAG);
									}

									sendLoginData(mWebAPI, activity, userName,
											password);
								} catch (WebAPIException e) {
									DbgUtil.showDebug(TAG, e.getMessage());
								}
							} else {
								mSignInResultView.setVisibility(View.VISIBLE);
								mSignInResultView.setText(resultPassword);
								DbgUtil.showDebug(TAG, "Password is Invalid:"
										+ resultPassword);
							}
						} else {
							mSignInResultView.setVisibility(View.VISIBLE);
							mSignInResultView.setText(result);
							DbgUtil.showDebug(TAG, "User name is Invalid.");
						}
					}
				}
			}
		});
	}

	public void onPause() {
		DbgUtil.showDebug(TAG, "onPause");
		super.onPause();
		if (mWebAPI != null) {
			DbgUtil.showDebug(TAG, "Interrupt");
			mWebAPI.interrupt();
		}

		if (mProgressHelper != null) {
			mProgressHelper.dismissDialog(mActivity, TAG);
		}

	}

	private void checkInputAndChangeButtonState() {
		if (mLoginButton != null) {
			if (mUserNamelength != 0) {
				if (mPasswordLength != 0) {
					mLoginButton.setEnabled(true);
				} else {
					mLoginButton.setEnabled(false);
				}
			} else {
				mLoginButton.setEnabled(false);
			}

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.menu_login_help:
			DbgUtil.showDebug(TAG, "Login help");
			FriendListActivityUtil
					.startActivityForHelp(getApplicationContext());
			break;
		}
		return false;
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
	public void onResume() {
		super.onResume();
		mSignInResultView.setText("");
		mSignInResultView.setVisibility(View.GONE);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mWebAPI != null) {
			mWebAPI.removeListener();
		}
	}

	private String checkAndShowErrorForUserName(String userName) {
		DbgUtil.showDebug(TAG, "checkAndShowErrorForUserName");
		String result = null;
		if (userName != null) {
			if (LoginActivityUtil.isValidInputString(
					LcomConst.MIN_USER_NAME_LENGTH,
					LcomConst.MAX_USER_NAME_LENGTH, userName)) {
				if (LoginActivityUtil.isHalfSizeString(userName)) {
					result = null;
				} else {
					DbgUtil.showDebug(TAG, "Not half size mail address");
					result = getString(R.string.str_login_login_fail_username_not_half_char);
				}
			} else {
				result = getString(R.string.str_login_login_fail_username_invalid_length);
			}
		} else {
			result = getString(R.string.str_login_login_fail_username_null);
		}
		return result;
	}

	private String checkAndShowErrorForPassword(String password) {
		String result = null;
		if (password != null) {
			if (LoginActivityUtil.isValidInputString(
					LcomConst.MIN_USER_NAME_LENGTH,
					LcomConst.MAX_USER_NAME_LENGTH, password)) {
				if (LoginActivityUtil.isHalfSizeString(password)) {
					result = null;
				} else {
					result = getString(R.string.str_login_login_fail_password_not_half_char);
				}
			} else {
				result = getString(R.string.str_login_login_fail_password_invalid_length);
			}
		} else {
			result = getString(R.string.str_login_login_fail_password_null);
		}
		return result;
	}

	@Override
	public void onResponseReceived(List<String> respList) {
		DbgUtil.showDebug(TAG, "onResponseReceived");

		if (mProgressHelper != null) {
			mProgressHelper.dismissDialog(mActivity, TAG);
		}

		if (respList != null) {
			parseAndHandleResponse(respList);
		}
	}

	@Override
	public void onAPITimeout() {
		DbgUtil.showDebug(TAG, "onAPITimeout");

		if (mProgressHelper != null) {
			mProgressHelper.dismissDialog(mActivity, TAG);
		}
		FeedbackUtil.showTimeoutToast(getApplicationContext(), mHandler);
	}

	private void sendLoginData(LcomServerAccessor webAPI, Activity activity,
			String userName, String password) throws WebAPIException {
		if (activity != null) {
			// String origin = activity.getPackageName()
			// + activity.getApplicationInfo().getClass();
			String value[] = { TAG, userName, password,
					String.valueOf(LcomConst.API_LEVEL) };
			String key[] = { LcomConst.SERVLET_ORIGIN,
					LcomConst.SERVLET_USER_NAME, LcomConst.SERVLET_PASSWORD,
					LcomConst.SERVLET_API_LEVEL };

			if (webAPI == null) {
				throw new WebAPIException("WebAPI instance is null");
			} else {
				webAPI.sendData(LcomConst.SERVLET_NAME_LOGIN, key, value,
						userName);
			}
		} else {
			throw new WebAPIException("activity is null");
		}
	}

	private void parseAndHandleResponse(List<String> respList) {
		DbgUtil.showDebug(TAG, "parseAndHandleResponse");
		try {
			String origin = respList.get(0);
			String result = respList.get(1);
			final String userId = respList.get(2);
			final String userName = respList.get(3);

			DbgUtil.showDebug(TAG, "origin: " + origin);
			if (origin != null && origin.equals(TAG)) {
				if (result != null) {
					switch (Integer.valueOf(result)) {
					case LcomConst.LOGIN_RESULT_OK:
						DbgUtil.showDebug(TAG, "LOGIN_RESULT_OK");
						LoginActivityUtil.storeUserDataToPref(
								getApplicationContext(),
								Integer.valueOf(userId), userName);
						LoginActivityUtil.startActivityForFriendList(
								getApplicationContext(),
								Integer.valueOf(userId), userName);
						break;
					case LcomConst.LOGIN_RESULT_PARAMETER_NULL:
						FeedbackUtil
								.showFeedbackToast(
										getApplicationContext(),
										mHandler,
										getString(R.string.str_login_login_fail_login_failed));
						break;
					case LcomConst.LOGIN_RESULT_LOGIN_FAILED:
						FeedbackUtil
								.showFeedbackToast(
										getApplicationContext(),
										mHandler,
										getString(R.string.str_login_login_fail_no_user));
						break;
					default:
						TrackingUtil.trackExceptionMessage(
								getApplicationContext(), TAG,
								"illegal origin - switch is default");
						break;
					}
				} else {
					DbgUtil.showDebug(TAG, "invalid result");
					FeedbackUtil
							.showFeedbackToast(
									getApplicationContext(),
									mHandler,
									getString(R.string.str_login_login_fail_unknown_error));
					TrackingUtil.trackExceptionMessage(getApplicationContext(),
							TAG, "illegal result - result is null");
				}
			} else {
				DbgUtil.showDebug(TAG, "invalid origin");
				TrackingUtil.trackExceptionMessage(getApplicationContext(),
						TAG,
						"illegal origin - origin is null or unexpected case");

				FeedbackUtil.showFeedbackToast(getApplicationContext(),
						mHandler,
						getString(R.string.str_login_login_fail_unknown_error));
			}
		} catch (IndexOutOfBoundsException e) {
			DbgUtil.showDebug(TAG,
					"IndexOutOfBoundsException: " + e.getMessage());

			TrackingUtil.trackExceptionMessage(getApplicationContext(), TAG,
					"IndexOutOfBoundsException: " + e.getMessage());

			FeedbackUtil.showFeedbackToast(getApplicationContext(), mHandler,
					getString(R.string.str_login_login_fail_unknown_error));
		}
	}
}
