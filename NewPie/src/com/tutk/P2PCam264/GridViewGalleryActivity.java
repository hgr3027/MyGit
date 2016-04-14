package com.tutk.P2PCam264;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.example.newpie.R;
import com.tutk.Logger.Glog;
/*
 import pl.polidea.coverflow.CoverFlow;

 import pl.polidea.coverflow.AbstractCoverFlowImageAdapter;
 import pl.polidea.coverflow.ReflectingImageAdapter;
 import pl.polidea.coverflow.ResourceImageAdapter;
 */

@SuppressLint({ "all", "InflateParams" })
public class GridViewGalleryActivity extends SherlockActivity {

	private String TAG = "GridViewGalleryActivity";

	private String imagesPath;
	private String videosPath;
	private GridView gridview;
	private RelativeLayout bottombar;

	private Button btnEdit;
	private Button btnDel;
	private ImageButton btn_change_mode;
	private TextView tvPhoto;
	private TextView tvVideo;

//	private boolean mThumbnaillDone = false;

	private enum MyMode {
		PHOTO, VIDEO
	}

	private MyMode mMode = MyMode.PHOTO;

	/** The Constant DEFAULT_LIST_SIZE. */
	private static final int DEFAULT_LIST_SIZE = 1;
	/** The Constant IMAGE_RESOURCE_IDS. */
	final List<String> IMAGE_FILES = new ArrayList<String>(DEFAULT_LIST_SIZE);
	final List<String> VIDEO_FILES = new ArrayList<String>(DEFAULT_LIST_SIZE);
	private List<String> videoPath = new ArrayList<String>(DEFAULT_LIST_SIZE);
	private List<Bitmap> videoImage = new ArrayList<Bitmap>(DEFAULT_LIST_SIZE);
	private List<Boolean> multiDel_photo = new ArrayList<Boolean>();
	private List<Boolean> multiDel_video = new ArrayList<Boolean>();
	private ImageAdapter imageAdapter;
	private VideoAdapter adapterVideo;
	private Object mLock = new Object();
	private boolean mIsEdit = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setCustomView(R.layout.titlebar);
		TextView tv = (TextView) this.findViewById(R.id.bar_text);
		btnEdit = (Button) findViewById(R.id.bar_right_btn);
		btn_change_mode = (ImageButton) findViewById(R.id.bar_btn_mid);
		tvPhoto = (TextView) findViewById(R.id.bar_txt_photo);
		tvVideo = (TextView) findViewById(R.id.bar_txt_video);

//		  tv.setText(getText(R.string.ctxViewSnapshot));
		tv.setVisibility(View.GONE);
		btnEdit.setText(R.string.txt_edit);
		btnEdit.setTextColor(Color.WHITE);
		btnEdit.setVisibility(View.VISIBLE);
		btn_change_mode.setVisibility(View.VISIBLE);
		tvPhoto.setVisibility(View.VISIBLE);
		tvPhoto.setTextColor(Color.WHITE);
		tvVideo.setVisibility(View.VISIBLE);
		tvVideo.setTextColor(Color.BLACK);

		btnEdit.setOnClickListener(btnEditClick);
		btn_change_mode.setOnClickListener(mode_change);

		super.onCreate(savedInstanceState);
		System.gc();
		Bundle extras = this.getIntent().getExtras();
		imagesPath = extras.getString("images_path"); // XXX: extras may be null and data stored in
														// intent directly. WTF
		videosPath = extras.getString("videos_path");
		// imagesPath = this.getIntent().getStringExtra("images_path") ;
		setContentView(R.layout.gridviewgalleryactivity);
		bottombar = (RelativeLayout) findViewById(R.id.gridview_bottom);
		btnDel = (Button) findViewById(R.id.gridview_btn_delete);
		btnDel.setOnClickListener(btnDelClick);
		setImagesPath(imagesPath);
//          setImagesPath("/storage/emulated/0/Movies");
		removeCorruptImage();

