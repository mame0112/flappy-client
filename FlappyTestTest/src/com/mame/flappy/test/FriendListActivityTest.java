package com.mame.flappy.test;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Handler;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.TextView;

import com.mame.flappy.R;
import com.mame.flappy.constant.LcomConst;
import com.mame.flappy.data.FriendListData;
import com.mame.flappy.datamanager.FriendDataManager;
import com.mame.flappy.test.util.ReflectionUtil;
import com.mame.flappy.ui.ConversationActivity;
import com.mame.flappy.ui.FriendListActivity;
import com.mame.flappy.ui.ProgressDialogFragment;

public class FriendListActivityTest extends
		ActivityInstrumentationTestCase2<FriendListActivity> {

	private final String TAG = LcomConst.TAG + "/FriendListActivityTest";

	private Activity mActivity = null;

	private ProgressDialogFragment mProgressDialog = null;

	private Button mFirstAddButton = null;

	private TextView mFirstAddText = null;

	private Handler mHandler = null;

	private ArrayList<FriendListData> mFriendListData = null;

	public FriendListActivityTest(Class<FriendListActivity> activityClass) {
		super(activityClass);

	}

	public FriendListActivityTest() {
		super("FriendListActivity", FriendListActivity.class);

	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		mActivity = getActivity();
		mProgressDialog = ProgressDialogFragment.newInstance("Title", "Body");
		mFirstAddButton = (Button) mActivity
				.findViewById(R.id.firstFriendAddButton);
		mFirstAddText = (TextView) mActivity
				.findViewById(R.id.firstFriendAddText);
		mHandler = new Handler();

		mFriendListData = new ArrayList<FriendListData>();

		mFirstAddText = null;
	}

	public void testNotifyPresentDatasetWithNewDataNotReady() {
		final FriendListActivity friendList = new FriendListActivity();

		ReflectionUtil.setFieldValue(FriendListActivity.class, friendList,
				"isNewDataAvailable", false);

		ArrayList<FriendListData> userData = new ArrayList<FriendListData>();

		FriendListData data1 = new FriendListData(1, "friend name", 0,
				"Test message", 123455677, 1, "a1@test.com", null);
		FriendListData data2 = new FriendListData(2, "friend name2", 0,
				"Test message2", 123455678, 0, "a2@test.com", null);
		FriendListData data3 = new FriendListData(3, "friend name3", 0,
				"Test message3", 123455679, 2, "a3@test.com", null);

		userData.add(data1);
		userData.add(data2);
		userData.add(data3);

		friendList.notifyPresentDataset(userData);

		ArrayList<FriendListData> mUserData = (ArrayList<FriendListData>) ReflectionUtil
				.getValue(FriendListActivity.class, "mUserData", friendList);
		assertTrue(mUserData.size() == 3);

		boolean isExistingDataAvailable = (Boolean) ReflectionUtil
				.getValue(FriendListActivity.class, "isExistingDataAvailable",
						friendList);
		assertTrue(isExistingDataAvailable == true);
	}

	// public void testCheckAndShowFirstAddButton() {
	// final FriendListActivity friendList = new FriendListActivity();
	// ReflectionUtil.setFieldValue(FriendListActivity.class, friendList,
	// "mFirstAddText", mFirstAddText);
	// ReflectionUtil.setFieldValue(FriendListActivity.class, friendList,
	// "mFirstAddButton", mFirstAddButton);
	// ReflectionUtil.setFieldValue(FriendListActivity.class, friendList,
	// "mHandler", mHandler);
	//
	// // friendList.check
	// }

	public void testNotifyLatestStoredMessageWithFriendListNull() {
		final FriendListActivity friendList = new FriendListActivity();

		ReflectionUtil.setFieldValue(FriendListActivity.class, friendList,
				"mHandler", mHandler);

		ReflectionUtil.setFieldValue(FriendListActivity.class, friendList,
				"mFriendListData", null);

		FriendListData data1 = new FriendListData(1, "friend name", 0,
				"Test message", 123455677, 1, "a1@test.com", null);

		friendList.notifyLatestStoredMessage(data1);

		mFriendListData = (ArrayList<FriendListData>) ReflectionUtil.getValue(
				FriendListActivity.class, "mFriendListData", friendList);
		assertNotNull(mFriendListData);
		assertTrue(mFriendListData.size() == 1);
	}

	public void testNotifyLatestStoredMessageWithFriendListSize0() {
		final FriendListActivity friendList = new FriendListActivity();

		ReflectionUtil.setFieldValue(FriendListActivity.class, friendList,
				"mHandler", mHandler);

		ReflectionUtil.setFieldValue(FriendListActivity.class, friendList,
				"mFriendListData", mFriendListData);

		FriendListData data1 = new FriendListData(1, "friend name", 0,
				"Test message", 123455677, 1, "a1@test.com", null);

		friendList.notifyLatestStoredMessage(data1);

		mFriendListData = (ArrayList<FriendListData>) ReflectionUtil.getValue(
				FriendListActivity.class, "mFriendListData", friendList);
		assertNotNull(mFriendListData);
		assertTrue(mFriendListData.size() == 1);
	}

	public void testNotifyLatestStoredMessageWithFriendListData() {
		final FriendListActivity friendList = new FriendListActivity();

		ReflectionUtil.setFieldValue(FriendListActivity.class, friendList,
				"mHandler", mHandler);

		FriendListData data2 = new FriendListData(1, "friend name2", 0,
				"Test message2", 123455678, 0, "a2@test.com", null);
		FriendListData data3 = new FriendListData(3, "friend name3", 0,
				"Test message3", 123455679, 2, "a3@test.com", null);
		mFriendListData.add(data2);
		mFriendListData.add(data3);

		ReflectionUtil.setFieldValue(FriendListActivity.class, friendList,
				"mFriendListData", mFriendListData);

		FriendListData data1 = new FriendListData(1, "friend name", 0,
				"Test message", 123455677, 1, "a1@test.com", null);

		friendList.notifyLatestStoredMessage(data1);

		mFriendListData = (ArrayList<FriendListData>) ReflectionUtil.getValue(
				FriendListActivity.class, "mFriendListData", friendList);
		assertNotNull(mFriendListData);
		assertTrue(mFriendListData.size() == 2);

		int numOfNewMessage = 0;

		for (FriendListData data : mFriendListData) {
			if (data != null) {
				int friendId = data.getFriendId();
				if (friendId == 1) {
					numOfNewMessage = data.getNumOfNewMessage();
				}
			}
		}

		assertTrue(numOfNewMessage == 1);

	}

	public void testNotifyPresentDatasetWithNewDataReady() {

		mActivity = getActivity();
		mProgressDialog = ProgressDialogFragment.newInstance("Title", "Body");

		final FriendListActivity friendList = new FriendListActivity();

		ReflectionUtil.setFieldValue(FriendListActivity.class, friendList,
				"isNewDataAvailable", true);

		ArrayList<FriendListData> userData = new ArrayList<FriendListData>();

		FriendListData data1 = new FriendListData(1, "friend name", 0,
				"Test message", 123455677, 1, "a1@test.com", null);
		FriendListData data2 = new FriendListData(2, "friend name2", 0,
				"Test message2", 123455678, 0, "a2@test.com", null);
		FriendListData data3 = new FriendListData(3, "friend name3", 0,
				"Test message3", 123455679, 2, "a3@test.com", null);

		userData.add(data1);
		userData.add(data2);
		userData.add(data3);

		ReflectionUtil.setFieldValue(FriendListActivity.class, friendList,
				"mActivity", mActivity);

		// mActivity.runOnUiThread(new Runnable() {
		// @Override
		// public void run() {
		// ReflectionUtil.setFieldValue(ConversationActivity.class,
		// friendList, "mProgressDialog", mProgressDialog);
		//
		// }
		// });
		
		ReflectionUtil.setFieldValue(ConversationActivity.class,
				friendList, "mProgressDialog", mProgressDialog);

		FriendDataManager manager = FriendDataManager.getInstance();
		ReflectionUtil.setFieldValue(FriendListActivity.class, friendList,
				"mManager", manager);

		friendList.notifyPresentDataset(userData);

		ArrayList<FriendListData> mUserData = (ArrayList<FriendListData>) ReflectionUtil
				.getValue(FriendListActivity.class, "mUserData", friendList);
		assertTrue(mUserData.size() == 3);

		boolean isExistingDataAvailable = (Boolean) ReflectionUtil
				.getValue(FriendListActivity.class, "isExistingDataAvailable",
						friendList);
		assertTrue(isExistingDataAvailable == true);
	}
}
