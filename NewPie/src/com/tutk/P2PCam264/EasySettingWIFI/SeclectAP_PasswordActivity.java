package com.tutk.P2PCam264.EasySettingWIFI;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import appteam.WifiAdmin;

import com.example.newpie.R;
import com.tutk.IOTC.Camera;
import com.tutk.IOTC.st_LanSearchInfo;
import com.tutk.P2PCam264.AddDeviceActivity;
import com.tutk.P2PCam264.DELUX.MultiViewActivity;
@SuppressWarnings("all")
public class SeclectAP_PasswordActivity extends Activity implements View.OnClickListener {
	static final String TAG = "NOWAPActivity";
	static final String UNKNOWN = "";
	static final int NULLAP = 0;
	static final int NOT_CONNECT = 0;
	static final int NOT_SEARCHUID = 1;
	static final int SEARCHUIDOK = 2;
	static final int MAX_LAN_SEARCH_COUNT = 4;
	private int wifi_enc = -1;
	private WifiAdmin WifiAdmin;
	private RelativeLayout loadingLayout;
	private RelativeLayout mTouchLayout;
	private ProgressBar LodingProgressBar;
	private TextView txt_not_connect_wifi;
	private TextView WIFI_txt;
	private ImageButton btn_add;
	private EditText password_edit;
	private String wifi_ssid = "";
	private String wifi_password = "";
	private String select_device_ssid = "";
	private String SecarchUID = "";
	private boolean not_commect = true;
	private ConnectionChangeReceiver mNetworkStateReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setCustomView(R.layout.titlebar);
		TextView tv = (TextView) this.findViewById(R.id.bar_text);
		tv.setText(getText(R.string.txtAddCamera));
		setContentView(R.layout.easy_setting_wifi_selectap_password);
		super.onCreate(savedInstanceState);
		Bundle bundle = this.getIntent().getExtras();
		wifi_ssid = bundle.getString("wifi_ssid");
		wifi_password = bundle.getString("wifi_password");
		select_device_ssid = bundle.getString("select_device_ssid");
		wifi_enc = bundle.getInt("wifi_enc");

		setupView();

