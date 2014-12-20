package com.mame.flappy.ui.view;

import java.util.List;

import com.mame.flappy.R;
import com.mame.flappy.constant.LcomConst;
import com.mame.flappy.data.FriendListData;
import com.mame.flappy.util.DbgUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendListCustomAdapter extends BaseAdapter {

	private final String TAG = LcomConst.TAG + "/FriendListCustomAdapter";

	private LayoutInflater mLayoutInflater = null;

	private List<FriendListData> mDataList = null;

	public FriendListCustomAdapter(Context context, List<FriendListData> objects) {
		super();
		mDataList = objects;
		mLayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		FriendListData item = (FriendListData) getItem(position);
		ViewHolder holder;

		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.friendlist_item,
					null);
			holder = new ViewHolder();
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.userThumbnail = (ImageView) convertView
				.findViewById(R.id.userThumbnail);
		holder.userNameView = (TextView) convertView
				.findViewById(R.id.friendUserName);
		holder.numOfNewMessageView = (TextView) convertView
				.findViewById(R.id.numOfNewMessage);
		holder.lastMessageView = (TextView) convertView
				.findViewById(R.id.lastMessage);

		Bitmap bitmap = item.getThumbnail();
		if (bitmap != null) {
			// userThumbnail.setim
			holder.userThumbnail.setImageBitmap(bitmap);
		} else {
			// Nothing to do.
		}

		String name = item.getFriendName();
		if (name == null || name.equals(LcomConst.NULL) || name.equals("")) {
			name = item.getMailAddress();
		}
		holder.userNameView.setText(name);
		int numOfMessage = item.getNumOfNewMessage();
		if (numOfMessage <= 0) {
			// Nothing to do
			if (Build.VERSION.SDK_INT >= 16) {
				holder.numOfNewMessageView.setBackground(null);
			} else {
				holder.numOfNewMessageView.setBackgroundDrawable(null);
			}
		} else if (numOfMessage >= 1 && numOfMessage <= 10) {
			// If the number of message is between 1 and 10
			holder.numOfNewMessageView.setText(String.valueOf(numOfMessage));
			holder.numOfNewMessageView
					.setBackgroundResource(R.drawable.flappy_new_message_number_bg);
		} else if (numOfMessage > 10) {
			// If the number of message is more than 10
			holder.numOfNewMessageView
					.setBackgroundResource(R.drawable.flappy_new_message_number_10plus);

		} else {
			if (Build.VERSION.SDK_INT >= 16) {
				holder.numOfNewMessageView.setBackground(null);
			} else {
				holder.numOfNewMessageView.setBackgroundDrawable(null);
			}
		}

		holder.lastMessageView.setText(item.getLastMessage());

		return convertView;

	}

	static class ViewHolder {
		ImageView userThumbnail;
		TextView userNameView;
		TextView numOfNewMessageView;
		TextView lastMessageView;
	}

	@Override
	public int getCount() {
		return mDataList.size();
	}

	@Override
	public Object getItem(int position) {
		return mDataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}
