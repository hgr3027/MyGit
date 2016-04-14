package com.tutk.P2PCam264;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.example.newpie.R;
import com.tutk.IOTC.AVFrame;
import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.AVIOCTRLDEFs.SMsgAVIoctrlGetSupportStreamReq;
import com.tutk.IOTC.AVIOCTRLDEFs.SStreamDef;
import com.tutk.IOTC.Camera;
import com.tutk.IOTC.CameraListener;
import com.tutk.IOTC.IMonitor;
import com.tutk.IOTC.IOTCAPIs;
import com.tutk.IOTC.IRegisterIOTCListener;
import com.tutk.IOTC.MediaCodecListener;
import com.tutk.IOTC.MediaCodecMonitor;
import com.tutk.IOTC.MonitorClickListener;
import com.tutk.IOTC.St_SInfo;
import com.tutk.Logger.Glog;
import com.tutk.P2PCam264.DELUX.MultiViewActivity;

@SuppressLint("all")
public class LiveViewActivity extends SherlockActivity implements
		ViewSwitcher.ViewFactory, IRegisterIOTCListener, MonitorClickListener,
		android.view.View.OnTouchListener, CameraListener, MediaCodecListener {

	private static final int BUILD_VERSION_CODES_ICE_CREAM_SANDWICH = 14;
	private static final int STS_CHANGE_CHANNEL_STREAMINFO = 99;
	private static final int STS_SNAPSHOT_SCANED = 98;
	// private static final String FILE_TYPE = "image/*";

	private static final int REQUEST_CODE_ALBUM = 99;

	private static final int OPT_MENU_ITEM_ALBUM = Menu.FIRST;
	private static final int OPT_MENU_ITEM_SNAPSHOT = Menu.FIRST + 1;
	private static final int OPT_MENU_ITEM_AUDIOCTRL = Menu.FIRST + 4;
	private static final int OPT_MENU_ITEM_AUDIO_IN = Menu.FIRST + 5;
	private static final int OPT_MENU_ITEM_AUDIO_OUT = Menu.FIRST + 6;

	private final int THUMBNAIL_LIMIT_HEIGHT = 720;
	private final int THUMBNAIL_LIMIT_WIDTH = 1280;
	
	private final int ROTATEMODE_NORMAL = 0;
	private final int ROTATEMODE_FLIP = 1;
	private final int ROTATEMODE_MIRROR = 2;
	private final int ROTATEMODE_FLIPANDMIRROR = 3;
	
	

//	private IMonitor monitor = null;
	private IMonitor mSoftMonitor = null;
	private IMonitor mHardMonitor = null;
	private MyCamera mCamera = null;
	private DeviceInfo mDevice = null;
	// private int SOPTZOOM=-1;

	private int mMonitorIndex = -1;
	private int OriginallyChannelIndex = -1;

	private TextView txt_title;
	private String mDevUID;
	private String mDevUUID;
	private String mConnStatus = "";
	private String mPhotoFilePath = "";
	private String mVideoFilePath = "";
	private int mVideoFPS;
	private long mVideoBPS;
	private int mOnlineNm;
	private int mFrameCount;
	private int mIncompleteFrameCount;
	private int mVideoWidth;
	private int mVideoHeight;
	private int mMiniVideoWidth;
	private int mMiniVideoHeight;
	private int mSelectedChannel;
	private int mRotateMode = ROTATEMODE_NORMAL;
	

	private RelativeLayout toolbar_layout;
	private RelativeLayout linSpeaker;
	private LinearLayout linPnlCameraInfo;
	private RelativeLayout linQVGA;
	private RelativeLayout linPnlemode;
	private LinearLayout nullLayout;
	private RelativeLayout mSoftMonitorLayout;
	private VerticalScrollView myScrollView;
	private HorizontalScrollView myHorScrollView;

	private boolean LevelFlip = false;
	private boolean VerticalFlip = false;
	private boolean isOpenLinQVGA = false;
	private boolean isOpenLinEmode = false;
	private boolean isShowToolBar = true;

	// TOOLBAR
	private HorizontalScrollView toolbarScrollView;
	private ImageButton button_toolbar_speaker;
	private ImageButton button_toolbar_recording;
	private ImageButton button_toolbar_snapshot;
	private ImageButton button_toolbar_mirror;
	private ImageButton button_toolbar_mirror_rl;
	private ImageButton button_toolbar_SET;
	private ImageButton button_toolbar_QVGA;
	private ImageButton button_toolbar_EMODE;

	private ImageButton btn_speaker;

	private TextView txtConnectionSlash;
	private TextView txtResolutionSlash;
	private TextView txtShowFPS;
	private TextView txtFPSSlash;
	private TextView txtShowBPS;
	private TextView txtOnlineNumberSlash;
	private TextView txtShowFrameRatio;
	private TextView txtFrameCountSlash;
	private TextView txtQuality;
	private TextView txtRecvFrmPreSec;
	private TextView txtRecvFrmSlash;
	private TextView txtDispFrmPreSeco;

	private TextView txtConnectionStatus;
	private TextView txtConnectionMode;
	private TextView txtResolution;
	private TextView txtFrameRate;
	private TextView txtBitRate;
	private TextView txtOnlineNumber;
	private TextView txtFrameCount;
	private TextView txtIncompleteFrameCount;
	private TextView txtPerformance;
	private TextView mCHTextView;
	private TextView txtCodecMode;

	private boolean mIsListening = false;
	private boolean mIsSpeaking = false;
	private boolean mIsRecording = false;

	private BitmapDrawable bg;
	private BitmapDrawable bgSplit;

	private ImageButton CH_button;

	private Button qvga_button1;
	private Button qvga_button2;
	private Button qvga_button3;
	private Button qvga_button4;
	private Button qvga_button5;
	private Button emode_button1;
	private Button emode_button2;
	private Button emode_button3;
	private Button emode_button4;

	private Context mContext;
	private boolean unavailable = false;
	private boolean mSoftCodecDefault = false;
	private boolean mSoftCodecCurrent = false;
	
enum FrameMode {
	    PORTRAIT, LANDSCAPE_ROW_MAJOR, LANDSCAPE_COL_MAJOR
	}  
	
	private FrameMode mFrameMode = FrameMode.PORTRAIT;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		mContext = this;

		SharedPreferences settings = this.getSharedPreferences("CodecConfig", 0);
		mSoftCodecDefault = settings.getBoolean("isSoftCodec", false);
		
//		setContentView(R.layout.live_view_portrait);

		bg = (BitmapDrawable) getResources().getDrawable(R.drawable.bg_striped);
		bgSplit = (BitmapDrawable) getResources().getDrawable(
				R.drawable.bg_striped_split_img);

		Bundle bundle = this.getIntent().getExtras();
		mDevUID = bundle.getString("dev_uid");
		mDevUUID = bundle.getString("dev_uuid");
		mConnStatus = bundle.getString("conn_status");
		mSelectedChannel = bundle.getInt("camera_channel");
		OriginallyChannelIndex = bundle.getInt("camera_channel");
		mMonitorIndex = bundle.getInt("MonitorIndex");

		for (MyCamera camera : MultiViewActivity.CameraList) {

			if (mDevUID.equalsIgnoreCase(camera.getUID())
					&& mDevUUID.equalsIgnoreCase(camera.getUUID())) {
				mCamera = camera;
				break;
			}
		}

		for (DeviceInfo dev : MultiViewActivity.DeviceList) {

			if (mDevUID.equalsIgnoreCase(dev.UID)
					&& mDevUUID.equalsIgnoreCase(dev.UUID)) {
				mDevice = dev;
				break;
			}
		}

		// Configuration cfg = getResources().getConfiguration();
		//
		// if (cfg.orientation == Configuration.ORIENTATION_PORTRAIT) {
		// setupViewInPortraitLayout();
		// } else if (cfg.orientation == Configuration.ORIENTATION_LANDSCAPE) {
		// setupViewInLandscapeLayout();
		// }

		if (mCamera != null) {

			mCamera.registerIOTCListener(this);
			mCamera.SetCameraListener(this);

			if (!mCamera.isSessionConnected()) {

				mCamera.connect(mDevUID);
				mCamera.start(Camera.DEFAULT_AV_CHANNEL, mDevice.View_Account,
						mDevice.View_Password);
				mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
						AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETSUPPORTSTREAM_REQ,
						SMsgAVIoctrlGetSupportStreamReq.parseContent());
				mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
						AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_REQ,
						AVIOCTRLDEFs.SMsgAVIoctrlDeviceInfoReq.parseContent());
				mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
						AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETAUDIOOUTFORMAT_REQ,
						AVIOCTRLDEFs.SMsgAVIoctrlGetAudioOutFormatReq
								.parseContent());
				mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
						AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_TIMEZONE_REQ,
						AVIOCTRLDEFs.SMsgAVIoctrlTimeZone.parseContent());
			}
