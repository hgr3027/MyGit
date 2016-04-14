package com.tutk.P2PCam264;

import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.newpie.R;

@SuppressWarnings("all")
public class PhotoViewerActivity  extends Activity implements GestureDetector.OnGestureListener, OnTouchListener {
    
	private static final int FLING_MIN_DISTANCE = 20;
	private static final int FLING_MIN_VELOCITY = 0;
	private GestureDetector mGestureDetector;
	private String mFileName;
	private List<String> IMAGE_FILES;
	private int mPosition;
	private int mSize;
	private Bitmap bm;
	private ImageView iv;
    
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	
          super.onCreate(savedInstanceState);
		  ActionBar actionBar = getActionBar();
		  actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); 
		  actionBar.setCustomView(R.layout.titlebar);
		  TextView tv = (TextView)this.findViewById(R.id.bar_text);
		  tv.setText(getText(R.string.ctxViewSnapshot));
          System.gc();
          Intent i = getIntent();
          Bundle extras = i.getExtras();
          //BitmapFactory.Options bfo = new BitmapFactory.Options();
          //bfo.inSampleSize = 2;
          mFileName = extras.getString("filename");
          IMAGE_FILES = extras.getStringArrayList("files");
          mPosition = extras.getInt("pos");
          mSize = IMAGE_FILES.size();
          iv = new ImageView(getApplicationContext());
          mGestureDetector = new GestureDetector(this);
          iv.setOnTouchListener(this);
          bm = BitmapFactory.decodeFile(mFileName);// ,bfo);
          iv.setImageBitmap(bm);
          setContentView(iv);
    }

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		mGestureDetector.onTouchEvent(event);
		return true;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
			// Fling left
			if (mPosition == (mSize - 1)) {
				mPosition = 0;
				mFileName = IMAGE_FILES.get(mPosition);
				bm = BitmapFactory.decodeFile(mFileName);
		        iv.setImageBitmap(bm);
		        setContentView(iv);				
			} else {
				mPosition += 1;
				mFileName = IMAGE_FILES.get(mPosition);
				bm = BitmapFactory.decodeFile(mFileName);
		        iv.setImageBitmap(bm);
		        setContentView(iv);	
			}
		} else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
			// Fling right
			if (mPosition == 0) {
				mPosition = mSize - 1;
				mFileName = IMAGE_FILES.get(mPosition);
				bm = BitmapFactory.decodeFile(mFileName);
		        iv.setImageBitmap(bm);
		        setContentView(iv);	
			} else {
				mPosition -= 1;
				mFileName = IMAGE_FILES.get(mPosition);
				bm = BitmapFactory.decodeFile(mFileName);
		        iv.setImageBitmap(bm);
		        setContentView(iv);	
			}
		}
		return false;
	} 
}
