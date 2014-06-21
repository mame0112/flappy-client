package com.mame.flappy.ui;

import com.mame.flappy.LcomBaseActivity;
import com.mame.flappy.R;
import com.mame.flappy.constant.LcomConst;

import android.app.Activity;
import android.os.Bundle;

public class MyProfileActivity extends LcomBaseActivity {
	private final String TAG = LcomConst.TAG + "/MyProfileActivity";

	private int mUserId = LcomConst.NO_USER;

	private String mUserName = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myprofile);

	}

}
