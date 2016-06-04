package cn.com.tiantong.game72.util;



import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.com.tiantong.game72.PlayService;




import android.content.Context;
import android.content.SharedPreferences;

public class Util {
	
//	private static final String tag = "UTIL";
	
	//判断imsi是否为中国移动的
	public static boolean chargeIMSIIsCOMM(String imsi){
		
		if(imsi != null && ! "".equals(imsi.trim()) && imsi.length() >= 5){
			String sub = imsi.trim().substring(3, 5);
			if(("00".equals(sub)) || ("02".equals(sub)) || ("07".equals(sub))){
				return true;
			}
		}
		
		return false;
		
	}
	
	
	//处理逗号问题
	public static String getKeyWords(String str){
		
		if(str != null && ! "".equals(str.trim())){
			
			str = str.trim();
				
			StringBuffer strBuffer = new StringBuffer();
			
			String[] english = str.split(",");
			
			for(int i = 0; i < english.length; i++){
				
				String englishKey = english[i].trim();
				
				if(englishKey != null && ! "".equals(englishKey)){
					
					if(englishKey.contains("，")){
						
						String[] chinese = englishKey.split("，");
						
						for(int j = 0; j < chinese.length; j++){
							
							String chineseKey = chinese[j].trim();
							
							if(chineseKey != null && ! "".equals(chineseKey)){
								strBuffer.append(chineseKey).append(",");
							}
						}
						
					}else{
						strBuffer.append(englishKey).append(",");
					}
					
				}
				
			}
			
			String needStr = strBuffer.toString();
			
			String returnStr = needStr.substring(0, needStr.length() - 1);
			
			//LH.LogPrint(tag, "合并后的关键字是：" + returnStr);
			
			return returnStr;
			
		}
		
		return null;
		
	}
	
	public static String subString(String sub) {
	    Pattern pp = Pattern.compile("\\s*|\t|\r|\n");
	    Matcher mm = pp.matcher(sub);
	    return mm.replaceAll("");
    }

	//保存随机数
	public static void saveSalt(Context mContext, String salt){
		
		if(salt != null && ! "".equals(salt)){
			
			try {
				SharedPreferences initServicePreferences = mContext.getSharedPreferences(PlayService.PREFS_NAME, 0);
				SharedPreferences.Editor initServiceEditor = initServicePreferences.edit();
				
				initServiceEditor.putString(PlayService.SALTFROMSERVICE, salt);
				initServiceEditor.commit();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	//保存随机数
	public static String getSalt(Context mContext){
		
		try {
			SharedPreferences initServicePreferences = mContext.getSharedPreferences(PlayService.PREFS_NAME, 0);
			String salt = initServicePreferences.getString(PlayService.SALTFROMSERVICE, null);
			return salt;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
	
}
