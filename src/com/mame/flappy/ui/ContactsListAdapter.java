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
import com.mame.flappy.data.ContactsListData;

public class ContactsListAdapter extends ArrayAdapter<ContactsListData> {
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

		Bitmap bitmap = item.getThumbnailData();
		if (bitmap != null) {
			holder.thumbnailView.setImageBitmap(bitmap);
		} else {
			// use default image (Nothing to do)
		}

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
