package com.tutk.P2PCam264.DELUX;

import java.io.File;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.newpie.R;
import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.AVIOCTRLDEFs.SMsgAVIoctrlGetSupportStreamReq;
import com.tutk.IOTC.Camera;
import com.tutk.IOTC.IRegisterIOTCListener;
import com.tutk.IOTC.Monitor;
import com.tutk.Logger.Glog;
import com.tutk.P2PCam264.DatabaseManager;
import com.tutk.P2PCam264.DeviceInfo;
import com.tutk.P2PCam264.EditDeviceActivity;
import com.tutk.P2PCam264.EventListActivity;
import com.tutk.P2PCam264.GridViewGalleryActivity;
import com.tutk.P2PCam264.LiveViewActivity;
import com.tutk.P2PCam264.MyCamera;
import com.tutk.P2PCam264.DELUX.Multi_Setting_custom_Dialog.On_Dialog_button_click_Listener;
@SuppressWarnings("all")
public class LiveviewFragment extends Fragment implements IRegisterIOTCListener, View.OnClickListener, On_Dialog_button_click_Listener {

	private static final String TAG = "MultiViewActivity";

	private static final int CLICK_BUTTON1 = 0;
	private static final int CLICK_BUTTON2 = 1;
	private static final int CLICK_BUTTON3 = 2;
	private static final int CLICK_BUTTON4 = 3;
	private static final int CLICK_BUTTON5 = 4;
	private static final int CLICK_BUTTON6 = 5;

	// private ArrayList<Chaanel_to_Monitor_Info> mChaanel_Info;

	private static final int MultiView_NUM = 4;

	private View layout;

	private Monitor[] marrMonitor = new Monitor[MultiView_NUM];
	private MyCamera[] marrCamera = new MyCamera[MultiView_NUM];
	private DeviceInfo[] marrDevice = new DeviceInfo[MultiView_NUM];
	private Chaanel_to_Monitor_Info[] Monitor_Info_Array = new Chaanel_to_Monitor_Info[MultiView_NUM];
	private MultiViewStatusBar[] marrStatusBar = new MultiViewStatusBar[MultiView_NUM];

	private String[] muidMatrix = new String[MultiView_NUM];
	private String[] muuidMatrix = new String[MultiView_NUM];
	private int[] mnSelChannelID = new int[MultiView_NUM];

	private int[] marrMonitorResId = { R.id.mv_monitor_0, R.id.mv_monitor_1, R.id.mv_monitor_2, R.id.mv_monitor_3,
//			R.id.mv_monitor_4,
//			R.id.mv_monitor_5,
	};

	private int[] marrMonitorPlaceBtnResId = { R.id.btn_monitor_place_1, R.id.btn_monitor_place_2, R.id.btn_monitor_place_3,
			R.id.btn_monitor_place_4,
//			R.id.btn_monitor_place_5,
//			R.id.btn_monitor_place_6,
	};
	private int[] marrStatusBarResId = { R.id.status_bar1, R.id.status_bar2, R.id.status_bar3, R.id.status_bar4,
//			R.id.status_bar5,
//			R.id.status_bar6,
	};

	private int[] SettingButtonResId = { R.id.Settingbutton1, R.id.Settingbutton2, R.id.Settingbutton3, R.id.Settingbutton4,
//			R.id.Settingbutton5,
//			R.id.Settingbutton6,
	};

	// private GaBtnHighlightOnTouchListener mGaBtnHighlightOnTouchListener = new
	// GaBtnHighlightOnTouchListener();

	public static LiveviewFragment newInstance(ArrayList<Chaanel_to_Monitor_Info> Chaanel_Info) {
		LiveviewFragment fragment = new LiveviewFragment(Chaanel_Info);
		// fragment.setArguments(param);

		return fragment;
	}

