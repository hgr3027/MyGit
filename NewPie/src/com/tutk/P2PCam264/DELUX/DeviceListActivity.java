package com.tutk.P2PCam264.DELUX;

import java.io.File;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.example.newpie.R;
import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.Camera;
import com.tutk.IOTC.IRegisterIOTCListener;
import com.tutk.P2PCam264.AddDeviceActivity;
import com.tutk.P2PCam264.DatabaseManager;
import com.tutk.P2PCam264.DeviceInfo;
import com.tutk.P2PCam264.MyCamera;
import com.tutk.P2PCam264.DeviceOnCloud.CloudDeviceListActivity;
import com.tutk.P2PCam264.DeviceOnCloud.LoginActivity;
import com.tutk.P2PCam264.EasySettingWIFI.SelectCableActivity;
@SuppressWarnings("all")
public class DeviceListActivity extends SherlockActivity implements IRegisterIOTCListener, View.OnClickListener {

	private final String TAG = "DeviceListActivity";
	private static final int Build_VERSION_CODES_ICE_CREAM_SANDWICH = 14;

	private DeviceListActivity mActivity;
	private DeviceListAdapter mAdapter;

	public static final int REQUEST_CODE_CAMERA_ADD = 0;
	public static final int REQUEST_CODE_CLOUDCAMERA_ADD = 1;
	public static final int Result_CLOUD_CAMERA_Cancel = 6;
	public static final int Result_CLOUD_CAMERA_OK = 7;
	public static int nShowMessageCount = 0;

	private int mMonitorIndex = -1;
	private String OriginallyUID;
	private int OriginallyChannelIndex;

	private TextView mNullDeviceTxt;
	private ImageButton btn_add_web;
	private ImageButton btn_add_local;
	private Button mEditBtn;

	private LinearLayout add_btn_layout;

	private ListView mCameraList;
	private boolean mIsRemovable = false;

	// private View addDeviceView;;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mActivity = this;

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setCustomView(R.layout.titlebar);
		TextView tv = (TextView) this.findViewById(R.id.bar_text);
		tv.setText(getText(R.string.app_name));

		mEditBtn = (Button) this.findViewById(R.id.bar_right_btn);
		mEditBtn.setText(R.string.txt_edit);
		mEditBtn.setTextColor(Color.WHITE);
		if (MultiViewActivity.DeviceList.size() > 0)
			mEditBtn.setVisibility(View.VISIBLE);
		mEditBtn.setOnClickListener(this);

		if (Build.VERSION.SDK_INT < Build_VERSION_CODES_ICE_CREAM_SANDWICH) {
			BitmapDrawable bg = (BitmapDrawable) getResources().getDrawable(R.drawable.bg_striped);
			bg.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
			getSupportActionBar().setBackgroundDrawable(bg);
		}

		Bundle bundle = this.getIntent().getExtras();
		mMonitorIndex = bundle.getInt("MonitorIndex");
		OriginallyUID = bundle.getString("OriginallyUID");
		OriginallyChannelIndex = bundle.getInt("OriginallyChannelIndex");
		setupView();

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

			quit();
			return false;

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

