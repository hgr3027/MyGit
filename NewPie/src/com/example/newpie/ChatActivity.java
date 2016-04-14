package com.example.newpie;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.opengl.Visibility;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.newpie.bean.ActivityManager;
import com.example.newpie.utils.ChatButton;

public class ChatActivity extends Activity implements OnClickListener,
		OnTouchListener {

	private View chatTitle;
	private View chatMain;
	private View chatMenu;
	private ImageView iv1;
	private TextView tv1;
	private TextView tv2;
	private TextView tv3;
	private ImageView jpImageView;
	private ImageView bqImageView;
	private ImageView fjImageView;
	private EditText chatText;
	private ChatButton chatButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		init();
		ActivityManager exitM = ActivityManager.getInstance();
		exitM.addActivity(ChatActivity.this);
	}

	private void init() {

		chatTitle = findViewById(R.id.chattitle);
		chatMain = findViewById(R.id.chatmain);
		chatMenu = findViewById(R.id.chatmenu);
		iv1 = (ImageView) chatTitle.findViewById(R.id.bakimageview);
		tv1 = (TextView) chatTitle.findViewById(R.id.baktextview);
		tv2 = (TextView) chatTitle.findViewById(R.id.titletext);
		tv3 = (TextView) chatTitle.findViewById(R.id.functext);
		jpImageView = (ImageView) chatMain.findViewById(R.id.chatspeechimage);
		bqImageView = (ImageView) chatMain.findViewById(R.id.chatbqimage);
		fjImageView = (ImageView) chatMain.findViewById(R.id.chatfjimage);
		chatText = (EditText) chatMain.findViewById(R.id.chattext);
		chatButton = (ChatButton) chatMain.findViewById(R.id.chatbuton);
		tv1.setText(getResources().getString(R.string.chatlefttext));
		tv2.setText("OK");
		tv3.setText(getResources().getString(R.string.chatrighttext));
		iv1.setOnClickListener(this);
		tv1.setOnClickListener(this);
		jpImageView.setOnTouchListener(this);
		bqImageView.setOnTouchListener(this);
		fjImageView.setOnTouchListener(this);
		
	}



	

	@Override
	public void onClick(View arg0) {

		switch (arg0.getId()) {
		case R.id.baktextview: {
			onBackPressed();
			break;
		}
		case R.id.bakimageview: {
			onBackPressed();
			break;
		}
		}
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		setResult(0, intent);
		finish();
		this.overridePendingTransition(R.anim.in, R.anim.out);
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		switch (arg0.getId()) {
		case (R.id.chatspeechimage): {
			switch (arg1.getAction()) {
			case (MotionEvent.ACTION_DOWN): {
				if (jpImageView.getDrawable().getCurrent().getConstantState() == getResources()
						.getDrawable(R.drawable.jp).getConstantState()) {
					jpImageView.setImageDrawable(getResources().getDrawable(
							R.drawable.jp2));
				} else if (jpImageView.getDrawable().getCurrent()
						.getConstantState() == getResources().getDrawable(
						R.drawable.yy).getConstantState()) {
					jpImageView.setImageDrawable(getResources().getDrawable(
							R.drawable.yy2));
				}

				break;
			}
			case (MotionEvent.ACTION_UP): {
				if (jpImageView.getDrawable().getCurrent().getConstantState() == getResources()
						.getDrawable(R.drawable.jp2).getConstantState()) {
					jpImageView.setImageDrawable(getResources().getDrawable(
							R.drawable.yy));
					chatText.setVisibility(View.GONE);
					chatButton.setVisibility(View.VISIBLE);

				} else if (jpImageView.getDrawable().getCurrent()
						.getConstantState() == getResources().getDrawable(
						R.drawable.yy2).getConstantState()) {
					jpImageView.setImageDrawable(getResources().getDrawable(
							R.drawable.jp));
					chatText.setVisibility(View.VISIBLE);
					chatButton.setVisibility(View.GONE);
				}
				break;
			}
			}
			break;
		}

		case (R.id.chatbqimage): {
			switch (arg1.getAction()) {
			case (MotionEvent.ACTION_DOWN): {
				bqImageView.setImageDrawable(getResources().getDrawable(
						R.drawable.bq2));
				break;
			}
			case (MotionEvent.ACTION_UP): {
				bqImageView.setImageDrawable(getResources().getDrawable(
						R.drawable.bq));
				break;
			}
			}
			break;
		}

		case (R.id.chatfjimage): {
			switch (arg1.getAction()) {
			case (MotionEvent.ACTION_DOWN): {
				fjImageView.setImageDrawable(getResources().getDrawable(
						R.drawable.fj2));
				break;
			}
			case (MotionEvent.ACTION_UP): {
				fjImageView.setImageDrawable(getResources().getDrawable(
						R.drawable.fj));
				break;
			}
			}
			break;
		}

		}

		return true;
	}

}
