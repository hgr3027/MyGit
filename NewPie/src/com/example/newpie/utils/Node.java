package com.example.newpie.utils;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.NamedNodeMap;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * http://blog.csdn.net/lmj623565791/article/details/40212367
 * 
 * @author zhy
 * 
 */
public class Node {

	private String id;
	/**
	 * 根节点pId为0
	 */
	private String pId = "0";

	private String name;

	/**
	 * 当前的级别
	 */
	private int level;

	/**
	 * 是否展开
	 */
	private boolean isExpand = false;

	private int icon;

	/**
	 * 下一级的子Node
	 */
	private List<Node> children = new ArrayList<Node>();

	/**
	 * 父Node
	 */
	private Node parent;

	public Bitmap getUserPhoto() {
		return userPhoto;
	}

	public void setUserPhoto(Bitmap userPhoto) {
		this.userPhoto = userPhoto;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserDepartment() {
		return userDepartment;
	}

	public void setUserDepartment(String userDepartment) {
		this.userDepartment = userDepartment;
	}

	private Bitmap userPhoto;
	private String userId;
	private String userCompany;
	private String userDepartment;

	public String getUserCompany() {
		return userCompany;
	}

	public void setUserCompany(String userCompany) {
		this.userCompany = userCompany;
	}	

	public Node() {
	}

	public Node(String id, String pId, String userId, String name,
			Bitmap userPhoto, String userComapny,String userDepartment) {
		super();
		this.id = id;
		this.pId = pId;
		this.userId = userId;
		this.name = name;
		this.userPhoto = userPhoto;
		this.userCompany = userComapny;
		this.userDepartment = userDepartment;
	}

	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getpId() {
		return pId;
	}

	public void setpId(String pId) {
		this.pId = pId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public boolean isExpand() {
		return isExpand;
	}

	public List<Node> getChildren() {
		return children;
	}

	public void setChildren(List<Node> children) {
		this.children = children;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	/**
	 * 是否为跟节点
	 * 
	 * @return
	 */
	public boolean isRoot() {
		return parent == null;
	}

	/**
	 * 判断父节点是否展开
	 * 
	 * @return
	 */
	public boolean isParentExpand() {
		if (parent == null)
			return false;
		return parent.isExpand();
	}

	/**
	 * 是否是叶子界点
	 * 
	 * @return
	 */
	public boolean isLeaf() {
		return children.size() == 0;
	}

	/**
	 * 获取level
	 */
	public int getLevel() {
		return parent == null ? 0 : parent.getLevel() + 1;
	}

	/**
	 * 设置展开
	 * 
	 * @param isExpand
	 */
	public void setExpand(boolean isExpand) {
		this.isExpand = isExpand;
		if (!isExpand) {

			for (Node node : children) {
				node.setExpand(isExpand);
			}
		}
	}

}
