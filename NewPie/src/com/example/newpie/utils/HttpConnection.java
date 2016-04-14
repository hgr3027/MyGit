package com.example.newpie.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.example.newpie.bean.MapUserTrail;
import org.apache.http.entity.mime.content.StringBody;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import org.apache.http.entity.mime.content.FileBody;

public class HttpConnection {

	private static Context context;
	public static final String MYURL = "http://120.27.42.95:8080/Androidpn-tomcat/";

	public HttpConnection(Context context) {
		super();
		HttpConnection.context = context;
	}

	/*
	 * �ж��ֻ��Ƿ�����
	 */
	public static boolean isNetworkAvailable() {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm != null) {
			NetworkInfo[] info = cm.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/*
	 * ������Map<String,String> ��ʽ��POST ���أ��� HttpClient
	 */
	public ByteArrayOutputStream listPostStreamClient(String stringUrl,
			Map<String, String> getparams) {

		HttpClient httpClient = new DefaultHttpClient();
		HttpEntity entity = null;
		ByteArrayOutputStream os = null;
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		for (Object o : getparams.keySet()) {
			params.add(new BasicNameValuePair((String) o, getparams.get(o)));
		}

		try {
			HttpPost postMethod = new HttpPost(stringUrl);
			postMethod.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
			HttpResponse response = httpClient.execute(postMethod);
			if (response.getStatusLine().getStatusCode() == 200) {
				entity = response.getEntity();
			}
			if (entity != null) {
				InputStream instream = entity.getContent();
				os = new ByteArrayOutputStream();
				int temp = 0;
				while ((temp = instream.read()) != -1) {
					os.write(temp);
				}

				os.flush();
				os.close();
				return os;
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
		}
		return null;

	}

	/*
	 * ������Map<String,String> ��ʽ��POST ���أ��� HttpURLConnection
	 */
	public InputStream listPostStreamConnection(String stringUrl,
			Map<String, Object> getparams) {

		HttpURLConnection connection = null;
		InputStream is = null;
		OutputStream outStream = null;
		byte[] entity = getParams(getparams);
		URL url = null;
		try {
			url = new URL(stringUrl);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		try {
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			outStream = connection.getOutputStream();
			outStream.write(entity);
			if (connection.getResponseCode() == 200) {
				is = connection.getInputStream();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return is;
	}

	/*
	 * ������Map<String,String> ��ʽ��POST ���أ��� HttpURLConnection
	 */
	public InputStream voidPostStreamConnection(String stringUrl) {

		HttpURLConnection connection = null;
		InputStream is = null;
		URL url = null;
		try {
			url = new URL(stringUrl);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		try {
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.connect();
			if (connection.getResponseCode() == 200) {
				is = connection.getInputStream();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return is;
	}

	/*
	 * ��������
	 */
	public String parseStream(String stringUrl,
			Map<String, Object> getparams) {
		
		
		HttpURLConnection connection = null;
		InputStream is = null;
		OutputStream outStream = null;
		byte[] entity = getParams(getparams);
		URL url = null;
		try {
			url = new URL(stringUrl);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		try {
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			outStream = connection.getOutputStream();
			outStream.write(entity);
			if (connection.getResponseCode() == 200) {
				is = connection.getInputStream();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		byte[] b = new byte[connection.getContentLength()];
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		try {
			while ((is.read(b)) != -1) {
				bo.write(b);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		String result = new String(bo.toByteArray());
		try {
			bo.close();
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public String getServerVersion(String filename) {
		URL url;
		String str;
		try {
			url = new URL(filename);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.connect();
			int len = conn.getContentLength();
			InputStream is = conn.getInputStream();
			byte[] by = new byte[len];
			is.read(by);
			str = new String(by, "UTF-8");
			return str;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}

	// public Boolean upPhoto(String filename, String RequestURL,
	// Map<String, Object> params) {
	// OutputStream outputSteam = null;
	// InputStream is = null;
	// String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
	// String PREFIX = "--", LINE_END = "\r\n";
	// String CONTENT_TYPE = "multipart/form-data"; // 内容类型
	// File file = new File(Environment.getExternalStorageDirectory(),
	// filename);
	// try {
	// URL url = new URL(RequestURL);
	// HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	// conn.setDoInput(true);
	// conn.setDoOutput(true);
	// conn.setUseCaches(false);
	// conn.setRequestMethod("POST");
	// conn.setRequestProperty("Charset", "utf-8");
	// conn.setRequestProperty("connection", "keep-alive");
	// conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="
	// + BOUNDARY);
	// outputSteam = conn.getOutputStream();
	// DataOutputStream dos = new DataOutputStream(outputSteam);
	// if (file != null) {
	//
	// StringBuffer sb = new StringBuffer();
	// sb.append(PREFIX);
	// sb.append(BOUNDARY);
	// sb.append(LINE_END);
	// sb.append("Content-Disposition: form-data; name=\"img\"; filename=\""
	// + file.getName() + "\"" + LINE_END);
	// sb.append("Content-Type: application/octet-stream; charset="
	// + "utf-8" + LINE_END);
	// sb.append(LINE_END);
	// dos.write(sb.toString().getBytes());
	// is = new FileInputStream(file);
	// byte[] bytes = new byte[1024];
	// int len = 0;
	// while ((len = is.read(bytes)) != -1) {
	// dos.write(bytes, 0, len);
	// }
	// is.close();
	// dos.write(LINE_END.getBytes());
	// byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)
	// .getBytes();
	// dos.write(end_data);
	// }
	// dos.flush();
	// int res = conn.getResponseCode();
	// if (res == 200) {
	// return true;
	// }
	// } catch (MalformedURLException e) {
	// e.printStackTrace();
	// return false;
	// } catch (IOException e) {
	// e.printStackTrace();
	// return false;
	// } finally {
	// try {
	// outputSteam.close();
	// if (is != null) {
	// is.close();
	// }
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	// }
	// return false;
	// }

	public Boolean upPhoto(String filename, String RequestURL,
			Map<String, Object> params) {
		HttpPost post = new HttpPost(RequestURL);
		HttpClient httpClient = new DefaultHttpClient();
		MultipartEntity entity = new MultipartEntity();
		HttpResponse response;
		if (params != null && !params.isEmpty()) {
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				try {
					entity.addPart(
							entry.getKey(),
							new StringBody((String) entry.getValue(), Charset
									.forName("UTF-8")));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					return false;
				}
			}
		}
		File file = new File(Environment.getExternalStorageDirectory()
				.toString() + File.separator + filename);

		Bitmap m = reduce(BitmapFactory.decodeFile(Environment
				.getExternalStorageDirectory().toString()
				+ File.separator
				+ filename), 100, 200, false);

		file.delete();

		CompressFormat format = Bitmap.CompressFormat.JPEG;
		int quality = 100;
		OutputStream stream = null;
		try {
			stream = new FileOutputStream(Environment
					.getExternalStorageDirectory().toString()
					+ File.separator
					+ filename);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		m.compress(format, quality, stream);

		entity.addPart("file", new FileBody(file));
		post.setEntity(entity);
		try {
			response = httpClient.execute(post);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		int stateCode = response.getStatusLine().getStatusCode();
		if (stateCode == HttpStatus.SC_OK) {
			post.abort();
		}
		return true;
	}

	private boolean checka(String a) {
		try {
			File f = new File(a);
			if (!f.exists()) {
				return false;
			}

		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		return true;

	}

	public MapUserTrail loadCurrUserCurrDateDataHttp(String currYear,
			String currMonth, String currDate, String currUserId,
			String currUserCompany) {

		MapUserTrail mut = new MapUserTrail();
		Map<String, Object> m1 = new HashMap<String, Object>();
		List<Map<String, Object>> lm = new ArrayList<Map<String, Object>>();
		m1.put("currUserId", "hgr");
		m1.put("currUserName", "韩国瑞");
		m1.put("latitude", 39.01001);
		m1.put("longitude", 117.02654);
		m1.put("reachTime", "2015年5月20日8:32");
		m1.put("ifPhotomark", "0");
		m1.put("photoId", "");
		m1.put("photoTime", "");
		m1.put("currUserCompany", "tkws");
		m1.put("photoNum", 0);
		m1.put("stopTime", "");
		lm.add((HashMap<String, Object>) m1);
		mut.setMapUserTrailList(lm);

		Map<String, String> timeMap = new HashMap<String, String>();

		/*
		 * 原始点
		 */
		int iii = 0;
		String a = "http://192.168.2.165:8080/tecoa/" + "android"
				+ File.separator + "download" + File.separator + "map"
				+ File.separator + currUserCompany + File.separator
				+ currUserId + "_basic" + File.separator;

		boolean c = checka(a);
		System.out.println(c);
		return null;
	}

	public MapUserTrail loadCurrUserCurrDateDataSD(String fileName) {

		MapUserTrail returnMapUserTrail = null;
		try {
			FileInputStream fis = new FileInputStream(fileName); // 获得输入流
			ObjectInputStream ois = new ObjectInputStream(fis);
			returnMapUserTrail = (MapUserTrail) ois.readObject();
			ois.close();
		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OptionalDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return returnMapUserTrail;
	}

	// public Map<String, String> loadCurrUserCurrDateMainPhotoUrl(String
	// photoId) {
	// Map<String, String> m = new HashMap<String, String>();
	// String photoUrl = "photoUrl";
	//
	// if ("123456789".equals(photoId)) {
	// m.put(photoUrl, "2015527_tkws_hgr_0833_0.jpg");
	//
	// } else if ("234567891".equals(photoId)) {
	// m.put(photoUrl, "2015527_tkws_hgr_0844_0.jpg");
	//
	// } else if ("345678912".equals(photoId)) {
	// m.put(photoUrl, "2015527_tkws_hgr_0859_0.jpg");
	//
	// } else {
	// m.put(photoUrl, "2015527_tkws_hgr_0922_0.jpg");
	//
	// }
	// return m;
	// }

	/*
	 * param Map POST return boolean
	 */
	public boolean mapPostConnection(String stringUrl,
			Map<String, Object> params) {

		StringBuilder data = new StringBuilder();
		if (params != null && !params.isEmpty()) {
			for (Map.Entry<String, Object> entry : params.entrySet()) {
				data.append(entry.getKey()).append("=");

				if (entry.getValue() instanceof Double
						|| entry.getValue() instanceof Integer)

				{
					data.append(entry.getValue());
				} else {
					try {
						data.append(URLEncoder.encode(entry.getValue()
								.toString(), "UTF-8"));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
				data.append("&");
			}
			data.deleteCharAt(data.length() - 1);
		}
		byte[] entity = data.toString().getBytes();
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) new URL(stringUrl).openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length",
					String.valueOf(entity.length));
			OutputStream outStream = conn.getOutputStream();
			outStream.write(entity);
			if (conn.getResponseCode() == 200) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private byte[] getParams(Map<String, Object> params) {

		StringBuilder data = new StringBuilder();
		if (params != null && !params.isEmpty()) {
			for (Entry<String, Object> entry : params.entrySet()) {
				data.append(entry.getKey()).append("=");
				try {
					data.append(URLEncoder.encode(entry.getValue().toString(),
							"UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				data.append("&");
			}
			data.deleteCharAt(data.length() - 1);
		}

		return data.toString().getBytes();
	}

	public Object loadNewDataHttp(Map<String, Object> params, String stringUrl) {
		byte[] data1 = null;
		StringBuilder data = new StringBuilder();
		InputStream is = null;
		OutputStream outStream = null;
		String ifcurrDate = null;
		String a;
		MapUserTrail aaa = new MapUserTrail();
		String currYear = "9999";
		String currMonth = "99";
		String currDate = "99";
		Map<String, Object> bbb = new HashMap<String, Object>();
		ByteArrayOutputStream baos = null;
		byte[] entity = getParams(params);
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) new URL(stringUrl).openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length",
					String.valueOf(entity.length));
			outStream = conn.getOutputStream();
			outStream.write(entity);
			if (conn.getResponseCode() == 200) {
				is = conn.getInputStream();
				baos = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int size = 0;
				while ((size = is.read(buffer)) >= 1) {
					baos.write(buffer, 0, size);
				}
				data1 = baos.toByteArray();
				if (0 != data1.length) {
					a = new String(data1);
					JSONObject jsonObj = new JSONObject(a);
					currYear = jsonObj.getString("currYear");
					currMonth = jsonObj.getString("currMonth");
					currDate = jsonObj.getString("currDate");
					// if (null != jsonObj.getString("ifcurrDate"))
					// {
					// ifcurrDate = jsonObj.getString("ifcurrDate");
					// }
					// else
					// {
					// ifcurrDate = "0";
					// }
					aaa = (MapUserTrail) loadNewDataHttp2(currYear, currMonth,
							currDate, params.get("currUserId").toString(),
							params.get("currUserCompany").toString());
				} else {
					Map<String, String> map = new HashMap<String, String>();
					map.put("currYear", currYear);
					map.put("currMonth", currMonth);
					map.put("currDate", currDate);
					// ifcurrDate = "0";
					aaa.setCurrTime(map);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != is) {
					is.close();
				}
				if (null != baos) {
					baos.close();
				}
				if (null != outStream) {
					outStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// bbb.put("mapUserTrail", aaa);
		// bbb.put("ifcurrDate", ifcurrDate);
		return aaa;
	}

	public Object loadNewDataHttp2(String currYear, String currMonth,
			String currDate, String currUserId, String currUserCompany) {
		InputStream is = null;
		StringBuffer result = new StringBuffer();
		ByteArrayOutputStream bo = null;
		MapUserTrail mapUserTrail = null;
		Map<String, String> map = new HashMap<String, String>();
		map.put("currYear", currYear);
		map.put("currMonth", currMonth);
		map.put("currDate", currDate);
		String basicPath = MYURL + "android" + File.separator + "download"
				+ File.separator + "map" + File.separator + currUserCompany
				+ File.separator + currUserId + File.separator + "basic"
				+ File.separator;
		HttpURLConnection conn = null;
		URL url;
		try {
			url = new URL(basicPath + currYear + currMonth + currDate
					+ File.separator + "basic.json");

			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (conn.getResponseCode() == 200) {
				is = conn.getInputStream();
				// byte[] b = new byte[1024];
				// bo = new ByteArrayOutputStream();
				// try {
				// while ((is.read(b)) != -1) {
				// bo.write(b);
				// }
				// } catch (IOException e) {
				// e.printStackTrace();
				// }

				// int count = 0;
				// while (count == 0) {
				// count = is.available();
				// }
				// byte[] b = new byte[count];
				//
				// int total = 0;
				// while (total < count) {
				// total += is.read(b, total, count - total);
				// bo.write(b);
				// }
				// System.out.println(bo.size()+"");
				// String result = new String(bo.toByteArray());
				// System.out.println("======================"+result.length()+"");

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is));

				String temp;
				while (null != (temp = reader.readLine())) {
					result.append(temp);
				}

				mapUserTrail = parseJson(result.toString());
				mapUserTrail.setCurrTime(map);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// try {
			// bo.close();
			// is.close();
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
			// conn.disconnect();
		}
		return mapUserTrail;
	}

	private MapUserTrail parseJson(String fileJson) {
		MapUserTrail mapUserTrail = new MapUserTrail();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		JSONArray ja = null;
		try {
			ja = new JSONArray(fileJson);
			for (int i = 0; i < ja.length(); i++) {
				JSONObject jo = ja.getJSONObject(i);
				if (!"2".equals(jo.getString("ifPhotomark"))) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("reachTime", jo.getString("reachTime"));
					map.put("photoNum", jo.getInt("photoNum"));
					map.put("d", jo.getString("d"));
					map.put("ifPhotomark", jo.getString("ifPhotomark"));
					map.put("photoId", jo.getString("photoId"));
					map.put("currUserId", jo.getString("currUserId"));
					map.put("currUserCompany", jo.getString("currUserCompany"));
					map.put("longitude", jo.getDouble("longitude"));
					map.put("latitude", jo.getDouble("latitude"));
					map.put("currUserName", jo.getString("currUserName"));
					map.put("stopTime", jo.getString("stopTime"));
					map.put("photoIfIndex", jo.getString("photoIfIndex"));
					map.put("photoUrl", jo.getString("photoUrl"));
					map.put("photoUrl", jo.getString("photoUrl"));
					map.put("currUserDepartment",
							jo.getString("currUserDepartment"));
					list.add((HashMap<String, Object>) map);
				}
			}
		} catch (JSONException e) {
			System.out.println("======================3");
			e.printStackTrace();
		}

		mapUserTrail.setMapUserTrailList(list);
		return mapUserTrail;
	}

	public Boolean closePhoto(String RequestURL, Map<String, Object> map) {
		StringBuilder data = new StringBuilder();
		if (map != null && !map.isEmpty()) {
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				data.append(entry.getKey()).append("=");

				if (entry.getValue() instanceof Double
						|| entry.getValue() instanceof Integer)

				{
					data.append(entry.getValue());
				} else {
					try {
						data.append(URLEncoder.encode(entry.getValue()
								.toString(), "UTF-8"));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
				data.append("&");
			}
			data.deleteCharAt(data.length() - 1);
		}
		byte[] entity = data.toString().getBytes();
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) new URL(RequestURL).openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length",
					String.valueOf(entity.length));
			OutputStream outStream = conn.getOutputStream();
			outStream.write(entity);
			if (conn.getResponseCode() == 200) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;

		// HttpPost post = new HttpPost(RequestURL);
		// HttpClient httpClient = new DefaultHttpClient();
		// MultipartEntity entity = new MultipartEntity();
		// HttpResponse response;
		// if (map != null && !map.isEmpty()) {
		// for (Map.Entry<String, Object> entry : map.entrySet()) {
		// try {
		// entity.addPart(
		// entry.getKey(),
		// new StringBody((String) entry.getValue(), Charset
		// .forName("UTF-8")));
		// } catch (UnsupportedEncodingException e) {
		// e.printStackTrace();
		// return false;
		// }
		// }
		// }
		// post.setEntity(entity);
		// try {
		// response = httpClient.execute(post);
		// } catch (Exception e) {
		// e.printStackTrace();
		// return false;
		// }
		// int stateCode = response.getStatusLine().getStatusCode();
		// if (stateCode == HttpStatus.SC_OK) {
		// post.abort();
		// return true;
		// } else {
		// post.abort();
		// return false;
		// }
	}

	public List<String> loadAllPhotoName(String stringUrl,
			Map<String, Object> params) {
		List<String> returnResult = new ArrayList<String>();
		byte[] data1 = null;
		StringBuilder data = new StringBuilder();
		InputStream is = null;
		OutputStream outStream = null;
		ByteArrayOutputStream baos = null;
		byte[] entity = getParams(params);
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) new URL(stringUrl).openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length",
					String.valueOf(entity.length));
			outStream = conn.getOutputStream();
			outStream.write(entity);
			if (conn.getResponseCode() == 200) {
				is = conn.getInputStream();
				baos = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int size = 0;
				while ((size = is.read(buffer)) >= 1) {
					baos.write(buffer, 0, size);
				}
				data1 = baos.toByteArray();
				if (null != data1) {
					String a = new String(data1);
					JSONArray jsonArray = new JSONArray(a);
					for (int i = 0; i < jsonArray.length(); i++) {
						returnResult.add(jsonArray.getJSONObject(i).getString(
								"photoUrl"));
					}
				}
			} else {
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
				baos.close();
				outStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return returnResult;
	}

	public void openSend(Map<String, Object> params, String stringUrl) {
		OutputStream outStream = null;
		byte[] entity = getParams(params);
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) new URL(stringUrl).openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length",
					String.valueOf(entity.length));
			outStream = conn.getOutputStream();
			outStream.write(entity);
			if (conn.getResponseCode() == 200) {
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				outStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 踢人
	 * 
	 * @param params
	 * @param stringUrl
	 */
	public String killUser(Map<String, Object> params, String stringUrl) {
		OutputStream outStream = null;
		byte[] entity = getParams(params);
		HttpURLConnection conn = null;
		InputStream is = null;
		String result = new String();
		try {
			conn = (HttpURLConnection) new URL(stringUrl).openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length",
					String.valueOf(entity.length));
			outStream = conn.getOutputStream();
			outStream.write(entity);
			if (conn.getResponseCode() == 200) {
				is = conn.getInputStream();

				byte[] b = new byte[10];
				ByteArrayOutputStream bo = new ByteArrayOutputStream();
				try {
					while ((is.read(b)) != -1) {
						bo.write(b);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				result = new String(bo.toByteArray());
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				outStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public void upLine(Map<String, Object> params, String stringUrl) {
		OutputStream outStream = null;
		byte[] entity = getParams(params);
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) new URL(stringUrl).openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length",
					String.valueOf(entity.length));
			outStream = conn.getOutputStream();
			outStream.write(entity);

			if (conn.getResponseCode() == 200) {
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				outStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public String loadAddressList(Map<String, Object> myUserId, String stringUrl) {
		OutputStream outStream = null;
		String result = new String();
		InputStream is = null;
		byte[] entity = getParams(myUserId);
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) new URL(stringUrl).openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length",
					String.valueOf(entity.length));
			outStream = conn.getOutputStream();
			outStream.write(entity);

			if (conn.getResponseCode() == 200) {
				System.out.println(1024);
				is = conn.getInputStream();

				byte[] b = new byte[conn.getContentLength()];
				ByteArrayOutputStream bo = new ByteArrayOutputStream();
				try {
					while ((is.read(b)) != -1) {
						bo.write(b);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				result = new String(bo.toByteArray());
				try {
					bo.close();
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				outStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public void subscribeUser(Map<String, Object> params, String stringUrl) {
		OutputStream outStream = null;
		byte[] entity = getParams(params);
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) new URL(stringUrl).openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length",
					String.valueOf(entity.length));
			outStream = conn.getOutputStream();
			outStream.write(entity);

			if (conn.getResponseCode() == 200) {
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				outStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public Bitmap reduce(Bitmap bitmap, int width, int height, boolean isAdjust) {
		// 如果想要的宽度和高度都比源图片小，就不压缩了，直接返回原图
		if (bitmap.getWidth() < width && bitmap.getHeight() < height) {
			return bitmap;
		}
		// 根据想要的尺寸精确计算压缩比例, 方法详解：public BigDecimal divide(BigDecimal divisor,
		// int scale, int roundingMode);
		// scale表示要保留的小数位, roundingMode表示如何处理多余的小数位，BigDecimal.ROUND_DOWN表示自动舍弃
		float sx = new BigDecimal(width).divide(
				new BigDecimal(bitmap.getWidth()), 4, BigDecimal.ROUND_DOWN)
				.floatValue();
		float sy = new BigDecimal(height).divide(
				new BigDecimal(bitmap.getHeight()), 4, BigDecimal.ROUND_DOWN)
				.floatValue();
		if (isAdjust) {// 如果想自动调整比例，不至于图片会拉伸
			sx = (sx < sy ? sx : sy);
			sy = sx;// 哪个比例小一点，就用哪个比例
		}
		Matrix matrix = new Matrix();
		matrix.postScale(sx, sy);// 调用api中的方法进行压缩，就大功告成了
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
	}
}