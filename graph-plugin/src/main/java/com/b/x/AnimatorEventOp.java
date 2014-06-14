package com.b.x;

import com.b.a.ClientEventSupport;
import com.b.a.ViewerEvent;
import com.b.u.AnimatorOp;

public class AnimatorEventOp implements AnimatorOp{
	
	protected ClientEventSupport eventSupport;
	protected String param;	

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public ClientEventSupport getEnventSupport() {
		return eventSupport;
	}

	public void setEnventSupport(ClientEventSupport eventSupport) {
		this.eventSupport = eventSupport;
	}

	public void finish() {
		eventSupport.putEvent(ViewerEvent.EVENT_LAYOUTDONE, param);		
	}

	public void step() {
		// TODO Auto-generated method stub
		
	}

	public void stop() {
		// TODO Auto-generated method stub
		
	}

}
