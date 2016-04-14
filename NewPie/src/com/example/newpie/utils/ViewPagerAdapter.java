package com.example.newpie.utils;


import java.util.ArrayList; 
import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
 
public class ViewPagerAdapter extends PagerAdapter {
 
   
	private List<View> listViews;// content  
	private List<String> loadNoPhotoList;
	  
    private int size;// 页数  

    public ViewPagerAdapter(List<View> listViews,List<String> loadNoPhotoList ) {// 构造函数  
                                                        // 初始化viewpager的时候给的一个页面  
        this.listViews = listViews;  
        this.loadNoPhotoList = loadNoPhotoList;
        size = listViews == null ? 0 : listViews.size();  
    }  

    public void setListViews() {// 自己写的一个方法用来添加数据  这个可是重点啊  
        size = listViews == null ? 0 : listViews.size();  
    }  

    @Override  
    public int getCount() {// 返回数量  
        return size;  
    }  

    @Override  
    public void destroyItem(View arg0, int arg1, Object arg2) {// 销毁view对象  
        ((ViewPager) arg0).removeView(listViews.get(arg1 % size));  
    }  

    @Override  
    public void finishUpdate(View arg0) {  
    }  

    @Override  
    public Object instantiateItem(View arg0, int arg1) {// 返回view对象  
        try {  
            ((ViewPager) arg0).addView(listViews.get(arg1 % size), 0);  
        } catch (Exception e) {  
        	e.printStackTrace();
        }  
        return listViews.get(arg1 % size);  
    }  

    @Override  
    public boolean isViewFromObject(View arg0, Object arg1) {  
        return arg0 == arg1;  
    }  

}  