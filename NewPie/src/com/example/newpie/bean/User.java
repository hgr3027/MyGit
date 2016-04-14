package com.example.newpie.bean;

import java.io.Serializable;
import org.json.JSONArray;

public class User implements Serializable {
	
	
	private String userName;
	private String fullName;
	private String department;
	private String position;
	private String userResources;
	private String userReceiveMessageBox;
	private String fileattribute;
	private String company;
	
	

	public String getUserResources() {
		return userResources;
	}

	public void setUserResources(String userResources) {
		this.userResources = userResources;
	}

	public String getUserReceiveMessageBox() {
		return userReceiveMessageBox;
	}

	public void setUserReceiveMessageBox(String userReceiveMessageBox) {
		this.userReceiveMessageBox = userReceiveMessageBox;
	}

	public User() {
		super();
	}

	public User(String userName, String fullName, String department,
			String position, String userResources,
			String userReceiveMessageBox, String fileattribute, String company) {
		super();
		this.userName = userName;
		this.fullName = fullName;
		this.department = department;
		this.position = position;
		this.userResources = userResources;
		this.userReceiveMessageBox = userReceiveMessageBox;
		this.fileattribute = fileattribute;
		this.company = company;
	}

	

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getFileattribute() {
		return fileattribute;
	}

	public void setFileattribute(String fileattribute) {
		this.fileattribute = fileattribute;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

}