//			mCamera.startShow(mSelectedChannel, true, mSoftCodecDefault);

			/*
			 * if (mCamera.LastAudioMode == 1) {
			 * mCamera.startListening(mSelectedChannel); mIsListening = true; }
			 * if (mCamera.LastAudioMode == 2) {
			 * mCamera.startSpeaking(mSelectedChannel); mIsSpeaking = true; }
			 */
		}

		// Inflate the custom view
		final View audioView = LayoutInflater.from(this).inflate(
				R.layout.two_way_audio, null);

		// Bind to its state change
		((RadioGroup) audioView.findViewById(R.id.radioAudio))
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {

						if (checkedId == audioView.findViewById(
								R.id.radioSpeaker).getId()) {

							if (mCamera.isChannelConnected(mSelectedChannel)) {
								mCamera.stopSpeaking(mSelectedChannel);
								mCamera.startListening(mSelectedChannel,
										mIsRecording);
								mIsListening = true;
								mIsSpeaking = false;
							}

						} else if (checkedId == audioView.findViewById(
								R.id.radioMicrophone).getId()) {

							if (mCamera.isChannelConnected(mSelectedChannel)) {
								mCamera.stopListening(mSelectedChannel);
								mCamera.startSpeaking(mSelectedChannel);
								mIsListening = false;
								mIsSpeaking = true;
							}
						}
					}
				});

		// getSupportActionBar().setCustomView(audioView);
		// getSupportActionBar().setDisplayShowCustomEnabled(true);
	}

	@Override
	protected void onStart() {
		super.onStart();
		// FlurryAgent.onStartSession(this, "Q1SDXDZQ21BQMVUVJ16W");
	}

	@Override
	protected void onStop() {
		super.onStop();
		// FlurryAgent.onEndSession(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (mSoftMonitor != null)
			mSoftMonitor.deattachCamera();
		if (mHardMonitor != null)
			mHardMonitor.deattachCamera();
		
		if (mCamera != null) {
			mCamera.unregisterIOTCListener(this);
			mCamera.stopSpeaking(mSelectedChannel);
			mCamera.stopListening(mSelectedChannel);
			mCamera.stopShow(mSelectedChannel);
		}
		if (mIsRecording) {
			// mIsSpeaking = false;
			// mIsListening = false;
			mCamera.stopRecording();
			button_toolbar_recording
					.setBackgroundResource(R.drawable.btn_recording_switch_start);
			mIsRecording = false;
		}

		
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (mCamera != null) {
			mCamera.registerIOTCListener(this);

			mCamera.startShow(mSelectedChannel, true, mSoftCodecDefault);

			if (mIsListening)
				mCamera.startListening(mSelectedChannel, mIsRecording);
			if (mIsSpeaking)
				mCamera.startSpeaking(mSelectedChannel);
		}

		Configuration cfg = getResources().getConfiguration();

		if (cfg.orientation == Configuration.ORIENTATION_PORTRAIT) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					setupPortraitView(mSoftCodecDefault);
				}
			});
		} else if (cfg.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					setupLandscapeView(mSoftCodecDefault);
				}
			});
		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		
		if (mSoftMonitor != null)
			mSoftMonitor.deattachCamera();
		if (mHardMonitor != null)
			mHardMonitor.deattachCamera();

		Configuration cfg = getResources().getConfiguration();

		if (cfg.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			if (unavailable)
				setupLandscapeView(true);
			else
				setupLandscapeView(mSoftCodecCurrent);

		} else if (cfg.orientation == Configuration.ORIENTATION_PORTRAIT) {
			if (unavailable)
				setupPortraitView(true);
			else
				setupPortraitView(mSoftCodecCurrent);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_CODE_ALBUM) {
			if(mSoftCodecCurrent) {
				mSoftMonitor = (IMonitor) findViewById(R.id.softMonitor);
				mSoftMonitor.setMaxZoom(3.0f);
				mSoftMonitor.enableDither(mCamera.mEnableDither);
				mSoftMonitor.attachCamera(mCamera, mSelectedChannel);
			}
			else {
				mHardMonitor = (IMonitor) findViewById(R.id.hardMonitor);
				mHardMonitor.setMaxZoom(3.0f);
				mHardMonitor.enableDither(mCamera.mEnableDither);
				mHardMonitor.attachCamera(mCamera, mSelectedChannel);
			}
		}

		else if (requestCode == MultiViewActivity.REQUEST_CODE_CAMERA_EDIT) {

			txt_title.setText(mDevice.NickName);
			switch (resultCode) {
			case MultiViewActivity.REQUEST_CODE_CAMERA_EDIT_DELETE_OK:
				Bundle extras = data.getExtras();
				Intent Intent = new Intent();
				Intent.putExtras(extras);
				setResult(MultiViewActivity.REQUEST_CODE_CAMERA_EDIT_DELETE_OK,
						Intent);
				finish();

				break;
			}
		}
	}

	private void setupLandscapeView(final boolean runSoftwareDecode) {

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().clearFlags(
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		ActionBar actionBar = getActionBar();
		actionBar.hide();

		setContentView(R.layout.live_view_landscape_mix);
		
		mSoftCodecCurrent = false;
		if (runSoftwareDecode)
			mSoftCodecCurrent = true;


		toolbarScrollView = (HorizontalScrollView) findViewById(R.id.ScrollView_toolbar);
		toolbarScrollView.setHorizontalFadingEdgeEnabled(false);
		toolbar_layout = (RelativeLayout) findViewById(R.id.toolbar_layout);

		nullLayout = (LinearLayout) findViewById(R.id.null_layout);

		linPnlCameraInfo = (LinearLayout) findViewById(R.id.pnlCameraInfo);

		linQVGA = (RelativeLayout) findViewById(R.id.qvga_layout);
		linQVGA.setVisibility(View.GONE);
		linSpeaker = (RelativeLayout) findViewById(R.id.speaker_layout);
		linSpeaker.setVisibility(View.GONE);

		linPnlemode = (RelativeLayout) findViewById(R.id.emode_layout);
		linPnlemode.setVisibility(View.GONE);

		// toolbar
		button_toolbar_speaker = (ImageButton) findViewById(R.id.button_speaker);
		button_toolbar_recording = (ImageButton) findViewById(R.id.button_recording);
		button_toolbar_snapshot = (ImageButton) findViewById(R.id.button_snapshot);
		button_toolbar_mirror = (ImageButton) findViewById(R.id.button_mirror);
		button_toolbar_mirror_rl = (ImageButton) findViewById(R.id.button_mirror_rl);
		button_toolbar_SET = (ImageButton) findViewById(R.id.button_SET);
		button_toolbar_QVGA = (ImageButton) findViewById(R.id.button_QVGA);
		button_toolbar_EMODE = (ImageButton) findViewById(R.id.button_EMODE);

		qvga_button1 = (Button) findViewById(R.id.qvga_button1);
		qvga_button2 = (Button) findViewById(R.id.qvga_button2);
		qvga_button3 = (Button) findViewById(R.id.qvga_button3);
		qvga_button4 = (Button) findViewById(R.id.qvga_button4);
		qvga_button5 = (Button) findViewById(R.id.qvga_button5);

		qvga_button1.setOnClickListener(QVGAClick);
		qvga_button2.setOnClickListener(QVGAClick);
		qvga_button3.setOnClickListener(QVGAClick);
		qvga_button4.setOnClickListener(QVGAClick);
		qvga_button5.setOnClickListener(QVGAClick);

		emode_button1 = (Button) findViewById(R.id.emode_button1);
		emode_button2 = (Button) findViewById(R.id.emode_button2);
		emode_button3 = (Button) findViewById(R.id.emode_button3);
		emode_button4 = (Button) findViewById(R.id.emode_button4);
		emode_button1.setOnClickListener(EmodeClick);
		emode_button2.setOnClickListener(EmodeClick);
		emode_button3.setOnClickListener(EmodeClick);
		emode_button4.setOnClickListener(EmodeClick);

		btn_speaker = (ImageButton) findViewById(R.id.btn_speaker);
		btn_speaker.setOnTouchListener(this);

		button_toolbar_speaker.setOnClickListener(ToolBarClick);

		button_toolbar_recording.setOnClickListener(ToolBarClick);
		button_toolbar_snapshot.setOnClickListener(ToolBarClick);
		button_toolbar_mirror.setOnClickListener(ToolBarClick);
		button_toolbar_mirror_rl.setOnClickListener(ToolBarClick);
		button_toolbar_SET.setOnClickListener(ToolBarClick);
		button_toolbar_QVGA.setOnClickListener(ToolBarClick);
		button_toolbar_EMODE.setOnClickListener(ToolBarClick);
		
		mSoftMonitorLayout = (RelativeLayout) findViewById(R.id.softMonitorLayout);
		myScrollView = (VerticalScrollView) findViewById(R.id.scrollView);
		myHorScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);

		toolbar_layout.setVisibility(View.GONE);
		isShowToolBar = false;

		if (mIsRecording) {
			button_toolbar_recording
					.setBackgroundResource(R.drawable.btn_recording_switch_stop);
		}

		if (mIsListening) {
			button_toolbar_speaker
					.setBackgroundResource(R.drawable.btn_speaker_on_switch);
			linSpeaker.setVisibility(View.VISIBLE);
			nullLayout.setVisibility(View.GONE);
			toolbar_layout.setVisibility(View.VISIBLE);
			isShowToolBar = true;
		}

		if (isOpenLinQVGA) {
			button_toolbar_QVGA
					.setBackgroundResource(R.drawable.btn_qvga_enable_switch);
			linQVGA.setVisibility(View.VISIBLE);
			nullLayout.setVisibility(View.GONE);
			toolbar_layout.setVisibility(View.VISIBLE);
			isShowToolBar = true;
		}

		if (isOpenLinEmode) {
			button_toolbar_EMODE
					.setBackgroundResource(R.drawable.btn_emode_enable_switch);
			linPnlemode.setVisibility(View.VISIBLE);
			nullLayout.setVisibility(View.GONE);
			toolbar_layout.setVisibility(View.VISIBLE);
			isShowToolBar = true;
		}

		if (Build.VERSION.SDK_INT < BUILD_VERSION_CODES_ICE_CREAM_SANDWICH) {

			bg.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
			getSupportActionBar().setBackgroundDrawable(bg);

			bgSplit.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
			getSupportActionBar().setSplitBackgroundDrawable(bgSplit);
		}

		if (mCamera != null && mCamera.getMultiStreamSupported(0)
				&& mCamera.getSupportedStream().length > 1)
			getSupportActionBar().setSubtitle(
					getText(R.string.dialog_LiveView).toString() + " : "
							+ mDevice.NickName + " - CH"
							+ (mSelectedChannel + 1));
		else
			getSupportActionBar().setSubtitle(
					getText(R.string.dialog_LiveView).toString() + " : "
							+ mDevice.NickName);

		txtConnectionStatus = null;
		txtConnectionMode = null;
		txtResolution = null;
		txtFrameRate = null;
		txtBitRate = null;
		txtOnlineNumber = null;
		txtFrameCount = null;
		txtIncompleteFrameCount = null;
		txtRecvFrmPreSec = null;
		txtDispFrmPreSeco = null;
		txtPerformance = null;
		txtCodecMode = null;
		
		if (!runSoftwareDecode) {
			
			mHardMonitor = (IMonitor) findViewById(R.id.hardMonitor);
			mHardMonitor.setMaxZoom(3.0f);
			mHardMonitor.enableDither(mCamera.mEnableDither);
			mHardMonitor.attachCamera(mCamera, mSelectedChannel);
			
			mSoftMonitorLayout.setVisibility(View.GONE);
			myScrollView.setVisibility(View.VISIBLE);
			mHardMonitor.SetOnMonitorClickListener(this);
			
			mHardMonitor.cleanFrameQueue();
			mHardMonitor.setMediaCodecListener(this);

			myScrollView = (VerticalScrollView) findViewById(R.id.scrollView);
			myHorScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);
			// calculate surface view size
			Display display = getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			int width = size.x;
			int height = size.y;

			final SurfaceView surfaceView = (SurfaceView) mHardMonitor;
			surfaceView.getLayoutParams().width = width;
			surfaceView.getLayoutParams().height = height;
			mMiniVideoHeight = surfaceView.getLayoutParams().height;
			mMiniVideoWidth = surfaceView.getLayoutParams().width;
			surfaceView.setLayoutParams(surfaceView.getLayoutParams());
			if (myHorScrollView != null) {
				myHorScrollView.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View view, MotionEvent event) {
						mHardMonitor.HorizontalScrollTouch(view, event);
						return false;
					}
				});
			}
		}
		else {
			
			mSoftMonitor = (IMonitor) findViewById(R.id.softMonitor);
			mSoftMonitor.setMaxZoom(3.0f);
			mSoftMonitor.enableDither(mCamera.mEnableDither);
			mSoftMonitor.attachCamera(mCamera, mSelectedChannel);
			mSoftMonitor.SetOnMonitorClickListener(this);
			
			mSoftMonitorLayout.setVisibility(View.VISIBLE);
			myScrollView.setVisibility(View.GONE);
		}

		
		reScaleMonitor();
	}

	private void setupPortraitView(final boolean runSoftwareDecode) {
		

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().clearFlags(
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setCustomView(R.layout.titlebar);
		actionBar.show();
		
		if(mDevice == null || mCamera == null){
			this.finish();
			return;
		}
		txt_title = (TextView) this.findViewById(R.id.bar_text);
		txt_title.setText(mDevice.NickName);

		setContentView(R.layout.live_view_portrait_mix);
		
		mSoftCodecCurrent = false;
		if (runSoftwareDecode)
			mSoftCodecCurrent = true;

		toolbarScrollView = (HorizontalScrollView) findViewById(R.id.ScrollView_toolbar);
		toolbarScrollView.setHorizontalFadingEdgeEnabled(false);

		toolbar_layout = null;

		nullLayout = (LinearLayout) findViewById(R.id.null_layout);

		linPnlCameraInfo = (LinearLayout) findViewById(R.id.pnlCameraInfo);

		linQVGA = (RelativeLayout) findViewById(R.id.qvga_layout);
		linQVGA.setVisibility(View.GONE);
		linSpeaker = (RelativeLayout) findViewById(R.id.speaker_layout);
		linSpeaker.setVisibility(View.GONE);
		linPnlemode = (RelativeLayout) findViewById(R.id.emode_layout);
		linPnlemode.setVisibility(View.GONE);

		// toolbar
		button_toolbar_speaker = (ImageButton) findViewById(R.id.button_speaker);
		button_toolbar_recording = (ImageButton) findViewById(R.id.button_recording);
		button_toolbar_snapshot = (ImageButton) findViewById(R.id.button_snapshot);
		button_toolbar_mirror = (ImageButton) findViewById(R.id.button_mirror);
		button_toolbar_mirror_rl = (ImageButton) findViewById(R.id.button_mirror_rl);
		button_toolbar_SET = (ImageButton) findViewById(R.id.button_SET);
		button_toolbar_QVGA = (ImageButton) findViewById(R.id.button_QVGA);
		button_toolbar_EMODE = (ImageButton) findViewById(R.id.button_EMODE);

		qvga_button1 = (Button) findViewById(R.id.qvga_button1);
		qvga_button2 = (Button) findViewById(R.id.qvga_button2);
		qvga_button3 = (Button) findViewById(R.id.qvga_button3);
		qvga_button4 = (Button) findViewById(R.id.qvga_button4);
		qvga_button5 = (Button) findViewById(R.id.qvga_button5);

		qvga_button1.setOnClickListener(QVGAClick);
		qvga_button2.setOnClickListener(QVGAClick);
		qvga_button3.setOnClickListener(QVGAClick);
		qvga_button4.setOnClickListener(QVGAClick);
		qvga_button5.setOnClickListener(QVGAClick);

		emode_button1 = (Button) findViewById(R.id.emode_button1);
		emode_button2 = (Button) findViewById(R.id.emode_button2);
		emode_button3 = (Button) findViewById(R.id.emode_button3);
		emode_button4 = (Button) findViewById(R.id.emode_button4);
		emode_button1.setOnClickListener(EmodeClick);
		emode_button2.setOnClickListener(EmodeClick);
		emode_button3.setOnClickListener(EmodeClick);
		emode_button4.setOnClickListener(EmodeClick);

		button_toolbar_speaker.setOnClickListener(ToolBarClick);

		button_toolbar_recording.setOnClickListener(ToolBarClick);
		button_toolbar_snapshot.setOnClickListener(ToolBarClick);
		button_toolbar_mirror.setOnClickListener(ToolBarClick);
		button_toolbar_mirror_rl.setOnClickListener(ToolBarClick);
		button_toolbar_SET.setOnClickListener(ToolBarClick);
		button_toolbar_QVGA.setOnClickListener(ToolBarClick);
		button_toolbar_EMODE.setOnClickListener(ToolBarClick);

		txtConnectionSlash = (TextView) findViewById(R.id.txtConnectionSlash);
		txtResolutionSlash = (TextView) findViewById(R.id.txtResolutionSlash);
		txtShowFPS = (TextView) findViewById(R.id.txtShowFPS);
		txtFPSSlash = (TextView) findViewById(R.id.txtFPSSlash);
		txtShowBPS = (TextView) findViewById(R.id.txtShowBPS);
		txtOnlineNumberSlash = (TextView) findViewById(R.id.txtOnlineNumberSlash);
		txtShowFrameRatio = (TextView) findViewById(R.id.txtShowFrameRatio);
		txtFrameCountSlash = (TextView) findViewById(R.id.txtFrameCountSlash);
		txtQuality = (TextView) findViewById(R.id.txtQuality);
		txtDispFrmPreSeco = (TextView) findViewById(R.id.txtDispFrmPreSeco);
		txtRecvFrmSlash = (TextView) findViewById(R.id.txtRecvFrmSlash);
		txtRecvFrmPreSec = (TextView) findViewById(R.id.txtRecvFrmPreSec);
		txtPerformance = (TextView) findViewById(R.id.txtPerformance);

		txtConnectionStatus = (TextView) findViewById(R.id.txtConnectionStatus);
		txtConnectionMode = (TextView) findViewById(R.id.txtConnectionMode);
		txtResolution = (TextView) findViewById(R.id.txtResolution);
		txtFrameRate = (TextView) findViewById(R.id.txtFrameRate);
		txtBitRate = (TextView) findViewById(R.id.txtBitRate);
		txtOnlineNumber = (TextView) findViewById(R.id.txtOnlineNumber);
		txtFrameCount = (TextView) findViewById(R.id.txtFrameCount);
		txtIncompleteFrameCount = (TextView) findViewById(R.id.txtIncompleteFrameCount);
		txtCodecMode  = (TextView) findViewById(R.id.txtCodecMode);
		mCHTextView = (TextView) findViewById(R.id.CH_textview);
		mCHTextView.setText("CH" + (mSelectedChannel + 1));
		CH_button = (ImageButton) findViewById(R.id.CH_button);
		
		mSoftMonitorLayout = (RelativeLayout) findViewById(R.id.softMonitorLayout);
		myScrollView = (VerticalScrollView) findViewById(R.id.scrollView);
		myHorScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);
		

		btn_speaker = (ImageButton) findViewById(R.id.btn_speaker);
		btn_speaker.setOnTouchListener(this);

		if (mIsRecording) {
			button_toolbar_recording
					.setBackgroundResource(R.drawable.btn_recording_switch_stop);
		}

		if (mIsListening) {
			button_toolbar_speaker
					.setBackgroundResource(R.drawable.btn_speaker_on_switch);
			linSpeaker.setVisibility(View.VISIBLE);
			nullLayout.setVisibility(View.GONE);
		}

		if (isOpenLinQVGA) {
			button_toolbar_QVGA
					.setBackgroundResource(R.drawable.btn_qvga_enable_switch);
			linQVGA.setVisibility(View.VISIBLE);
			nullLayout.setVisibility(View.GONE);
		}

		if (isOpenLinEmode) {
			button_toolbar_EMODE
					.setBackgroundResource(R.drawable.btn_emode_enable_switch);
			linPnlemode.setVisibility(View.VISIBLE);
			nullLayout.setVisibility(View.GONE);
		}

		if (mCamera != null && mCamera.isSessionConnected()
				&& mCamera.getMultiStreamSupported(0)
				&& mCamera.getSupportedStream().length > 1) {
			CH_button.setVisibility(View.VISIBLE);
		} else {
			CH_button.setVisibility(View.GONE);
		}

		if (txtConnectionStatus != null) {
			if (getText(R.string.connstus_connecting).toString().equals(
					mConnStatus)) {
				txtConnectionStatus
						.setBackgroundResource(R.drawable.live_unknow);
			} else if (getText(R.string.connstus_connected).toString().equals(
					mConnStatus)) {
				txtConnectionStatus
						.setBackgroundResource(R.drawable.live_online);
			} else {
				txtConnectionStatus
						.setBackgroundResource(R.drawable.live_offline);
			}
		}
		;
		txtConnectionStatus.setText(mConnStatus);

		txtConnectionSlash.setText("");
		txtResolutionSlash.setText("");
		txtShowFPS.setText("");
		txtFPSSlash.setText("");
		txtShowBPS.setText("");
		txtOnlineNumberSlash.setText("");
		txtShowFrameRatio.setText("");
		txtFrameCountSlash.setText("");
		txtRecvFrmSlash.setText("");
		txtPerformance
				.setText(getPerformance((int) (((float) mCamera
						.getDispFrmPreSec() / (float) mCamera
						.getRecvFrmPreSec()) * 100)));

		txtConnectionMode.setVisibility(View.GONE);
		txtFrameRate.setVisibility(View.GONE);
		txtBitRate.setVisibility(View.GONE);
		txtFrameCount.setVisibility(View.GONE);
		txtIncompleteFrameCount.setVisibility(View.GONE);
		txtRecvFrmPreSec.setVisibility(View.GONE);
		txtDispFrmPreSeco.setVisibility(View.GONE);
		txtCodecMode.setVisibility(View.GONE);

		linPnlCameraInfo.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				MultiViewActivity.nShowMessageCount++;
				showMessage();

			}
		});

		CH_button.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				final AlertDialog dlg = new AlertDialog.Builder(
						LiveViewActivity.this).create();
				ListView view = new ListView(LiveViewActivity.this);
				dlg.setView(view);
				dlg.setCanceledOnTouchOutside(true);
				Window window = dlg.getWindow();
				WindowManager.LayoutParams lp = window.getAttributes();
				// lp.y = -64;
				lp.dimAmount = 0f;

				ArrayAdapter<SStreamDef> adapter = new ArrayAdapter<AVIOCTRLDEFs.SStreamDef>(
						LiveViewActivity.this,
						android.R.layout.simple_list_item_1);
				for (SStreamDef streamDef : mCamera.getSupportedStream())
					adapter.add(streamDef);

				view.setAdapter(adapter);
				view.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						
						if(mSoftMonitor != null)
							mSoftMonitor.deattachCamera();
						if(mHardMonitor != null)
							mHardMonitor.deattachCamera();
						
						mCamera.stopShow(mSelectedChannel);
						mCamera.stopListening(mSelectedChannel);
						mCamera.stopSpeaking(mSelectedChannel);

						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						System.out.println("OnSpinStreamItemSelected: " + arg2);
						mSelectedChannel = arg2;
						mCHTextView.setText("CH" + (mSelectedChannel + 1));
						if (mCamera != null
								&& mCamera.getMultiStreamSupported(0)
								&& mCamera.getSupportedStream().length > 1)
							getSupportActionBar().setSubtitle(
									getText(R.string.dialog_LiveView)
											.toString()
											+ " : "
											+ mDevice.NickName
											+ " - CH"
											+ (mSelectedChannel + 1));
						else
							getSupportActionBar().setSubtitle(
									getText(R.string.dialog_LiveView)
											.toString()
											+ " : "
											+ mDevice.NickName);

						
