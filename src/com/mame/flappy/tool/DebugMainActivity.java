package com.mame.flappy.tool;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.mame.flappy.LcomBaseActivity;
import com.mame.flappy.R;
import com.mame.flappy.ui.WelcomeActivity;

public class DebugMainActivity extends LcomBaseActivity {

	private Button mBackToMailButton;

	private Button mConversationButton;

	private Button mFriendshipButton;

	private Button mAllUserButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mConversationButton = (Button) findViewById(R.id.debugBackToMainButton);
		mConversationButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(),
						WelcomeActivity.class);
				intent.setAction(Intent.ACTION_VIEW);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				finish();
			}
		});

		mConversationButton = (Button) findViewById(R.id.debugConversationButton);
		mConversationButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(),
						DebugConversationActivity.class);
				intent.setAction(Intent.ACTION_VIEW);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});

		mFriendshipButton = (Button) findViewById(R.id.debugFriendshipButton);
		mFriendshipButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(),
						DebugFriendshipActivity.class);
				intent.setAction(Intent.ACTION_VIEW);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});

		mAllUserButton = (Button) findViewById(R.id.debugAllUserButton);
		mAllUserButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						DebugAllUserDataActivity.class);
				intent.setAction(Intent.ACTION_VIEW);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});

	}
}
