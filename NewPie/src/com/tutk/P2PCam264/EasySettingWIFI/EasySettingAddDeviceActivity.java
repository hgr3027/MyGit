package com.tutk.P2PCam264.EasySettingWIFI;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newpie.R;
import com.tutk.P2PCam264.DatabaseManager;
import com.tutk.P2PCam264.MyCamera;
import com.tutk.P2PCam264.ThreadTPNS;
import com.tutk.P2PCam264.DELUX.MultiViewActivity;
import com.tutk.P2PCam264.DeviceOnCloud.DeviceOnCloudClient;
import com.tutk.P2PCam264.DeviceOnCloud.DeviceOnCloudClientInterface;
@SuppressWarnings("all")
public class EasySettingAddDeviceActivity extends Activity implements DeviceOnCloudClientInterface {

	private final String TAG = "EasySettingAddDeviceActivity";

	private final int REQUEST_CODE_GETUID_BY_SCAN_BARCODE = 0;

	private Activity mActivity;
	private String wifi_ssid;
	private String wifi_password;

	private TextView txt_ssid;
	private EditText mUIDEdtTxt;
	private EditText edtSecurityCode;
	private EditText edtName;
	private CheckBox SyncCheck;
	private Button btnOK;
	private Button btnCancel;
	private ResultStateReceiver resultStateReceiver;
	private IntentFilter filter;
	private ThreadTPNS thread;
	private RelativeLayout mTouchLayout;

	private List<SearchResult> list = new ArrayList<SearchResult>();

//	private boolean mIsAddToCloud = false;
//	private boolean mIsSyncToCloud = false;

	private DeviceOnCloudClient mDeviceOnCloudClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mActivity = this;
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setCustomView(R.layout.titlebar);
		TextView tv = (TextView) this.findViewById(R.id.bar_text);
		tv.setText(getText(R.string.dialog_AddCamera));
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_device_easy_wifi_setting);

		filter = new IntentFilter();
		filter.addAction(EasySettingAddDeviceActivity.class.getName());
		resultStateReceiver = new ResultStateReceiver();

		registerReceiver(resultStateReceiver, filter);

		txt_ssid = (TextView) findViewById(R.id.txt_ssid);
		mUIDEdtTxt = (EditText) findViewById(R.id.edtUID);
		edtSecurityCode = (EditText) findViewById(R.id.edtSecurityCode);
		edtName = (EditText) findViewById(R.id.edtNickName);

		SyncCheck = (CheckBox) findViewById(R.id.Synccheck);

		mTouchLayout = (RelativeLayout) findViewById(R.id.touch_layout);
		mTouchLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				InputMethodManager imm = (InputMethodManager) getSystemService(mActivity.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mTouchLayout.getWindowToken(), 0);
			}
		});

		btnOK = (Button) findViewById(R.id.btnOK);
		btnOK.setOnClickListener(btnOKOnClickListener);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(btnCancelOnClickListener);

		Bundle bundle = this.getIntent().getExtras();
		if (bundle != null) {
			if (bundle.getString("select_device_uid") != null)
				mUIDEdtTxt.setText(bundle.getString("select_device_uid"));

			wifi_ssid = bundle.getString("wifi_ssid");
			wifi_password = bundle.getString("wifi_password");
			String select_device_ssid = bundle.getString("select_device_ssid");
			String select_device_password = bundle.getString("select_device_password");
			txt_ssid.setText(select_device_ssid);
		}

		mDeviceOnCloudClient = new DeviceOnCloudClient();
		mDeviceOnCloudClient.RegistrInterFace(this);

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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

				edtSecurityCode.requestFocus();

			} else if (resultCode == RESULT_CANCELED) {
				// Handle cancel
			}
		}
	}

	/*@Override protected void onStart() { super.onStart(); // FlurryAgent.onStartSession(this,
	 * "Q1SDXDZQ21BQMVUVJ16W"); }
	 * @Override protected void onStop() { super.onStop(); // FlurryAgent.onEndSession(this); } */

	private View.OnClickListener btnOKOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {

			String dev_nickname = edtName.getText().toString();
			String dev_uid = mUIDEdtTxt.getText().toString().trim();
			String view_pwd = edtSecurityCode.getText().toString().trim();
			if (dev_nickname.length() == 0) {
				MultiViewActivity.showAlert(EasySettingAddDeviceActivity.this, getText(R.string.tips_warning), getText(R.string.tips_camera_name),
						getText(R.string.ok));
				return;
			}

			if (dev_uid.length() == 0) {
				MultiViewActivity.showAlert(EasySettingAddDeviceActivity.this, getText(R.string.tips_warning), getText(R.string.tips_dev_uid),
						getText(R.string.ok));
				return;
			}

			if (dev_uid.length() != 20) {
				MultiViewActivity.showAlert(EasySettingAddDeviceActivity.this, getText(R.string.tips_warning),
						getText(R.string.tips_dev_uid_character), getText(R.string.ok));
				return;
			}

			if (view_pwd.length() == 0) {
				MultiViewActivity.showAlert(EasySettingAddDeviceActivity.this, getText(R.string.tips_warning),
						getText(R.string.tips_dev_security_code), getText(R.string.ok));
				return;
			}

			thread = new ThreadTPNS(EasySettingAddDeviceActivity.this, dev_uid, 0);
			thread.start();

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
				MultiViewActivity.showAlert(EasySettingAddDeviceActivity.this, getText(R.string.tips_warning),
						getText(R.string.tips_add_camera_duplicated), getText(R.string.ok));
				return;
			}

