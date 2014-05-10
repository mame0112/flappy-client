package com.mame.flappy.tool;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mame.flappy.R;
import com.mame.flappy.constant.LcomConst;
import com.mame.flappy.exception.WebAPIException;
import com.mame.flappy.web.LcomWebAPI;
import com.mame.flappy.web.LcomWebAPI.LcomWebAPIListener;

public class DebugAllUserDataActivity extends Activity implements
		LcomWebAPIListener {

	private final String TAG = LcomConst.TAG + "/DebugAllUserDataActivity";

	private EditText mAllUserNumberExitText = null;

	private Button mChangeUserNumberButton = null;

	private LcomWebAPI mWebAPI = null;

	private Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.debug_alluser);

		mWebAPI = new LcomWebAPI();
		mWebAPI.setListener(this);

		mAllUserNumberExitText = (EditText) findViewById(R.id.debugAllUserNumberText);

		mChangeUserNumberButton = (Button) findViewById(R.id.debugAllUserNumberButton);
		mChangeUserNumberButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SpannableStringBuilder sbUserNumber = (SpannableStringBuilder) mAllUserNumberExitText
						.getText();
				String totalNumber = sbUserNumber.toString();
				sendLoginData(totalNumber);
			}

		});
	}

	private void sendLoginData(String totalNum) {
		String value[] = { "ALL_USER_DATA", totalNum };
		String key[] = { LcomConst.SERVLET_ORIGIN,
				LcomConst.SERVLET_TOTAL_USER_NUM };
		mWebAPI.sendData(LcomConst.DEBUG_SERVLET, key, value);
	}

	@Override
	public void onResponseReceived(List<String> respList) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(getApplicationContext(), "done.",
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		}).start();

	}

	@Override
	public void onAPITimeout() {
		// TODO Auto-generated method stub

	}
}
