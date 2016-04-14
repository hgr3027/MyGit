package com.tutk.P2PCam264.EasySettingWIFI;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import appteam.WifiAdmin;

import com.example.newpie.R;
import com.tutk.P2PCam264.DELUX.InitCamActivity;
import com.tutk.P2PCam264.DELUX.MultiViewActivity;
@SuppressWarnings("all")
public class NOWAPActivity extends Activity implements View.OnClickListener {
	static final String TAG = "NOWAPActivity";
	static final String UNKNOWN = "";
	private WifiAdmin WifiAdmin;
	private RelativeLayout loadingLayout;
	private TextView WIFI_txt;
	private EditText password_edit;
	private ImageButton Next_Button;
	private RelativeLayout mTouchLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setCustomView(R.layout.titlebar);
		TextView tv = (TextView) this.findViewById(R.id.bar_text);
		tv.setText(getText(R.string.txtAddCamera));
		setContentView(R.layout.easy_setting_wifi_nowap);
		super.onCreate(savedInstanceState);
		setupView();
		WifiAdmin = new WifiAdmin(this);
		if (CheckUseWifi()) {
			getWifiInfo();
		}
	}

	private void setupView() {
		WIFI_txt = (TextView) findViewById(R.id.WIFI_txt);
		password_edit = (EditText) findViewById(R.id.password_edit);
		loadingLayout = (RelativeLayout) findViewById(R.id.loadingLayout);
		Next_Button = (ImageButton) findViewById(R.id.Next_Button);
		Next_Button.setOnClickListener(this);
		mTouchLayout = (RelativeLayout) findViewById(R.id.touch_layout);
		mTouchLayout.setOnClickListener(this);

	}

	/**
	 * check if using wifi
	 */
	private boolean CheckUseWifi() {
		if (WifiAdmin.isWifi()) {
			loadingLayout.setVisibility(View.GONE);
			return true;
		} else {
			loadingLayout.setVisibility(View.VISIBLE);
			return false;
		}
	}

	/**
	 * get wifi SSID
	 */
	private void getWifiInfo() {
		String ssid = UNKNOWN;

		ssid = WifiAdmin.getSSID().toString().replace("\"", "");
		WIFI_txt.setText(ssid);
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
		case R.id.Next_Button:
			Intent intent = new Intent();
			Bundle extras = new Bundle();
			extras.putString("wifi_ssid", WIFI_txt.getText().toString());
			extras.putString("wifi_password", password_edit.getText().toString());
			intent.putExtras(extras);
			intent.setClass(NOWAPActivity.this, SeclectAPActivity.class);
			startActivityForResult(intent, MultiViewActivity.REQUEST_CODE_CAMERA_ADD);
			InitCamActivity.setCurrWifi(WIFI_txt.getText().toString(), password_edit.getText().toString());
			break;

		case R.id.touch_layout:
			hideSoftKeyBoard();
			break;
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
