package com.b.s;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.b.a.ClientEventSupport;
import com.b.a.ViewerEvent;

public class ClientEventSupportImpl implements ClientEventSupport {
	
	@SuppressWarnings ("unchecked")
	Set eventStatus = new HashSet();
	
	ConcurrentLinkedQueue<ViewerEvent> eventQueue = new ConcurrentLinkedQueue<ViewerEvent>();
	//Queue<ViewerEvent> eventQueue = new LinkedList<ViewerEvent>();
	
	boolean raiseFlag = true;
	
	public void setRaiseEvents(boolean raiseFlag)
	{
		this.raiseFlag = raiseFlag;
	}
	
	public boolean getRaiseEvents()
	{
		return this.raiseFlag;
	}	
	
	@SuppressWarnings ("unchecked")
	public void disableEvent(int eventId) {
		eventStatus.add(eventId);
	}

	public void enableEvent(int eventId) {
		eventStatus.remove(eventId);
	}

	public String getEvent() {
		ViewerEvent event = eventQueue.poll();
		
		if( event != null ) return event.toString();
		else return null;
	}
	
	public void putEvent(int eventId, String eventParam) {
		putEvent( new ViewerEvent(eventId,eventParam));
		
	}

	private void putEvent(ViewerEvent event) {
		if(! raiseFlag) return;
		if( !(eventStatus.contains(event.getEventId())))
		eventQueue.offer(event);
	}
	
	public static void main(String[] args)
	{
		ClientEventSupport eventImpl = new ClientEventSupportImpl();
		eventImpl.setRaiseEvents(false);
		eventImpl.disableEvent(3);
		eventImpl.disableEvent(7);
		
		for(int i=0; i< 10; i++){
			eventImpl.putEvent(i,String.valueOf(i));
		}
		String s = eventImpl.getEvent();
		while(null != s){
			System.out.println(s);
			s = eventImpl.getEvent();
		};	
		
	}
	

}
