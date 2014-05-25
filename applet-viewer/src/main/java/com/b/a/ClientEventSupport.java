package com.b.a;

public interface ClientEventSupport {
		
	public void disableEvent(int eventId);
	
	/**
	 * ��������¼��ض��¼�
	 * @param eventId
	 */
	public void enableEvent(int eventId);
	
	/**
	 * ��ȡ�¼������¼���û�з���null
	 * @return
	 */
	public String getEvent();
	
	public void putEvent(int eventId, String eventParam);
	
	public void setRaiseEvents(boolean raiseFlag);
	
	public boolean getRaiseEvents();
}
