package com.b.a;

public interface ClientMenuSupport {
	
	/**
	 * 向快捷菜单，添加菜单项
	 * @param menuId 菜单标识代码，如100
	 * @param menuTxt 菜单文本，如expand
	 */
	public void addMenuItem(String menuId, String menuTxt);
	
	/**
	 * 清除快捷菜单
	 */
	public void clearMenuItems();
	
	/**
	 * 添加子菜单
	 * @param pMenuId 父菜单id
	 * @param menuId 子菜单id
	 * @param menuTxt 菜单文本
	 */
	public void addSubMenuItem(String pMenuId, String menuId, String menuTxt);
	
	
	public void setMenuItemState(String menuId, boolean blnEnable);
	
	/**
	 * 向菜单添加分割符
	 * @param menuId 菜单id
	 */
	public void addSeparator(String menuId);
	
	public void showMenu();

}
