package com.mame.flappy.test;

import java.util.ArrayList;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;

import com.mame.flappy.R;
import com.mame.flappy.constant.LcomConst;
import com.mame.flappy.data.MessageItemData;
import com.mame.flappy.test.util.ReflectionUtil;
import com.mame.flappy.ui.ConversationActivity;
import com.mame.flappy.ui.ConversationListCustonAdapter;
import com.mame.flappy.ui.ProgressDialogFragment;

public class ConversationActivityTest extends
		ActivityInstrumentationTestCase2<ConversationActivity> {

	private final String TAG = LcomConst.TAG + "/ConversationActivity";

	private Activity mActivity = null;

	private boolean mIsNewDataReady = false;

	private boolean mIsPresentDataReady = false;

	private ArrayList<MessageItemData> mConversationData = null;

	private ConversationListCustonAdapter mAdapter = null;

	private ListView mListView = null;

	private ProgressDialogFragment mProgressDialog = null;

	public ConversationActivityTest(Class<ConversationActivity> activityClass) {
		super(activityClass);

	}

	public ConversationActivityTest() {
		super("ConversationActivity", ConversationActivity.class);

	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		mActivity = getActivity();
		mIsNewDataReady = false;
		mIsPresentDataReady = false;
		mConversationData = new ArrayList<MessageItemData>();
		mAdapter = new ConversationListCustonAdapter(mActivity, 0,
				mConversationData);

		mListView = (ListView) mActivity
				.findViewById(R.id.conversationListView);

		mProgressDialog = ProgressDialogFragment.newInstance("Title", "Body");

	}

	public void testNotifyPresentMessageDataLoaded() throws Exception {
		ConversationActivity conversation = new ConversationActivity();

		ArrayList<MessageItemData> messageData = new ArrayList<MessageItemData>();

		MessageItemData data = new MessageItemData(1, 0, "friend name",
				"my name", "hello", 123456789, null);
		MessageItemData dat1 = new MessageItemData(1, 0, "friend name2",
				"my name2", "hello2", 123456799, null);

		messageData.add(dat1);
		messageData.add(data);

		conversation.notifyPresentMessageDataLoaded(messageData);

		mIsPresentDataReady = (Boolean) ReflectionUtil
				.getValue(ConversationActivity.class, "mIsPresentDataReady",
						conversation);
		assertTrue(mIsPresentDataReady);

		mConversationData = (ArrayList<MessageItemData>) ReflectionUtil
				.getValue(ConversationActivity.class, "mConversationData",
						conversation);
		assertEquals(mConversationData.size(), 2);

	}

//	public void testNotifyPresentMessageDataLoadedWithNewDataReady()
//			throws Exception {
//		final ConversationActivity conversation = new ConversationActivity();
//
//		ReflectionUtil.setFieldValue(ConversationActivity.class, conversation,
//				"mIsNewDataReady", true);
//
//		ReflectionUtil.setFieldValue(ConversationActivity.class, conversation,
//				"mAdapter", mAdapter);
//
//		ReflectionUtil.setFieldValue(ConversationActivity.class, conversation,
//				"mActivity", mActivity);
//
//		ReflectionUtil.setFieldValue(ConversationActivity.class, conversation,
//				"mProgressDialog", mProgressDialog);
//
//		mActivity.runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				ReflectionUtil.setFieldValue(ConversationActivity.class,
//						conversation, "mListView", mListView);
//				ReflectionUtil.setFieldValue(ConversationActivity.class,
//						conversation, "mProgressDialog", mProgressDialog);
//
//				mListView.setAdapter(mAdapter);
//
//			}
//		});
//
//		Thread.sleep(2000);
//
//		ArrayList<MessageItemData> messageData = new ArrayList<MessageItemData>();
//
//		MessageItemData data = new MessageItemData(1, 0, "friend name",
//				"my name", "hello", 123456789, null);
//		MessageItemData dat1 = new MessageItemData(1, 0, "friend name2",
//				"my name2", "hello2", 123456799, null);
//
//		messageData.add(dat1);
//		messageData.add(data);
//
//		conversation.notifyPresentMessageDataLoaded(messageData);
//
//		mIsNewDataReady = (Boolean) ReflectionUtil.getValue(
//				ConversationActivity.class, "mIsNewDataReady", conversation);
//		assertTrue(mIsNewDataReady == false);
//
//		mConversationData = (ArrayList<MessageItemData>) ReflectionUtil
//				.getValue(ConversationActivity.class, "mConversationData",
//						conversation);
//		assertEquals(mConversationData.size(), 2);
//
//	}

	public void testNnotifyNewConversationDataLoadedNotReady() throws Exception {

		final ConversationActivity conversation = new ConversationActivity();

		ReflectionUtil.setFieldValue(ConversationActivity.class, conversation,
				"mIsPresentDataReady", false);
		ReflectionUtil.setFieldValue(ConversationActivity.class, conversation,
				"mAdapter", mAdapter);
		ReflectionUtil.setFieldValue(ConversationActivity.class, conversation,
				"mActivity", mActivity);

		ArrayList<MessageItemData> messageData = new ArrayList<MessageItemData>();

		MessageItemData data = new MessageItemData(1, 0, "friend name",
				"my name3", "hello3", 123456789, null);
		MessageItemData dat1 = new MessageItemData(1, 0, "friend name2",
				"my name4", "hello4", 123456799, null);

		messageData.add(dat1);
		messageData.add(data);

		conversation.notifyNewConversationDataLoaded(messageData);

		mIsNewDataReady = (Boolean) ReflectionUtil.getValue(
				ConversationActivity.class, "mIsNewDataReady", conversation);
		assertTrue(mIsNewDataReady == true);

		mConversationData = (ArrayList<MessageItemData>) ReflectionUtil
				.getValue(ConversationActivity.class, "mConversationData",
						conversation);
		assertEquals(mConversationData.size(), 2);
	}

//	public void testNnotifyNewConversationDataLoadedReady() throws Exception {
//
//		final ConversationActivity conversation = new ConversationActivity();
//
//		ReflectionUtil.setFieldValue(ConversationActivity.class, conversation,
//				"mIsPresentDataReady", true);
//		ReflectionUtil.setFieldValue(ConversationActivity.class, conversation,
//				"mAdapter", mAdapter);
//		ReflectionUtil.setFieldValue(ConversationActivity.class, conversation,
//				"mActivity", mActivity);
//
//		mActivity.runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				ReflectionUtil.setFieldValue(ConversationActivity.class,
//						conversation, "mListView", mListView);
//				ReflectionUtil.setFieldValue(ConversationActivity.class,
//						conversation, "mProgressDialog", mProgressDialog);
//
//				mListView.setAdapter(mAdapter);
//
//			}
//		});
//
//		Thread.sleep(2000);
//
//		ArrayList<MessageItemData> messageData = new ArrayList<MessageItemData>();
//
//		MessageItemData data = new MessageItemData(1, 0, "friend name",
//				"my name3", "hello3", 123456789, null);
//		MessageItemData dat1 = new MessageItemData(1, 0, "friend name2",
//				"my name4", "hello4", 123456799, null);
//
//		messageData.add(dat1);
//		messageData.add(data);
//
//		conversation.notifyNewConversationDataLoaded(messageData);
//
//		mIsNewDataReady = (Boolean) ReflectionUtil.getValue(
//				ConversationActivity.class, "mIsNewDataReady", conversation);
//		assertTrue(mIsPresentDataReady == false);
//
//		mConversationData = (ArrayList<MessageItemData>) ReflectionUtil
//				.getValue(ConversationActivity.class, "mConversationData",
//						conversation);
//		assertEquals(mConversationData.size(), 2);
//
//	}
}
