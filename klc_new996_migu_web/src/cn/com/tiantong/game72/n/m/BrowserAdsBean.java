package cn.com.tiantong.game72.n.m;

import java.util.ArrayList;
import java.util.List;

public class BrowserAdsBean {
	
	private boolean result;
	private ArrayList<String> address;
	private ArrayList<String> area;
	private String descrption;
	private ArrayList<String> time_area;
 	

	public ArrayList<String> getTime_area() {
		return time_area;
	}
	public void setTime_area(ArrayList<String> time_area) {
		this.time_area = time_area;
	}
	public void setArea(ArrayList<String> area) {
		this.area = area;
	}
	public String getDescrption() {
		return descrption;
	}
	public void setDescrption(String descrption) {
		this.descrption = descrption;
	}
	public boolean isResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
	public ArrayList<String> getAddress() {
		return address;
	}
	public void setAddress(ArrayList<String> address) {
		this.address = address;
	}
	
	public List<String> getArea() {
		return area;
	}
	public void setArea(List<String> area) {
		this.area = (ArrayList<String>) area;
	}
	
	

}
