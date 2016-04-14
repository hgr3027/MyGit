package com.tutk.P2PCam264.DeviceOnCloud;


import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.example.newpie.R;
import com.tutk.IOTC.Camera;
import com.tutk.IOTC.IRegisterIOTCListener;
import com.tutk.P2PCam264.AddDeviceActivity;
import com.tutk.P2PCam264.DatabaseManager;
import com.tutk.P2PCam264.DeviceInfo;
import com.tutk.P2PCam264.MyCamera;
import com.tutk.P2PCam264.DELUX.MultiViewActivity;
@SuppressWarnings("all")
public class CloudDeviceListActivity extends SherlockActivity implements DeviceOnCloudClientInterface, IRegisterIOTCListener, View.OnClickListener {

	private String TAG = "CloudDeviceListActivity";
	
	private DeviceListAdapter adapter;
	
	private static final int REQUEST_CODE_CLOUD_CAMERA_ADD = 0;
	
	public static int nShowMessageCount = 0;
	
	private RelativeLayout mButtonLayout;
	
	private int mMonitorIndex=-1;
	private String OriginallyUID;
	private int OriginallyChannelIndex;
	
	private Button btnOK;
	private Button btnCancel;
	private TextView mNullDeviceTxt;
	
	
	private android.app.ProgressDialog ProgressDialog;

