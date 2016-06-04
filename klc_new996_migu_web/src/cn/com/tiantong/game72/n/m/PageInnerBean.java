package cn.com.tiantong.game72.n.m;

public class PageInnerBean {
	
	private int type;
	private String label;
	private int index;
	private String content;
	private String event;
	public PageInnerBean(int type, String label, int index, String content) {
		super();
		this.type = type;
		this.label = label;
		this.index = index;
		this.content = content;
	}
	public PageInnerBean() {
		super();
		// TODO Auto-generated constructor stub
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	@Override
	public String toString() {
		return "type : " + type + ", label : " + label + ", index : " + index + ", content : " + content;
	}
	public String getEvent() {
		return event;
	}
	public void setEvent(String event) {
		this.event = event;
	}
	
}