		WifiAdmin = new WifiAdmin(this);

	}

	private void setupView() {
		WIFI_txt = (TextView) findViewById(R.id.WIFI_txt);
		WIFI_txt.setText(select_device_ssid);
		loadingLayout = (RelativeLayout) findViewById(R.id.loadingLayout);
		LodingProgressBar = (ProgressBar) findViewById(R.id.LodingProgressBar);
		txt_not_connect_wifi = (TextView) findViewById(R.id.txt_not_connect_wifi);
		btn_add = (ImageButton) findViewById(R.id.btn_add);
		btn_add.setOnClickListener(this);
		password_edit = (EditText) findViewById(R.id.password_edit);
		mTouchLayout = (RelativeLayout) findViewById(R.id.touch_layout);
		mTouchLayout.setOnClickListener(this);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_add:
			loadingLayout.setVisibility(View.VISIBLE);
			LodingProgressBar.setVisibility(View.VISIBLE);
			ChangeWiFi();
			break;

		case R.id.touch_layout:
			hideSoftKeyBoard();
			break;
		}

	}

	private void ChangeWiFi() {
		WifiConfiguration WifiConfiguration = null;
		if (password_edit.getText().toString().equals("")) {
			WifiConfiguration = WifiAdmin.CreateWifiInfo(select_device_ssid, password_edit.getText().toString(), 1);
		} else {
			WifiConfiguration = WifiAdmin.CreateWifiInfo(select_device_ssid, password_edit.getText().toString(), 3);
		}
		if (WifiConfiguration != null) {
			WifiAdmin.openWifi();
			if (!WifiAdmin.addNetwork(WifiConfiguration)) {
				LodingProgressBar.setVisibility(View.GONE);
				txt_not_connect_wifi.setVisibility(View.VISIBLE);
			} else {
				mNetworkStateReceiver = new ConnectionChangeReceiver();
				IntentFilter filter = new IntentFilter();
				filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
				registerReceiver(mNetworkStateReceiver, filter);
				Threadwaitwifi Threadwaitwifi = new Threadwaitwifi();
				Threadwaitwifi.start();
			}
		} else {
			LodingProgressBar.setVisibility(View.GONE);
			txt_not_connect_wifi.setVisibility(View.VISIBLE);
		}
	}

	private void Connect_UID() {
		try {
			unregisterReceiver(mNetworkStateReceiver);
		} catch (IllegalArgumentException e) {

		}
		ThreadsearchUID ThreadsearchUID = new ThreadsearchUID();
		ThreadsearchUID.start();

	}

	private class ThreadsearchUID extends Thread {

		static final int SLEEP_TIME = 2000;

		@Override
		public void run() {
			super.run();
			SecarchUID = "";
			for (int count = 0; count < MAX_LAN_SEARCH_COUNT; count++) {

				st_LanSearchInfo[] arrResp = Camera.SearchLAN();
				if (arrResp != null && arrResp.length > 0) {

					for (st_LanSearchInfo resp : arrResp) {
						SecarchUID = new String(resp.UID).trim();
						if (SecarchUID != null && SecarchUID.equals("") != true) {
							not_commect = false;
							Message msg = handler.obtainMessage();
							msg.what = SEARCHUIDOK;
							handler.sendMessage(msg);
							return;
						}
					}

				}
				try {
					Thread.sleep(SLEEP_TIME);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			Message msg = handler.obtainMessage();
			msg.what = NOT_SEARCHUID;
			handler.sendMessage(msg);

		}
	}

	private class Threadwaitwifi extends Thread {

		static final int MAX_SLEEP_TIME = 25000;

		@Override
		public void run() {
			super.run();
			try {
				Thread.sleep(MAX_SLEEP_TIME);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (not_commect) {
				Message msg = handler.obtainMessage();
				msg.what = NOT_CONNECT;
				handler.sendMessage(msg);
			}

		}
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case NOT_CONNECT:
				LodingProgressBar.setVisibility(View.GONE);
				txt_not_connect_wifi.setVisibility(View.VISIBLE);
				try {
					unregisterReceiver(mNetworkStateReceiver);
				} catch (IllegalArgumentException e) {

				}
				break;
			case NOT_SEARCHUID:
				LodingProgressBar.setVisibility(View.GONE);
				txt_not_connect_wifi.setVisibility(View.VISIBLE);
				break;
			case SEARCHUIDOK:

				Intent intent = new Intent();
				Bundle extras = new Bundle();
				extras.putInt("addMode", AddDeviceActivity.ADD_DEVICE_FROM_WIRELESS);
				extras.putString("wifi_ssid", wifi_ssid);
				extras.putString("wifi_password", wifi_password);
				extras.putInt("wifi_enc", wifi_enc);
				extras.putString("select_device_ssid", select_device_ssid);
				extras.putString("select_device_password", password_edit.getText().toString());
				extras.putString("uid", SecarchUID);
				intent.putExtras(extras);
//					intent.setClass(SeclectAP_PasswordActivity.this, EasySettingAddDeviceActivity.class);
				intent.setClass(SeclectAP_PasswordActivity.this, AddDeviceActivity.class);
				startActivityForResult(intent, MultiViewActivity.REQUEST_CODE_CAMERA_ADD);
				loadingLayout.setVisibility(View.GONE);
				break;
			}
		}
	};

	public class ConnectionChangeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (WifiAdmin.isWifi()) {
				if (WifiAdmin.getSSID().equals("NULL") != true) {
					if (WifiAdmin.isCommect()) {
						Connect_UID();
					}
				}
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == MultiViewActivity.REQUEST_CODE_CAMERA_ADD) {
			switch (resultCode) {
			case RESULT_OK:
				Bundle extras = data.getExtras();
				Intent Intent = new Intent();
				Intent.putExtras(extras);
				setResult(RESULT_OK, Intent);
				finish();
				break;
			case RESULT_CANCELED:
				// Intent Intent2 = new Intent();
				// setResult(RESULT_CANCELED, Intent2);
				// finish();
				break;
			}
		}
	}

	private void hideSoftKeyBoard() {

		InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mTouchLayout.getWindowToken(), 0);
	}

}
