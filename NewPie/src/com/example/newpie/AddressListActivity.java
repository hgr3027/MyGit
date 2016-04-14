package com.example.newpie;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.androidpn.client.Constants;
import org.json.JSONArray;
import org.json.JSONException;
import com.example.newpie.bean.ActivityManager;
import com.example.newpie.bean.AddressUser;
import com.example.newpie.utils.FileBean;
import com.example.newpie.utils.HttpConnection;
import com.example.newpie.utils.Node;
import com.example.newpie.utils.TreeListViewAdapter;
import com.example.newpie.utils.TreeListViewAdapter.OnTreeNodeClickListener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newpie.utils.SimpleTreeAdapter;

public class AddressListActivity extends Activity implements OnClickListener,
		OnScrollListener {

	//public static final String PICURL = "http://120.27.42.95:80/tecoa/android/download";
	public static final String MYURL = "http://120.27.42.95:80/tecoa/";
	private View addressListTitle;
	private ImageView iv1;
	private TextView tv1;
	private TextView tv2;
	private List<FileBean> mDatas = new ArrayList<FileBean>();
	private ListView mTree;
	private TreeListViewAdapter mAdapter;
	private String SDCardRoot = null;
	private ImageView listviewimage;
	private ImageView addresslistWaitImageView;
	private AnimationDrawable animationDrawable;
	private List<AddressUser> addressuserList = new ArrayList<AddressUser>();
	private SharedPreferences sharedPrefs;
	private JSONArray jo;
	private List<String> headPhotoList = new ArrayList<String>();
	private List<String> loadNoPhoto;
	private int loadPhotoNum = 0;
	private static ExecutorService FULL_TASK_EXECUTOR;
	static {
		FULL_TASK_EXECUTOR = (ExecutorService) Executors
				.newSingleThreadExecutor();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {


        String a ="10";

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addresslist);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		init();
		ActivityManager exitM = ActivityManager.getInstance();
		exitM.addActivity(AddressListActivity.this);
	}	

	private void init() {
		listviewimage = (ImageView) findViewById(R.id.listviewimage);
		mTree = (ListView) findViewById(R.id.addresslistlistview);
		addressListTitle = findViewById(R.id.addresslisttitle);
		iv1 = (ImageView) addressListTitle.findViewById(R.id.bakimageview);
		tv1 = (TextView) addressListTitle.findViewById(R.id.baktextview);
		tv2 = (TextView) addressListTitle.findViewById(R.id.titletext);
		tv1.setText(getResources().getString(R.string.selectobjectlistlefttext));
		tv2.setText(getResources().getString(R.string.maprighttext));
		addresslistWaitImageView = (ImageView) findViewById(R.id.addresslistwaitimageview);
		iv1.setOnClickListener(this);
		tv1.setOnClickListener(this);
		initData();
		mTree.setVerticalScrollBarEnabled(false);
		mTree.setOnScrollListener(this);
	}

	private void initData() {
		sharedPrefs = AddressListActivity.this.getSharedPreferences(
				Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
		new laodAddressListDataAsyncTask().execute(sharedPrefs.getString(
				"myUserId", ""));
		aniStart();
	}

	class laodAddressListDataAsyncTask extends
			AsyncTask<String, String, String> {

		@Override
		protected void onPostExecute(String result) {

			for (int i = 0; i < jo.length(); i++) {
				try {
					if (!("0"
							.equals(jo.getJSONObject(i).getString("userPhoto")))) {
						headPhotoList.add(jo.getJSONObject(i)
								.getString("userPhoto").toString());
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			if ((!"SD card Error".equals(getSDPath()))) {
				if (headPhotoList.size() != 0) {
					loadNoPhoto = checkNoPhotoName(headPhotoList);
					if (loadNoPhoto.size() != 0) {
						for (int i = 0; i < loadNoPhoto.size(); i++) {
							new laodAddressListDataAsyncTask2()
									.executeOnExecutor(
											FULL_TASK_EXECUTOR,
											loadNoPhoto.get(i),
											loadNoPhoto
													.get(i)
													.substring(
															loadNoPhoto
																	.get(i)
																	.lastIndexOf(
																			"\\") + 1,
															loadNoPhoto.get(i)
																	.length())
													.replace("\\",
															File.separator));
						}
					}
					else
					{
						initTree();
					}
				}
				else
				{
					initTree();
				}
			} else {
				Toast.makeText(AddressListActivity.this,
						getResources().getString(R.string.nosdcard),
						Toast.LENGTH_SHORT).show();
			}

			super.onPostExecute(result);
		}

		@Override
		protected String doInBackground(String... arg0) {

			HttpConnection httpConnection = new HttpConnection(
					AddressListActivity.this);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("userId", arg0[0]);
			try {
				jo = new JSONArray(httpConnection.loadAddressList(map, MYURL
						+ "loadAddressList"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	class laodAddressListDataAsyncTask2 extends
			AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... arg0) {
			loadPhoto(
					MYURL + arg0[0].replace("\\", File.separator),
					SDCardRoot + "NewPai" + File.separator + "headPhotoList"
							+ File.separator,
					SDCardRoot
							+ "NewPai"
							+ File.separator
							+ "headPhotoList"
							+ File.separator
							+ arg0[0].substring(arg0[0].lastIndexOf("\\") + 1,
									arg0[0].length()).replace("\\",
									File.separator));
			return null;
		}

		@Override
		protected void onPostExecute(String result) {

			loadPhotoNum++;
			if (loadPhotoNum == loadNoPhoto.size()) {
				loadPhotoNum = 0;
				initTree();
			}
			super.onPostExecute(result);
		}
	}

	private void initTree() {
		aniStop();
		List<HashMap<String, Object>> returnData = new ArrayList<HashMap<String, Object>>();

		for (int i = 0; i < jo.length(); i++) {
			AddressUser addressUser = new AddressUser();
			try {
				addressUser.setId(jo.getJSONObject(i).getString("id"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				addressUser.setpId(jo.getJSONObject(i).getString("pid"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				addressUser.setUserId(jo.getJSONObject(i).getString("userId"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			try {
				addressUser.setUserName(jo.getJSONObject(i).getString(
						"userName"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			try {
				addressUser.setUserCompany(jo.getJSONObject(i).getString(
						"userCompany"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			try {
				addressUser.setUserDepartment(jo.getJSONObject(i).getString(
						"department"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			

			try {
				if ("0".equals(jo.getJSONObject(i).getString("userPhoto"))) {
					addressUser.setUserPhoto((Bitmap) reduce(BitmapFactory
							.decodeResource(getResources(), R.drawable.wt),
							100, 200, false));
				} else {
					addressUser.setUserPhoto((Bitmap) getUserPhoto(SDCardRoot
							+ "NewPai"
							+ File.separator
							+ "headPhotoList"
							+ File.separator
							+ jo
							.getJSONObject(i).getString("userPhoto").substring(jo
									.getJSONObject(i).getString("userPhoto").lastIndexOf("\\") + 1,
									jo
									.getJSONObject(i).getString("userPhoto").length()).replace("\\",
											File.separator)));					
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

			addressuserList.add(addressUser);
		}

		for (int i = 0; i < addressuserList.size(); i++) {
			mDatas.add(new FileBean(addressuserList.get(i).getId(),
					addressuserList.get(i).getpId(), addressuserList.get(i)
							.getUserId(), addressuserList.get(i).getUserName(),
					addressuserList.get(i).getUserPhoto(), addressuserList.get(
							i).getUserCompany(),addressuserList.get(
									i).getUserDepartment()));
		}
		try {
			mAdapter = new SimpleTreeAdapter<FileBean>(mTree,
					AddressListActivity.this, mDatas, 10);

			mAdapter.setOnTreeNodeClickListener(new OnTreeNodeClickListener() {
				@Override
				public void onClick(Node node, int position) {

					if (!("".equals(node.getUserId()))) {
						Intent intent = new Intent();
						Bundle bundle = new Bundle();
						bundle.putString("userId", node.getUserId());
						bundle.putString("company", node.getUserCompany());
						bundle.putString("department", node.getUserDepartment());
						intent.putExtras(bundle);
						setResult(0, intent);
						finish();
						AddressListActivity.this.overridePendingTransition(
								R.anim.in, R.anim.out);
					}
				}

			});

			mTree.setAdapter(mAdapter);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
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

	private Bitmap getUserPhoto(String userPhotoUrl) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		Bitmap bm = BitmapFactory.decodeFile(userPhotoUrl, options);
		Bitmap returnBitmap = reduce(bm, 100, 200, false);

		return returnBitmap;
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
	public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {
		switch (arg1) {
		// 当不滚动时
		case OnScrollListener.SCROLL_STATE_IDLE:
			// 判断滚动到底部
			if (arg0.getLastVisiblePosition() == (arg0.getCount() - 1)) {
				listviewimage.setVisibility(View.VISIBLE);
			} else {
				listviewimage.setVisibility(View.GONE);
			}
			break;
		}
	}

	private List<String> checkNoPhotoName(List<String> loadAllPhotoName) {

		List<String> noPhotoName = new ArrayList<String>();
		for (int i = 0; i < loadAllPhotoName.size(); i++) {
			if (checkData2(SDCardRoot
					+ "NewPai"
					+ File.separator
					+ "headPhotoList"
					+ File.separator
					+ loadAllPhotoName.get(i).substring(loadAllPhotoName.get(i).lastIndexOf("\\") + 1,
							loadAllPhotoName.get(i).length()).replace("\\",
									File.separator))) {
				noPhotoName.add(loadAllPhotoName.get(i));
			}
		}
		return noPhotoName;
	}

	private boolean checkData2(String fileName) {
		File file = new File(fileName);
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
	
	
	@Override
	public void finish() {
		onDestroy();
		super.finish();
	}

	@Override
	protected void onDestroy() {			
		super.onDestroy();
	}
	

	private void aniStart() {
		tv1.setClickable(false);
		tv2.setClickable(false);
		addresslistWaitImageView.setVisibility(View.VISIBLE);
	//	addresslistWaitImageView.setBackgroundResource(R.anim.);
		animationDrawable = (AnimationDrawable) addresslistWaitImageView
				.getBackground();
		animationDrawable.start();
	}

	private void aniStop() {
		tv1.setClickable(true);
		tv2.setClickable(true);
		if (null != animationDrawable) {
			animationDrawable.stop();
			addresslistWaitImageView.setVisibility(View.GONE);
		}
	}
}
