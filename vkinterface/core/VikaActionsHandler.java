package vkinterface.core;

import vkinterface.utils.JsonMap;
import vkinterface.vkapi.Invoker;

import java.io.IOException;

public class VikaActionsHandler {	
	private Invoker vkapiInvoker;
	
	public VikaActionsHandler(Invoker vkapiInvoker){
		this.vkapiInvoker = vkapiInvoker;
	}
	
	public String getChatName(int chatId) throws Exception{		
		String apiAnswer = vkapiInvoker.getChatInfo(chatId);
		JsonMap jsonAnswer = new JsonMap(apiAnswer);
		if(jsonAnswer.contains("response")){
			//TODO can be null
			return new JsonMap(jsonAnswer.getByKey("response")).getByKey("title");
		}else{
			//TODO some more info?..
			throw new Exception("API error: \n" + apiAnswer);
		}
	}
	
	public boolean setChatTitle(int chatId, String name) throws Exception {
		String apiAnswer = vkapiInvoker.setChatTitle(chatId, name);
		return apiAnswer.equals("response: 1"); //TODO Work with JSON, not strings
	}

	public boolean sendMessage(int chatId, String msg) throws Exception {
		String apiAnswer = vkapiInvoker.sendMessage(chatId, msg);
		System.out.println("Message sent. Response is:\n"+apiAnswer);///FIXME XXX
		return apiAnswer.equals("response: 1"); //TODO Work with JSON, not strings
	}

	public JsonMap[] getMessagesArrayByTimeOffset(int timeOffset) throws IOException, Exception{
		String apiAnswer = vkapiInvoker.getMessagesByTimeOffset(timeOffset);
		
		JsonMap jsonAnswer = new JsonMap(apiAnswer);
		
		//TODO not null safe
		JsonMap jsonAnswerBody = new JsonMap(jsonAnswer.getByKey("response"));

		return JsonMap.parseObjectsArray(jsonAnswerBody.getByKey("items"));
	}



	public void markMsgsAsReaden(JsonMap[] msgs){
		int[] msgsIds = new int[msgs.length];


		//TODO not null safe
		int i=0;
		for(JsonMap jm: msgs) {
			msgsIds[i++] = Integer.valueOf(jm.getByKey("id"));
		}

		try {
			vkapiInvoker.markAsRead(msgsIds);
		} catch (IOException e) {
			//TODO do smth
			e.printStackTrace();
		}
	}
}
