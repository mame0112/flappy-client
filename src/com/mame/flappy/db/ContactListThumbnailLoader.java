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
import android.widget.ImageView;

public class ContactListThumbnailLoader {

	private final String TAG = LcomConst.TAG + "/ContactListThumbnailLoader";

	private Context mContext = null;

	private String mContactId = null;

	private ImageView mThumbnailView = null;

	private String mTag = null;

	public ContactListThumbnailLoader() {
		DbgUtil.showDebug(TAG, "ContactListThumbnailLoader");
	}

	public void executeThumbnailLoad(Context context, String id, ImageView view) {
		mContext = context;
		mContactId = id;
		mThumbnailView = view;
		mTag = view.getTag().toString();

		new LoadContactThumbnailAsyncTask(mContext).execute();
	}

	private class LoadContactThumbnailAsyncTask extends
			AsyncTask<Context, Void, Bitmap> {

		public LoadContactThumbnailAsyncTask(Context context) {
			DbgUtil.showDebug(TAG, "LoadContactThumbnailAsyncTask");
		}

		@Override
		protected Bitmap doInBackground(Context... params) {
			DbgUtil.showDebug(TAG, "doInBackground");
			return loadContactThumbnailData();
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			DbgUtil.showDebug(TAG,
					"LoadContactThumbnailAsyncTask onPostExecute");
			if (mTag.equals(mThumbnailView.getTag())) {
				mThumbnailView.setImageBitmap(result);
			}
		}
	}

	public synchronized Bitmap loadContactThumbnailData() {
		DbgUtil.showDebug(TAG, "loadContactThumbnailData");

		ContentResolver resolver = mContext.getContentResolver();

		// Get bitmap
		Uri photoUri = ContentUris
				.withAppendedId(ContactsContract.Contacts.CONTENT_URI,
						Long.valueOf(mContactId));

		InputStream stream = ContactsContract.Contacts
				.openContactPhotoInputStream(resolver, photoUri);

		// If bitmap is not available, we should return null so that
		// activity can show default image
		if (stream != null) {
			Bitmap thumbnail = BitmapFactory.decodeStream(stream);
			return thumbnail;
		}

		return null;
	}
}
