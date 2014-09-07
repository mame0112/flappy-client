package com.mame.flappy.ui;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.mame.flappy.LcomBaseActivity;
import com.mame.flappy.R;
import com.mame.flappy.constant.LcomConst;
import com.mame.flappy.data.ContactsListData;
import com.mame.flappy.db.ContactListDataLoaderManager;
import com.mame.flappy.db.ContactListThumbnailLoader;
import com.mame.flappy.util.DbgUtil;
import com.mame.flappy.util.TrackingUtil;

public class ContactListActivity extends LcomBaseActivity implements
		ContactListDataLoaderManager.ContactListDataLoaderListener {

	private final String TAG = LcomConst.TAG + "/ContactListActivity";

	private ContentResolver mResolver = null;

	private ContactsListAdapter mAdapter = null;

	private ListView mListView = null;

	private ArrayList<ContactsListData> mContactsListData = new ArrayList<ContactsListData>();

	private ContentResolver mRevoler = null;

	private ContactListDataLoaderManager mLoaderManager = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.contactslist);

		ActionBar actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);

		mLoaderManager = new ContactListDataLoaderManager(
				getApplicationContext());

		mAdapter = new ContactsListAdapter(getApplicationContext(), 0,
				mContactsListData);

		mListView = (ListView) findViewById(R.id.contactsListListView);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				DbgUtil.showDebug(TAG, "onItemClick positon: " + position);
				ContactsListData data = mContactsListData.get(position);
				DbgUtil.showDebug(TAG, "name: " + data.getContactName()
						+ " mail: " + data.getMailAddress());
				// showConfirmDialog(data.getContactName(),
				// data.getMailAddress());
				Intent result = new Intent();
				result.putExtra(LcomConst.RESULT_EXTRA_CONTACT_NAME,
						data.getContactName());
				result.putExtra(LcomConst.RESULT_EXTRA_CONTACT_ADDRESS,
						data.getMailAddress());
				setResult(RESULT_OK, result);
				finish();
			}
		});
		mListView.setAdapter(mAdapter);
	}

	@Override
	public void onStart() {
		super.onStart();
		TrackingUtil.trackActivityStart(this);
	}

	public void onStop() {
		super.onStop();
		TrackingUtil.trackActivityStop(this);
	}

	@Override
	public void onResume() {
		super.onResume();

		if (mContactsListData != null) {
			mContactsListData.clear();
		}

		if (mLoaderManager != null) {
			mLoaderManager.executeContactLoad();
		}

		mRevoler = getContentResolver();

		DbgUtil.showDebug(TAG, "onResume");

		setProgressBarIndeterminateVisibility(true);

	}

	private void doNoContactsOperation() {
		Toast.makeText(getApplicationContext(),
				R.string.str_contactslist_error_no_contact, Toast.LENGTH_SHORT)
				.show();
		finish();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			DbgUtil.showDebug(TAG, "home button pressed");
			finish();
			return true;
		}
		return false;
	}

	@Override
	public void onContactInformationLoaded(
			ArrayList<ContactsListData> contactData) {
		DbgUtil.showDebug(TAG, "onContactInformationLoaded");
		mContactsListData = contactData;

		mAdapter.notifyDataSetChanged();

	}

	@Override
	public void onContactThumbnailLoaded() {
		DbgUtil.showDebug(TAG, "onContactThumbnailLoaded");

	}
}
