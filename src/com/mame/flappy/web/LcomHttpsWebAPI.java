package com.mame.flappy.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.LayeredSocketFactory;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;

import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.mame.flappy.constant.LcomConst;
import com.mame.flappy.util.CipherUtil;
import com.mame.flappy.util.DbgUtil;
import com.mame.flappy.util.PrngUtils;

public class LcomHttpsWebAPI implements LcomAbstractServerAccessor {

	private static final String TAG = LcomConst.TAG + "/LcomHttpsWebAPI";

	private String mTargetUrl = null;

	private LcomWebAccessorListener mListener = null;

	private final Handler mHandler = new Handler();

	// 30 sec time out period
	private final static int API_WAIT_TIMER = 30000;

	private boolean mIsResponed = false;

	private static final int ACT_HTTPS_UPLOAD = 2;

	private PostThread mPostThread = null;

	private String mIdentifier = null;

	public LcomHttpsWebAPI() {
		PrngUtils.apply();
	}

	@Override
	public void sendData(String servletName, String[] key, String[] value,
			String identifier) {
		DbgUtil.showDebug(TAG, "sendData");
		mIdentifier = identifier;
		if (LcomConst.IS_DEBUG) {
			mTargetUrl = LcomConst.DEVELOPMENT_BASE_HTTPS_URL + "/"
					+ servletName;
		} else {
			mTargetUrl = LcomConst.RELEASE_BASE_HTTPS_URL + "/" + servletName;
		}

		mPostThread = new PostThread(ACT_HTTPS_UPLOAD, mTargetUrl, key, value,
				identifier);
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (!mIsResponed) {
					DbgUtil.showDebug(TAG, "Timeout");
					if (mListener != null) {
						mPostThread.interrupt();
						mListener.onAPITimeout();
					}
				}
			}

		}, API_WAIT_TIMER);
		mPostThread.start();
	}

	@Override
	public void interrupt() {
		if (mPostThread != null && mPostThread.isAlive() == true) {
			mPostThread.interrupt();
		}
	}

	@Override
	public void destroyAccessor() {
		mTargetUrl = null;
		mPostThread = null;
		mListener = null;
	}

	private class PostThread extends Thread {
		private String url;
		private int type;
		private List<NameValuePair> postParams;
		private List<String> mRespList = new ArrayList<String>();
		HttpClient mClient = new MyHttpClient();

		public PostThread(int type, String url, String[] key, String[] value,
				String identifier) {
			this.url = url;
			this.type = type;
			String secretKey = CipherUtil
					.createSecretKeyFromIdentifier(identifier);
			DbgUtil.showDebug(TAG, "secretKey: " + secretKey);
			postParams = new ArrayList<NameValuePair>();
			for (int i = 0; i < key.length; i++) {
				String cipher = CipherUtil.encrypt(value[i], secretKey);
				// postParams.add(new BasicNameValuePair(key[i], value[i]));
				postParams.add(new BasicNameValuePair(key[i], cipher));
			}
			// Send identifier
			postParams.add(new BasicNameValuePair(LcomConst.SERVLET_IDENTIFIER,
					secretKey));
		}

		@Override
		public void run() {
			HttpPost postMethod = new HttpPost(url);
			UrlEncodedFormEntity sendData;
			try {
				sendData = new UrlEncodedFormEntity(postParams, "UTF-8");
				postMethod.setEntity(sendData);
				// HttpResponse response = client.execute(new HttpGet(url));
				HttpResponse response;
				response = mClient.execute(postMethod);

				mIsResponed = true;

				Result result = new Result(response.getStatusLine()
						.getStatusCode(), response.getAllHeaders());
				if (result.getStatusCode() == HttpStatus.SC_OK) {
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					response.getEntity().writeTo(stream);
					result.setBytes(stream);
					stream.close();
					String str = result.getString();
					if (str != null) {
						try {
							JSONArray jsonArray = new JSONArray(
									result.getString());
							if (jsonArray != null) {

								String secretKey = CipherUtil
										.createSecretKeyFromIdentifier(mIdentifier);

								for (int i = 0; i < jsonArray.length(); i++) {
									mRespList.add(CipherUtil.decrypt(
											jsonArray.getString(i), secretKey));
								}
							}
						} catch (JSONException e) {
							Log.e(TAG, "JSONException: " + e.getMessage());
						}
						if (mListener != null) {
							mListener.onResponseReceived(mRespList);
						}
					} else {
						mListener.onResponseReceived(null);
					}
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				this.url = null;
				this.postParams = null;
				this.mRespList = null;
				mClient = null;
			}
		}

		@Override
		public void interrupt() {
			DbgUtil.showDebug(TAG, "interrupt");
			new Thread(new Runnable() {
				@Override
				public void run() {
					if (mClient != null) {
						mClient.getConnectionManager().shutdown();
						mClient = null;
					}
				}
			}).start();
		}
	}

	@Override
	public void setListener(LcomWebAccessorListener listener) {
		mListener = listener;
	}

	public interface LcomHttpsWebAPIListener extends LcomWebAccessorListener {

		@Override
		public void onResponseReceived(List<String> respList);

		@Override
		public void onAPITimeout();
	}

	class MyHttpClient extends DefaultHttpClient {
		@Override
		protected ClientConnectionManager createClientConnectionManager() {
			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			try {
				// Set SSL protocol (In this case, we use TLS)
				SSLContext sslcontext = SSLContext
						.getInstance(SSLSocketFactory.TLS);
				sslcontext.init(null,
				// Set how certificate work (In this case, we use X509
				// certificates)
						new TrustManager[] { new X509TrustManager() {
							@Override
							public void checkClientTrusted(
									X509Certificate[] chain, String authType)
									throws CertificateException {
							}

							@Override
							public void checkServerTrusted(
									X509Certificate[] chain, String authType)
									throws CertificateException {
							}

							@Override
							public X509Certificate[] getAcceptedIssuers() {
								return new X509Certificate[0];
							}
						} }, new SecureRandom());

				final javax.net.ssl.SSLSocketFactory socketfactory = sslcontext
						.getSocketFactory();

				registry.register(new Scheme("https",
						new LayeredSocketFactory() {
							@Override
							public Socket createSocket(Socket socket,
									String host, int port, boolean autoClose)
									throws IOException, UnknownHostException {
								return socketfactory.createSocket(socket, host,
										port, autoClose);
							}

							@Override
							public Socket connectSocket(Socket sock,
									String host, int port,
									InetAddress localAddress, int localPort,
									HttpParams params) throws IOException,
									UnknownHostException,
									ConnectTimeoutException {
								SSLSocket sslsock = (SSLSocket) ((sock != null) ? sock
										: createSocket());
								if (localAddress != null || localPort > 0) {
									InetSocketAddress isa = new InetSocketAddress(
											localAddress, localPort);
									sslsock.bind(isa);
								}
								int connTimeout = HttpConnectionParams
										.getConnectionTimeout(params);
								int soTimeout = HttpConnectionParams
										.getSoTimeout(params);
								InetSocketAddress remoteAddress;
								remoteAddress = new InetSocketAddress(host,
										port);
								sslsock.connect(remoteAddress, connTimeout);
								sslsock.setSoTimeout(soTimeout);
								return sslsock;
							}

							@Override
							public Socket createSocket() throws IOException {
								return socketfactory.createSocket();
							}

							@Override
							public boolean isSecure(Socket sock)
									throws IllegalArgumentException {
								return true;
							}
						}, 443));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			ClientConnectionManager cm = new ThreadSafeClientConnManager(
					getParams(), registry);
			return cm;
			// return new SingleClientConnManager(getParams(), registry);
		}
	}

	class Result implements HttpResult {
		private int code;
		private byte[] bytes;
		private Header[] headers;

		protected Result(int code, Header[] headers) {
			this.code = code;
			this.headers = headers;
		}

		@Override
		public int getStatusCode() {
			return this.code;
		}

		@Override
		public byte[] getBytes() {
			return this.bytes;
		}

		@Override
		public String getString() {
			return new String(this.bytes);
		}

		@Override
		public Header[] getHeaders() {
			return this.headers;
		}

		protected void setBytes(ByteArrayOutputStream stream) {
			bytes = stream.toByteArray();
		}
	}

	public interface HttpResult {
		/**
		 * @return HTTP status code
		 */
		public int getStatusCode();

		/**
		 * @return org.apache.http.Header Header#getName() と Header#getValue()
		 *         で参照できる。
		 */
		public Header[] getHeaders();

		/**
		 * @return 取得コンテンツ
		 */
		public byte[] getBytes();

		/**
		 * @return 取得コンテンツ
		 */
		public String getString();
	}
}
