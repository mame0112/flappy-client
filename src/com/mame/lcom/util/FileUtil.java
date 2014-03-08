package com.mame.lcom.util;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.mame.lcom.constant.LcomConst;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class FileUtil {

	private final static String TAG = LcomConst.TAG + "/FileUtil";

	public static boolean storeBitmap(Context context, Bitmap bitmap,
			String fileName) {
		DbgUtil.showDebug(TAG, "storeBitmap");
		if (bitmap != null && fileName != null) {
			DbgUtil.showDebug(TAG, "bitmap: " + bitmap.getHeight() + " / "
					+ bitmap.getWidth());
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
			byte[] byteArray = output.toByteArray();
			OutputStream outputStream = null;
			try {
				outputStream = context.openFileOutput(fileName,
						Context.MODE_PRIVATE);
				// Execute write
				outputStream.write(byteArray);
				return true;
			} catch (FileNotFoundException e) {
				DbgUtil.showDebug(TAG,
						"FileNotFoundException: " + e.getMessage());
				return false;
			} catch (IOException e) {
				DbgUtil.showDebug(TAG, "IOException: " + e.getMessage());
				return false;
			}
		}
		return false;
	}

	public static Bitmap readBitmapData(Context context, String fileName) {
		Bitmap data = null;
		if (fileName != null) {
			try {
				InputStream inputstream = context.openFileInput(fileName);
				data = BitmapFactory.decodeStream(inputstream);
			} catch (FileNotFoundException e) {
				DbgUtil.showDebug(TAG,
						"FileNotFoundException: " + e.getMessage());
			}
		}
		return data;
	}
}
