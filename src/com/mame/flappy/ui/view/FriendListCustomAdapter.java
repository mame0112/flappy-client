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
import android.widget.ImageView;
import android.widget.TextView;

public class FriendListCustomAdapter extends ArrayAdapter<FriendListData> {

	private final String TAG = LcomConst.TAG + "/FriendListCustomAdapter";

	private LayoutInflater mLayoutInflater = null;

	public FriendListCustomAdapter(Context context, int textViewResourceId,
			List<FriendListData> objects) {
		super(context, textViewResourceId, objects);
		mLayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		FriendListData item = (FriendListData) getItem(position);

		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.friendlist_item,
					null);
		}

		ImageView userThumbnail = (ImageView) convertView
				.findViewById(R.id.userThumbnail);
		TextView userNameView = (TextView) convertView
				.findViewById(R.id.friendUserName);
		TextView numOfNewMessageView = (TextView) convertView
				.findViewById(R.id.numOfNewMessage);
		TextView lastMessageView = (TextView) convertView
				.findViewById(R.id.lastMessage);

		Bitmap bitmap = item.getThumbnail();
		if (bitmap != null) {
			// userThumbnail.setim
			userThumbnail.setImageBitmap(bitmap);
		} else {
			// Nothing to do.
		}

		String name = item.getFriendName();
		if (name == null || name.equals(LcomConst.NULL)) {
			name = item.getMailAddress();
		}
		userNameView.setText(name);
		int numOfMessage = item.getNumOfNewMessage();
		if (numOfMessage <= 0) {
			// Nothing to do
			if (Build.VERSION.SDK_INT >= 16) {
				numOfNewMessageView.setBackground(null);
			} else {
				numOfNewMessageView.setBackgroundDrawable(null);
			}
		} else if (numOfMessage >= 1 && numOfMessage <= 10) {
			// If the number of message is between 1 and 10
			numOfNewMessageView.setText(String.valueOf(numOfMessage));
			numOfNewMessageView
					.setBackgroundResource(R.drawable.flappy_new_message_number_bg);
		} else if (numOfMessage > 10) {
			// If the number of message is more than 10
			numOfNewMessageView
					.setBackgroundResource(R.drawable.flappy_new_message_number_10plus);

		} else {
			if (Build.VERSION.SDK_INT >= 16) {
				numOfNewMessageView.setBackground(null);
			} else {
				numOfNewMessageView.setBackgroundDrawable(null);
			}
		}

		lastMessageView.setText(item.getLastMessage());

		DbgUtil.showDebug(TAG, "userName;" + item.getFriendName());
		DbgUtil.showDebug(TAG, "numOfNewMessage;" + item.getNumOfNewMessage());
		DbgUtil.showDebug(TAG, "lastMessage;" + item.getLastMessage());
		DbgUtil.showDebug(TAG, "mailAddress;" + item.getMailAddress());

		return convertView;

	}
}
