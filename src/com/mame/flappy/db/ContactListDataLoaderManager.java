package com.mame.flappy.db;

import java.util.ArrayList;
import java.util.List;

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

public class ContactListDataLoaderManager implements
		ContactInformationLoader.ContactInformationLoaderListener,
		ContactListThumbnailLoader.ContactListThumbnailLoaderListener {

	private final String TAG = LcomConst.TAG + "/ContactListDataLoaderManager";

	private ContactListDataLoaderListener mListener = null;

	private Context mContext = null;

	private ArrayList<ContactsListData> mContactsListData = new ArrayList<ContactsListData>();

	private ContactInformationLoader mContactInfoLoader = null;

	private ContactListThumbnailLoader mContactThumbnailLoader = null;

	public ContactListDataLoaderManager(Context context) {
		mContext = context;
		mContactInfoLoader = new ContactInformationLoader();
		mContactInfoLoader.setContactInformationLoaderListener(this);
		mContactThumbnailLoader = new ContactListThumbnailLoader();
		mContactThumbnailLoader.setContactListThumbnailLoaderListener(this);
	}

	public void executeContactLoad() {
		// Bundle bundle = new Bundle();
		// bundle.putStringArrayList(THUMBNAIL_LOADER, ids);
		// getLoaderManager().initLoader(0, bundle, this).forceLoad();

		mContactInfoLoader.executeLoadContactInfo(mContext);
	}

	public void setListener(ContactListDataLoaderListener listener) {
		mListener = listener;
	}

	public interface ContactListDataLoaderListener {
		public void onContactInformationLoaded(
				ArrayList<ContactsListData> contactData);

		public void onContactThumbnailLoaded(ArrayList<Bitmap> thumbnailData);
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

	// private ArrayList<ContactsListData> loadContactInformation() {
	// mDataCallback = new LoaderCallbacks<ArrayList<ContactsListData>>() {
	//
	// @Override
	// public Loader<ArrayList<ContactsListData>> onCreateLoader(int id,
	// Bundle args) {
	// ContactInformationLoader loader = new ContactInformationLoader(
	// mContext);
	// loader.forceLoad();
	// // return loader;
	// }
	//
	// @Override
	// public void onLoadFinished(
	// Loader<ArrayList<ContactsListData>> loader,
	// ArrayList<ContactsListData> data) {
	// mContactsListData = data;
	// }
	//
	// @Override
	// public void onLoaderReset(Loader<ArrayList<ContactsListData>> loader) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// };
	// return mContactsListData;
	//
	// }

	@Override
	public void onContactInfoLoaded(ArrayList<ContactsListData> result) {
		DbgUtil.showDebug(TAG, "onContactInfoLoaded");
		if (mListener != null) {
			mListener.onContactInformationLoaded(result);
		}

		ArrayList<String> ids = new ArrayList<String>();

		// TODO This should be done in async task
		if (result != null && result.size() != 0) {
			for (ContactsListData data : result) {
				ids.add(data.getContactId());
			}
		}

		// Execute thumbnail load
		mContactThumbnailLoader.executeThumbnailLoad(mContext, ids);
	}

	@Override
	public void onThumbnailDataLoaded(ArrayList<Bitmap> result) {
		DbgUtil.showDebug(TAG, "onThumbnailDataLoaded");
		if (mListener != null) {
			mListener.onContactThumbnailLoaded(result);
		}
	}

}
