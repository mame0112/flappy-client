package com.mame.flappy.ui.view;

import com.mame.flappy.constant.LcomConst;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

public class FriendListCustomListView extends ListView {

	private final String TAG = LcomConst.TAG + "/FriendListCustomListView";

	private FriendListCustomAdapter mAdapter = null;

	public FriendListCustomListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
		mAdapter = (FriendListCustomAdapter) adapter;
	}

}
