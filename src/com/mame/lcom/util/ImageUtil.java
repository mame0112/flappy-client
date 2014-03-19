package com.mame.lcom.util;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.util.Base64;

public class ImageUtil {
	public static String encodeTobase64(Bitmap bitmap) {
		String imageBinary = null;

		if (bitmap != null) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.JPEG, 90, bos);
			byte[] byteArray = bos.toByteArray();
			String image64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
			imageBinary = image64;
			// imageBinary = "data:image/jpeg:base64, " + image64;
		}
		return imageBinary;

	}

	public static Bitmap decodeBase64ToBitmap(String input) {
		byte[] decodedByte = Base64.decode(input, 0);
		return BitmapFactory
				.decodeByteArray(decodedByte, 0, decodedByte.length);
	}

	public static byte[] decodeBase64ToByteArray(String input) {
		return Base64.decode(input, 0);
	}

	public static Bitmap decodeByteArrayToBitmap(byte[] input) {
		return BitmapFactory.decodeByteArray(input, 0, input.length);
	}

}
