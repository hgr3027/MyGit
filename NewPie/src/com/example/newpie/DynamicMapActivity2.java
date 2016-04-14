package com.example.newpie;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.androidpn.client.Constants;
import android.support.v4.view.ViewPager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapTouchListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.newpie.bean.ActivityManager;
import com.example.newpie.bean.MapUserTrail;
import com.example.newpie.bean.PointInformation;
import com.example.newpie.bean.User;
import com.example.newpie.utils.HttpConnection;
import com.example.newpie.utils.MyDatePickerDialog;
import com.example.newpie.utils.ViewPagerAdapter;

@SuppressLint("NewApi")
public class DynamicMapActivity2 extends Activity implements OnClickListener,
		OnTouchListener, OnMarkerClickListener, OnMapTouchListener,
		OnMapClickListener {

	// public static final String MYURL = "http://120.27.42.95:80/tecoa/";
	public static final String MYURL2 = "http://120.27.42.95:8080/Androidpn-tomcat/";
	public static final String MY_SETTING = "accessrecord";
	static {
		FULL_TASK_EXECUTOR = (ExecutorService) Executors
				.newSingleThreadExecutor();
	}
	public static final int SHOW_DATAPICK = 0;
	private static final int PHOTO = 0;
	private static final int ADDRESSLIST = 1;
	private LocationClient mLocationClient;
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private ImageView mapPhotoImageView;
	private Button mapOCButton;
	private View mapTitle;
	private ImageView iv1;
	private TextView tv1;
	private TextView tv2;
	private TextView tv3;
	private TextView popTime;
	private TextView popSelectTime;
	private ImageView popPhoto;
	private TextView popDepartement;
	private TextView popName;
	private TextView popStopTime;
	private TextView popoc;
	private ImageView mapWaitImageView;
	private AnimationDrawable animationDrawable;
	private Boolean upSucc;
	private static ExecutorService FULL_TASK_EXECUTOR;
	public int i = 1;
	private String currYear;
	private String currMonth;
	private String currDate;
	private String currUserCompany;
	private String currUserDepartment;
	private String currUserId;
	private String SDCardRoot = new String();
	private ViewPager vp;
	private MapUserTrail currUserCurrDateData = new MapUserTrail();
	private MapUserTrail newUserCurrDateData = new MapUserTrail();
	private String ifSDMark; // SD卡数据状态
	private ImageView mapOCButton2;
	private Calendar c;
	private String ifOpenPhoto = "0";
	private String upPhotoName;
	private String photoId;
	private String ifPhotoLLmark = "0";
	private double phoroLatitude;
	private double photoLongitude;
	public static List<String> sharedPreferencesList = new ArrayList<String>();
	private List<HashMap<String, String>> currUserCurrDateMainPhotoUrl = new ArrayList<HashMap<String, String>>();
	private int currUserCurrDateMainPhotoNum = 0;
	private int currUserCurrDateAllPhotoNum = 0;
	private Boolean closeSucc;
	private String ifSendmark = "0";
	private String pointInformationPhotoId;
	private List<String> loadAllPhotoName;
	private List<String> loadNoPhoto;
	private List<View> listViews;
	private String returnPhotomark = "0";
	private LinearLayout photoCloseLL;
	private double nomalLatitude;
	private double normalLongitude;
	private String loadMark = "1";
	public List<String> userList = new ArrayList<String>();
	private SharedPreferences sharedPrefs;
	private String ifOpenNormal;
	private String photoOpen = "1";
	private User user;
	private String currLoginUserCompany;
	private String currLoginUserId;
	private String currLoginUserName;
	private ActivityManager exitM;
	private String ifcurrDate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		user = (User) this.getIntent().getSerializableExtra("user");
		currUserDepartment = user.getDepartment();
		currLoginUserCompany = user.getCompany().toString().toLowerCase();
		currLoginUserId = user.getUserName();
		currLoginUserName = user.getFullName();
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_dynamicmap2);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		init();
		aniStop();
		clearSubscribe();
		registerBoradcastReceiver();
		exitM = ActivityManager.getInstance();
		exitM.addActivity(DynamicMapActivity2.this);
	}

	public void registerBoradcastReceiver() {
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction("AndroidPnBoardCast");
		registerReceiver(mBroadcastReceiver, myIntentFilter);
	}

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			String queryCurrYear = null;
			String queryCurrMonth = null;
			String queryCurrDate = null;
			String queryCurrUserId = new String();
			String queryCurrUserCompany = new String();
			if (action.equals("AndroidPnBoardCast")) {
				newUserCurrDateData = (MapUserTrail) intent
						.getSerializableExtra("mt");
				queryCurrYear = newUserCurrDateData.getCurrTime()
						.get("currYear").toString();
				queryCurrMonth = newUserCurrDateData.getCurrTime()
						.get("currMonth").toString();
				queryCurrDate = newUserCurrDateData.getCurrTime()
						.get("currDate").toString();

				queryCurrUserId = newUserCurrDateData.getMapUserTrailList()
						.get(0).get("currUserId").toString();
				queryCurrUserCompany = newUserCurrDateData
						.getMapUserTrailList().get(0).get("currUserCompany")
						.toString();

				MapUserTrail queryCurrUserCurrDateData;
				List<Map<String, Object>> bakList;
				ifSendmark = "1";
				if ((!"SD card Error".equals(getSDPath()))) {
					File file = new File(SDCardRoot + "NewPai" + File.separator
							+ "download" + File.separator + "map"
							+ File.separator + queryCurrUserCompany
							+ File.separator + queryCurrUserId + File.separator
							+ "basic" + File.separator + queryCurrYear
							+ queryCurrMonth + queryCurrDate + File.separator
							+ "basic.out");
					if (file.exists()) {
						queryCurrUserCurrDateData = new HttpConnection(
								DynamicMapActivity2.this)
								.loadCurrUserCurrDateDataSD(SDCardRoot
										+ "NewPai" + File.separator
										+ "download" + File.separator + "map"
										+ File.separator + queryCurrUserCompany
										+ File.separator + queryCurrUserId
										+ File.separator + "basic"
										+ File.separator + queryCurrYear
										+ queryCurrMonth + queryCurrDate
										+ File.separator + "basic.out");
						bakList = queryCurrUserCurrDateData
								.getMapUserTrailList();
					} else {
						queryCurrUserCurrDateData = new MapUserTrail();
						bakList = new ArrayList<Map<String, Object>>();
					}
					for (int i = 0; i < newUserCurrDateData
							.getMapUserTrailList().size(); i++) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("currUserId", newUserCurrDateData
								.getMapUserTrailList().get(i).get("currUserId"));
						map.put("currUserName",
								newUserCurrDateData.getMapUserTrailList()
										.get(i).get("currUserName"));
						map.put("latitude", newUserCurrDateData
								.getMapUserTrailList().get(i).get("latitude"));
						map.put("longitude", newUserCurrDateData
								.getMapUserTrailList().get(i).get("longitude"));
						map.put("reachTime", newUserCurrDateData
								.getMapUserTrailList().get(i).get("reachTime"));
						map.put("ifPhotomark", newUserCurrDateData
								.getMapUserTrailList().get(i)
								.get("ifPhotomark"));
						map.put("photoId", newUserCurrDateData
								.getMapUserTrailList().get(i).get("photoId"));
						map.put("photoTime", newUserCurrDateData
								.getMapUserTrailList().get(i).get("d"));
						map.put("currUserCompany",
								newUserCurrDateData.getMapUserTrailList()
										.get(i).get("currUserCompany"));
						map.put("photoNum", newUserCurrDateData
								.getMapUserTrailList().get(i).get("photoNum"));
						map.put("stopTime", newUserCurrDateData
								.getMapUserTrailList().get(i).get("stopTime"));
						map.put("photoIfIndex",
								newUserCurrDateData.getMapUserTrailList()
										.get(i).get("photoIfIndex"));
						map.put("photoUrl", newUserCurrDateData
								.getMapUserTrailList().get(i).get("photoUrl"));
						map.put("currUserDepartment",
								newUserCurrDateData.getMapUserTrailList()
										.get(i).get("currUserDepartment"));
						bakList.add(map);
					}
					queryCurrUserCurrDateData.setMapUserTrailList(bakList);
					queryCurrUserCurrDateData.setCurrTime(newUserCurrDateData
							.getCurrTime());
					saveBasic(queryCurrUserCurrDateData, SDCardRoot + "NewPai"
							+ File.separator + "download" + File.separator
							+ "map" + File.separator + queryCurrUserCompany
							+ File.separator + queryCurrUserId + File.separator
							+ "basic" + File.separator + queryCurrYear
							+ queryCurrMonth + queryCurrDate, SDCardRoot
							+ "NewPai" + File.separator + "download"
							+ File.separator + "map" + File.separator
							+ queryCurrUserCompany + File.separator + queryCurrUserId
							+ File.separator + "basic" + File.separator
							+ queryCurrYear + queryCurrMonth + queryCurrDate
							+ File.separator + "basic.out");
				} else {
					Toast.makeText(DynamicMapActivity2.this,
							getResources().getString(R.string.nosdcard),
							Toast.LENGTH_SHORT).show();
				}
			}

			if (queryCurrUserId.equals(currUserId)
					&& queryCurrUserCompany.equals(currUserCompany))
			{
				currUserCurrDateData = new HttpConnection(
						DynamicMapActivity2.this)
						.loadCurrUserCurrDateDataSD(SDCardRoot + "NewPai"
								+ File.separator + "download" + File.separator
								+ "map" + File.separator + currUserCompany
								+ File.separator + currUserId + File.separator
								+ "basic" + File.separator + queryCurrYear
								+ queryCurrMonth + queryCurrDate
								+ File.separator + "basic.out");
				String ifHavePhoto = "0";
				for (int i = 0; i < currUserCurrDateData.getMapUserTrailList()
						.size(); i++) {
					if ("1".equals(currUserCurrDateData.getMapUserTrailList()
							.get(i).get("ifPhotomark"))) {
						ifHavePhoto = "1";
					}
				}
				if ("0".equals(ifHavePhoto)) {
					initMapTrail(currUserCurrDateData, "0");
				} else {
					loadCurrUserCurrDateMainPhoto("0");
				}
			}
		}
	};

	private void clearSubscribe() {
		aniStart();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("currLoginUserCompany", user.getCompany().toString()
				.toLowerCase());
		map.put("currLoginUserId", user.getUserName().toString().toLowerCase());
		new clearSubscribeAsyncTask().execute(map);
	}

	class clearSubscribeAsyncTask extends
			AsyncTask<Map<String, Object>, String, String> {

		@Override
		protected void onPostExecute(String result) {
			aniStop();
			super.onPostExecute(result);
		}

		@Override
		protected String doInBackground(Map<String, Object>... arg0) {
			HttpConnection connection = new HttpConnection(
					DynamicMapActivity2.this);
			connection.openSend(arg0[0], MYURL2
					+ "androidConreoller.do?action=clearSubscribe");
			return null;
		}
	}

	private void init() {
		initTime();
		mapWaitImageView = (ImageView) findViewById(R.id.mapwaitimageview);
		mapTitle = findViewById(R.id.maptitle);
		iv1 = (ImageView) mapTitle.findViewById(R.id.bakimageview);
		tv1 = (TextView) mapTitle.findViewById(R.id.baktextview);
		tv2 = (TextView) mapTitle.findViewById(R.id.titletext);
		tv3 = (TextView) mapTitle.findViewById(R.id.functext);
		mapPhotoImageView = (ImageView) findViewById(R.id.mapphotoimage);
		mapOCButton = (Button) findViewById(R.id.mapocbutton);
		mapOCButton2 = (ImageView) findViewById(R.id.mapphotoimage2);
		tv1.setText(getResources().getString(R.string.maplefttext));
		tv2.setText(getResources().getString(R.string.mapmiddletext));
		tv3.setText(getResources().getString(R.string.maprighttext));
		vp = (ViewPager) findViewById(R.id.vp);
		photoCloseLL = (LinearLayout) findViewById(R.id.photoclosell);
		iv1.setOnClickListener(this);
		tv1.setOnClickListener(this);
		tv2.setOnClickListener(this);
		tv3.setOnClickListener(this);
		mapPhotoImageView.setOnTouchListener(this);
		mapOCButton.setOnTouchListener(this);
		mapOCButton2.setOnTouchListener(this);
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		mLocationClient = new LocationClient(getApplicationContext());
		mLocationClient.registerLocationListener(locationListener);
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);
		option.setCoorType("bd09ll");
		option.setOpenGps(true);
		int span = 1000 * 30;
		option.setScanSpan(span);
		option.setIsNeedAddress(false);
		mLocationClient.setLocOption(option);
		mBaiduMap.setOnMapTouchListener(this);
		mBaiduMap.setOnMapClickListener(this);
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker arg0) {

				PointInformation pointInformation = (PointInformation) arg0
						.getExtraInfo().get("info");
				View view = LayoutInflater.from(getApplicationContext())
						.inflate(R.layout.activity_mappop, null);

				popTime = (TextView) view.findViewById(R.id.poptime);
				popSelectTime = (TextView) view
						.findViewById(R.id.popselecttime);
				popPhoto = (ImageView) view.findViewById(R.id.popphoto);
				popDepartement = (TextView) view
						.findViewById(R.id.popdepartement);
				popName = (TextView) view.findViewById(R.id.popname);
				popStopTime = (TextView) view.findViewById(R.id.popstoptime);
				popoc = (TextView) view.findViewById(R.id.popoc);
				String t = pointInformation.getPointTime();
				popTime.setText(t.toString());
				popSelectTime.setText(getResources().getString(
						R.string.selectdatastring));
				popSelectTime.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Calendar c = Calendar.getInstance();
						MyDatePickerDialog datePickerView = new MyDatePickerDialog(
								DynamicMapActivity2.this,
								AlertDialog.THEME_HOLO_LIGHT,
								new MyDatePickerDialog.OnDateSetListener() {
									@Override
									public void onDateSet(DatePicker view,
											int year, int monthOfYear,
											int dayOfMonth) {
										// Toast.makeText(
										// DynamicMapActivity2.this,
										// year + "-" + (monthOfYear + 1)
										// + "-" + dayOfMonth,
										// Toast.LENGTH_SHORT).show();
										aniStart();
										mBaiduMap.clear();
										currYear = year + "";
										currMonth = monthOfYear + 1 + "";
										currDate = dayOfMonth + "";
										if (currMonth.length() == 1) {
											currMonth = "0" + currMonth;
										}
										if (currDate.length() == 1) {
											currDate = "0" + currDate;
										}
										new loadCurrUserCurrDateDataAsyncTask()
												.execute("loadCurrUserCurrDateData");
									}
								}, c.get(Calendar.YEAR), c.get(Calendar.MONTH),
								c.get(Calendar.DAY_OF_MONTH));
						datePickerView.myShow();

					}
				});
				Bitmap bm = pointInformation.getIndexBitmap();
				pointInformationPhotoId = pointInformation.getPhotoId();
				popPhoto.setImageBitmap(reduce(bm, 100, 200, false));
				popPhoto.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						new loadAllPhotoAsyncTask()
								.execute(pointInformationPhotoId.toString());
					}
				});
				popDepartement.setText(pointInformation.getDepartment()
						.toString());
				popName.setText(pointInformation.getName().toString());
				popStopTime.setText(pointInformation.getStayTime().toString());
				popoc.setText(getResources().getString(R.string.oc));

				popoc.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						new openSendAsyncTask().execute(currUserId,
								currUserCompany);
					}
				});

				LatLng ll = arg0.getPosition();
				InfoWindow mInfoWindow = new InfoWindow(view, new LatLng(
						pointInformation.getLatitude(), pointInformation
								.getLongitude()), -50);
				mBaiduMap.showInfoWindow(mInfoWindow);

				MapStatusUpdate m = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.setMapStatus(m);
				mBaiduMap.showInfoWindow(mInfoWindow);
				return true;
			}
		});
		clearSubscribe();
	}

	class openSendAsyncTask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... arg0) {
			HttpConnection connection = new HttpConnection(
					DynamicMapActivity2.this);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("currUserId", arg0[0]);
			map.put("currUserCompany", arg0[1]);
			connection.openSend(map, MYURL2
					+ "androidConreoller.do?action=openSend");
			return null;
		}
	}

	/**
	 * 照片浏览
	 * 
	 * @author 韩国瑞
	 * 
	 */
	class loadAllPhotoAsyncTask extends AsyncTask<String, String, String> {

		@Override
		protected void onPostExecute(String result) {
			if (loadAllPhotoName.size() != 0) {
				loadNoPhoto = checkNoPhotoName(loadAllPhotoName);
				if (loadNoPhoto.size() != 0) {
					for (int i = 0; i < loadNoPhoto.size(); i++) {
						new loadCurrUserCurrDateAllPhotoAsyncTask()
								.executeOnExecutor(
										FULL_TASK_EXECUTOR,
										loadNoPhoto.get(i),
										loadNoPhoto
												.get(i)
												.substring(
														loadNoPhoto.get(i)
																.lastIndexOf(
																		"\\") + 1,
														loadNoPhoto.get(i)
																.length())
												.replace("\\", File.separator));
					}
				} else {
					initVp(loadAllPhotoName);
				}
			}
			super.onPostExecute(result);
		}

		@Override
		protected String doInBackground(String... arg0) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("photoId", arg0[0]);
			map.put("currYear", currYear);
			map.put("currMonth", currMonth);
			map.put("currDate", currDate);
			map.put("currUserId", currUserId);
			map.put("currUserCompany", currUserCompany);
			loadAllPhotoName = allPhotoName(map);
			return null;
		}
	}

	private List<String> allPhotoName(Map<String, Object> map) {
		List<String> loadAllPhotoName = new ArrayList<String>();
		for (int i = 0; i < currUserCurrDateData.getMapUserTrailList().size(); i++) {
			if ("1".equals(currUserCurrDateData.getMapUserTrailList().get(i)
					.get("ifPhotomark"))
					&& map.get("photoId").equals(
							currUserCurrDateData.getMapUserTrailList().get(i)
									.get("photoId"))) {
				loadAllPhotoName.add(currUserCurrDateData.getMapUserTrailList()
						.get(i).get("photoUrl").toString());

			}
		}
		return loadAllPhotoName;
	}

	private List<String> checkNoPhotoName(List<String> loadAllPhotoName) {

		List<String> noPhotoName = new ArrayList<String>();
		for (int i = 0; i < loadAllPhotoName.size(); i++) {
			if (checkData2(currUserCompany
					+ File.separator
					+ currUserId
					+ File.separator
					+ "pic"
					+ File.separator
					+ currYear
					+ currMonth
					+ currDate
					+ File.separator
					+ loadAllPhotoName
							.get(i)
							.substring(
									loadAllPhotoName.get(i).lastIndexOf("\\") + 1,
									loadAllPhotoName.get(i).length())
							.replace("\\", File.separator))) {
				noPhotoName.add(loadAllPhotoName.get(i));
			}
		}
		return noPhotoName;
	}



	

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		if ("1".equals(loadMark)) {
			switch (arg0.getId()) {
			case (R.id.mapphotoimage): {
				switch (arg1.getAction()) {
				case (MotionEvent.ACTION_DOWN): {
					mapPhotoImageView.setImageDrawable(getResources()
							.getDrawable(R.drawable.photo2));
					break;
				}
				case (MotionEvent.ACTION_UP): {
					mapPhotoImageView.setImageDrawable(getResources()
							.getDrawable(R.drawable.photo));
					if ("0".equals(ifOpenPhoto)) {
						Toast.makeText(DynamicMapActivity2.this,
								getResources().getString(R.string.nocanphoto),
								Toast.LENGTH_SHORT).show();
					} else {
						mLocationClient.start();
						photoFun();
					}
					break;
				}
				}
				break;
			}

			case (R.id.mapocbutton): {
				switch (arg1.getAction()) {
				case (MotionEvent.ACTION_DOWN): {
					if (mapOCButton.getBackground().getCurrent()
							.getConstantState()

					== getResources().getDrawable(
							R.drawable.mapclosebuttonbackground)
							.getConstantState()) {
						mapOCButton.setBackground(getResources().getDrawable(
								R.drawable.mapclosebuttonbackground2));
					} else if (mapOCButton.getBackground().getCurrent()
							.getConstantState() == getResources().getDrawable(
							R.drawable.mapopenbuttonbackground)
							.getConstantState()) {
						mapOCButton.setBackground(getResources().getDrawable(
								R.drawable.mapopenbuttonbackground2));
					}
					break;
				}
				case (MotionEvent.ACTION_UP): {
					if (mapOCButton.getBackground().getCurrent()
							.getConstantState() == getResources().getDrawable(
							R.drawable.mapopenbuttonbackground2)
							.getConstantState()) {
						mapOCButton.setBackground(getResources().getDrawable(
								R.drawable.mapclosebuttonbackground));
						mapOCButton.setText(getResources().getString(
								R.string.mapclosebuttontext));
						openUploadNormalPointInformation();
						ifOpenNormal = "1";

					} else if (mapOCButton.getBackground().getCurrent()
							.getConstantState() == getResources().getDrawable(
							R.drawable.mapclosebuttonbackground2)
							.getConstantState()) {
						if ("1".equals(photoOpen)) {
							mapOCButton
									.setBackground(getResources().getDrawable(
											R.drawable.mapopenbuttonbackground));
							mapOCButton.setText(getResources().getString(
									R.string.mapopenbuttontext));
							closeUploadNormalPointInformation();
							ifOpenNormal = "0";
						} else {
							mapOCButton
									.setBackground(getResources()
											.getDrawable(
													R.drawable.mapclosebuttonbackground));
							mapOCButton.setText(getResources().getString(
									R.string.mapclosebuttontext));
							closeUploadNormalPointInformation();
							Toast.makeText(
									DynamicMapActivity2.this,
									getResources().getString(
											R.string.nocanphoto4),
									Toast.LENGTH_SHORT).show();
						}
					}
					break;
				}
				}
				break;
			}

			case (R.id.mapphotoimage2): {
				switch (arg1.getAction()) {
				case (MotionEvent.ACTION_DOWN): {
					if (mapOCButton2.getBackground().getCurrent()
							.getConstantState() == getResources().getDrawable(
							R.drawable.open1).getConstantState()) {
						mapOCButton2.setBackground(getResources().getDrawable(
								R.drawable.open2));
					} else if (mapOCButton2.getBackground().getCurrent()
							.getConstantState() == getResources().getDrawable(
							R.drawable.close1).getConstantState()) {
						mapOCButton2.setBackground(getResources().getDrawable(
								R.drawable.close2));
					}
					break;
				}
				case (MotionEvent.ACTION_UP): {

					if (mapOCButton2.getBackground().getCurrent()
							.getConstantState() == getResources().getDrawable(
							R.drawable.open2).getConstantState()) {
						if ("1".equals(ifOpenNormal)) {
							mapOCButton2.setBackground(getResources()
									.getDrawable(R.drawable.close1));

							openPhotoFunction();
							photoOpen = "0";
						} else {
							mapOCButton2.setBackground(getResources()
									.getDrawable(R.drawable.open1));
							Toast.makeText(
									DynamicMapActivity2.this,
									getResources().getString(
											R.string.nocanphoto3),
									Toast.LENGTH_SHORT).show();
						}

					} else if (mapOCButton2.getBackground().getCurrent()
							.getConstantState() == getResources().getDrawable(
							R.drawable.close2).getConstantState()) {
						mapOCButton2.setBackground(getResources().getDrawable(
								R.drawable.open1));
						closePhotoFunction();
						photoOpen = "1";
					}

					break;
				}
				}
				break;
			}
			}
		}
		return true;
	}

	/**
	 * 关闭照相 韩国瑞
	 */
	@SuppressWarnings("unchecked")
	private void closePhotoFunction() {
		ifOpenPhoto = "0";
		ifPhotoLLmark = "0";
		Map<String, String> params = new HashMap<String, String>();
		params.put("photoId", photoId);
		params.put("currUserId", currLoginUserId);
		params.put("currUserCompany", currLoginUserCompany);
		params.put("latitude", phoroLatitude + "");
		params.put("longitude", photoLongitude + "");
		aniStart();
		new closePhotoAsynctask().execute(params);
	}

	/**
	 * 开启照相 韩国瑞
	 */
	private void openPhotoFunction() {
		ifOpenPhoto = "1";
		photoId = UUID.randomUUID().toString().replace("-", "");
		ifPhotoLLmark = "1";
	}

	private void openUploadNormalPointInformation() {
		mLocationClient.start();
	}

	/**
	 * 定位回调 韩国瑞
	 */
	private BDLocationListener locationListener = new BDLocationListener() {
		@Override
		public void onReceiveLocation(BDLocation arg0) {
			Dispose(arg0);
		}

		private void Dispose(BDLocation location) {

			if (location.getLocType() != BDLocation.TypeNone
					&& location.getLocType() != BDLocation.TypeOffLineLocation
					&& location.getLocType() != 62) {
				if ("0".equals(ifPhotoLLmark)) {
					if (location.getLongitude() == photoLongitude
							&& location.getLatitude() == phoroLatitude) {

					} else {
						aniStart();
						UploadNormalPointAsyncTask UploadNormalPointAsyncTaskValue = new UploadNormalPointAsyncTask();
						UploadNormalPointAsyncTaskValue
								.execute(location.getLongitude(),
										location.getLatitude());
						nomalLatitude = location.getLatitude();
						normalLongitude = location.getLongitude();
					}
				} else if ("1".equals(ifPhotoLLmark)) {
					phoroLatitude = location.getLatitude();
					photoLongitude = location.getLongitude();
				}
			}
		}
	};

	/**
	 * 上传点0
	 * 
	 * @author 韩国瑞
	 * 
	 */
	class UploadNormalPointAsyncTask extends AsyncTask<Double, String, String> {

		@Override
		protected void onPostExecute(String result) {
			aniStop();
			super.onPostExecute(result);
		}

		@Override
		protected String doInBackground(Double... arg0) {
			HttpConnection connection = new HttpConnection(
					DynamicMapActivity2.this);
			HashMap<String, Object> m = new HashMap<String, Object>();
			m.put("currUserId", currLoginUserId);
			m.put("currUserName", currLoginUserName);
			m.put("currYear", currYear);
			m.put("currMonth", currMonth);
			m.put("currDate", currDate);
			m.put("currUserCompany", currLoginUserCompany);
			m.put("currUserDepartment", currUserDepartment);
			m.put("latitude", arg0[1]);
			m.put("longitude", arg0[0]);
			m.put("stopTime", "_");
			connection.mapPostConnection(MYURL2
					+ "androidConreoller.do?action=uploadNormalPoint", m);
			return null;
		}
	}

	private void closeUploadNormalPointInformation() {
		mLocationClient.stop();
	}

	private void photoFun() {
		aniStart();
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		upPhotoName = currLoginUserId + "_" + currLoginUserCompany + ".jpg";
		File out = new File(Environment.getExternalStorageDirectory(),
				upPhotoName);
		Uri uri = Uri.fromFile(out);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		startActivityForResult(intent, PHOTO);
	}

	class closePhotoAsynctask extends
			AsyncTask<Map<String, String>, String, String> {

		@Override
		protected void onPostExecute(String result) {
			aniStop();
			super.onPostExecute(result);
		}

		@Override
		protected String doInBackground(Map<String, String>... arg0) {
			HttpConnection httpConnection = new HttpConnection(
					DynamicMapActivity2.this);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("currUserCompany", currLoginUserCompany);
			map.put("currUserDepartment", currUserDepartment);
			map.put("currUserId", currLoginUserId);
			map.put("currUserName", currLoginUserName);
			map.put("currYear", currYear);
			map.put("currMonth", currMonth);
			map.put("currDate", currDate);
			map.put("photoId", photoId);
			map.put("latitude", phoroLatitude);
			map.put("longitude", photoLongitude);
			closeSucc = httpConnection.closePhoto(MYURL2
					+ "androidConreoller.do?action=closePhoto", map);
			return null;
		}
	}

	class upPhotoAsyncTask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			HttpConnection httpConnection = new HttpConnection(
					DynamicMapActivity2.this);

			if (phoroLatitude != 0.0 && photoLongitude != 0.0) {
				System.out.println(phoroLatitude);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("currUserCompany", currLoginUserCompany);
				map.put("currUserDepartment", currUserDepartment);
				map.put("currUserId", currLoginUserId);
				map.put("currUserName", currLoginUserName);
				map.put("photoId", photoId);
				map.put("phoroLatitude", phoroLatitude + "");
				map.put("photoLongitude", photoLongitude + "");

				upSucc = httpConnection.upPhoto(upPhotoName, MYURL2
						+ "androidConreoller.do?action=upPhoto", map);
			} else {
				upSucc = false;
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {

			aniStop();
			if (upSucc) {
				Toast.makeText(DynamicMapActivity2.this,
						getResources().getString(R.string.photosucc),
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(DynamicMapActivity2.this,
						getResources().getString(R.string.photosucchalf),
						Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(result);
		}
	}

	public boolean onMarkerClick(Marker marker) {
		PointInformation pointInformation = (PointInformation) marker
				.getExtraInfo().get("info");
		LatLng llInfo = new LatLng(pointInformation.getLatitude(),
				pointInformation.getLongitude());
		View location = LayoutInflater.from(getApplicationContext()).inflate(
				R.layout.activity_mappop, null);
		InfoWindow mInfoWindow = new InfoWindow(location, llInfo, 0);
		mBaiduMap.showInfoWindow(mInfoWindow);
		return true;
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.baktextview: {

			if ("0".equals(returnPhotomark)) {
				onBackPressed();
			} else {
				mMapView.setVisibility(View.VISIBLE);
				vp.setVisibility(View.GONE);
				tv1.setText(getResources().getString(R.string.maplefttext));
				returnPhotomark = "0";
				tv3.setText(getResources().getString(R.string.maprighttext));
				photoCloseLL.setVisibility(View.VISIBLE);
			}
			break;
		}
		case R.id.bakimageview: {
			if ("0".equals(returnPhotomark)) {
				onBackPressed();
			} else {
				mMapView.setVisibility(View.VISIBLE);
				vp.setVisibility(View.GONE);
				tv1.setText(getResources().getString(R.string.maplefttext));
				returnPhotomark = "0";
				tv3.setText(getResources().getString(R.string.maprighttext));
				photoCloseLL.setVisibility(View.VISIBLE);
			}
			break;
		}
		case R.id.functext: {
			Intent intent = new Intent();
			intent.setClass(DynamicMapActivity2.this, AddressListActivity.class);
			startActivityForResult(intent, ADDRESSLIST);
			overridePendingTransition(R.anim.in, R.anim.out);
			break;
		}
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == PHOTO) {

			if (resultCode == 0) {
				Toast.makeText(DynamicMapActivity2.this,
						getResources().getString(R.string.photoerror),
						Toast.LENGTH_SHORT).show();
				aniStop();
				return;
			} else {
				if (HttpConnection.isNetworkAvailable()) {

					new upPhotoAsyncTask().execute("upphoto");
					return;

				} else {
					Toast.makeText(DynamicMapActivity2.this,
							getResources().getString(R.string.photosucchalf),
							Toast.LENGTH_SHORT).show();
					aniStop();
					return;
				}
			}
		} else if (requestCode == ADDRESSLIST) {

			if (null != data) {
				if (null != data.getExtras()) {
					mBaiduMap.clear();
					aniStart();
					initTime();
					currUserId = data.getExtras().getString("userId");
					currUserCompany = data.getExtras().getString("company")
							.toString().toLowerCase();
					new loadCurrUserCurrDateDataAsyncTask()
							.execute("loadCurrUserCurrDateData");
				}
			}
		}
	}

	class loadCurrUserCurrDateDataAsyncTask extends
			AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... arg0) {
			if ((!"SD card Error".equals(getSDPath()))) {
				if (checkData(currUserCompany + File.separator + currUserId
						+ File.separator + "basic" + File.separator + currYear
						+ currMonth + currDate)) {
					// if
					// (!("no".equals(getSharedPreferencesFunc(currUserCompany
					// + "_" + currUserId)))) {
					// String dateSharedPreferences =
					// getSharedPreferencesFunc(currUserCompany
					// + "_" + currUserId);
					// String spCurrYear = dateSharedPreferences.substring(0,
					// 4);
					// String spCurrMonth = dateSharedPreferences.substring(4,
					// 6);
					// String spCurrDate = dateSharedPreferences.substring(6,
					// 8);
					// loadCurrUserCurrDateDataSD(SDCardRoot + "NewPai"
					// + File.separator + "download"
					// + File.separator + "map" + File.separator
					// + currUserCompany + File.separator
					// + currUserId + File.separator + "basic"
					// + File.separator + currYear + currMonth
					// + currDate + File.separator + "basic.out");
					// ifSDMark = "2";
					// } else {
					ifSDMark = "0";
					// }
				} else {
					loadCurrUserCurrDateDataSD(SDCardRoot + "NewPai"
							+ File.separator + "download" + File.separator
							+ "map" + File.separator + currUserCompany
							+ File.separator + currUserId + File.separator
							+ "basic" + File.separator + currYear + currMonth
							+ currDate + File.separator + "basic.out");
					ifSDMark = "1";
				}
			} else {
				ifSDMark = "2";
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {

			if ("0".equals(ifSDMark)) {
				loadCurrUserCurrDateDataHttp(currUserId, currUserCompany,
						currYear, currMonth, currDate);
			} else if ("1".equals(ifSDMark)) {
				String ifHavePhoto = "0";
				for (int i = 0; i < currUserCurrDateData.getMapUserTrailList()
						.size(); i++) {
					if ("1".equals(currUserCurrDateData.getMapUserTrailList()
							.get(i).get("ifPhotomark"))) {
						ifHavePhoto = "1";
					}
				}
				if ("0".equals(ifHavePhoto)) {
					initMapTrail(currUserCurrDateData, "1");
				} else {
					loadCurrUserCurrDateMainPhoto("1");
				}
			}
			// else if ("2".equals(ifSDMark)) {
			// String ifHavePhoto = "0";
			// for (int i = 0; i < currUserCurrDateData.getMapUserTrailList()
			// .size(); i++) {
			// if ("1".equals(currUserCurrDateData.getMapUserTrailList()
			// .get(i).get("ifPhotomark"))) {
			// ifHavePhoto = "1";
			// }
			// }
			// if ("0".equals(ifHavePhoto)) {
			// initMapTrail(currUserCurrDateData, "0");
			// } else {
			// loadCurrUserCurrDateMainPhoto("0");
			// }
			// }
			else {
				Toast.makeText(DynamicMapActivity2.this,
						getResources().getString(R.string.nosdcard),
						Toast.LENGTH_SHORT).show();
				aniStop();
			}
			super.onPostExecute(result);
		}
	}

	private void loadCurrUserCurrDateMainPhoto(String mark) {

		currUserCurrDateMainPhotoUrl = queryCurrUserCurrDateMainPhotoUrl();
		if (currUserCurrDateMainPhotoUrl.size() != 0) {
			try {
				for (int i = 0; i < currUserCurrDateMainPhotoUrl.size(); i++) {

					if (!(checkData2(currUserCompany
							+ File.separator
							+ currUserId
							+ File.separator
							+ "pic"
							+ File.separator
							+ currYear
							+ currMonth
							+ currDate
							+ File.separator
							+ currUserCurrDateMainPhotoUrl
									.get(i)
									.get("photoUrl")
									.toString()
									.substring(
											currUserCurrDateMainPhotoUrl.get(i)
													.get("photoUrl").toString()
													.lastIndexOf("\\") + 1,
											currUserCurrDateMainPhotoUrl.get(i)
													.get("photoUrl").toString()
													.length())
									.replace("\\", File.separator)))) {
						// if (null == BitmapFactory.decodeFile(SDCardRoot
						// + "NewPai"
						// + File.separator
						// + "download"
						// + File.separator
						// + "map"
						// + File.separator
						// + currUserCompany
						// + File.separator
						// + currUserId
						// + File.separator
						// + "pic"
						// + File.separator
						// + currYear
						// + currMonth
						// + currDate
						// + File.separator
						// + currUserCurrDateMainPhotoUrl
						// .get(i)
						// .get("photoUrl")
						// .toString()
						// .substring(
						// currUserCurrDateMainPhotoUrl
						// .get(i).get("photoUrl")
						// .toString()
						// .lastIndexOf("\\") + 1,
						// currUserCurrDateMainPhotoUrl
						// .get(i).get("photoUrl")
						// .toString().length())
						// .replace("\\", File.separator))) {
						// File file = new File(
						// SDCardRoot
						// + "NewPai"
						// + File.separator
						// + "download"
						// + File.separator
						// + "map"
						// + File.separator
						// + currUserCompany
						// + File.separator
						// + currUserId
						// + File.separator
						// + "pic"
						// + File.separator
						// + currYear
						// + currMonth
						// + currDate
						// + File.separator
						// + currUserCurrDateMainPhotoUrl
						// .get(i)
						// .get("photoUrl")
						// .toString()
						// .substring(
						// currUserCurrDateMainPhotoUrl
						// .get(i)
						// .get("photoUrl")
						// .toString()
						// .lastIndexOf(
						// "\\") + 1,
						// currUserCurrDateMainPhotoUrl
						// .get(i)
						// .get("photoUrl")
						// .toString()
						// .length())
						// .replace("\\",
						// File.separator));
						// file.delete();
						// new loadCurrUserCurrDateMainPhotoAsyncTask()
						// .executeOnExecutor(
						// FULL_TASK_EXECUTOR,
						// currUserCurrDateMainPhotoUrl.get(i)
						// .get("photoId"),
						// currUserCurrDateMainPhotoUrl.get(i)
						// .get("photoUrl"),
						// currUserCurrDateMainPhotoUrl
						// .get(i)
						// .get("photoUrl")
						// .toString()
						// .substring(
						// currUserCurrDateMainPhotoUrl
						// .get(i)
						// .get("photoUrl")
						// .lastIndexOf(
						// "\\") + 1,
						// currUserCurrDateMainPhotoUrl
						// .get(i)
						// .get("photoUrl")
						// .length())
						// .replace("\\",
						// File.separator),
						// currUserCurrDateMainPhotoUrl.get(i)
						// .get("photoIfIndex"), mark);
						// } else {
						// currUserCurrDateMainPhotoNum++;
						// if (currUserCurrDateMainPhotoNum ==
						// currUserCurrDateMainPhotoUrl
						// .size()) {
						// currUserCurrDateMainPhotoNum = 0;
						// initMapTrail(currUserCurrDateData, mark);
						// }
						// }
						{
							currUserCurrDateMainPhotoNum++;
							if (currUserCurrDateMainPhotoNum == currUserCurrDateMainPhotoUrl
									.size()) {
								currUserCurrDateMainPhotoNum = 0;
								initMapTrail(currUserCurrDateData, mark);
							}
						}
					} else {
						new loadCurrUserCurrDateMainPhotoAsyncTask()
								.executeOnExecutor(
										FULL_TASK_EXECUTOR,
										currUserCurrDateMainPhotoUrl.get(i)
												.get("photoId"),
										currUserCurrDateMainPhotoUrl.get(i)
												.get("photoUrl"),
										currUserCurrDateMainPhotoUrl
												.get(i)
												.get("photoUrl")
												.toString()
												.substring(
														currUserCurrDateMainPhotoUrl
																.get(i)
																.get("photoUrl")
																.lastIndexOf(
																		"\\") + 1,
														currUserCurrDateMainPhotoUrl
																.get(i)
																.get("photoUrl")
																.length())
												.replace("\\", File.separator),
										currUserCurrDateMainPhotoUrl.get(i)
												.get("photoIfIndex"), mark);

					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				File file = new File(SDCardRoot + "NewPai" + File.separator
						+ "download" + File.separator + "map" + File.separator
						+ currUserCompany + File.separator + currUserId
						+ File.separator + "pic" + File.separator + currYear
						+ currMonth + currDate);
				file.delete();
			}
		} else {
			initMapTrail(currUserCurrDateData, mark);
		}

	}

	class loadCurrUserCurrDateMainPhotoAsyncTask extends
			AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... arg0) {
			{
				loadPhoto(MYURL2 + arg0[1].replace("\\", File.separator),
						SDCardRoot + "NewPai" + File.separator + "download"
								+ File.separator + "map" + File.separator
								+ currUserCompany + File.separator + currUserId
								+ File.separator + "pic" + File.separator
								+ currYear + currMonth + currDate, SDCardRoot
								+ "NewPai" + File.separator + "download"
								+ File.separator + "map" + File.separator
								+ currUserCompany + File.separator + currUserId
								+ File.separator + "pic" + File.separator
								+ currYear + currMonth + currDate
								+ File.separator + arg0[2]);
			}
			return arg0[3];
		}

		@Override
		protected void onPostExecute(String result) {
			currUserCurrDateMainPhotoNum++;
			if (currUserCurrDateMainPhotoNum == currUserCurrDateMainPhotoUrl
					.size()) {
				initMapTrail(currUserCurrDateData, result);
				currUserCurrDateMainPhotoNum = 0;
			}
			super.onPostExecute(result);
		}

	}

	class loadCurrUserCurrDateAllPhotoAsyncTask extends
			AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... arg0) {
			loadPhoto(MYURL2 + arg0[0].replace("\\", File.separator),
					SDCardRoot + "NewPai" + File.separator + "download"
							+ File.separator + "map" + File.separator
							+ currUserCompany + File.separator + currUserId
							+ File.separator + "pic" + File.separator
							+ currYear + currMonth + currDate, SDCardRoot
							+ "NewPai" + File.separator + "download"
							+ File.separator + "map" + File.separator
							+ currUserCompany + File.separator + currUserId
							+ File.separator + "pic" + File.separator
							+ currYear + currMonth + currDate + File.separator
							+ arg0[1]);
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			currUserCurrDateAllPhotoNum++;
			if (currUserCurrDateAllPhotoNum == loadNoPhoto.size()) {
				initVp(loadAllPhotoName);
			}
			super.onPostExecute(result);
		}

	}

	private void loadCurrUserCurrDateDataHttp(String currUserId,
			String currUserCompany, String currYear, String currMonth,
			String currDate) {
		new loadCurrUserCurrDateDataHttpAsyncTask().execute(currUserId,
				currUserCompany, currYear, currMonth, currDate);
	}

	public void initVp(List<String> loadNoPhotoList) {

		listViews = new ArrayList<View>();
		for (int i = 0; i < loadNoPhotoList.size(); i++) {
			initListViews(loadNoPhotoList.get(i));
		}
		ViewPagerAdapter vpAdapter = new ViewPagerAdapter(listViews,
				loadNoPhotoList);
		vp.setAdapter(vpAdapter);
		mMapView.setVisibility(View.GONE);
		vp.setVisibility(View.VISIBLE);
		tv1.setText(getResources().getString(R.string.backText));
		returnPhotomark = "1";
		tv3.setText("");
		photoCloseLL.setVisibility(View.GONE);

	}

	private void initListViews(String photoName) {

		ImageView mImageView = new ImageView(this);
		Bitmap m = reduce(
				BitmapFactory.decodeFile(SDCardRoot
						+ "NewPai"
						+ File.separator
						+ "download"
						+ File.separator
						+ "map"
						+ File.separator
						+ currUserCompany
						+ File.separator
						+ currUserId
						+ File.separator
						+ "pic"
						+ File.separator
						+ currYear
						+ currMonth
						+ currDate
						+ File.separator
						+ photoName.substring(photoName.lastIndexOf("\\") + 1,
								photoName.length()).replace("\\",
								File.separator)), 100, 200, false);

		mImageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
		mImageView.setImageBitmap(m);
		listViews.add(mImageView);
	}

	class loadCurrUserCurrDateDataHttpAsyncTask extends
			AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... arg0) {
			HttpConnection connection = new HttpConnection(
					DynamicMapActivity2.this);

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("currUserId", arg0[0]);
			map.put("currUserCompany", arg0[1]);
			map.put("currYear", arg0[2]);
			map.put("currMonth", arg0[3]);
			map.put("currDate", arg0[4]);
			try {
				
//				Map<String,Object> bakReturn = connection
//						.loadNewDataHttp(map, MYURL2
//								+ "androidConreoller.do?action=loadNewData");
				currUserCurrDateData = 
						 (MapUserTrail) connection
							.loadNewDataHttp(map, MYURL2
							+ "androidConreoller.do?action=loadNewData");
//				ifcurrDate = (String) bakReturn.get("ifcurrDate");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			
			String mark = "0";
			if (null == currUserCurrDateData
					|| ("9999".equals(currUserCurrDateData.getCurrTime().get(
							"currYear"))
							&& "99".equals(currUserCurrDateData.getCurrTime()
									.get("currMonth")) && "99"
								.equals(currUserCurrDateData.getCurrTime().get(
										"currDate")))) {

				Toast.makeText(DynamicMapActivity2.this,
						getResources().getString(R.string.nobasicdata),
						Toast.LENGTH_SHORT).show();
				subscribeUser();
				aniStop();

			} else {
				initTime();
				if (currYear.equals(currUserCurrDateData.getCurrTime().get(
						"currYear"))
						&& currMonth.equals(currUserCurrDateData.getCurrTime()
								.get("currMonth"))
						&& currDate.equals(currUserCurrDateData.getCurrTime()
								.get("currDate"))) {

					mark = "1";

				} else {
					
					Toast.makeText(DynamicMapActivity2.this,
							getResources().getString(R.string.nobasicdata2),
							Toast.LENGTH_LONG).show();
					currYear = currUserCurrDateData.getCurrTime().get(
							"currYear");
					currMonth = currUserCurrDateData.getCurrTime().get(
							"currMonth");
					currDate = currUserCurrDateData.getCurrTime().get(
							"currDate");
					SharedPreferences myPreference = DynamicMapActivity2.this
							.getSharedPreferences(MY_SETTING,
									Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = myPreference.edit();
					editor.putString(currUserCompany + "_" + currUserId,
							currYear + currMonth + currDate);
					editor.commit();
					userList.add(currUserCompany + "_" + currUserId);
				}
				if ((!"SD card Error".equals(getSDPath()))) {
					saveBasic(currUserCurrDateData, SDCardRoot + "NewPai"
							+ File.separator + "download" + File.separator
							+ "map" + File.separator + currUserCompany
							+ File.separator + currUserId + File.separator
							+ "basic" + File.separator + currYear + currMonth
							+ currDate, SDCardRoot + "NewPai" + File.separator
							+ "download" + File.separator + "map"
							+ File.separator + currUserCompany + File.separator
							+ currUserId + File.separator + "basic"
							+ File.separator + currYear + currMonth + currDate
							+ File.separator + "basic.out");

					String ifHavePhoto = "0";
					for (int i = 0; i < currUserCurrDateData
							.getMapUserTrailList().size(); i++) {
						if ("1".equals(currUserCurrDateData
								.getMapUserTrailList().get(i)
								.get("ifPhotomark"))) {
							ifHavePhoto = "1";
						}
					}
					if ("0".equals(ifHavePhoto)) {
						initMapTrail(currUserCurrDateData, mark);
					} else {
						loadCurrUserCurrDateMainPhoto(mark);
					}
				} else {
					Toast.makeText(DynamicMapActivity2.this,
							getResources().getString(R.string.nosdcard),
							Toast.LENGTH_SHORT).show();
				}
			}
			super.onPostExecute(result);
		}
	}

	private void loadCurrUserCurrDateDataSD(String fileName) {
		HttpConnection connection = new HttpConnection(DynamicMapActivity2.this);
		currUserCurrDateData = connection.loadCurrUserCurrDateDataSD(fileName);
	}

	private void initMapTrail(MapUserTrail currUserCurrDateData, String mark) {

		OverlayOptions overlayOptions = null;
		Marker mMarker = null;
		BitmapDescriptor bitmap;
		List<PointInformation> newlpi = new ArrayList<PointInformation>();

		List<Map<String, Object>> currTrail = currUserCurrDateData
				.getMapUserTrailList();
		currYear = currUserCurrDateData.getCurrTime().get("currYear");
		currMonth = currUserCurrDateData.getCurrTime().get("currMonth");
		currDate = currUserCurrDateData.getCurrTime().get("currDate");
		mBaiduMap.clear();
		LatLng bakmarkPoint = null;
		for (int i = 0; i < currTrail.size(); i++) {

			Map<String, Object> m = currTrail.get(i);
			PointInformation newpi = new PointInformation();

			if ((("1".equals(m.get("ifPhotomark"))) && ("1".equals(m
					.get("photoIfIndex"))))
					|| ("0".equals(m.get("ifPhotomark")))) {
				newpi.setName(m.get("currUserName").toString());
				newpi.setPhotoId(m.get("photoId").toString());
				newpi.setDepartment(m.get("currUserDepartment").toString());
				newpi.setLatitude((java.lang.Double) (m.get("latitude")));
				newpi.setLongitude((java.lang.Double) (m.get("longitude")));
				newpi.setPointTime(m.get("reachTime").toString());
				newpi.setStayTime(m.get("stopTime").toString());
				if ("1".equals(m.get("ifPhotomark"))
						&& "1".equals(m.get("photoIfIndex"))) {
					if ((!"SD card Error".equals(getSDPath()))) {
						BitmapFactory.Options opts = new BitmapFactory.Options();
						opts.inJustDecodeBounds = true;
						newpi.setIndexBitmap(reduce(
								BitmapFactory
										.decodeFile(SDCardRoot
												+ "NewPai"
												+ File.separator
												+ "download"
												+ File.separator
												+ "map"
												+ File.separator
												+ currUserCompany
												+ File.separator
												+ currUserId
												+ File.separator
												+ "pic"
												+ File.separator
												+ currYear
												+ currMonth
												+ currDate
												+ File.separator
												+ m.get("photoUrl")
														.toString()
														.substring(
																m.get("photoUrl")
																		.toString()
																		.lastIndexOf(
																				"\\") + 1,
																m.get("photoUrl")
																		.toString()
																		.length())
														.replace("\\",
																File.separator)),
								100, 200, false));
					} else {
						Toast.makeText(DynamicMapActivity2.this,
								getResources().getString(R.string.nosdcard),
								Toast.LENGTH_SHORT).show();
					}
				} else {
					BitmapFactory.Options options = new BitmapFactory.Options();
					newpi.setIndexBitmap(BitmapFactory.decodeResource(
							getResources(), R.drawable.wt));
				}

				newlpi.add(newpi);
				if ((Double) currTrail.get(i).get("latitude") == 0.0
						&& (Double) currTrail.get(i).get("longitude") == 0.0) {

				} else {
					LatLng markPoint = new LatLng((Double) currTrail.get(i)
							.get("latitude"), (Double) currTrail.get(i).get(
							"longitude"));

					if ("1".equals(currTrail.get(i).get("ifPhotomark"))) {
						bitmap = BitmapDescriptorFactory
								.fromResource(R.drawable.stop1);
					} else {
						bitmap = BitmapDescriptorFactory
								.fromResource(R.drawable.stop2);
					}
					OverlayOptions option = new MarkerOptions()
							.position(markPoint).icon(bitmap).zIndex(2);
					mMarker = (Marker) mBaiduMap.addOverlay(option);
					Bundle bundle = new Bundle();
					bundle.putSerializable("info", newpi);
					mMarker.setExtraInfo(bundle);
					List<LatLng> currPoints = new ArrayList<LatLng>();
					currPoints.add(markPoint);
					currPoints.add((null == bakmarkPoint) ? markPoint
							: bakmarkPoint);
					OverlayOptions ooPolyline = new PolylineOptions().width(5)
							.color(Color.RED).points(currPoints);
					mBaiduMap.addOverlay(ooPolyline);
					bakmarkPoint = markPoint;
				}
			}
		}
		aniStop();
		// if ("1".equals(mark)) {
		subscribeUser();
		// }
	}

	// =====================以下都是些工具方法=====================

	private void subscribeUser() {

		sharedPrefs = DynamicMapActivity2.this.getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
		new subscribeUserAsyncTask().execute(currLoginUserCompany + "_"
				+ currLoginUserId, currUserCompany + "_" + currUserId);
	}

	class subscribeUserAsyncTask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... arg0) {
			HttpConnection httpConnection = new HttpConnection(
					DynamicMapActivity2.this);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("bossUser", arg0[0]);
			map.put("subscribeUser", arg0[1]);
			httpConnection.subscribeUser(map, MYURL2
					+ "androidConreoller.do?action=subscribeUser");
			return null;
		}
	}

	private boolean checkData(String dir) {

		File file = new File(SDCardRoot + "NewPai" + File.separator
				+ "download" + File.separator + "map" + File.separator + dir
				+ File.separator);
		if (file.exists()) {
			return false;
		} else {
			return true;
		}
	}

	private boolean checkData2(String fileName) {

		String a = (SDCardRoot + "NewPai" + File.separator + "download"
				+ File.separator + "map" + File.separator + fileName)
				.toString().replace("//", File.separator);

		File file = new File(a);
		if (file.exists()) {
			return false;
		} else {
			return true;
		}
	}

	public String getSDPath() {
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		if (sdCardExist) {
			SDCardRoot = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + File.separator;
		} else {
			return "SD card Error";
		}
		return SDCardRoot;
	}

	private String getSharedPreferencesFunc(String userCompanyUserId) {
		SharedPreferences myPreference = this.getSharedPreferences(MY_SETTING,
				Context.MODE_PRIVATE);
		return myPreference.getString(userCompanyUserId, "no");
	}

	private List<HashMap<String, String>> queryCurrUserCurrDateMainPhotoUrl() {
		List<HashMap<String, String>> queryCurrUserCurrDateMainPhotoUrl = new ArrayList<HashMap<String, String>>();
		for (Map<String, Object> currUserCurrDateDataMap : currUserCurrDateData
				.getMapUserTrailList()) {
			if ("1".equals(currUserCurrDateDataMap.get("ifPhotomark"))
					&& "1".equals(currUserCurrDateDataMap.get("photoIfIndex"))) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("photoId", currUserCurrDateDataMap.get("photoId")
						.toString());
				map.put("photoUrl", currUserCurrDateDataMap.get("photoUrl")
						.toString());
				map.put("photoIfIndex",
						currUserCurrDateDataMap.get("photoIfIndex").toString());
				queryCurrUserCurrDateMainPhotoUrl
						.add((HashMap<String, String>) map);
			}
		}
		return queryCurrUserCurrDateMainPhotoUrl;
	}

	private void loadPhoto(String photoUrl, String sdFileDir, String sdFileName) {
		File fileDir = new File(sdFileDir);
		if (!fileDir.exists()) {
			fileDir.mkdirs();

		}
		File file = new File(sdFileName);
		if (file.exists()) {
			file.delete();
		}
		try {
			URL url = new URL(photoUrl);
			URLConnection con = url.openConnection();
			int contentLength = con.getContentLength();
			InputStream is = con.getInputStream();
			byte[] bs = new byte[contentLength];
			int len;
			OutputStream os = new FileOutputStream(sdFileName);
			while ((len = is.read(bs)) != -1) {
				os.write(bs, 0, len);
			}
			os.close();
			is.close();

		} catch (Exception e) {
			fileDir.delete();
			e.printStackTrace();
		}
	}

	private void saveBasic(MapUserTrail basic, String sdFileDir,
			String sdFileName) {

		File sdCardDir = new File(sdFileDir);
		if (!sdCardDir.exists()) {
			sdCardDir.mkdirs();
		}
		try {
			FileOutputStream fos = new FileOutputStream(sdFileName);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(basic);// 写入
			fos.close(); // 关闭输出流
		} catch (FileNotFoundException e) {
			sdCardDir.delete();
			e.printStackTrace();
		} catch (IOException e) {
			sdCardDir.delete();
			e.printStackTrace();
		}
	}

	@Override
	public void onMapClick(LatLng arg0) {
		mBaiduMap.hideInfoWindow();

	}

	@Override
	public boolean onMapPoiClick(MapPoi arg0) {
		return false;
	}

	@Override
	public void onTouch(MotionEvent arg0) {
	}

	public Bitmap reduce(Bitmap bitmap, int width, int height, boolean isAdjust) {
		// 如果想要的宽度和高度都比源图片小，就不压缩了，直接返回原图
		if (bitmap.getWidth() < width && bitmap.getHeight() < height) {
			return bitmap;
		}
		// 根据想要的尺寸精确计算压缩比例, 方法详解：public BigDecimal divide(BigDecimal divisor,
		// int scale, int roundingMode);
		// scale表示要保留的小数位, roundingMode表示如何处理多余的小数位，BigDecimal.ROUND_DOWN表示自动舍弃
		float sx = new BigDecimal(width).divide(
				new BigDecimal(bitmap.getWidth()), 4, BigDecimal.ROUND_DOWN)
				.floatValue();
		float sy = new BigDecimal(height).divide(
				new BigDecimal(bitmap.getHeight()), 4, BigDecimal.ROUND_DOWN)
				.floatValue();
		if (isAdjust) {// 如果想自动调整比例，不至于图片会拉伸
			sx = (sx < sy ? sx : sy);
			sy = sx;// 哪个比例小一点，就用哪个比例
		}
		Matrix matrix = new Matrix();
		matrix.postScale(sx, sy);// 调用api中的方法进行压缩，就大功告成了
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mMapView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mMapView.onResume();
	}

	// @Override
	// public void finish() {
	// super.finish();
	// onDestroy();
	// }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (null != mMapView) {
			mMapView.onDestroy();
		}
		aniStop();
		// cleanList();
	}

	@Override
	public void onBackPressed() {
		if (vp.getVisibility() == View.VISIBLE) {
			vp.setVisibility(View.GONE);
			mMapView.setVisibility(View.VISIBLE);
			vp.setVisibility(View.GONE);

			tv1.setText(getResources().getString(R.string.maplefttext));
			returnPhotomark = "0";
			tv3.setText(getResources().getString(R.string.maprighttext));
			photoCloseLL.setVisibility(View.VISIBLE);

		} else {
			unregisterReceiver(mBroadcastReceiver);
			Intent intent = new Intent();
			setResult(0, intent);
			finish();
			this.overridePendingTransition(R.anim.in, R.anim.out);
		}
	}

	private void initTime() {
		c = Calendar.getInstance();
		currYear = c.get(Calendar.YEAR) + "";
		currMonth = c.get(Calendar.MONTH) + 1 + "";
		if (currMonth.length() == 1) {
			currMonth = "0" + currMonth;
		}
		currDate = c.get(Calendar.DAY_OF_MONTH) + "";
		if (currDate.length() == 1) {
			currDate = "0" + currDate;
		}
	}

	private void aniStop() {
		iv1.setClickable(true);
		tv1.setClickable(true);
		tv3.setClickable(true);
		loadMark = "1";
		if (null != animationDrawable) {
			animationDrawable.stop();
			mapWaitImageView.setVisibility(View.GONE);
		}
	}

	private void aniStart() {
		iv1.setClickable(false);
		tv1.setClickable(false);
		tv3.setClickable(false);
		loadMark = "0";
		mapWaitImageView.setVisibility(View.VISIBLE);
		mapWaitImageView.setBackgroundResource(R.anim.wait_anim);
		animationDrawable = (AnimationDrawable) mapWaitImageView
				.getBackground();
		animationDrawable.start();
	}

	private void cleanList() {
		SharedPreferences myPreference = DynamicMapActivity2.this
				.getSharedPreferences(MY_SETTING, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = myPreference.edit();

		for (int i = 0; i < userList.size(); i++) {
			editor.remove(userList.get(i));
		}
		editor.commit();
	}
}