		setVideoPath(videosPath);
		adapterVideo = new VideoAdapter(this);
		imageAdapter = new ImageAdapter(this);
		gridview = (GridView) findViewById(R.id.gridview);

		gridview.setAdapter(imageAdapter);

		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				if (!mIsEdit) {
					if (mMode == MyMode.PHOTO) {
						Intent intent = new Intent(GridViewGalleryActivity.this, PhotoViewerActivity.class);
						String fileName = IMAGE_FILES.get(position);
						int size = IMAGE_FILES.size();
						Bundle bundle = new Bundle();
						bundle.putStringArrayList("files", (ArrayList<String>) IMAGE_FILES);
						bundle.putString("filename", fileName);
						bundle.putInt("size", size);
						bundle.putInt("pos", position);
						intent.putExtras(bundle);
						startActivity(intent);
					} else {
//                		  Intent intent = new Intent(Intent.ACTION_VIEW);
//                		  String type = "video/mp4";
//                		  String path = VIDEO_FILES.get(position);
//                		  Uri name = Uri.parse("file://"+path);
//                		  intent.setDataAndType(name, type);

						Intent intent = new Intent(GridViewGalleryActivity.this, LocalPlaybackActivity.class);
						int mSize = VIDEO_FILES.size();
						Bundle bundle = new Bundle();
						bundle.putStringArrayList("videos", (ArrayList<String>) VIDEO_FILES);
						bundle.putInt("position", position);
						bundle.putInt("size", mSize);
						intent.putExtras(bundle);
						startActivity(intent);
					}
				} else {
					if (mMode == MyMode.PHOTO) {
						multiDel_photo.set(position, !multiDel_photo.get(position));
						imageAdapter.notifyDataSetChanged();
					} else {
						multiDel_video.set(position, !multiDel_video.get(position));
						adapterVideo.notifyDataSetChanged();
					}
				}
			}
		});
		gridview.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, final long id) {
				// TODO Auto-generated method stub
				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case DialogInterface.BUTTON_POSITIVE:
							// Yes button clicked
							GridViewGalleryActivity.this.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									if (mMode == MyMode.PHOTO) {
									} else {
										File file = new File(VIDEO_FILES.get(position));
										videoImage.remove(position);
										videoPath.remove(position);
										VIDEO_FILES.remove(position);
										file.delete();
										if (file.exists()) {
											try {
												mLock.wait(1000);
											} catch (InterruptedException e) {
												e.printStackTrace();
											}
										}
										
										setVideoPath(videosPath);
										adapterVideo.notifyDataSetChanged();
									}
									// XXX: CA's hack, adapter.notifyDataSetChanged not working when
									// delete item
									// It's strange.
									// Intent intent = new Intent(CoverFlowGalleryActivity.this,
									// CoverFlowGalleryActivity.class);
									// intent.putExtra("images_path", imagesPath);
									// startActivity(intent);
									// finish() ;
								}
							});
							break;

						case DialogInterface.BUTTON_NEGATIVE:
							// No button clicked
							break;
						}
					}
				};
				if (mMode == MyMode.PHOTO) {
					AlertDialog.Builder builder = new AlertDialog.Builder(GridViewGalleryActivity.this);

					builder.setMessage(getResources().getString(R.string.dlgAreYouSureToDeleteThisSnapshot))
							.setPositiveButton(getResources().getString(R.string.dlgDeleteSnapshotYes), dialogClickListener)
							.setNegativeButton(getResources().getString(R.string.dlgDeleteSnapshotNo), dialogClickListener).show();

				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(GridViewGalleryActivity.this);

					builder.setMessage(getResources().getString(R.string.dlgAreYouSureToDeleteThisRecord))
							.setPositiveButton(getResources().getString(R.string.dlgDeleteSnapshotYes), dialogClickListener)
							.setNegativeButton(getResources().getString(R.string.dlgDeleteSnapshotNo), dialogClickListener).show();
				}
				return true;
			}
		});
	}

	private void setVideoPath(final String path) {
		VIDEO_FILES.clear();
		multiDel_video.clear();
		videoPath.clear();
		videoImage.clear();
		
		File folder = new File(path);
		String[] videoFiles = folder.list();

		if (videoFiles != null && videoFiles.length > 0) {
			Arrays.sort(videoFiles);
			for (final String videofile : videoFiles) {
				videoPath.add(videofile);
				Bitmap res_to_bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ceo_record_clicked);
				videoImage.add(res_to_bmp);
				multiDel_video.add(false);
				VIDEO_FILES.add(path + "/" + videofile);
			}
		}
		
		Thread mThread = new Thread(new Runnable() {
			@Override
			public void run() {
				setVideoImage(videosPath);
			}
		});
		mThread.start();
	}

	private void setVideoImage(final String path) {
		Glog.I(TAG, "Start to build the thumnail");
//		mThumbnaillDone = true;
		File folder = new File(path);
		String[] videoFiles = folder.list();
		int i = 0;

		if (videoFiles != null && videoFiles.length > 0) {
			Arrays.sort(videoFiles);
			for (final String videofile : videoFiles) {
				Bitmap bmp = ThumbnailUtils.createVideoThumbnail(path + "/" + videofile, MediaStore.Video.Thumbnails.MINI_KIND);
				if (bmp == null) {
					Bitmap res_to_bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ceo_record);
					videoImage.set(i, res_to_bmp);
				} else {
					videoImage.set(i, bmp);
				}
				i += 1;
			}
			Glog.I(TAG, "Thumbnaill done");

			if (mMode == MyMode.VIDEO) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						adapterVideo.notifyDataSetChanged();
					}
				});
			}
		}
	}

	public final void removeCorruptImage() {
		Iterator<String> it = IMAGE_FILES.iterator();
		while (it.hasNext()) {
			String path = it.next();
			Bitmap bitmap = BitmapFactory.decodeFile(path);
			// XXX: CA's hack, snapshot may fail and create corrupted bitmap
			if (bitmap == null) {
				it.remove();
			}
		}
	}

	public final synchronized void setImagesPath(String path) {
		IMAGE_FILES.clear();
		multiDel_photo.clear();
		File folder = new File(path);
		String[] imageFiles = folder.list();

		if (imageFiles != null && imageFiles.length > 0) {
			Arrays.sort(imageFiles);
			for (String imageFile : imageFiles) {
				IMAGE_FILES.add(path + "/" + imageFile);
				multiDel_photo.add(false);
			}
			Collections.reverse(IMAGE_FILES);
		}
	}

	public class ImageAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private Context mContext;

		public ImageAdapter(Context c) {
			this.mInflater = LayoutInflater.from(c);
			mContext = c;
		}

		public int getCount() {
			return IMAGE_FILES.size();
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return position;
		}

		// create a new ImageView for each item referenced by the Adapter
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;
			convertView = new RelativeLayout(mContext);
			holder = new ViewHolder();

			View view = mInflater.inflate(R.layout.gridview_photo_item, null);
