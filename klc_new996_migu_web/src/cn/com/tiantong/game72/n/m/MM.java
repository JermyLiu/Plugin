package cn.com.tiantong.game72.n.m;

import android.os.Handler;
import android.os.Message;

/**
 * 消息管理�?
 * @author wujianheng
 *
 */
public class MM {
	private int messageid = 0;
	private Handler handler = null;
	private Object result;

	public MM(Handler handler, int msgid) {
		this.handler = handler;
		this.messageid = msgid;
	}

	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}
	
	public int getMessageID() {
		return messageid;
	}

	public void setMessageID(int msgid) {
		this.messageid = msgid;
	}

	public Object getResult() {
		return result;
	}

	private void setResult(Object result) {
		this.result = result;
	}
	
	public void sendHandlerMessage(Object obj) {
		if (handler != null) {
			Message message = new Message();
			message.what = messageid;
			message.obj = obj;
			handler.sendMessage(message);
		}
		setResult(obj);
	}
}
