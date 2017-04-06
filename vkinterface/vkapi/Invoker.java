package vkinterface.vkapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Invoker {
	/* 
	 * Many thanks to GitHub users
	 * @liosha2007
	 * @aNNiMON
	 */
	
	private String token;
	//Maybe we will need that also?..
	///private String client_secret;
	///private String client_id;


	public Invoker(String token){///, String client_secret, String client_id){		
		this.token = token;
		///this.client_secret = client_secret;
		///this.client_id = client_id;
	}

	private static final String METHOD_HOST = "https://api.vk.com/method/";
	
	private static String sendRequest(String urlstr) throws IOException{
		final StringBuilder result = new StringBuilder();
        URL url = new URL(urlstr);
        
        try (InputStream is = url.openStream()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            reader.lines().forEach(result::append);
        }

        return result.toString();
	}
	
	public static String invokeMethod(String methodName, List<Param> params, String token) throws IOException{
		params.add(new Param("access_token", token));
		params.add(new Param("v", "5.9"));
		
        return sendRequest(new RequestBuilder(METHOD_HOST+methodName, params).toString());
	}	

	public static String checkToken(String token, String client_secret, String client_id) throws IOException {
		ArrayList<Param> params = new ArrayList<Param>();
		params.add(new Param("token", token));
		params.add(new Param("client_secret", client_secret));
		params.add(new Param("client_id", client_id));
		
        return sendRequest(new RequestBuilder(METHOD_HOST+"secure.checkToken", params).toString());
	}

	public String getChatInfo(int chatId) throws IOException {
		ArrayList<Param> params = new ArrayList<Param>();
		params.add(new Param("chat_id", String.valueOf(chatId)));
		
		return invokeMethod("messages.getChat", params, token);
	}
	
	public String setChatTitle(int chatId, String name) throws IOException{
		ArrayList<Param> params = new ArrayList<Param>();
		params.add(new Param("chat_id", String.valueOf(chatId)));
		params.add(new Param("title", name));
		
		return invokeMethod("messages.editChat", params, token);
	}

	public String sendMessage(int chatId, String msg) throws IOException{
		ArrayList<Param> params = new ArrayList<Param>();
		params.add(new Param("chat_id", String.valueOf(chatId)));
		params.add(new Param("message", msg));

		return invokeMethod("messages.send", params, token);
	}
	
	public String getMessagesByTimeOffset(int timeOffset) throws IOException{
		ArrayList<Param> params = new ArrayList<Param>();
		params.add(new Param("time_offset",String.valueOf(timeOffset)));
		
		return invokeMethod("messages.get", params, token);
	}

	//TODO make boolean
	public void markAsRead(int[] msgids) throws IOException{
		StringBuilder msgIdsBuilder = new StringBuilder();

		if(msgids.length>0) {
			for (int msgid : msgids) {
				msgIdsBuilder.append(msgid).append(",");
			}
			msgIdsBuilder.deleteCharAt(msgIdsBuilder.length() - 1); //for last ","
		}

		ArrayList<Param> params = new ArrayList<Param>();
		params.add(new Param("message_ids", msgIdsBuilder.toString()));

		invokeMethod("messages.markAsRead", params, token);
	}
}
