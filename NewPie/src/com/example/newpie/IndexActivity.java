package com.example.newpie;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;

import org.androidpn.client.ServiceManager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.newpie.bean.ActivityManager;
import com.example.newpie.bean.User;
import com.example.newpie.utils.HttpConnection;
import com.readystatesoftware.viewbadger.BadgeView;
import com.tutk.P2PCam264.DELUX.MultiViewActivity;

public class IndexActivity extends Activity implements OnClickListener {

	public static final String MYURL = "http://120.27.42.95:80/tecoa/";
	public static final String PACKAGENAME = "com.example.newpie";
	private ImageView headImageview;
	private TextView nameTextView;
	private TextView departmentTextView;
	private TextView positionTextView;
	private ImageView wechatImageView;
	private User user;
	private ImageView qrcodeImageView;
	private Bitmap bm;
	private TextView tvRight;
	private ImageView iv1;
	private ImageView iv4;
	private ImageView homeimage;
	private ImageView bookimage;
	private ServiceManager serviceManager;
	private ImageView paiimage;
	private ImageView cardimage;
	private boolean homemark = true;
	private boolean bookmark = true;
	private boolean paimark = true;
	private boolean cardmark = true;
	private String SDCardRoot = new String();
	private ImageView indexwaitimageview;
	private AnimationDrawable animationDrawable;
	private static boolean isFirstLaunch = true;
	private static final int SPLASH_DISPLAY_TIME = 2000;
	private LinearLayout pic1Layout; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_index);		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		user = (User) this.getIntent().getSerializableExtra("user");		
		init();
		aniStart();
		serviceManager = new ServiceManager(IndexActivity.this);
		serviceManager.setNotificationIcon(R.drawable.notification);
		serviceManager.startService(
				user.getUserName().toString().toLowerCase(), user.getCompany()
						.toString().toLowerCase());
		ActivityManager exitM = ActivityManager.getInstance();
		exitM.addActivity(IndexActivity.this);
		registerBoradcastReceiver();
	}

	public void registerBoradcastReceiver() {
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction("upLine");
		registerReceiver(mBroadcastReceiver, myIntentFilter);
	}

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("upLine")) {
				aniStop();
			}
		}
	};

	private void init() {

		if ((!"SD card Error".equals(getSDPath()))) {
			new loadUserHead()
					.execute("0".equals(user.getFileattribute()) ? "0" : user
							.getFileattribute());
		}
		nameTextView = (TextView) findViewById(R.id.nametext);
		departmentTextView = (TextView) findViewById(R.id.departmenttext);
		positionTextView = (TextView) findViewById(R.id.positiontext);
		headImageview = (ImageView) findViewById(R.id.head);
		nameTextView.setText(user.getFullName().toString());
		departmentTextView.setText(user.getDepartment().toString());
		positionTextView.setText(user.getPosition().toString());
		qrcodeImageView = (ImageView) findViewById(R.id.qrcode);
		qrcodeImageView.setOnClickListener(this);
		
		pic1Layout = (LinearLayout)findViewById(R.id.pic1layout); 
		
		
		BadgeView bv = new BadgeView(this,pic1Layout);
		bv.setBadgeMargin(50, 50);
		bv.setText("5");
		bv.setTextColor(Color.BLACK);
		bv.setTextSize(14);
		bv.setBadgePosition(BadgeView.POSITION_TOP_RIGHT); 
		bv.show();
		
		
		iv1 = (ImageView) findViewById(R.id.pic1);
		iv4 = (ImageView) findViewById(R.id.pic4);
		tvRight = (TextView) findViewById(R.id.functext);
		homeimage = (ImageView) findViewById(R.id.homeimage);
		bookimage = (ImageView) findViewById(R.id.bookimage);
		paiimage = (ImageView) findViewById(R.id.paiimage);
		cardimage = (ImageView) findViewById(R.id.cardimage);
		wechatImageView = (ImageView) findViewById(R.id.infor);
		indexwaitimageview = (ImageView) findViewById(R.id.indexwaitimageview);
		iv1.setOnClickListener(this);
		iv4.setOnClickListener(this);
		tvRight.setOnClickListener(this);
		homeimage.setOnClickListener(this);
		bookimage.setOnClickListener(this);
		paiimage.setOnClickListener(this);
		cardimage.setOnClickListener(this);
		wechatImageView.setOnClickListener(this);
	}

	class loadUserHead extends AsyncTask<String, String, String> {

		@Override
		protected void onPostExecute(String result) {

			if ("0".equals(user.getFileattribute())) {
				headImageview.setImageBitmap(reduce(BitmapFactory
						.decodeResource(getResources(), R.drawable.wt), 100,
						200, false));
			} else {
				headImageview.setImageBitmap(reduce(BitmapFactory
						.decodeFile((SDCardRoot + "NewPai" + File.separator
								+ "download" + File.separator + "user"
								+ File.separator + user
								.getFileattribute()
								.substring(
										user.getFileattribute().lastIndexOf(
												"\\") + 1,
										user.getFileattribute().length())
								.replace("\\", File.separator)).replace("\\",
								File.separator)), 100, 200, false));
			}
			super.onPostExecute(result);
		}

		@Override
		protected String doInBackground(String... arg0) {

			if ("0".equals(arg0[0])) {

			} else {
				File file = new File(SDCardRoot
						+ "NewPai"
						+ File.separator
						+ "download"
						+ File.separator
						+ "user"
						+ File.separator
						+ arg0[0].substring(
								arg0[0].lastIndexOf(File.separator) + 1,
								arg0[0].length()).replace("\\", File.separator));
				if (!file.exists()) {
					file.mkdir();
				}

				loadPhoto(
						MYURL + arg0[0].replace("\\", File.separator),

						SDCardRoot + "NewPai" + File.separator + "download"
								+ File.separator + "user" + File.separator,
						SDCardRoot
								+ "NewPai"
								+ File.separator
								+ "download"
								+ File.separator
								+ "user"
								+ File.separator
								+ arg0[0].substring(
										arg0[0].lastIndexOf("\\") + 1,
										arg0[0].length()).replace("\\",
										File.separator));
			}
			return null;
		}
	}



	

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.qrcode: {
			new queryQRCodeAsyncTask().execute("QRCode");
			break;
		}
		
		case R.id.pic4: {
			
			if (user.getUserResources().contains("地图"))
			{
			Animation shake = AnimationUtils.loadAnimation(IndexActivity.this,
					R.anim.button_shake);
			iv4.startAnimation(shake);
			shake.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation arg0) {
				}

				@Override
				public void onAnimationRepeat(Animation arg0) {

				}

				@Override
				public void onAnimationEnd(Animation arg0) {
					dynamicMap();

				}
			});			
			}
			else
			{
				Toast.makeText(this, "您不具备此权限", Toast.LENGTH_SHORT).show();				
			}
			break;
		}
		
		case R.id.pic1: {
//			Animation shake = AnimationUtils.loadAnimation(IndexActivity.this,
//					R.anim.button_shake);
//			iv4.startAnimation(shake);
//			shake.setAnimationListener(new AnimationListener() {
//
//				@Override
//				public void onAnimationStart(Animation arg0) {
//				}
//
//				@Override
//				public void onAnimationRepeat(Animation arg0) {
//
//				}
//
//				@Override
//				public void onAnimationEnd(Animation arg0) {
//					dynamicMap();
//
//				}
//			});
			
//			Splash splash = (Splash) findViewById(R.id.splash);
//
//			if (splash != null) {
//				splash.setVersion(versionName);
//
//				if (!isFirstLaunch) {
//					splash.setVisibility(View.INVISIBLE);
//				}
//				else {
//					// Clean connection leaved by crash last time
//					SharedPreferences settings = this.getSharedPreferences("ProcessList", 0);
//					if(settings != null){
//						
//						int killpid = settings.getInt("pid", 0);
//						android.os.Process.killProcess(killpid);
//						
//						int pid = android.os.Process.myPid();
//						SharedPreferences.Editor PE = settings.edit();
//						PE.putInt("pid", pid);
//						PE.commit();
//					}
//				}
//			}
			
			
			if (user.getUserResources().contains("地图"))
			{
			
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {

					Intent mainIntent = new Intent(IndexActivity.this, MultiViewActivity.class);
					mainIntent.setPackage(PACKAGENAME);
					IndexActivity.this.startActivity(mainIntent);
				//	IndexActivity.this.finish();

					overridePendingTransition(R.anim.mainfadein, R.anim.splashfadeout);
				}
			}, isFirstLaunch ? SPLASH_DISPLAY_TIME : 500);

			isFirstLaunch = false;
			}
			else
			{
				Toast.makeText(this, "您不具备此权限", Toast.LENGTH_SHORT).show();
			}
			
			
			break;
		}
		
		
		
		
		
		case R.id.functext: {

			break;
		}
		case R.id.homeimage: {
			if (homemark) {
				homeimage.setImageDrawable(getResources().getDrawable(
						R.drawable.home2));
				homemark = false;
				bookimage.setImageDrawable(getResources().getDrawable(
						R.drawable.book));
				bookmark = true;
				paiimage.setImageDrawable(getResources().getDrawable(
						R.drawable.pai));
				paimark = true;
				cardimage.setImageDrawable(getResources().getDrawable(
						R.drawable.card));
				cardmark = true;

				break;
			} else {
				break;
			}
		}
		case R.id.bookimage: {

			if (bookmark) {
				bookimage.setImageDrawable(getResources().getDrawable(
						R.drawable.book2));
				bookmark = false;
				homeimage.setImageDrawable(getResources().getDrawable(
						R.drawable.home));
				homemark = true;
				paiimage.setImageDrawable(getResources().getDrawable(
						R.drawable.pai));
				paimark = true;
				cardimage.setImageDrawable(getResources().getDrawable(
						R.drawable.card));
				cardmark = true;
				break;
			} else {
				break;
			}
		}
		case R.id.paiimage: {

			if (paimark) {
				paiimage.setImageDrawable(getResources().getDrawable(
						R.drawable.pai2));
				paimark = false;
				homeimage.setImageDrawable(getResources().getDrawable(
						R.drawable.home));
				homemark = true;
				bookimage.setImageDrawable(getResources().getDrawable(
						R.drawable.book));
				bookmark = true;
				cardimage.setImageDrawable(getResources().getDrawable(
						R.drawable.card));
				cardmark = true;
				break;
			} else {
				break;
			}
		}
		case R.id.cardimage: {

			if (cardmark) {
				cardimage.setImageDrawable(getResources().getDrawable(
						R.drawable.card2));
				cardmark = false;
				homeimage.setImageDrawable(getResources().getDrawable(
						R.drawable.home));
				homemark = true;
				bookimage.setImageDrawable(getResources().getDrawable(
						R.drawable.book));
				bookmark = true;
				paiimage.setImageDrawable(getResources().getDrawable(
						R.drawable.pai));
				paimark = true;
				break;
			} else {
				break;
			}
		}

		case R.id.infor: {

			Intent intent = new Intent();
			intent.setClass(IndexActivity.this, ChatActivity.class);
			forwardActivity(intent);
			break;
		}
		}

	}

	/**
	 * 二维码
	 * 
	 * @author 韩国瑞
	 * 
	 */
	class queryQRCodeAsyncTask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... param) {

			Log.v("tag", "查询二维码");
			queryQRCode();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {

			if (null != bm) {
				Intent intent = new Intent(IndexActivity.this,
						PopQRCodeActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("user", user);
				intent.putExtra("bitmap", bm);
				startActivity(intent);
			} else {
				Toast.makeText(IndexActivity.this, R.string.failqrcode,
						Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(result);
		}

	}

	private void queryQRCode() {

		HttpConnection httpConnection = new HttpConnection(IndexActivity.this);
		if (HttpConnection.isNetworkAvailable()) {
			bm = BitmapFactory.decodeStream(httpConnection
					.voidPostStreamConnection(MYURL + "getQRCode"));
		}
	}

	private void dynamicMap() {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putSerializable("user", user);
		intent.putExtras(bundle);
		intent.setClass(IndexActivity.this, DynamicMapActivity2.class);
		startActivity(intent);
		overridePendingTransition(R.anim.in, R.anim.out);
	}

	private void forwardActivity(Intent intent) {
		startActivity(intent);
		overridePendingTransition(R.anim.in, R.anim.out);
	}

	@Override
	public void onBackPressed() {
		if (null != serviceManager) {
			serviceManager.stopService();
		}
		unregisterReceiver(mBroadcastReceiver);
		System.exit(0);
	}

	@Override
	public void finish() {
		onDestroy();
		super.finish();
	}

	@Override
	protected void onDestroy() {
		if (null != serviceManager) {
			serviceManager.stopService();
		}
		unregisterReceiver(mBroadcastReceiver);
		super.onDestroy();
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
			byte[] bs = new byte[1024];
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

	private Object getSDPath() {
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

	private void aniStart() {
		qrcodeImageView.setClickable(false);
		iv4.setClickable(false);
		tvRight.setClickable(false);
		homeimage.setClickable(false);
		bookimage.setClickable(false);
		paiimage.setClickable(false);
		cardimage.setClickable(false);
		wechatImageView.setClickable(false);
		indexwaitimageview.setVisibility(View.VISIBLE);
		indexwaitimageview.setBackgroundResource(R.anim.wait_anim);
		animationDrawable = (AnimationDrawable) indexwaitimageview
				.getBackground();
		animationDrawable.start();
	}

	private void aniStop() {
		qrcodeImageView.setClickable(true);
		iv4.setClickable(true);
		tvRight.setClickable(true);
		homeimage.setClickable(true);
		bookimage.setClickable(true);
		paiimage.setClickable(true);
		cardimage.setClickable(true);
		wechatImageView.setClickable(true);
		if (null != animationDrawable) {
			animationDrawable.stop();
			indexwaitimageview.setVisibility(View.GONE);
		}
	}
}
