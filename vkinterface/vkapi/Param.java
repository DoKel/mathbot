package vkinterface.vkapi;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Param {
	
	private String key;
	private String value;
	
	public Param(){
		this("","");
	}
	
	public Param(String key, String value){
		this.setKey(key);
		this.setValue(value);
	}
	
	public void setKey(String key){
		if(key==null){
			this.key="";
			return;
		}
		

		try {
			this.key = URLEncoder.encode(key, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			this.key = key;
			e.printStackTrace();
		}
	}
	
	public void setValue(String value){
		if(value==null){
			this.value="";
			return;
		}
		

		try {
			this.value = URLEncoder.encode(value, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			this.value = value;
			e.printStackTrace();
		}
	}
	
	public String toString(){
		return String.format("%s=%s", key, value);
	}
}
