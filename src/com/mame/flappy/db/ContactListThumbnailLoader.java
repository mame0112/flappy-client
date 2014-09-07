package com.mame.flappy.db;

import java.io.InputStream;
import java.util.ArrayList;

import com.mame.flappy.constant.LcomConst;
import com.mame.flappy.data.ContactsListData;
import com.mame.flappy.util.DbgUtil;

import android.content.AsyncTaskLoader;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;

public class ContactListThumbnailLoader {

	private final String TAG = LcomConst.TAG + "/ContactListThumbnailLoader";

	private Context mContext = null;

	private ArrayList<String> mIds = null;

	private ContactListThumbnailLoaderListener mListener = null;

	public ContactListThumbnailLoader() {
		DbgUtil.showDebug(TAG, "ContactListThumbnailLoader");
	}

	public void executeThumbnailLoad(Context context, ArrayList<String> ids) {
		mIds = ids;
		mContext = context;
		new LoadContactThumbnailAsyncTask(mContext).execute();
	}

	private class LoadContactThumbnailAsyncTask extends
			AsyncTask<Context, Void, ArrayList<Bitmap>> {

		public LoadContactThumbnailAsyncTask(Context context) {
			DbgUtil.showDebug(TAG, "LoadContactThumbnailAsyncTask");
		}

		@Override
		protected ArrayList<Bitmap> doInBackground(Context... params) {
			DbgUtil.showDebug(TAG, "doInBackground");
			return loadContactThumbnailData();
		}

		@Override
		protected void onPostExecute(ArrayList<Bitmap> result) {
			DbgUtil.showDebug(TAG,
					"LoadContactThumbnailAsyncTask onPostExecute");
			mListener.onThumbnailDataLoaded(result);
		}
	}

	public ArrayList<Bitmap> loadContactThumbnailData() {
		DbgUtil.showDebug(TAG, "loadContactThumbnailData");

		ArrayList<Bitmap> thumbnails = new ArrayList<Bitmap>();

		ContentResolver resolver = mContext.getContentResolver();

		for (String id : mIds) {
			// Get bitmap
			Uri photoUri = ContentUris.withAppendedId(
					ContactsContract.Contacts.CONTENT_URI, Long.valueOf(id));

			InputStream stream = ContactsContract.Contacts
					.openContactPhotoInputStream(resolver, photoUri);

			// If bitmap is not available, we should return null so that
			// activity can show default image
			if (stream != null) {
				Bitmap thumbnail = BitmapFactory.decodeStream(stream);
				if (thumbnail != null) {
					thumbnails.add(thumbnail);
					DbgUtil.showDebug(TAG,
							"thumbnail size: " + thumbnail.getWidth() + " / "
									+ thumbnail.getHeight());
				} else {
					thumbnails.add(null);
				}
			} else {
				thumbnails.add(null);
			}
		}

		return thumbnails;
	}

	public void setContactListThumbnailLoaderListener(
			ContactListThumbnailLoaderListener listener) {
		mListener = listener;
	}

	public interface ContactListThumbnailLoaderListener {
		public void onThumbnailDataLoaded(ArrayList<Bitmap> result);
	}

}
