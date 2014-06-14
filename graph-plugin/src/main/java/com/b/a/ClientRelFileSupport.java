package com.b.a;

public interface ClientRelFileSupport {
	
	public void setFile(String strUrl);
	
	public void mergeRel(String strUrl);
	
	public String getRel();
	
	public void mergeRelString(String strRel);
	
	public void putRelString(String strRel);
	
	public void reloadFile();
	
	public String getFile() ;
}
