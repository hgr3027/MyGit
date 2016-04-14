package com.tutk.P2PCam264.DeviceOnCloud;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.newpie.R;
import com.tutk.P2PCam264.DatabaseManager;
import com.tutk.P2PCam264.DELUX.MultiViewActivity;
@SuppressWarnings("all")
public class LoginActivity extends Activity implements DeviceOnCloudClientInterface, View.OnClickListener {
	private static final String SignURL = "https://p2pcamweb.tutk.com/DeviceCloud/signup.php";
	private static final String ForgotURL = "https://p2pcamweb.tutk.com/DeviceCloud/forgetPwd.php";
	private Button Sign_up_button;
	private Button forgot_password_button;
	private Button login_button;
	private Button Skip_button;
	private EditText email_edit;
	private EditText password_edit;
	private RelativeLayout mTouchLayout;
	private DeviceOnCloudClient DeviceOnCloudClient;
	private android.app.ProgressDialog ProgressDialog;
	private SharedPreferences settings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ActionBar actionBar = getActionBar();
		actionBar.hide();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		setupView();
		DeviceOnCloudClient = new DeviceOnCloudClient();
		DeviceOnCloudClient.RegistrInterFace(this);
		settings = this.getSharedPreferences("Login", 0);
		settings.edit().putBoolean("IsLogin", true).commit();
	}

	private void setupView() {
		Sign_up_button = (Button) findViewById(R.id.Sign_up_button);
		Sign_up_button.setOnClickListener(this);
		forgot_password_button = (Button) findViewById(R.id.forgot_password_button);
		forgot_password_button.setOnClickListener(this);
		login_button = (Button) findViewById(R.id.login_button);
		login_button.setOnClickListener(this);
		Skip_button = (Button) findViewById(R.id.Skip_button);
		Skip_button.setOnClickListener(this);
		email_edit = (EditText) findViewById(R.id.email_edit);
		password_edit = (EditText) findViewById(R.id.password_edit);
		DatabaseManager DatabaseManager = new DatabaseManager(LoginActivity.this);
		email_edit.setText(DatabaseManager.getLoginAccount());
		password_edit.setText(DatabaseManager.getLoginPassword());
		mTouchLayout = (RelativeLayout) findViewById(R.id.touch_layout);
		mTouchLayout.setOnClickListener(this);

	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {

	}

	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.Sign_up_button:
			Intent ie = new Intent(Intent.ACTION_VIEW, Uri.parse(SignURL));
			startActivity(ie);
			break;
		case R.id.forgot_password_button:
			Intent ie2 = new Intent(Intent.ACTION_VIEW, Uri.parse(ForgotURL));
			startActivity(ie2);
			break;
		case R.id.login_button:
			loginclick();
			break;
		case R.id.Skip_button:
			settings.edit().putBoolean("IsLogin", false).commit();
			finish();
			break;
		case R.id.touch_layout:
			hideSoftKeyBoard();
			break;

		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {

		case KeyEvent.KEYCODE_BACK:

			setResult(MultiViewActivity.REQUEST_CODE_LOGIN_QUIT);
			finish();

			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void loginclick() {
		ProgressDialog = new android.app.ProgressDialog(this);
		ProgressDialog.setMessage(getText(R.string.txtLoadEventList));
		ProgressDialog.show();
		JSONObject JSONObject = new JSONObject();
		try {
			JSONObject.put("cmd", "readall");
			JSONObject.put("usr", email_edit.getText().toString());
			JSONObject.put("pwd", password_edit.getText().toString());

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DeviceOnCloudClient.download("http://p2pcamweb.tutk.com/DeviceCloud/api.php", JSONObject);
	}

	@Override
	public void uploadok(int Status) {
		// TODO Auto-generated method stub
		ProgressDialog.dismiss();
	}

	@Override
	public void downloadok(int Status) {
		// TODO Auto-generated method stub
		ProgressDialog.dismiss();
		DatabaseManager DatabaseManager = new DatabaseManager(LoginActivity.this);
		DatabaseManager.Login(email_edit.getText().toString(), password_edit.getText().toString());
		setResult(RESULT_OK);
		finish();
	}

	@Override
	public void error(int Status) {
		// TODO Auto-generated method stub
		ProgressDialog.dismiss();
		if (Status == DeviceOnCloudClient.ERROR_NOT_ACTIVE)
			Toast.makeText(this, getText(R.string.txt_active_error).toString(), Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(this, getText(R.string.txt_login_error).toString(), Toast.LENGTH_SHORT).show();
	}

	private void hideSoftKeyBoard() {

		InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mTouchLayout.getWindowToken(), 0);
	}

}
