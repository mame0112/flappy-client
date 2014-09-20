package com.mame.flappy.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;

import com.mame.flappy.constant.LcomConst;

public class CipherUtil {

	private final static String TAG = LcomConst.TAG + "/CipherUtil";

	// public static final String ENCRYPT_KEY = "1234567890123456";
	public static final String ENCRYPT_IV = "loosecomm_vector";

	private final static int AES_KEY_LENGTH = 16;

	public static String encrypt(String text, String secretKey) {
		DbgUtil.showDebug(TAG, "encrypt");
		String strResult = null;

		if (text != null && secretKey != null) {
			try {
				// Decode base64 to byte array
				byte[] byteText = text.getBytes("UTF-8");

				// Trancode decode key and initialize vector to byte array
				byte[] byteKey = secretKey.getBytes("UTF-8");
				byte[] byteIv = ENCRYPT_IV.getBytes("UTF-8");

				// Create object for decode key and initialize vector
				SecretKeySpec key = new SecretKeySpec(byteKey, "AES");
				IvParameterSpec iv = new IvParameterSpec(byteIv);

				// Create Cipher object
				Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

				// Initialize Cipher object
				cipher.init(Cipher.ENCRYPT_MODE, key, iv);

				// Get cipher result
				byte[] byteResult = cipher.doFinal(byteText);

				// Encode to base64
				// strResult = Base64.encodeBase64String(byteResult);
				// strResult = new String(Base64.encodeBase64(byteResult));
				strResult = new String(Base64.encodeToString(byteResult,
						Base64.DEFAULT));

			} catch (UnsupportedEncodingException e) {
				DbgUtil.showDebug(TAG,
						"UnsupportedEncodingException: " + e.getMessage());
			} catch (NoSuchAlgorithmException e) {
				DbgUtil.showDebug(TAG,
						"NoSuchAlgorithmException: " + e.getMessage());
			} catch (NoSuchPaddingException e) {
				DbgUtil.showDebug(TAG,
						"NoSuchPaddingException: " + e.getMessage());
			} catch (InvalidKeyException e) {
				DbgUtil.showDebug(TAG, "InvalidKeyException: " + e.getMessage());
			} catch (IllegalBlockSizeException e) {
				DbgUtil.showDebug(TAG,
						"IllegalBlockSizeException: " + e.getMessage());
			} catch (BadPaddingException e) {
				DbgUtil.showDebug(TAG, "BadPaddingException: " + e.getMessage());
			} catch (InvalidAlgorithmParameterException e) {
				DbgUtil.showDebug(TAG, "InvalidAlgorithmParameterException: "
						+ e.getMessage());
			}

		}

		return strResult;
	}

	public static String decrypt(String text, String secretKey) {
		DbgUtil.showDebug(TAG, "decrypt");
		String strResult = null;

		if (text != null && secretKey != null) {
			try {
				// Decode base64 to byte array
				// byte[] byteText = Base64.decodeBase64(text);
				byte[] byteText = Base64.decode(text, Base64.DEFAULT);

				// Trancode decode key and initialize vector to byte array
				byte[] byteKey = secretKey.getBytes("UTF-8");
				byte[] byteIv = ENCRYPT_IV.getBytes("UTF-8");

				// Create object for decode key and initialize vector
				SecretKeySpec key = new SecretKeySpec(byteKey, "AES");
				IvParameterSpec iv = new IvParameterSpec(byteIv);

				// Create Cipher object
				Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

				// Initialize Cipher object
				cipher.init(Cipher.DECRYPT_MODE, key, iv);

				// Get decoded result
				byte[] byteResult = cipher.doFinal(byteText);

				// Transcode byte array to string
				strResult = new String(byteResult, "UTF-8");

			} catch (UnsupportedEncodingException e) {
				DbgUtil.showDebug(TAG,
						"UnsupportedEncodingException: " + e.getMessage());
			} catch (NoSuchAlgorithmException e) {
				DbgUtil.showDebug(TAG,
						"NoSuchAlgorithmException: " + e.getMessage());
			} catch (NoSuchPaddingException e) {
				DbgUtil.showDebug(TAG,
						"NoSuchPaddingException: " + e.getMessage());
			} catch (InvalidKeyException e) {
				DbgUtil.showDebug(TAG, "InvalidKeyException: " + e.getMessage());
			} catch (IllegalBlockSizeException e) {
				DbgUtil.showDebug(TAG,
						"IllegalBlockSizeException: " + e.getMessage());
			} catch (BadPaddingException e) {
				DbgUtil.showDebug(TAG, "BadPaddingException: " + e.getMessage());
			} catch (InvalidAlgorithmParameterException e) {
				DbgUtil.showDebug(TAG, "InvalidAlgorithmParameterException: "
						+ e.getMessage());
			}

		}

		return strResult;
	}

	public static String createSecretKeyFromIdentifier(String identifier) {
		if (identifier != null) {
			String result = UUID.nameUUIDFromBytes(identifier.getBytes())
					.toString();
			result = result.substring(0, AES_KEY_LENGTH);
			return result;
		}
		return null;
	}
}
