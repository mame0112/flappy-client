package com.mame.flappy.ui;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.http.HttpStatus;

import com.mame.flappy.R;
import com.mame.flappy.constant.LcomConst;
import com.mame.flappy.util.DbgUtil;
import com.mame.flappy.util.PreferenceUtil;
import com.mame.flappy.util.TrackingUtil;
import com.mame.flappy.web.LcomHttpsWebAPI;
import com.mame.flappy.web.LcomHttpsWebAPI.HttpResult;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class WelcomeActivity extends Activity {

	private final String TAG = LcomConst.TAG + "/WelcomeActivity";

	private Button mSigninButton = null;

	private Button mCreateAccountButton = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);

		final String userName = PreferenceUtil
				.getUserName(getApplicationContext());
		final int userId = PreferenceUtil.getUserId(getApplicationContext());
		DbgUtil.showDebug(TAG, "userName: " + userName + " userId: " + userId);

		// If the user has already logged in to this service before
		if (userName != null && userId != LcomConst.NO_USER) {
			LoginActivityUtil
					.startActivityForFriendList(this, userId, userName);
			finish();
		}

		mSigninButton = (Button) findViewById(R.id.welcomeSigninButton);
		mSigninButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				DbgUtil.showDebug(TAG, "Welcome finish button pressed");
				TrackingUtil.trackEvent(getApplicationContext(),
						TrackingUtil.EVENT_CATEGORY_CREATE_ACCOUNT,
						TrackingUtil.EVENT_ACTION_START_OPERATION_EXECUTION,
						TrackingUtil.EVENT_LABEL_LOGIN_BUTTON, 1);
				// PreferenceUtil.setFirstTime(getApplicationContext(), false);
				Intent intent = new Intent(getApplicationContext(),
						LoginActivity.class);
				intent.setAction(Intent.ACTION_VIEW);
				intent.putExtra(LcomConst.EXTRA_USER_ID, userId);
				intent.putExtra(LcomConst.EXTRA_USER_NAME, userName);
				startActivity(intent);
			}
		});

		mCreateAccountButton = (Button) findViewById(R.id.welcomeCreateAccountButton);
		mCreateAccountButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				DbgUtil.showDebug(TAG, "create account button pressed");
				TrackingUtil.trackEvent(getApplicationContext(),
						TrackingUtil.EVENT_CATEGORY_CREATE_ACCOUNT,
						TrackingUtil.EVENT_ACTION_START_OPERATION_EXECUTION,
						TrackingUtil.EVENT_LABEL_CREATE_ACCOUNT_BUTTON, 1);
				Intent intent = new Intent(getApplicationContext(),
						CreateAccountActivity.class);
				intent.setAction(Intent.ACTION_VIEW);
				intent.putExtra(LcomConst.EXTRA_USER_ID, userId);
				intent.putExtra(LcomConst.EXTRA_USER_NAME, userName);
				startActivity(intent);
			}

		});
	}

	@Override
	public void onStart() {
		super.onStart();
		TrackingUtil.trackActivityStart(this);

//		String url = "https://loosecommunication.appspot.com/" + LcomConst.SERVLET_NAME_LOGIN;
//		DbgUtil.showDebug(TAG, "url: " + url);
//
//		Future<LcomHttpsWebAPI.HttpResult> future = Executors
//				.newSingleThreadExecutor().submit(new LcomHttpsWebAPI(url));
//		HttpResult result;
//		try {
//			result = future.get();
//			DbgUtil.showDebug(TAG, "statuscode: " + result.getStatusCode());
//			if (result.getStatusCode() == HttpStatus.SC_OK) {
//				byte[] tmp = result.getBytes();
//				try {
//					DbgUtil.showDebug(TAG, "byte: " + new String(tmp, "UTF-8"));
//				} catch (UnsupportedEncodingException e) {
//					DbgUtil.showDebug(TAG,
//							"UnsupportedEncodingException: " + e.getMessage());
//				}
//				DbgUtil.showDebug(TAG, "string: " + result.getString());
//			}
//		} catch (InterruptedException e) {
//			DbgUtil.showDebug(TAG, "InterruptedException: " + e.getMessage());
//		} catch (ExecutionException e) {
//			DbgUtil.showDebug(TAG, "ExecutionException: " + e.getMessage());
//		}

	}

	public void onStop() {
		super.onStop();
		TrackingUtil.trackActivityStop(this);
	}
}
