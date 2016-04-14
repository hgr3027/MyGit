package com.tutk.P2PCam264.DELUX;


import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

import com.example.newpie.R;
import com.tutk.P2PCam264.DatabaseManager;
@SuppressWarnings("all")
public class Custom_popupWindow implements View.OnClickListener{

	public boolean auto_dismis = true;
	private PopupWindow popupWindow;
	On_PopupWindow_click_Listener  On_PopupWindow_click_Listener;
	
	public static PopupWindow Menu_PopupWindow_newInstance(Context Context,ViewGroup layout) 
	{
		Custom_popupWindow Menu_popupWindow = new Custom_popupWindow(Context);
		return Menu_popupWindow.Init_popupWindow(Context,layout);
	}
	
	public static PopupWindow Menu_PopupWindow_newInstance(Context Context,ViewGroup layout,On_PopupWindow_click_Listener On_PopupWindow_click_Listener) 
	{
		Custom_popupWindow Menu_popupWindow = new Custom_popupWindow(Context);
		return Menu_popupWindow.Init_popupWindow(Context,layout,On_PopupWindow_click_Listener);
	}
    private Custom_popupWindow(Context Context) {
       
	    super();

    }
    
    

    private PopupWindow Init_popupWindow(Context Context,ViewGroup layout)
    {
 		return Init_popupWindow(Context,layout,null);
    }

   private PopupWindow Init_popupWindow(Context Context,ViewGroup layout, On_PopupWindow_click_Listener On_PopupWindow_click_Listener)
   {
	    popupWindow = new PopupWindow(layout);
	    if(On_PopupWindow_click_Listener!=null)
	    {
	    	this.On_PopupWindow_click_Listener = On_PopupWindow_click_Listener;
	    }
	    Button btn_onDropbox = (Button)layout.findViewById(R.id.btn_onDropbox);
	    if(btn_onDropbox!=null)
	    btn_onDropbox.setOnClickListener(this);
		Button btn_info = (Button)layout.findViewById(R.id.btn_infomation);
		if(btn_info!=null)
		btn_info.setOnClickListener(this);
		Button btn_log_in_out = (Button)layout.findViewById(R.id.btn_log_in_out);
		if(btn_log_in_out!=null)
		{
			DatabaseManager DatabaseManager = new DatabaseManager(Context);
			if(DatabaseManager.getLoginPassword().equals(""))
			{
				btn_log_in_out.setText(R.string.optLogin);
			}
			else
			{
				btn_log_in_out.setText(R.string.optLogout);
			}
			btn_log_in_out.setOnClickListener(this);
		}
		
		Button btn_onSwitchCodec = (Button)layout.findViewById(R.id.btn_switch_codec);
	    if(btn_onSwitchCodec!=null)
	    	btn_onSwitchCodec.setOnClickListener(this);
	    
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setWidth(LayoutParams.WRAP_CONTENT);    
		popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setFocusable(true);
		return popupWindow;
   }
    
    public interface On_PopupWindow_click_Listener
    {
    	public void   btn_onDropbox_click(final PopupWindow PopupWindow);
        public void   btn_infomation_click(final PopupWindow PopupWindow);
        public void   btn_log_in_out_click(final PopupWindow PopupWindow);
        public void   btn_switch_codec_config_click(final PopupWindow PopupWindow);
    }
    
    public void btn_onDropbox_click(PopupWindow PopupWindow)
    {
    	On_PopupWindow_click_Listener.btn_onDropbox_click(PopupWindow);
    }  
    public void btn_infomation_click(PopupWindow PopupWindow)
    {
    	On_PopupWindow_click_Listener.btn_infomation_click(PopupWindow);
    }  
    public void btn_log_in_out_click(PopupWindow PopupWindow)
    {
    	On_PopupWindow_click_Listener.btn_log_in_out_click(PopupWindow);
    } 
    public void  set_button_click_Listener( On_PopupWindow_click_Listener _On_PopupWindow_click_Listener)
    {
    	On_PopupWindow_click_Listener = _On_PopupWindow_click_Listener ;
    }
    public void btn_onSwtichCodec_click(PopupWindow PopupWindow)
    {
    	On_PopupWindow_click_Listener.btn_switch_codec_config_click(PopupWindow);
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(this==null||On_PopupWindow_click_Listener==null)
		{
			popupWindow.dismiss();
			return;
		}
		switch(v.getId())
		{
			case R.id.btn_onDropbox:
				On_PopupWindow_click_Listener.btn_onDropbox_click(popupWindow);
				break;
			case R.id.btn_infomation:
				On_PopupWindow_click_Listener.btn_infomation_click(popupWindow);
				break;
			case R.id.btn_log_in_out:
				On_PopupWindow_click_Listener.btn_log_in_out_click(popupWindow);
				break;
			case R.id.btn_switch_codec:
				On_PopupWindow_click_Listener.btn_switch_codec_config_click(popupWindow);
				break;
		}
		if(auto_dismis)
			popupWindow.dismiss();
	}
	
}