		if (requestCode == REQUEST_CODE_CAMERA_ADD || requestCode == REQUEST_CODE_CLOUDCAMERA_ADD) {

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
				DeviceInfo dev = new DeviceInfo(db_id, camera.getUUID(), dev_nickname, dev_uid, view_acc, view_pwd, "", event_notification, channel,
						null);
				MultiViewActivity.DeviceList.add(dev);

				camera.registerIOTCListener(this);
				camera.registerIOTCListener(MultiViewActivity.getMultiViewActivityIRegisterIOTCListener());
				camera.connect(dev_uid);
				camera.start(MyCamera.DEFAULT_AV_CHANNEL, view_acc, view_pwd);
				camera.sendIOCtrl(MyCamera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_REQ,
						AVIOCTRLDEFs.SMsgAVIoctrlDeviceInfoReq.parseContent());
				camera.sendIOCtrl(MyCamera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETSUPPORTSTREAM_REQ,
						AVIOCTRLDEFs.SMsgAVIoctrlGetSupportStreamReq.parseContent());
				camera.sendIOCtrl(MyCamera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETAUDIOOUTFORMAT_REQ,
						AVIOCTRLDEFs.SMsgAVIoctrlGetAudioOutFormatReq.parseContent());
				camera.sendIOCtrl(MyCamera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_TIMEZONE_REQ,
						AVIOCTRLDEFs.SMsgAVIoctrlTimeZone.parseContent());
				if (MultiViewActivity.SupportEasyWiFiSetting) {

					// 簡易wifi設定
					if (extras.getString("wifi_ssid") != null && extras.getString("wifi_password") != null) {
						SharedPreferences settings = this.getSharedPreferences("WiFi Setting", 0);
						settings.edit().putString("wifi_uid", dev_uid).commit();
						settings.edit().putString("wifi_ssid", extras.getString("wifi_ssid")).commit();
						settings.edit().putString("wifi_password", extras.getString("wifi_password")).commit();
						settings.edit().putInt("wifi_enc", extras.getInt("wifi_enc")).commit();
						// if(extras.getString("wifi_password").equals(""))
						// {
						// camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
						// AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETWIFI_REQ,
						// AVIOCTRLDEFs.SMsgAVIoctrlSetWifiReq.parseContent(extras.getString("wifi_ssid").getBytes(),
						// extras.getString("wifi_password").getBytes(),(byte)1,(byte)AVIOCTRLDEFs.AVIOTC_WIFIAPENC_NONE));
						// }
						// else
						// {
						// camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
						// AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETWIFI_REQ,
						// AVIOCTRLDEFs.SMsgAVIoctrlSetWifiReq.parseContent(extras.getString("wifi_ssid").getBytes(),
						// extras.getString("wifi_password").getBytes(),(byte)1,(byte)AVIOCTRLDEFs.AVIOTC_WIFIAPENC_WPA2_AES));
						// }
					}
				}

				camera.LastAudioMode = 1;

				MultiViewActivity.CameraList.add(camera);

				mAdapter.notifyDataSetChanged();

				Bundle NEWextras = new Bundle();
				NEWextras.putString("dev_uid", dev.UID);
				NEWextras.putString("dev_uuid", dev.UUID);
				NEWextras.putString("dev_nickname", dev.NickName);
				NEWextras.putString("conn_status", dev.Status);
				NEWextras.putString("view_acc", dev.View_Account);
				NEWextras.putString("view_pwd", dev.View_Password);
				NEWextras.putString("OriginallyUID", OriginallyUID);
				NEWextras.putInt("OriginallyChannelIndex", OriginallyChannelIndex);
				// ADD DEVICE CLANNEL IS ALWAYS 0
				NEWextras.putInt("camera_channel", MyCamera.DEFAULT_AV_CHANNEL);
				NEWextras.putInt("MonitorIndex", mMonitorIndex);
				Intent Intent = new Intent();
				Intent.putExtras(NEWextras);
				DeviceListActivity.this.setResult(RESULT_OK, Intent);
				DeviceListActivity.this.finish();

				break;
			}
		} else {
			switch (resultCode) {

			case RESULT_OK:
				Intent intent = new Intent();
				intent.setClass(DeviceListActivity.this, CloudDeviceListActivity.class);
				startActivityForResult(intent, REQUEST_CODE_CLOUDCAMERA_ADD);
				break;
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	protected void onResume() {
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
		setContentView(R.layout.devicelistactivty);
		add_btn_layout = (LinearLayout) findViewById(R.id.add_btn_layout);
		mNullDeviceTxt = (TextView) findViewById(R.id.txt_null_device);
		if (MultiViewActivity.DeviceList.size() >= MultiViewActivity.CAMERA_MAX_LIMITS || OriginallyUID != null) {
			add_btn_layout.setVisibility(View.GONE);
		} else if (MultiViewActivity.DeviceList.size() == 0) {
			mNullDeviceTxt.setVisibility(View.VISIBLE);
		} else {
			Animation animation = AnimationUtils.loadAnimation(this, R.anim.devicelist_add_layout_in_anim);
			add_btn_layout.startAnimation(animation);
		}
		btn_add_web = (ImageButton) findViewById(R.id.btnWebAdd);
		btn_add_web.setOnClickListener(this);
		btn_add_local = (ImageButton) findViewById(R.id.btnLocalAdd);
		btn_add_local.setOnClickListener(this);
		mCameraList = (ListView) findViewById(R.id.lstCameraList);
		// addDeviceView = getLayoutInflater().inflate(R.layout.add_device_row, null);
		mAdapter = new DeviceListAdapter(this);
		// listView.addFooterView(addDeviceView);
		mCameraList.setAdapter(mAdapter);
		mCameraList.setOnItemClickListener(listViewOnItemClickListener);
	}

	private AdapterView.OnItemClickListener listViewOnItemClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {

			Bundle extras = new Bundle();
			extras.putString("dev_uid", MultiViewActivity.DeviceList.get(position).UID);
			extras.putString("dev_uuid", MultiViewActivity.DeviceList.get(position).UUID);
			extras.putString("dev_nickname", MultiViewActivity.DeviceList.get(position).NickName);
			extras.putString("conn_status", MultiViewActivity.DeviceList.get(position).Status);
			extras.putString("view_acc", MultiViewActivity.DeviceList.get(position).View_Account);
			extras.putString("view_pwd", MultiViewActivity.DeviceList.get(position).View_Password);
			extras.putString("OriginallyUID", OriginallyUID);
			extras.putInt("OriginallyChannelIndex", OriginallyChannelIndex);
			// add device always channel0
			extras.putInt("camera_channel", MyCamera.DEFAULT_AV_CHANNEL);
			extras.putInt("MonitorIndex", mMonitorIndex);
			Intent Intent = new Intent();
			Intent.putExtras(extras);
			DeviceListActivity.this.setResult(RESULT_OK, Intent);
			DeviceListActivity.this.finish();
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
	public void receiveFrameInfo(final Camera camera, int sessionChannel, long bitRate, int frameRate, int onlineNm, int frameCount,
			int incompleteFrameCount) {

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

			mAdapter.notifyDataSetChanged();
			super.handleMessage(msg);
		}
	};

	private class DeviceListAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public DeviceListAdapter(Context context) {

			this.mInflater = LayoutInflater.from(context);
		}

		public int getCount() {

			return MultiViewActivity.DeviceList.size();
		}

		public Object getItem(int position) {

			return MultiViewActivity.DeviceList.get(position);
		}

		public long getItemId(int position) {

			return position;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {

			final DeviceInfo dev = MultiViewActivity.DeviceList.get(position);
			final MyCamera cam = MultiViewActivity.CameraList.get(position);

			if (dev == null || cam == null)
				return null;

			ViewHolder holder = null;

			if (convertView == null) {

				convertView = mInflater.inflate(R.layout.device_list, null);

				holder = new ViewHolder();
				holder.img = (ImageView) convertView.findViewById(R.id.img);
				holder.title = (TextView) convertView.findViewById(R.id.title);
				holder.info = (TextView) convertView.findViewById(R.id.info);
				holder.status = (TextView) convertView.findViewById(R.id.status);
				holder.eventLayout = (FrameLayout) convertView.findViewById(R.id.eventLayout);
				holder.GCM_Prompt = (TextView) convertView.findViewById(R.id.GCM_Prompt);
				holder.more = (ImageView) convertView.findViewById(R.id.more);
				holder.remove = (ImageView) convertView.findViewById(R.id.remove);
				convertView.setTag(holder);

			} else {

				holder = (ViewHolder) convertView.getTag();
			}

			if (holder != null) {

				holder.eventLayout.setVisibility(View.GONE);
				if (mIsRemovable) {
					holder.eventLayout.setVisibility(View.VISIBLE);
					holder.more.setVisibility(View.GONE);
					holder.remove.setVisibility(View.VISIBLE);
					holder.remove.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							removeItemFromList(position);
						}
					});
				}

				holder.img.setImageBitmap(dev.Snapshot);
				holder.title.setText(dev.NickName);
				holder.info.setText(dev.UID);
				if (dev.n_gcm_count == 0) {
					holder.GCM_Prompt.setVisibility(View.GONE);
				} else {
					holder.GCM_Prompt.setVisibility(View.VISIBLE);
					holder.GCM_Prompt.setText(Integer.toString(dev.n_gcm_count));
				}
				if (nShowMessageCount == 0) {
					holder.status.setText(dev.Status);
				} else {
					holder.status.setText(dev.Status + " " + cam.gettempAvIndex());
				}

			}

			return convertView;

		}

		public final class ViewHolder {
			public ImageView img;
			public TextView title;
			public TextView info;
			public TextView status;
			public TextView GCM_Prompt;
			public FrameLayout eventLayout;
			public ImageView more;
			public ImageView remove;
		}

		private void removeItemFromList(int position) {

			DatabaseManager manager = new DatabaseManager(DeviceListActivity.this);
			SQLiteDatabase db = manager.getReadableDatabase();
			Cursor cursor = db.query(DatabaseManager.TABLE_SNAPSHOT, new String[] { "_id", "dev_uid", "file_path", "time" }, "dev_uid = '"
					+ MultiViewActivity.DeviceList.get(position).UID + "'", null, null, null, "_id LIMIT " + MultiViewActivity.CAMERA_MAX_LIMITS);
			while (cursor.moveToNext()) {
				String file_path = cursor.getString(2);
				File file = new File(file_path);
				if (file.exists())
					file.delete();
			}
			cursor.close();
			db.close();
			manager.removeSnapshotByUID(MultiViewActivity.DeviceList.get(position).UID);
			manager.removeDeviceByUID(MultiViewActivity.DeviceList.get(position).UID);
			MyCamera myCamera = MultiViewActivity.CameraList.get(position);
			DeviceInfo deviceInfo = MultiViewActivity.DeviceList.get(position);

			myCamera.stop(Camera.DEFAULT_AV_CHANNEL);
			myCamera.disconnect();
			myCamera.unregisterIOTCListener(mActivity);

			MultiViewActivity.DeviceList.remove(position);
			MultiViewActivity.CameraList.remove(position);
			this.notifyDataSetChanged();
			MultiViewActivity.removeFromMultiView(deviceInfo.UID, deviceInfo.UUID);
			mCameraList.post(new Runnable() {

				@Override
				public void run() {
					if (MultiViewActivity.DeviceList.size() == 0) {
						mEditBtn.setVisibility(View.GONE);
						mNullDeviceTxt.setVisibility(View.VISIBLE);

					} else if (MultiViewActivity.DeviceList.size() < MultiViewActivity.CAMERA_MAX_LIMITS
							&& add_btn_layout.getVisibility() != View.VISIBLE) {

						Animation animation = AnimationUtils.loadAnimation(mActivity, R.anim.devicelist_add_layout_in_anim);
						add_btn_layout.startAnimation(animation);
						add_btn_layout.setVisibility(View.VISIBLE);
					}
				}
			});

		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnWebAdd:
			if (MultiViewActivity.CameraList.size() < MultiViewActivity.CAMERA_MAX_LIMITS) {
				if (MultiViewActivity.SupportDeviceOnCloud) {

					DatabaseManager DatabaseManager = new DatabaseManager(this);
					if (DatabaseManager.getLoginPassword().equals("")) {

						DialogInterface.OnClickListener okClick = new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								DatabaseManager DatabaseManager = new DatabaseManager(DeviceListActivity.this);
								DatabaseManager.Logout();
								Intent intent = new Intent();
								intent.setClass(DeviceListActivity.this, LoginActivity.class);
								startActivityForResult(intent, MultiViewActivity.REQUEST_CODE_LOGIN);
								dialog.dismiss();
							}
						};

						DialogInterface.OnClickListener cancelClick = new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
							}
						};

						MultiViewActivity.showSelectDialog(this, getText(R.string.tips_warning),
								getText(R.string.txt_please_login_your_cloud_account), getText(R.string.txt_login), getText(R.string.cancel),
								okClick, cancelClick);

					} else {
						Intent intent = new Intent();
						intent.setClass(DeviceListActivity.this, CloudDeviceListActivity.class);
						startActivityForResult(intent, REQUEST_CODE_CLOUDCAMERA_ADD);
					}

				} else {
				}
			}
			break;
		case R.id.btnLocalAdd:
			if (MultiViewActivity.CameraList.size() < MultiViewActivity.CAMERA_MAX_LIMITS) {
				Intent intent = new Intent();
				if (MultiViewActivity.SupportEasyWiFiSetting) {
					intent.setClass(DeviceListActivity.this, SelectCableActivity.class);
				} else {
					intent.setClass(DeviceListActivity.this, AddDeviceActivity.class);
				}
				startActivityForResult(intent, REQUEST_CODE_CAMERA_ADD);
			}
			break;

		case R.id.bar_right_btn:

			mIsRemovable = !mIsRemovable;
			if (mIsRemovable)
				mEditBtn.setText(R.string.txt_done);
			else
				mEditBtn.setText(R.string.txt_edit);
			mAdapter.notifyDataSetChanged();
			break;
		}
	}

	@Override
	public void receiveFrameDataForMediaCodec(Camera camera, int avChannel, byte[] buf, int length, int pFrmNo, byte[] pFrmInfoBuf, boolean isIframe, int codecId) {
		// TODO Auto-generated method stub

	}

}