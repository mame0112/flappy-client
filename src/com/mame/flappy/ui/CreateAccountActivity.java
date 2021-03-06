package com.mame.flappy.ui;

import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mame.flappy.LcomBaseActivity;
import com.mame.flappy.R;
import com.mame.flappy.constant.LcomConst;
import com.mame.flappy.exception.WebAPIException;
import com.mame.flappy.util.AlphaNumericFilter;
import com.mame.flappy.util.ButtonUtil;
import com.mame.flappy.util.DbgUtil;
import com.mame.flappy.util.FeedbackUtil;
import com.mame.flappy.util.TrackingUtil;
import com.mame.flappy.web.LcomHttpWebAPI;
import com.mame.flappy.web.LcomServerAccessor;
import com.mame.flappy.web.LcomHttpWebAPI.LcomWebAPIListener;

public class CreateAccountActivity extends LcomBaseActivity implements
		LcomServerAccessor.LcomServerAccessorListener {

	private final String TAG = LcomConst.TAG + "/CreateAccountActivity";

	private TextView mGoNextResultView = null;

	private EditText mUserNameEditText = null;

	private ImageButton mThumbnailButton = null;

	private Button mNextButton = null;

	private int mUserNameLength = 0;

	private final int PHOTO_REQUEST_CODE = 1;

	private final int REQUEST_CROP_PICK = 2;

	// private ProgressDialogFragment mProgressDialog = null;

	private Handler mHandler = new Handler();

	private LcomServerAccessor mWebAPI = null;

	private Bitmap mThumbBitmap = null;

	// private Activity mActivity = null;

	private ProgressDialogFragmentHelper mProgressHelper = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		DbgUtil.showDebug(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.createaccount);

		final Activity activity = this;

		mWebAPI = new LcomServerAccessor();
		mWebAPI.setListener(this);

		ActionBar actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);

		mProgressHelper = new ProgressDialogFragmentHelper();

		mGoNextResultView = (TextView) findViewById(R.id.createResultView);
		mGoNextResultView.setText("");
		mGoNextResultView.setVisibility(View.GONE);

		mThumbnailButton = (ImageButton) findViewById(R.id.selectThumbnailtButton);
		final Activity a = this;
		mThumbnailButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				DbgUtil.showDebug(TAG, "Thumbnail button pressed");

				// Track thumbnail imageView tapped event
				TrackingUtil.trackEvent(getApplicationContext(),
						TrackingUtil.EVENT_CATEGORY_CREATE_ACCOUNT,
						TrackingUtil.EVENT_ACTION_CREATE_ACCOUNT_EXECUTION,
						TrackingUtil.EVENT_LABEL_CREATE_THUMBNAIL_BUTTON, 1);

				CreateAccountActivityUtil.launchPhotoPicker(a,
						PHOTO_REQUEST_CODE);
			}
		});

		mUserNameEditText = (EditText) findViewById(R.id.createUserName);
		// mUserNameEditText
		// .setFilters(new InputFilter[] { new AlphaNumericFilter() });
		mUserNameEditText.addTextChangedListener(new TextWatcher() {
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
				mUserNameLength = s.length();
				changeNextButtonState();
			}
		});

		mNextButton = (Button) findViewById(R.id.accountNextButton);
		mNextButton.setEnabled(false);
		mNextButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				DbgUtil.showDebug(TAG, "next button pressed");

				if (ButtonUtil.isClickable()) {

					// Track next button tapped event
					TrackingUtil.trackEvent(getApplicationContext(),
							TrackingUtil.EVENT_CATEGORY_CREATE_ACCOUNT,
							TrackingUtil.EVENT_ACTION_CREATE_ACCOUNT_EXECUTION,
							TrackingUtil.EVENT_LABEL_CREATE_NEXT_BUTTON, 1);

					SpannableStringBuilder sbUserName = (SpannableStringBuilder) mUserNameEditText
							.getText();
					String userName = sbUserName.toString();
					String result = checkAndShowErrorForUserName(userName);
					if (result != null) {
						mGoNextResultView.setVisibility(View.VISIBLE);
						mGoNextResultView.setText(result);
					} else {
						mGoNextResultView.setVisibility(View.GONE);
						try {
							if (mProgressHelper != null) {
								mProgressHelper
										.showProgressDialog(
												activity,
												getString(R.string.str_create_account_check_name_title),
												getString(R.string.str_create_account_check_name_desc),
												TAG);
							}
							sendcheckUserNameData(userName);
						} catch (WebAPIException e) {
							DbgUtil.showDebug(TAG,
									"WebAPIException: " + e.getMessage());
							TrackingUtil.trackExceptionMessage(
									getApplicationContext(), TAG,
									"WebAPIException: " + e.getMessage());
						}
					}
				}

			}

		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mGoNextResultView != null) {
			mGoNextResultView.setVisibility(View.GONE);
		}

		// if (mProgressDialog != null) {
		// mProgressDialog.getDialog().dismiss();
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

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mWebAPI != null) {
			mWebAPI.removeListener();
		}

	}

	private void sendcheckUserNameData(String userName) throws WebAPIException {
		DbgUtil.showDebug(TAG, "sendcheckUserNameData");
		String origin = TAG;
		String key[] = { LcomConst.SERVLET_ORIGIN, LcomConst.SERVLET_USER_NAME,
				LcomConst.SERVLET_API_LEVEL };
		String value[] = { origin, userName,
				String.valueOf(LcomConst.API_LEVEL) };
		mWebAPI.sendData(LcomConst.SERVLET_NAME_CREATE_ACCOUNT_CHECK_USER_NAME,
				key, value, userName);
	}

	private void changeNextButtonState() {
		if (mUserNameLength >= 4) {
			mNextButton.setEnabled(true);
		} else {
			mNextButton.setEnabled(false);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case PHOTO_REQUEST_CODE:
			if (resultCode == RESULT_OK) {
				try {
					Uri uri = data.getData();
					DbgUtil.showDebug(TAG, "uri: " + uri);
					Intent intent = new Intent("com.android.camera.action.CROP");
					intent.setData(uri);
					intent.putExtra("outputX", 200);
					intent.putExtra("outputY", 200);
					intent.putExtra("aspectX", 1);
					intent.putExtra("aspectY", 1);
					intent.putExtra("scale", true);
					intent.putExtra("return-data", true);
					startActivityForResult(intent, REQUEST_CROP_PICK);
				} catch (Exception e) {
					DbgUtil.showDebug(TAG, "Exception: " + e.getMessage());
				}
			} else {
				DbgUtil.showDebug(TAG, "PHOTO_REQUEST_CODE failed");
				TrackingUtil.trackExceptionMessage(getApplicationContext(),
						TAG, "PHOTO_REQUEST_CODE failed");
			}
			break;
		case REQUEST_CROP_PICK:
			if (resultCode == RESULT_OK) {
				mThumbBitmap = data.getExtras().getParcelable("data");
				if (mThumbBitmap != null) {
					mThumbnailButton.setImageBitmap(mThumbBitmap);
				} else {
					DbgUtil.showDebug(TAG, "bitmap is null");
				}
			} else {
				DbgUtil.showDebug(TAG, "REQUESTS_CROP_PICK failed");
				TrackingUtil.trackExceptionMessage(getApplicationContext(),
						TAG, "REQUESTS_CROP_PICK failed");
			}
			break;
		}
	}

	private String checkAndShowErrorForUserName(String userName) {
		String result = null;
		if (userName != null) {
			if (LoginActivityUtil.isValidInputString(
					LcomConst.MIN_USER_NAME_LENGTH,
					LcomConst.MAX_USER_NAME_LENGTH, userName)) {
				if (LoginActivityUtil.isHalfSizeString(userName)) {
					result = null;
				} else {
					result = getString(R.string.str_create_account_fail_username_not_half_char);
				}
			} else {
				result = getString(R.string.str_create_account_fail_username_invalid_length);
			}
		} else {
			result = getString(R.string.str_create_account_fail_username_null);
		}
		return result;
	}

	@Override
	public void onResponseReceived(List<String> respList) {
		DbgUtil.showDebug(TAG, "onResponseReceived");

		if (mProgressHelper != null) {
			mProgressHelper.dismissDialog(this, TAG);
		}

		if (respList != null) {
			parseAndHandleResponse(respList);
		} else {
			DbgUtil.showDebug(TAG, "respList is null");
			TrackingUtil.trackExceptionMessage(getApplicationContext(), TAG,
					"respList is null");
		}
	}

	@Override
	public void onAPITimeout() {
		DbgUtil.showDebug(TAG, "onAPITimeoput");

		if (mProgressHelper != null) {
			mProgressHelper.dismissDialog(this, TAG);
		}
		FeedbackUtil.showTimeoutToast(getApplicationContext(), mHandler);
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
						CreateAccountActivityUtil
								.startActivityForCreateAccountComplete(this,
										userName, mThumbBitmap);
						break;
					case LcomConst.CREATE_ACCOUNT_USER_ALREADY_EXIST:
						DbgUtil.showDebug(TAG,
								"CREATE_ACCOUNT_USER_ALREADY_EXIST");
						showErrorView(getString(R.string.str_create_account_fail_user_exist));
						break;
					case LcomConst.CREATE_ACCOUNT_PARAMETER_NULL:
						DbgUtil.showDebug(TAG, "CREATE_ACCOUNT_PARAMETER_NULL");
						showErrorView(getString(R.string.str_create_account_fail_server_error));
						break;
					case LcomConst.CREATE_ACCOUNT_UNKNOWN_ERROR:
						DbgUtil.showDebug(TAG, "CREATE_ACCOUNT_UNKNOWN_ERROR");
					default:
						showErrorView(getString(R.string.str_create_account_fail_server_error));
						break;
					}
				}
			} else {
				DbgUtil.showDebug(TAG, "invalid origin");
				showErrorView(getString(R.string.str_create_account_fail_server_error));
			}
		} catch (IndexOutOfBoundsException e) {
			DbgUtil.showDebug(TAG,
					"IndexOutOfBoundsException: " + e.getMessage());
			TrackingUtil.trackExceptionMessage(getApplicationContext(), TAG,
					"IndexOutOfBoundsException: " + e.getMessage());
			showErrorView(getString(R.string.str_create_account_fail_server_error));
		}
	}

	private void showErrorView(final String errorMessage) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						mGoNextResultView.setVisibility(View.VISIBLE);
						mGoNextResultView.setText(errorMessage);
					}
				});
			}
		}).start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.signup_menu, menu);
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
