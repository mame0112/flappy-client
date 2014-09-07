package com.mame.flappy.db;

import java.util.ArrayList;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;

import com.mame.flappy.constant.LcomConst;
import com.mame.flappy.data.ContactsListData;
import com.mame.flappy.datamanager.FriendDataManager.FriendDataManagerListener;
import com.mame.flappy.exception.FriendDataManagerException;
import com.mame.flappy.exception.UserLocalDataHandlerException;
import com.mame.flappy.util.DbgUtil;
import com.mame.flappy.util.TrackingUtil;

public class ContactListDataLoaderManager {

	private final String TAG = LcomConst.TAG + "/ContactListDataLoader";

	private ContactListDataLoaderListener mListener = null;

	private Context mContext = null;

	private ArrayList<ContactsListData> mContactsListData = new ArrayList<ContactsListData>();

	private final String THUMBNAIL_LOADER = "thumbnail_loader";

	private final int LOADER_CONTACT_DATA = 0;

	private final int LOADER_CONTACT_THUMBNAIL = 1;

	private LoaderCallbacks<ArrayList<ContactsListData>> mDataCallback = null;

	private LoaderCallbacks<ArrayList<Bitmap>> mThumbnailCallback = null;

	public ContactListDataLoaderManager(Context context) {
		mContext = context;

		// mContactsListData = new
		// LoadContactListInformationAsyncTask().execute();
	}

	public void executeContactLoad() {
		// Bundle bundle = new Bundle();
		// bundle.putStringArrayList(THUMBNAIL_LOADER, ids);
		// getLoaderManager().initLoader(0, bundle, this).forceLoad();

		loadContactInformation();
	}

	public void loadContactThumbnailInformation() {
		if (mListener == null) {
			return;
		}

	}

	public void setListener(ContactListDataLoaderListener listener) {
		mListener = listener;
	}

	public interface ContactListDataLoaderListener {
		public void onContactInformationLoaded(
				ArrayList<ContactsListData> contactData);

		public void onContactThumbnailLoaded();
	}

	// private class LoadContactListInformationAsyncTask extends
	// AsyncTask<Void, Void, ArrayList<ContactsListData>> {
	//
	// public LoadContactListInformationAsyncTask() {
	// DbgUtil.showDebug(TAG, "LoadContactListInformationAsyncTask");
	// }
	//
	// @Override
	// protected ArrayList<ContactsListData> doInBackground(Void... params) {
	// DbgUtil.showDebug(TAG, "doInBackground");
	// return loadContactInformation();
	// }
	//
	// @Override
	// protected void onPostExecute(ArrayList<ContactsListData> result) {
	// DbgUtil.showDebug(TAG,
	// "LoadContactListInformationAsyncTask onPostExecute");
	// if (result != null && result.size() != 0) {
	// DbgUtil.showDebug(TAG, "size: " + result.size());
	// mListener.onContactInformationLoaded(result);
	// }
	// }
	// }

	// @Override
	// public Loader<ArrayList<Bitmap>> onCreateLoader(int id, Bundle bundle) {
	// DbgUtil.showDebug(TAG, "onCreateLoader");
	// switch (id) {
	// case LOADER_CONTACT_DATA:
	// DbgUtil.showDebug(TAG, "Contact data");
	// if (bundle != null) {
	// ArrayList<String> ids = bundle
	// .getStringArrayList(THUMBNAIL_LOADER);
	// return new ContactListThumbnailLoader(this, mRevoler, ids);
	// }
	// break;
	// case LOADER_CONTACT_THUMBNAIL:
	// DbgUtil.showDebug(TAG, "Contact thumbnail");
	// break;
	// default:
	// DbgUtil.showDebug(TAG, "Some other stuff");
	// break;
	// }
	// return null;
	// }
	//
	// @Override
	// public void onLoadFinished(Loader<ArrayList<Bitmap>> loader,
	// ArrayList<Bitmap> thumbnails) {
	// DbgUtil.showDebug(TAG, "onLoadFinished");
	//
	// if (mContactsListData != null && thumbnails != null) {
	// if (mContactsListData.size() == thumbnails.size()) {
	// for (int i = 0; i < thumbnails.size(); i++) {
	// ContactsListData data = mContactsListData.get(i);
	// Bitmap thumbnail = thumbnails.get(i);
	// if (thumbnail != null) {
	// data.setThumbnailData(thumbnail);
	// }
	// }
	//
	// mAdapter.notifyDataSetChanged();
	//
	// } else {
	// DbgUtil.showDebug(TAG, "Different the number of item");
	// }
	// }
	// setProgressBarIndeterminateVisibility(false);
	//
	// }
	//
	// @Override
	// public void onLoaderReset(Loader<ArrayList<Bitmap>> loader) {
	// DbgUtil.showDebug(TAG, "onLoaderReset");
	// setProgressBarIndeterminateVisibility(false);
	// }

	private ArrayList<ContactsListData> loadContactInformation() {
		mDataCallback = new LoaderCallbacks<ArrayList<ContactsListData>>() {

			@Override
			public Loader<ArrayList<ContactsListData>> onCreateLoader(int id,
					Bundle args) {
				// TODO Auto-generated method stub
				return null;
//				return new ContactInformationLoader().loadContactInformation();
			}

			@Override
			public void onLoadFinished(
					Loader<ArrayList<ContactsListData>> loader,
					ArrayList<ContactsListData> data) {
				mContactsListData = data;
			}

			@Override
			public void onLoaderReset(Loader<ArrayList<ContactsListData>> loader) {
				// TODO Auto-generated method stub

			}

		};

		ArrayList<ContactsListData> result = new ArrayList<ContactsListData>();
		try {

			if (mListener == null) {
				return null;
			}

			String[] PROJECTION = { ContactsContract.Contacts._ID,
					ContactsContract.Contacts.DISPLAY_NAME };

			Cursor cursor = mContext.getContentResolver().query(
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
				Cursor cMail = mContext.getContentResolver().query(
						ContactsContract.CommonDataKinds.Email.CONTENT_URI,
						null,
						ContactsContract.CommonDataKinds.Email.CONTACT_ID
								+ " =? ",
						new String[] { cursor.getString(columnIndex) }, null);
				while (cMail.moveToNext()) {
					data.setMailAddress(cMail.getString(cMail
							.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA1)));
					DbgUtil.showDebug(TAG,
							"mail address: " + data.getMailAddress());
				}
				cMail.close();

				// If mail address is null, we avoid to show it on ListView.
				if (data != null && data.getMailAddress() != null) {
					result.add(data);
				}

			}

			// Start to load thmbnail
			Bundle bundle = new Bundle();
			bundle.putStringArrayList(THUMBNAIL_LOADER, ids);
			// getLoaderManager().initLoader(LOADER_CONTACT_THUMBNAIL, bundle,
			// this).forceLoad();

			cursor.close();

		} catch (CursorIndexOutOfBoundsException e) {
			DbgUtil.showDebug(TAG, "No contacts: " + e.getMessage());
			TrackingUtil.trackExceptionMessage(mContext, TAG,
					"CursorIndexOutOfBoundsException: " + e.getMessage());
			// doNoContactsOperation();
		} catch (StringIndexOutOfBoundsException e2) {
			DbgUtil.showDebug(TAG, "No contacts: " + e2.getMessage());
			TrackingUtil.trackExceptionMessage(mContext, TAG,
					"StringIndexOutOfBoundsException: " + e2.getMessage());
			// doNoContactsOperation();
		}

		return result;
	}

	private ArrayList<ContactsListData> executeContactDataLoad() {
		return null;
	}

}
