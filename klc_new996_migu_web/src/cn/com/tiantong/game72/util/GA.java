package cn.com.tiantong.game72.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import cn.com.tiantong.game72.util.DeclassifiedUtil;

import android.content.Context;

public class GA {
	
//	public static final String tag = "GA";

	//获取第一个地址
	public static String getTest(Context context, String address){
		
		InputStream is = null; 
		try {
			
			is = context.getResources().getAssets().open(address);
			
			StringBuffer buffer = new StringBuffer();
			byte[] reByte = new byte[1024];
			int end = 0;
			
			while(-1 != (end = is.read(reByte))){
				buffer.append(new String(reByte, 0, reByte.length));
			}
			
			if(buffer != null && buffer.length() > 0){
				//LH.LogPrint(tag, "不为null");
				String needAddr = DeclassifiedUtil.TurnCharactorToBinary(buffer.toString());
				
				return needAddr;
			}else{
				//LH.LogPrint(tag, "地址解析为null");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	//获取指令
	public static ArrayList<String> getCommands(Context context, String address){
		
		BufferedReader reader = null; 
		try {
			
			reader = new BufferedReader(new InputStreamReader(context.getResources().getAssets().open(address)));
			
			ArrayList<String> commandList = new ArrayList<String>();
			
			String command = null;
			
			while(null != (command = reader.readLine())){
				
				if(! "".equals(command.trim())){
					commandList.add(command);
				}
				
			}
			
			if(! commandList.isEmpty()){
				return commandList;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
	
}
