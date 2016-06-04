package cn.com.tiantong.game72;



import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.Signature;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import cn.com.tiantong.game72.util.DualSimUtil;
import cn.com.tiantong.game72.util.MD5;
import cn.com.tiantong.game72.util.Util;

public class In {

	/*
	 * 获取手机自身的相关信息的类
	 */
	
//	public static final String tag = "InformationForPhone";
	private TelephonyManager tm;
	private Context context;
	
	public In(Context context){
		
		this.tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		this.context = context;
		
	}
	
	//获取IMEI
	public String getIMEI(){
		
		String ret = "000000";
		ret = tm.getDeviceId();
		if(ret == null){
			ret = "000000";
		}
		
		//LH.LogPrint(tag, "IMEI : " + ret);
		
		return ret;
		
	}
	
	//获取IMSI
	public String getIMSI(){
		String imsi = "000000";
		imsi = tm.getSubscriberId();
		if(imsi == null){
			imsi = "000000";
		}
		
		//LH.LogPrint(tag, "裸露的 IMSI ： " + imsi);
		
		return imsi;
	}
	
	public String getCid()
	{
//		PhoneStateListener listener = new PhoneStateListener();
//		tm.listen(listener, 0);
		int PhoneType = tm.getPhoneType();
		int type = tm.getNetworkType();
		String ret = "unknown";	
		
		if(type == 0x00000004//TelephonyManager.NETWORK_TYPE_CDMA
			|| type == 0x00000005//TelephonyManager.NETWORK_TYPE_EVDO_0
			|| type == 0x00000006//TelephonyManager.NETWORK_TYPE_EVDO_A 
			|| type == 0x00000007)//TelephonyManager.NETWORK_TYPE_1xRTT	*/		
		{
		//		cdma = ((CdmaCellLocation) manager.getCellLocation());
		//		Class <android.telephony.cdma.CdmaCellLocation> c = android.telephony.cdma.CdmaCellLocation;   
				Class<?> c = null;
				Method getsetInterfaceMethod = null;      
				try {
					c = Class.forName("android.telephony.cdma.CdmaCellLocation");
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
			        //LH.LogPrint(tag, "getCid CDMA:"+e1.getMessage());	    	
					e1.printStackTrace();
					return ret;
				}
			    try 
			    {        
			    	getsetInterfaceMethod = c.getMethod("getBaseStationId", (Class[])null);
			    	getsetInterfaceMethod.setAccessible(true);
			    } 
			    catch (SecurityException e) 
			    {
			        // TODO Auto-generated catch block
			        //LH.LogPrint(tag, "getCid CDMA:"+e.getMessage());	    	
			        e.printStackTrace();
					return ret;
			    } 
			    catch (NoSuchMethodException e) 
			    {
			        // TODO Auto-generated catch block
			        //LH.LogPrint(tag, "getCid CDMA:"+e.getMessage());	      	
			        e.printStackTrace();
					return ret;
			    }        
			    try 
			    {
//			    	Object obj = c.newInstance(); 
			    	Integer cid = (Integer)getsetInterfaceMethod.invoke(tm.getCellLocation());
					//LH.LogPrint(tag, "MyInformation:CDMA Cid:"+cid);
					return Integer.toString(cid);
			    }
			    catch (IllegalArgumentException e) 
			    {
			        // TODO Auto-generated catch block
			        //LH.LogPrint(tag, "getCid CDMA:"+e.getMessage());	      	
			        e.printStackTrace();
			    } 
			    catch (IllegalAccessException e) 
			    {
			        // TODO Auto-generated catch block
			        //LH.LogPrint(tag, "getCid CDMA:"+e.getMessage());	      	
			        e.printStackTrace();
			    } 
			    catch (InvocationTargetException e) 
			    {
			        //LH.LogPrint(tag, "getCid CDMA:"+e.getMessage());	      	
			        // TODO Auto-generated catch block
			        e.printStackTrace();
			    }		 
		
			    return ret;
		}
		else
		{
		    try 
		    {        
			    GsmCellLocation gcl = (GsmCellLocation)tm.getCellLocation();
				if(null == gcl)
				{
					return ret;
				}	
				int cid = gcl.getCid();	
				//LH.LogPrint(tag, "MyInformation:GSM Cid:"+cid);  		
				return Integer.toString(cid);
		    }
		    catch (Exception e) 
		    {
		        // TODO Auto-generated catch block
		        //LH.LogPrint(tag, "getCid GSM:"+e.getMessage());	      	
		        e.printStackTrace();
		    } 		
		}	
		
		return ret;
	/*	
		GsmCellLocation gcl = (GsmCellLocation)tm.getCellLocation();
		int cid = gcl.getCid();	
		LogHandler.LogPrint(tag, "MyInformation:Cid:"+cid);  		
		return Integer.toString(cid);
	*/
		
	}
	public String getLac()
	{
//		PhoneStateListener listener = new PhoneStateListener();
//		tm.listen(listener, 0);

		int type = tm.getNetworkType();
		String ret = "unknown";	
		if (type == 0x00000004/*TelephonyManager.NETWORK_TYPE_CDMA*/ 
			|| type == 0x00000005/*TelephonyManager.NETWORK_TYPE_EVDO_0*/
			|| type == 0x00000006/*TelephonyManager.NETWORK_TYPE_EVDO_A*/ 
			|| type == 0x00000007/*TelephonyManager.NETWORK_TYPE_1xRTT*/)
		{

//			cdma = ((CdmaCellLocation) manager.getCellLocation());
//			Class <android.telephony.cdma.CdmaCellLocation> c = android.telephony.cdma.CdmaCellLocation;   
			Class<?> c = null;
			Method getsetInterfaceMethod = null;      
			try {
				c = Class.forName("android.telephony.cdma.CdmaCellLocation");
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
		        //LH.LogPrint(tag, "getLac CDMA:"+e1.getMessage());
				e1.printStackTrace();
				return ret;
			}
		    try 
		    {        
		    	getsetInterfaceMethod = c.getMethod("getSystemId", (Class[])null);
		    	getsetInterfaceMethod.setAccessible(true);
		    } 
		    catch (SecurityException e) 
		    {
		        // TODO Auto-generated catch block
		        //LH.LogPrint(tag, "getLac CDMA:"+e.getMessage());	    	
		        e.printStackTrace();
				return ret;
		    } 
		    catch (NoSuchMethodException e) 
		    {
		        // TODO Auto-generated catch block
		        //LH.LogPrint(tag, "getLac CDMA:"+e.getMessage());	      	
		        e.printStackTrace();
				return ret;
		    }        
		    try 
		    {        
//		    	Object obj = c.newInstance(); 
		    	Integer lac = (Integer)getsetInterfaceMethod.invoke(tm.getCellLocation());
				//LH.LogPrint(tag, "MyInformation:CDMA lac:"+lac);  		
				return Integer.toString(lac);
		    }
		    catch (IllegalArgumentException e) 
		    {
		        // TODO Auto-generated catch block
		        //LH.LogPrint(tag, "getLac CDMA:"+e.getMessage());	      	
		        e.printStackTrace();
		    } 
		    catch (IllegalAccessException e) 
		    {
		        // TODO Auto-generated catch block
		        //LH.LogPrint(tag, "getLac CDMA:"+e.getMessage());	      	
		        e.printStackTrace();
		    } 
		    catch (InvocationTargetException e) 
		    {
		        //LH.LogPrint(tag, "getLac CDMA:"+e.getMessage());	      	
		        // TODO Auto-generated catch block
		        e.printStackTrace();
		    }		 

		    return ret;
		}
		else
		{
		    try 
		    {        
		GsmCellLocation gcl = (GsmCellLocation)tm.getCellLocation();
				if(null == gcl)
				{
					return ret;
				}	
		int lac = gcl.getLac();		
				//LH.LogPrint(tag, "MyInformation:GSM Lac:"+lac);  		
		return Integer.toString(lac);
		    }
		    catch (Exception e) 
		    {
		        // TODO Auto-generated catch block
		        //LH.LogPrint(tag, "getLac GSM:"+e.getMessage());	      	
		        e.printStackTrace();
		    } 		
		}	
		
		return ret;
	/*	
		GsmCellLocation gcl = (GsmCellLocation)tm.getCellLocation();
		int lac = gcl.getLac();		
		LogHandler.LogPrint(tag, "MyInformation:Lac:"+lac);  		
		return Integer.toString(lac);
	*/	
	}
	public String getNID()
	{
//		PhoneStateListener listener = new PhoneStateListener();
//		tm.listen(listener, 0);

		int type = tm.getNetworkType();
		String ret = "-1";	
		if (type == 0x00000004/*TelephonyManager.NETWORK_TYPE_CDMA*/ 
			|| type == 0x00000005/*TelephonyManager.NETWORK_TYPE_EVDO_0*/
			|| type == 0x00000006/*TelephonyManager.NETWORK_TYPE_EVDO_A*/ 
			|| type == 0x00000007/*TelephonyManager.NETWORK_TYPE_1xRTT*/)
		{

//			cdma = ((CdmaCellLocation) manager.getCellLocation());
//			Class <android.telephony.cdma.CdmaCellLocation> c = android.telephony.cdma.CdmaCellLocation;   
			Class<?> c = null;
			Method getsetInterfaceMethod = null;      
			try {
				c = Class.forName("android.telephony.cdma.CdmaCellLocation");
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
		        //LH.LogPrint(tag, "getNID CDMA:"+e1.getMessage());	    	
				e1.printStackTrace();
				return ret;
			}
		    try 
		    {        
		    	getsetInterfaceMethod = c.getMethod("getNetworkId", (Class[])null);
		    	getsetInterfaceMethod.setAccessible(true);
		    } 
		    catch (SecurityException e) 
		    {
		        // TODO Auto-generated catch block
		        //LH.LogPrint(tag, "getNID CDMA:"+e.getMessage());	    	
		        e.printStackTrace();
				return ret;
		    } 
		    catch (NoSuchMethodException e) 
		    {
		        // TODO Auto-generated catch block
		        //LH.LogPrint(tag, "getNID CDMA:"+e.getMessage());	      	
		        e.printStackTrace();
				return ret;
		    }        
		    try 
		    {        
//		    	Object obj = c.newInstance(); 
		    	Integer nid = (Integer)getsetInterfaceMethod.invoke(tm.getCellLocation());
				//LH.LogPrint(tag, "MyInformation:CDMA NID:"+nid);  		
				return Integer.toString(nid);
		    }
		    catch (IllegalArgumentException e) 
		    {
		        // TODO Auto-generated catch block
		        //LH.LogPrint(tag, "getNID CDMA:"+e.getMessage());	      	
		        e.printStackTrace();
		    } 
		    catch (IllegalAccessException e) 
		    {
		        // TODO Auto-generated catch block
		        //LH.LogPrint(tag, "getNID CDMA:"+e.getMessage());	      	
		        e.printStackTrace();
		    } 
		    catch (InvocationTargetException e) 
		    {
		        //LH.LogPrint(tag, "getNID CDMA:"+e.getMessage());	      	
		        // TODO Auto-generated catch block
		        e.printStackTrace();
		    }		 

		    return ret;
		}
		
		return ret;
	}
	
