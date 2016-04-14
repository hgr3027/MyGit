package com.example.newpie.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

public class ChatButton extends Button {

	private static final int STATE_NORMAL = 1;
	private static final int STATE_RECODING = 2;
	private static final int STATE_CANCEL = 3;
	private int currState = STATE_NORMAL;
	boolean isRecoding = false;

	public ChatButton(Context context) {
		this(context, null);
	}

	public ChatButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		int action = event.getAction();
		int x = (int) event.getX();
		int y = (int) event.getY();
		switch (action) {
		case (MotionEvent.ACTION_DOWN): {
			changeState(STATE_RECODING);
			break;
		}
		case (MotionEvent.ACTION_MOVE): {
			if (isRecoding) {

			}

			if (wantCancel(x, y)) {
				changeState(STATE_CANCEL);
			} else {
				changeState(STATE_RECODING);
			}

			break;
		}
		case (MotionEvent.ACTION_UP): {
			break;
		}
		}

		return super.onTouchEvent(event);
	}

	private boolean wantCancel(int x, int y) {
		// TODO Auto-generated method stub
		return false;
	}

	private void changeState(int stateRecoding) {
		// TODO Auto-generated method stub

	}

}
