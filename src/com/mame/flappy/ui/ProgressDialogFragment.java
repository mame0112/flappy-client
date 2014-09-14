package com.mame.flappy.ui;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.os.Bundle;

import com.mame.flappy.constant.LcomConst;
import com.mame.flappy.util.DbgUtil;

public class ProgressDialogFragment extends DialogFragment {

	private final String TAG = LcomConst.TAG + "/ProgressDialogFragment";

	public ProgressDialogFragment() {

	}

	public static ProgressDialogFragment newInstance(String title,
			String message) {

		ProgressDialogFragment instance = new ProgressDialogFragment();

		Bundle arguments = new Bundle();
		arguments.putString("title", title);
		arguments.putString("message", message);
		instance.setArguments(arguments);

		return instance;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		ProgressDialog mProgressDialog = null;

		String title = getArguments().getString("title");
		String message = getArguments().getString("message");

		mProgressDialog = new ProgressDialog(getActivity());
		mProgressDialog.setTitle(title);
		mProgressDialog.setMessage(message);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		setCancelable(false);

		return mProgressDialog;
	}

}
