package vkinterface.core;

import vkinterface.utils.JsonMap;
import vkinterface.vkapi.*;
import vkinterface.vkapi.PermissionBuilder.Permission;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Boot {

	private enum ExitStatus{
		NOT_YET, OFF, REBOOT;
	}
	
	public static void main(String[] args) {
		while(new Boot().execute() != ExitStatus.OFF){}
	}

	private VikaActionsHandler handler;
	private VikaLogic vikaLogic;
	
	private Boot(){
		Invoker invoker;
		
		invoker = new Invoker(getToken());
		
		vikaLogic = new VikaLogic(new VikaActionsHandler(invoker));
	}
	
	
	private String getToken() {
		ArrayList<Permission> list = new ArrayList<Permission>();

		list.add(PermissionBuilder.Permission.MESSAGES);
		list.add(PermissionBuilder.Permission.OFFLINE);
		
		String token = loadToken();
			try{
				while(!checkToken(token)){
				token = parseTokenFromURL(
							AuthorizerGUI.askToken("5639519" , list)
						);
				}
			} catch (Exception e) {
				System.err.println("Something went wrong with Json parsing, while getting token. ");
				System.exit(0);
			}
				
		return token;
	}

	private boolean checkToken(String token) throws Exception{
		System.out.println(token);
		
		String response;
		try {
			response = Invoker.checkToken(token, "EqlTDkow6UsnhuO3xeQV", "5639519");
		} catch (IOException e) {
			// XXX something wrong 
			e.printStackTrace();
			return false;
		}
		
		return !(new JsonMap(response).contains("error"));
	}
	
	private String parseTokenFromURL(String tokenURL) {
		//FIXME ultimate kostil --- there are no english words for that mess 
		return tokenURL.split("access_token=")[1].split("&")[0];
	}



	private String loadToken() {
		return "099098fb5d4e39813cf2f11682fc32cfc9835cf1c643bba7763af774aebfd2c2cd83d866bb5bf54467f41";
	}



	private ExitStatus execute(){
		ExitStatus exitStatus = ExitStatus.NOT_YET;
		
		Thread vikaLogicThread = new Thread(vikaLogic);
		vikaLogicThread.setName("Vika Logic thread");

		vikaLogicThread.start();
		
		try(Scanner in = new Scanner(System.in)){
			
			String[] command;
			while(exitStatus == ExitStatus.NOT_YET){
				
				if(!vikaLogicThread.isAlive()){
					switch(vikaLogic.getStatus()){
					case BROKEN_TOKEN:
						///XXX reget token
						break;
					case EXITED:
						exitStatus = ExitStatus.OFF;
						break;
					case RUNNING:
						//pass through
					default:
						//XXX Unexpected thread death
						break;
					}
				}
				
				if(in.hasNext()){
					command = in.nextLine().split(" ");
					switch(command[0]){
					case "exit":
						exitStatus = ExitStatus.OFF;
						break;
						//TODO more commands
					}
				}
			}
			vikaLogic.terminate();
		}
		
		return exitStatus;
	}
	
	
}
