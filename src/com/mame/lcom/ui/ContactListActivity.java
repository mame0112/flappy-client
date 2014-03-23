package com.mame.lcom.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mame.lcom.R;
import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.data.ContactsListData;
import com.mame.lcom.db.ContactListThumbnailLoader;
import com.mame.lcom.util.DbgUtil;
import com.mame.lcom.util.TrackingUtil;

public class ContactListActivity extends Activity implements
		LoaderCallbacks<ArrayList<Bitmap>> {

	private final String TAG = LcomConst.TAG + "/ContactListActivity";

	private ContentResolver mResolver = null;

	private ContactsListAdapter mAdapter = null;

	private ListView mListView = null;

	private ArrayList<ContactsListData> mContactsListData = new ArrayList<ContactsListData>();

	private final String THUMBNAIL_LOADER = "thumbnail_loader";

	private ContentResolver mRevoler = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contactslist);

		ActionBar actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);

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

		mRevoler = getContentResolver();

		DbgUtil.showDebug(TAG, "onResume");

		loadContactInformation();

	}

	private void loadContactInformation() {
		try {

			String[] PROJECTION = { ContactsContract.Contacts._ID,
					ContactsContract.Contacts.DISPLAY_NAME };

			mResolver = getContentResolver();
			Cursor cursor = mResolver.query(
					ContactsContract.Contacts.CONTENT_URI, PROJECTION, null,
					null, null);

			ArrayList<String> ids = new ArrayList<String>();

			while (cursor.moveToNext()) {

				int columnIndex = cursor
						.getColumnIndex(ContactsContract.Contacts._ID);
				String id = cursor.getString(columnIndex);

				ids.add(id);

				int nameIndex = cursor
						.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);

				ContactsListData data = new ContactsListData(id,
						cursor.getString(nameIndex), null, null);

				// Get mail address
				Cursor cMail = mResolver.query(
						ContactsContract.CommonDataKinds.Email.CONTENT_URI,
						null, ContactsContract.CommonDataKinds.Email.CONTACT_ID
								+ " =? ",
						new String[] { cursor.getString(columnIndex) }, null);
				while (cMail.moveToNext()) {
					// DbgUtil.showDebug(
					// TAG,
					// "Mail address: "
					// + cMail.getString(cMail
					// .getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA1)));
					data.setMailAddress(cMail.getString(cMail
							.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA1)));
					DbgUtil.showDebug(TAG,
							"mail address: " + data.getMailAddress());
				}
				cMail.close();

				// If mail address is null, we avoid to show it on ListView.
				if (data != null && data.getMailAddress() != null) {
					mContactsListData.add(data);
				}

			}

			// Start to load thmbnail
			Bundle bundle = new Bundle();
			bundle.putStringArrayList(THUMBNAIL_LOADER, ids);
			getLoaderManager().initLoader(0, bundle, this).forceLoad();

			cursor.close();

			mAdapter.notifyDataSetChanged();

		} catch (CursorIndexOutOfBoundsException e) {
			DbgUtil.showDebug(TAG, "No contacts: " + e.getMessage());
			TrackingUtil.trackExceptionMessage(getApplicationContext(), TAG,
					"CursorIndexOutOfBoundsException: " + e.getMessage());
			doNoContactsOperation();
		} catch (StringIndexOutOfBoundsException e2) {
			DbgUtil.showDebug(TAG, "No contacts: " + e2.getMessage());
			TrackingUtil.trackExceptionMessage(getApplicationContext(), TAG,
					"StringIndexOutOfBoundsException: " + e2.getMessage());
			doNoContactsOperation();
		}
	}

	private void doNoContactsOperation() {
		Toast.makeText(getApplicationContext(),
				R.string.str_contactslist_error_no_contact, Toast.LENGTH_SHORT)
				.show();
		finish();

	}

	private class ContactsListAdapter extends ArrayAdapter<ContactsListData> {

		private LayoutInflater mLayoutInflater = null;

		public ContactsListAdapter(Context context, int textViewResourceId,
				List<ContactsListData> objects) {
			super(context, textViewResourceId, objects);
			mLayoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ContactsListData item = (ContactsListData) getItem(position);

			if (convertView == null) {
				convertView = mLayoutInflater.inflate(
						R.layout.contactslist_item, null);
			}

			ImageView thumbnailView = (ImageView) convertView
					.findViewById(R.id.contactListThumbnail);
			TextView userNameView = (TextView) convertView
					.findViewById(R.id.contactsListName);
			TextView lastMessageView = (TextView) convertView
					.findViewById(R.id.contactsListAddress);

			Bitmap bitmap = item.getThumbnailData();
			if (bitmap != null) {
				thumbnailView.setImageBitmap(bitmap);
			} else {
				// use default image (Nothing to do)
			}

			userNameView.setText(item.getContactName());
			lastMessageView.setText(item.getMailAddress());

			return convertView;
		}

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
	public Loader<ArrayList<Bitmap>> onCreateLoader(int id, Bundle bundle) {
		DbgUtil.showDebug(TAG, "onCreateLoader");
		if (bundle != null) {
			ArrayList<String> ids = bundle.getStringArrayList(THUMBNAIL_LOADER);
			return new ContactListThumbnailLoader(this, mRevoler, ids);
		}
		return null;
	}

	@Override
	public void onLoadFinished(Loader<ArrayList<Bitmap>> loader,
			ArrayList<Bitmap> thumbnails) {
		DbgUtil.showDebug(TAG, "onLoadFinished");

		if (mContactsListData != null && thumbnails != null) {
			if (mContactsListData.size() == thumbnails.size()) {
				for (int i = 0; i < thumbnails.size(); i++) {
					ContactsListData data = mContactsListData.get(i);
					Bitmap thumbnail = thumbnails.get(i);
					if (thumbnail != null) {
						data.setThumbnailData(thumbnail);
					}
				}

				mAdapter.notifyDataSetChanged();

			} else {
				DbgUtil.showDebug(TAG, "Different the number of item");
			}
		}

	}

	@Override
	public void onLoaderReset(Loader<ArrayList<Bitmap>> loader) {
		DbgUtil.showDebug(TAG, "onLoaderReset");

	}
}
