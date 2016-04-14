package com.tutk.P2PCam264.EasySettingWIFI;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.newpie.R;
import com.tutk.P2PCam264.AddDeviceActivity;
import com.tutk.P2PCam264.DELUX.MultiViewActivity;

public class SelectCableActivity extends Activity implements  View.OnClickListener 
{

	private Button Wired_button;
	private Button Wireless_button;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); 
		actionBar.setCustomView(R.layout.titlebar);
		TextView tv = (TextView)this.findViewById(R.id.bar_text);
		tv.setText(getText(R.string.txtAddCamera));
		setContentView(R.layout.easy_setting_wifi_selectcable);
		super.onCreate(savedInstanceState);
		setupView();
	}

	
	private void setupView()
	{
		Wired_button = (Button)findViewById(R.id.Wired_button);
		Wired_button.setOnClickListener(this);
		Wireless_button = (Button)findViewById(R.id.Wireless_button);
		Wireless_button.setOnClickListener(this);
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
		Intent intent = new Intent();
		switch(v.getId())
		{
			case R.id.Wired_button:
				Bundle extras = new Bundle();
				extras.putInt("addMode", AddDeviceActivity.ADD_DEVICE_FROM_WIRED);
				intent.putExtras(extras);
				intent.setClass(SelectCableActivity.this, AddDeviceActivity.class);
				startActivityForResult(intent, MultiViewActivity.REQUEST_CODE_CAMERA_ADD);
			break;
			case R.id.Wireless_button:
				intent.setClass(SelectCableActivity.this, NOWAPActivity.class);
				startActivityForResult(intent, MultiViewActivity.REQUEST_CODE_CAMERA_ADD);
				break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == MultiViewActivity.REQUEST_CODE_CAMERA_ADD)
		{
			switch (resultCode) 
			{
				case RESULT_OK:
					Bundle extras = data.getExtras();
					Intent Intent = new Intent();
					Intent.putExtras(extras);
					setResult(RESULT_OK, Intent);
					finish();
				break;
				case RESULT_CANCELED:
					//Intent Intent2 = new Intent();
					//setResult(RESULT_CANCELED, Intent2);
					//finish();
					break;
			}
		}
	}

}
