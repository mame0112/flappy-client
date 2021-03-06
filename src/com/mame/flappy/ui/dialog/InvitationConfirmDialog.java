package com.mame.flappy.ui.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mame.flappy.R;
import com.mame.flappy.constant.LcomConst;
import com.mame.flappy.data.FriendListData;
import com.mame.flappy.data.FriendListUpdateData;
import com.mame.flappy.data.MessageItemData;
import com.mame.flappy.data.NotificationContentData;
import com.mame.flappy.datamanager.FriendDataManager;
import com.mame.flappy.datamanager.FriendDataManager.FriendDataManagerListener;
import com.mame.flappy.exception.WebAPIException;
import com.mame.flappy.ui.StartNewConversationActivityUtil;
import com.mame.flappy.util.ButtonUtil;
import com.mame.flappy.util.DbgUtil;
import com.mame.flappy.util.FeedbackUtil;
import com.mame.flappy.util.LocaleUtil;
import com.mame.flappy.util.NetworkUtil;
import com.mame.flappy.util.StringUtil;
import com.mame.flappy.util.TrackingUtil;
import com.mame.flappy.web.LcomHttpWebAPI;
import com.mame.flappy.web.LcomServerAccessor;
import com.mame.flappy.web.LcomHttpWebAPI.LcomWebAPIListener;

