package com.mame.flappy.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.mame.flappy.constant.LcomConst;
import com.mame.flappy.util.DbgUtil;

public class FriendListCustomListView extends ListView {

	private final String TAG = LcomConst.TAG + "/FriendListCustomListView";

	private FriendListCustomAdapter mAdapter = null;

	private OnScrollListener mScrollListener = null;

	private boolean mIsScrolling = false;

	private FriendListScrollListener mListener = null;

	public FriendListCustomListView(Context context) {
		super(context);
	}

	public FriendListCustomListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public FriendListCustomListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public void listViewUIReady() {
		// Set scroll flag false
		mIsScrolling = false;
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);

		mAdapter = (FriendListCustomAdapter) adapter;
		setOnScrollListener(mScrollListener);

		mScrollListener = new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// DbgUtil.showDebug(TAG, "onScrollStateChanged");

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				DbgUtil.showDebug(TAG, firstVisibleItem + " / "
						+ visibleItemCount + " / " + totalItemCount);

				// To avoid first time automatic load
				if (totalItemCount != 0
						&& totalItemCount > LcomConst.ITEM_ON_SCREEN) {
					// To load when scroll comes to bottom of screen
					if (totalItemCount == firstVisibleItem + visibleItemCount) {
						if (mIsScrolling == false) {
							int pageNum = (int) (totalItemCount / LcomConst.ITEM_ON_SCREEN);
							loadAdditionalData(pageNum);
							mIsScrolling = true;

						}

					}
				}
			}
		};
	}

	@Override
	protected void layoutChildren() {
		try {
			super.layoutChildren();
		} catch (IllegalStateException e) {
			DbgUtil.showDebug(TAG, "IllegalStateException: " + e.getMessage());
		}
	}

	public interface FriendListScrollListener {
		public void onNotifyScrollPositionChnaged(int pageNum);
	}

	public void setListener(FriendListScrollListener listener) {
		mListener = listener;
	}

	private void loadAdditionalData(int pageNum) {
		DbgUtil.showDebug(TAG, "loadAdditionalData");
		if (mListener != null) {
			mListener.onNotifyScrollPositionChnaged(pageNum);
		}
	}
}
