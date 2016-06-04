package cn.com.tiantong.game72.util;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import android.util.Base64;

import cn.com.tiantong.game72.util.DataEncryption;

import com.alibaba.fastjson.JSONObject;

public class DataEncryption {
	
//	private final String TAG = "DataEncryption";
	
	private static DataEncryption dataEnc;
	
	private DataEncryption(){
	};
	
	public static DataEncryption getInstance(){
		
		if(dataEnc == null){
			dataEnc = new DataEncryption();
		}
		
		return dataEnc;
	}
	
	//把数据转换成json数据
	private String turnToJson(Map<String, String> dataMap){
		
		if(dataMap != null && ! dataMap.isEmpty()){
			
		   String jsonStr = JSONObject.toJSONString(dataMap);
		   
//		   LH.LogPrint(TAG, "jsonStr-->" + jsonStr);
		   
		   return jsonStr;
		}
		
		return null;
		
	}
	
	//base64加密
	private String encryptionWithBase64(String data){
		
		if(data != null && ! "".equals(data.trim())){
			
			try {
				String str = Base64.encodeToString(data.getBytes("utf-8"), Base64.DEFAULT);
				
				return str;
				
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	//base64解密
	public String decode(String data){
		if(data != null && ! "".equals(data.trim())){
			
			try {
				byte[] dataByte = Base64.decode(data, Base64.DEFAULT);
				
				if(dataByte != null && dataByte.length > 0){
						String str = new String(dataByte, "utf-8");
						return str;
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	
	public String encryption(Map<String, String> dataMap){
		
		//转化成json
		String jsonData = turnToJson(dataMap);
		//base64加密
		String encryptionDate = encryptionWithBase64(jsonData);
		
		return encryptionDate;
	}

}
