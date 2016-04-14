package com.example.newpie.utils;

import android.graphics.Bitmap;

public class FileBean {
//	@TreeNodeId
//	private int _id;
//	@TreeNodePid
//	private int parentId;
//	@TreeNodeLabel
//	private String name;
//	@TreeNodeUserPhoto
//	private Bitmap userPhoto;
//	@TreeNodeUserId
//	private String userId;
//
//	private long length;
//	private String desc;
//
//	public FileBean(int _id, int parentId, String userId, String name, 
//			Bitmap userPhoto) {
//		super();
//		this._id = _id;
//		this.parentId = parentId;
//		this.name = name;
//		this.userPhoto = userPhoto;
//		this.userId = userId;
//	}
	@TreeNodeId
	private String _id;
	@TreeNodePid
	private String parentId;	
	@TreeNodeUserId
	private String _userId;
	@TreeNodeLabel
	private String name;
	@TreeNodeUserPhoto
	private Bitmap userPhoto;
	@TreeNodeUserCompany
	private String _userCompany;
	@TreeNodeUserDepartment
	private String _userDepartment;
	
	

	private long length;
	private String desc;

	public FileBean(String _id, String parentId, String _userId, String name, 
			Bitmap userPhoto,String _userCompany,String _userDepartment) {
		super();
		this._id = _id;
		this.parentId = parentId;
		this._userId = _userId;
		this.name = name;
		this.userPhoto = userPhoto;	
		this._userCompany = _userCompany;
		this._userDepartment = _userDepartment;
		
	}
}
