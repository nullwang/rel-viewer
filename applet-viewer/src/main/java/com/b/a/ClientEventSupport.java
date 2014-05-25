package com.b.a;

public interface ClientEventSupport {
		
	public void disableEvent(int eventId);
	
	/**
	 * 激活产生事件特定事件
	 * @param eventId
	 */
	public void enableEvent(int eventId);
	
	/**
	 * 获取事件队列事件，没有返回null
	 * @return
	 */
	public String getEvent();
	
	public void putEvent(int eventId, String eventParam);
	
	public void setRaiseEvents(boolean raiseFlag);
	
	public boolean getRaiseEvents();
}