//			if(mIsAddToCloud) {
//				Glog.I(TAG, "[wula] AddToCloud");
//				/* add value to cloud */	
//				
//				
//				JSONObject jsonObject = new JSONObject();
//				try 
//				{	
//					jsonObject.put("cmd","create");
//					jsonObject.put("usr",DatabaseManager.getLoginAccount());
//					jsonObject.put("pwd",DatabaseManager.getLoginPassword());
//					jsonObject.put("uid",mUIDEdtTxt.getText());
//					
//				} catch (JSONException e) 
//				{
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				
//				Glog.I(TAG,"[wula] check database acc: "+DatabaseManager.getLoginAccount());
//				Glog.I(TAG,"[wula] check database pwd: "+DatabaseManager.getLoginPassword());
//				Glog.I(TAG,"[wula] check database mUID: "+mUIDEdtTxt.getText());
//				mDeviceOnCloudClient.upload(DatabaseManager.Device_On_Cloud_URL, DatabaseManager.getLoginAccount(), DatabaseManager.getLoginPassword(), jsonObject);
//			}

			/* add value to data base */
			DatabaseManager manager = new DatabaseManager(EasySettingAddDeviceActivity.this);
//			int Check = 0;
//			if(SyncCheck.isChecked())
//			{
//				Check = 1;
//			}
//			else
//			{
//				Check = 0;
//			}
			long db_id = manager.addDevice(dev_nickname, dev_uid, "", "", "admin", view_pwd, 3, channel, SyncCheck.isChecked());

			Toast.makeText(EasySettingAddDeviceActivity.this, getText(R.string.tips_add_camera_ok).toString(), Toast.LENGTH_SHORT).show();

			/* return value to main activity */
			Bundle extras = new Bundle();
			extras.putLong("db_id", db_id);
			extras.putString("dev_nickname", dev_nickname);
			extras.putString("dev_uid", dev_uid);
			extras.putString("dev_name", "");
			extras.putString("dev_pwd", "");
			extras.putString("view_acc", "admin");
			extras.putString("view_pwd", view_pwd);
			extras.putInt("video_quality", video_quality);
			extras.putInt("camera_channel", 0);

			extras.putString("wifi_ssid", wifi_ssid);
			extras.putString("wifi_password", wifi_password);

			Intent Intent = new Intent();
			Intent.putExtras(extras);
			EasySettingAddDeviceActivity.this.setResult(RESULT_OK, Intent);
			EasySettingAddDeviceActivity.this.finish();
		}
	};

	private View.OnClickListener btnCancelOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {

			Intent intent = new Intent();
			setResult(RESULT_CANCELED, intent);
			finish();
		}
	};

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
		unregisterReceiver(resultStateReceiver);
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

	}

	@Override
	public void downloadok(int Status) {
		// TODO Auto-generated method stub

	}

	@Override
	public void error(int Status) {
		// TODO Auto-generated method stub

	}
}
