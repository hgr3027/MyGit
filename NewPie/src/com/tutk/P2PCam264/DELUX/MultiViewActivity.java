package com.tutk.P2PCam264.DELUX;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.newpie.R;
import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.Camera;
import com.tutk.IOTC.IRegisterIOTCListener;
import com.tutk.IOTC.Packet;
import com.tutk.Logger.Glog;
import com.tutk.P2PCam264.AboutDialog;
import com.tutk.P2PCam264.DatabaseManager;
import com.tutk.P2PCam264.DeviceInfo;
import com.tutk.P2PCam264.EventListActivity;
import com.tutk.P2PCam264.LiveViewActivity;
import com.tutk.P2PCam264.MyCamera;
import com.tutk.P2PCam264.SwitchCodecActivity;
import com.tutk.P2PCam264.ThreadTPNS;
import com.tutk.P2PCam264.DeviceOnCloud.LoginActivity;
import com.tutk.P2PCam264.onDropbox.LinkDropBoxActivity;
import com.viewpagerindicator.CirclePageIndicator;
@SuppressWarnings("all")
public class MultiViewActivity extends InitCamActivity implements IRegisterIOTCListener, View.OnClickListener,
		Custom_popupWindow.On_PopupWindow_click_Listener {

	private static final String TAG = "MultiViewActivity";
	public static final boolean SupportMultiPage = false;
	public static final boolean SupportOnDropbox = true;
	public static final boolean SupportEasyWiFiSetting = true;
	public static final boolean SupportDeviceOnCloud = true;
	private static final int idCmd_AddCamera = 7000;
	private static final int idCmd_LiveView = 7001;
	public static final int REQUEST_CODE_CAMERA_ADD = 0;
	public static final int REQUEST_CODE_CAMERA_VIEW = 1;
	public static final int REQUEST_CODE_CAMERA_EDIT = 2;
	public static final int REQUEST_CODE_CAMERA_EDIT_DELETE_OK = 1;
	public static final int REQUEST_CODE_CAMERA_EDIT_DATA_OK = 5;
	public static final int REQUEST_CODE_CAMERA_HISTORY = 3;
	public static final int REQUEST_CODE_CAMERA_SELECT_MONITOR = 4;
	private static final int REQUEST_CODE_Dropbox_SETTING = 6;
	public static final int REQUEST_CODE_LOGIN = 7;
	public static final int REQUEST_CODE_LOGIN_QUIT = 8;
	public static final int REQUEST_CODE_SWITCH_CODEC = 9;
	private static livaviewFragmentAdapter mAdapter;
	private ViewPager mPager;
	private CirclePageIndicator mIndicator;
	public static int nShowMessageCount = 0;
	public static final int CAMERA_MAX_LIMITS = 4;
	private static IRegisterIOTCListener IRegisterIOTCListener;
	private int mCustomURL_CmdID;
	private String mstrUid_FromCustomURL;
	private ImageButton btn_menu = null;
	// pageview無法重復新增
	private boolean IsAddpage = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setCustomView(R.layout.titlebar);
		RelativeLayout layout = (RelativeLayout) this.findViewById(R.id.title_bar_layout);
		layout.setBackgroundResource(R.drawable.main_title_bk);

		btn_menu = (ImageButton) this.findViewById(R.id.bar_btn);
		btn_menu.setVisibility(View.VISIBLE);
		btn_menu.setBackgroundResource(R.drawable.btn_moreset_switch);
		btn_menu.setOnClickListener(this);
		setContentView(R.layout.multi_view_activity);

		super.onCreate(savedInstanceState);

		mCustomURL_CmdID = -1;
		Intent tIntent = this.getIntent();
		String tSchema = tIntent.getScheme();
		Glog.I("p2pcamlive", "MainActivity.onCreate... tSchema:\"" + ((tSchema == null) ? "(null)" : tSchema) + "\"");
		if (tSchema != null && tSchema.equals("p2pcamlive")) {
			Uri myURI = tIntent.getData();
			if (myURI != null) {
				Glog.I("p2pcamlive", "MainActivity.onCreate... myURI:\"" + myURI.toString() + "\"");
				if (myURI != null) {
					String strUri = myURI.toString();
					int nIdx_ValidURI = strUri.indexOf("com.tutk.p2pcamlive?");
					if (0 <= nIdx_ValidURI) {

						String strQueryParameterCP = strUri.substring(nIdx_ValidURI + "com.tutk.p2pcamlive?".length());
						// p2pcamlive://com.tutk.p2pcamlive?tabIdx:0
						// 0: Camera list
						// 1: Event list (NOT support on Android UI)
						//
						int nIdx_Parameter = strQueryParameterCP.indexOf("tabIdx:");
						if (0 <= nIdx_Parameter) {
							String strTabIndex = strQueryParameterCP.substring(nIdx_Parameter + "tabIdx:".length());

							Glog.I("p2pcamlive", "CameraList count:" + CameraList.size() + " Jump to Event list!");
						}

						// p2pcamlive://com.tutk.p2pcamlive?addDev:XXXXXXXXXXXXXXXXXXXX
						//
						nIdx_Parameter = strQueryParameterCP.indexOf("addDev:");
						if (0 <= nIdx_Parameter) {
							String strUid = strQueryParameterCP.substring(nIdx_Parameter + "addDev:".length());

							Glog.I("p2pcamlive", "CameraList count:" + CameraList.size() + " Add Camera UID:\"" + strUid + "\"");
							mCustomURL_CmdID = idCmd_AddCamera;
							mstrUid_FromCustomURL = strUid;
						}

						// p2pcamlive://com.tutk.p2pcamlive?liveView:XXXXXXXXXXXXXXXXXXXX
						//
						nIdx_Parameter = strQueryParameterCP.indexOf("liveView:");
						if (0 <= nIdx_Parameter) {
							String strUid = strQueryParameterCP.substring(nIdx_Parameter + "liveView:".length());

							Glog.I("p2pcamlive", "CameraList count:" + CameraList.size() + " Live view UID:\"" + strUid + "\"");
							mCustomURL_CmdID = idCmd_LiveView;
							mstrUid_FromCustomURL = strUid;

							handler.postDelayed(new Runnable() {

								@Override
								public void run() {

									for (DeviceInfo dev_info : DeviceList) {
										if (dev_info.UID.equals(mstrUid_FromCustomURL)) {

											Bundle extras = new Bundle();
											extras.putString("dev_uid", dev_info.UID);
											extras.putString("dev_uuid", dev_info.UUID);
											extras.putString("dev_nickname", dev_info.NickName);
											extras.putString("conn_status", dev_info.Status);
											extras.putString("view_acc", dev_info.View_Account);
											extras.putString("view_pwd", dev_info.View_Password);
											extras.putInt("camera_channel", dev_info.ChannelIndex);

											Intent intent = new Intent();
											intent.putExtras(extras);
											intent.setClass(MultiViewActivity.this, LiveViewActivity.class);
											startActivityForResult(intent, REQUEST_CODE_CAMERA_VIEW);

											break;
										}
									}

								}

							}, 1000);

						}

					}
				}
			}
		}

		setupView();
		IRegisterIOTCListener = this;
		if (SupportDeviceOnCloud) {
			DatabaseManager DatabaseManager = new DatabaseManager(this);
//			if (DatabaseManager.getLoginPassword().equals("")) {
//				Intent mainIntent = new Intent(this, LoginActivity.class);
//				startActivityForResult(mainIntent, REQUEST_CODE_LOGIN);
//			}
		}
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
		quit();
	}

	public static IRegisterIOTCListener getMultiViewActivityIRegisterIOTCListener() {
		return IRegisterIOTCListener;
	}

	private void setupView() {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		mAdapter = new livaviewFragmentAdapter(MultiViewActivity.this.getSupportFragmentManager());

		mPager = (ViewPager) findViewById(R.id.liveviewpager);
		mPager.setAdapter(mAdapter);

		mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);

		InitMutliMonitor();

	}

	@SuppressWarnings("deprecation")
	private void startOnGoingNotification(String Text) {

		NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		try {

			Intent intent = new Intent(this, MultiViewActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

			Notification notification = new Notification(R.drawable.nty_app, String.format(getText(R.string.ntfAppRunning).toString(),
					getText(R.string.app_name).toString()), 0);
			notification.setLatestEventInfo(this, getText(R.string.app_name), Text, pendingIntent);
			notification.flags |= Notification.FLAG_ONGOING_EVENT;

			manager.notify(0, notification);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void stopOnGoingNotification() {

		NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		manager.cancel(0);
		manager.cancel(1);
	}

	@SuppressWarnings("deprecation")
	private void showNotification(DeviceInfo dev, int camChannel, int evtType, long evtTime) {

		try {

			NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

			Bundle extras = new Bundle();
			extras.putString("dev_uuid", dev.UUID);
			extras.putString("dev_uid", dev.UID);
			extras.putString("dev_nickname", dev.NickName);
			extras.putInt("camera_channel", camChannel);
			extras.putString("view_acc", dev.View_Account);
			extras.putString("view_pwd", dev.View_Password);

			Intent intent = new Intent(this, EventListActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtras(extras);

			PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

			Calendar cal = Calendar.getInstance();
			cal.setTimeZone(TimeZone.getDefault());
			cal.setTimeInMillis(evtTime);
			cal.add(Calendar.MONTH, 0);

			Notification notification = new Notification(R.drawable.nty_alert, String.format(getText(R.string.ntfIncomingEvent).toString(),
					dev.NickName), cal.getTimeInMillis());
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			notification.flags |= Notification.FLAG_NO_CLEAR;

			if (dev.EventNotification == 0)
				notification.defaults = Notification.DEFAULT_LIGHTS;
			else if (dev.EventNotification == 1)
				notification.defaults = Notification.DEFAULT_SOUND;
			else if (dev.EventNotification == 2)
				notification.defaults = Notification.DEFAULT_VIBRATE;
			else
				notification.defaults = Notification.DEFAULT_ALL;

			notification.setLatestEventInfo(this, String.format(getText(R.string.ntfIncomingEvent).toString(), dev.NickName),
					String.format(getText(R.string.ntfLastEventIs).toString(), getEventType(this, evtType, false)), pendingIntent);

			manager.notify(1, notification);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void showSDCardFormatDialog(final Camera camera, final DeviceInfo device) {

		final AlertDialog dlg = new AlertDialog.Builder(MultiViewActivity.this).create();
		dlg.setTitle(R.string.dialog_FormatSDCard);
		dlg.setIcon(android.R.drawable.ic_menu_more);

		LayoutInflater inflater = dlg.getLayoutInflater();
		View view = inflater.inflate(R.layout.format_sdcard, null);
		dlg.setView(view);

		final CheckBox chbShowTipsFormatSDCard = (CheckBox) view.findViewById(R.id.chbShowTipsFormatSDCard);
		final Button btnFormat = (Button) view.findViewById(R.id.btnFormatSDCard);
		final Button btnClose = (Button) view.findViewById(R.id.btnClose);

		btnFormat.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_FORMATEXTSTORAGE_REQ,
						AVIOCTRLDEFs.SMsgAVIoctrlFormatExtStorageReq.parseContent(0));
				device.ShowTipsForFormatSDCard = chbShowTipsFormatSDCard.isChecked();

				DatabaseManager db = new DatabaseManager(MultiViewActivity.this);
				db.updateDeviceAskFormatSDCardByUID(device.UID, device.ShowTipsForFormatSDCard);

				dlg.dismiss();
			}
		});

		btnClose.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				device.ShowTipsForFormatSDCard = chbShowTipsFormatSDCard.isChecked();

				DatabaseManager db = new DatabaseManager(MultiViewActivity.this);
				db.updateDeviceAskFormatSDCardByUID(device.UID, device.ShowTipsForFormatSDCard);

				dlg.dismiss();
			}
		});
	}

	@Override
	public void receiveFrameData(Camera camera, int avChannel, Bitmap bmp) {
		// TODO Auto-generated method stub

	}

	private String getSessionMode(int mode) {

		String result = "";
		if (mode == 0)
			result = getText(R.string.connmode_p2p).toString();
		else if (mode == 1)
			result = getText(R.string.connmode_relay).toString();
		else if (mode == 2)
			result = getText(R.string.connmode_lan).toString();
		else
			result = getText(R.string.connmode_none).toString();

		return result;
	}

	private String getPerformance(int mode) {

		String result = "";
		if (mode < 30)
			result = getText(R.string.txtBad).toString();
		else if (mode < 60)
			result = getText(R.string.txtNormal).toString();
		else
			result = getText(R.string.txtGood).toString();

		return result;
	}

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

	public static void showSelectDialog(Context context, CharSequence title, CharSequence message, CharSequence okBtnTxt, CharSequence cancelBtnTxt,
			OnClickListener okClickListener, OnClickListener cancelClickLinstener) {

		AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(context);
		dlgBuilder.setIcon(android.R.drawable.ic_dialog_alert);
		dlgBuilder.setTitle(title);
		dlgBuilder.setMessage(message);
		dlgBuilder.setPositiveButton(okBtnTxt, okClickListener);
		dlgBuilder.setNegativeButton(cancelBtnTxt, cancelClickLinstener);
		dlgBuilder.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_CODE_CAMERA_ADD) {

			switch (resultCode) {
			case RESULT_OK:
				// Bundle extras = data.getExtras();
				// String dev_uuid = extras.getString("dev_uuid");
				// String dev_uid = extras.getString("dev_uid");
				// int channelIndex = extras.getInt("camera_channel");

				break;
			}
		} else if (requestCode == REQUEST_CODE_CAMERA_VIEW) {

			if (mCustomURL_CmdID != -1) {
				finish();
				return;
			}

			switch (resultCode) {
			case RESULT_OK:

				Bundle extras = data.getExtras();
				String dev_uuid = extras.getString("dev_uuid");
				String dev_uid = extras.getString("dev_uid");
				byte[] byts = extras.getByteArray("snapshot");
				int channelIndex = extras.getInt("camera_channel");
				int MonitorIndex = extras.getInt("MonitorIndex");
				int OriginallyChannelIndex = extras.getInt("OriginallyChannelIndex");
				String dev_nickname = extras.getString("dev_nickname");

				Bitmap snapshot = null;
				if (byts != null && byts.length > 0)
					snapshot = DatabaseManager.getBitmapFromByteArray(byts);

				for (int i = 0; i < DeviceList.size(); i++) {

					if (dev_uuid.equalsIgnoreCase(DeviceList.get(i).UUID) && dev_uid.equalsIgnoreCase(DeviceList.get(i).UID)) {

						DeviceList.get(i).ChannelIndex = channelIndex;

						if (snapshot != null)
							DeviceList.get(i).Snapshot = snapshot;

						break;
					}
				}
				if (channelIndex != OriginallyChannelIndex) {
					DatabaseManager DatabaseManager = new DatabaseManager(this);
					DatabaseManager.remove_Device_Channel_Allonation_To_MonitorByUID(dev_uid, OriginallyChannelIndex, MonitorIndex);
					DatabaseManager.add_Device_Channel_Allonation_To_MonitorByUID(dev_uid, channelIndex, MonitorIndex);
					DatabaseManager = null;
					ChangeMutliMonitor(dev_uid, dev_uuid, dev_nickname, channelIndex, MonitorIndex);
				}
				if (mAdapter != null)
					mAdapter.ReflashChannelInfo();
				break;
			// 流程上有可能從LIVEVIEW 刪除
			case REQUEST_CODE_CAMERA_EDIT_DELETE_OK:

				Bundle extras2 = data.getExtras();
				String dev_uuid2 = extras2.getString("dev_uuid");
				String dev_uid2 = extras2.getString("dev_uid");
				DeviceInfo device = null;
				MyCamera mCamera = null;
				for (DeviceInfo deviceInfo : MultiViewActivity.DeviceList) {
					if (dev_uuid2.equalsIgnoreCase(deviceInfo.UUID) && dev_uid2.equalsIgnoreCase(deviceInfo.UID)) {
						device = deviceInfo;
						break;
					}
				}
				for (MyCamera camera : MultiViewActivity.CameraList) {

					if (dev_uuid2.equalsIgnoreCase(camera.getUUID()) && dev_uid2.equalsIgnoreCase(camera.getUID())) {

						mCamera = camera;
						break;
					}

				}
				if (device == null || mCamera == null)
					return;
				removeFromMultiView(device.UID, device.UUID);

				ThreadTPNS thread = new ThreadTPNS(MultiViewActivity.this, device.UID);
				thread.start();
				// stop & remove camera
				mCamera.stop(Camera.DEFAULT_AV_CHANNEL);
				mCamera.disconnect();
				mCamera.unregisterIOTCListener(this);
				CameraList.remove(mCamera);
				// remove snapshot from database & storage
				DatabaseManager manager = new DatabaseManager(MultiViewActivity.this);
				SQLiteDatabase db = manager.getReadableDatabase();
				Cursor cursor = db.query(DatabaseManager.TABLE_SNAPSHOT, new String[] { "_id", "dev_uid", "file_path", "time" }, "dev_uid = '"
						+ device.UID + "'", null, null, null, "_id LIMIT " + MultiViewActivity.CAMERA_MAX_LIMITS);
				while (cursor.moveToNext()) {
					String file_path = cursor.getString(2);
					File file = new File(file_path);
					if (file.exists())
						file.delete();
				}
				cursor.close();
				db.close();

				manager.removeSnapshotByUID(device.UID);

				// remove camera from database
				manager.removeDeviceByUID(device.UID);

				// remove item from listview
				MultiViewActivity.DeviceList.remove(device);
				MultiViewActivity.showAlert(MultiViewActivity.this, getText(R.string.tips_warning), getText(R.string.tips_remove_camera_ok),
						getText(R.string.ok));
				break;
			}
		} else if (requestCode == REQUEST_CODE_CAMERA_SELECT_MONITOR) {
			switch (resultCode) {
			case RESULT_OK:
				Bundle extras = data.getExtras();
				String dev_uuid = extras.getString("dev_uuid");
				String dev_uid = extras.getString("dev_uid");
				String dev_nickname = extras.getString("dev_nickname");
				String OriginallyUID = extras.getString("OriginallyUID");
				int OriginallyChannelIndex = extras.getInt("OriginallyChannelIndex");
				int channelIndex = extras.getInt("camera_channel");
				int MonitorIndex = extras.getInt("MonitorIndex");

				DatabaseManager DatabaseManager = new DatabaseManager(this);
				DatabaseManager.remove_Device_Channel_Allonation_To_MonitorByUID(OriginallyUID, OriginallyChannelIndex, MonitorIndex);
				DatabaseManager.add_Device_Channel_Allonation_To_MonitorByUID(dev_uid, channelIndex, MonitorIndex);
				DatabaseManager = null;
				ChangeMutliMonitor(dev_uid, dev_uuid, dev_nickname, channelIndex, MonitorIndex);
				break;
			}
		} else if (requestCode == REQUEST_CODE_CAMERA_EDIT) {

			switch (resultCode) {
			case REQUEST_CODE_CAMERA_EDIT_DELETE_OK:

				Bundle extras = data.getExtras();
				String dev_uuid = extras.getString("dev_uuid");
				String dev_uid = extras.getString("dev_uid");
				DeviceInfo device = null;
				MyCamera mCamera = null;
				for (DeviceInfo deviceInfo : MultiViewActivity.DeviceList) {
					if (dev_uuid.equalsIgnoreCase(deviceInfo.UUID) && dev_uid.equalsIgnoreCase(deviceInfo.UID)) {
						device = deviceInfo;
						break;
					}
				}
				for (MyCamera camera : MultiViewActivity.CameraList) {

					if (dev_uuid.equalsIgnoreCase(camera.getUUID()) && dev_uid.equalsIgnoreCase(camera.getUID())) {

						mCamera = camera;
						break;
					}

				}
				if (device == null || mCamera == null)
					return;
				removeFromMultiView(device.UID, device.UUID);

				ThreadTPNS thread = new ThreadTPNS(MultiViewActivity.this, device.UID);
				thread.start();
				// stop & remove camera
				mCamera.stop(Camera.DEFAULT_AV_CHANNEL);
				mCamera.disconnect();
				mCamera.unregisterIOTCListener(this);
				CameraList.remove(mCamera);
				// remove snapshot from database & storage
				DatabaseManager manager = new DatabaseManager(MultiViewActivity.this);
				SQLiteDatabase db = manager.getReadableDatabase();
				Cursor cursor = db.query(DatabaseManager.TABLE_SNAPSHOT, new String[] { "_id", "dev_uid", "file_path", "time" }, "dev_uid = '"
						+ device.UID + "'", null, null, null, "_id LIMIT " + MultiViewActivity.CAMERA_MAX_LIMITS);
				while (cursor.moveToNext()) {
					String file_path = cursor.getString(2);
					File file = new File(file_path);
					if (file.exists())
						file.delete();
				}
				cursor.close();
				db.close();

				manager.removeSnapshotByUID(device.UID);

				// remove camera from database
				manager.removeDeviceByUID(device.UID);

				// remove item from listview
				MultiViewActivity.DeviceList.remove(device);
				MultiViewActivity.showAlert(MultiViewActivity.this, getText(R.string.tips_warning), getText(R.string.tips_remove_camera_ok),
						getText(R.string.ok));
				break;

			case REQUEST_CODE_CAMERA_EDIT_DATA_OK:
				mAdapter.ReflashChannelInfo();
				break;
			}

		} else if (requestCode == REQUEST_CODE_LOGIN) {
			switch (resultCode) {
			case REQUEST_CODE_LOGIN_QUIT:
				quit();
				break;
			}

		}
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

		}
	};

	private void quit() {

		Glog.D(TAG, "issue activity result code:" + RESULT_OK + "L ...");
		stopOnGoingNotification();
		setResult(RESULT_OK);
		System.exit(0);
	//	finish();
		
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {

		case KeyEvent.KEYCODE_BACK:

			quit();

			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@SuppressWarnings("unused")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.bar_btn:
			btn_menu.setBackgroundResource(R.drawable.moreset_clicked);
			PopupWindow popupWindow = null;
			ViewGroup layout = null;
			if (SupportOnDropbox && SupportDeviceOnCloud) {
				layout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.menu_popupwindow, null);
			} else if (SupportOnDropbox) {
				layout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.menu_popupwindow_no_device_on_cloud, null);
			} else if (SupportDeviceOnCloud) {
				layout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.menu_popupwindow_notondropbox, null);
			} else {
				layout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.menu_popupwindow_no_cloud_no_dropbox, null);
			}
			popupWindow = Custom_popupWindow.Menu_PopupWindow_newInstance(MultiViewActivity.this, layout, this);
			popupWindow.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss() {
					// TODO Auto-generated method stub
					btn_menu.setBackgroundResource(R.drawable.btn_moreset_switch);

				}
			});
			popupWindow.showAsDropDown(v);

			break;

		}

	}

	private void ChangeMutliMonitor(String UID, String UUID, String name, int CameraChannel, int MonitorIndex) {

		Chaanel_to_Monitor_Info mChaanel_Info = new Chaanel_to_Monitor_Info(UUID, name, UID, CameraChannel, "CH" + CameraChannel, MonitorIndex);

		if (mAdapter.ChangeChannelInfo(mChaanel_Info, MonitorIndex) && !IsAddpage) {
			if (SupportMultiPage) {
				mIndicator.setViewPager(mPager);
				mIndicator.setOnPageChangeListener(mAdapter);
			}
		}
		mAdapter.notifyDataSetChanged();
	}

	private void InitMutliMonitor() {

		ArrayList<ArrayList<Chaanel_to_Monitor_Info>> mChaanel_Info = new ArrayList<ArrayList<Chaanel_to_Monitor_Info>>();
		int LinkMonitorCount = 0;
		int PageCount = 0;
		ArrayList<Chaanel_to_Monitor_Info> BasicsubInfo = new ArrayList<Chaanel_to_Monitor_Info>();
		mChaanel_Info.add(BasicsubInfo);
		for (MyCamera camera : CameraList) {
			if (camera != null) {

				DatabaseManager manager = new DatabaseManager(this);
				SQLiteDatabase db = manager.getReadableDatabase();
				Cursor cursor = db.query(DatabaseManager.TABLE_DEVICE_CHANNEL_ALLOCATION_TO_MONITOR, new String[] { "dev_uid", "dev_channel_Index",
						"Monitor_Index" }, "dev_uid = ?", new String[] { camera.getUID() }, null, null, "dev_channel_Index" + " ASC");
				while (cursor.moveToNext()) {
					int ChannelIndex = cursor.getInt(1);
					int monitorindex = cursor.getInt(2);
					LinkMonitorCount++;
					mChaanel_Info.get(PageCount).add(
							new Chaanel_to_Monitor_Info(camera.getUUID(), camera.getName(), camera.getUID(), ChannelIndex, "CH" + ChannelIndex,
									monitorindex));
					if (SupportMultiPage) {
						if (LinkMonitorCount % 6 == 0) {
							ArrayList<Chaanel_to_Monitor_Info> subInfo = new ArrayList<Chaanel_to_Monitor_Info>();
							mChaanel_Info.add(subInfo);
							PageCount++;
						}
					}
				}
				cursor.close();

			}
		}

		if (PageCount != 0) {
			if (SupportMultiPage) {
				IsAddpage = true;
				mIndicator.setViewPager(mPager);
				mIndicator.setOnPageChangeListener(mAdapter);
			}
		}
		mAdapter.SetChannelInfo(mChaanel_Info);
		mAdapter.notifyDataSetChanged();
	}

	@Override
	protected void INIT_CAMERA_LIST_OK() {
		// TODO Auto-generated method stub
		String notify = "";
		if (CameraList.size() == 0)
			notify = String.format(getText(R.string.ntfAppRunning).toString(), getText(R.string.app_name).toString());
		else
			notify = String.format(getText(R.string.ntfCameraRunning).toString(), CameraList.size());

		startOnGoingNotification(notify);
		reflash_Status();

	}

	@Override
	protected void IOTYPE_USER_IPCAM_DEVINFO_RESP(MyCamera camera, DeviceInfo device, byte[] data) {
		// TODO Auto-generated method stub
		int total = Packet.byteArrayToInt_Little(data, 40);
		if (total == -1 && camera != null && camera.getSDCardFormatSupported(0) && device != null && device.ShowTipsForFormatSDCard)
			showSDCardFormatDialog(camera, device);

		reflash_Status();
	}

	@Override
	protected void IOTYPE_USER_IPCAM_EVENT_REPORT(DeviceInfo DeviceInfo, byte[] data) {
		byte[] t = new byte[8];
		System.arraycopy(data, 0, t, 0, 8);
		AVIOCTRLDEFs.STimeDay evtTime = new AVIOCTRLDEFs.STimeDay(t);

		int camChannel = Packet.byteArrayToInt_Little(data, 12);
		int evtType = Packet.byteArrayToInt_Little(data, 16);

		if (evtType != AVIOCTRLDEFs.AVIOCTRL_EVENT_MOTIONPASS && evtType != AVIOCTRLDEFs.AVIOCTRL_EVENT_IOALARMPASS)
			showNotification(DeviceInfo, camChannel, evtType, evtTime.getTimeInMillis());
	};

	@Override
	protected void IOTYPE_USER_IPCAM_FORMATEXTSTORAGE_RESP(byte[] data) {
		// TODO Auto-generated method stub

		int result = data[4];

		if (result == 0)
			Toast.makeText(this, getText(R.string.tips_format_sdcard_success).toString(), Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(this, getText(R.string.tips_format_sdcard_failed).toString(), Toast.LENGTH_SHORT).show();

	}

	@Override
	protected void CONNECTION_STATE_CONNECTING(boolean Status) {
		// TODO Auto-generated method stub
		reflash_Status();
	}

	@Override
	protected void CONNECTION_STATE_CONNECTED(boolean Status, String UID) {
		// TODO Auto-generated method stub
		SharedPreferences settings = this.getSharedPreferences("WiFi Setting", 0);

		String WiFisettingUID = settings.getString("wifi_uid", "");

		if (MultiViewActivity.SupportEasyWiFiSetting) {
			if (WiFisettingUID.equals("") != true && WiFisettingUID.equals(UID) == true) {
				for (MyCamera camera : CameraList) {

					if (UID.equalsIgnoreCase(camera.getUID())) {

						String WiFiSSID = settings.getString("wifi_ssid", "");
						String WiFiPassword = settings.getString("wifi_password", "");
						int WiFiEnc = settings.getInt("wifi_enc", -1);
						if (WiFiPassword.equals("")) {
							camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETWIFI_REQ,
									AVIOCTRLDEFs.SMsgAVIoctrlSetWifiReq.parseContent(WiFiSSID.getBytes(), WiFiPassword.getBytes(), (byte) 1,
											(byte) AVIOCTRLDEFs.AVIOTC_WIFIAPENC_NONE));
						} else {
							if (WiFiEnc == AVIOCTRLDEFs.AVIOTC_WIFIAPENC_WPA2_AES)
								camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETWIFI_REQ,
										AVIOCTRLDEFs.SMsgAVIoctrlSetWifiReq.parseContent(WiFiSSID.getBytes(), WiFiPassword.getBytes(), (byte) 1,
												(byte) AVIOCTRLDEFs.AVIOTC_WIFIAPENC_WPA2_AES));
							else if (WiFiEnc == AVIOCTRLDEFs.AVIOTC_WIFIAPENC_WPA_AES)
								camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETWIFI_REQ,
										AVIOCTRLDEFs.SMsgAVIoctrlSetWifiReq.parseContent(WiFiSSID.getBytes(), WiFiPassword.getBytes(), (byte) 1,
												(byte) AVIOCTRLDEFs.AVIOTC_WIFIAPENC_WPA_AES));
						}
						settings.edit().putString("wifi_uid", "").commit();
						settings.edit().putString("wifi_ssid", "").commit();
						settings.edit().putString("wifi_password", "").commit();
						break;
					}

				}
			}
		}
		reflash_Status();
		connected_Status(UID);
	}

	@Override
	protected void CONNECTION_STATE_UNKNOWN_DEVICE(boolean Status) {
		// TODO Auto-generated method stub
		reflash_Status();
	}

	@Override
	protected void CONNECTION_STATE_DISCONNECTED(boolean Status) {
		// TODO Auto-generated method stub
		reflash_Status();
	}

	@Override
	protected void CONNECTION_STATE_TIMEOUT(boolean Status) {
		// TODO Auto-generated method stub
		reflash_Status();
	}

	@Override
	protected void CONNECTION_STATE_WRONG_PASSWORD(boolean Status) {
		// TODO Auto-generated method stub
		reflash_Status();
	}

	@Override
	protected void CONNECTION_STATE_CONNECT_FAILED(boolean Status) {
		// TODO Auto-generated method stub
		reflash_Status();
	}

	private void reflash_Status() {
		if (mAdapter != null)
			mAdapter.reflash_Status();
	}

	private void connected_Status(String UID) {
		if (mAdapter != null)
			mAdapter.connected_Status(UID);
	}

	@Override
	public void btn_infomation_click(PopupWindow PopupWindow) {
		// TODO Auto-generated method stub
		String versionName = "";
		try {
			versionName = this.getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
		}
		AboutDialog dlg = new AboutDialog(this, getText(R.string.dialog_AboutMe).toString(), versionName);
		dlg.setCanceledOnTouchOutside(true);
		dlg.show();

	}

	@Override
	public void btn_log_in_out_click(PopupWindow PopupWindow) {
		// TODO Auto-generated method stub
		if (MultiViewActivity.SupportDeviceOnCloud) {
			boolean isLogin = (DatabaseManager.getLoginPassword().equals("")) ? false : true;
			if (isLogin) {
				DialogInterface.OnClickListener okClick = new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						clearAllDeviceSync();
						showLoginPage();
						dialog.dismiss();
					}
				};

				DialogInterface.OnClickListener cancelClick = new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				};
				showSelectDialog(this, getText(R.string.tips_warning), getText(R.string.txt_logout_warning), getText(R.string.ok),
						getText(R.string.cancel), okClick, cancelClick);
			} else
				showLoginPage();

		}

	}

	private void showLoginPage() {
		DatabaseManager DatabaseManager = new DatabaseManager(this);
		DatabaseManager.Logout();
		Intent intent = new Intent();
		intent.setClass(MultiViewActivity.this, LoginActivity.class);
		startActivityForResult(intent, REQUEST_CODE_LOGIN);
	}

	private void clearAllDeviceSync() {
		for (int i = 0; i < CameraList.size(); i++) {
			DatabaseManager manager = new DatabaseManager(MultiViewActivity.this);
			manager.updateDeviceIsSyncByUID(CameraList.get(i).getUID(), false);
		}
	}

	@Override
	public void btn_onDropbox_click(PopupWindow PopupWindow) {
		// TODO Auto-generated method stub
		if (isSupportHardwareDecode()) {
			Intent intent = new Intent();
			intent.setClass(MultiViewActivity.this, LinkDropBoxActivity.class);
			startActivityForResult(intent, REQUEST_CODE_Dropbox_SETTING);
		}
	}
	
	@Override
	public void btn_switch_codec_config_click(PopupWindow PopupWindow) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.setClass(MultiViewActivity.this, SwitchCodecActivity.class);
		startActivityForResult(intent, REQUEST_CODE_SWITCH_CODEC);
	}

	public static void removeFromMultiView(String uid, String uuid) {
		if (mAdapter != null)
			mAdapter.remove_uid(uid, uuid);
	}

	public static boolean isSupportHardwareDecode() {
		return (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN)? true : false;
	}
	
}
