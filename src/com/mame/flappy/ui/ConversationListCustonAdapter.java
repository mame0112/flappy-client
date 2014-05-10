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

		if (convertView == null) {
			convertView = mLayoutInflater.inflate(
					R.layout.conversationlist_item, null);
		}

		if (item != null) {
			FrameLayout friendLayout = (FrameLayout) convertView
					.findViewById(R.id.conversationFriendLayout);
			FrameLayout myLayout = (FrameLayout) convertView
					.findViewById(R.id.conversationMyLayout);
			ImageView thumbnailView = (ImageView) convertView
					.findViewById(R.id.conversationThumbnail);
			TextView userNameView = (TextView) convertView
					.findViewById(R.id.conversationUserName);
			TextView messageView = (TextView) convertView
					.findViewById(R.id.conversationMessage);
			TextView dateView = (TextView) convertView
					.findViewById(R.id.conversationDate);
			TextView myMessageView = (TextView) convertView
					.findViewById(R.id.conversationMessageReverse);
			TextView myDateView = (TextView) convertView
					.findViewById(R.id.conversationDateReverse);

			String message = item.getMessage();

			String date = null;
			long originalDate = item.getPostedDate();

			// If sender is myself
			if (mUserId == item.getFromUserId()) {
				friendLayout.setVisibility(View.VISIBLE);
				thumbnailView.setVisibility(View.GONE);
				userNameView.setVisibility(View.GONE);
				messageView.setVisibility(View.GONE);
				dateView.setVisibility(View.GONE);
				myLayout.setVisibility(View.GONE);
				myMessageView.setVisibility(View.VISIBLE);
				myDateView.setVisibility(View.VISIBLE);

				if (message != null && !message.equals("")) {
					myMessageView.setText(message);
				} else {
					myMessageView.setText(R.string.str_conversation_no_message);
				}

				String postDate = TimeUtil.getDateForDisplay(originalDate,
						mContext);
				// myDateView.setText(date);
				myDateView.setText(postDate);

			} else {
				// If sender if friend

				userNameView.setText(item.getFromUserName());

				myLayout.setVisibility(View.VISIBLE);
				friendLayout.setVisibility(View.GONE);
				thumbnailView.setVisibility(View.VISIBLE);
				userNameView.setVisibility(View.VISIBLE);
				messageView.setVisibility(View.VISIBLE);
				dateView.setVisibility(View.VISIBLE);
				myMessageView.setVisibility(View.GONE);
				myDateView.setVisibility(View.GONE);

				if (message != null && !message.equals("")) {
					messageView.setText(message);
				} else {
					messageView.setText(R.string.str_conversation_no_message);
				}

				String postDate = TimeUtil.getDateForDisplay(originalDate,
						mContext);
				// dateView.setText(date);
				dateView.setText(postDate);

				if (mThumbnail != null) {
					thumbnailView.setImageBitmap(mThumbnail);
				}

			}

		}
		return convertView;

	}
}