	/*
	 * 需要进一步研究其实现
	 */
//	public String getMODELID()
//	{
//		String model = null;
//		Class<android.os.Build> build_class = android.os.Build.class;   
//	    Field field2 = null;
//		try {
//			field2 = build_class.getField("MODEL");
//		} catch (SecurityException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (NoSuchFieldException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		if(field2!=null)
//		{
//		    try {
//				model = (String) field2.get(new android.os.Build());
//			} catch (IllegalArgumentException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IllegalAccessException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}    
//		}
//		if(model==null)
//		{
//			model = Build.MODEL;		
//		}
//	/*
//	 * For OMS phones, Build.MODEL not work on them, so try to extract the model info from UserAgent. 
//	 * Since according to CMCC Specification, model info should be contained in UserAgent.
//	 * */
//		if(model.contains("OMAP"))/*Samsung Ophone handling*/
//		{
//			String  UA = getUserAgent();
//			if(UA != null)
//			model = UA.split(" ")[0];
//		}
//		else if(model.contains("OMS"))/*LG Ophone handling*/
//		{
//			String  UA = getUserAgent();
//			if(UA != null)
//			{
//				String[] clips = UA.split(" ");
//				if(clips.length>1)
//				{
//					model = clips[0]+" "+clips[1];/*"LG" + " " + model name*/
//				}	
//			}	
//		}	
//			
//		LogHandler.LogPrint(tag, "MyInformation:MODELID:"+model);  			
//		return model;
//	}
	
	
	public String getSDKversionnumber()
	{
	    int sdkVersion = Integer.parseInt(Build.VERSION.SDK); 
		//LH.LogPrint(tag, "MyInformation:SDKversion:"+sdkVersion);  		    
	    return Integer.toString(sdkVersion);
	}
	/*
	 * 软件版本号
	 */
	
	
	public String getversionnumber()
	{
		String ret = "9.0";
		//LH.LogPrint(tag, "MyInformation:versionnumber:" + ret);  	
		return ret;
	}
	
	
	public static boolean IsOPHONE()
	{
		boolean ret = false;
		String model = null;
		Class<android.os.Build> build_class = android.os.Build.class;    
	    Field field2 = null;
		try {
			field2 = build_class.getField("MODEL");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(field2!=null)
		{
		    try {
				model = (String) field2.get(new android.os.Build());
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    
		}
		if(model==null)
		{
			model = Build.MODEL;		
		}
		//LH.LogPrint(tag, "IsOPHONE:MODELID:"+model);  	
	/*
	 * For OMS phones, Build.MODEL not work on them, so try to extract the model info from UserAgent. 
	 * Since according to CMCC Specification, model info should be contained in UserAgent.
	 * */
		if(model.contains("OMAP"))/*Samsung Ophone handling*/
		{
			ret = true;
		}
		else if(model.contains("OMS"))/*LG Ophone handling*/
		{
			ret = true;
		}	
		//LH.LogPrint(tag, "IsOPHONE:ret:"+ret);  						
		return ret;
	}
	
	
	public void getinformation()
	{
	    String version = null;
	    String manufacturer = null;
	    String model = null;
	    String device = null;
	    String imei = tm.getDeviceId();

	    String tel = tm.getLine1Number();
	    String imsi = tm.getSubscriberId();
	 //   Toast.makeText(cm, "imei:"+imei, Toast.LENGTH_SHORT).show();   
	    int sdkVersion = Integer.parseInt(Build.VERSION.SDK); 
	    manufacturer = Build.PRODUCT; 
	    model = Build.MODEL;         
	    device = Build.DEVICE;   
	    version = Build.VERSION.RELEASE;
	    String i = Build.ID;
	    String i1 = Build.DISPLAY;
	    String i2 = Build.BOARD;
	    String i3 = Build.FINGERPRINT;
	    String i4 = Build.HOST;
	    String i5 = Build.BRAND;
	    String i6 = Build.VERSION.INCREMENTAL;
//		Toast.makeText(cm,"BRAND:" + i5+ " FINGERPRINT:" + i3+"version:"+version, Toast.LENGTH_SHORT).show();        
	/*        
	    Class<android.os.Build.VERSION> build_version_class = android.os.Build.VERSION.class;
	    //取得 android 版本                
	    Field field = null;
		try {
			field = build_version_class.getField("SDK_INT");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Toast.makeText(this, "2", Toast.LENGTH_SHORT).show();		
	    try {
			version = (Integer) field.get(new android.os.Build.VERSION());
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	*/        
//		Toast.makeText(this, "imei:"+imei+" SDK版本:" + sdkVersion, Toast.LENGTH_SHORT).show();
	/*
		Class<android.os.Build> build_class = android.os.Build.class;
	    //取得牌子
	    Field manu_field = null;
		try {
			manu_field = build_class.getField("MANUFACTURER");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    try {
			manufacturer = (String) manu_field.get(new android.os.Build());
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Toast.makeText(this, "imei:"+imei+"牌子:" + manufacturer + " SDK版本:" + version, Toast.LENGTH_SHORT).show();		
	    //取得型號
	    Field field2 = null;
		try {
			field2 = build_class.getField("MODEL");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    try {
			model = (String) field2.get(new android.os.Build());
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Toast.makeText(this, "imei:"+imei+"牌子:" + manufacturer + " 型號:" + model + " SDK版本:" + version, Toast.LENGTH_SHORT).show();		
	    //模組號碼
	    Field device_field = null;
		try {
			device_field = build_class.getField("DEVICE");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    try {
			device = (String) device_field.get(new android.os.Build());
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
		Toast.makeText(this, "imei:"+imei+"牌子:" + manufacturer + " 型號:" + model + " SDK版本:" + version + " 模組號碼:" + device, Toast.LENGTH_SHORT).show();          
	    LogHandler.LogPrint("android", "牌子:" + manufacturer + " 型號:" + model + " SDK版本:" + version + " 模組號碼:" + device);
	*/
	}	
	/*
	public void call(String number)
	{
	    // 初始化iTelephony
	    Class <TelephonyManager> c = TelephonyManager.class;
	    Method getITelephonyMethod = null;
	    try {
	    getITelephonyMethod = c.getDeclaredMethod("getITelephony", (Class[])null);
	    getITelephonyMethod.setAccessible(true);
	    } catch (SecurityException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    } catch (NoSuchMethodException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    }

	    try {
	    ITelephony iTelephony = (ITelephony) getITelephonyMethod.invoke(tm, (Object[])null);
	    iTelephony.call(number);
	    } catch (IllegalArgumentException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    } catch (IllegalAccessException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    } catch (InvocationTargetException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    }catch (RemoteException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	        }		
	}
	*/
	public void  GetLocationInfo()
	{
		GsmCellLocation gcl = (GsmCellLocation)tm.getCellLocation();
		int cid = gcl.getCid();
		int lac = gcl.getLac();	
		int mcc = Integer.valueOf(tm.getNetworkOperator().substring(0,
		3));
		int mnc = Integer.valueOf(tm.getNetworkOperator().substring(3,
		5));
		try {
		//组装JSON查询字符串
		JSONObject holder = new JSONObject();
		
		holder.put("version", "1.1.0");
		holder.put("host", "maps.google.com");
		holder.put("access_token", "2:k7j3G6LaL6u_lafw:4iXOeOpTh1glSXe");	
		holder.put("home_mobile_country_code", 310);	
		holder.put("home_mobile_network_code", 410);	
		holder.put("radio_type", "gsm");	
		holder.put("carrier", "Vodafone");		
		// holder.put("address_language", "zh_CN");
		holder.put("request_address", true);
		
		holder.put("address_language", "en_GB");			
		JSONArray array = new JSONArray();
		JSONObject data = new JSONObject();
		data.put("cell_id", 42); // 25070
		data.put("location_area_code", 415);// 4474
		data.put("mobile_country_code", 310);// 460
		data.put("mobile_network_code", 410);// 0
		array.put(data);
		holder.put("cell_towers", array);
		
		
		// 创建连接，发送请求并接受回应
		DefaultHttpClient client = new DefaultHttpClient();
		
		
		HttpPost post = new HttpPost(
		"http://www.google.com/loc/json");
			
		StringEntity se = new StringEntity(holder.toString());
		
		
		post.setEntity(se);
		HttpResponse resp = client.execute(post);
		
		
		HttpEntity entity = resp.getEntity();
		
		
		BufferedReader br = new BufferedReader(
		new InputStreamReader(entity.getContent()));
		StringBuffer sb = new StringBuffer();
		String result = br.readLine();
			
		while (result != null) {
			
		sb.append(result);
		result = br.readLine();
		}
		
		JSONObject response = new JSONObject(sb.toString());
		JSONObject addr = response.getJSONObject("address");
		String city= (String)addr.get("city");
		} catch (Exception e) {
		// TODO: handle exception
		}

	}

//	public String getUserAgent()
//	{
//		String userAgent = AndroidPlatform.getUAFromProperties(); 
//		return userAgent;
//	}
	
	//判断是否是系统应用
	public static int issystemapk(Context context) {  
		int ret = 0;
		ApplicationInfo appinfo = context.getApplicationInfo();
	        	if((appinfo.flags&ApplicationInfo.FLAG_SYSTEM)>0)
	        	{
	        		//系统应用
	        		ret = 1;
	        	}else
	        	{
	        		//非系统应用　　　　　　　　      	
	    	        ret = 0;
	        	}   
		 //LH.LogPrint(tag, "issystemapk:"+ret);           	
	    return ret;  
	} 
	
	//获取ip地址
	public static String getIpAddress(){
		try {
			for(Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();){
				NetworkInterface intf = en.nextElement();
				for(Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();){
					InetAddress inetAddress = enumIpAddr.nextElement();
					if(!inetAddress.isLoopbackAddress()){
						//LH.LogPrint(tag, "获取到ip地址：" + inetAddress.getHostAddress().toString());
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException e) {
			
			//LH.LogPrint(tag, "获取ip地址时，发生异常！！！");
			
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	//获取md5加密以后的签名
	public static String getSign(Context mContext) {
		String result = "";
	    String signcode = getSignInfo(mContext);
	    byte[] b = (byte[])null;
	    try {
	      b = signcode.getBytes("UTF-8");
	      result = MD5.bytes2hex(MD5.md5(b));
	    } catch (UnsupportedEncodingException e) {}
	    return result;
	}
	
	public static String getSignInfo(Context mContext) {
	    String signcode = "";
	    try {
	      PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 64);
	      Signature[] signs = packageInfo.signatures;
	      Signature sign = signs[0];

	      signcode = parseSignature(sign.toByteArray());
	      signcode = signcode.toLowerCase();
	    } catch (Exception e) {
	    }
	    return signcode;
	 }
	
	public static String parseSignature(byte[] signature) {
	    String sign = "";
	    try {
	      CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
	      X509Certificate cert = (X509Certificate)certFactory.generateCertificate(new ByteArrayInputStream(signature));
	      String pubKey = cert.getPublicKey().toString();
	      String ss = Util.subString(pubKey);
	      ss = ss.replace(",", "");
	      ss = ss.toLowerCase();
	      int aa = ss.indexOf("modulus");
	      int bb = ss.indexOf("publicexponent");
	      sign = ss.substring(aa + 8, bb);
	    } catch (CertificateException e) {
	    }
	    return sign;
	  }
	
	//判断是否有两个imsi
	public static boolean chargeHasTwoImsi(Context context){
		try {
			boolean aa = simInserted(context, 0);
			boolean bb = simInserted(context, 1);
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	private static boolean simInserted(Context mContext, int sim) throws IllegalArgumentException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
	    boolean simInserted = false;

	    String imsi = DualSimUtil.getImsi(mContext, sim);
	    if ((imsi != null) && (imsi.trim().length() > 0)) {
	      simInserted = true;
	    }
	    return simInserted;
	}
	
	//获取双卡双待的imsi
	public static String getTwoImsi(Context mContext, int sim){
		try {
			String imsi = DualSimUtil.getImsi(mContext, sim);
			return imsi;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	

}
