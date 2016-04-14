package com.example.newpie;



import com.example.newpie.bean.ActivityManager;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

public class PopQRCodeActivity extends Activity {

	private ImageView im;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_popqrcode);
		im = (ImageView) findViewById(R.id.qrcodeimage);
		Intent intent = getIntent();
		if (intent != null) {

			im.setImageBitmap((Bitmap) intent.getParcelableExtra("bitmap"));
		}
		ActivityManager exitM = ActivityManager.getInstance();
		exitM.addActivity(PopQRCodeActivity.this);
	}



	

	@Override
	public void onBackPressed() {
		finish();
	}

}
