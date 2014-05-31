package com.mame.flappy.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.LayeredSocketFactory;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.mame.flappy.constant.LcomConst;

public class LcomHttpsWebAPI implements Callable<LcomHttpsWebAPI.HttpResult> {
	private String url;

	private List<NameValuePair> postParams;

	public LcomHttpsWebAPI(String url) {
		this.url = url;
	}

	@Override
	public HttpResult call() throws Exception {
		HttpClient client = new MyHttpClient(); // → DefaultHttpClient継承

		// ---

		String value[] = { "LCom/LoginActivity", "aaaa", "bbbb",
				String.valueOf(LcomConst.API_LEVEL) };
		String key[] = { LcomConst.SERVLET_ORIGIN, LcomConst.SERVLET_USER_NAME,
				LcomConst.SERVLET_PASSWORD, LcomConst.SERVLET_API_LEVEL };
		postParams = new ArrayList<NameValuePair>();
		for (int i = 0; i < key.length; i++) {
			postParams.add(new BasicNameValuePair(key[i], value[i]));
		}

		HttpPost postMethod = new HttpPost(url);
		UrlEncodedFormEntity sendData = new UrlEncodedFormEntity(postParams,
				"UTF-8");
		postMethod.setEntity(sendData);
		// ---

		// HttpResponse response = client.execute(new HttpGet(url));
		HttpResponse response = client.execute(postMethod);

		Result result = new Result(response.getStatusLine().getStatusCode(),
				response.getAllHeaders());
		if (result.getStatusCode() == HttpStatus.SC_OK) {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			response.getEntity().writeTo(stream);
			result.setBytes(stream);
			stream.close();
		}
		return result;
	}

	class MyHttpClient extends DefaultHttpClient {
		@Override
		protected ClientConnectionManager createClientConnectionManager() {
			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			try {
				SSLContext sslcontext = SSLContext
						.getInstance(SSLSocketFactory.TLS);
				sslcontext.init(null,
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
			return new SingleClientConnManager(getParams(), registry);
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
