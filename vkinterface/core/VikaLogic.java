package vkinterface.core;

import eltech.bignumbers.util.BigMathParser;
import vkinterface.utils.Cooldown;
import vkinterface.utils.JsonMap;

public class VikaLogic implements Runnable{

	public enum ThreadStatus{
		RUNNING, BROKEN_TOKEN, EXITED;
	}

	private VikaActionsHandler handler;
	
	private Cooldown messageGetCD;	
	private int lastCheckTime;
	
	private volatile ThreadStatus status;

	
	public VikaLogic(VikaActionsHandler handler) {
		this.handler = handler;

		messageGetCD = new Cooldown(1*30*1000, false); //Important to have "false" here, overwise we can read same msgs not once
	}

	@Override
	public void run() {
		status = ThreadStatus.RUNNING;
		lastCheckTime = (int) System.currentTimeMillis()/1000;
		
		/*{
		///========== MESSAGE HANDLING EXAMPLE
		 JsonMap[] js;
		try {
			js = handler.getMessagesArrayByTimeOffset(5*60*60);
			
			for(JsonMap j: js){
				System.out.println(j.getByKey("body"));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		///============================
		}*/
		
		while (this.status == ThreadStatus.RUNNING){
			if(messageGetCD.canAct()){
				JsonMap[] js;
				try {
					js = handler.getMessagesArrayByTimeOffset(
							(int)System.currentTimeMillis()/1000-lastCheckTime);
					handler.markMsgsAsReaden(js);
					for(JsonMap j: js){
						System.out.println(j.getByKey("user_id")+":\t"+j.getByKey("body"));
						String msgBody = j.getByKey("body").toLowerCase();
							if(msgBody.startsWith("vika.")){
								String[] command = msgBody.split("\\\\\\\n");
								String responce = BigMathParser.doCommand(command);
								handler.sendMessage(Integer.parseInt(j.getByKey("chat_id")), responce);
							}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				lastCheckTime = (int) System.currentTimeMillis()/1000;
			}
		
		}
	}

	public void terminate(){
		this.status = ThreadStatus.EXITED;
	}

	public ThreadStatus getStatus() {
		return this.status;
	}
}
