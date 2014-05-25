package com.b.s;

import java.awt.Container;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.b.a.ClientMenuSupport;

public class ClientMenuSupportImpl implements ClientMenuSupport {
	Map<String , List<MenuEntry>> menuMaps = new HashMap<String , List<MenuEntry>>();	
	String rootId = null;
	
	Set<String> disableItem = new HashSet<String>();
	int depth = 0;
	int maxDepth = 3;
	
	public Map<String , List<MenuEntry>> getMenuMaps() {
		return menuMaps;
	}
	
	public List<MenuEntry> getMenuItems(Object id)
	{
		return menuMaps.get(id);
	}
	
	public List<MenuEntry> getRootMenuItems()
	{
		return getMenuItems(rootId);
	}

	public void addMenuItem(String menuId, String menuTxt) {
		this.addMenuItem(rootId, menuId, menuTxt, MenuEntry.ITEM);
	}
	
	public void setMenuItemState(String menuId, boolean blnEnable) {
		if( blnEnable ){
			disableItem.remove(menuId);
		}else 
			disableItem.add(menuId);		
	}
	
	public boolean getMenuItemState(String menuId)
	{
		if( disableItem.contains(menuId))
			return false;
		return true;
	}
	
	public PopupMenu getPopupMenu(ActionListener l)
	{
		PopupMenu popupMenu = new PopupMenu();
		List<MenuEntry> lst = getRootMenuItems();
		
		for( int i=0; i<lst.size();i++){
			createMenu(popupMenu, (MenuEntry) lst.get(i),l);
			depth = 0;
		}
		
		return popupMenu;
	}
	
	private void createMenu(Menu parent, MenuEntry me,ActionListener l)
	{	
		if( depth > maxDepth) return;
		depth ++;
		
		MenuItem item;
		if( me.getType() == MenuEntry.SEPARATOR ) {
			parent.addSeparator();
			return ;
		}
		
		List<MenuEntry> lst = getMenuItems(me.getId());
		
		if ( lst != null && lst.size() > 0 ){
			item = new Menu(me.getTxt());
			if(disableItem.contains(me.getId())) item.setEnabled(false);
			else item.setEnabled(true);
			for( int i=0; i<lst.size(); i++) {
				createMenu((Menu)item,(MenuEntry) lst.get(i),l);		
			}
			parent.add(item);
		}
		else {
			item = new MenuItem(me.getTxt());
			if(disableItem.contains(me.getId())) item.setEnabled(false);
			else item.setEnabled(true);
			item.setActionCommand(me.getId());
			//item.setActionCommand(Parameter.Command.MENU);
			item.addActionListener(l);
			parent.add(item);			
		}		
	}	
	
	private void addMenuItem(String pMenuId, String menuId, String menuTxt, int type)
	{
		MenuEntry me = new MenuEntry(menuId, menuTxt, type );
		List<MenuEntry> lst = menuMaps.get(pMenuId);
		if(lst == null) {
			lst = new ArrayList<MenuEntry>();
		}
		lst.add(me);
		menuMaps.put(pMenuId, lst);
	}

	public void addSeparator(String menuId) {
		this.addMenuItem(menuId, "SEPARATOR", "SEPARATOR", MenuEntry.SEPARATOR);
	}

	public void addSubMenuItem(String pMenuId, String menuId, String menuTxt) {
		this.addMenuItem(pMenuId, menuId, menuTxt, MenuEntry.ITEM);
	}

	public void clearMenuItems() {
		menuMaps.clear();
	}
	
	public void showMenu(){	}
	
	class MenuEntry {
		String id;
		String txt;

		public MenuEntry(String id, String txt) {
			this(id,txt,ITEM);
		}
		
		public MenuEntry(String id, String txt, int type) {
			this.id = id;
			this.txt = txt;
			this.type = type;
		}
		
		int type = MenuEntry.ITEM;
		
		public int getType() {
			return type;
		}
		public void setType(int type) {
			this.type = type;
		}		
		
		static final int ITEM = 0;
		
		public MenuEntry(int type) {
			super();
			this.type = type;
		}
		static final int SEPARATOR = 1;
		
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getTxt() {
			return txt;
		}
		public void setTxt(String txt) {
			this.txt = txt;
		}	
	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container content = frame.getContentPane();
		ClientMenuSupportImpl menuSupport = new ClientMenuSupportImpl();
		JPanel panel = new JPanel();
		content.add(panel);
		menuSupport.addMenuItem("11", "aa");
		
		PopupMenu popupMenu = menuSupport.getPopupMenu(null);
		panel.add(popupMenu);
		
		frame.pack();
		frame.setVisible(true);
		
		popupMenu.show(panel, 1, 1);
	}
}