public class InvitationConfirmDialog extends DialogFragment implements
		LcomServerAccessor.LcomServerAccessorListener,
		FriendDataManagerListener {

	private final String TAG = LcomConst.TAG + "/InvitationConfirmDialog";

	private LcomServerAccessor mWebAPI = null;

	private EditText mMessageEditText = null;

	private Button mPositiveButton = null;

	private Button mNegativeButton = null;

	private TextView mSendProgressText = null;

	private FriendDataManager mManager = null;

	private InvitationConfirmationListener mListener = null;

	private Handler mHandler = new Handler();

	public InvitationConfirmDialog() {
		mWebAPI = new LcomServerAccessor();
		mWebAPI.setListener(this);

	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final String userId = getArguments().getString(LcomConst.EXTRA_USER_ID);
		final String userName = getArguments().getString(
				LcomConst.EXTRA_USER_NAME);
		final String targetMailAddress = getArguments().getString(
				LcomConst.EXTRA_TARGET_MAIL_ADDRESS);
		// final String targetMessage = getArguments().getString(
		// LcomConst.EXTRA_TARGET_MESSAGE);
		final String targetUserId = getArguments().getString(
				LcomConst.EXTRA_TARGET_USER_ID);
		final String targetUserName = getArguments().getString(
				LcomConst.EXTRA_TARGET_USER_NAME);
		DbgUtil.showDebug(TAG, "userId: " + userId + " / userName: " + userName
				+ "/ targetMailAddress: " + targetMailAddress
				+ "/ targetUserId: " + targetUserId + " /targetUserName "
				+ targetUserName);

		mManager = FriendDataManager.getInstance();

		mManager.initializeFriendDataManager(Integer.valueOf(userId),
				getActivity());
		mManager.setFriendDataManagerListener(this);

		LayoutInflater factory = LayoutInflater.from(getActivity()
				.getApplicationContext());
		final View dialogView = factory.inflate(
				R.layout.invitation_confirm_dialog, null);

		mSendProgressText = (TextView) dialogView
				.findViewById(R.id.sendMessageProgressText);
		mSendProgressText.setVisibility(View.GONE);

		mMessageEditText = (EditText) dialogView
				.findViewById(R.id.inviMsgDlgMessageEditText);
		mMessageEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void onTextChanged(CharSequence s, int arg1, int arg2,
					int arg3) {
				if (s.length() >= 1) {
					mPositiveButton.setEnabled(true);
				} else {
					mPositiveButton.setEnabled(false);
				}

			}

		});

		final Activity activity = getActivity();

		mPositiveButton = (Button) dialogView
				.findViewById(R.id.inviMsgDlgMessagePositive);
		mPositiveButton.setEnabled(false);
		mPositiveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				DbgUtil.showDebug(TAG, "onClick");
				if (ButtonUtil.isClickable()) {
					try {
						TrackingUtil
								.trackEvent(
										activity,
										TrackingUtil.EVENT_CATEGORY_MESSAGE_INPUT_DIALOG,
										TrackingUtil.EVENT_ACTION_INPUT_MESSAGE,
										TrackingUtil.EVENT_LABEL_SEND_NEW_MESSAGE_BUTTON,
										1);

						if (NetworkUtil.isNetworkAvailable(activity, mHandler)) {
							final String targetMessage = mMessageEditText
									.getText().toString();
							DbgUtil.showDebug(TAG, "targetMessage: "
									+ targetMessage);
							if (!StringUtil
									.isContainPreservedCharacters(targetMessage)) {
								mSendProgressText.setVisibility(View.VISIBLE);
								mPositiveButton.setEnabled(false);
								mNegativeButton.setEnabled(false);

								sendConfirmedMessage(userId, userName,
										targetUserId, targetUserName,
										targetMailAddress, targetMessage);
							} else {
								Toast.makeText(
										getActivity(),
										R.string.str_generic_preserved_char_error,
										Toast.LENGTH_SHORT).show();
							}
						}
					} catch (WebAPIException e) {
						DbgUtil.showDebug(TAG,
								"WebAPIException: " + e.getMessage());
					}
				}
			}
		});

		mNegativeButton = (Button) dialogView
				.findViewById(R.id.inviMsgDlgMessageNegative);
		mNegativeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				DbgUtil.showDebug(TAG, "negative button");
				TrackingUtil.trackEvent(activity,
						TrackingUtil.EVENT_CATEGORY_MESSAGE_INPUT_DIALOG,
						TrackingUtil.EVENT_ACTION_INPUT_MESSAGE,
						TrackingUtil.EVENT_LABEL_CANCEL_NEW_MESSAGE_BUTTON, 1);
				dismiss();
			}
		});

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(dialogView);
		return builder.create();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		if (mSendProgressText != null) {
			mSendProgressText.setVisibility(View.GONE);
		}

		if (mManager != null) {
			mManager.removeFriendDataManagerListener(this);
			// mManager.destroyFriendDataManager();
		}

		if (mWebAPI != null) {
			mWebAPI.removeListener();
		}
	}

	@Override
	public void onResponseReceived(List<String> respList) {
		DbgUtil.showDebug(TAG, "onResponseReceived");
		if (respList != null && respList.size() != 0) {
			try {
				String origin = respList.get(0);
				String userId = respList.get(1);
				String userName = respList.get(2);
				String targetUserId = respList.get(3);
				String targetUserName = respList.get(4);
				String message = respList.get(5);
				String date = respList.get(6);
				String result = respList.get(7);
				String mailAddress = respList.get(8);
				DbgUtil.showDebug(TAG, "mailAddress: " + mailAddress);

				if (origin != null) {
					if (origin.equals(TAG)) {
						if (result != null) {
							// TODO need to add error handling here.
							switch (Integer.valueOf(result)) {
							case LcomConst.INVITATION_CONFIRMED_RESULT_OK:
								DbgUtil.showDebug(TAG,
										"INVITATION_CONFIRMED_RESULT_OK");
								showFeedbackToast(R.string.str_contactslist_confirm_successfuly_sent);
								// TODO need to have senderId from server side.
								DbgUtil.showDebug(TAG, "userId: " + userId);
								DbgUtil.showDebug(TAG, "userName: " + userName);
								DbgUtil.showDebug(TAG, "targetUserId: "
										+ targetUserId);
								DbgUtil.showDebug(TAG, "targetUserName: "
										+ targetUserName);
								DbgUtil.showDebug(TAG, "message: " + message);
								DbgUtil.showDebug(TAG, "Date: " + date);
								DbgUtil.showDebug(TAG, "mailAddress: "
										+ mailAddress);

								boolean isSuccess = mManager
										.setFriendListPresentDataset(
												Integer.valueOf(userId),
												Integer.valueOf(targetUserId),
												userName, targetUserName,
												Integer.valueOf(userId),
												message, date, null,
												mailAddress);
								mListener.onNotifyNewFriendAdded();
								if (!isSuccess) {
									DbgUtil.showDebug(TAG,
											"Failed t store data to DB.");
									showFeedbackToast(R.string.str_contactslist_confirm_failed_to_store_data);
								}
								// getActivity().finish();
								break;
							case LcomConst.INVITATION_CONFIRMED_UNKNOWN_ERROR:
								DbgUtil.showDebug(TAG,
										"INVITATION_CONFIRMED_UNKNOWN_ERROR");
								showFeedbackToast(R.string.str_contactslist_confirm_unknown_error);
								break;
							case LcomConst.INVITATION_CONFIRMED_MAIL_CANNOT_BE_SENT:
								DbgUtil.showDebug(TAG,
										"INVITATION_CONFIRMED_MAIL_CANNOT_BE_SENT");
								showFeedbackToast(R.string.str_contactslist_confirm_maiL_cannot_be_sent);
								break;
							}
							// if (isAdded()) {
							// dismissAllowingStateLoss();
							// }
						}
					}
				}
			} catch (IndexOutOfBoundsException e) {
				// We would come here if returned List doesn't have expected
				// length.
				DbgUtil.showDebug(TAG,
						"IndexOutOfBoundException: " + e.getMessage());
				TrackingUtil.trackExceptionMessage(getActivity(), TAG,
						"IndexOutOfBoundsException: " + e.getMessage());
				FeedbackUtil.showFeedbackToast(getActivity(), mHandler,
						R.string.str_generic_unknown_error);
				dismiss();
			} catch (NumberFormatException e) {
				DbgUtil.showDebug(TAG,
						"NumberFormatException: " + e.getMessage());
				TrackingUtil.trackExceptionMessage(getActivity(), TAG,
						"NumberFormatException: " + e.getMessage());
				FeedbackUtil.showFeedbackToast(getActivity(), mHandler,
						R.string.str_generic_unknown_error);
				dismiss();
			}
		} else {
			DbgUtil.showDebug(TAG, "response is null or size 0");
			TrackingUtil.trackExceptionMessage(getActivity(), TAG,
					"respList is null or zero");
			FeedbackUtil.showFeedbackToast(getActivity(), mHandler,
					R.string.str_generic_unknown_error);
			dismiss();
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						// Dismiss progress text
						if (mSendProgressText != null) {
							mSendProgressText.setVisibility(View.GONE);
							mPositiveButton.setEnabled(true);
							mNegativeButton.setEnabled(true);
						}

					}
				});
			}
		}).start();
	}

	private void showFeedbackToast(int resId) {
		StartNewConversationActivityUtil.showFeedbackToast(getActivity(),
				mHandler, getActivity().getString(resId));
	}

	@Override
	public void onAPITimeout() {
		DbgUtil.showDebug(TAG, "onAPITimeout");
		FeedbackUtil.showFeedbackToast(getActivity(), mHandler,
				R.string.str_generic_server_time_out);

		TrackingUtil.trackExceptionMessage(getActivity(), TAG,
				"API call timeout");

		new Thread(new Runnable() {
			@Override
			public void run() {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						// Dismiss progress text
						if (mSendProgressText != null) {
							mSendProgressText.setVisibility(View.GONE);
							mPositiveButton.setEnabled(true);
							mNegativeButton.setEnabled(true);
						}
					}
				});
			}
		}).start();

		// Before dismiss dialog, need to chcck if the target dialog is still
		// (to avoid null pointer exception especially server timeout case)
		// shown.
		// FragmentManager fragmentManager = getFragmentManager();
		// Fragment fragment = fragmentManager.findFragmentByTag("progress");
		// if (fragment != null && fragment instanceof DialogFragment) {
		// ((DialogFragment) fragment).dismiss();
		// }

		dismiss();
	}

	private void sendConfirmedMessage(String userId, String userName,
			String targetUserId, String targetUserName,
			String targetMailAddress, String targetMessage)
			throws WebAPIException {
		DbgUtil.showDebug(TAG, "targetUserId: " + targetUserId);

		LcomConst.LOCALE_SETTING locale = LocaleUtil.getCurrentLocale();

		String origin = TAG;
		String key[] = { LcomConst.SERVLET_ORIGIN, LcomConst.SERVLET_USER_ID,
				LcomConst.SERVLET_USER_NAME, LcomConst.SERVLET_MAILADDRESS,
				LcomConst.SERVLET_MESSAGE_BODY,
				LcomConst.SERVLET_TARGET_USER_ID,
				LcomConst.SERVLET_TARGET_USER_NAME, LcomConst.SERVLET_LANGUAGE,
				LcomConst.SERVLET_API_LEVEL };
		String value[] = { origin, userId, userName, targetMailAddress,
				targetMessage, targetUserId, targetUserName, locale.toString(),
				String.valueOf(LcomConst.API_LEVEL) };
		mWebAPI.sendData(LcomConst.SERVLET_NAME_NEW_INVITATION_CONFIRMED, key,
				value, targetMailAddress);
	}

	@Override
	public void notifyPresentDataset(ArrayList<FriendListData> userData) {
		DbgUtil.showDebug(TAG, "notifyPresentDataset");

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

	public void setInvitationConfirmationListener(
			InvitationConfirmationListener listener) {
		mListener = listener;
	}

	public interface InvitationConfirmationListener {
		public void onNotifyNewFriendAdded();
	}

	@Override
	public void notifyPresentMessageDataLoaded(
			ArrayList<MessageItemData> messageData) {
		DbgUtil.showDebug(TAG, "notifyPresentMessageData : not to be used");

	}

	@Override
	public void notifyNewConversationDataLoaded(
			ArrayList<MessageItemData> messageData) {
		DbgUtil.showDebug(TAG,
				"notifyNewConversationDataLoaded : not to be used");

	}

	@Override
	public void notifyFriendThubmailsLoaded(
			List<HashMap<Integer, Bitmap>> thumbnailsthumbnails) {
		DbgUtil.showDebug(TAG, "notifyFriendThubmailsLoaded - not to be used");

	}

	@Override
	public void notifyLatestStoredMessage(FriendListData result) {
		DbgUtil.showDebug(TAG, "notifyLatestStoredMessage - not to be used");
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
