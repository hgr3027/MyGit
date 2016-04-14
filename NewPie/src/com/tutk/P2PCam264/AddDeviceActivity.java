package com.tutk.P2PCam264;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newpie.R;
import com.tutk.IOTC.Camera;
import com.tutk.IOTC.st_LanSearchInfo;
import com.tutk.P2PCam264.DELUX.InitCamActivity;
import com.tutk.P2PCam264.DELUX.MultiViewActivity;
import com.tutk.P2PCam264.DeviceOnCloud.DeviceOnCloudClient;
import com.tutk.P2PCam264.DeviceOnCloud.DeviceOnCloudClientInterface;

@SuppressLint("all")
public class AddDeviceActivity extends Activity implements OnClickListener, DeviceOnCloudClientInterface {

	private final int REQUEST_CODE_GETUID_BY_SCAN_BARCODE = 0;

	public static final int ADD_DEVICE_FROM_CLOUD = 0;
	public static final int ADD_DEVICE_FROM_WIRED = 1;
	public static final int ADD_DEVICE_FROM_WIRELESS = 2;

	private TextView mDeviceSSIDTxtView;
	private EditText mUIDEdtTxt;
	private EditText mSecurityEdtTxt;
	private EditText mNameEdtTxt;
	private ImageButton mScanBtn;
	private ImageButton mSearchBtn;
	private Button mOkBtn;
	private Button mCancelBtn;
	private CheckBox mAddToCloud;
	private CheckBox mSyncToCloud;
	private ResultStateReceiver mResultStateReceiver;
	private RelativeLayout mTouchLayout;

	private int mAddMode;
	private int wifi_enc = -1;
	private String mUID = null;
	private String mDeviceSSID = null;
	private String mDeviceNickName = null;
	private String mType = "IP Camera";
	private String mWifiSSID;
	private String mWifiPassword;

	private DeviceOnCloudClient mDeviceOnCloudClient;

	// private final static String ZXING_DOWNLOAD_URL =
	// "http://zxing.googlecode.com/files/BarcodeScanner3.72.apk";
	// private final static String ZXING_DOWNLOAD_URL =
	// "http://www.tutk.com/IOTCApp/Android/BarcodeScanner.apk";
	private final static String ZXING_DOWNLOAD_URL = "http://push.iotcplatform.com/release/BarcodeScanner.apk";

	private List<SearchResult> list = new ArrayList<SearchResult>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle bundle = this.getIntent().getExtras();
		mAddMode = bundle.getInt("addMode");
		mUID = bundle.getString("uid");
		mDeviceSSID = bundle.getString("select_device_ssid");
		mDeviceNickName = bundle.getString("nickName");
		mWifiSSID = bundle.getString("wifi_ssid");
		mWifiPassword = bundle.getString("wifi_password");
		wifi_enc = bundle.getInt("wifi_enc");

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setCustomView(R.layout.titlebar);
		TextView tv = (TextView) this.findViewById(R.id.bar_text);
		tv.setText(getText(R.string.dialog_AddCamera));
		setContentView(R.layout.add_device);

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(AddDeviceActivity.class.getName());
		mResultStateReceiver = new ResultStateReceiver();

		registerReceiver(mResultStateReceiver, intentFilter);

		mUIDEdtTxt = (EditText) findViewById(R.id.edtUID);
		if (mUID != null) {
			mUIDEdtTxt.setText(mUID);
			mUIDEdtTxt.setEnabled(false);
		}

		mDeviceSSIDTxtView = (TextView) findViewById(R.id.ssidTextView);

		mSecurityEdtTxt = (EditText) findViewById(R.id.edtSecurityCode);
		mNameEdtTxt = (EditText) findViewById(R.id.edtNickName);

		mAddToCloud = (CheckBox) findViewById(R.id.addToCloud);
		mSyncToCloud = (CheckBox) findViewById(R.id.syncToCloud);

		mScanBtn = (ImageButton) findViewById(R.id.btnScan);
		mSearchBtn = (ImageButton) findViewById(R.id.btnSearch);
		mOkBtn = (Button) findViewById(R.id.btnOK);
		mCancelBtn = (Button) findViewById(R.id.btnCancel);

		mDeviceOnCloudClient = new DeviceOnCloudClient();
		mDeviceOnCloudClient.RegistrInterFace(this);

		mTouchLayout = (RelativeLayout) findViewById(R.id.touch_layout);
		mTouchLayout.setOnClickListener(this);

