package com.mame.flappy.ui;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mame.flappy.R;
import com.mame.flappy.constant.LcomConst;
import com.mame.flappy.data.ContactsListData;
import com.mame.flappy.db.ContactListThumbnailLoader;
import com.mame.flappy.util.DbgUtil;

public class ContactsListAdapter extends ArrayAdapter<ContactsListData> {

	private final String TAG = LcomConst.TAG + "/ContactsListAdapter";

	private LayoutInflater mLayoutInflater = null;

	// private ContactListThumbnailLoader mContactThumbnailLoader = null;

	public ContactsListAdapter(Context context, int textViewResourceId,
			List<ContactsListData> objects) {
		super(context, textViewResourceId, objects);
		mLayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// ContactListThumbnailLoader contactThumbnailLoader = new
		// ContactListThumbnailLoader();
		// mContactThumbnailLoader.setContactListThumbnailLoaderListener(this);

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ContactsListData item = (ContactsListData) getItem(position);
		ViewHolder holder;

		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.contactslist_item,
					null);
			holder = new ViewHolder();
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.thumbnailView = (ImageView) convertView
				.findViewById(R.id.contactListThumbnail);
		holder.userNameView = (TextView) convertView
				.findViewById(R.id.contactsListName);
		holder.lastMessageView = (TextView) convertView
				.findViewById(R.id.contactsListAddress);

		String id = item.getContactId();
		holder.thumbnailView.setTag(id);
		holder.thumbnailView
				.setBackgroundResource(R.drawable.flappy_default_thumbnail_large);

		//Set as default image
		Bitmap bitmap = item.getThumbnailData();
		if (bitmap != null) {
			holder.thumbnailView.setImageBitmap(bitmap);
		}

		// Load image from Contact
		new ContactListThumbnailLoader().executeThumbnailLoad(getContext(),
				item.getContactId(), holder.thumbnailView);

		holder.userNameView.setText(item.getContactName());
		holder.lastMessageView.setText(item.getMailAddress());

		return convertView;
	}

	static class ViewHolder {
		ImageView thumbnailView;
		TextView userNameView;
		TextView lastMessageView;
		Bitmap thumbnail;
	}
}
