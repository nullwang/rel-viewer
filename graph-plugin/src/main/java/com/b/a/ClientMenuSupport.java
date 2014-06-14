package com.b.a;

public interface ClientMenuSupport {
	
	/**
	 * @param menuId
	 * @param menuTxt
	 */
	public void addMenuItem(String menuId, String menuTxt);
	
	/**
	 * ����ݲ˵�
	 */
	public void clearMenuItems();
	
	/**
	 * @param pMenuId
	 * @param menuId
	 * @param menuTxt
	 */
	public void addSubMenuItem(String pMenuId, String menuId, String menuTxt);
	
	
	public void setMenuItemState(String menuId, boolean blnEnable);
	
	/**
	 * ��˵���ӷָ��
	 * @param menuId �˵�id
	 */
	public void addSeparator(String menuId);
	
	public void showMenu();

}
