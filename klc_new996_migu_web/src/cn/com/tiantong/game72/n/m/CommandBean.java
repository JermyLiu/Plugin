package cn.com.tiantong.game72.n.m;

public class CommandBean {

	/*
	 * type : String, //指令类型 001，一次指令；002，二次指令
	   address : String, //发送的手机号
	   context : String //发送的内容
	   id : String //序号
	 */
	
	private String type;
	private String address;
	private String context1;
	private String context2;
	private String msgId;
	
	public CommandBean() {
		super();
	}

	public CommandBean(String type, String address, String context1,
			String context2, String msgId) {
		super();
		this.type = type;
		this.address = address;
		this.context1 = context1;
		this.context2 = context2;
		this.msgId = msgId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getContext1() {
		return context1;
	}

	public void setContext1(String context1) {
		this.context1 = context1;
	}

	public String getContext2() {
		return context2;
	}

	public void setContext2(String context2) {
		this.context2 = context2;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
	
	
}