//			holder.photo_thumb_img = new ImageView(mContext);
//			((ViewGroup) view).addView(holder.photo_thumb_img);
			view.setPadding(8, 8, 8, 8);
			((ViewGroup) convertView).addView(view);
			holder.del_check_img = (ImageView) convertView.findViewById(R.id.video_image_check);
			holder.photo_thumb_img = (ImageView) convertView.findViewById(R.id.video_image);

			if (holder != null) {
				BitmapFactory.Options bfo = new BitmapFactory.Options();
				bfo.inSampleSize = 4;

				Bitmap bitmap = BitmapFactory.decodeFile(IMAGE_FILES.get(position), bfo);

				// XXX: CA's hack, snapshot may fail and create corrupted bitmap
				if (bitmap == null) {
					for (int i = this.getCount() - 1; i >= 0; i--) {
						bitmap = BitmapFactory.decodeFile(IMAGE_FILES.get(i), bfo);
						if (bitmap != null)
							break;
					}
				}

				holder.photo_thumb_img.setImageBitmap(bitmap);
				if (multiDel_photo.get(position)) {
					holder.del_check_img.setVisibility(View.VISIBLE);
				} else {
					holder.del_check_img.setVisibility(View.GONE);
				}
			}

			return convertView;
		}

		public final boolean deleteImageAtPosition(int position) {
			File file = new File(IMAGE_FILES.get(position));
			boolean deleted = file.delete();
			IMAGE_FILES.remove(position);
			this.notifyDataSetChanged();
			return deleted;
		}

		public final class ViewHolder {
			public ImageView photo_thumb_img;
			public ImageView del_check_img;
		}
	}

	public class VideoAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private Context mContext;

		public VideoAdapter(Context c) {
			this.mInflater = LayoutInflater.from(c);
			mContext = c;
		}

		public int getCount() {
			return VIDEO_FILES.size();
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return position;
		}

		// create a new ImageView for each item referenced by the Adapter
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;
			convertView = new RelativeLayout(mContext);
			holder = new ViewHolder();

			View view = mInflater.inflate(R.layout.gridview_video_item, null);
			view.setPadding(8, 8, 8, 8);
			((ViewGroup) convertView).addView(view);
			holder.del_check_img = (ImageView) convertView.findViewById(R.id.video_image_check);
			holder.video_thumb_img = (ImageView) convertView.findViewById(R.id.video_image);
			holder.video_text = (TextView) convertView.findViewById(R.id.video_text);

			if (holder != null) {
				holder.video_thumb_img.setImageBitmap(videoImage.get(position));
//				holder.video_thumb_img.setBackgroundResource(R.drawable.ceo_record_clicked);
				holder.video_text.setText(videoPath.get(position));
				if (multiDel_video.get(position)) {
					holder.del_check_img.setVisibility(View.VISIBLE);
				} else {
					holder.del_check_img.setVisibility(View.GONE);
				}
			}

			return convertView;
		}

		public final boolean deleteImageAtPosition(int position) {
			File file = new File(IMAGE_FILES.get(position));
			boolean deleted = file.delete();
			IMAGE_FILES.remove(position);
			this.notifyDataSetChanged();
			return deleted;
		}

		public final class ViewHolder {
			public ImageView video_thumb_img;
			public ImageView del_check_img;
			public TextView video_text;
		}
	}

