package com.mame.flappy.ui;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.mame.flappy.R;
import com.mame.flappy.constant.LcomConst;
import com.mame.flappy.util.DbgUtil;
import com.mame.flappy.util.PreferenceUtil;
import com.mame.flappy.util.TrackingUtil;
import com.mame.flappy.web.LcomServerAccessor;

public class WelcomeActivity extends Activity implements
		LcomServerAccessor.LcomServerAccessorListener {

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

//		String value[] = { TAG, "aaaa", "bbbb",
//				String.valueOf(LcomConst.API_LEVEL) };
//		String key[] = { LcomConst.SERVLET_ORIGIN, LcomConst.SERVLET_USER_NAME,
//				LcomConst.SERVLET_PASSWORD, LcomConst.SERVLET_API_LEVEL };
//
//		LcomServerAccessor accessor = new LcomServerAccessor();
//		accessor.setListener(this);
//		accessor.sendData(LcomConst.SERVLET_NAME_LOGIN, key, value);

		// LcomHttpsWebAPI mWebAPI = new LcomHttpsWebAPI();
		// mWebAPI.setListener(this);
		// mWebAPI.sendData(LcomConst.SERVLET_NAME_LOGIN, key, value);
	}

	public void onStop() {
		super.onStop();
		TrackingUtil.trackActivityStop(this);
	}

	@Override
	public void onResponseReceived(List<String> respList) {
		DbgUtil.showDebug(TAG, "onResponse");
		if (respList != null) {
			for (String str : respList) {
				DbgUtil.showDebug(TAG, "size: " + str);
			}
		}

	}

	@Override
	public void onAPITimeout() {
		DbgUtil.showDebug(TAG, "onAPITimeout");

	}
}
