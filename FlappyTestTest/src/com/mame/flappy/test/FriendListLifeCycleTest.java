package com.mame.flappy.test;

import java.util.ArrayList;

import com.mame.flappy.data.FriendListData;
import com.mame.flappy.datamanager.FriendDataManager;
import com.mame.flappy.test.util.ReflectionUtil;
import com.mame.flappy.ui.ConversationActivity;
import com.mame.flappy.ui.FriendListActivity;
import com.mame.flappy.ui.FriendListActivity.FriendListBroadcastReceiver;
import com.mame.flappy.ui.ProgressDialogFragment;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.test.ActivityUnitTestCase;

public class FriendListLifeCycleTest extends
		ActivityUnitTestCase<FriendListActivity> {

	private Activity mActivity = null;

	private ProgressDialogFragment mProgressDialog = null;

	public FriendListLifeCycleTest(Class<FriendListActivity> activityClass) {
		super(activityClass);

	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		startActivity(new Intent(), null, null);

		mActivity = getActivity();

	}

	public void testOnResume() {
		final FriendListActivity friendList = new FriendListActivity();

		mActivity = getActivity();
		startActivity(new Intent(), null, null);
		FriendDataManager manager = FriendDataManager.getInstance();
		ReflectionUtil.setFieldValue(FriendListActivity.class, friendList,
				"mManager", manager);

		Instrumentation instrumentation = getInstrumentation();
		instrumentation.callActivityOnResume(mActivity);

		FriendDataManager dm = (FriendDataManager) ReflectionUtil.getValue(
				FriendListActivity.class, "mManager", friendList);

		boolean isRegistered = dm.isListenerAlreadyRegistered(friendList);

		assertTrue(isRegistered == true);

		boolean isExistingDataAvailable = (Boolean) ReflectionUtil
				.getValue(FriendListActivity.class, "isExistingDataAvailable",
						friendList);
		assertTrue(isExistingDataAvailable == false);

		boolean isNowLoading = (Boolean) ReflectionUtil.getValue(
				FriendListActivity.class, "isNowLoading", friendList);
		assertTrue(isNowLoading == true);

		FriendListBroadcastReceiver mPushReceiver = (FriendListBroadcastReceiver) ReflectionUtil
				.getValue(FriendListActivity.class, "mPushReceiver", friendList);

		assertNotNull(mPushReceiver);

	}

	public void testOnActivityResultWithCorrectRequestCode() throws Exception {
		startActivity(new Intent(), null, null);
		final FriendListActivity friendList = new FriendListActivity();

		int correctRequestCode = (Integer) ReflectionUtil.getValue(
				FriendListActivity.class, "REQUEST_CODE", friendList);

		FriendDataManager manager = FriendDataManager.getInstance();
		ReflectionUtil.setFieldValue(FriendListActivity.class, friendList,
				"mManager", manager);

		// TODO
		friendList.onCreateOptionsMenu(null);
		Intent intent = new Intent();
		friendList.onActivityResult(correctRequestCode, Activity.RESULT_OK,
				intent);

		boolean isNewDataAvailable = (Boolean) ReflectionUtil.getValue(
				FriendListActivity.class, "isNewDataAvailable", friendList);
		assertTrue(isNewDataAvailable == false);

		boolean isExistingDataAvailable = (Boolean) ReflectionUtil
				.getValue(FriendListActivity.class, "isExistingDataAvailable",
						friendList);
		assertTrue(isExistingDataAvailable == false);

		boolean isNowLoading = (Boolean) ReflectionUtil.getValue(
				FriendListActivity.class, "isNowLoading", friendList);
		assertTrue(isNowLoading == true);

	}

}