		updateLayout(mAddMode);
		initialListener();

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	private void updateLayout(int mode) {

		LinearLayout actionLayout = (LinearLayout) findViewById(R.id.btnlayout);
		RelativeLayout ssidLayout = (RelativeLayout) findViewById(R.id.ssidlayout);
		switch (mode) {

		case AddDeviceActivity.ADD_DEVICE_FROM_CLOUD:

			actionLayout.setVisibility(View.INVISIBLE);
			ssidLayout.setVisibility(View.GONE);
			mAddToCloud.setVisibility(View.GONE);
			mSyncToCloud.setVisibility(View.VISIBLE);
			if (mDeviceNickName != null)
				mNameEdtTxt.setText(mDeviceNickName);
			break;

		case AddDeviceActivity.ADD_DEVICE_FROM_WIRED:

			actionLayout.setVisibility(View.VISIBLE);
			ssidLayout.setVisibility(View.GONE);
			mAddToCloud.setVisibility(View.VISIBLE);
			mSyncToCloud.setVisibility(View.GONE);
			break;

		case AddDeviceActivity.ADD_DEVICE_FROM_WIRELESS:

			actionLayout.setVisibility(View.INVISIBLE);
			ssidLayout.setVisibility(View.VISIBLE);
			mAddToCloud.setVisibility(View.VISIBLE);
			mSyncToCloud.setVisibility(View.GONE);
			if (mDeviceSSID != null)
				mDeviceSSIDTxtView.setText(mDeviceSSID);
			break;

		}
	}

