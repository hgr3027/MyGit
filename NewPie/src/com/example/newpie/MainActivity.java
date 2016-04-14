package com.example.newpie;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.androidpn.client.Constants;
import org.androidpn.client.ServiceManager;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.newpie.bean.ActivityManager;
import com.example.newpie.bean.User;
import com.example.newpie.utils.HttpConnection;
import com.example.newpie.utils.MySlidingMenu;

public class MainActivity extends Activity implements OnClickListener {

	public static final String MYURL = "http://120.27.42.95:80/tecoa/";
	public static final String MYURL2 = "http://120.27.42.95:80/tecoa/android/";
	public static final String MYURL3 = "http://120.27.42.95:8080/Androidpn-tomcat/";
	public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
	public static final String KEY_MESSAGE = "message";
	public static final String KEY_EXTRAS = "extras";
	public static final String XMPP_USERNAME = "XMPP_USERNAME";
	public static final String XMPP_PASSWORD = "XMPP_PASSWORD";
	public static final String API_KEY = "API_KEY";
	public static final String VERSION = "VERSION";
	public static final String XMPP_HOST = "XMPP_HOST";
	public static final String XMPP_PORT = "XMPP_PORT";
	public static final String CALLBACK_ACTIVITY_PACKAGE_NAME = "CALLBACK_ACTIVITY_PACKAGE_NAME";
	public static final String CALLBACK_ACTIVITY_CLASS_NAME = "CALLBACK_ACTIVITY_CLASS_NAME";
	public static final String PAVKAGENAME = "com.example.newpie";
	public static final String FILENAME = "NewPie.apk";
	public static final String MY_SETTING = "accessrecord";
	@SuppressWarnings("unused")
	private static ExecutorService FULL_TASK_EXECUTOR;
	private MySlidingMenu mm;
	private String versionName;
	private Button button1;
	private Button button2;
	private EditText nameeditText;
	private EditText passeditText;
	private ImageView waitImageView;
	private AnimationDrawable animationDrawable;
	private int versionCode;
	private View includeLog;
	private TextView versionNameText;
	private int serverCode;
	private String serverName;
	private Boolean isNewVersion;
	private Boolean isgetNewVersion = false;
	private NotificationManager mManager;
	private Notification notification;
	@SuppressWarnings("unused")
	private Runnable runnable;
	private JSONObject userInformation;
	private ActivityManager exitM;
	private String SDCardRoot = null;
	private SharedPreferences sharedPrefs;
	private String downMark = "0";
	static {
		FULL_TASK_EXECUTOR = (ExecutorService) Executors
				.newSingleThreadExecutor();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);  
		Log.d("TAG", "Max memory is " + maxMemory + "KB");
		
		
		registerBoradcastReceiver();
		exitM = ActivityManager.getInstance();
		exitM.addActivity(MainActivity.this);
		button1 = (Button) findViewById(R.id.submitButton);
		button2 = (Button) findViewById(R.id.cancelButton);
		nameeditText = (EditText) findViewById(R.id.inputIdText);
		passeditText = (EditText) findViewById(R.id.inputPassText);
		includeLog = findViewById(R.id.includelog);
		versionNameText = (TextView) includeLog
				.findViewById(R.id.versionnametext);
		waitImageView = (ImageView) findViewById(R.id.waitimageview);
		mm = (MySlidingMenu) findViewById(R.id.myhs);
		initDir();
		aniStart();
		new checkVersion().execute("checkVersion");
		final Handler handler = new Handler();
		runnable = new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				if (null == mManager) {
					mManager = (NotificationManager) MainActivity.this
							.getSystemService(NOTIFICATION_SERVICE);
				}
				if (null == notification) {
					notification = new Notification();
				}
				notification = new Notification();
				notification.icon = R.drawable.ic_launcher;
				notification.flags = Notification.FLAG_AUTO_CANCEL;
				notification.defaults = Notification.DEFAULT_SOUND;

