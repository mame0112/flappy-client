package com.mame.flappy.test;

import com.mame.flappy.R;
import com.mame.flappy.ui.LoginActivity;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.TextView;

public class LoginActivityTest extends
		ActivityInstrumentationTestCase2<LoginActivity> {

	private Activity mActivity = null;

	private TextView mSignInResultView = null;

	public LoginActivityTest(Class<LoginActivity> activityClass) {
		super(activityClass);

	}

	public LoginActivityTest() {
		super("LoginActivity", LoginActivity.class);

	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		mActivity = getActivity();
		mSignInResultView = (TextView) mActivity
				.findViewById(R.id.signinResult);

	}

	public void testDisplay() throws Exception {
		assertTrue(mSignInResultView.getVisibility() == View.GONE);
	}

}
