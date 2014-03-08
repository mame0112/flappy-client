package com.mame.lcom.ui;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mame.lcom.R;
import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.data.MessageItemData;
import com.mame.lcom.util.DbgUtil;
import com.mame.lcom.util.PreferenceUtil;
import com.mame.lcom.util.TimeUtil;

public class ConversationListCustonAdapter extends
		ArrayAdapter<MessageItemData> {

	private final String TAG = LcomConst.TAG + "/ConversationListCustonAdapter";

	private LayoutInflater mLayoutInflater = null;

	private Context mContext = null;

	private int mUserId = LcomConst.NO_USER;

	public ConversationListCustonAdapter(Context context,
			int textViewResourceId, List<MessageItemData> objects) {
		super(context, textViewResourceId, objects);
		mContext = context;
		mLayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mUserId = PreferenceUtil.getUserId(mContext);
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
			if (originalDate != 0L) {
				date = String.valueOf(originalDate);
				// try {
				// date = TimeUtil.parseDateInDateToString(originalDate);
				// } catch (ParseException e) {
				// DbgUtil.showDebug(TAG, "ParseException: " + e.getMessage());
				// }
			} else {
				date = mContext
						.getString(R.string.str_conversation_time_unknown);
			}

			// If sender is myself
			if (mUserId == item.getFromUserId()) {
				thumbnailView.setVisibility(View.GONE);
				userNameView.setVisibility(View.GONE);
				messageView.setVisibility(View.GONE);
				dateView.setVisibility(View.GONE);
				myMessageView.setVisibility(View.VISIBLE);
				myDateView.setVisibility(View.VISIBLE);

				if (message != null && !message.equals("")) {
					myMessageView.setText(message);
				} else {
					myMessageView.setText(R.string.str_conversation_no_message);
				}

				myDateView.setText(date);

			} else {

				userNameView.setText(item.getFromUserName());

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

				dateView.setText(date);

			}

		}
		return convertView;

	}
}
