package com.mame.flappy.ui.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.mame.flappy.R;
import com.mame.flappy.constant.LcomConst;
import com.mame.flappy.data.FriendListData;
import com.mame.flappy.data.MessageItemData;
import com.mame.flappy.data.NotificationContentData;
import com.mame.flappy.datamanager.FriendDataManager.FriendDataManagerListener;
import com.mame.flappy.util.DbgUtil;
import com.mame.flappy.web.LcomServerAccessor;

public class SignoutConfirmationDialog extends DialogFragment implements
		LcomServerAccessor.LcomServerAccessorListener,
		FriendDataManagerListener {
	private final String TAG = LcomConst.TAG + "/SignoutConfirmationDialog";

	private SignoutConfirmationListener mListener = null;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.str_signout_confirm_title)
				.setMessage(R.string.str_signout_confirm_desc)
				.setPositiveButton(R.string.str_signout_dialog_positive,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								if (mListener != null) {
									mListener
											.onSignoutConfirmationSelected(true);
								}
							}
						})
				.setNegativeButton(R.string.str_signout_dialog_negative,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								if (mListener != null) {
									mListener
											.onSignoutConfirmationSelected(false);
								}
							}
						});
		// Create the AlertDialog object and return it
		return builder.create();
	}

	@Override
	public void notifyPresentDataset(ArrayList<FriendListData> userData) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyNewDataset(ArrayList<FriendListData> newUserData) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyAddPresentDataFinished(boolean result,
			MessageItemData messageData) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyPresentMessageDataLoaded(
			ArrayList<MessageItemData> messageData) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyNewConversationDataLoaded(
			ArrayList<MessageItemData> messageData) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyFriendThubmailsLoaded(
			List<HashMap<Integer, Bitmap>> thumbnailsthumbnails) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyLatestStoredMessage(FriendListData message) {
		// TODO Auto-generated method stub

	}

	// @Override
	// public void notifiyNearlestExpireNotification(NotificationContentData
	// data) {
	// // TODO Auto-generated method stub
	//
	// }

	@Override
	public void onResponseReceived(List<String> respList) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAPITimeout() {
		// TODO Auto-generated method stub

	}

	public void setSignoutConfirmationListener(
			SignoutConfirmationListener listener) {
		mListener = listener;
	}

	public interface SignoutConfirmationListener {
		public void onSignoutConfirmationSelected(boolean isAccepted);
	}

	@Override
	public void notifyValidNotificationList(
			ArrayList<NotificationContentData> notifications) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyAdditionalLocalUserDataLoaded(
			ArrayList<FriendListData> userData) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyAdditionalLocalConversationDataLoaded(
			ArrayList<MessageItemData> userData) {
		// TODO Auto-generated method stub

	}
}
