package com.mame.flappy.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.mame.flappy.R;
import com.mame.flappy.constant.LcomConst;
import com.mame.flappy.util.DbgUtil;

public class ContactToUsActivity extends Activity {

	private final String TAG = LcomConst.TAG + "/ContactToUsActivity";

	private Button mSendMailButton = null;

	private EditText mCommentEditText = null;

	private Spinner mTitleSpinner = null;

	private final static String MAIL_TO = "mailto:";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_to_us);

		DbgUtil.showDebug(TAG, "onCreate");

		mCommentEditText = (EditText) findViewById(R.id.contactToUsComment);

		mTitleSpinner = (Spinner) findViewById(R.id.contactToUsTitle);

		mSendMailButton = (Button) findViewById(R.id.contactToUsButton);
		mSendMailButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				DbgUtil.showDebug(TAG, "sendMail button");

				SpannableStringBuilder comment = (SpannableStringBuilder) mCommentEditText
						.getText();
				String commentText = comment.toString();

				String item = (String) mTitleSpinner.getSelectedItem();
				DbgUtil.showDebug(TAG, "item: " + item);

				Uri uri = Uri.parse(MAIL_TO + LcomConst.FLAPPY_MAIL_ADDRESS);
				Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
				if (item != null) {
					intent.putExtra(Intent.EXTRA_SUBJECT,
							getString(R.string.str_help_mail_title) + " / "
									+ item);
				} else {
					intent.putExtra(Intent.EXTRA_TEXT,
							getString(R.string.str_help_mail_title) + " / "
									+ getString(R.string.str_help_no_subject));
				}

				if (commentText != null) {
					intent.putExtra(Intent.EXTRA_TEXT, commentText);
				} else {
					intent.putExtra(Intent.EXTRA_TEXT,
							getString(R.string.str_help_no_comment));
				}

				startActivity(intent);
			}

		});

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.contact_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return false;
	}
}
