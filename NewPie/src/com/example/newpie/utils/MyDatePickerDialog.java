package com.example.newpie.utils;

import java.util.Calendar;

import com.example.newpie.R;

import android.app.AlertDialog;
import android.app.DatePickerDialog.OnDateSetListener;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TextView;

public class MyDatePickerDialog extends AlertDialog implements
		OnDateChangedListener {

	private static final String YEAR = "year";
	private static final String MONTH = "month";
	private static final String DAY = "day";
	private DatePicker mDatePicker;
	private OnDateSetListener mCallBack;
	private View view;
	
	public interface OnDateSetListener {
		void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth);
	}

	public MyDatePickerDialog(Context context, OnDateSetListener callBack,
			int year, int monthOfYear, int dayOfMonth) {
		this(context, 0, callBack, year, monthOfYear, dayOfMonth);
	}

	public MyDatePickerDialog(Context context, int theme,
			OnDateSetListener callBack, int year, int monthOfYear,
			int dayOfMonth) {
		super(context, theme);
		mCallBack = callBack;
		Context themeContext = getContext();
		LayoutInflater inflater = (LayoutInflater) themeContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.date_picker_dialog, null);
		mDatePicker = (DatePicker) view.findViewById(R.id.datePicker);
		mDatePicker.init(year, monthOfYear, dayOfMonth, this);
		setTitle("选择日期");
		setButton();

	}

	public void myShow() {
		// 自己实现show方法，主要是为了把setContentView方法放到show方法后面，否则会报错。
		show();
		setContentView(view);
	}

	private void setTitle(String title) {
		// 获取自己定义的title布局并赋值。
		((TextView) view.findViewById(R.id.date_picker_title)).setText(title);
	}

	private void setButton() {
		// 获取自己定义的响应按钮并设置监听，直接调用构造时传进来的CallBack接口（为了省劲，没有自己写接口，直接用之前本类定义好的）同时关闭对话框。
		view.findViewById(R.id.date_picker_ok).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (mCallBack != null) {
							mDatePicker.clearFocus();
							mCallBack.onDateSet(mDatePicker,
									mDatePicker.getYear(),
									mDatePicker.getMonth(),
									mDatePicker.getDayOfMonth());
						}
						dismiss();
					}
				});
		view.findViewById(R.id.date_picker_back).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Calendar c = Calendar.getInstance();
						if (mCallBack != null) {
							mDatePicker.clearFocus();
							mCallBack.onDateSet(mDatePicker,
									c.get(Calendar.YEAR), c.get(Calendar.MONTH),
									c.get(Calendar.DAY_OF_MONTH));
						}
						dismiss();
					}
				});
	}

	@Override
	public void onDateChanged(DatePicker view, int year, int month, int day) {
		mDatePicker.init(year, month, day, null);
	}

	public DatePicker getDatePicker() {
		return mDatePicker;
	}

	public void updateDate(int year, int monthOfYear, int dayOfMonth) {
		mDatePicker.updateDate(year, monthOfYear, dayOfMonth);
	}

	@Override
	public Bundle onSaveInstanceState() {
		Bundle state = super.onSaveInstanceState();
		state.putInt(YEAR, mDatePicker.getYear());
		state.putInt(MONTH, mDatePicker.getMonth());
		state.putInt(DAY, mDatePicker.getDayOfMonth());
		return state;
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		int year = savedInstanceState.getInt(YEAR);
		int month = savedInstanceState.getInt(MONTH);
		int day = savedInstanceState.getInt(DAY);
		mDatePicker.init(year, month, day, this);
	}

}
