package com.mame.lcom.data;

import android.graphics.Bitmap;

public class ContactsListData {

	private String mContactId = null;

	private String mContactName = null;

	private String mAddress = null;

	private Bitmap mThumbnail = null;

	public ContactsListData(String id, String name, String address,
			Bitmap thumbnail) {
		mContactId = id;
		mContactName = name;
		mAddress = address;
		mThumbnail = thumbnail;
	}

	public void setContactId(String id) {
		mContactId = id;
	}

	public void setContactName(String name) {
		mContactName = name;
	}

	public void setMailAddress(String address) {
		mAddress = address;
	}

	public void setThumbnailData(Bitmap thumbnail) {
		mThumbnail = thumbnail;
	}

	public String getContactId() {
		return mContactId;
	}

	public String getContactName() {
		return mContactName;
	}

	public String getMailAddress() {
		return mAddress;
	}

	public Bitmap getThumbnailData() {
		return mThumbnail;
	}

}
