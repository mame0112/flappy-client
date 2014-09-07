package com.mame.flappy.db;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import com.mame.flappy.constant.LcomConst;
import com.mame.flappy.data.ContactsListData;
import com.mame.flappy.util.DbgUtil;
import com.mame.flappy.util.TrackingUtil;

public class ContactInformationLoader {

	private final String TAG = LcomConst.TAG + "/ContactInformationLoader";

	private ContactInformationLoaderListener mListener = null;

	private Context mContext = null;

	public ContactInformationLoader() {

	}

	public void executeLoadContactInfo(Context context) {
		mContext = context;
		new LoadContactInfoAsyncTask().execute();
	}

	private class LoadContactInfoAsyncTask extends
			AsyncTask<Void, Void, ArrayList<ContactsListData>> {

		public LoadContactInfoAsyncTask() {
			DbgUtil.showDebug(TAG, "LoadContactInfoAsyncTask");
		}

		@Override
		protected ArrayList<ContactsListData> doInBackground(Void... params) {
			DbgUtil.showDebug(TAG, "doInBackground");
			return loadContactInformation(mContext);
		}

		@Override
		protected void onPostExecute(ArrayList<ContactsListData> result) {
			DbgUtil.showDebug(TAG, "LoadContactInfoAsyncTask onPostExecute");
			mListener.onContactInfoLoaded(result);
		}
	}

	private ArrayList<ContactsListData> loadContactInformation(Context context) {
		ArrayList<ContactsListData> result = new ArrayList<ContactsListData>();
		Cursor cursor = null;
		try {

			String[] PROJECTION = { ContactsContract.Contacts._ID,
					ContactsContract.Contacts.DISPLAY_NAME };

			cursor = context.getContentResolver().query(
					ContactsContract.Contacts.CONTENT_URI, PROJECTION, null,
					null, null);

			// ArrayList<String> ids = new ArrayList<String>();

			while (cursor.moveToNext()) {

				int columnIndex = cursor
						.getColumnIndex(ContactsContract.Contacts._ID);
				String id = cursor.getString(columnIndex);

				// ids.add(id);

				int nameIndex = cursor
						.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);

				ContactsListData data = new ContactsListData(id,
						cursor.getString(nameIndex), null, null);

				// Get mail address
				Cursor cMail = context.getContentResolver().query(
						ContactsContract.CommonDataKinds.Email.CONTENT_URI,
						null,
						ContactsContract.CommonDataKinds.Email.CONTACT_ID
								+ " =? ",
						new String[] { cursor.getString(columnIndex) }, null);
				while (cMail.moveToNext()) {
					data.setMailAddress(cMail.getString(cMail
							.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA1)));
					DbgUtil.showDebug(TAG,
							"mail address: " + data.getMailAddress());
				}
				cMail.close();

				// If mail address is null, we avoid to show it on ListView.
				if (data != null && data.getMailAddress() != null) {
					result.add(data);
				}

			}

		} catch (CursorIndexOutOfBoundsException e) {
			DbgUtil.showDebug(TAG, "No contacts: " + e.getMessage());
			TrackingUtil.trackExceptionMessage(context, TAG,
					"CursorIndexOutOfBoundsException: " + e.getMessage());
			// doNoContactsOperation();
		} catch (StringIndexOutOfBoundsException e2) {
			DbgUtil.showDebug(TAG, "No contacts: " + e2.getMessage());
			TrackingUtil.trackExceptionMessage(context, TAG,
					"StringIndexOutOfBoundsException: " + e2.getMessage());
			// doNoContactsOperation();
		} finally {
			cursor.close();
		}

		return result;
	}

	public void setContactInformationLoaderListener(
			ContactInformationLoaderListener listener) {
		mListener = listener;
	}

	public interface ContactInformationLoaderListener {
		public void onContactInfoLoaded(ArrayList<ContactsListData> result);
	}

}

// public class ContactInformationLoader extends
// AsyncTaskLoader<List<ContactsListData>> {
//
// private final String TAG = LcomConst.TAG + "/ContactInformationLoader";
//
// private Context mContext = null;
//
// private final static String THUMBNAIL_LOADER = "thumbnail_loader";
//
// public ContactInformationLoader(Context context) {
// super(context);
// mContext = context;
// }
//
// public ArrayList<ContactsListData> loadContactInformation() {
// ArrayList<ContactsListData> result = new ArrayList<ContactsListData>();
// try {
//
// String[] PROJECTION = { ContactsContract.Contacts._ID,
// ContactsContract.Contacts.DISPLAY_NAME };
//
// Cursor cursor = mContext.getContentResolver().query(
// ContactsContract.Contacts.CONTENT_URI, PROJECTION, null,
// null, null);
//
// ArrayList<String> ids = new ArrayList<String>();
//
// while (cursor.moveToNext()) {
//
// int columnIndex = cursor
// .getColumnIndex(ContactsContract.Contacts._ID);
// String id = cursor.getString(columnIndex);
//
// ids.add(id);
//
// int nameIndex = cursor
// .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
//
// ContactsListData data = new ContactsListData(id,
// cursor.getString(nameIndex), null, null);
//
// // Get mail address
// Cursor cMail = mContext.getContentResolver().query(
// ContactsContract.CommonDataKinds.Email.CONTENT_URI,
// null,
// ContactsContract.CommonDataKinds.Email.CONTACT_ID
// + " =? ",
// new String[] { cursor.getString(columnIndex) }, null);
// while (cMail.moveToNext()) {
// data.setMailAddress(cMail.getString(cMail
// .getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA1)));
// DbgUtil.showDebug(TAG,
// "mail address: " + data.getMailAddress());
// }
// cMail.close();
//
// // If mail address is null, we avoid to show it on ListView.
// if (data != null && data.getMailAddress() != null) {
// result.add(data);
// }
//
// }
//
// // Start to load thmbnail
// Bundle bundle = new Bundle();
// bundle.putStringArrayList(THUMBNAIL_LOADER, ids);
// // getLoaderManager().initLoader(LOADER_CONTACT_THUMBNAIL, bundle,
// // this).forceLoad();
//
// cursor.close();
//
// } catch (CursorIndexOutOfBoundsException e) {
// DbgUtil.showDebug(TAG, "No contacts: " + e.getMessage());
// TrackingUtil.trackExceptionMessage(mContext, TAG,
// "CursorIndexOutOfBoundsException: " + e.getMessage());
// // doNoContactsOperation();
// } catch (StringIndexOutOfBoundsException e2) {
// DbgUtil.showDebug(TAG, "No contacts: " + e2.getMessage());
// TrackingUtil.trackExceptionMessage(mContext, TAG,
// "StringIndexOutOfBoundsException: " + e2.getMessage());
// // doNoContactsOperation();
// }
//
// return result;
// }
//
// @Override
// public List<ContactsListData> loadInBackground() {
// // TODO Auto-generated method stub
// return null;
// }
//
// }
