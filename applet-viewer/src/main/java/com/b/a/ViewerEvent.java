package com.b.a;

public class ViewerEvent  {	
	/**
	 */
	public static int EVENT_MOUSEIN = 0;

	/**
	 */
	public static int EVENT_MOUSEOUT = 1;
	
	public static int EVENT_SELECTIONCHANGED = 2;
	
	public static int EVENT_CLICK=3;
	
	public static int EVENT_DOUBLECLICK=4;
	
	public static int EVENT_CONTEXTMENU=5;
	
	public static int EVENT_MENU_COMMAND=6;
	
	public static int EVENT_FILEDONE = 7;
	
	public static int EVENT_LAYOUTDONE=8;
	
	public static int EVENT_ERROR=99;
	
	private int eventId;
	private String eventParam;
		
	public  ViewerEvent( int eventId, String eventParam) {
		this.eventId = eventId;
		this.eventParam = eventParam;
	}
	
	public ViewerEvent(int eventId){
		this.eventId = eventId;
	}
	
	public int getEventId() {
		return eventId;
	}
	
	public void setEventId(int eventId) {
		this.eventId = eventId;
	}

	public String getEventParam() {
		return eventParam;
	}

	public void setEventParam(String eventParam) {
		this.eventParam = eventParam;
	}
	
	@Override
	public String toString()
	{
		return String.valueOf(eventId) + ":" + ( (eventParam == null )? "" : eventParam) ;
	}
	
}
