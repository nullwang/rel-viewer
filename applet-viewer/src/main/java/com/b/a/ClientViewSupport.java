package com.b.a;

public interface ClientViewSupport {

	public void focusChartOnEnd(String entId);
	
	public boolean getAnimateLayout();
	
	public void setAnimateLayout(boolean bln);
	
	public String getLayoutManager();
	
	public void invertSelection();
	
	public void reorganize();
	
	public void reorganizeEx(String strLayout);
	
	public void reorganizeSelection();
	
	public void scrollCenter();
	
	public void setBackgroundColor(String color);
	
	public void setBackgroundImage(String strUrl, boolean stretch);
	
	public void setTargetSeparation(float separation);
	
	public void setZoom(float f);
	
	public void setConnectivityEmphasis(String strEntityTypeList);
	
	public void setPropertyEmphasis(String strEntityType,
			String strPropertyType,boolean blnHighest);
	
	void setEmphasisOn(boolean blnEmphasis);
	
	void setEndEmphasis(String strEntityList, boolean blnEmphasis);
	
	void clearEndEmphasis();
	
	void setMaxEmphasised(int iEntities);
	
	void showWaiting(boolean blnWaiting);
	
	void zoomFit();
	
	void zoomOne();
	
	void setFadedComposite(float f);
	
	void setTimeBar(boolean bln);
	
	void setMode(String mode);
	
}
