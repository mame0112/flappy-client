package com.mame.lcom.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mame.lcom.R;
import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.util.DbgUtil;

public class HelpActivity extends Activity {

	private final String TAG = LcomConst.TAG + "/HelpActivity";

	private ListView mListView = null;

	private ArrayAdapter<String> mAdapter = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);

		mListView = (ListView) findViewById(R.id.helpListView);
		mAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1);

		ActionBar actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);

		Resources res = getResources();
		String[] itemArray = res.getStringArray(R.array.help_label);

		for (int i = 0; i < itemArray.length; i++) {
			mAdapter.add(itemArray[i]);
			DbgUtil.showDebug(TAG, "itemArray[i]: " + itemArray[i]);
		}
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long aid) {
				DbgUtil.showDebug(TAG, "position: " + position);
				switch (position) {
				// What is this
				case 0:
					openFlappyAbout();
					break;
				// Privacy policy
				case 1:
					openPrivacyPolicy();
					break;
				// Terms of service
				case 2:
					openTermsOfService();
					break;
				// Contact
				case 3:
					Intent intent = new Intent(getApplicationContext(),
							ContactToUsActivity.class);
					intent.setAction(Intent.ACTION_VIEW);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
					break;
				default:
					break;
				}

			}

		});

	}

	private void openFlappyAbout() {
		Intent intent = new Intent(getApplicationContext(),
				FlappyAboutActivity.class);
		intent.setAction(Intent.ACTION_VIEW);
		startActivity(intent);
	}

	private void openPrivacyPolicy() {
		Uri uri = Uri.parse(LcomConst.BASE_URL + LcomConst.PRIVACY_AUTOHRITY);
		Intent i = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(i);
	}

	private void openTermsOfService() {
		Uri uri = Uri.parse(LcomConst.BASE_URL + LcomConst.TOC_AUTOHRITY);
		Intent i = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(i);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.help_menu, menu);
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
