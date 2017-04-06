package vkinterface.vkapi;

import java.util.ArrayList;
import java.util.List;

public class PermissionBuilder {
	
	public enum Permission{
		//TODO move to separated class
		//TODO add more possible options
		MESSAGES("messages"),
		OFFLINE("offline");
		
		private String name;
		Permission (String name){
			this.name = name;
		}
		public String toString(){
			return this.name;
		}
	}
	
	StringBuilder sb;
	boolean needBuild;

	List<Permission> permissions;
	
	public PermissionBuilder(List<Permission> list){
		this.permissions = (list==null? new ArrayList<Permission>() : list);
		this.needBuild = true;
	}

	public String toString(){
		if(needBuild){
			this.build();
		}
		
		return sb.toString();
	}

	private void build() {	
		sb = new StringBuilder();
		
		for(Permission prmsn: permissions){
			sb.append(prmsn.toString()).append(",");
		}
		
		//TODO check if string is empty
		sb.deleteCharAt(sb.length()-1); //For last ","
		
		needBuild = false;
	}

}