	private void initialListener() {

		mScanBtn.setOnClickListener(this);
		mSearchBtn.setOnClickListener(this);
		mOkBtn.setOnClickListener(this);
		mCancelBtn.setOnClickListener(this);

		mAddToCloud.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@SuppressWarnings("static-access")
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				DatabaseManager DatabaseManager = new DatabaseManager(AddDeviceActivity.this);
				if (DatabaseManager.getLoginPassword().equals("")) {

					MultiViewActivity.showAlert(AddDeviceActivity.this, getText(R.string.tips_warning),
							getText(R.string.txt_please_login_your_cloud_account), getText(R.string.ok));
					mAddToCloud.setChecked(false);
				} else {

					if (isChecked)
						showIsSyncDialog();
				}

			}
		});

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btnScan:
			doScan();
			break;

		case R.id.btnSearch:
			doLanSearch();
			break;

		case R.id.btnOK:
			doOK();
			break;

		case R.id.btnCancel:
			doCancel();
			break;

		case R.id.touch_layout:
			hideSoftKeyBoard();
			break;

		}
	}

	private void doScan() {

		Intent intent = new Intent("com.google.zxing.client.android.SCAN");

		if (getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() == 0) {

			new AlertDialog.Builder(AddDeviceActivity.this).setIcon(android.R.drawable.ic_dialog_alert).setTitle(getText(R.string.tips_warning))
					.setMessage(getText(R.string.tips_no_barcode_scanner))
					.setPositiveButton(getText(R.string.ok), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							new Thread(new Runnable() {
								public void run() {

									if (isSDCardValid()) {
										try {
											startDownload(ZXING_DOWNLOAD_URL);
										} catch (Exception e) {
											System.out.println(e.getMessage());
										}
									} else {
										Toast.makeText(AddDeviceActivity.this, AddDeviceActivity.this.getText(R.string.tips_no_sdcard).toString(),
												Toast.LENGTH_SHORT).show();
									}
								}
							}).start();

						}
					}).setNegativeButton(getText(R.string.cancel), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					}).show();

		} else {

			intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
			startActivityForResult(intent, REQUEST_CODE_GETUID_BY_SCAN_BARCODE);
		}
	}

	private void doLanSearch() {

		AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(AddDeviceActivity.this, R.style.HoloAlertDialog));
		final AlertDialog dlg = builder.create();
		dlg.setTitle(getText(R.string.dialog_LanSearch));
		dlg.setIcon(android.R.drawable.ic_menu_more);

		LayoutInflater inflater = dlg.getLayoutInflater();
		View view = inflater.inflate(R.layout.search_device, null);
		dlg.setView(view);

		ListView lstSearchResult = (ListView) view.findViewById(R.id.lstSearchResult);
		Button btnRefresh = (Button) view.findViewById(R.id.btnRefresh);

		list.clear();
		st_LanSearchInfo[] arrResp = Camera.SearchLAN();

		if (arrResp != null && arrResp.length > 0) {
			for (st_LanSearchInfo resp : arrResp) {

				list.add(new SearchResult(new String(resp.UID).trim(), new String(resp.IP).trim(), (int) resp.port));
			}
		}

		final SearchResultListAdapter adapter = new SearchResultListAdapter(dlg.getLayoutInflater());
		lstSearchResult.setAdapter(adapter);

		lstSearchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				SearchResult r = list.get(position);
				mUIDEdtTxt.setText(r.UID);
				dlg.dismiss();
			}
		});

		btnRefresh.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				list.clear();
				st_LanSearchInfo[] arrResp = Camera.SearchLAN();

				if (arrResp != null && arrResp.length > 0) {
					for (st_LanSearchInfo resp : arrResp) {

						list.add(new SearchResult(new String(resp.UID).trim(), new String(resp.IP).trim(), (int) resp.port));
					}
				}

				adapter.notifyDataSetChanged();
			}
		});

		dlg.show();
	}

	private void doOK() {

		String dev_nickname = mNameEdtTxt.getText().toString();
		String dev_uid = mUIDEdtTxt.getText().toString().trim();
		String view_pwd = mSecurityEdtTxt.getText().toString().trim();

		if (dev_nickname.length() == 0) {
			MultiViewActivity.showAlert(AddDeviceActivity.this, getText(R.string.tips_warning), getText(R.string.tips_camera_name),
					getText(R.string.ok));
			return;
		}

		if (dev_uid.length() == 0) {
			MultiViewActivity.showAlert(AddDeviceActivity.this, getText(R.string.tips_warning), getText(R.string.tips_dev_uid), getText(R.string.ok));
			return;
		}

		if (dev_uid.length() != 20) {
			MultiViewActivity.showAlert(AddDeviceActivity.this, getText(R.string.tips_warning), getText(R.string.tips_dev_uid_character),
					getText(R.string.ok));
			return;
		}

		if (view_pwd.length() == 0) {
			MultiViewActivity.showAlert(AddDeviceActivity.this, getText(R.string.tips_warning), getText(R.string.tips_dev_security_code),
					getText(R.string.ok));
			return;
		}

		ThreadTPNS TPNSThread = new ThreadTPNS(AddDeviceActivity.this, dev_uid, 0);
		TPNSThread.start();

		int video_quality = 0;
		int channel = 0;

		boolean duplicated = false;
		for (MyCamera camera : MultiViewActivity.CameraList) {

			if (dev_uid.equalsIgnoreCase(camera.getUID())) {
				duplicated = true;
				break;
			}
		}

		if (duplicated) {
			MultiViewActivity.showAlert(AddDeviceActivity.this, getText(R.string.tips_warning), getText(R.string.tips_add_camera_duplicated),
					getText(R.string.ok));
			return;
		}

		if (mAddMode == AddDeviceActivity.ADD_DEVICE_FROM_WIRELESS) {

			InitCamActivity.isAddToCloud(mAddToCloud.isChecked());
			InitCamActivity.hasReconnect(true);
			InitCamActivity.setCandidate(mUID, mNameEdtTxt.getText().toString(), mType);
		} else {

			if (mAddToCloud.isChecked()) {
				/* add value to cloud */
				JSONObject jsonObject = new JSONObject();
				try {
					jsonObject.put("cmd", "create");
					jsonObject.put("usr", DatabaseManager.getLoginAccount());
					jsonObject.put("pwd", DatabaseManager.getLoginPassword());
					jsonObject.put("uid", mUIDEdtTxt.getText());
					jsonObject.put("name", mNameEdtTxt.getText().toString());
					jsonObject.put("type", mType);

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				mDeviceOnCloudClient.upload(DatabaseManager.Device_On_Cloud_URL, DatabaseManager.getLoginAccount(),
						DatabaseManager.getLoginPassword(), jsonObject);
			}
		}

//		if(mSyncToCloud.isChecked()) {
//			/* sync value to cloud */
//			JSONObject jsonObject = new JSONObject();
//			try 
//			{	
//				jsonObject.put("cmd","update");
//				jsonObject.put("usr",DatabaseManager.getLoginAccount());
//				jsonObject.put("pwd",DatabaseManager.getLoginPassword());
//				jsonObject.put("uid", mUID);
//				jsonObject.put("name", mNameEdtTxt.getText().toString());
//				jsonObject.put("type", mType);
//				
//			} catch (JSONException e) 
//			{
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			mDeviceOnCloudClient.upload(DatabaseManager.Device_On_Cloud_URL, DatabaseManager.getLoginAccount(), DatabaseManager.getLoginPassword(), jsonObject);
//		}

		/* add value to data base */
		DatabaseManager manager = new DatabaseManager(AddDeviceActivity.this);
		long db_id = manager.addDevice(dev_nickname, dev_uid, "", "", "admin", view_pwd, 3, channel, mSyncToCloud.isChecked());

		Toast.makeText(AddDeviceActivity.this, getText(R.string.tips_add_camera_ok).toString(), Toast.LENGTH_SHORT).show();

		/* return value to main activity */
		Bundle extras = new Bundle();
		extras.putLong("db_id", db_id);
		extras.putString("dev_nickname", dev_nickname);
		extras.putString("dev_uid", dev_uid);
		extras.putString("dev_name", "");
		extras.putString("dev_pwd", "");
		extras.putString("wifi_ssid", mWifiSSID);
		extras.putString("wifi_password", mWifiPassword);
		extras.putInt("wifi_enc", wifi_enc);
		extras.putString("view_acc", "admin");
		extras.putString("view_pwd", view_pwd);
		extras.putInt("video_quality", video_quality);
		extras.putInt("camera_channel", 0);

		Intent Intent = new Intent();
		Intent.putExtras(extras);
		AddDeviceActivity.this.setResult(RESULT_OK, Intent);
		AddDeviceActivity.this.finish();
	}

	private void doCancel() {

		Intent intent = new Intent();
		setResult(RESULT_CANCELED, intent);
		finish();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {

		if (requestCode == REQUEST_CODE_GETUID_BY_SCAN_BARCODE) {

			if (resultCode == RESULT_OK) {

				String contents = intent.getStringExtra("SCAN_RESULT");
				// String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
				if (null == contents) {
					Bundle bundle = intent.getExtras();
					if (null != bundle) {
						/* String */contents = bundle.getString("result");
					}
				}
				if (null == contents) {
					return;
				}

				if (contents.length() > 20) {
					String temp = "";

					for (int t = 0; t < contents.length(); t++) {
						if (contents.substring(t, t + 1).matches("[A-Z0-9]{1}"))
							temp += contents.substring(t, t + 1);
					}
					contents = temp;
				}

				mUIDEdtTxt.setText(contents);

				mSecurityEdtTxt.requestFocus();

			} else if (resultCode == RESULT_CANCELED) {
				// Handle cancel
			}
		}
	}

	/*@Override protected void onStart() { super.onStart(); // FlurryAgent.onStartSession(this,
	 * "Q1SDXDZQ21BQMVUVJ16W"); }
	 * @Override protected void onStop() { super.onStop(); // FlurryAgent.onEndSession(this); } */

	/* private View.OnClickListener btnScanClickListener = new View.OnClickListener() {
	 * @Override public void onClick(View v) { Intent intent = new
	 * Intent("com.google.zxing.client.android.SCAN"); if
	 * (getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size()
	 * == 0) { new
	 * AlertDialog.Builder(AddDeviceActivity.this).setIcon(android.R.drawable.ic_dialog_alert
	 * ).setTitle
	 * (getText(R.string.tips_warning)).setMessage(getText(R.string.tips_no_barcode_scanner
	 * )).setPositiveButton(getText(R.string.ok), new DialogInterface.OnClickListener() {
	 * @Override public void onClick(DialogInterface dialog, int which) { new Thread(new Runnable()
	 * { public void run() { if (isSDCardValid()) { try { startDownload(ZXING_DOWNLOAD_URL); } catch
	 * (Exception e) { System.out.println(e.getMessage()); } } else {
	 * Toast.makeText(AddDeviceActivity.this,
	 * AddDeviceActivity.this.getText(R.string.tips_no_sdcard).toString(),
	 * Toast.LENGTH_SHORT).show(); } } }).start(); } }).setNegativeButton(getText(R.string.cancel),
	 * new DialogInterface.OnClickListener() {
	 * @Override public void onClick(DialogInterface dialog, int which) { } }).show(); } else {
	 * intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); startActivityForResult(intent,
	 * REQUEST_CODE_GETUID_BY_SCAN_BARCODE); } } }; */
	/*private View.OnClickListener btnSearchOnClickListener = new View.OnClickListener() {
	 * @Override public void onClick(View v) { AlertDialog.Builder builder = new
	 * AlertDialog.Builder(new ContextThemeWrapper(AddDeviceActivity.this,
	 * R.style.HoloAlertDialog)); final AlertDialog dlg = builder.create();
	 * dlg.setTitle(getText(R.string.dialog_LanSearch));
	 * dlg.setIcon(android.R.drawable.ic_menu_more); LayoutInflater inflater =
	 * dlg.getLayoutInflater(); View view = inflater.inflate(R.layout.search_device, null);
	 * dlg.setView(view); ListView lstSearchResult = (ListView)
	 * view.findViewById(R.id.lstSearchResult); Button btnRefresh = (Button)
	 * view.findViewById(R.id.btnRefresh); list.clear(); st_LanSearchInfo[] arrResp =
	 * Camera.SearchLAN(); if (arrResp != null && arrResp.length > 0) { for (st_LanSearchInfo resp :
	 * arrResp) { list.add(new SearchResult(new String(resp.UID).trim(), new String(resp.IP).trim(),
	 * (int) resp.port)); } } final SearchResultListAdapter adapter = new
	 * SearchResultListAdapter(dlg.getLayoutInflater()); lstSearchResult.setAdapter(adapter);
	 * lstSearchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	 * @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	 * SearchResult r = list.get(position); mUIDEdtTxt.setText(r.UID); dlg.dismiss(); } });
	 * btnRefresh.setOnClickListener(new View.OnClickListener() {
	 * @Override public void onClick(View v) { list.clear(); st_LanSearchInfo[] arrResp =
	 * Camera.SearchLAN(); if (arrResp != null && arrResp.length > 0) { for (st_LanSearchInfo resp :
	 * arrResp) { list.add(new SearchResult(new String(resp.UID).trim(), new String(resp.IP).trim(),
	 * (int) resp.port)); } } adapter.notifyDataSetChanged(); } }); dlg.show(); } }; */
	/*private View.OnClickListener btnOKOnClickListener = new View.OnClickListener() {
	 * @Override public void onClick(View v) { String dev_nickname =
	 * mNameEdtTxt.getText().toString(); String dev_uid = mUIDEdtTxt.getText().toString().trim();
	 * String view_pwd = mSecurityEdtTxt.getText().toString().trim(); if (dev_nickname.length() ==
	 * 0) { MultiViewActivity.showAlert(AddDeviceActivity.this, getText(R.string.tips_warning),
	 * getText(R.string.tips_camera_name), getText(R.string.ok)); return; } if (dev_uid.length() ==
	 * 0) { MultiViewActivity.showAlert(AddDeviceActivity.this, getText(R.string.tips_warning),
	 * getText(R.string.tips_dev_uid), getText(R.string.ok)); return; } if (dev_uid.length() != 20)
	 * { MultiViewActivity.showAlert(AddDeviceActivity.this, getText(R.string.tips_warning),
	 * getText(R.string.tips_dev_uid_character), getText(R.string.ok)); return; } if
	 * (view_pwd.length() == 0) { MultiViewActivity.showAlert(AddDeviceActivity.this,
	 * getText(R.string.tips_warning), getText(R.string.tips_dev_security_code),
	 * getText(R.string.ok)); return; } ThreadTPNS TPNSThread = new
	 * ThreadTPNS(AddDeviceActivity.this,dev_uid,0); TPNSThread.start(); int video_quality = 0; int
	 * channel = 0; boolean duplicated = false; for (MyCamera camera : MultiViewActivity.CameraList)
	 * { if (dev_uid.equalsIgnoreCase(camera.getUID())) { duplicated = true; break; } } if
	 * (duplicated) { MultiViewActivity.showAlert(AddDeviceActivity.this,
	 * getText(R.string.tips_warning), getText(R.string.tips_add_camera_duplicated),
	 * getText(R.string.ok)); return; } // add value to data base DatabaseManager manager = new
	 * DatabaseManager(AddDeviceActivity.this); long db_id = manager.addDevice(dev_nickname,
	 * dev_uid, "", "", "admin", view_pwd, 3, channel); Toast.makeText(AddDeviceActivity.this,
	 * getText(R.string.tips_add_camera_ok).toString(), Toast.LENGTH_SHORT).show(); // return value
	 * to main activity Bundle extras = new Bundle(); extras.putLong("db_id", db_id);
	 * extras.putString("dev_nickname", dev_nickname); extras.putString("dev_uid", dev_uid);
	 * extras.putString("dev_name", ""); extras.putString("dev_pwd", "");
	 * extras.putString("view_acc", "admin"); extras.putString("view_pwd", view_pwd);
	 * extras.putInt("video_quality", video_quality); extras.putInt("camera_channel", 0); Intent
	 * Intent = new Intent(); Intent.putExtras(extras); AddDeviceActivity.this.setResult(RESULT_OK,
	 * Intent); AddDeviceActivity.this.finish(); } }; private View.OnClickListener
	 * btnCancelOnClickListener = new View.OnClickListener() {
	 * @Override public void onClick(View v) { Intent intent = new Intent();
	 * setResult(RESULT_CANCELED, intent); finish(); } }; */

	private boolean isSDCardValid() {

		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	private void startInstall(File tempFile) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(tempFile), "application/vnd.android.package-archive");

		startActivity(intent);
	}

	private void startDownload(String url) throws Exception {

		if (URLUtil.isNetworkUrl(url)) {

			URL myURL = new URL(url);

			HttpURLConnection conn = (HttpURLConnection) myURL.openConnection();
			// conn.connect();

			InputStream is = conn.getInputStream();

			if (is == null) {
				return;
			}

			BufferedInputStream bis = new BufferedInputStream(is);

			File myTempFile = File.createTempFile("BarcodeScanner", ".apk", Environment.getExternalStorageDirectory());
			FileOutputStream fos = new FileOutputStream(myTempFile);

			byte[] buffer = new byte[1024];
			int read = 0;
			while ((read = bis.read(buffer)) > 0) {
				fos.write(buffer, 0, read);
			}

			try {
				fos.flush();
				fos.close();
			} catch (Exception ex) {
				System.out.println("error: " + ex.getMessage());
			}

			startInstall(myTempFile);
		}
	}

	private class SearchResult {

		public String UID;
		public String IP;

		// public int Port;

		public SearchResult(String uid, String ip, int port) {

			UID = uid;
			IP = ip;
			// Port = port;
		}
	}

	private class SearchResultListAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public SearchResultListAdapter(LayoutInflater inflater) {

			this.mInflater = inflater;
		}

		public int getCount() {

			return list.size();
		}

		public Object getItem(int position) {

			return list.get(position);
		}

		public long getItemId(int position) {

			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			final SearchResult result = (SearchResult) getItem(position);
			ViewHolder holder = null;

			if (convertView == null) {

				convertView = mInflater.inflate(R.layout.search_device_result, null);

				holder = new ViewHolder();
				holder.uid = (TextView) convertView.findViewById(R.id.uid);
				holder.ip = (TextView) convertView.findViewById(R.id.ip);

				convertView.setTag(holder);

			} else {

				holder = (ViewHolder) convertView.getTag();
			}

			holder.uid.setText(result.UID);
			holder.ip.setText(result.IP);
			// holder.port.setText(result.Port);

			return convertView;
		}// getView()

		public final class ViewHolder {
			public TextView uid;
			public TextView ip;
		}
	}

	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mResultStateReceiver);
	}

	private void showIsSyncDialog() {
		AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(this);

		dlgBuilder.setMessage(R.string.txt_if_sync_to_cloud);
		dlgBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				mSyncToCloud.setChecked(false);
			}
		});
		dlgBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mSyncToCloud.setChecked(true);
			}
		}).show();
	}

	private class ResultStateReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub

		}
	}

	@Override
	public void uploadok(int Status) {
		// TODO Auto-generated method stub
		Toast.makeText(this, getText(R.string.txt_upload_cloud_success).toString(), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void downloadok(int Status) {
		// TODO Auto-generated method stub
	}

	@Override
	public void error(final int Status) {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {
			public void run() {
				if (Status == DeviceOnCloudClient.UPLOADERROR) {
					if (!isFinishing()) {
						MultiViewActivity.showAlert(AddDeviceActivity.this, getText(R.string.tips_warning), getText(R.string.txt_upload_cloud_fail),
								getText(R.string.ok));
						Toast.makeText(AddDeviceActivity.this, getText(R.string.tips_format_sdcard_failed).toString(), Toast.LENGTH_SHORT).show();
					}
				}

				else if (Status == DeviceOnCloudClient.DOWNLOADERROR) {
				}
			}

		});

	}

	@SuppressWarnings("all")
	private void hideSoftKeyBoard() {

		InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mTouchLayout.getWindowToken(), 0);
	}
}