//    private void setNewItem(){
//    	video_GridView_item.clear();
//        for (int i = 0; i < VIDEO_FILES.size(); i++) {
//            Map<String, Object> item = new HashMap<String, Object>();
//            item.put("image", videoImage.get(i));
//            item.put("text", videoPath.get(i));
//            video_GridView_item.add(item);
//       }
//        
//        adapterVideo = new SimpleAdapter(this, 
//        								video_GridView_item, 
//        								R.layout.gridview_video_item, 
//        								new String[]{"text","image"}, 
//        								new int[]{R.id.video_text,R.id.video_image});
//        
//        adapterVideo.setViewBinder(new ViewBinder() {			
//			@Override
//			public boolean setViewValue(View view, Object data,
//					String textRepresentation) {
//	            if( (view instanceof ImageView) & (data instanceof Bitmap) ) {  
//	                ImageView iv = (ImageView) view;  
//	                Bitmap bm = (Bitmap) data;  
//	                iv.setImageBitmap(bm);  
//	                return true;  
//	            }    
//				return false;
//			}
//		});
//        
//        if(mMode == MyMode.VIDEO){
//        	runOnUiThread(new Runnable() {				
//				@Override
//				public void run() {
//		        	gridview.setAdapter(adapterVideo);
//				}
//			});
//        }
//    }

	private OnClickListener mode_change = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mMode == MyMode.PHOTO) {
				btn_change_mode.setBackgroundResource(R.drawable.ceo_record_tab_r);
				mMode = MyMode.VIDEO;
				tvPhoto.setTextColor(Color.BLACK);
				tvVideo.setTextColor(Color.WHITE);

//		        setVideoPath(videosPath);
//				setNewItem();
				gridview.setAdapter(adapterVideo);
//				Thread mThread = new Thread(new Runnable() {
//					@Override
//					public void run() {
//						setVideoImage(videosPath);
//					}
//				});
//				if (!mThumbnaillDone) {
//					mThread.start();
//				}
			} else {
				btn_change_mode.setBackgroundResource(R.drawable.ceo_record_tab_l);
				tvPhoto.setTextColor(Color.WHITE);
				tvVideo.setTextColor(Color.BLACK);
				setImagesPath(imagesPath);
				gridview.setAdapter(imageAdapter);
				removeCorruptImage();
				imageAdapter.notifyDataSetChanged();
				mMode = MyMode.PHOTO;
			}
		}
	};

	private OnClickListener btnEditClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (!mIsEdit) {
				bottombar.startAnimation(AnimationUtils.loadAnimation(GridViewGalleryActivity.this, R.anim.bottombar_slide_show));
				bottombar.setVisibility(View.VISIBLE);
				btnEdit.setText(getString(R.string.txt_done));
				mIsEdit = true;
			} else {
				bottombar.startAnimation(AnimationUtils.loadAnimation(GridViewGalleryActivity.this, R.anim.bottombar_slide_hide));
				bottombar.setVisibility(View.GONE);
				btnEdit.setText(R.string.txt_edit);

				if (mMode == MyMode.PHOTO) {
					for (int i = 0; i < multiDel_photo.size(); i++) {
						multiDel_photo.set(i, false);
					}
					imageAdapter.notifyDataSetChanged();
				} else {
					for (int i = 0; i < multiDel_video.size(); i++) {
						multiDel_video.set(i, false);
					}
					adapterVideo.notifyDataSetChanged();
				}
				mIsEdit = false;
			}

		}
	};

	private OnClickListener btnDelClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case DialogInterface.BUTTON_POSITIVE:
						if (mMode == MyMode.PHOTO) {
							int deleteList = multiDel_photo.size();
							for (int i = 0; i < deleteList; i++) {
								if (multiDel_photo.get(i)) {
									File file = new File(IMAGE_FILES.get(i));
									file.delete();
								}
							}
							setImagesPath(imagesPath);
							imageAdapter.notifyDataSetChanged();
						} else {
							int deleteList = multiDel_video.size();
							for (int i = 0; i < deleteList; i++) {
								if (multiDel_video.get(i)) {
									File file = new File(VIDEO_FILES.get(i));
									file.delete();

									if (file.exists()) {
										try {
											mLock.wait(1000);
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
									}
								}
							}

							setVideoPath(videosPath);
							adapterVideo.notifyDataSetChanged();
						}
						break;

					case DialogInterface.BUTTON_NEGATIVE:
						// No button clicked
						break;
					}
				}
			};
			if (mMode == MyMode.PHOTO) {
				AlertDialog.Builder builder = new AlertDialog.Builder(GridViewGalleryActivity.this);

				builder.setMessage(getResources().getString(R.string.dlgAreYouSureToDeleteThisSnapshot))
						.setPositiveButton(getResources().getString(R.string.dlgDeleteSnapshotYes), dialogClickListener)
						.setNegativeButton(getResources().getString(R.string.dlgDeleteSnapshotNo), dialogClickListener).show();

			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(GridViewGalleryActivity.this);

				builder.setMessage(getResources().getString(R.string.dlgAreYouSureToDeleteThisRecord))
						.setPositiveButton(getResources().getString(R.string.dlgDeleteSnapshotYes), dialogClickListener)
						.setNegativeButton(getResources().getString(R.string.dlgDeleteSnapshotNo), dialogClickListener).show();
			}
		}
	};

}
