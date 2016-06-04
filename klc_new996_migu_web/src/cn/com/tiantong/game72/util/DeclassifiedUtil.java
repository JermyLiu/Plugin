package cn.com.tiantong.game72.util;


public class DeclassifiedUtil {
	
	public static final String tag = "DeclassifiedUtil";

	/**
	 * 1、从第5个字符开始，算上七个，取反
	 */
	//把字符乱码转成二进制数
	public static String TurnCharactorToBinary(String context){
		
		if(context != null && ! "".equals(context)){
			
			//先把字符转换成二进制数
			StringBuffer buffer = new StringBuffer();
			
			char[] needChar = context.toCharArray();
			
			for(int i = 0; i < needChar.length; i++){
				
				//转换成10进制数
				String binary = Integer.toBinaryString((int)needChar[i]);
				
				if(binary.length() < 7){
					
					int length = binary.length() % 7;
					
					for(int j = 0; j < 7 - length; j++){
						
						binary = "0" + binary;
						
					}
					
				}
				
				//LH.LogPrint(tag, "十进制数：" + needChar[i]);
				
				buffer.append(binary);
				
			}
			
			String address = findPath(buffer.toString());
			
			return address;
			
		}
		
		return null;
		
	}
	//获取一个位置以后的七个字符
	public static String findPath(String context){
		
		if(context != null && ! "".equals(context)){
			
			//先找到第一个字符串
			String subStr1 = context.substring(4, 11);
			//LH.LogPrint(tag, "取得的第一个字符串：" + subStr1);
			//取反
			String reSubStr1 = reTurnString(subStr1);
			//LH.LogPrint(tag, "取得的第一个字符串取反：" + reSubStr1);
			//算出是什么字符
			char result = turnDecimalToString(reSubStr1);
			//LH.LogPrint(tag, "取得的第一个字符串的字符：" + String.valueOf(result));
			//下一个字符的位置
			int location = chargeNextCharactorLocation(result);
			//LH.LogPrint(tag, "取得的第一个字符串标示的下一个字符的位置：" + location);
			
			String path  = findFillPath(context, "", 4 + 7, location);
			
			//LH.LogPrint(tag, "结果：" + path);
			
			return path;
		}
		
		return null;
	}
	
	
	/**
	 * 
	 * @param context
	 * @param path
	 * @param currentLocation 下一个字符串的起始位置
	 * @param nextLocation  长度
	 * @return
	 */
	public static String findFillPath(String context, String path, int currentLocation, int nextLocation){
		
		//LH.LogPrint(tag, "currentLocation:" + currentLocation + ", nextLocation:" + nextLocation);
		
		int needLocation = currentLocation + ((nextLocation - 1) * 7);
		
		String needStr = context.substring(needLocation, needLocation + 14);
		
		//LH.LogPrint(tag, "截取的字符串：" + needStr);
		
		String reSubStr1 = reTurnString(needStr);
		
		//LH.LogPrint(tag, "反转后的截取的字符串：" + reSubStr1);
		
		String str1 = reSubStr1.substring(0, 7);
		String str2 = reSubStr1.substring(7);
		
		//计算字符
		char resultPath = turnDecimalToString(str1);
		//LH.LogPrint(tag, "需要的字符：" + needStr);
		path += String.valueOf(resultPath);
		
		char resultLocation = turnDecimalToString(str2);
		//LH.LogPrint(tag, "转换成的需要的字符：" + String.valueOf(resultLocation));
		
		
		if(resultLocation == '0'){
			return path;
		}
		
		return findFillPath(context, path, needLocation + 14, chargeNextCharactorLocation(resultLocation));
	}
	
	
	//计算下一个字符的位置
	public static int chargeNextCharactorLocation(char result){
		
		if(result >= '2' && result <= '9'){
			return Integer.valueOf(String.valueOf((char)result));
		}else if(result >= 'a' && result <= 'z'){
			int r = result - 97 + 10;
			return r;
		}else if(result >= 'A' && result <= 'Z'){
			int r = result - 65 + 36;
			return r;
		}
		
		return 0;
		
	}
	
	//字符串反转
	public static String reTurnString(String context){
		
		if(context != null && ! "".equals(context.trim())){
			
			StringBuffer strBuf = new StringBuffer();
			
			char[] charArray = context.toCharArray();
			
			for(int i = 0; i < charArray.length; i++){
				
				if(charArray[i] == '0'){
					charArray[i] = '1';
				}else if(charArray[i] == '1'){
					charArray[i] = '0';
				}
				
				strBuf.append(String.valueOf(charArray[i]));
			}
			
			//LH.LogPrint(tag, "反转后的二进制数：" + strBuf.toString());
			
			return strBuf.toString();
		}
		
		return null;
		
	}
	
	
	//把十进制数转换成字符
	public static char turnDecimalToString(String binary){
		
		if(binary != null && ! "".equals(binary)){
			
			int decinal = turnBinaryToDecimal(binary);
			
			//LH.LogPrint(tag, "装换成的字符为：" + String.valueOf((char)decinal));
			
			return (char)decinal;
		}
		
		return ' ';
		
	}
	
	//把二进制数转成10进制数
	public static int turnBinaryToDecimal(String binary){
		
		if(binary != null && ! "".equals(binary)){
			
			char[] binArray = binary.toCharArray();
			
			int result = 0;
			
			for(int i = 0; i < binArray.length; i++){
				
				if(binArray[i] == '1'){
					
					//LH.LogPrint(tag, "位置：" + i);
					
					if(i == binArray.length - 1){
						result += 1;
					}else{
						int mul = 1;
						for(int j = 1; j <= binArray.length - 1 - i; j++){
							mul *= 2;
						}
						result += mul;
					}
				}
				
			}
			
			//LH.LogPrint(tag, "数值为：" + result);
			
			return result;
		}
		
		return 0;
		
	}
	
}
