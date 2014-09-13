package com.mame.flappy.ui;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;

public class ProgressDialogFragmentHelper {

	public ProgressDialogFragmentHelper() {

	}

	public void showProgressDialog(Activity activity, String title,
			String description, String tag) {
		FragmentTransaction ft = activity.getFragmentManager()
				.beginTransaction();
		Fragment prev = activity.getFragmentManager().findFragmentByTag(tag);
		if (prev != null) {
			ft.remove(prev);
		}
		ft.addToBackStack(null);

		// Create and show the dialog.
		Fragment newFragment = ProgressDialogFragment.newInstance(title,
				description);
		((DialogFragment) newFragment).show(ft, tag);
	}

	public void dismissDialog(Activity activity, String tag) {
		Fragment prev = activity.getFragmentManager().findFragmentByTag(tag);
		if (prev != null) {
			// if (prev instanceof ProgressDialogFragment) {
			((ProgressDialogFragment) prev).dismiss();
			// }
		}
	}
}
