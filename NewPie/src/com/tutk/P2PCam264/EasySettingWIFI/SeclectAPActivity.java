package com.tutk.P2PCam264.EasySettingWIFI;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import appteam.WifiAdmin;

import com.example.newpie.R;
import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.Logger.Glog;
import com.tutk.P2PCam264.DELUX.MultiViewActivity;
import com.tutk.P2PCam264.EasySettingWIFI.UITable.BasicItem;
import com.tutk.P2PCam264.EasySettingWIFI.UITable.UITableView;
import com.tutk.P2PCam264.EasySettingWIFI.UITable.UITableView.ClickListener;
@SuppressWarnings("all")
public class SeclectAPActivity extends Activity {
	public static final String WIFIAPENC_NONE = "";
	public static final String WIFIAPENC_WPA_AES = "[WPA-PSK-CCMP]";
	public static final String WIFIAPENC_WPA2_AES = "";
	static final String TAG = "NOWAPActivity";
	static final String UNKNOWN = "";
	static final int NULLAP = 0;
	private WifiAdmin WifiAdmin;
	private RelativeLayout loadingLayout;
	private ProgressBar LodingProgressBar;
	private TextView txt_not_search_wifi;
	private String wifi_ssid = "";
	private String wifi_password = "";
	private UITableView tableView;
	private String mEncType;
	private int wifi_enc = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setCustomView(R.layout.titlebar);
		TextView tv = (TextView) this.findViewById(R.id.bar_text);
		tv.setText(getText(R.string.txtAddCamera));
		setContentView(R.layout.easy_setting_wifi_selectap);
		super.onCreate(savedInstanceState);
		Bundle bundle = this.getIntent().getExtras();
		wifi_ssid = bundle.getString("wifi_ssid");
		wifi_password = bundle.getString("wifi_password");

		setupView();

		WifiAdmin = new WifiAdmin(this);
		WifiAdmin.startScan();
		if (WifiAdmin.getWifiList().size() == NULLAP) {
			LodingProgressBar.setVisibility(View.GONE);
			txt_not_search_wifi.setVisibility(View.VISIBLE);
		} else {
			tableView.clear();
			loadingLayout.setVisibility(View.GONE);
			for (ScanResult ScanResult : WifiAdmin.getWifiList()) {
				Glog.D(TAG, "" + ScanResult.SSID);
				BasicItem Item = new BasicItem(ScanResult.SSID);
				tableView.addBasicItem(Item);
				if (wifi_ssid.equals(ScanResult.SSID)) {
					mEncType = ScanResult.capabilities;
					if (!wifi_password.equals("")) {
						if (mEncType.contains("2")) {
							wifi_enc = AVIOCTRLDEFs.AVIOTC_WIFIAPENC_WPA2_AES;
						} else {
							wifi_enc = AVIOCTRLDEFs.AVIOTC_WIFIAPENC_WPA_AES;
						}
					}
				}
			}
			tableView.commit();
		}

	}

	private void setupView() {

		loadingLayout = (RelativeLayout) findViewById(R.id.loadingLayout);
		tableView = (UITableView) findViewById(R.id.tableView);
		CustomClickListener listener = new CustomClickListener();
		tableView.setClickListener(listener);
		txt_not_search_wifi = (TextView) findViewById(R.id.txt_not_search_wifi);
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

	/*@Override public void onScanComplete(List<String> ssidList) { // TODO Auto-generated method
	 * stub } */

	private class CustomClickListener implements ClickListener {

		@Override
		public void onClick(int index) {
			Intent intent = new Intent();
			Bundle extras = new Bundle();
			extras.putString("wifi_ssid", wifi_ssid);
			extras.putString("wifi_password", wifi_password);
			extras.putString("select_device_ssid", WifiAdmin.getWifiListsSSID()[index].toString());
			extras.putInt("wifi_enc", wifi_enc);
			intent.putExtras(extras);
			intent.setClass(SeclectAPActivity.this, SeclectAP_PasswordActivity.class);
			startActivityForResult(intent, MultiViewActivity.REQUEST_CODE_CAMERA_ADD);
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

}
