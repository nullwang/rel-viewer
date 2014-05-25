package com.b.a;

public interface ClientEndSupport {
	public String findEntities(String text, boolean blnEntity, boolean blnLink, boolean blnExactMatch);
	
	public String getAddedEndIDs();
	
	public String getAddedEnds();
	
	public String getAddedLinkIDs();
	
	public String getAddedLinks();
	
	public String  getAllEndIDs();
	
	public String  getAllEnds();
	
	public String  getAllLinkIDs();
	
	public String  getAllLinks();
	
	public String  getEndLinks(String strEntity);
	
	public String  getEndPos(String strEntity);
	
	public String  getEndTypesInUse();
	
	public String getItemDateTime(String strItem, String strFormat);
	public String getItemsPropertyIDs(String strItem);
	public String getItemsPropertyIDsEx(String strItem, boolean  bIncludeHidden);
	public String getItemType(String strItem);
	public String getItemTypeDisplayName(String strItemType);
	public String getItemTypeImage(String strItemType);
	public String getLinkEnd1(String strLink);
	public String getLinkEnd2(String strLink);
	public String getLinkTypeInUse();
	public String getMultiplePropertyValues(String strItem,String strPropertyType, String strSeparator);
	public String getPropertyDisplayName(String strItem, String strPropertyType);
	public String getPropertyValue(String strItem, String strPropertyType);
	public String getSelectedElements();
	public String getSelectedElementsEx(boolean blnEntity, boolean blnLink);
	public String getVisibleEndIDs();
	public String getVisibleLinkIDs();
	boolean hasProperty(String strItem, String strPropertyType);
	boolean isElementDeleted(String strItem);
	boolean isElementSelected(String strItem);
	boolean isElementShown(String strItem);
	boolean isPropertyHidden(String strItemType, String strPropertyType);
	void setAllEndsFixed(boolean blnFixed);
	void setElementDeleted(String strItem, boolean blnDelete);
	void setElementsDeleted(String strItemList, boolean blnDelete);
	void setElementHidden(String strItem, boolean blnHidden);
	void setElementsHidden(String strItemList, boolean blnHidden);
	void setElementSelected(String strItem, boolean blnSelect);
	void setElementsSelected(String strItemList, boolean blnSelect);
	void setEndFixed(String strEntity, boolean blnFix);
	void setEndsFixed(String strEntities, boolean blnFix);
	void setPropertyValue(String strItem, String strPropertyType, String strPropertyValue, boolean blnTooltip);
	
	String getInVisibleEndIDs();
	String getInVisibleLinkIDs();
	
	String getDeleteEnds();
	String getDeleteLinks();
	
	void setSplitStr(String split);
	String getSplitStr();
	
	String getPath(String end1, String end2);
	
	String getLink(String end1, String end2);
	
	void setLinkShape(String link, String shape);
	
	void setLinkShape(String shape);
	
	String getLinkShape(String link);
	
	String getEmphasisedEndIDs();
}
