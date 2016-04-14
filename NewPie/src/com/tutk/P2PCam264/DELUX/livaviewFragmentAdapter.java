package com.tutk.P2PCam264.DELUX;

import java.util.ArrayList;

import com.tutk.Logger.Glog;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;

public class livaviewFragmentAdapter extends FragmentPagerAdapter implements OnPageChangeListener {
	private static final String TAG = "MultiViewActivity";
	private ArrayList< ArrayList<Chaanel_to_Monitor_Info>> mChaanel_Info;
	private ArrayList<LiveviewFragment> mLiveviewFragment=new ArrayList<LiveviewFragment>();
	private static final int OnePageMultiView_MAX_NUM = 6;
	
	private int page=0;
    public livaviewFragmentAdapter(FragmentManager fm) {
        super(fm);
        
    }
    //當有需要更動連線狀態時
    public void reflash_Status()
	{
    	if(mLiveviewFragment == null)	
    		return;
		for(LiveviewFragment LiveviewFragment:mLiveviewFragment)
		{
			if(LiveviewFragment!=null)
			LiveviewFragment.reflash_Status();
		}
	}
    
    //移除特定UID時
    public void remove_uid(String uid,String uuid)
	{
    	if(mLiveviewFragment == null)	
    		return;
		for(LiveviewFragment LiveviewFragment:mLiveviewFragment)
		{
			if(LiveviewFragment!=null)
			LiveviewFragment.DeleteAllMonitor(uid, uuid);
		}
	}
    
    //連線成功時 要重新撥放monitor
    public void connected_Status(String UID)
	{
    	if(mLiveviewFragment == null)	
    		return;
    	for(LiveviewFragment LiveviewFragment:mLiveviewFragment)
		{
			if(LiveviewFragment!=null)
			LiveviewFragment.connected_Status(UID);
		}
	}
    
    
    public void SetChannelInfo(ArrayList< ArrayList<Chaanel_to_Monitor_Info>> Chaanel_Info)
    {
    	mChaanel_Info=Chaanel_Info;
    	for(int i =0;i<mChaanel_Info.size();i++)
    	{
    		mLiveviewFragment.add(LiveviewFragment.newInstance(mChaanel_Info.get(i)));
    	}
    }
    //機器被修改完成時 用來更新ui資料
    public void ReflashChannelInfo()
    {
    	mLiveviewFragment.get(page).reflash_Status();
    }
    //回傳是否新增1頁
    public boolean ChangeChannelInfo(Chaanel_to_Monitor_Info Chaanel_Info,int MonitorIndex)
    {
    	mLiveviewFragment.get(page).SetMonitor(Chaanel_Info,MonitorIndex);
    	//搜尋陣列有無綁定同一格monitor的資料 沒有則新增 有則取代
    	boolean same_MonitorIndex=false;
    	for(int i =0;i<mChaanel_Info.get(page).size();i++)
    	{
    		if(mChaanel_Info!=null)
    		{
    			if(mChaanel_Info.get(page).get(i)!=null)
    			{
    				if(mChaanel_Info.get(page).get(i).MonitorIndex==MonitorIndex)
    				{
    					if(i<mChaanel_Info.get(page).size()-1)
    					{
    						mChaanel_Info.get(page).set(i, Chaanel_Info);
    						same_MonitorIndex=true;
    					}
    					break;
    				}
    			}
    		}
    	}
    	if(!same_MonitorIndex)
    	{
    	   	mChaanel_Info.get(page).add(Chaanel_Info);
    	}
    	
    	if(mChaanel_Info.get(page).size()==OnePageMultiView_MAX_NUM)
    	{
    		if(MultiViewActivity.SupportMultiPage)
    		{
    			mChaanel_Info.add(new ArrayList<Chaanel_to_Monitor_Info>());
    			mLiveviewFragment.add(LiveviewFragment.newInstance(mChaanel_Info.get(mChaanel_Info.size()-1)));
    		}
    		return true;
    	}
    	else
    	{
    		return false;
    	}
    	
    }
    @Override
	public Fragment getItem(int position) 
    {
    	Glog.D( TAG, "Fragment.getItem( " + position + " )..." );
    	if(position!=0)
    	{
    		mLiveviewFragment.get(position).stop();
    	}
    	return mLiveviewFragment.get(position);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
    	if(mChaanel_Info!=null)
    	{
    		return mChaanel_Info.size();
    	}
    	else
    	{
    		return 0;
    	}
	}

	@Override
	public void onPageScrollStateChanged(int position) 
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2)
	{

	}

	@Override
	public void onPageSelected(int position) 
	{
		// TODO Auto-generated method stub
		Glog.D( TAG, "onPageSelected( " + position + " )..." );
		//mLiveviewFragment[position].start();
		//mLiveviewFragment[page].stop();
		page = position;
	}

}
