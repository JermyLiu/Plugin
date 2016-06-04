package cn.com.tiantong.game72.n.m;



import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import cn.com.tiantong.game72.n.m.EM;
import cn.com.tiantong.game72.n.m.RO;
import cn.com.tiantong.game72.n.m.YDII;




public class CP {

//	public static final String tag = "CMOMPParse";
	
	private RO ro = new RO();
	
	
	//轻量级初始化
	public Object parseInitLightCmOmp(Hashtable<String, String> table){
		
		YDII info = new YDII();
		
		try {
			if(table != null && ! table.isEmpty()){
				
				if(table.containsKey("code")){
					
					String yidongCode = table.get("code");
					
					if(yidongCode != null && ! "".equals(yidongCode.trim())){
						
						yidongCode = yidongCode.trim();
						
						if("0".equals(yidongCode)){
							//LH.LogPrint("CMOMPParse", "(CMOMPParse)移动端初始化接口成功！！！");
							// 成功
							ro.result = true;
						    
						    info.setResCode(0);
						    info.setResInfo("初始化成功");
						    
						    ro.obj = info;
						    
						}else if("15".equals(yidongCode)){
							//LH.LogPrint("CMOMPParse", "移动端初始化接口失败，错误码为：" + table.get("code"));
							ro.result = false;
							String detail = table.get("detail");
							info.setResCode(Integer.parseInt(yidongCode));
							info.setResInfo(table.get("desc") + "--" + detail);
							
							ro.obj = info;
						}else{
							//LH.LogPrint("CMOMPParse", "移动端初始化接口失败，错误码为：" + table.get("code"));
							ro.result = false;
							
							info.setResCode(Integer.parseInt(yidongCode));
							info.setResInfo(table.get("desc"));
							
							ro.obj = info;
						}
						
					}else{
						//LH.LogPrint("CMOMPParse", "移动端初始化接口失败，错误码为：" + table.get("code"));
						ro.result = false;
						
						info.setResCode(10003);
						info.setResInfo("code is null");
						
						ro.obj = info;
					}
					
					
				}else{
					ro.result = false;
					
					info.setResCode(10004);
					
					Set<String> keySet = table.keySet();
					Iterator<String> iterator = keySet.iterator();
					StringBuilder sb = new StringBuilder();
					while(iterator.hasNext()){
						String key = iterator.next();
						String value = table.get(key);
						sb.append("key:" + key + "-value:" + value + ";");
					}
					
					info.setResInfo((sb.length() == 0 ? "null" : sb.toString()));
					
					ro.obj = info;
				}
				
			}else{
				
				ro.result = false;
				
				info.setResCode(10005);
				info.setResInfo("yi dong return result is null");
				
				ro.obj = info;
				
			}
			
			
			
			//单卡
//			if(table.containsKey("code")){
//				
//				if("0".equals(table.get("code"))){
//					//LH.LogPrint("CMOMPParse", "(CMOMPParse)移动端初始化接口成功！！！");
//					// 成功
//					ro.result = true;
//				    
//				    info.setResCode(0);
//				    info.setResInfo("初始化成功");
//				    
//				    ro.obj = info;
//				    
//				}else{
//					//LH.LogPrint("CMOMPParse", "移动端初始化接口失败，错误码为：" + table.get("code"));
//					ro.result = false;
//					
//					info.setResCode(Integer.parseInt(table.get("code")));
//					info.setResInfo(table.get("desc"));
//					
//					ro.obj = info;
//				}
//				
//			}else{
//				//双卡
//				String key = table.get("detail");
//				String code1 = table.get("n1");
//				String code2 = table.get("n2");
//				
//				if((code1 != null && "0".equals(code1.trim())) || (code2 != null && "0".equals(code2.trim()))){
//					ro.result = true;
//					
//					info.setResCode(0);
//					info.setResInfo("key:" + (key == null ? "-null-" : key) + ", ualue:" + code1 + "_" + code2);
//					
//					ro.obj = info;
//				}else{
//					ro.result = false;
//					//表示双卡都失败
//					info.setResCode(100002);
//					info.setResInfo("key:" + (key == null ? "-null-" : key) + ", ualue:" + (code1 == null ? "null" : code1) + "_" + (code2 == null ? "null" : code2));
//					
//					ro.obj = info;
//				}
//			}
			
		} catch (Exception e) {
			//LH.LogPrint(tag, "(CMOMPParse)移动端初始化--因为异常失败");
			e.printStackTrace();
        	ro.result = false;
			String err = EM.getErrorInfo(EM.DATAPARSE_FAIL_ERROR);
			info.setResCode(100000);
			if(table != null && table.containsKey("code")){
				info.setResInfo(table.containsKey("code") + "____" + err);
			}else{
				info.setResInfo(err);
			}
			
			ro.obj = info;
		}
		
		return ro;
		
	}
	
	//轻量级初始化是否成功
	public Object parseLightInitIsSuccessful(boolean f){
		if(f){
			//LH.LogPrint("CMOMPParse", "(CMOMPParse)移动端初始化<是否>接口成功，并且移动端已经初始化！！！");
			// 成功
			ro.result = true;
	        ro.obj = "success";	
		}else{
			//LH.LogPrint("CMOMPParse", "(CMOMPParse)移动端初始化《是否》接口失败，移动端只有true/false，没有错误说明……");
			ro.result = false;
			ro.obj = EM.getErrorInfo(EM.DATAPARSE_FAIL_ERROR);
		}
		
		return ro;
	}

}
