package com.mame.flappy.ui;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.mame.flappy.R;
import com.mame.flappy.constant.LcomConst;
import com.mame.flappy.data.MessageItemData;
import com.mame.flappy.util.DbgUtil;
import com.mame.flappy.util.PreferenceUtil;
import com.mame.flappy.util.TimeUtil;

public class ConversationListCustonAdapter extends
		ArrayAdapter<MessageItemData> {

	private final String TAG = LcomConst.TAG + "/ConversationListCustonAdapter";

	private LayoutInflater mLayoutInflater = null;

	private Context mContext = null;

	private int mUserId = LcomConst.NO_USER;

	private Bitmap mThumbnail = null;

	public ConversationListCustonAdapter(Context context,
			int textViewResourceId, List<MessageItemData> objects) {
		super(context, textViewResourceId, objects);
		mContext = context;
		mLayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mUserId = PreferenceUtil.getUserId(mContext);
	}

	public void setFriendThumbnail(Bitmap thumbnail) {
		mThumbnail = thumbnail;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MessageItemData item = (MessageItemData) getItem(position);
		ViewHolder holder;

		if (convertView == null) {
			convertView = mLayoutInflater.inflate(
					R.layout.conversationlist_item, null);

			holder = new ViewHolder();

			holder.friendLayout = (FrameLayout) convertView
					.findViewById(R.id.conversationFriendLayout);
			holder.myLayout = (FrameLayout) convertView
					.findViewById(R.id.conversationMyLayout);
			holder.thumbnailView = (ImageView) convertView
					.findViewById(R.id.conversationThumbnail);
			holder.userNameView = (TextView) convertView
					.findViewById(R.id.conversationUserName);
			holder.messageView = (TextView) convertView
					.findViewById(R.id.conversationMessage);
			holder.dateView = (TextView) convertView
					.findViewById(R.id.conversationDate);
			holder.myMessageView = (TextView) convertView
					.findViewById(R.id.conversationMessageReverse);
			holder.myDateView = (TextView) convertView
					.findViewById(R.id.conversationDateReverse);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (item != null) {

			String message = item.getMessage();

			String date = null;
			long originalDate = item.getPostedDate();

			// If sender is myself
			if (mUserId == item.getFromUserId()) {
				holder.friendLayout.setVisibility(View.VISIBLE);
				holder.thumbnailView.setVisibility(View.GONE);
				holder.userNameView.setVisibility(View.GONE);
				holder.messageView.setVisibility(View.GONE);
				holder.dateView.setVisibility(View.GONE);
				holder.myLayout.setVisibility(View.GONE);
				holder.myMessageView.setVisibility(View.VISIBLE);
				holder.myDateView.setVisibility(View.VISIBLE);

				if (message != null && !message.equals("")) {
					holder.myMessageView.setText(message);
				} else {
					holder.myMessageView
							.setText(R.string.str_conversation_no_message);
				}

				String postDate = TimeUtil.getDateForDisplay(originalDate,
						mContext);
				// myDateView.setText(date);
				holder.myDateView.setText(postDate);

			} else {
				// If sender if friend

				String friendName = item.getFromUserName();
				if (friendName == null || friendName.equals("")) {
					friendName = mContext
							.getString(R.string.str_conversation_user_name_not_set);
				}
				holder.userNameView.setText(friendName);

				holder.myLayout.setVisibility(View.VISIBLE);
				holder.friendLayout.setVisibility(View.GONE);
				holder.thumbnailView.setVisibility(View.VISIBLE);
				holder.userNameView.setVisibility(View.VISIBLE);
				holder.messageView.setVisibility(View.VISIBLE);
				holder.dateView.setVisibility(View.VISIBLE);
				holder.myMessageView.setVisibility(View.GONE);
				holder.myDateView.setVisibility(View.GONE);

				if (message != null && !message.equals("")) {
					holder.messageView.setText(message);
				} else {
					holder.messageView
							.setText(R.string.str_conversation_no_message);
				}

				String postDate = TimeUtil.getDateForDisplay(originalDate,
						mContext);
				// dateView.setText(date);
				holder.dateView.setText(postDate);

				if (mThumbnail != null) {
					holder.thumbnailView.setImageBitmap(mThumbnail);
				}

			}

		}
		return convertView;

	}

	static class ViewHolder {
		FrameLayout friendLayout;
		FrameLayout myLayout;
		ImageView thumbnailView;
		TextView userNameView;
		TextView messageView;
		TextView dateView;
		TextView myMessageView;
		TextView myDateView;
	}
}