	public LiveviewFragment(ArrayList<Chaanel_to_Monitor_Info> Chaanel_Info) {
		super();
		// mChaanel_Info = Chaanel_Info;
		for (Chaanel_to_Monitor_Info Chaanel_to_Monitor_Info : Chaanel_Info) {
			Monitor_Info_Array[Chaanel_to_Monitor_Info.MonitorIndex] = Chaanel_to_Monitor_Info;
			muidMatrix[Chaanel_to_Monitor_Info.MonitorIndex] = Chaanel_to_Monitor_Info.UID;
			muuidMatrix[Chaanel_to_Monitor_Info.MonitorIndex] = Chaanel_to_Monitor_Info.UUID;
			mnSelChannelID[Chaanel_to_Monitor_Info.MonitorIndex] = Chaanel_to_Monitor_Info.ChannelIndex;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		layout = use_multi_templates(4, inflater, container);
		LocationViews();
		InitMultiView(layout);

		return layout;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	private void LocationViews() {
	}

	private void InitMultiView(View layout) {
		int nIdx = 0;
		for (String uid : muidMatrix) {

			if (nIdx >= MultiView_NUM) {
				Glog.E(TAG, "Too many camera!");
				break;
			}
			boolean bFound = false;
			for (MyCamera camera : MultiViewActivity.CameraList) {
				if (muidMatrix[nIdx] != null && muuidMatrix[nIdx] != null) {
					if (muidMatrix[nIdx].equalsIgnoreCase(camera.getUID()) && muuidMatrix[nIdx].equalsIgnoreCase(camera.getUUID())) {
						marrCamera[Monitor_Info_Array[nIdx].MonitorIndex] = camera;
						marrCamera[Monitor_Info_Array[nIdx].MonitorIndex].setMonitorIndex(Monitor_Info_Array[nIdx].MonitorIndex);
						bFound = true;
						break;
					}
				}
			}

			if (bFound) {
				bFound = false;
				for (DeviceInfo dev : MultiViewActivity.DeviceList) {
					if (muidMatrix[nIdx] != null && muuidMatrix[nIdx] != null) {
						if (muidMatrix[nIdx].equalsIgnoreCase(dev.UID) && muuidMatrix[nIdx].equalsIgnoreCase(dev.UUID)) {
							marrDevice[Monitor_Info_Array[nIdx].MonitorIndex] = dev;
							marrDevice[Monitor_Info_Array[nIdx].MonitorIndex].mMonitorIndex = Monitor_Info_Array[nIdx].MonitorIndex;
							marrDevice[Monitor_Info_Array[nIdx].MonitorIndex].ChannelIndex = mnSelChannelID[nIdx];

							bFound = true;
							break;
						}
					}
				}
			}
			nIdx++;
		}

		nIdx = 0;
		for (Monitor monitor : marrMonitor) {
			if (monitor != null) {
				monitor.deattachCamera();
			}
			if (marrCamera[nIdx] != null) {
				monitor = null;
				monitor = (Monitor) layout.findViewById(marrMonitorResId[nIdx]);
				if (monitor != null) {
					int nSelectedChannel = mnSelChannelID[nIdx];
					monitor.setPTZ(false);
					monitor.setFixXY(true);
					monitor.setMaxZoom(3.0f);
					monitor.mEnableDither = marrCamera[nIdx].mEnableDither;
					monitor.attachCamera(marrCamera[nIdx], nSelectedChannel);
					if (marrDevice[nIdx].Online) {
						monitor.setVisibility(View.VISIBLE);
					} else {
						monitor.setVisibility(View.GONE);
					}
				}
			} else {
				monitor = (Monitor) layout.findViewById(marrMonitorResId[nIdx]);
				if (monitor != null)
					monitor.setVisibility(View.INVISIBLE);
			}

			monitor.setOnClickListener(this);
			marrMonitor[nIdx] = monitor;
			nIdx++;
		}

		nIdx = 0;
		for (int nResId : marrStatusBarResId) {
			marrStatusBar[nIdx] = new MultiViewStatusBar((View) layout.findViewById(nResId));
			marrStatusBar[nIdx].InitStatusBar(nIdx, marrDevice[nIdx], this);
			nIdx++;
		}
		nIdx = 0;
		ImageButton btn = null;
		for (int nResId : marrMonitorPlaceBtnResId) {
			btn = (ImageButton) layout.findViewById(nResId);
			btn.setOnClickListener(this);
			ChangePlaceBtn(nIdx);
			nIdx++;
		}
		for (int nResId : SettingButtonResId) {
			btn = (ImageButton) layout.findViewById(nResId);
			btn.setOnClickListener(this);
		}

		nIdx = 0;
		for (MyCamera camera : marrCamera) {

			if (camera != null) {
				camera.registerIOTCListener(this);

				if (!camera.isSessionConnected()) {

					camera.connect(muidMatrix[nIdx]);
					camera.start(Camera.DEFAULT_AV_CHANNEL, marrDevice[nIdx].View_Account, marrDevice[nIdx].View_Password);
					camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETSUPPORTSTREAM_REQ,
							SMsgAVIoctrlGetSupportStreamReq.parseContent());
					camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_REQ,
							AVIOCTRLDEFs.SMsgAVIoctrlDeviceInfoReq.parseContent());
					camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETAUDIOOUTFORMAT_REQ,
							AVIOCTRLDEFs.SMsgAVIoctrlGetAudioOutFormatReq.parseContent());
					camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_TIMEZONE_REQ,
							AVIOCTRLDEFs.SMsgAVIoctrlTimeZone.parseContent());

				}
 
				// int nSelectedChannel = mnSelChannelID[nIdx];
				// camera.startShow(nSelectedChannel,true);
			}
			nIdx++;
		}

	}

	private void ChangePlaceBtn(int Index) {
		if (marrDevice[Index] == null) {
			ImageButton btn = (ImageButton) layout.findViewById(marrMonitorPlaceBtnResId[Index]);
			if (btn != null) {
				btn.setBackgroundResource(R.drawable.btn_addcam_switch);

			}
			ImageButton imagebtn = (ImageButton) layout.findViewById(SettingButtonResId[Index]);
			if (imagebtn != null) {
				imagebtn.setVisibility(View.GONE);
			}
			if (marrMonitor[Index] != null)
				marrMonitor[Index].setVisibility(View.GONE);
		} else {
			if (!marrDevice[Index].Online) {
				ImageButton btn = (ImageButton) layout.findViewById(marrMonitorPlaceBtnResId[Index]);
				if (btn != null) {
					btn.setBackgroundResource(R.drawable.btn_refresh_switch);
					btn.setVisibility(View.VISIBLE);
				}
				ImageButton imagebtn = (ImageButton) layout.findViewById(SettingButtonResId[Index]);
				if (imagebtn != null) {
					imagebtn.setVisibility(View.VISIBLE);
				}
			} else {
				ImageButton btn = (ImageButton) layout.findViewById(marrMonitorPlaceBtnResId[Index]);
				if (btn != null) {
					btn.setBackgroundResource(R.drawable.btn_refresh_switch);
					btn.setVisibility(View.VISIBLE);
				}
				ImageButton imagebtn = (ImageButton) layout.findViewById(SettingButtonResId[Index]);
				if (imagebtn != null) {
					imagebtn.setVisibility(View.VISIBLE);
				}
			}
		}
	}

	private View use_multi_templates(int Type, LayoutInflater inflater, ViewGroup container) {
		switch (Type) {
		case 1:
			return inflater.inflate(R.layout.multi_monitor_1, container, false);
		case 2:
			return inflater.inflate(R.layout.multi_monitor_2, container, false);
		case 3:
			return inflater.inflate(R.layout.multi_monitor_3, container, false);
		case 4:
			return inflater.inflate(R.layout.multi_monitor_4, container, false);
		case 5:
			return inflater.inflate(R.layout.multi_monitor_5, container, false);
		case 6:
			return inflater.inflate(R.layout.multi_monitor_6, container, false);
		default:
			return inflater.inflate(R.layout.multi_monitor_6, container, false);

		}
	}

	@Override
	public void receiveFrameData(Camera camera, int avChannel, Bitmap bmp) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveFrameInfo(Camera camera, int avChannel, long bitRate, int frameRate, int onlineNm, int frameCount, int incompleteFrameCount) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveSessionInfo(Camera camera, int resultCode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveChannelInfo(Camera camera, int avChannel, int resultCode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveIOCtrlData(Camera camera, int avChannel, int avIOCtrlMsgType, byte[] data) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPause() {
		super.onPause();

		stop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stop();
	}

	@Override
	public void onResume() {
		super.onResume();
		start();
	}

	// 暫停所有畫面
	public void stop() {
		int nIdx = 0;
		for (MyCamera camera : marrCamera) {
			if (camera != null)
				stoping_monitor(camera, marrMonitor[nIdx], marrDevice[nIdx], mnSelChannelID[nIdx], true);
			nIdx++;
		}
	}

	// 暫停指定channel畫面
	public void stopOneChannel(int Index) {
		stopOneChannel(Index, true);
	}

	public void stopOneChannel(int Index, boolean seedStopShow) {
		stoping_monitor(marrCamera[Index], marrMonitor[Index], marrDevice[Index], mnSelChannelID[Index], seedStopShow);
	}

	// IOTC_StartShow: 當有同多個monitor載入同一個camera 及同一個channel 不能執行 stopshow
	private void stoping_monitor(MyCamera Camera, Monitor Monitor, DeviceInfo Device, int SelectedChannel, boolean IOTC_StopShow) {
		if (Camera != null) {
			if (IOTC_StopShow) {
				// Camera.stopSpeaking(SelectedChannel);
				// Camera.stopListening(SelectedChannel);
				Camera.stopShow(SelectedChannel);
			}

			if (Monitor != null)
				Monitor.deattachCamera();
		}
	}

	// 確認目前有無多個Monitor綁定同一個UID同一個channel
	private boolean Check_Same_channel_toMonitor(String UID, String UUID, int ChannelIndex) {
		int SameCount = 0;
		for (int i = 0; i < marrCamera.length; i++) {
			if (marrDevice[i] != null) {
				if (marrDevice[i].UID.equalsIgnoreCase(UID) && marrDevice[i].UUID.equalsIgnoreCase(UUID) && mnSelChannelID[i] == ChannelIndex) {
					SameCount++;
				}
			}
		}
		if (SameCount > 1) {
			return true;
		} else {
			return false;
		}
	}

	// 啟動所有畫面
	public void start() {
		int nIdx = 0;
		for (MyCamera camera : marrCamera) {
			if (camera != null)
				playing_monitor(camera, marrMonitor[nIdx], marrDevice[nIdx], nIdx);
			nIdx++;
		}
	}

	// 啟動指定channel的畫面
	public void startOneChannel(int Index) {
		playing_monitor(marrCamera[Index], marrMonitor[Index], marrDevice[Index], Index);
	}

	// 啟動指定uid的畫面
	public void startOneUID(String UID) {
		int nIdx = 0;
		for (DeviceInfo device : marrDevice) {
			if (device != null) {
				if (device.UID.equalsIgnoreCase(UID)) {
					// playing_monitor(marrCamera[nIdx],marrMonitor[nIdx],marrDevice[nIdx],nIdx);
					// int nSelectedChannel = mnSelChannelID[nIdx];

					// if (marrMonitor[nIdx] != null) {
					// marrMonitor[nIdx].mEnableDither = marrCamera[nIdx].mEnableDither;
					// marrMonitor[nIdx].attachCamera(marrCamera[nIdx], nSelectedChannel);
					// }
				}
			}
			nIdx++;
		}
	}

	private void playing_monitor(final MyCamera Camera, Monitor Monitor, DeviceInfo Device, int Index) {
		if (Camera != null) {
			final int nSelectedChannel = mnSelChannelID[Index];

			if (Monitor != null) {
				// if(!Monitor.mEnableDither)
				// {
				Monitor.mEnableDither = Camera.mEnableDither;
				Monitor.attachCamera(Camera, nSelectedChannel);
				// }
				Camera.startShow(nSelectedChannel, true, true);
				if(!Camera.mIsShow){
					new Handler().postDelayed(new Runnable() {						
						@Override
						public void run() {
							Camera.startShow(nSelectedChannel, true, true);
						}
					}, 3000);
				}
			}
		}
	}

	// 每當連線狀態更新時 需要更新狀態攔
	public void reflash_Status() {
		int nIdx = 0;
		for (MultiViewStatusBar MultiViewStatusBar : marrStatusBar) {
			if (MultiViewStatusBar != null) {
				MultiViewStatusBar.ChangeStatusBar(nIdx, marrDevice[nIdx], marrMonitor[nIdx]);
				if(marrDevice[nIdx] != null && marrDevice[nIdx].Online) {
					int nSelectedChannel = mnSelChannelID[nIdx];
					marrCamera[nIdx].startShow(nSelectedChannel, true, true);
				}
				ChangePlaceBtn(nIdx);
			}
			nIdx++;
		}

	}

	// 特定uid連線成功時
	public void connected_Status(String UID) {
		// startOneUID(UID);
	}

	private void create_dialog(On_Dialog_button_click_Listener _On_button_click_Listener, int index, boolean device_online) {
		Multi_Setting_custom_Dialog Multi_Setting_custom_Dialog = new Multi_Setting_custom_Dialog(getActivity(), index, device_online);
		Multi_Setting_custom_Dialog.set_button_click_Listener(this);
		Window window = Multi_Setting_custom_Dialog.getWindow();
		window.setWindowAnimations(R.style.setting_dailog_animstyle);
		Multi_Setting_custom_Dialog.show();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.Settingbutton1:
			Glog.D(TAG, "ClickSettingButton( " + 1 + " )...");
			create_dialog(this, CLICK_BUTTON1, marrDevice[CLICK_BUTTON1].Online);
			break;
		case R.id.Settingbutton2:
			Glog.D(TAG, "ClickSettingButton( " + 2 + " )...");
			create_dialog(this, CLICK_BUTTON2, marrDevice[CLICK_BUTTON2].Online);
			break;
		case R.id.Settingbutton3:
			Glog.D(TAG, "ClickSettingButton( " + 3 + " )...");
			create_dialog(this, CLICK_BUTTON3, marrDevice[CLICK_BUTTON3].Online);
			break;
		case R.id.Settingbutton4:
			Glog.D(TAG, "ClickSettingButton( " + 4 + " )...");
			create_dialog(this, CLICK_BUTTON4, marrDevice[CLICK_BUTTON4].Online);
			break;
		case R.id.Settingbutton5:
			Glog.D(TAG, "ClickSettingButton( " + 5 + " )...");
			create_dialog(this, CLICK_BUTTON5, marrDevice[CLICK_BUTTON5].Online);
			break;
		case R.id.Settingbutton6:
			Glog.D(TAG, "ClickSettingButton( " + 6 + " )...");
			create_dialog(this, CLICK_BUTTON6, marrDevice[CLICK_BUTTON6].Online);
			break;
		case R.id.btn_monitor_place_1:
			IntentDeviceList(CLICK_BUTTON1);
			break;
		case R.id.btn_monitor_place_2:
			IntentDeviceList(CLICK_BUTTON2);
			break;
		case R.id.btn_monitor_place_3:
			IntentDeviceList(CLICK_BUTTON3);
			break;
		case R.id.btn_monitor_place_4:
			IntentDeviceList(CLICK_BUTTON4);
			break;
		case R.id.btn_monitor_place_5:
			IntentDeviceList(CLICK_BUTTON5);
			break;
		case R.id.btn_monitor_place_6:
			IntentDeviceList(CLICK_BUTTON6);
			break;
		case R.id.mv_monitor_0:
			IntentLiveView(CLICK_BUTTON1);
			break;
		case R.id.mv_monitor_1:
			IntentLiveView(CLICK_BUTTON2);
			break;
		case R.id.mv_monitor_2:
			IntentLiveView(CLICK_BUTTON3);
			break;
		case R.id.mv_monitor_3:
			IntentLiveView(CLICK_BUTTON4);
			break;
		case R.id.mv_monitor_4:
			IntentLiveView(CLICK_BUTTON5);
			break;
		case R.id.mv_monitor_5:
			IntentLiveView(CLICK_BUTTON6);
			break;
		}
	}

	// 塞入Monitor新的畫面
	public void SetMonitor(Chaanel_to_Monitor_Info Chaanel_to_Monitor_Info, int MonitorIndex) {
		// 如果原本不為空 先停止撥放原本的
		if (marrDevice[MonitorIndex] != null)
			stoping_monitor(marrCamera[MonitorIndex], marrMonitor[MonitorIndex], marrDevice[MonitorIndex], mnSelChannelID[MonitorIndex],
					!Check_Same_channel_toMonitor(marrDevice[MonitorIndex].UID, marrDevice[MonitorIndex].UUID, mnSelChannelID[MonitorIndex]));

		int old_mnSelChannelID = mnSelChannelID[MonitorIndex];

		muidMatrix[MonitorIndex] = Chaanel_to_Monitor_Info.UID;
		muuidMatrix[MonitorIndex] = Chaanel_to_Monitor_Info.UUID;
		mnSelChannelID[MonitorIndex] = Chaanel_to_Monitor_Info.ChannelIndex;

		if (muidMatrix[MonitorIndex] != null && muuidMatrix[MonitorIndex] != null) {
			for (MyCamera camera : MultiViewActivity.CameraList) {
				if (muidMatrix[MonitorIndex].equalsIgnoreCase(camera.getUID()) && muuidMatrix[MonitorIndex].equalsIgnoreCase(camera.getUUID())) {
					marrCamera[MonitorIndex] = null;
					marrCamera[MonitorIndex] = camera;
					marrCamera[MonitorIndex].setMonitorIndex(Chaanel_to_Monitor_Info.MonitorIndex);
					break;
				}
			}
			for (DeviceInfo dev : MultiViewActivity.DeviceList) {

				if (muidMatrix[MonitorIndex].equalsIgnoreCase(dev.UID) && muuidMatrix[MonitorIndex].equalsIgnoreCase(dev.UUID)
						&& mnSelChannelID[MonitorIndex] == old_mnSelChannelID) {
					marrDevice[MonitorIndex] = null;
					marrDevice[MonitorIndex] = dev;
					marrDevice[MonitorIndex].ChannelIndex = Chaanel_to_Monitor_Info.ChannelIndex;
					marrDevice[MonitorIndex].mMonitorIndex = Chaanel_to_Monitor_Info.MonitorIndex;
					break;
				}
			}
		} else {
			marrCamera[MonitorIndex] = null;
			marrDevice[MonitorIndex] = null;

		}
		if (marrMonitor[MonitorIndex] != null) {
			marrMonitor[MonitorIndex].deattachCamera();
		}
		if (marrCamera[MonitorIndex] != null) {

			int nSelectedChannel = mnSelChannelID[MonitorIndex];

			marrMonitor[MonitorIndex] = null;
			marrMonitor[MonitorIndex] = (Monitor) layout.findViewById(marrMonitorResId[MonitorIndex]);
			if (marrMonitor[MonitorIndex] != null) {
				marrMonitor[MonitorIndex].setPTZ(false);
				marrMonitor[MonitorIndex].setFixXY(true);
				marrMonitor[MonitorIndex].setMaxZoom(3.0f);
				marrMonitor[MonitorIndex].mEnableDither = marrCamera[MonitorIndex].mEnableDither;
				marrMonitor[MonitorIndex].attachCamera(marrCamera[MonitorIndex], nSelectedChannel);
			}
		} else {
			marrMonitor[MonitorIndex] = (Monitor) layout.findViewById(marrMonitorResId[MonitorIndex]);
			if (marrMonitor[MonitorIndex] != null)
				marrMonitor[MonitorIndex].setVisibility(View.INVISIBLE);
		}
		if (marrMonitor[MonitorIndex] != null) {
			marrMonitor[MonitorIndex].setOnClickListener(this);
		}
		if (marrCamera[MonitorIndex] != null) {
			marrCamera[MonitorIndex].registerIOTCListener(this);

			if (!marrCamera[MonitorIndex].isSessionConnected()) {

				marrCamera[MonitorIndex].connect(muidMatrix[MonitorIndex]);
				marrCamera[MonitorIndex].start(Camera.DEFAULT_AV_CHANNEL, marrDevice[MonitorIndex].View_Account,
						marrDevice[MonitorIndex].View_Password);
				marrCamera[MonitorIndex].sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETSUPPORTSTREAM_REQ,
						SMsgAVIoctrlGetSupportStreamReq.parseContent());
				marrCamera[MonitorIndex].sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_REQ,
						AVIOCTRLDEFs.SMsgAVIoctrlDeviceInfoReq.parseContent());
				marrCamera[MonitorIndex].sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETAUDIOOUTFORMAT_REQ,
						AVIOCTRLDEFs.SMsgAVIoctrlGetAudioOutFormatReq.parseContent());
				marrCamera[MonitorIndex].sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_TIMEZONE_REQ,
						AVIOCTRLDEFs.SMsgAVIoctrlTimeZone.parseContent());
				if (MultiViewActivity.SupportOnDropbox) {
					marrCamera[MonitorIndex].sendIOCtrl(MyCamera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_SAVE_DROPBOX_REQ,
							"0".getBytes());
				}
			}

			int nSelectedChannel = mnSelChannelID[MonitorIndex];
			marrCamera[MonitorIndex].startShow(nSelectedChannel, true, true);
		}
		reflash_Status();
	}

	public void DeleteAllMonitor(String uid, String uuid) {
		int index = 0;
		// 同一台camera可能對應好幾個monitor 故要搜尋
		for (MyCamera camera : marrCamera) {
			if (camera != null) {
				if (camera.getUID().equalsIgnoreCase(uid) && camera.getUUID().equalsIgnoreCase(uuid)) {
					StopAndClearMonitor(index);

				}
			}
			index++;
		}

	}

	public void DeleteOneMonitor(String uid, String uuid, int MonitorIndex) {
		DeleteOneMonitor(uid, uuid, MonitorIndex, true);
	}

	public void DeleteOneMonitor(String uid, String uuid, int MonitorIndex, boolean seedStopShow) {
		if (marrCamera[MonitorIndex] == null)
			return;
		if (marrCamera[MonitorIndex].getUID().equalsIgnoreCase(uid) && marrCamera[MonitorIndex].getUUID().equalsIgnoreCase(uuid))
			StopAndClearMonitor(MonitorIndex, seedStopShow);
	}

	private void StopAndClearMonitor(int index) {
		StopAndClearMonitor(index, true);
	}

	// 取消綁定Monitor 要把Monitor清掉
	private void StopAndClearMonitor(int index, boolean seedStopShow) {
		stopOneChannel(index, seedStopShow);
		DatabaseManager DatabaseManager = new DatabaseManager(getActivity());
		DatabaseManager.remove_Device_Channel_Allonation_To_MonitorByUID(marrCamera[index].getUID(), mnSelChannelID[index], index);
		DatabaseManager = null;
		muidMatrix[index] = null;
		muuidMatrix[index] = null;
		mnSelChannelID[index] = 0;
		marrCamera[index] = null;
		marrDevice[index] = null;
		Monitor_Info_Array[index] = null;
		marrStatusBar[index].ChangeStatusBar(index, marrDevice[index], marrMonitor[index]);
		ChangePlaceBtn(index);
	}

	// 前往liveview
	private void IntentLiveView(int index) {
		if (marrDevice[index] == null)
			return;

		if (marrCamera[index].mUID == null)
			marrCamera[index].mUID = marrDevice[index].UID;
		Bundle extras = new Bundle();
		extras.putString("dev_uid", marrDevice[index].UID);
		extras.putString("dev_uuid", marrDevice[index].UUID);
		extras.putString("dev_nickname", marrDevice[index].NickName);
		extras.putString("conn_status", marrDevice[index].Status);
		extras.putString("view_acc", marrDevice[index].View_Account);
		extras.putString("view_pwd", marrDevice[index].View_Password);
		extras.putInt("camera_channel", mnSelChannelID[index]);
		extras.putInt("MonitorIndex", index);

		extras.putString("OriginallyUID", marrDevice[index].UID);
		extras.putInt("OriginallyChannelIndex", mnSelChannelID[index]);
		Intent intent = new Intent();
		intent.putExtras(extras);
		intent.setClass(getActivity(), LiveViewActivity.class);
		getActivity().startActivityForResult(intent, MultiViewActivity.REQUEST_CODE_CAMERA_VIEW);
	}

	// 前往devicelist
	private void IntentDeviceList(int index) {
		// 沒有綁定機器 跳去adddevice
		if (marrDevice[index] == null) {
			Intent intent = new Intent();
			Bundle extras = new Bundle();
			extras.putString("OriginallyUID", null);
			extras.putInt("OriginallyChannelIndex", 0);
			extras.putInt("MonitorIndex", index);
			intent.setClass(getActivity(), DeviceListActivity.class);
			intent.putExtras(extras);
			getActivity().startActivityForResult(intent, MultiViewActivity.REQUEST_CODE_CAMERA_SELECT_MONITOR);
		}
		// 已綁定機器 重新連線
		else {
			if (marrDevice[index].Status.equals(getText(R.string.connstus_unknown_device).toString())
					|| marrDevice[index].Status.equals(getText(R.string.connstus_disconnect).toString())
					|| marrDevice[index].Status.equals(getText(R.string.connstus_connection_failed).toString())) {
				stopOneChannel(index);
				marrCamera[index].disconnect();
				marrCamera[index].connect(marrDevice[index].UID);
				marrCamera[index].start(Camera.DEFAULT_AV_CHANNEL, marrDevice[index].View_Account, marrDevice[index].View_Password);
				marrCamera[index].sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_REQ,
						AVIOCTRLDEFs.SMsgAVIoctrlDeviceInfoReq.parseContent());
				marrCamera[index].sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETSUPPORTSTREAM_REQ,
						AVIOCTRLDEFs.SMsgAVIoctrlGetSupportStreamReq.parseContent());
				marrCamera[index].sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETAUDIOOUTFORMAT_REQ,
						AVIOCTRLDEFs.SMsgAVIoctrlGetAudioOutFormatReq.parseContent());
				marrCamera[index].sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_TIMEZONE_REQ,
						AVIOCTRLDEFs.SMsgAVIoctrlTimeZone.parseContent());
				startOneChannel(index);
			}
		}

	}

	@Override
	public void cancel_click(DialogInterface Dialog) {
		// TODO Auto-generated method stub
	}

	@Override
	public void btn_changech_click(DialogInterface Dialog, int index) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		Bundle extras = new Bundle();
		if (marrDevice[index] == null) {
			extras.putString("OriginallyUID", null);
			extras.putInt("OriginallyChannelIndex", 0);
		} else {
			extras.putString("OriginallyUID", marrDevice[index].UID);
			extras.putInt("OriginallyChannelIndex", mnSelChannelID[index]);
		}
		extras.putInt("MonitorIndex", index);
		intent.setClass(getActivity(), DeviceListActivity.class);
		intent.putExtras(extras);
		getActivity().startActivityForResult(intent, MultiViewActivity.REQUEST_CODE_CAMERA_SELECT_MONITOR);
	}

	@Override
	public void btn_event_click(DialogInterface Dialog, int index) {
		// TODO Auto-generated method stub

		if (marrDevice[index] == null)
			return;
		Bundle extras = new Bundle();
		extras.putString("dev_uid", marrDevice[index].UID);
		extras.putString("dev_uuid", marrDevice[index].UUID);
		extras.putString("dev_nickname", marrDevice[index].NickName);
		extras.putString("conn_status", marrDevice[index].Status);
		extras.putString("view_acc", marrDevice[index].View_Account);
		extras.putString("view_pwd", marrDevice[index].View_Password);
		extras.putInt("camera_channel", mnSelChannelID[index]);
		Intent intent = new Intent();
		intent.putExtras(extras);
		intent.setClass(getActivity(), EventListActivity.class);
		getActivity().startActivity(intent);
	}

	@Override
	public void btn_photo_click(DialogInterface Dialog, int index) {
		// TODO Auto-generated method stub
		if (marrDevice[index] == null)
			return;
		File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Snapshot/" + marrDevice[index].UID);
		File folder_video = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Record/" + marrDevice[index].UID);
		String[] allFiles = folder.list();
		String[] allVideos = folder_video.list();
		if ((allFiles != null && allFiles.length > 0) | (allVideos != null && allVideos.length > 0)) {
			Intent intent2 = new Intent(getActivity(), GridViewGalleryActivity.class);
			intent2.putExtra("snap", marrDevice[index].UID);
			intent2.putExtra("images_path", folder.getAbsolutePath());
			intent2.putExtra("videos_path", folder_video.getAbsolutePath());
			getActivity().startActivity(intent2);
		} else {
			String msg = getActivity().getText(R.string.tips_no_snapshot_found).toString();
			Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void btn_set_click(DialogInterface Dialog, int index) {
		// TODO Auto-generated method stub
		if (marrDevice[index] == null)
			return;
		Bundle extras = new Bundle();
		extras.putString("dev_uid", marrDevice[index].UID);
		extras.putString("dev_uuid", marrDevice[index].UUID);
		extras.putString("dev_nickname", marrDevice[index].NickName);
		extras.putString("conn_status", marrDevice[index].Status);
		extras.putString("view_acc", marrDevice[index].View_Account);
		extras.putString("view_pwd", marrDevice[index].View_Password);
		extras.putInt("camera_channel", mnSelChannelID[index]);
		Intent intent = new Intent();
		intent.putExtras(extras);
		intent.setClass(getActivity(), EditDeviceActivity.class);
		getActivity().startActivityForResult(intent, MultiViewActivity.REQUEST_CODE_CAMERA_EDIT);

	}

	@Override
	public void btn_delete_click(DialogInterface Dialog, final int index) {
		// TODO Auto-generated method stub
		new AlertDialog.Builder(getActivity()).setIcon(android.R.drawable.ic_dialog_alert).setTitle(getText(R.string.tips_warning))
				.setMessage(getText(R.string.tips_remove_camera_monitor_confirm))
				.setPositiveButton(getText(R.string.ok), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						DeleteOneMonitor(marrDevice[index].UID, marrDevice[index].UUID, index,
								!Check_Same_channel_toMonitor(marrDevice[index].UID, marrDevice[index].UUID, mnSelChannelID[index]));
					}
				}).setNegativeButton(getText(R.string.cancel), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				}).show();
	}

	@Override
	public void receiveFrameDataForMediaCodec(Camera camera, int avChannel, byte[] buf, int length, int pFrmNo, byte[] pFrmInfoBuf, boolean isIframe, int codecId) {
		// TODO Auto-generated method stub

	}
}