				PendingIntent m_PendingIntent = PendingIntent.getActivity(
						MainActivity.this, 0, getIntent(), 0);
				notification.setLatestEventInfo(MainActivity.this, "公司提醒",
						getResources().getString(R.string.notificationinfor),
						m_PendingIntent);
				mManager.notify(0, notification);
				handler.postDelayed(this, 5000);
			}
		};
	}

	private void initDir() {
		deleteUserDir();
	}

	public void registerBoradcastReceiver() {
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction("AndroidPnBoardKill");
		registerReceiver(mBroadcastReceiver, myIntentFilter);
	}

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("AndroidPnBoardKill")) {
				sharedPrefs = context.getSharedPreferences(
						Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPrefs.edit();
				editor.remove(Constants.XMPP_USERNAME);
				editor.remove(Constants.XMPP_PASSWORD);
				editor.remove(Constants.API_KEY);
				editor.remove(Constants.VERSION);
				editor.remove(Constants.XMPP_HOST);
				editor.remove(Constants.XMPP_PORT);
				editor.remove(Constants.CALLBACK_ACTIVITY_PACKAGE_NAME);
				editor.remove(Constants.CALLBACK_ACTIVITY_CLASS_NAME);
				if (null != userInformation) {
					try {
						if (null != (editor.remove(userInformation.getString(
								"company").toString()
								+ "_"
								+ userInformation.getString("userName")
										.toString()))) {

							editor.remove(userInformation.getString("company")
									.toString()
									+ "_"
									+ userInformation.getString("userName")
											.toString());
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				editor.clear();
				editor.commit();
				sharedPrefs = MainActivity.this.getSharedPreferences(
						MY_SETTING, Context.MODE_PRIVATE);
				SharedPreferences.Editor editor1 = sharedPrefs.edit();
				editor1.clear();
				editor1.commit();
				deleteUserDir();
				if (null == mManager) {
					mManager = (NotificationManager) MainActivity.this
							.getSystemService(NOTIFICATION_SERVICE);
				}
				if (null == notification) {
					notification = new Notification();
				}
				notification.icon = R.drawable.ic_launcher;
				notification.flags = Notification.FLAG_AUTO_CANCEL;
				notification.defaults = Notification.DEFAULT_SOUND;

				PendingIntent m_PendingIntent = PendingIntent.getActivity(
						MainActivity.this, 0, getIntent(), 0);
				notification.setLatestEventInfo(MainActivity.this, "公司提醒",
						getResources().getString(R.string.notificationinfor2),
						m_PendingIntent);
				mManager.notify(0, notification);
				exitM.exit();
			}
		}
	};

	private int getVerCodeName(Context context) {
		try {
			versionCode = context.getPackageManager().getPackageInfo(
					PAVKAGENAME, 0).versionCode;
			versionName = context.getPackageManager().getPackageInfo(
					PAVKAGENAME, 0).versionName;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
		return 1;
	}

	/**
	 * 验证版本
	 * 
	 * @author 韩国瑞
	 * 
	 */
	class checkVersion extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... arg0) {

			if (getVerCodeName(MainActivity.this) == 1
					&& getServerCodeName() == 1) {
				isgetNewVersion = true;
				isNewVersion = ifNewVersion(versionCode, serverCode,
						versionName, serverName);
			} else {
				isgetNewVersion = false;
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			if (isgetNewVersion) {
				if (isNewVersion) {
					Dialog dialog = new AlertDialog.Builder(MainActivity.this)
							.setTitle("软件更新")
							.setCancelable(false)
							.setMessage(
									getResources().getString(
											R.string.findnewversion))
							.setPositiveButton("更新",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											downFile();
										}
									})
							.setNegativeButton("暂不更新",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int whichButton) {
											startRun();
										}
									}).create();
					dialog.show();
					startRun();
				} else {
					Dialog dialog = new AlertDialog.Builder(MainActivity.this)
							.setTitle("软件更新")
							.setCancelable(false)
							.setMessage(
									getResources().getString(
											R.string.isnewversion))
							.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
										}
									}).create();
					dialog.show();
					startRun();
				}
			} else {
				Toast.makeText(MainActivity.this,
						getResources().getString(R.string.nonewversion),
						Toast.LENGTH_SHORT).show();
				startRun();
			}
			super.onPostExecute(result);
		}
	}

	private void startRun() {
		aniStop();
		button1.setOnClickListener(this);
		button2.setOnClickListener(this);
		versionNameText.setText("当前版本：" + versionName);
	}

	private int getServerCodeName() {
		try {
			HttpConnection connection = new HttpConnection(MainActivity.this);
			String verjson = connection.getServerVersion(MYURL2 + "ver.json");
			JSONArray array = new JSONArray(verjson);
			if (array.length() > 0) {
				JSONObject obj = array.getJSONObject(0);
				try {
					serverCode = Integer.parseInt(obj.getString("verCode"));
					serverName = (obj.getString("verName").toString());
				} catch (Exception e) {
					return 0;
				}
			}
		} catch (Exception e) {
			return 0;
		}
		return 1;
	}

	private Boolean ifNewVersion(int versionCode, int serverCode,
			String versionName, String serverName) {
		return (versionCode < serverCode);
	}

	private void downFile() {
		aniStart();
		new updataAsyncTask().execute(MYURL2 + FILENAME);
	}

	class updataAsyncTask extends AsyncTask<String, String, String> {

		@Override
		protected void onPostExecute(String result) {
			aniStop();
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(new File(Environment
					.getExternalStorageDirectory(), FILENAME)),
					"application/vnd.android.package-archive");
			startActivity(intent);
			super.onPostExecute(result);
		}

		@Override
		protected String doInBackground(String... arg0) {

			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(arg0[0]);
			HttpResponse response;
			try {
				response = client.execute(get);
				HttpEntity entity = response.getEntity();
				long length = entity.getContentLength();
				InputStream is = entity.getContent();
				FileOutputStream fileOutputStream = null;
				if (is != null) {
					File file = new File(
							Environment.getExternalStorageDirectory(), FILENAME);
					fileOutputStream = new FileOutputStream(file);
					byte[] buf = new byte[1024];
					int ch = -1;
					@SuppressWarnings("unused")
					int count = 0;
					while ((ch = is.read(buf)) != -1) {
						fileOutputStream.write(buf, 0, ch);
						count += ch;
						if (length > 0) {
						}
					}
				}
				fileOutputStream.flush();
				if (fileOutputStream != null) {
					fileOutputStream.close();
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
	}



	

	@SuppressWarnings("unchecked")
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.submitButton: {// 空姓名
			if ("".equals(nameeditText.getText().toString().trim())) {
				Toast.makeText(MainActivity.this,
						getResources().getString(R.string.namenull),
						Toast.LENGTH_SHORT).show();
				break;
			} else if ("".equals(passeditText.getText().toString().trim())) {// 空密码
				Toast.makeText(MainActivity.this,
						getResources().getString(R.string.passnull),
						Toast.LENGTH_SHORT).show();
				break;
			} else {
				Map<String, String> param = new HashMap<String, String>();
				param.put("userName", nameeditText.getText().toString());
				param.put("passWord", passeditText.getText().toString());
				new validationAsyncTask().execute(param);
				aniStart();
				break;
			}

		}
		case (R.id.cancelButton): {
			Toast.makeText(this, "如需申请测试账号，请联系QQ:605581564", Toast.LENGTH_LONG).show();
			break;
		}
		}

	}

	/**
	 * 登录
	 * 
	 * @author 韩国瑞
	 * 
	 */
	class validationAsyncTask extends
			AsyncTask<Map<String, String>, String, String> {

		@Override
		protected String doInBackground(Map<String, String>... params) {

			Log.v("tag", "验证数据");
			HttpConnection httpConnection = new HttpConnection(
					MainActivity.this);
			if (HttpConnection.isNetworkAvailable()) {
				Map<String, Object> getparams = new HashMap<String, Object>();
				getparams.put("userName", params[0].get("userName"));
				getparams.put("passWord", params[0].get("passWord"));

				try {
//					userInformation = new JSONObject(
//							httpConnection.parseStream(httpConnection
//									.listPostStreamConnection(MYURL
//											+ "validationUser", getparams)));
					userInformation = new JSONObject(
					httpConnection.parseStream(MYURL
									+ "validationUser", getparams));
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {

			try {
				if ("0".equals(userInformation.getString("ifSuccLogin")
						.toString())) {
					Toast.makeText(MainActivity.this, R.string.falseresult,
							Toast.LENGTH_SHORT).show();
					Log.v("tag", "验证失败");
					aniStop();
				} else {

					KillUser(userInformation.getString("company").toLowerCase()
							+ "_" + userInformation.getString("userName"));
				}
			} catch (NotFoundException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			super.onPostExecute(result);

		}

		private void KillUser(String params) {
			new KillUserAsyncTask().execute(params);
		}
	}

	class KillUserAsyncTask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... arg0) {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("param", arg0[0].toLowerCase());
			new HttpConnection(MainActivity.this).killUser(param, MYURL3
					+ "androidConreoller.do?action=killUser");
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			aniStop();
			User user = null;
			try {
				user = new User(userInformation.getString("userName"), // 用户名
						userInformation.getString("fullName"),// 姓名
						userInformation.getString("department"),// 所属部门
						userInformation.getString("position"),// 担任职务
						userInformation.getJSONArray("userRoles").toString(),// 用户权限
						userInformation.getJSONArray("userReceiveMessageBox")
								.toString(),// 收件箱
						("0".equals(userInformation.getString("fileattribute")) ? "0"
								: userInformation.getString("fileattribute")),// 头像
						userInformation.getString("company"));
			} catch (JSONException e) {
			}
			
			cleanNamePass();
			Intent intent = new Intent(MainActivity.this, IndexActivity.class);
			Bundle bundle = new Bundle();

			bundle.putSerializable("user", user);
			intent.putExtras(bundle);
			startActivity(intent);
			super.onPostExecute(result);
		}

	}

	@Override
	public void onBackPressed() {
		cleanNamePass();
		sharedPrefs = MainActivity.this.getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPrefs.edit();
		editor.remove(Constants.XMPP_USERNAME);
		editor.remove(Constants.XMPP_PASSWORD);
		editor.remove(Constants.API_KEY);
		editor.remove(Constants.VERSION);
		editor.remove(Constants.XMPP_HOST);
		editor.remove(Constants.XMPP_PORT);
		editor.remove(Constants.CALLBACK_ACTIVITY_PACKAGE_NAME);
		editor.remove(Constants.CALLBACK_ACTIVITY_CLASS_NAME);
		if (null != userInformation) {
			try {
				if (null != (editor.remove(userInformation.getString("company")
						.toString()
						+ "_"
						+ userInformation.getString("userName").toString()))) {

					editor.remove(userInformation.getString("company")
							.toString()
							+ "_"
							+ userInformation.getString("userName").toString());
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		editor.clear();
		editor.commit();
		sharedPrefs = MainActivity.this.getSharedPreferences(MY_SETTING,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor1 = sharedPrefs.edit();
		editor1.clear();
		editor1.commit();
		deleteUserDir();
		unregisterReceiver(mBroadcastReceiver);
		exitM.exit();
	}

	public void qcc(View v) {
		mm.qc();
	}

	@Override
	public void finish() {
		onDestroy();
		super.finish();
	}

	@Override
	protected void onDestroy() {
		cleanNamePass();
		sharedPrefs = MainActivity.this.getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPrefs.edit();
		editor.remove(Constants.XMPP_USERNAME);
		editor.remove(Constants.XMPP_PASSWORD);
		editor.remove(Constants.API_KEY);
		editor.remove(Constants.VERSION);
		editor.remove(Constants.XMPP_HOST);
		editor.remove(Constants.XMPP_PORT);
		editor.remove(Constants.CALLBACK_ACTIVITY_PACKAGE_NAME);
		editor.remove(Constants.CALLBACK_ACTIVITY_CLASS_NAME);
		if (null != userInformation) {
			try {
				if (null != (editor.remove(userInformation.getString("company")
						.toString()
						+ "_"
						+ userInformation.getString("userName").toString()))) {

					editor.remove(userInformation.getString("company")
							.toString()
							+ "_"
							+ userInformation.getString("userName").toString());
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		editor.clear();
		editor.commit();
		sharedPrefs = MainActivity.this.getSharedPreferences(MY_SETTING,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor1 = sharedPrefs.edit();
		editor1.clear();
		editor1.commit();
		deleteUserDir();
		super.onDestroy();
	}

	private void aniStart() {
		button1.setClickable(false);
		button2.setClickable(false);
		nameeditText.setFocusable(false);
		passeditText.setFocusable(false);
		waitImageView.setVisibility(View.VISIBLE);
		waitImageView.setBackgroundResource(R.anim.wait_anim);
		animationDrawable = (AnimationDrawable) waitImageView.getBackground();
		animationDrawable.start();
	}

	private void aniStop() {
		button1.setClickable(true);
		button2.setClickable(true);
		nameeditText.setFocusable(true);
		nameeditText.setFocusableInTouchMode(true);
		passeditText.setFocusable(true);
		passeditText.setFocusableInTouchMode(true);
		if (null != animationDrawable) {
			animationDrawable.stop();
			waitImageView.setVisibility(View.GONE);
		}
	}

	private void deleteUserDir() {
		if ((!"SD card Error".equals(getSDPath()))) {
			File file = new File(SDCardRoot + "NewPai" + File.separator);
			if (file.exists()) {
				delete(file);
			}
		} else {
			Toast.makeText(MainActivity.this,
					getResources().getString(R.string.noclean),
					Toast.LENGTH_SHORT).show();
		}

	}

	public static void delete(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}

		if (file.isDirectory()) {
			File[] childFiles = file.listFiles();
			if (childFiles == null || childFiles.length == 0) {
				file.delete();
				return;
			}

			for (int i = 0; i < childFiles.length; i++) {
				delete(childFiles[i]);
			}
			file.delete();
		}
	}

	public String getSDPath() {
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		if (sdCardExist) {
			SDCardRoot = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + File.separator;
		} else {
			return "SD card Error";
		}
		return SDCardRoot;
	}

	private void cleanNamePass() {
		if (null != nameeditText) {
			nameeditText.setText("");
		}
		if (null != passeditText) {
			passeditText.setText("");
		}
	}

}
