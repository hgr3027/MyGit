package com.example.newpie.utils;

import com.example.newpie.R;
import com.nineoldandroids.view.ViewHelper;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

@SuppressLint("NewApi")
public class MySlidingMenu extends HorizontalScrollView {

	private int mScreenWidth;
	private int mMenuRightPadding;
	private LinearLayout layout;
	private ImageView m1; // 第1页图片
	private ImageView m2; // 第2页图片
	private ImageView m3; // 第3页图片
	private ViewGroup m4; // 第4页布局
	private ViewGroup m5; // 第5页布局
	private ImageView m51;

	boolean isOnce;
	boolean isChange;
	int m1Width;
	int m2Width;
	int m3Width;
	int m4Width;
	int m5Width;
	private int menuWidth;
	private Boolean isOpen = false;
	private int lastImage = 0;
	private int currImage = 1;
	float currX = 0;
	float moveX = 0;

	public MySlidingMenu(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.MySlidingMenu, defStyleAttr, 0);
		int n = a.getIndexCount();
		for (int i = 0; i < n; i++) {
			int attr = a.getIndex(i);
			switch (attr) {
			case R.styleable.MySlidingMenu_rightPadding:
				// 默认50
				mMenuRightPadding = a.getDimensionPixelSize(attr,
						(int) TypedValue.applyDimension(
								TypedValue.COMPLEX_UNIT_DIP, 50f,
								getResources().getDisplayMetrics()));
				break;
			}
		}
		a.recycle();

		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metrics);
		mScreenWidth = metrics.widthPixels;
	}

	public MySlidingMenu(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MySlidingMenu(Context context) {
		this(context, null);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		if (!isOnce) {
			layout = (LinearLayout) getChildAt(0);
			m1 = (ImageView) layout.getChildAt(0);
			m2 = (ImageView) layout.getChildAt(1);
			m3 = (ImageView) layout.getChildAt(2);

			m4 = (ViewGroup) layout.getChildAt(3);
			m5 = (ViewGroup) layout.getChildAt(4);
			m51 = (ImageView) m5.getChildAt(0);
			menuWidth = m1.getLayoutParams().width = mScreenWidth;
			m2.getLayoutParams().width = mScreenWidth;
			m3.getLayoutParams().width = mScreenWidth;
			m4.getLayoutParams().width = mScreenWidth;
			// m5.getLayoutParams().width = (int) TypedValue.applyDimension(
			// TypedValue.COMPLEX_UNIT_DIP, 120f, getResources()
			// .getDisplayMetrics());
			isOnce = true;
			ViewHelper.setTranslationX(m5, -50);
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (changed) {
			this.scrollTo(0, 0);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		int action = ev.getAction();
		switch (action) {
		case (MotionEvent.ACTION_UP): {
			if (moveX - currX <= 20 && moveX - currX >= -20) {
				// 点击
				if (currImage == 1) {
					this.smoothScrollTo(menuWidth, 0);
					currImage = 2;
					return true;
				} else if (currImage == 2) {
					this.smoothScrollTo(menuWidth * 2, 0);
					currImage = 3;
					return true;
				} else if (currImage == 3) {
					this.smoothScrollTo(menuWidth * 3, 0);
					currImage = 4;
					lastImage = 3;
					return true;
				} else if (currImage == 4 && lastImage == 3) {
					return false;
				}
			}
		}
		case (MotionEvent.ACTION_DOWN): {
			currX = ev.getX();
			if (currImage == 4 && lastImage == 3) {
				return false;
			}

		}
		case (MotionEvent.ACTION_MOVE): {
			moveX = ev.getX();
			if (moveX - currX > 20) {
				// 右移
				if (currImage == 1) {
					return false;
				} else if (currImage == 4 && lastImage == 3) {
					return false;
				} else {
					this.smoothScrollTo(0, 0);
					currImage = 1;
					return true;
				}
				
			} else if (moveX - currX < -20) {
				// 左移
				if (currImage == 4 && lastImage == 3) {
					return false;
				} else {
					this.smoothScrollTo(menuWidth * 3, 0);
					currImage = 4;
					lastImage = 3;
					return true;
				}
			}
		}
		}
		return super.onTouchEvent(ev);
	}

	public void open() {
		if (isOpen) {
			return;
		} else {
			this.smoothScrollTo(menuWidth, 0);
			isOpen = true;
		}
	}

	public void close() {
		if (!isOpen) {
			return;
		} else {
			this.smoothScrollTo(0, 0);
			isOpen = false;
		}
	}

	public void qc() {
		if (isOpen) {
			this.smoothScrollTo(menuWidth * 3, 0);
			isOpen = false;
			m51.setBackground(getResources().getDrawable(R.drawable.tab_icon1));
		} else {
			this.smoothScrollTo(menuWidth * 3 + 120, 0);
			isOpen = true;
			m51.setBackground(getResources().getDrawable(R.drawable.tab_icon2));
		}
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		// TODO Auto-generated method stub
		super.onScrollChanged(l, t, oldl, oldt);
		if (currImage == 4) {
			float scale = l * 1.0f / menuWidth / 3;
			ViewHelper.setTranslationX(m4, -(menuWidth * 3 * (1 - scale)));
		}

	}
}
