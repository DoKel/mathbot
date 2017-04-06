package vkinterface.utils;

public class Cooldown {

	private long lastCalledTime;
	private int cdTime;
	
	public Cooldown(int cdTime, boolean canRunNow){
		this.cdTime = cdTime;
		this.lastCalledTime = canRunNow? 0 : System.currentTimeMillis();
	}
	
	public Cooldown(int cdTime) {
		this(cdTime, true);
	}

	public boolean canAct() {
		if(System.currentTimeMillis() - this.lastCalledTime >= this.cdTime){
			this.lastCalledTime = System.currentTimeMillis();
			return true;
		}
			
		return false;
	}

}
