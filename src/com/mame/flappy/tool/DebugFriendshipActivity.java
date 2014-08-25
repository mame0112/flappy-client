package com.mame.flappy.tool;

import java.util.ArrayList;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.mame.flappy.LcomBaseActivity;
import com.mame.flappy.R;
import com.mame.flappy.constant.LcomConst;
import com.mame.flappy.datamanager.FriendDataManager.FriendDataManagerListener;
import com.mame.flappy.db.UserLocalDataHandler;
import com.mame.flappy.exception.FriendDataManagerException;
import com.mame.flappy.exception.UserLocalDataHandlerException;
import com.mame.flappy.util.DbgUtil;
import com.mame.flappy.util.TrackingUtil;

public class DebugFriendshipActivity extends LcomBaseActivity {

	private final String TAG = LcomConst.TAG + "/DebugFriendshipActivity";

	private DebugLocalDataHandler mLocalDataHandler = null;

	private EditText mUserIdEditText = null;

	private EditText mUserNameEditText = null;

	private EditText mFriendIdEditText = null;

	private EditText mFriendNameEditText = null;

	private EditText mSenderIdEditText = null;

	private EditText mMessageEditText = null;

	private EditText mDateEditText = null;

	private EditText mMailAddressEditText = null;

	private Button mAddButton = null;

	private Button mDummyLocalButton = null;

	private Button mDummyFriendshipButton = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.debug_friendship);

		mLocalDataHandler = new DebugLocalDataHandler(getApplicationContext());

		mUserIdEditText = (EditText) findViewById(R.id.debugFriendshipUserId);

		mUserNameEditText = (EditText) findViewById(R.id.debugFriendshipUesrName);

		mFriendIdEditText = (EditText) findViewById(R.id.debugFriendshipFriendId);

		mFriendNameEditText = (EditText) findViewById(R.id.debugFriendshipFriendName);

		mSenderIdEditText = (EditText) findViewById(R.id.debugFriendshipSenderId);

		mMessageEditText = (EditText) findViewById(R.id.debugFriendshipMessage);

		mDateEditText = (EditText) findViewById(R.id.debugFriendshipDate);

		mMailAddressEditText = (EditText) findViewById(R.id.debugFriendshipMail);

		mAddButton = (Button) findViewById(R.id.debugFriendshipAddButton);
		mAddButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				SpannableStringBuilder sbUserId = (SpannableStringBuilder) mUserIdEditText
						.getText();
				String userId = sbUserId.toString();

				SpannableStringBuilder sbUserName = (SpannableStringBuilder) mUserNameEditText
						.getText();
				String userName = sbUserName.toString();

				SpannableStringBuilder sbFriendId = (SpannableStringBuilder) mFriendIdEditText
						.getText();
				String friendId = sbFriendId.toString();

				SpannableStringBuilder sbFriendName = (SpannableStringBuilder) mFriendNameEditText
						.getText();
				String friendName = sbFriendName.toString();

				SpannableStringBuilder sbSenderId = (SpannableStringBuilder) mSenderIdEditText
						.getText();
				String senderId = sbSenderId.toString();

				SpannableStringBuilder sbMessage = (SpannableStringBuilder) mMessageEditText
						.getText();
				String message = sbMessage.toString();

				SpannableStringBuilder sbDate = (SpannableStringBuilder) mDateEditText
						.getText();
				String date = sbDate.toString();

				SpannableStringBuilder sbMailAddress = (SpannableStringBuilder) mMailAddressEditText
						.getText();
				String mailAddress = sbMailAddress.toString();

				try {
					mLocalDataHandler.addNewMessageAndFriendIfNecessary(
							Integer.valueOf(userId), Integer.valueOf(friendId),
							userName, friendName, Integer.valueOf(senderId),
							message, date, null, mailAddress);
				} catch (NumberFormatException e) {
					DbgUtil.showDebug(TAG,
							"NumberFormatException: " + e.getMessage());
				} catch (UserLocalDataHandlerException e) {
					DbgUtil.showDebug(TAG,
							"NumberFormatException: " + e.getMessage());
				}
			}

		});

		mDummyLocalButton = (Button) findViewById(R.id.debugAddDummyDataButton);
		mDummyLocalButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new save1000DummyUserDataAsyncTask().execute();

			}

		});

		mDummyFriendshipButton = (Button) findViewById(R.id.debugAddDummyFriendshipButton);
		mDummyFriendshipButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new save1000DummyFriendshipDataAsyncTask().execute();

			}

		});
	}

	private class save1000DummyUserDataAsyncTask extends
			AsyncTask<Void, Void, Boolean> {

		public save1000DummyUserDataAsyncTask() {
			DbgUtil.showDebug(TAG, "save1000DummyUserDataAsyncTask");
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			DbgUtil.showDebug(TAG, "doInBackground");
			mLocalDataHandler.saveDummy1000DummyData();
			return null;
		}

		@Override
		protected void onPostExecute(Boolean isSuccess) {
			DbgUtil.showDebug(TAG,
					"save1000DummyUserDataAsyncTask onPostExecute");

		}
	}

	private class save1000DummyFriendshipDataAsyncTask extends
			AsyncTask<Void, Void, Boolean> {

		public save1000DummyFriendshipDataAsyncTask() {
			DbgUtil.showDebug(TAG, "save1000DummyFriendshipDataAsyncTask");
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			DbgUtil.showDebug(TAG, "doInBackground");
			mLocalDataHandler.saveDummy1000DummyFriendshipData();
			return null;
		}

		@Override
		protected void onPostExecute(Boolean isSuccess) {
			DbgUtil.showDebug(TAG,
					"save1000DummyFriendshipDataAsyncTask onPostExecute");

		}
	}

}
