package com.mame.lcom.ui;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;

import com.mame.lcom.constant.LcomConst;
import com.mame.lcom.util.DbgUtil;

public class ProgressDialogFragment extends DialogFragment {

	private final String TAG = LcomConst.TAG + "/ProgressDialogFragment";

	private static ProgressDialog progressDialog = null;

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
		if (progressDialog != null)
			return progressDialog;

		String title = getArguments().getString("title");
		String message = getArguments().getString("message");

		progressDialog = new ProgressDialog(getActivity());
		progressDialog.setTitle(title);
		progressDialog.setMessage(message);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		setCancelable(false);

		return progressDialog;
	}

	@Override
	public Dialog getDialog() {
		return progressDialog;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		progressDialog = null;
	}

	public boolean isShowing() {
		if (progressDialog != null) {
			if (progressDialog.isShowing()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void dismiss() {
		super.dismiss();
		if (progressDialog != null) {
			try {
				progressDialog.dismiss();
				progressDialog = null;
			} catch (IllegalStateException e) {
				DbgUtil.showDebug(TAG,
						"IllegalStateException: " + e.getMessage());
			}
		}
	}
}
