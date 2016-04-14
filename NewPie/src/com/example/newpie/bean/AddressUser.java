package com.example.newpie.bean;

import java.io.Serializable;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/*
 * 1--变量
 * 2--构造
 * 3--Get Set
 * 4--序列化
 * 5--反序列化
 */

public class AddressUser implements Serializable {

	private String id;// Id
	private String pId;// 父ID
	private String userId; // 用户ID
	private String userName; // 用户名
	private Bitmap userPhoto; // 照片
	private String userCompany;//公司
	private String userDepartment;//部门
	
	
	
	public String getUserDepartment() {
		return userDepartment;
	}

	public void setUserDepartment(String userDepartment) {
		this.userDepartment = userDepartment;
	}

	public String getUserCompany() {
		return userCompany;
	}

	public void setUserCompany(String userCompany) {
		this.userCompany = userCompany;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Bitmap getUserPhoto() {
		return userPhoto;
	}

	public void setUserPhoto(Bitmap userPhoto) {
		this.userPhoto = userPhoto;
	}

	public String getpId() {
		return pId;
	}

	public void setpId(String pId) {
		this.pId = pId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	// public static Parcelable.Creator<AddressUser> getCreator() {
	// return CREATOR;
	// }

	/*
	 * 无参构造
	 */
	public AddressUser() {
		super();
	}

	public AddressUser(String id, String pId, String userId, String userName,
			Bitmap userPhoto, String userCompany,String userDepartment) {
		super();
		this.id = id;
		this.pId = pId;
		this.userId = userId;
		this.userName = userName;
		this.userPhoto = userPhoto;
		this.userCompany = userCompany;
		this.userDepartment = userDepartment;
	}

	

	// @Override
	// public int describeContents() {
	// // TODO Auto-generated method stub
	// return 0;
	// }
	//
	// public AddressUser(Parcel source) {
	// id = source.readString();
	// pId = source.readString();
	// userId = source.readString();
	// userName = source.readString();
	// // userPhoto = Bitmap.CREATOR.createFromParcel(source);
	// userPhoto = source.readString();
	// }
	//
	// /*
	// * (non-Javadoc)序列化
	// *
	// * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	// */
	// @Override
	// public void writeToParcel(Parcel arg0, int arg1) {
	// arg0.writeInt(id);
	// arg0.writeInt(pId);
	// arg0.writeString(userId);
	// arg0.writeString(userName);
	// userPhoto.writeToParcel(arg0, 0);
	// }
	//
	// /*
	// * 反序列化
	// */
	// public static final Parcelable.Creator<AddressUser> CREATOR = new
	// Creator<AddressUser>() {
	//
	// @Override
	// public AddressUser createFromParcel(Parcel source) {
	// return new AddressUser(source);
	// }
	//
	// @Override
	// public AddressUser[] newArray(int size) {
	// // TODO Auto-generated method stub
	// return new AddressUser[size];
	// }
	// };

}