	private DeviceOnCloudClient mDeviceOnCloudClient;
	private ListView mCloudDeviceList;
	private ArrayList<OnCloudDeviceInfo> MyOnCloudDeviceInfo = new ArrayList<OnCloudDeviceInfo>();
	private int mSelectedDeviceIndex = 0;
	private List<Boolean> device_click_list=null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); 
		actionBar.setCustomView(R.layout.titlebar);
		TextView tv = (TextView)this.findViewById(R.id.bar_text);
		tv.setText(getText(R.string.app_name));
		
		Bundle bundle = this.getIntent().getExtras();
		if(bundle!=null)
		{
			mMonitorIndex = bundle.getInt("MonitorIndex");
			OriginallyUID = bundle.getString("OriginallyUID");
			OriginallyChannelIndex = bundle.getInt("OriginallyChannelIndex");
			
		}
		setupView();
		initDownLoad();
		
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		quit();
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {

			quit() ; 			
			return false ;
			
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		Configuration cfg = getResources().getConfiguration();

		if (cfg.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			System.out.println("ORIENTATION_LANDSCAPE");

		} else if (cfg.orientation == Configuration.ORIENTATION_PORTRAIT) {
			System.out.println("ORIENTATION_PORTRAIT");
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_CODE_CLOUD_CAMERA_ADD) {

			switch (resultCode) {
			case RESULT_OK:

				Bundle extras = data.getExtras();
				long db_id = extras.getLong("db_id");
				String dev_nickname = extras.getString("dev_nickname");
				String dev_uid = extras.getString("dev_uid");
				String view_acc = extras.getString("view_acc");
				String view_pwd = extras.getString("view_pwd");
				int event_notification = 3;
				int channel = extras.getInt("camera_channel");

				MyCamera camera = new MyCamera(dev_nickname, dev_uid, view_acc, view_pwd);
				DeviceInfo dev = new DeviceInfo(db_id, camera.getUUID(), dev_nickname, dev_uid, view_acc, view_pwd, "", event_notification, channel, null);
//				MultiViewActivity.DeviceList.add(dev);

//				camera.registerIOTCListener(this);
//				camera.registerIOTCListener(MultiViewActivity.getMultiViewActivityIRegisterIOTCListener());
//				camera.connect(dev_uid);
//				camera.start(MyCamera.DEFAULT_AV_CHANNEL, view_acc, view_pwd);
//				camera.sendIOCtrl(MyCamera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_REQ, AVIOCTRLDEFs.SMsgAVIoctrlDeviceInfoReq.parseContent());
//				camera.sendIOCtrl(MyCamera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETSUPPORTSTREAM_REQ, AVIOCTRLDEFs.SMsgAVIoctrlGetSupportStreamReq.parseContent());
//				camera.sendIOCtrl(MyCamera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETAUDIOOUTFORMAT_REQ, AVIOCTRLDEFs.SMsgAVIoctrlGetAudioOutFormatReq.parseContent());
//				camera.sendIOCtrl(MyCamera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_TIMEZONE_REQ, AVIOCTRLDEFs.SMsgAVIoctrlTimeZone.parseContent());
//				if(MultiViewActivity.SupportEasyWiFiSetting)
//				{
//					//簡易wifi設定
//					if(extras.getString("wifi_ssid")!=null&&extras.getString("wifi_password")!=null)
//					{
//						if(extras.getString("wifi_password").equals(""))
//						{
//							camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETWIFI_REQ, AVIOCTRLDEFs.SMsgAVIoctrlSetWifiReq.parseContent(extras.getString("wifi_ssid").getBytes(), extras.getString("wifi_password").getBytes(),(byte)1,(byte)AVIOCTRLDEFs.AVIOTC_WIFIAPENC_NONE));
//						}
//						else
//						{
//							camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETWIFI_REQ, AVIOCTRLDEFs.SMsgAVIoctrlSetWifiReq.parseContent(extras.getString("wifi_ssid").getBytes(), extras.getString("wifi_password").getBytes(),(byte)1,(byte)AVIOCTRLDEFs.AVIOTC_WIFIAPENC_WPA2_AES));
//						}
//					}
//				}
				
//				camera.LastAudioMode = 1;

//				MultiViewActivity.CameraList.add(camera);
//				adapter.notifyDataSetChanged();
				
				
				Bundle NEWextras = new Bundle();
				NEWextras.putString("dev_uid",dev.UID);
				NEWextras.putString("dev_uuid", dev.UUID);
				NEWextras.putString("dev_nickname", dev.NickName);
				NEWextras.putString("conn_status",dev.Status);
				NEWextras.putString("view_acc", dev.View_Account);
				NEWextras.putString("view_pwd", dev.View_Password);
				NEWextras.putString("OriginallyUID",OriginallyUID);
				NEWextras.putInt("OriginallyChannelIndex",OriginallyChannelIndex);
				// add device  always channel0
				NEWextras.putInt("camera_channel",MyCamera.DEFAULT_AV_CHANNEL);
				NEWextras.putInt("MonitorIndex", mMonitorIndex);
				Intent Intent = new Intent();
				Intent.putExtras(NEWextras);
				CloudDeviceListActivity.this.setResult(RESULT_OK, Intent);
				CloudDeviceListActivity.this.finish();


				break;
			}
		} 
	}


	private void initDownLoad()
	{
		mDeviceOnCloudClient = new DeviceOnCloudClient();
		mDeviceOnCloudClient.RegistrInterFace(this);
		
		ProgressDialog = new android.app.ProgressDialog(this);
		ProgressDialog.setMessage("取得資料中....");
		ProgressDialog.show();
		DatabaseManager DatabaseManager = new DatabaseManager(this);
		JSONObject JSONObject = new JSONObject();
		try 
		{	
			JSONObject.put("cmd","readall");
			JSONObject.put("usr",DatabaseManager.getLoginAccount());
			JSONObject.put("pwd",DatabaseManager.getLoginPassword());
			
		} catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		mDeviceOnCloudClient.download(DatabaseManager.Device_On_Cloud_URL,JSONObject);
	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	protected void onResume() 
	{
		super.onResume();
		
	}

	private void quit() {

		finish();// IOTC 

		for (MyCamera camera : MultiViewActivity.CameraList) {
			// camera.stop(MyCamera.DEFAULT_AV_CHANNEL);
			camera.unregisterIOTCListener(this);
		}

	}
	

	private void setupView() {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.cloud_devicelistactivty);
		mButtonLayout = (RelativeLayout)findViewById(R.id.btn_Layout);
		btnOK = (Button)findViewById(R.id.btnOK);
		btnOK.setOnClickListener(this);
		btnCancel = (Button)findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(this);
		mNullDeviceTxt = (TextView)findViewById(R.id.txt_null_device);
		mCloudDeviceList = (ListView) findViewById(R.id.lstCameraList);
		//addDeviceView = getLayoutInflater().inflate(R.layout.add_device_row, null);
		adapter = new DeviceListAdapter(this);
		//listView.addFooterView(addDeviceView);
		mCloudDeviceList.setAdapter(adapter);
		mCloudDeviceList.setOnItemClickListener(listViewOnItemClickListener);

	}

	
	private AdapterView.OnItemClickListener listViewOnItemClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View v, int position, long id) 
		{
			mSelectedDeviceIndex = position;
			if(!device_click_list.get(position))
			{
				for(int i = 0;i < device_click_list.size();i++)
				{
					device_click_list.set(i,false);
				}
				mMonitorIndex=position;
				device_click_list.set(position,true);
			}
			if(adapter!=null)
				adapter.notifyDataSetChanged();
		}
	};

	public static void showAlert(Context context, CharSequence title, CharSequence message, CharSequence btnTitle) {

		AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(context);
		dlgBuilder.setIcon(android.R.drawable.ic_dialog_alert);
		dlgBuilder.setTitle(title);
		dlgBuilder.setMessage(message);
		dlgBuilder.setPositiveButton(btnTitle, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		}).show();
	}


	@Override
	public void receiveFrameData(final Camera camera, int sessionChannel, Bitmap bmp) {

	}

	@Override
	public void receiveFrameInfo(final Camera camera, int sessionChannel, long bitRate, int frameRate, int onlineNm, int frameCount, int incompleteFrameCount) {

	}

	@Override
	public void receiveSessionInfo(final Camera camera, int resultCode) {

		Bundle bundle = new Bundle();
		bundle.putString("requestDevice", ((MyCamera) camera).getUUID());

		Message msg = handler.obtainMessage();
		msg.what = resultCode;
		msg.setData(bundle);
		handler.sendMessage(msg);
	}

	@Override
	public void receiveChannelInfo(final Camera camera, int sessionChannel, int resultCode) {

		Bundle bundle = new Bundle();
		bundle.putString("requestDevice", ((MyCamera) camera).getUUID());
		bundle.putInt("sessionChannel", sessionChannel);

		Message msg = handler.obtainMessage();
		msg.what = resultCode;
		msg.setData(bundle);
		handler.sendMessage(msg);
	}

	@Override
	public void receiveIOCtrlData(final Camera camera, int sessionChannel, int avIOCtrlMsgType, byte[] data) {

		Bundle bundle = new Bundle();
		bundle.putString("requestDevice", ((MyCamera) camera).getUUID());
		bundle.putInt("sessionChannel", sessionChannel);
		bundle.putByteArray("data", data);

		Message msg = handler.obtainMessage();
		msg.what = avIOCtrlMsgType;
		msg.setData(bundle);
		handler.sendMessage(msg);
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			adapter.notifyDataSetChanged();
			super.handleMessage(msg);
		}
	};

	private class DeviceListAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public DeviceListAdapter(Context context) {

			this.mInflater = LayoutInflater.from(context);
		}

		public int getCount() {

			return MyOnCloudDeviceInfo.size();
		}

		public Object getItem(int position) {

			return MyOnCloudDeviceInfo.get(position);
		}

		public long getItemId(int position) {

			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			final OnCloudDeviceInfo dev = MyOnCloudDeviceInfo.get(position);
			if (dev == null)
				return null;

			ViewHolder holder = null;

			if (convertView == null) {

				convertView = mInflater.inflate(R.layout.on_cloud_device_list, null);

				holder = new ViewHolder();
				holder.title = (TextView) convertView.findViewById(R.id.title);
				holder.info = (TextView) convertView.findViewById(R.id.info);
				holder.Radio = (RadioButton) convertView.findViewById(R.id.SelectButton);
				convertView.setTag(holder);

			} else {

				holder = (ViewHolder) convertView.getTag();
			}

			if (holder != null) {

				holder.title.setText(dev.NickName);
				holder.info.setText(dev.UID);
				if(device_click_list!=null)
				{
					try
					{
						holder.Radio.setChecked(device_click_list.get(position));
					}
					catch(IndexOutOfBoundsException e)
					{
						
					}
				}
			}

			return convertView;

		}

		public final class ViewHolder {
			public TextView title;
			public TextView info;
			public RadioButton Radio;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
			case R.id.btnOK:
				
				Intent intent = new Intent();
				Bundle extras = new Bundle();
				extras.putInt("addMode", AddDeviceActivity.ADD_DEVICE_FROM_CLOUD);
				extras.putString("uid", MyOnCloudDeviceInfo.get(mSelectedDeviceIndex).UID);
				extras.putString("nickName", MyOnCloudDeviceInfo.get(mSelectedDeviceIndex).NickName);
				intent.putExtras(extras);
				intent.setClass(CloudDeviceListActivity.this, AddDeviceActivity.class);
				startActivityForResult(intent, MultiViewActivity.REQUEST_CODE_CAMERA_ADD);
				
//				String nickName = MyOnCloudDeviceInfo.get(mSelectedDeviceIndex).NickName;
//				String devUID = MyOnCloudDeviceInfo.get(mSelectedDeviceIndex).UID;
//				String view_acc = MyOnCloudDeviceInfo.get(mSelectedDeviceIndex).View_Account;
//				String view_pwd = MyOnCloudDeviceInfo.get(mSelectedDeviceIndex).View_Password;
//				
//				DatabaseManager manager = new DatabaseManager(this);
//				long db_id = manager.addDevice(nickName, devUID, "", "", view_acc, view_pwd, 3, 0);
//				Bundle extras = new Bundle();
//				extras.putLong("db_id", db_id);
//				extras.putString("dev_nickname", nickName);
//				extras.putString("dev_uid", devUID);
//				extras.putString("view_acc", view_acc);
//				extras.putString("view_pwd", view_pwd);
//				extras.putInt("camera_channel", 0);
//				
//				Intent intent = new Intent();
//				intent.putExtras(extras);
////				String dev_nickname = extras.getString("dev_nickname");
////				String dev_uid = extras.getString("dev_uid");
////				String view_acc = extras.getString("view_acc");
////				String view_pwd = extras.getString("view_pwd");
////				int event_notification = 3;
////				int channel = extras.getInt("camera_channel");
//				
//				setResult(RESULT_OK/*DeviceListActivity.REQUEST_CODE_CLOUDCAMERA_ADD*/, intent);
//				finish();
				break;
			case R.id.btnCancel:
				setResult(RESULT_CANCELED/*DeviceListActivity.Result_CLOUD_CAMERA_Cancel*/);
				finish();
				break;
				
		}
	}

	@Override
	public void uploadok(int Status) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void downloadok(int Status) {
		// TODO Auto-generated method stub
		mSelectedDeviceIndex = 0;
		if(device_click_list!=null)
			device_click_list.clear();
		device_click_list = null;
		device_click_list = new ArrayList<Boolean>();
		MyOnCloudDeviceInfo.clear();
		if(ProgressDialog!=null)
		{
			ProgressDialog.dismiss();
		}
		JSONObject JSONObject = mDeviceOnCloudClient.getJSONObject();
//		for(int i =0; i<JSONObject.length();i++)
//		{
			try 
			{
				JSONArray mJSONArray = JSONObject.getJSONArray("record");
				for(int j=0;j<mJSONArray.length();j++)
				{
					JSONObject mJSONObject = (JSONObject)mJSONArray.get(j);
					OnCloudDeviceInfo onCloudDeviceInfo = new OnCloudDeviceInfo(mJSONObject.getString("dev_name"), mJSONObject.getString("dev_uid"), 
																				mJSONObject.getString("dev_user"), mJSONObject.getString("dev_passwd"), 
																				mJSONObject.getString("place"), mJSONObject.getString("dev_detail"), 
																				mJSONObject.getString("location"));
					if(!isExistInLocalList(onCloudDeviceInfo))
						MyOnCloudDeviceInfo.add(onCloudDeviceInfo);
					
					if(j == 0)
						device_click_list.add(true);
					else
						device_click_list.add(false);
				}

				if(MyOnCloudDeviceInfo.size() == 0){
					
					mNullDeviceTxt.setVisibility(View.VISIBLE);
					mButtonLayout.setVisibility(View.GONE);
				}
					
					
			}
			catch (JSONException e) 
			{
				mNullDeviceTxt.setVisibility(View.VISIBLE);
				mButtonLayout.setVisibility(View.GONE);
				e.printStackTrace();
			}
			adapter.notifyDataSetChanged();
//		}
	}

	@Override
	public void error(int Status) {
		// TODO Auto-generated method stub
		if(ProgressDialog!=null)
		{
			ProgressDialog.dismiss();
			Toast.makeText(this,"登入失敗"+Status, Toast.LENGTH_SHORT).show();
		}
	}
	
	private boolean isExistInLocalList(OnCloudDeviceInfo cloudDeviceInfo) {
		
		
		for(int i=0; i< MultiViewActivity.CameraList.size(); i++){
			MyCamera myCamera = MultiViewActivity.CameraList.get(i);
			if(myCamera.getUID().equals(cloudDeviceInfo.UID))
				return true;
		}
			
		return false;
	}

	@Override
	public void receiveFrameDataForMediaCodec(Camera camera, int avChannel,
			byte[] buf, int length, int pFrmNo, byte[] pFrmInfoBuf, boolean isIframe, int codecId) {
		// TODO Auto-generated method stub
		
	}

}