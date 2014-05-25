package com.b.a;

public interface ClientMenuSupport {
	
	/**
	 * ���ݲ˵�����Ӳ˵���
	 * @param menuId �˵���ʶ���룬��100
	 * @param menuTxt �˵��ı�����expand
	 */
	public void addMenuItem(String menuId, String menuTxt);
	
	/**
	 * �����ݲ˵�
	 */
	public void clearMenuItems();
	
	/**
	 * ����Ӳ˵�
	 * @param pMenuId ���˵�id
	 * @param menuId �Ӳ˵�id
	 * @param menuTxt �˵��ı�
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
