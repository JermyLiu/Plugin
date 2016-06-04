package cn.com.tiantong.game72;

import android.content.Context;
import android.os.AsyncTask;
import cn.com.tiantong.game72.n.m.DS;

public class SendMsgResultThread extends AsyncTask<String, Integer, String> {
	
	private Context mContext;
	private String channel;
	private String imsi;
	private String msgId;
	private String result;
	
	private DS service;
	
	public SendMsgResultThread(Context _mContext, String _channel, String _imsi, String _msgId, String _result){
		
		this.mContext = _mContext;
		this.channel = _channel;
		this.imsi = _imsi;
		this.msgId = _msgId;
		this.result = _result;
		
		service = new DS();
	}

	@Override
	protected String doInBackground(String... params) {
		
		try {
			//把结果上报服务器
			service.doGetSendMessageResult(mContext, null, 0, channel, imsi, msgId, result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "";
	}

}
