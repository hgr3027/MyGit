package com.tutk.P2PCam264;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.example.newpie.R;

public class SwitchCodecActivity extends SherlockActivity{

	private Switch mSoftCodecSwt;
	private Activity mActivity;
	
	@SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); 
		actionBar.setCustomView(R.layout.titlebar);
		TextView tv = (TextView)this.findViewById(R.id.bar_text);
		tv.setText(getText(R.string.switch_codec_config));
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.switch_codec);
		
		mActivity = this;
		mSoftCodecSwt = (Switch) findViewById(R.id.soft_codec_switch);
		
		mSoftCodecSwt.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				SharedPreferences settings = mActivity.getSharedPreferences("CodecConfig", 0);
				settings.edit().putBoolean("isSoftCodec", isChecked).commit();
				
			}
		});
		
		initCodecSwitch();
	}


	@SuppressLint("NewApi") private void initCodecSwitch() {
		SharedPreferences settings = this.getSharedPreferences("CodecConfig", 0);
		boolean isSoftCodec = settings.getBoolean("isSoftCodec", false);
		
		mSoftCodecSwt.setChecked(isSoftCodec);
	}

}
