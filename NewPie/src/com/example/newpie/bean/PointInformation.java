package com.example.newpie.bean;

import java.io.Serializable;
import java.util.List;

import android.graphics.Bitmap;

public class PointInformation implements Serializable {

	private String name; // 姓名
	private String department; // 部门
	private double latitude;
	private double longitude;
	private String pointTime; // 时间
	private String stayTime; // 停留时间
	public String getPhotoId() {
		return photoId;
	}

	public void setPhotoId(String photoId) {
		this.photoId = photoId;
	}

	private Bitmap indexBitmap; // 首张图片
	private String photoId;//照片ID
	
	public Bitmap getIndexBitmap() {
		return indexBitmap;
	}

	public void setIndexBitmap(Bitmap indexBitmap) {
		this.indexBitmap = indexBitmap;
	}

	public String getPointTime() {
		return pointTime;
	}

	public void setPointTime(String pointTime) {
		this.pointTime = pointTime;
	}

	public PointInformation() {
		super();
	}

	public PointInformation(String name, String department, double latitude,
			double longitude, String pointTime, String stayTime,
			Bitmap indexBitmap,String photoId) {
		super();
		this.name = name;
		this.department = department;
		this.latitude = latitude;
		this.longitude = longitude;
		this.pointTime = pointTime;
		this.stayTime = stayTime;
		this.indexBitmap = indexBitmap;
		this.photoId = photoId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getStayTime() {
		return stayTime;
	}

	public void setStayTime(String stayTime) {
		this.stayTime = stayTime;
	}

}
