package cn.com.tiantong.game72.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NS {
	
//	public static final String tag = "NS";
	//判断网络是否正常连接
	public static ConnectivityManager connectivityManager;
	public static NetworkInfo info;
	
	public static boolean chargeIsConnection(Context context){
		//网络是否已经打开
		connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		info = connectivityManager.getActiveNetworkInfo();
		
		if(info != null && info.isAvailable()){
			return true;
		}else{
			return false;
		}
	}
	
	//判断是否为wifi连接
	public static boolean isWifi(Context mContext) {  
	    ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);  
	    if(connectivityManager != null){
	    	NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();  
	    	if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {  
	    		return true;  
	    	}  
	    }
	    return false;  
	}
	
	//判断网络是否是cmwap或者cmnet
	public static boolean isMobileConnect(Context context){
		
		// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
		//LH.LogPrint(tag, "判断网络是否使用的是手机网络");
		
		try {
			ConnectivityManager manager = (ConnectivityManager)context.getSystemService(context.CONNECTIVITY_SERVICE);
			if(manager != null){
				// 获取网络连接管理的对象 
				NetworkInfo info = manager.getActiveNetworkInfo();
				
				if(info != null && info.isConnected()){
					
					//LH.LogPrint(tag, "info.getType() --v> " + info.getType());
					
					if(info.getType() == ConnectivityManager.TYPE_MOBILE){
						
						//LH.LogPrint(tag, "网络类型：" + info.getExtraInfo());
						return true;
						
					}else{
						//LH.LogPrint(tag, "网络类型：" + info.getExtraInfo());
					}
						
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
		
	}
	
	//判断网络连接方式：cmwap
	public static boolean isCMWAP(Context cm)
	{
		boolean ret = false;
	    ConnectivityManager mag = (ConnectivityManager) cm.getSystemService(Context.CONNECTIVITY_SERVICE);    
	    // 此处输出当前可用网络    
	    //LH.LogPrint(tag,"\nActive:\n");    
	    if(mag!= null)
	    {
		    NetworkInfo info = mag.getActiveNetworkInfo();    
		    if(info!=null)
		    {
		    	//LH.LogPrint(tag,"ExtraInfo=" + info.getExtraInfo() + "\n");    
		    	//LH.LogPrint(tag,"SubtypeName=" + info.getSubtypeName() + "\n");    
		    	//LH.LogPrint(tag,"TypeName=" + info.getTypeName() + "\n");    
			    if(info.getTypeName().compareToIgnoreCase("mobile")==0)
			    {
			    	if(info.getExtraInfo()!=null)
			    	{
				    	if(info.getExtraInfo().compareToIgnoreCase("cmwap")==0)
				    	{
				    		ret = true;
				    	}
			    	}
			    }
		    }
	    }
	    
		return ret;
	}
	
	//判断网络连接方式：cmnet
	public static boolean isCMNET(Context cm)
	{
		boolean ret = false;
	    ConnectivityManager mag = (ConnectivityManager) cm.getSystemService(Context.CONNECTIVITY_SERVICE);    
	    // 此处输出当前可用网络    
	    //LH.LogPrint(tag,"\nActive:\n");    
	    if(mag!= null)
	    {
		    NetworkInfo info = mag.getActiveNetworkInfo();    
		    if(info!=null)
		    {
		    	//LH.LogPrint(tag,"ExtraInfo=" + info.getExtraInfo() + "\n");    
		    	//LH.LogPrint(tag,"SubtypeName=" + info.getSubtypeName() + "\n");    
		    	//LH.LogPrint(tag,"TypeName=" + info.getTypeName() + "\n");    
			    if(info.getTypeName().compareToIgnoreCase("mobile")==0)
			    {
			    	if(info.getExtraInfo().compareToIgnoreCase("cmnet")==0)
			    	{
			    		ret = true;
			    	}
			    }
		     }
		  }
		return ret;
	}
}
