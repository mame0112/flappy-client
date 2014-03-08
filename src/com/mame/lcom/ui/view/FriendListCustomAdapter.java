package com.mame.lcom.ui.view;

import java.util.List;

import com.mame.lcom.R;
import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.data.FriendListData;
import com.mame.lcom.util.DbgUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		FriendListData item = (FriendListData) getItem(position);

		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.friendlist_item,
					null);
		}

		TextView userNameView = (TextView) convertView
				.findViewById(R.id.friendUserName);
		TextView numOfNewMessageView = (TextView) convertView
				.findViewById(R.id.numOfNewMessage);
		TextView lastMessageView = (TextView) convertView
				.findViewById(R.id.lastMessage);

		String name = item.getFriendName();
		if (name == null || name.equals(LcomConst.NULL)) {
			name = item.getMailAddress();
		}
		userNameView.setText(name);
		numOfNewMessageView.setText(String.valueOf(item.getNumOfNewMessage()));
		lastMessageView.setText(item.getLastMessage());

		DbgUtil.showDebug(TAG, "userName;" + item.getFriendName());
		DbgUtil.showDebug(TAG, "numOfNewMessage;" + item.getNumOfNewMessage());
		DbgUtil.showDebug(TAG, "lastMessage;" + item.getLastMessage());
		DbgUtil.showDebug(TAG, "mailAddress;" + item.getMailAddress());

		return convertView;

	}
}
