package com.mame.lcom.tool;

import android.app.Activity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.mame.lcom.R;
import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.db.UserLocalDataHandler;
import com.mame.lcom.exception.UserLocalDataHandlerException;
import com.mame.lcom.util.DbgUtil;

public class DebugFriendshipActivity extends Activity {

	private final String TAG = LcomConst.TAG + "/DebugFriendshipActivity";

	private UserLocalDataHandler mLocalDataHandler = null;

	private EditText mUserIdEditText = null;

	private EditText mUserNameEditText = null;

	private EditText mFriendIdEditText = null;

	private EditText mFriendNameEditText = null;

	private EditText mSenderIdEditText = null;

	private EditText mMessageEditText = null;

	private EditText mDateEditText = null;

	private EditText mMailAddressEditText = null;

	private Button mAddButton = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.debug_friendship);

		mLocalDataHandler = new UserLocalDataHandler(getApplicationContext());

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

	}

}