//						mCamera.startShow(mSelectedChannel, true, mSoftCodecDefault);
						
						if(mSoftCodecDefault){
							mSoftMonitor.enableDither(mCamera.mEnableDither);
							mSoftMonitor.attachCamera(mCamera, mSelectedChannel);
						}
						else {
							mHardMonitor.enableDither(mCamera.mEnableDither);
							mHardMonitor.attachCamera(mCamera, mSelectedChannel);
							reSetCodec();
						}

						if (mIsListening)
							mCamera.startListening(mSelectedChannel,
									mIsRecording);
						if (mIsSpeaking)
							mCamera.startSpeaking(mSelectedChannel);
						
						handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								mCamera.startShow(mSelectedChannel, true, mSoftCodecCurrent);
								mCHTextView.setText("CH" + (mSelectedChannel + 1));
							}
						}, 2500);

						dlg.dismiss();
					}
				});

				dlg.show();

			}
		});

		if (!runSoftwareDecode) {
			
			mHardMonitor = (IMonitor) findViewById(R.id.hardMonitor);
			mHardMonitor.setMaxZoom(3.0f);
			mHardMonitor.enableDither(mCamera.mEnableDither);
			mHardMonitor.attachCamera(mCamera, mSelectedChannel);
			
			mSoftMonitorLayout.setVisibility(View.GONE);
			myScrollView.setVisibility(View.VISIBLE);

			mHardMonitor.cleanFrameQueue();
			mHardMonitor.setMediaCodecListener(this);

			
			// calculate surface view size
			Display display = getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			int width = size.x;
			final SurfaceView surfaceView = (SurfaceView) mHardMonitor;
			surfaceView.getLayoutParams().width = width;
			handler.post(new Runnable() {
				@Override
				public void run() {
					if (myScrollView.getMeasuredHeight() == 0) {
						handler.postDelayed(this, 200);
					} else {
						surfaceView.getLayoutParams().height = myScrollView.getMeasuredHeight();
						mMiniVideoHeight = surfaceView.getLayoutParams().height;
						mMiniVideoWidth = surfaceView.getLayoutParams().width;
						surfaceView.setLayoutParams(surfaceView.getLayoutParams());
					}
				}
			});
			if (myHorScrollView != null) {
				myHorScrollView.setOnTouchListener(new OnTouchListener() {
					@Override
					public boolean onTouch(View view, MotionEvent event) {
						mHardMonitor.HorizontalScrollTouch(view, event);
						return false;
					}
				});
			}
		}
		else{ 
			mSoftMonitor = (IMonitor) findViewById(R.id.softMonitor);
			mSoftMonitor.setMaxZoom(3.0f);
			mSoftMonitor.enableDither(mCamera.mEnableDither);
			mSoftMonitor.attachCamera(mCamera, mSelectedChannel);
			
			mSoftMonitorLayout.setVisibility(View.VISIBLE);
			myScrollView.setVisibility(View.GONE);
		}

		
		reScaleMonitor();
	}

	private void showMessage() {

		St_SInfo stSInfo = new St_SInfo();
		IOTCAPIs.IOTC_Session_Check(mCamera.getMSID(), stSInfo);

		if (MultiViewActivity.nShowMessageCount >= 10) {

			txtConnectionStatus.setText(mConnStatus);
			txtConnectionMode.setText(getSessionMode(mCamera != null ? mCamera
					.getSessionMode() : -1)
					+ " C: "
					+ IOTCAPIs.IOTC_Get_Nat_Type()
					+ ", D: "
					+ stSInfo.NatType
					+ ",R" + mCamera.getbResend());

			txtConnectionSlash.setText(" / ");
			txtResolutionSlash.setText(" / ");
			txtShowFPS.setText(getText(R.string.txtFPS));
			txtFPSSlash.setText(" / ");
			txtShowBPS.setText(getText(R.string.txtBPS));
			// txtShowOnlineNumber.setText(getText(R.string.txtOnlineNumber));
			txtOnlineNumberSlash.setText(" / ");
			txtShowFrameRatio.setText(getText(R.string.txtFrameRatio));
			txtFrameCountSlash.setText(" / ");
			txtQuality.setText(getText(R.string.txtQuality));
			txtRecvFrmSlash.setText(" / ");
			// mCamera.getDispFrmPreSec()
			txtConnectionMode.setVisibility(View.VISIBLE);
			// txtResolution.setVisibility(View.VISIBLE);
			txtFrameRate.setVisibility(View.VISIBLE);
			txtBitRate.setVisibility(View.VISIBLE);
			txtOnlineNumber.setVisibility(View.VISIBLE);
			txtFrameCount.setVisibility(View.VISIBLE);
			txtIncompleteFrameCount.setVisibility(View.VISIBLE);
			txtRecvFrmPreSec.setVisibility(View.VISIBLE);
			txtDispFrmPreSeco.setVisibility(View.VISIBLE);
			txtCodecMode.setVisibility(View.VISIBLE);
		}
	}

	private Button.OnClickListener QVGAClick = new Button.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			button_toolbar_QVGA
					.setBackgroundResource(R.drawable.btn_qvga_switch);
			isOpenLinQVGA = false;
			linQVGA.setVisibility(View.GONE);
			nullLayout.setVisibility(View.VISIBLE);
			switch (v.getId()) {
			case R.id.qvga_button1:
				mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
						AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETSTREAMCTRL_REQ,
						AVIOCTRLDEFs.SMsgAVIoctrlSetStreamCtrlReq.parseContent(
								mDevice.ChannelIndex, (byte) 1));
				break;
			case R.id.qvga_button2:
				mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
						AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETSTREAMCTRL_REQ,
						AVIOCTRLDEFs.SMsgAVIoctrlSetStreamCtrlReq.parseContent(
								mDevice.ChannelIndex, (byte) 2));
				break;
			case R.id.qvga_button3:
				mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
						AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETSTREAMCTRL_REQ,
						AVIOCTRLDEFs.SMsgAVIoctrlSetStreamCtrlReq.parseContent(
								mDevice.ChannelIndex, (byte) 3));
				break;
			case R.id.qvga_button4:
				mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
						AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETSTREAMCTRL_REQ,
						AVIOCTRLDEFs.SMsgAVIoctrlSetStreamCtrlReq.parseContent(
								mDevice.ChannelIndex, (byte) 4));
				break;
			case R.id.qvga_button5:
				mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
						AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETSTREAMCTRL_REQ,
						AVIOCTRLDEFs.SMsgAVIoctrlSetStreamCtrlReq.parseContent(
								mDevice.ChannelIndex, (byte) 5));
				break;
			}
			reSetCodec();
		}

	};
	
	private void reSetCodec() {
		if(mSoftCodecCurrent == false && mHardMonitor != null)
			mHardMonitor.resetCodec();
	}

	private Button.OnClickListener EmodeClick = new Button.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			button_toolbar_EMODE
					.setBackgroundResource(R.drawable.btn_emode_switch);
			isOpenLinEmode = false;
			linPnlemode.setVisibility(View.GONE);
			nullLayout.setVisibility(View.VISIBLE);
			switch (v.getId()) {
			case R.id.emode_button1:
				mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
						AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SET_ENVIRONMENT_REQ,
						AVIOCTRLDEFs.SMsgAVIoctrlSetEnvironmentReq
								.parseContent(mDevice.ChannelIndex, (byte) 0));
				break;
			case R.id.emode_button2:
				mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
						AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SET_ENVIRONMENT_REQ,
						AVIOCTRLDEFs.SMsgAVIoctrlSetEnvironmentReq
								.parseContent(mDevice.ChannelIndex, (byte) 1));
				break;
			case R.id.emode_button3:
				mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
						AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SET_ENVIRONMENT_REQ,
						AVIOCTRLDEFs.SMsgAVIoctrlSetEnvironmentReq
								.parseContent(mDevice.ChannelIndex, (byte) 2));
				break;
			case R.id.emode_button4:
				mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
						AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SET_ENVIRONMENT_REQ,
						AVIOCTRLDEFs.SMsgAVIoctrlSetEnvironmentReq
								.parseContent(mDevice.ChannelIndex, (byte) 3));
				break;
			}
		}

	};

	private Button.OnClickListener ToolBarClick = new Button.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {

			case R.id.button_speaker:
				inittoolar();
				if (mIsListening) {
					button_toolbar_recording.setClickable(true);
					button_toolbar_speaker
							.setBackgroundResource(R.drawable.btn_speaker_off_switch);
					linSpeaker.setVisibility(View.GONE);
					nullLayout.setVisibility(View.VISIBLE);
					mCamera.stopSpeaking(mSelectedChannel);
					mCamera.stopListening(mSelectedChannel);
					mIsListening = false;
					mIsSpeaking = false;

				} else {
					button_toolbar_recording.setClickable(false);
					inittoolarboolean();
					mIsListening = true;
					mCamera.startListening(mSelectedChannel, mIsRecording);
					button_toolbar_speaker
							.setBackgroundResource(R.drawable.btn_speaker_on_switch);
					linSpeaker.setVisibility(View.VISIBLE);
					nullLayout.setVisibility(View.GONE);
				}
				break;
			case R.id.button_recording:
				if (mIsRecording && mCamera.hasRecordFreme()) {
					button_toolbar_speaker.setClickable(true);
					button_toolbar_speaker.setEnabled(true);
					button_toolbar_snapshot.setEnabled(true);
					button_toolbar_mirror.setEnabled(true);
					button_toolbar_mirror_rl.setEnabled(true);
					button_toolbar_SET.setEnabled(true);
					button_toolbar_QVGA.setEnabled(true);
					button_toolbar_EMODE.setEnabled(true);
					CH_button.setEnabled(true);

					mCamera.stopListening(mSelectedChannel);
					mCamera.stopSpeaking(mSelectedChannel);
					button_toolbar_recording
							.setBackgroundResource(R.drawable.btn_recording_switch_start);
					// mIsSpeaking = false;
					// mIsListening = false;
					mCamera.stopRecording();
					// mCamera.setThumbnailPath(mContext);
					mIsRecording = false;
				} else {
					button_toolbar_speaker.setClickable(false);
					if (getAvailaleSize() <= 300) {
						Toast.makeText(mContext, R.string.recording_tips_size,
								Toast.LENGTH_SHORT).show();
					}

					if (mCamera.codec_ID_for_recording == AVFrame.MEDIA_CODEC_VIDEO_H264) {
						button_toolbar_speaker.setEnabled(false);
						button_toolbar_snapshot.setEnabled(false);
						button_toolbar_mirror.setEnabled(false);
						button_toolbar_mirror_rl.setEnabled(false);
						button_toolbar_SET.setEnabled(false);
						button_toolbar_QVGA.setEnabled(false);
						button_toolbar_EMODE.setEnabled(false);
						CH_button.setEnabled(false);

						mIsRecording = true;
						mCamera.startListening(mSelectedChannel, mIsRecording);
						mCamera.stopSpeaking(mSelectedChannel);
						button_toolbar_recording
								.setBackgroundResource(R.drawable.btn_recording_switch_stop);
						// mIsListening = true;
						// mIsSpeaking = false;
						File rootFolder = new File(Environment
								.getExternalStorageDirectory()
								.getAbsolutePath()
								+ "/Record/");
						File targetFolder = new File(
								rootFolder.getAbsolutePath() + "/" + mDevUID);
						if (!rootFolder.exists()) {
							try {
								rootFolder.mkdir();
							} catch (SecurityException se) {
							}
						}

						if (!targetFolder.exists()) {
							try {
								targetFolder.mkdir();
							} catch (SecurityException se) {
							}
						}
						String path = "/sdcard/Record/" + mDevUID + "/"
								+ getFileNameWithTime2();
						mVideoFilePath = path;
						mCamera.startRecording(path);
						// mCamera.setThumbnailPath(path,mContext);
					} else {
						Toast.makeText(mContext,
								R.string.recording_tips_format,
								Toast.LENGTH_SHORT).show();
					}

					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							if (mIsRecording) {

								button_toolbar_speaker.setEnabled(true);
								button_toolbar_snapshot.setEnabled(true);
								button_toolbar_mirror.setEnabled(true);
								button_toolbar_mirror_rl.setEnabled(true);
								button_toolbar_SET.setEnabled(true);
								button_toolbar_QVGA.setEnabled(true);
								button_toolbar_EMODE.setEnabled(true);
								CH_button.setEnabled(true);

								mCamera.stopListening(mSelectedChannel);
								mCamera.stopSpeaking(mSelectedChannel);
								button_toolbar_recording
										.setBackgroundResource(R.drawable.btn_recording_switch_start);
								// mIsSpeaking = false;
								// mIsListening = false;
								mCamera.stopRecording();
								// mCamera.setThumbnailPath(mContext);
								mIsRecording = false;
							}
						}
					}, 180000);
				}
				break;
			case R.id.button_snapshot:
				inittoolar();
				inittoolarboolean();
				if (mCamera != null
						&& mCamera.isChannelConnected(mSelectedChannel)) {

					if (isSDCardValid()) {

						File rootFolder = new File(Environment
								.getExternalStorageDirectory()
								.getAbsolutePath()
								+ "/Snapshot/");
						File targetFolder = new File(
								rootFolder.getAbsolutePath() + "/" + mDevUID);
						// File rootFolder = new
						// File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()
						// + "/Snapshot");
						// File targetFolder = new
						// File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()
						// + "/Snapshot");

						if (!rootFolder.exists()) {
							try {
								rootFolder.mkdir();
							} catch (SecurityException se) {
							}
						}

						if (!targetFolder.exists()) {

							try {
								targetFolder.mkdir();
							} catch (SecurityException se) {
							}
						}

						final String file = targetFolder.getAbsoluteFile()
								+ "/" + getFileNameWithTime();
						mPhotoFilePath = file;
						if (mCamera != null) {
							mCamera.setSnapshot(mContext, file);
						}

					} else {

						Toast.makeText(
								LiveViewActivity.this,
								LiveViewActivity.this.getText(
										R.string.tips_no_sdcard).toString(),
								Toast.LENGTH_SHORT).show();
					}
				}
				break;
			case R.id.button_mirror:
				inittoolar();
				inittoolarboolean();
				if (LevelFlip){
					LevelFlip = false;
					if(VerticalFlip)
						mRotateMode = ROTATEMODE_MIRROR;
					else
						mRotateMode = ROTATEMODE_NORMAL;
				}
				else {
					LevelFlip = true;
					if(VerticalFlip)
						mRotateMode = ROTATEMODE_FLIPANDMIRROR;
					else
						mRotateMode = ROTATEMODE_FLIP;
				}
				
				mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
						AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SET_VIDEOMODE_REQ,
						AVIOCTRLDEFs.SMsgAVIoctrlSetVideoModeReq
								.parseContent(mDevice.ChannelIndex,
										(byte) mRotateMode));
				
				break;
			case R.id.button_mirror_rl:
				inittoolar();
				inittoolarboolean();
				if (VerticalFlip) {
					VerticalFlip = false;
					if(LevelFlip)
						mRotateMode = ROTATEMODE_FLIP;
					else
						mRotateMode = ROTATEMODE_NORMAL;
				} else {
					VerticalFlip = true;
					if(LevelFlip)
						mRotateMode = ROTATEMODE_FLIPANDMIRROR;
					else
						mRotateMode = ROTATEMODE_MIRROR;
				}
				
				mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
						AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SET_VIDEOMODE_REQ,
						AVIOCTRLDEFs.SMsgAVIoctrlSetVideoModeReq
								.parseContent(mDevice.ChannelIndex,
										(byte) mRotateMode));
				break;

			case R.id.button_SET:
				inittoolar();
				inittoolarboolean();
				Bundle extras = new Bundle();
				extras.putString("dev_uid", mDevice.UID);
				extras.putString("dev_uuid", mDevice.UUID);
				extras.putString("dev_nickname", mDevice.NickName);
				extras.putString("conn_status", mDevice.Status);
				extras.putString("view_acc", mDevice.View_Account);
				extras.putString("view_pwd", mDevice.View_Password);
				extras.putInt("camera_channel", mSelectedChannel);
				Intent intent = new Intent();
				intent.putExtras(extras);
				intent.setClass(LiveViewActivity.this, EditDeviceActivity.class);
				startActivityForResult(intent,
						MultiViewActivity.REQUEST_CODE_CAMERA_EDIT);
				break;
			case R.id.button_QVGA:
				inittoolar();
				if (isOpenLinQVGA) {
					button_toolbar_QVGA
							.setBackgroundResource(R.drawable.btn_qvga_switch);
					isOpenLinQVGA = false;
					linQVGA.setVisibility(View.GONE);
					nullLayout.setVisibility(View.VISIBLE);
				} else {
					inittoolarboolean();
					button_toolbar_QVGA
							.setBackgroundResource(R.drawable.btn_qvga_enable_switch);
					isOpenLinQVGA = true;
					linQVGA.setVisibility(View.VISIBLE);
					nullLayout.setVisibility(View.GONE);
				}
				break;
			case R.id.button_EMODE:
				inittoolar();
				if (isOpenLinEmode) {
					button_toolbar_EMODE
							.setBackgroundResource(R.drawable.btn_emode_switch);
					isOpenLinEmode = false;
					linPnlemode.setVisibility(View.GONE);
					nullLayout.setVisibility(View.VISIBLE);
				}

				else {
					inittoolarboolean();
					button_toolbar_EMODE
							.setBackgroundResource(R.drawable.btn_emode_enable_switch);
					isOpenLinEmode = true;
					linPnlemode.setVisibility(View.VISIBLE);
					nullLayout.setVisibility(View.GONE);
				}

				break;

			}

		}
	};

	private void inittoolar() {
		button_toolbar_EMODE.setBackgroundResource(R.drawable.btn_emode_switch);
		button_toolbar_speaker
				.setBackgroundResource(R.drawable.btn_speaker_off_switch);
		button_toolbar_QVGA.setBackgroundResource(R.drawable.btn_qvga_switch);
		nullLayout.setVisibility(View.VISIBLE);
		linSpeaker.setVisibility(View.GONE);
		linQVGA.setVisibility(View.GONE);
		linPnlemode.setVisibility(View.GONE);
	}

	private void inittoolarboolean() {
		if (mIsListening) {
			mCamera.stopSpeaking(mSelectedChannel);
			mCamera.stopListening(mSelectedChannel);
		}
		mIsListening = false;
		isOpenLinEmode = false;
		isOpenLinQVGA = false;
		
		if(button_toolbar_recording != null)
			button_toolbar_recording.setClickable(true);
	}

	/*
	 * private void addImageGallery(File file) { ContentValues values = new
	 * ContentValues(); values.put(MediaStore.Images.Media.DATA,
	 * file.getAbsolutePath()); values.put(MediaStore.Images.Media.MIME_TYPE,
	 * "image/jpeg"); // setar isso
	 * getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
	 * values); } private boolean saveImage(String fileName, Bitmap frame) { if
	 * (fileName == null || fileName.length() <= 0) return false; boolean bErr =
	 * false; FileOutputStream fos = null; try { fos = new
	 * FileOutputStream(fileName, false);
	 * frame.compress(Bitmap.CompressFormat.JPEG, 90, fos); fos.flush();
	 * fos.close(); } catch (Exception e) { bErr = true;
	 * System.out.println("saveImage(.): " + e.getMessage()); } finally { if
	 * (bErr) { if (fos != null) { try { fos.close(); } catch (IOException e) {
	 * e.printStackTrace(); } } return false; } } addImageGallery(new
	 * File(fileName)); return true; }
	 */

	// filename: such as,M20101023_181010.jpg
	private static String getFileNameWithTime() {

		Calendar c = Calendar.getInstance();
		int mYear = c.get(Calendar.YEAR);
		int mMonth = c.get(Calendar.MONTH) + 1;
		int mDay = c.get(Calendar.DAY_OF_MONTH);
		int mHour = c.get(Calendar.HOUR_OF_DAY);
		int mMinute = c.get(Calendar.MINUTE);
		int mSec = c.get(Calendar.SECOND);
		
		StringBuffer sb = new StringBuffer();
		sb.append("IMG_");
		sb.append(mYear);
		if (mMonth < 10)
			sb.append('0');
		sb.append(mMonth);
		if (mDay < 10)
			sb.append('0');
		sb.append(mDay);
		sb.append('_');
		if (mHour < 10)
			sb.append('0');
		sb.append(mHour);
		if (mMinute < 10)
			sb.append('0');
		sb.append(mMinute);
		if (mSec < 10)
			sb.append('0');
		sb.append(mSec);
		sb.append(".jpg");

		return sb.toString();
	}

	private static String getFileNameWithTime2() {

		Calendar c = Calendar.getInstance();
		int mYear = c.get(Calendar.YEAR);
		int mMonth = c.get(Calendar.MONTH) + 1;
		int mDay = c.get(Calendar.DAY_OF_MONTH);
		int mHour = c.get(Calendar.HOUR_OF_DAY);
		int mMinute = c.get(Calendar.MINUTE);
		int mSec = c.get(Calendar.SECOND);

		StringBuffer sb = new StringBuffer();
		// sb.append("MP4_");
		sb.append(mYear);
		if (mMonth < 10)
			sb.append('0');
		sb.append(mMonth);
		if (mDay < 10)
			sb.append('0');
		sb.append(mDay);
		sb.append('_');
		if (mHour < 10)
			sb.append('0');
		sb.append(mHour);
		if (mMinute < 10)
			sb.append('0');
		sb.append(mMinute);
		if (mSec < 10)
			sb.append('0');
		sb.append(mSec);
		sb.append(".mp4");

		sb.toString();

		return sb.toString();
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

	private static boolean isSDCardValid() {

		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	private void quit() {
	
		if(!mVideoFilePath.equals("") && !mCamera.hasRecordFreme()){
			
			File file = new File(mVideoFilePath);
			if(file.exists())
				file.delete();
			
			Toast.makeText(this, getString(R.string.recording_fail), Toast.LENGTH_LONG).show();

		}
		
		byte[] snapshot = null;
		Bitmap bmp = mCamera.Snapshot(mSelectedChannel);
		if (bmp != null) {
			try {
				bmp = compressImage(mCamera.Snapshot(mSelectedChannel));
//				if (bmp.getWidth() * bmp.getHeight() > THUMBNAIL_LIMIT_WIDTH
//						* THUMBNAIL_LIMIT_HEIGHT) {
//					if(!bmp.isRecycled())
//					bmp = Bitmap.createScaledBitmap(bmp, THUMBNAIL_LIMIT_WIDTH,
//							THUMBNAIL_LIMIT_HEIGHT, false);
//				}
				snapshot = DatabaseManager.getByteArrayFromBitmap(bmp);
				bmp.recycle();
				
			}catch (OutOfMemoryError E) {
					Glog.D("LiveView", "compressImage OutOfMemoryError" + E);
					System.gc();
					// continue;
			}
			
		}

		DatabaseManager manager = new DatabaseManager(this);
		manager.updateDeviceChannelByUID(mDevUID, mSelectedChannel);

		if (snapshot != null) {
			manager.updateDeviceSnapshotByUID(mDevUID, snapshot);
		}


		if (mCamera != null) {

			if (mIsListening)
				mCamera.LastAudioMode = 1;
			else if (mIsSpeaking)
				mCamera.LastAudioMode = 2;
			else
				mCamera.LastAudioMode = 0;

		}

		/* return values to main page */
		Bundle extras = new Bundle();
		extras.putString("dev_uuid", mDevUUID);
		extras.putString("dev_uid", mDevUID);
		extras.putString("dev_nickname", mDevice.NickName);
		extras.putByteArray("snapshot", snapshot);
		extras.putInt("camera_channel", mSelectedChannel);
		extras.putInt("OriginallyChannelIndex", OriginallyChannelIndex);
		extras.putInt("MonitorIndex", mMonitorIndex);

		Intent intent = new Intent();
		intent.putExtras(extras);
		setResult(RESULT_OK, intent);
		finish();
	}

	private Bitmap compressImage(Bitmap image) {

		Bitmap tempBitmap = image;

		try {

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			image.compress(Bitmap.CompressFormat.JPEG, 5, baos);
			ByteArrayInputStream isBm = new ByteArrayInputStream(
					baos.toByteArray());
			if(image.getWidth() * image.getHeight() > THUMBNAIL_LIMIT_WIDTH * THUMBNAIL_LIMIT_HEIGHT){
				BitmapFactory.Options opts = new BitmapFactory.Options();
				opts.inSampleSize = 4;
				Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, opts);
				return bitmap;
			}else{
				Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
				return bitmap;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return tempBitmap;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int id = item.getItemId();

		if (id == OPT_MENU_ITEM_ALBUM) {

			File folder = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/Snapshot/" + mDevUID);
			// File folder = new
			// File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()
			// + "/Snapshot");
			String[] allFiles = folder.list();

			if (allFiles != null && allFiles.length > 0) {

				if(mSoftMonitor != null)
					mSoftMonitor.deattachCamera();
				if(mHardMonitor != null)
					mHardMonitor.deattachCamera();

				Intent intent = new Intent(LiveViewActivity.this,
						GridViewGalleryActivity.class);
				intent.putExtra("snap", mDevUID);
				intent.putExtra("images_path", folder.getAbsolutePath());
				startActivity(intent);

			} else {
				String msg = LiveViewActivity.this.getText(
						R.string.tips_no_snapshot_found).toString();
				Toast.makeText(LiveViewActivity.this, msg, Toast.LENGTH_SHORT)
						.show();
			}

		} else if (id == OPT_MENU_ITEM_SNAPSHOT) {

			if (mCamera != null && mCamera.isChannelConnected(mSelectedChannel)) {

				if (isSDCardValid()) {

					File rootFolder = new File(Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ "/Snapshot/");
					File targetFolder = new File(rootFolder.getAbsolutePath()
							+ "/" + mDevUID);

					if (!rootFolder.exists()) {
						try {
							rootFolder.mkdir();
						} catch (SecurityException se) {
							super.onOptionsItemSelected(item);
						}
					}

					if (!targetFolder.exists()) {

						try {
							targetFolder.mkdir();
						} catch (SecurityException se) {
							super.onOptionsItemSelected(item);
						}
					}

					final String file = targetFolder.getAbsoluteFile() + "/"
							+ getFileNameWithTime();
					mPhotoFilePath = file;
					if (mCamera != null) {
						mCamera.setSnapshot(mContext, file);
					}

				} else {

					Toast.makeText(
							LiveViewActivity.this,
							LiveViewActivity.this.getText(
									R.string.tips_no_sdcard).toString(),
							Toast.LENGTH_SHORT).show();
				}
			}
		}

		else if (id == OPT_MENU_ITEM_AUDIOCTRL) {

			ArrayList<String> s = new ArrayList<String>();
			s.add(getText(R.string.txtMute).toString());
			if (mCamera.getAudioInSupported(0))
				s.add(getText(R.string.txtListen).toString());
			if (mCamera.getAudioOutSupported(0))
				s.add(getText(R.string.txtSpeak).toString());

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, s);

			final AlertDialog dlg = new AlertDialog.Builder(this).create();
			dlg.setTitle(null);
			dlg.setIcon(null);

			ListView view = new ListView(this);
			view.setAdapter(adapter);
			view.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View view,
						int position, long id) {

					if (mCamera == null)
						return;

					if (position == 1) { // Listening
						mCamera.stopSpeaking(mSelectedChannel);
						mCamera.startListening(mSelectedChannel, mIsRecording);
						mIsListening = true;
						mIsSpeaking = false;
					} else if (position == 2) { // Speaking
						mCamera.stopListening(mSelectedChannel);
						mCamera.startSpeaking(mSelectedChannel);
						mIsListening = false;
						mIsSpeaking = true;
					} else if (position == 0) { // Mute
						mCamera.stopListening(mSelectedChannel);
						mCamera.stopSpeaking(mSelectedChannel);
						mIsListening = mIsSpeaking = false;
					}

					dlg.dismiss();
					LiveViewActivity.this.invalidateOptionsMenu();
				}
			});

			dlg.setView(view);
			dlg.setCanceledOnTouchOutside(true);
			dlg.show();

		} else if (id == OPT_MENU_ITEM_AUDIO_IN) {

			if (!mIsListening) {
				mCamera.startListening(mSelectedChannel, mIsRecording);
			} else {
				mCamera.stopListening(mSelectedChannel);
			}

			mIsListening = !mIsListening;

			this.invalidateOptionsMenu();

		} else if (id == OPT_MENU_ITEM_AUDIO_OUT) {

			if (!mIsSpeaking) {
				mCamera.startSpeaking(mSelectedChannel);
			} else {
				mCamera.stopSpeaking(mSelectedChannel);
			}

			mIsSpeaking = !mIsSpeaking;

			this.invalidateOptionsMenu();
		}

		return super.onOptionsItemSelected(item);
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

	@Override
	public void receiveFrameData(final Camera camera, int avChannel, Bitmap bmp) {

		if (mCamera == camera && avChannel == mSelectedChannel) {
			if (bmp.getWidth() != mVideoWidth
					|| bmp.getHeight() != mVideoHeight) {
				mVideoWidth = bmp.getWidth();
				mVideoHeight = bmp.getHeight();
				
				reScaleMonitor();
			}
		}

	}

	@Override
	public void receiveFrameInfo(final Camera camera, int avChannel,
			long bitRate, int frameRate, int onlineNm, int frameCount,
			int incompleteFrameCount) {

		if (mCamera == camera && avChannel == mSelectedChannel) {

			mVideoFPS = frameRate;
			mVideoBPS = bitRate;
			mOnlineNm = onlineNm;
			mFrameCount = frameCount;
			mIncompleteFrameCount = incompleteFrameCount;

			Bundle bundle = new Bundle();
			bundle.putInt("avChannel", avChannel);

			Message msg = handler.obtainMessage();
			msg.what = STS_CHANGE_CHANNEL_STREAMINFO;
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
	}

	@Override
	public void receiveChannelInfo(final Camera camera, int avChannel,
			int resultCode) {

		if (mCamera == camera && avChannel == mSelectedChannel) {
			Bundle bundle = new Bundle();
			bundle.putInt("avChannel", avChannel);

			Message msg = handler.obtainMessage();
			msg.what = resultCode;
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
	}

	@Override
	public void receiveSessionInfo(final Camera camera, int resultCode) {

		if (mCamera == camera) {
			Bundle bundle = new Bundle();
			Message msg = handler.obtainMessage();
			msg.what = resultCode;
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
	}

	@Override
	public void receiveIOCtrlData(final Camera camera, int avChannel,
			int avIOCtrlMsgType, byte[] data) {

		if (mCamera == camera) {
			Bundle bundle = new Bundle();
			bundle.putInt("avChannel", avChannel);
			bundle.putByteArray("data", data);

			Message msg = handler.obtainMessage();
			msg.what = avIOCtrlMsgType;
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			Bundle bundle = msg.getData();
			int avChannel = bundle.getInt("avChannel");

			St_SInfo stSInfo = new St_SInfo();
			IOTCAPIs.IOTC_Session_Check(mCamera.getMSID(), stSInfo);

			switch (msg.what) {

			case STS_CHANGE_CHANNEL_STREAMINFO:

				if (txtResolution != null)
					txtResolution.setText(String.valueOf(mVideoWidth) + "x"
							+ String.valueOf(mVideoHeight));
				
				if (txtFrameRate != null)
					txtFrameRate.setText(String.valueOf(mVideoFPS));

				if (txtBitRate != null)
					txtBitRate.setText(String.valueOf(mVideoBPS) + "Kbps");

				if (txtOnlineNumber != null)
					txtOnlineNumber.setText(String.valueOf(mOnlineNm));

				if (txtFrameCount != null)
					txtFrameCount.setText(String.valueOf(mFrameCount));

				if (txtIncompleteFrameCount != null)
					txtIncompleteFrameCount.setText(String
							.valueOf(mIncompleteFrameCount));

				if (txtConnectionMode != null)
					txtConnectionMode
							.setText(getSessionMode(mCamera != null ? mCamera
									.getSessionMode() : -1)
									+ " C: "
									+ IOTCAPIs.IOTC_Get_Nat_Type()
									+ ", D: "
									+ stSInfo.NatType
									+ ",R"
									+ mCamera.getbResend());

				if (txtRecvFrmPreSec != null)
					txtRecvFrmPreSec.setText(String.valueOf(mCamera
							.getRecvFrmPreSec()));

				if (txtDispFrmPreSeco != null)
					txtDispFrmPreSeco.setText(String.valueOf(mCamera
							.getDispFrmPreSec()));

				if (txtPerformance != null)
					txtPerformance
							.setText(getPerformance((int) (((float) mCamera
									.getDispFrmPreSec() / (float) mCamera
									.getRecvFrmPreSec()) * 100)));
				
				if(txtCodecMode != null) {
					int codec_string = R.string.hardware_decode;
					if(mSoftCodecDefault || mSoftCodecCurrent)
						codec_string = R.string.software_decode;
					
					txtCodecMode.setText(codec_string);
				}
					

				break;

			case STS_SNAPSHOT_SCANED:

				Toast.makeText(LiveViewActivity.this,
						getText(R.string.tips_snapshot_ok), Toast.LENGTH_SHORT)
						.show();

				break;

			case Camera.CONNECTION_STATE_CONNECTING:

				if (!mCamera.isSessionConnected()
						|| !mCamera.isChannelConnected(mSelectedChannel)) {

					mConnStatus = getText(R.string.connstus_connecting)
							.toString();

					if (txtConnectionStatus != null) {
						txtConnectionStatus.setText(mConnStatus);
						txtConnectionStatus
								.setBackgroundResource(R.drawable.live_unknow);

					}
				}

				break;

			case Camera.CONNECTION_STATE_CONNECTED:

				if (mCamera.isSessionConnected()
						&& avChannel == mSelectedChannel
						&& mCamera.isChannelConnected(mSelectedChannel)) {

					mConnStatus = getText(R.string.connstus_connected)
							.toString();

					if (txtConnectionStatus != null) {
						txtConnectionStatus.setText(mConnStatus);
						txtConnectionStatus
								.setBackgroundResource(R.drawable.live_online);
					}

					LiveViewActivity.this.invalidateOptionsMenu();
				}

				break;

			case Camera.CONNECTION_STATE_DISCONNECTED:

				mConnStatus = getText(R.string.connstus_disconnect).toString();

				if (txtConnectionStatus != null) {
					txtConnectionStatus.setText(mConnStatus);
					txtConnectionStatus
							.setBackgroundResource(R.drawable.live_offline);
				}

				LiveViewActivity.this.invalidateOptionsMenu();

				break;

			case Camera.CONNECTION_STATE_UNKNOWN_DEVICE:

				mConnStatus = getText(R.string.connstus_unknown_device)
						.toString();

				if (txtConnectionStatus != null) {
					txtConnectionStatus.setText(mConnStatus);
					txtConnectionStatus
							.setBackgroundResource(R.drawable.live_offline);
				}

				LiveViewActivity.this.invalidateOptionsMenu();

				break;

			case Camera.CONNECTION_STATE_TIMEOUT:

				if (mCamera != null) {

					mCamera.stopSpeaking(mSelectedChannel);
					mCamera.stopListening(mSelectedChannel);
					mCamera.stopShow(mSelectedChannel);
					mCamera.stop(mSelectedChannel);
					mCamera.disconnect();
					mCamera.connect(mDevUID);
					mCamera.start(Camera.DEFAULT_AV_CHANNEL,
							mDevice.View_Account, mDevice.View_Password);
					mCamera.startShow(mSelectedChannel, true, mSoftCodecDefault);

					mCamera.connect(mDevUID);
					mCamera.start(Camera.DEFAULT_AV_CHANNEL,
							mDevice.View_Account, mDevice.View_Password);
					mCamera.sendIOCtrl(
							Camera.DEFAULT_AV_CHANNEL,
							AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETSUPPORTSTREAM_REQ,
							SMsgAVIoctrlGetSupportStreamReq.parseContent());
					mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
							AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_REQ,
							AVIOCTRLDEFs.SMsgAVIoctrlDeviceInfoReq
									.parseContent());
					mCamera.sendIOCtrl(
							Camera.DEFAULT_AV_CHANNEL,
							AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETAUDIOOUTFORMAT_REQ,
							AVIOCTRLDEFs.SMsgAVIoctrlGetAudioOutFormatReq
									.parseContent());
					mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
							AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_TIMEZONE_REQ,
							AVIOCTRLDEFs.SMsgAVIoctrlTimeZone.parseContent());
					if (mIsListening)
						mCamera.startListening(mSelectedChannel, mIsRecording);
				}

				break;

			case Camera.CONNECTION_STATE_CONNECT_FAILED:

				mConnStatus = getText(R.string.connstus_connection_failed)
						.toString();

				if (txtConnectionStatus != null) {
					txtConnectionStatus.setText(mConnStatus);
					txtConnectionStatus
							.setBackgroundResource(R.drawable.live_offline);
				}

				LiveViewActivity.this.invalidateOptionsMenu();

				break;

			case Camera.CONNECTION_STATE_WRONG_PASSWORD:

				mConnStatus = getText(R.string.connstus_wrong_password)
						.toString();

				if (txtConnectionStatus != null) {
					txtConnectionStatus.setText(mConnStatus);
					txtConnectionStatus
							.setBackgroundResource(R.drawable.live_offline);
				}

				break;

			case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETSUPPORTSTREAM_RESP:

				LiveViewActivity.this.invalidateOptionsMenu();

				break;
				
			}
		}
	};

	@Override
	public View makeView() {
		TextView t = new TextView(this);
		return t;
	}

	@Override
	public void OnClick() {
		// TODO Auto-generated method stub
		if (mIsListening || isOpenLinQVGA || isOpenLinEmode) {
			return;
		}

		if (isShowToolBar) {
			isShowToolBar = false;
			if (toolbar_layout != null)
				toolbar_layout.setVisibility(View.GONE);
		} else {
			isShowToolBar = true;
			if (toolbar_layout != null)
				toolbar_layout.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (mCamera != null) {
				mCamera.startSpeaking(mSelectedChannel);
				mCamera.stopListening(mSelectedChannel);
			}
			break;
		case MotionEvent.ACTION_UP:
			if (mCamera != null) {
				mCamera.stopSpeaking(mSelectedChannel);
				mCamera.startListening(mSelectedChannel, mIsRecording);
			}
			break;
		}
		return false;
	}

	@Override
	public void OnSnapshotComplete() {
		// TODO Auto-generated method stub
		MediaScannerConnection.scanFile(LiveViewActivity.this,
				new String[] { mPhotoFilePath.toString() },
				new String[] { "image/*" },
				new MediaScannerConnection.OnScanCompletedListener() {
					public void onScanCompleted(String path, Uri uri) {
						Log.i("ExternalStorage", "Scanned " + path + ":");
						Log.i("ExternalStorage", "-> uri=" + uri);
						Message msg = handler.obtainMessage();
						msg.what = STS_SNAPSHOT_SCANED;
						handler.sendMessage(msg);
					}
				});
	}

	private long getAvailaleSize() {

		File path = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();

		return (availableBlocks * blockSize) / 1024 / 1024;
	}

	@Override
	public void receiveFrameDataForMediaCodec(Camera camera, int avChannel,
			byte[] buf, int length, int pFrmNo, byte[] pFrmInfoBuf,
			boolean isIframe, int codecId) {
		// TODO Auto-generated method stub

		if (mHardMonitor != null
				&& mHardMonitor.getClass().equals(MediaCodecMonitor.class)) {
			
			if((mVideoWidth != ((MediaCodecMonitor) mHardMonitor).getVideoWidth() ||
				mVideoHeight != ((MediaCodecMonitor) mHardMonitor).getVideoHeight())) {
				
				mVideoWidth = ((MediaCodecMonitor) mHardMonitor).getVideoWidth();
				mVideoHeight = ((MediaCodecMonitor) mHardMonitor).getVideoHeight();

				reScaleMonitor();
			}
			
		}


	}

	@Override
	public void Unavailable() {
		if(unavailable)
			return;
		
		unavailable = true;
		mSoftCodecCurrent = true;
		if (mSoftMonitor != null) {
			mSoftMonitor.deattachCamera();
		}
		if (mHardMonitor != null) {
			mHardMonitor.deattachCamera();
		}

		Configuration cfg = getResources().getConfiguration();

		if (cfg.orientation == Configuration.ORIENTATION_PORTRAIT) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					
					if(mCamera != null) {
						
						mCamera.stopShow(mSelectedChannel);
						handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								mCamera.startShow(mSelectedChannel, true, true);
								setupPortraitView(true);
							}
						}, 1000);
						
					}
						
					mCHTextView.setText("CH" + (mSelectedChannel + 1));
				}
			});
		}
		else if (cfg.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					setupLandscapeView(true);
				}
			});
		}
	}

	@Override
	public void zoomSurface(final float scale) {

		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		final int screenWidth = size.x;
		final int screenHeight = size.y;

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				SurfaceView surfaceView = (SurfaceView) mHardMonitor;
				android.view.ViewGroup.LayoutParams lp = surfaceView.getLayoutParams();

				lp.width = (int) (mMiniVideoWidth * scale);
				lp.height = (int) (mMiniVideoHeight * scale);
				surfaceView.setLayoutParams(lp);
				
				int paddingLeft = 0;
				int paddingTop = 0;
				
				if(mFrameMode == FrameMode.LANDSCAPE_COL_MAJOR) {
					paddingTop = (screenHeight - lp.height) / 2;
					if(paddingTop < 0)
						paddingTop = 0;
				}
				else if(mFrameMode == FrameMode.LANDSCAPE_ROW_MAJOR) {
					paddingLeft = (screenWidth - lp.width) / 2;
					if(paddingLeft < 0)
						paddingLeft = 0;
				}
				
				myScrollView.setPadding(paddingLeft, paddingTop, 0, 0);
			}
		});
		
	}
	
	private void reScaleMonitor() {
		
		
		if(mVideoHeight == 0 || mVideoWidth == 0)
			return;
		
		
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		final int screenWidth = size.x;
		final int screenHeight = size.y;

		
		final SurfaceView surfaceView;
		if(mSoftCodecCurrent){
			surfaceView = (SurfaceView) mSoftMonitor;
		}else{
			surfaceView = (SurfaceView) mHardMonitor;
		}
		
		if(surfaceView == null || myScrollView == null || myHorScrollView == null)
			return;
		
		
		/**
		 * portrait mode
		 */
		if(screenHeight >= screenWidth) {
			
			mFrameMode = FrameMode.PORTRAIT;
			surfaceView.getLayoutParams().width = screenWidth;
			surfaceView.getLayoutParams().height = (int) (screenWidth * mVideoHeight / (float)mVideoWidth);
			
			
		}
		/**
		 * landscape mode
		 */
		else {
			
			
			if(surfaceView.getLayoutParams().width > screenWidth) {
				/**
				 * up down space
				 */
				mFrameMode = FrameMode.LANDSCAPE_COL_MAJOR;
				surfaceView.getLayoutParams().width = screenWidth;
				surfaceView.getLayoutParams().height = (int) (screenWidth * mVideoHeight / (float)mVideoWidth);
				final int scrollViewHeight = surfaceView.getLayoutParams().height;
				handler.post(new Runnable() {
					@Override
					public void run() {
						if(mSoftCodecCurrent)
							mSoftMonitorLayout.setPadding(0, (screenHeight - scrollViewHeight) / 2, 0, 0);
						else
							myScrollView.setPadding(0, (screenHeight - scrollViewHeight) / 2, 0, 0);
					}
				});
				
			}
			else {
				/**
				 * left right space
				 */
				mFrameMode = FrameMode.LANDSCAPE_ROW_MAJOR;
				surfaceView.getLayoutParams().height = screenHeight;
				surfaceView.getLayoutParams().width = (int) (screenHeight * mVideoWidth / (float)mVideoHeight);
				final int scrollViewWidth = surfaceView.getLayoutParams().width;
				handler.post(new Runnable() {
					@Override
					public void run() {
						if(mSoftCodecCurrent)
							mSoftMonitorLayout.setPadding((screenWidth - scrollViewWidth) / 2, 0, 0, 0);
						else
							myScrollView.setPadding((screenWidth - scrollViewWidth) / 2, 0, 0, 0);
					}
				});
			}
		}

		mMiniVideoHeight = surfaceView.getLayoutParams().height;
		mMiniVideoWidth = surfaceView.getLayoutParams().width;
		
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				surfaceView.setLayoutParams(surfaceView.getLayoutParams());
			}
		});

//		handler.post(new Runnable() {
//			@Override
//			public void run() {
//				if (myScrollView.getMeasuredHeight() == 0) {
//					handler.postDelayed(this, 200);
//				} else {
//				}
//			}
//		});
		
	}

}
