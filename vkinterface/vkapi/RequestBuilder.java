package vkinterface.vkapi;

import java.util.ArrayList;
import java.util.List;

public class RequestBuilder {

	StringBuilder sb;
	boolean needBuild;
	
	String host;
	List<Param> parameters;
	
	public RequestBuilder(String host, List<Param> parameters){
		setHost(host);
		this.parameters = (parameters==null? new ArrayList<Param>() : parameters );
		this.needBuild = true;
	}
	
	private void setHost(String host) {
		if(host==null){
			this.host="";
			return;
		}
		

		this.host = host;
	}

	public String toString(){
		if(needBuild){
			this.build();
		}
		
		return sb.toString();
	}



	private void build() {		
		sb = new StringBuilder();
		
		sb.append(host);
		sb.append("?");
		
		for(Param param : parameters){
			sb.append(param.toString()).append("&");
		}
		
		sb.deleteCharAt(sb.length()-1); //For last "&"
		
		needBuild = false;
	}

}
