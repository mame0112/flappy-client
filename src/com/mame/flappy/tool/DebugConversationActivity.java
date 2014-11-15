package com.mame.flappy.tool;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.mame.flappy.LcomBaseActivity;
import com.mame.flappy.R;
import com.mame.flappy.constant.LcomConst;
import com.mame.flappy.db.UserLocalDataHandler;
import com.mame.flappy.exception.UserLocalDataHandlerException;
import com.mame.flappy.web.LcomHttpWebAPI;
import com.mame.flappy.web.LcomServerAccessor;
import com.mame.flappy.web.LcomHttpWebAPI.LcomWebAPIListener;

public class DebugConversationActivity extends LcomBaseActivity implements
		LcomServerAccessor.LcomServerAccessorListener {

	private UserLocalDataHandler mLocalDataHandler = null;

	private EditText mUserIdEditText = null;

	private EditText mUserNameEditText = null;

	private EditText mFriendIdEditText = null;

	private EditText mFriendNameEditText = null;

	private EditText mSenderIdEditText = null;

	private EditText mMessageEditText = null;

	private EditText mDateEditText = null;

	private Button mAddButton = null;

	private Button mAddServerButton = null;

	private LcomServerAccessor mWebAPI = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.debug_conversation);

		mWebAPI = new LcomServerAccessor();
		mWebAPI.setListener(this);

		mLocalDataHandler = new UserLocalDataHandler(getApplicationContext());

		mUserIdEditText = (EditText) findViewById(R.id.debugConversationUserId);

		mUserNameEditText = (EditText) findViewById(R.id.debugConversationUesrName);

		mFriendIdEditText = (EditText) findViewById(R.id.debugConversationFriendId);

		mFriendNameEditText = (EditText) findViewById(R.id.debugConversationFriendName);

		mSenderIdEditText = (EditText) findViewById(R.id.debugConversationSenderId);

		mMessageEditText = (EditText) findViewById(R.id.debugConversationMessage);

		mDateEditText = (EditText) findViewById(R.id.debugConversationDate);

		mAddButton = (Button) findViewById(R.id.debugConversationAddButton);
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

				try {
					mLocalDataHandler.addNewMessage(Integer.valueOf(userId),
							Integer.valueOf(friendId), userName, friendName,
							Integer.valueOf(senderId), message, date);
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UserLocalDataHandlerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});

		mAddServerButton = (Button) findViewById(R.id.debugConversationServerAddButton);
		mAddServerButton.setOnClickListener(new OnClickListener() {

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
				String origin = "DEBUG_SEND_AND_ADD_DATA";
				String key[] = { LcomConst.SERVLET_ORIGIN,
						LcomConst.SERVLET_USER_ID, LcomConst.SERVLET_USER_NAME,
						LcomConst.SERVLET_TARGET_USER_ID,
						LcomConst.SERVLET_TARGET_USER_NAME,
						LcomConst.SERVLET_MESSAGE_BODY,
						LcomConst.SERVLET_MESSAGE_DATE,
						LcomConst.SERVLET_API_LEVEL };
				String value[] = { origin, String.valueOf(userId), userName,
						String.valueOf(friendId), friendName, message, date,
						String.valueOf(LcomConst.API_LEVEL) };
				// TODO Should we use servlet with request new user data case?
				mWebAPI.sendData(LcomConst.DEBUG_SERVLET, key, value, userName);
				// mLocalDataHandler.addNewMessage(Integer.valueOf(userId),
				// Integer.valueOf(friendId), userName, friendName,
				// Integer.valueOf(senderId), message, date);
			}

		});

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mWebAPI != null) {
			mWebAPI.removeListener();
		}
	}

	@Override
	public void onResponseReceived(List<String> respList) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAPITimeout() {
		// TODO Auto-generated method stub

	}

}
