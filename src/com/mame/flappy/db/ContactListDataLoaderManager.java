package com.mame.flappy.db;

import java.util.ArrayList;

import android.content.Context;

import com.mame.flappy.constant.LcomConst;
import com.mame.flappy.data.ContactsListData;
import com.mame.flappy.util.DbgUtil;

public class ContactListDataLoaderManager implements
		ContactInformationLoader.ContactInformationLoaderListener {

	private final String TAG = LcomConst.TAG + "/ContactListDataLoaderManager";

	private ContactListDataLoaderListener mListener = null;

	private Context mContext = null;

	private ArrayList<ContactsListData> mContactsListData = new ArrayList<ContactsListData>();

	private ContactInformationLoader mContactInfoLoader = null;

	// private ContactListThumbnailLoader mContactThumbnailLoader = null;

	public ContactListDataLoaderManager(Context context) {
		mContext = context;
		mContactInfoLoader = new ContactInformationLoader();
		mContactInfoLoader.setContactInformationLoaderListener(this);
		// mContactThumbnailLoader = new ContactListThumbnailLoader();
		// mContactThumbnailLoader.setContactListThumbnailLoaderListener(this);
	}

	public void executeContactLoad() {
		mContactInfoLoader.executeLoadContactInfo(mContext);
	}

	public void setListener(ContactListDataLoaderListener listener) {
		mListener = listener;
	}

	public interface ContactListDataLoaderListener {
		public void onContactInformationLoaded(
				ArrayList<ContactsListData> contactData);

		// public void onContactThumbnailLoaded(Bitmap thumbnailData);
	}

	@Override
	public void onContactInfoLoaded(ArrayList<ContactsListData> result) {
		DbgUtil.showDebug(TAG, "onContactInfoLoaded");
		if (mListener != null) {
			mListener.onContactInformationLoaded(result);
		}

		// ArrayList<String> ids = new ArrayList<String>();

		// TODO This should be done in async task
		// if (result != null && result.size() != 0) {
		// for (ContactsListData data : result) {
		// ids.add(data.getContactId());
		// }
		// }

		// Execute thumbnail load
		// mContactThumbnailLoader.executeThumbnailLoad(mContext, ids);
	}

	// @Override
	// public void onThumbnailDataLoaded(Bitmap result) {
	// DbgUtil.showDebug(TAG, "onThumbnailDataLoaded");
	// if (mListener != null) {
	// mListener.onContactThumbnailLoaded(result);
	// }
	// }

}
