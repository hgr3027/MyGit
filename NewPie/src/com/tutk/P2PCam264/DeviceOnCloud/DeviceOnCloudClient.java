package com.tutk.P2PCam264.DeviceOnCloud;

import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
@SuppressWarnings("all")
public class DeviceOnCloudClient {
	public static final int ERROR_PASSWORD = 3;
	public static final int UPLOADOK = 0;
	public static final int UPLOADERROR = -2;
	public static final int DOWNLOADOK = 1;
	public static final int DOWNLOADERROR = -1;
	public static final int ERROR_AUTHORIZATION = -3;
	public static final int ERROR_NOT_ACTIVE = -4;
	private DefaultHttpClient DefaultHttpClient = null;
	private DeviceOnCloudClientInterface mDeviceOnCloudClientInterface;
	private upload_Thread upload_Thread = null;
	private download_Thread download_Thread = null;
	private JSONObject DownLoadjson = null;

	public void RegistrInterFace(DeviceOnCloudClientInterface InterFace) {
		mDeviceOnCloudClientInterface = InterFace;
	}

	public void unRegistrInterFace() {
		mDeviceOnCloudClientInterface = null;
	}

	public DeviceOnCloudClient() {
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
		HttpProtocolParams.setUseExpectContinue(params, true);
		SchemeRegistry schReg = new SchemeRegistry();
		KeyStore trustStore = null;
		SSLSocketFactory sf = null;
		try {
			KeyStore.getInstance(KeyStore.getDefaultType());
			// trustStore.load(null, null);
			sf = new MySSLSocketFactory(trustStore);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schReg.register(new Scheme("https", sf, 443));
		ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);

		DefaultHttpClient = new DefaultHttpClient(conMgr, params);
	}

	public JSONObject getJSONObject() {
		return DownLoadjson;
	}

	public void upload(String url, String account, String passwd, JSONObject jsonObject) {
		upload_Thread = new upload_Thread(url, account, passwd, jsonObject);
		upload_Thread.start();
	}

	public void uploadstop() {
		if (upload_Thread != null) {
			upload_Thread.Threadstop();
			upload_Thread = null;
		}
	}

	private class upload_Thread extends Thread {
		private boolean stopThread = false;
		private String mURL;
		private JSONObject mjsonObject;
		private String mAccount;
		private String mPasswd;

		private upload_Thread(String url, String account, String passwd, JSONObject jsonObject) {
			mURL = url;
			mjsonObject = jsonObject;
			mAccount = account;
			mPasswd = passwd;
		}

		public void Threadstop() {
			stopThread = true;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			Message msg = Message.obtain(handler);
			msg.what = mHttpUpLoadPosts(mURL, mAccount, mPasswd, mjsonObject);
			if (stopThread)
				return;
			handler.sendMessage(msg);
		}
	}

	public int mHttpUpLoadPosts(String fromURL, String account, String passwd, JSONObject Upjson) {

		HttpClient httpClient = DefaultHttpClient;
		HttpPost httpRequest;
		httpRequest = new HttpPost(fromURL);
		httpRequest.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("account", account));
		params.add(new BasicNameValuePair("passwd", passwd));
		params.add(new BasicNameValuePair("upjson", Upjson.toString()));
		try {
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		try {
			HttpResponse httpResponse = httpClient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				String Response = EntityUtils.toString(httpResponse.getEntity());
				JSONObject jsonObject = null;
				try {
					jsonObject = new JSONObject(Response);
				} catch (JSONException e) {

				}
				if (jsonObject == null)
					return UPLOADERROR;

				String status = jsonObject.get("status").toString();
				if (status.equals("ok")) {
					return UPLOADOK;
				} else if (status.equals("user or password error")) {
					return ERROR_PASSWORD;
				} else {
					return UPLOADERROR;
				}
			} else {
				return UPLOADERROR;
			}
		} catch (Exception e) {
			System.out.println("error");
			return UPLOADERROR;
		}
	}

	public void download(String url, JSONObject Upjson) {
		download_Thread = new download_Thread(url, Upjson);
		download_Thread.start();
	}

	public void downloadstop() {
		if (download_Thread != null) {
			download_Thread.Threadstop();
			download_Thread = null;
		}
	}

	private class download_Thread extends Thread {
		private boolean stopThread = false;
		private String mURL;
		private JSONObject mjsonObject;

		private download_Thread(String url, JSONObject Upjson) {
			mURL = url;
			mjsonObject = Upjson;
			stopThread = false;
		}

		public void Threadstop() {
			stopThread = true;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			Message msg = Message.obtain(handler);
			msg.what = mHttpDownLoadPosts(mURL, mjsonObject);
			if (stopThread)
				return;
			handler.sendMessage(msg);
		}
	}

	public int mHttpDownLoadPosts(String fromURL, JSONObject Upjson) {

		HttpClient httpClient = DefaultHttpClient;
		HttpPost httpRequest;
		httpRequest = new HttpPost(fromURL);
		httpRequest.addHeader("Content-Type", "application/x-www-form-urlencoded");
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("upjson", Upjson.toString()));
		try {
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		try {
			HttpResponse httpResponse = httpClient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				String Response = EntityUtils.toString(httpResponse.getEntity());
				DownLoadjson = ResponseToJSONArray(Response);
				if (DownLoadjson != null) {
					String status = DownLoadjson.getString("status");
					if (status.equals("ok") || status.equals("uid not found")) {
						return DOWNLOADOK;
					} else if (status.equals("user or password error")) {
						return ERROR_PASSWORD;
					} else if (status.equals("authorization failed")) {

						return ERROR_AUTHORIZATION;
					} else if (status.equals("account not active")) {

						return ERROR_NOT_ACTIVE;
					} else {
						return DOWNLOADERROR;
					}
				} else {
					return DOWNLOADERROR;
				}
			} else {
				return DOWNLOADERROR;
			}
		} catch (Exception e) {
			System.out.println("error");
			return DOWNLOADERROR;
		}

	}

	private JSONObject ResponseToJSONArray(String Response) {
		JSONObject json_obj = null;
		try {
			json_obj = new JSONObject(Response);
			return json_obj;
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;

	}

	private Handler handler = new Handler() {
		public void handleMessage(Message message) {

			switch (message.what) {
			case UPLOADOK:
				if (mDeviceOnCloudClientInterface != null)
					mDeviceOnCloudClientInterface.uploadok(UPLOADOK);
				break;
			case DOWNLOADOK:
				if (mDeviceOnCloudClientInterface != null)
					mDeviceOnCloudClientInterface.downloadok(DOWNLOADOK);
				break;
			case UPLOADERROR:
				if (mDeviceOnCloudClientInterface != null)
					mDeviceOnCloudClientInterface.error(UPLOADERROR);
				break;
			case DOWNLOADERROR:
				if (mDeviceOnCloudClientInterface != null)
					mDeviceOnCloudClientInterface.error(DOWNLOADERROR);
				break;

			case ERROR_PASSWORD:
				if (mDeviceOnCloudClientInterface != null)
					mDeviceOnCloudClientInterface.error(ERROR_PASSWORD);
				break;

			case ERROR_AUTHORIZATION:
				if (mDeviceOnCloudClientInterface != null)
					mDeviceOnCloudClientInterface.error(ERROR_AUTHORIZATION);
				break;

			case ERROR_NOT_ACTIVE:
				if (mDeviceOnCloudClientInterface != null)
					mDeviceOnCloudClientInterface.error(ERROR_NOT_ACTIVE);
				break;
			}
		}
	};

}
