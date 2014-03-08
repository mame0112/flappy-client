package com.mame.lcom.db;

import java.io.InputStream;
import java.util.ArrayList;

import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.util.DbgUtil;

import android.content.AsyncTaskLoader;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;

public class ContactListThumbnailLoader extends
		AsyncTaskLoader<ArrayList<Bitmap>> {

	private final String TAG = LcomConst.TAG + "/ContactListThumbnailLoader";

	private ContentResolver mResolver = null;

	private ArrayList<String> mIds = null;

	public ContactListThumbnailLoader(Context context,
			ContentResolver resolver, ArrayList<String> ids) {
		super(context);
		DbgUtil.showDebug(TAG, "ContactListThumbnailLoader");
		mResolver = resolver;
		mIds = ids;

	}

	@Override
	public ArrayList<Bitmap> loadInBackground() {
		DbgUtil.showDebug(TAG, "loadInBackground");

		ArrayList<Bitmap> thumbnails = new ArrayList<Bitmap>();

		for (String id : mIds) {
			// Get bitmap
			Uri photoUri = ContentUris.withAppendedId(
					ContactsContract.Contacts.CONTENT_URI, Long.valueOf(id));

			InputStream stream = ContactsContract.Contacts
					.openContactPhotoInputStream(mResolver, photoUri);

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

}
