package com.mame.flappy.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.mame.flappy.constant.LcomConst;
import com.mame.flappy.util.DbgUtil;

public class ConversationCustomListView extends ListView {

	private final String TAG = LcomConst.TAG + "/ConversationCustomListView";

	private OnScrollListener mScrollListener = null;

	private boolean mIsScrolling = false;

	private ConversationListScrollListener mListener = null;

	public ConversationCustomListView(Context context) {
		super(context);
	}

	public ConversationCustomListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public ConversationCustomListView(Context context, AttributeSet attrs) {
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
				if (totalItemCount != 0 && visibleItemCount != 0) {
					// To load when scroll comes to bottom of screen
					// (And "visibleItemCount != 0" is just for avoiding reload
					// triggered in launch app case)
					if (0 == firstVisibleItem
							&& totalItemCount >= LcomConst.ITEM_ON_SCREEN) {
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

	private void loadAdditionalData(int pageNum) {
		DbgUtil.showDebug(TAG, "loadAdditionalData");
		if (mListener != null) {
			mListener.onNotifyScrollPositionChanged(pageNum);
		}
	}

	public interface ConversationListScrollListener {
		public void onNotifyScrollPositionChanged(int pageNum);
	}

	public void setListener(ConversationListScrollListener listener) {
		mListener = listener;
	}

}
