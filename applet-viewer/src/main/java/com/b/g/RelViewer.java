package com.b.g;

import java.awt.EventQueue;
import java.awt.PopupMenu;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JApplet;
import com.b.a.ClientEndSupport;
import com.b.a.ClientEventSupport;
import com.b.a.ClientMenuSupport;
import com.b.a.ClientRelFileSupport;
import com.b.a.ClientViewSupport;
import com.b.a.Parameter;
import com.b.a.ViewerEvent;
import com.b.s.ClientEventSupportImpl;
import com.b.s.ClientMenuSupportImpl;
import com.b.s.ClientSupportAdapter;
import com.b.u.Result;
import com.b.u.ParameterUtil;

public class RelViewer extends JApplet implements ClientEndSupport,ClientEventSupport,
ClientMenuSupport,ClientRelFileSupport,ClientViewSupport
{
	protected URL codeBase; 
	
	RelChart relChart ;
	ClientEventSupport eventSupport = new ClientEventSupportImpl();	
	ClientSupportAdapter clientSupport = new ClientSupportAdapter();
	ClientMenuSupportImpl menuSupport = new ClientMenuSupportImpl();

	public void disableEvent(int eventId) {
		eventSupport.disableEvent(eventId);
	}

	public void enableEvent(int eventId) {
		eventSupport.enableEvent(eventId);
	}

	public String getEvent() {
		return eventSupport.getEvent();
	}

	public boolean getRaiseEvents() {
		return eventSupport.getRaiseEvents();
	}

	public void putEvent(int eventId, String eventParam) {
		eventSupport.putEvent(eventId, eventParam);
	}

	public void setRaiseEvents(boolean raiseFlag) {
		eventSupport.setRaiseEvents(raiseFlag);
	}

	private void handleParameters() {
		ParameterUtil.putBoolean(Parameter.TOOL_BAR,
				getParameter(Parameter.TOOL_BAR));
		
		ParameterUtil.putBoolean(Parameter.BUTTON_PANNING,getParameter(Parameter.BUTTON_PANNING));
		ParameterUtil.putBoolean(Parameter.BUTTON_ZOOM_IN,getParameter(Parameter.BUTTON_ZOOM_IN));
		ParameterUtil.putBoolean(Parameter.BUTTON_ZOOM_OUT,getParameter(Parameter.BUTTON_ZOOM_OUT));
		ParameterUtil.putBoolean(Parameter.BUTTON_ZOOM_ACTUAL,
				getParameter(Parameter.BUTTON_ZOOM_ACTUAL));
		ParameterUtil.putBoolean(Parameter.BUTTON_ZOOM_FIT,
				getParameter(Parameter.BUTTON_ZOOM_FIT));
		
		ParameterUtil.putBoolean(Parameter.BUTTON_REORGANIZE,
				getParameter(Parameter.BUTTON_REORGANIZE));
		
		ParameterUtil.putBoolean(Parameter.BUTTON_VIEW_GRID,
				getParameter(Parameter.BUTTON_VIEW_GRID));
		ParameterUtil.putBoolean(Parameter.BUTTON_VIEW_TIMELINE,
				getParameter(Parameter.BUTTON_VIEW_TIMELINE));
		
		ParameterUtil.putBoolean(Parameter.BUTTON_VIEW_HIERARCHY,
				getParameter(Parameter.BUTTON_VIEW_HIERARCHY));
		ParameterUtil.putBoolean(Parameter.BUTTON_VIEW_GROUP,
				getParameter(Parameter.BUTTON_VIEW_GROUP));
		ParameterUtil.putBoolean(Parameter.BUTTON_VIEW_ROUND,
				getParameter(Parameter.BUTTON_VIEW_ROUND));
		ParameterUtil.putBoolean(Parameter.BUTTON_VIEW_PEACOK,
				getParameter(Parameter.BUTTON_VIEW_PEACOK));
		ParameterUtil.putBoolean(Parameter.BUTTON_DELETE,
				getParameter(Parameter.BUTTON_DELETE));
		ParameterUtil.putBoolean(Parameter.BUTTON_LOCK,
				getParameter(Parameter.BUTTON_LOCK));
		ParameterUtil.putBoolean(Parameter.BUTTON_HIDDEN,
				getParameter(Parameter.BUTTON_HIDDEN));
		ParameterUtil.putBoolean(Parameter.BUTTON_UNLOCK,
				getParameter(Parameter.BUTTON_UNLOCK));
		ParameterUtil.putBoolean(Parameter.BUTTON_UNDELETE,
				getParameter(Parameter.BUTTON_UNDELETE));
		ParameterUtil.putBoolean(Parameter.BUTTON_INVERTSELECTION,
				getParameter(Parameter.BUTTON_INVERTSELECTION));
		ParameterUtil.putBoolean(Parameter.BUTTON_SHOWALL,
				getParameter(Parameter.BUTTON_SHOWALL));
		
		ParameterUtil.putString(Parameter.LAYOUT_MANAGER,
				getParameter(Parameter.LAYOUT_MANAGER));		
		ParameterUtil.putString(Parameter.REL_FILE,
				getParameter(Parameter.REL_FILE));
		ParameterUtil.putString(Parameter.INITIAL_WAITING_MESSAGE,
				getParameter(Parameter.INITIAL_WAITING_MESSAGE));
		ParameterUtil.putBoolean(Parameter.RAISE_EVENTS,
				getParameter(Parameter.RAISE_EVENTS));
		ParameterUtil.putBoolean(Parameter.LAYOUT_ANIMATION,
				getParameter(Parameter.LAYOUT_ANIMATION));
		ParameterUtil.putString(Parameter.BACKGROUND_IMAGE,
				getParameter(Parameter.BACKGROUND_IMAGE));
		ParameterUtil.putBoolean(Parameter.BACKGROUND_IMAGE_STRETCH,
						getParameter(Parameter.BACKGROUND_IMAGE_STRETCH));
		
		ParameterUtil.putString(Parameter.LINK_SHAPE,
				getParameter(Parameter.LINK_SHAPE));
		
		ParameterUtil.putBoolean(Parameter.PAIR_ARROW_HIDDEN, getParameter(Parameter.PAIR_ARROW_HIDDEN));
		ParameterUtil.putString(Parameter.ARROW_POSITION, getParameter(Parameter.ARROW_POSITION));
		
		ParameterUtil.putBoolean(Parameter.LABEL_DISPLAY_HIDDEN, getParameter(Parameter.LABEL_DISPLAY_HIDDEN));
		ParameterUtil.putString(Parameter.LABEL_SEP, getParameter(Parameter.LABEL_SEP));
		
		ParameterUtil.putBoolean(Parameter.TOOLTIP_DISPLAY_HIDDEN, getParameter(Parameter.TOOLTIP_DISPLAY_HIDDEN));
		ParameterUtil.putString(Parameter.TOOLTIP_SEP, getParameter(Parameter.TOOLTIP_SEP));
		
		ParameterUtil.putString(Parameter.LINK_PICKING_COLOR, getParameter(Parameter.LINK_PICKING_COLOR));
		
		ParameterUtil.putString(Parameter.TEXT_PICKING_COLOR, getParameter(Parameter.TEXT_PICKING_COLOR));
		ParameterUtil.putString(Parameter.TEXT_PICKING_BGCOLOR, getParameter(Parameter.TEXT_PICKING_BGCOLOR));
						
		ParameterUtil.putBoolean(Parameter.MOUSE_SCALING,
				getParameter(Parameter.MOUSE_SCALING));

		setCodeBase(this.getCodeBase());
	}
		
	@Override
	public void init() {
		//this.setSize(800, 650);
		try {
			handleParameters();
		}	catch(NullPointerException e){
			//run from main;			
		}
		
		relChart = new RelChart(this);
		
		relChart.setCodeBase(this.codeBase);		
		clientSupport.setCodeBase(this.codeBase);
		
		clientSupport.setVv(relChart.getVv());
		clientSupport.setDataService(relChart.getDataService());
		clientSupport.setEventSupport(eventSupport);
		clientSupport.setAnimateLayout(ParameterUtil.getBoolean(Parameter.LAYOUT_ANIMATION, true));
		
		eventSupport.setRaiseEvents(ParameterUtil.getBoolean(Parameter.RAISE_EVENTS, true));
		
		relChart.init();
		this.add(relChart);
		
		Timer t = new Timer(true);
		t.schedule(
				new TimerTask(){
					@Override
					public void run() {
						repaint();
					}
				}
				, 1, 1000);
		
		Timer ts = new Timer();
		ts.schedule(
				new TimerTask(){
					@Override
					public void run() {
						Runtime.getRuntime().gc();
						Runtime.getRuntime().runFinalization();
					}
				}
				, 1, 30000);
		
	}

	@Override
	public void start() {
		// validate();
		 repaint();
	}
	
	public void setCodeBase(URL url)
	{
		if( this.codeBase == null) {
			this.codeBase = url;
//			System.out.println("code base:" + url);
		}
	}
	
	public void addMenuItem(final String menuId, final String menuTxt) {
		Runnable runnable = new Runnable()
		{
			public void run() {			
				menuSupport.addMenuItem(menuId, menuTxt);			
			}
		};
		
		runLater(runnable,"addMenuItem");
		
	}

	public void addSeparator(final String menuId) {
		Runnable runnable = new Runnable()
		{
			public void run() {			
				menuSupport.addSeparator(menuId);		
			}
		};
		
		runLater(runnable,"addSeparator");
	}

	public void addSubMenuItem(final String pMenuId, final String menuId, final String menuTxt) {
		Runnable runnable = new Runnable()
		{
			public void run() {
				menuSupport.addSubMenuItem(pMenuId, menuId, menuTxt);
			}
		};
		
		runLater(runnable,"addSubMenuItem");
	}

	public void clearMenuItems() {
		Runnable runnable = new Runnable()
		{
			public void run() {			
				menuSupport.clearMenuItems();
			}
		};
		
		runLater(runnable,"clearMenuItems");
		
	}



	public void setMenuItemState(final String menuId, final boolean blnEnable) {	
		Runnable runnable = new Runnable()
		{
			public void run() {			
				menuSupport.setMenuItemState(menuId, blnEnable);
			}
		};
		
		runLater(runnable,"setMenuItemState");
	}

	public void showMenu() {
		Runnable runnable = new Runnable()
		{
			public void run() {	
				PopupMenu popupMenu = menuSupport.getPopupMenu(relChart);
				relChart.showMenu(popupMenu);
			}
		};
		
		runLater(runnable,"showMenu");
	}

	public String getFile() {
		final Result result = new Result();
		Runnable runnable = new Runnable()
		{
			public void run() {
				result.setResult(relChart.getFile());
			}
		};
		
		runWait(runnable,"getFile");
		
		return  result.getString();
	}

	public String getRel() {
		final Result result = new Result();
		Runnable runnable = new Runnable()
		{
			public void run() {
				result.setResult(relChart.getRel());
			}
		};
		
		runWait(runnable,"getRel");
		
		return  result.getString();
	}

	public void mergeRel(final String strUrl) {
		Runnable runnable = new Runnable()
		{
			public void run() {			
				relChart.mergeRel(strUrl);
			}
		};
		
		runLater(runnable,"mergeRel");
	}

	public void mergeRelString(final String strRel) {
		Runnable runnable = new Runnable()
		{
			public void run() {			
				relChart.mergeRelString(strRel);
			}
		};
		
		runLater(runnable,"mergeRelString");
	}
	
	public void setTimeBar(final boolean bln)
	{
		Runnable runnable = new Runnable()
		{
			public void run() {			
				relChart.setTimeBar(bln);
			}
		};
		
		runLater(runnable,"setTimeBar");
	}

	public void putRelString(final String strRel) {
		Runnable runnable = new Runnable()
		{
			public void run() {			
				relChart.putRelString(strRel);
			}
		};
		
		runLater(runnable,"putRelString");
	}

	public void reloadFile() {
		Runnable runnable = new Runnable()
		{
			public void run() {					
				relChart.reloadFile();
			}
		};
		
		runLater(runnable,"reloadFile");
	}

	public void setFile(final String strUrl) {
		Runnable runnable = new Runnable()
		{
			public void run() {			
				relChart.setFile(strUrl);
			}
		};
		
		runLater(runnable,"setFile");
	}

	public void focusChartOnEnd(final String entId) {
		Runnable runnable = new Runnable()
		{
			public void run() {					
				clientSupport.focusChartOnEnd(entId);
			}
		};
		
		runLater(runnable,"focusChartOnEnd");
	}

	public boolean getAnimateLayout() {
		final Result result = new Result();
		Runnable runnable = new Runnable()
		{
			public void run() {
				result.setResult(clientSupport.getAnimateLayout());
			}
		};
		
		runWait(runnable,"getAnimateLayout");
		
		return result.getBoolean();
	}

	public String getLayoutManager() {
		final Result result = new Result();
		Runnable runnable = new Runnable()
		{
			public void run() {
				result.setResult(clientSupport.getLayoutManager());
			}
		};
		
		runWait(runnable,"getLayoutManager");
		
		return result.getString();
	}

	public void invertSelection() {
		Runnable runnable = new Runnable()
		{
			public void run() {					
				clientSupport.invertSelection();
			}
		};
		
		runLater(runnable,"invertSelection");
	}

	public void reorganize() {
		Runnable runnable = new Runnable()
		{
			public void run() {					
				clientSupport.reorganize();
//				eventSupport.putEvent(ViewerEvent.EVENT_LAYOUTDONE, clientSupport
//						.getLayoutManager());
			}
		};
		
		runLater(runnable,"reorganize");
		
	}

	public void reorganizeEx(final String strLayout) {
		Runnable runnable = new Runnable()
		{
			public void run() {					
				clientSupport.reorganizeEx(strLayout);
//				eventSupport.putEvent(ViewerEvent.EVENT_LAYOUTDONE, clientSupport
//						.getLayoutManager());
			}
		};
		
		runLater(runnable,"reorganizeEx");
		
	}

	public void reorganizeSelection() {
		Runnable runnable = new Runnable()
		{
			public void run() {					
				clientSupport.reorganizeSelection();
			}
		};
		
		runLater(runnable,"reorganizeSelection");
	}

	public void scrollCenter() {
		Runnable runnable = new Runnable()
		{
			public void run() {						
				clientSupport.scrollCenter();
			}
		};
		
		runLater(runnable,"scrollCenter");
	}

	public void setAnimateLayout(final boolean bln) {
		Runnable runnable = new Runnable()
		{
			public void run() {						
				clientSupport.setAnimateLayout(bln);
			}
		};
		
		runLater(runnable,"setAnimateLayout");
	}

	public void setBackgroundColor(final String color) {
		Runnable runnable = new Runnable()
		{
			public void run() {		
				clientSupport.setBackgroundColor(color);
			}
		};
		
		runLater(runnable,"setBackgroundColor");
	}

	public void setBackgroundImage(final String strUrl, final boolean stretch) {
		Runnable runnable = new Runnable()
		{
			public void run() {
				clientSupport.setBackgroundImage(strUrl, stretch);
			}
		};
		
		runLater(runnable,"setBackgroundImage");			
		
	}

	public void setConnectivityEmphasis(final String strEntityTypeList) {
		Runnable runnable = new Runnable()
		{
			public void run() {
				clientSupport.setConnectivityEmphasis(strEntityTypeList);
			}
		};
		
		runLater(runnable,"setConnectivityEmphasis");	
	}

	public void setEmphasisOn(final boolean blnEmphasis) {
		Runnable runnable = new Runnable()
		{
			public void run() {
				clientSupport.setEmphasisOn(blnEmphasis);
			}
		};
		
		runLater(runnable,"setEmphasisOn");		
	}

	public void setMaxEmphasised(final int iEntities) {
		Runnable runnable = new Runnable()
		{
			public void run() {
				clientSupport.setMaxEmphasised(iEntities);
			}
		};
		
		runLater(runnable,"setMaxEmphasised");		
		
	}

	public void setPropertyEmphasis(final String strEntityType,
			final String strPropertyType, final boolean blnHighest) {
		Runnable runnable = new Runnable()
		{
			public void run() {
				clientSupport.setPropertyEmphasis(strEntityType, strPropertyType, blnHighest);
			}
		};
		
		runLater(runnable,"setPropertyEmphasis");		
		
	}

	public void setTargetSeparation(final float separation) {
		Runnable runnable = new Runnable()
		{
			public void run() {
				clientSupport.setTargetSeparation(separation);
			}
		};
		
		runLater(runnable,"setTargetSeparation");		
	}

	public void setZoom(final float f) {
		Runnable runnable = new Runnable()
		{
			public void run() {
				clientSupport.setZoom(f);
			}
		};
		
		runLater(runnable,"setZoom");
	}

	public String findEntities(final String text, final boolean blnEntity, final boolean blnLink,
			final boolean blnExactMatch) {
		final Result result = new Result();
		Runnable runnable = new Runnable()
		{
			public void run() {
				result.setResult(clientSupport.findEntities(text, blnEntity, blnLink, blnExactMatch));
			}
		};
		
		runWait(runnable,"findEntities");
		
		return result.getString();
	}

	public String getAddedEndIDs() {
		final Result result = new Result();
		Runnable runnable = new Runnable()
		{
			public void run() {
				result.setResult(clientSupport.getAddedEndIDs());
			}
		};
		
		runWait(runnable,"getAddedEndIDs");
		
		return result.getString();
	}

	public String getAddedEnds() {
	
		final Result result = new Result();
		Runnable runnable = new Runnable()
		{
			public void run() {
				result.setResult(clientSupport.getAddedEnds());
			}
		};
		
		runWait(runnable,"getAddedEnds");
		
		return  result.getString();
	}

	public String getAddedLinkIDs() {
		final Result result = new Result();
		Runnable runnable = new Runnable()
		{
			public void run() {
				result.setResult(clientSupport.getAddedLinkIDs());
			}
		};
		
		runWait(runnable,"getAddedLinkIDs");
		
		return  result.getString();
	}

	public String getAddedLinks() {
		final Result result = new Result();
		Runnable runnable = new Runnable()
		{
			public void run() {
				result.setResult(clientSupport.getAddedLinks());
			}
		};
		
		runWait(runnable,"getAddedLinks");
		
		return  result.getString();
	}

	public String getAllEndIDs() {
		final Result result = new Result();
		Runnable runnable = new Runnable()
		{
			public void run() {
				result.setResult(clientSupport.getAllEndIDs());
			}
		};
		
		runWait(runnable,"getAllEndIDs");
		
		return  result.getString();
	}

	public String getAllEnds() {
		final Result result = new Result();
		Runnable runnable = new Runnable()
		{
			public void run() {
				result.setResult(clientSupport.getAllEnds());
			}
		};
		
		runWait(runnable,"getAllEnds");
		
		return  result.getString();
	}

	public String getAllLinkIDs() {
		final Result result = new Result();
		Runnable runnable = new Runnable()
		{
			public void run() {
				result.setResult(clientSupport.getAllLinkIDs());
			}
		};
		
		runWait(runnable,"getAllLinkIDs");
		
		return  result.getString();
	}

	public String getAllLinks() {
		final Result result = new Result();
		Runnable runnable = new Runnable()
		{
			public void run() {
				result.setResult(clientSupport.getAllLinks());
			}
		};
		
		runWait(runnable,"getAllLinks");
		
		return  result.getString();
	}

	public String getDeleteEnds() {
		final Result result = new Result();
		Runnable runnable = new Runnable()
		{
			public void run() {
				result.setResult(clientSupport.getDeleteEnds());
			}
		};
		
		runWait(runnable,"getDeleteEnds");
		
		return  result.getString();
	}

	public String getDeleteLinks() {
		final Result result = new Result();
		Runnable runnable = new Runnable()
		{
			public void run() {
				result.setResult(clientSupport.getDeleteLinks());
			}
		};
		
		runWait(runnable,"getDeleteLinks");
		
		return  result.getString();
	}

	public String getEndLinks(final String strEntity) {
		final Result result = new Result();
		Runnable runnable = new Runnable()
		{
			public void run() {
				result.setResult(clientSupport.getEndLinks(strEntity));
			}
		};
		
		runWait(runnable,"getEndLinks");
		
		return  result.getString();
	}

	public String getEndPos(final String strEntity) {
		final Result result = new Result();
		Runnable runnable = new Runnable()
		{
			public void run() {
				result.setResult(clientSupport.getEndPos(strEntity));
			}
		};
		
		runWait(runnable,"getEndPos");
		
		return  result.getString();
	}

	public String getEndTypesInUse() {
		final Result result = new Result();
		Runnable runnable = new Runnable()
		{
			public void run() {
				result.setResult(clientSupport.getEndTypesInUse());
			}
		};
		
		runWait(runnable,"getEndTypesInUse");
		
		return  result.getString();
	}

	public String getInVisibleEndIDs() {
		final Result result = new Result();
		Runnable runnable = new Runnable()
		{
			public void run() {
				result.setResult( clientSupport.getInVisibleEndIDs());
			}
		};
		
		runWait(runnable,"getInVisibleEndIDs");
		
		return  result.getString();
	}

	public String getInVisibleLinkIDs() {
		final Result result = new Result();
		Runnable runnable = new Runnable()
		{
			public void run() {
				result.setResult(clientSupport.getInVisibleLinkIDs());
			}
		};
		
		runWait(runnable,"getInVisibleLinkIDs");
		
		return  result.getString();
	}

	public String getItemDateTime(final String strItem, final String strFormat) {
		final Result result = new Result();
		Runnable runnable = new Runnable()
		{
			public void run() {
				result.setResult(clientSupport.getItemDateTime(strItem, strFormat));
			}
		};
		
		runWait(runnable,"getItemDateTime");
		
		return  result.getString();
	}

	public String getItemType(final String strItem) {
		final Result result = new Result();
		Runnable runnable = new Runnable()
		{
			public void run() {
				result.setResult(clientSupport.getItemType(strItem));
			}
		};
		
		runWait(runnable,"getItemType");
		
		return  result.getString();
	}

	public String getItemTypeDisplayName(final String strItemType) {
		final Result result = new Result();
		Runnable runnable = new Runnable()
		{
			public void run() {
				result.setResult(clientSupport.getItemTypeDisplayName(strItemType));
			}
		};
		
		runWait(runnable,"getItemTypeDisplayName");
		
		return  result.getString();
	}

	public String getItemTypeImage(final String strItemType) {
		final Result result = new Result();
		Runnable runnable = new Runnable()
		{
			public void run() {
				result.setResult(clientSupport.getItemTypeImage(strItemType));
			}
		};
		
		runWait(runnable,"getItemTypeImage");
		
		return result.getString();
	}

	public String getItemsPropertyIDs(final String strItem) {
		final Result result = new Result();
		Runnable runnable = new Runnable()
		{
			public void run() {
				result.setResult(clientSupport.getItemsPropertyIDs(strItem));
			}
		};
		
		runWait(runnable,"getItemsPropertyIDs");
		
		return result.getString();
	}

	public String getItemsPropertyIDsEx(final String strItem, final boolean bIncludeHidden) {
		final Result result = new Result();
		Runnable runnable = new Runnable()
		{
			public void run() {
				result.setResult(clientSupport.getItemsPropertyIDsEx(strItem, bIncludeHidden));
			}
		};
		
		runWait(runnable,"getItemsPropertyIDsEx");
		
		return result.getString();
	}

	public String getLinkEnd1(final String strLink) {
		final Result result = new Result();
		Runnable runnable = new Runnable()
		{
			public void run() {
				result.setResult(clientSupport.getLinkEnd1(strLink));
			}
		};
		
		runWait(runnable,"getLinkEnd1");
		
		return result.getString();
	}

	public String getLinkEnd2(final String strLink) {
	
		final Result result = new Result();
		Runnable runnable = new Runnable()
		{
			public void run() {
				result.setResult(clientSupport.getLinkEnd2(strLink));
			}
		};
		
		runWait(runnable,"getLinkEnd2");
		
		return result.getString();
	}

	public String getLinkTypeInUse() {
		final Result result = new Result();
		Runnable runnable = new Runnable()
		{
			public void run() {
				result.setResult(clientSupport.getLinkTypeInUse());
			}
		};
		
		runWait(runnable,"getLinkTypeInUse");
		
		return result.getString();
	}

	public String getMultiplePropertyValues(final String strItem,
			final String strPropertyType, final String strSeparator) {
		final Result result = new Result();
		Runnable runnable = new Runnable()
		{
			public void run() {
				result.setResult(clientSupport.getMultiplePropertyValues(strItem, strPropertyType, strSeparator));
			}
		};
		
		runWait(runnable,"getMultiplePropertyValues");
		
		return result.getString();
	}

	public String getPropertyDisplayName(final String strItem, final String strPropertyType) {
		final Result result = new Result();
		Runnable runnable = new Runnable()
		{
			public void run() {
				result.setResult(clientSupport.getPropertyDisplayName(strItem, strPropertyType));
			}
		};
		
		runWait(runnable,"getPropertyDisplayName");
		
		return result.getString();
	}

	public String getPropertyValue(final String strItem, final String strPropertyType) {
		final Result result = new Result();
		Runnable runnable = new Runnable()
		{
			public void run() {
				result.setResult(clientSupport.getPropertyValue(strItem, strPropertyType));
			}
		};
		
		runWait(runnable,"getPropertyValue");
		
		return result.getString();
	}

	public String getSelectedElements() {
		final Result result = new Result();
		Runnable runnable = new Runnable()
		{
			public void run() {
				result.setResult(clientSupport.getSelectedElements());
			}
		};
		
		runWait(runnable,"getSelectedElements");
		
		return (result.getResult()).toString();
	}

	public String getSelectedElementsEx(final boolean blnEntity, final boolean blnLink) {
		final Result result = new Result();
		Runnable runnable = new Runnable()
		{
			public void run() {
				result.setResult(clientSupport.getSelectedElementsEx(blnEntity, blnLink));
			}
		};
		
		runWait(runnable,"getSelectedElementsEx");
		
		return (result.getResult()).toString();
	}

	public String getVisibleEndIDs() {		
		final Result result = new Result();
		Runnable runnable = new Runnable()
		{
			public void run() {
				result.setResult(clientSupport.getVisibleEndIDs());
			}
		};
		
		runWait(runnable,"getVisibleEndIDs");
		
		return result.getString();
	}

	public String getVisibleLinkIDs() {
		final Result result = new Result();
		Runnable runnable = new Runnable()
		{
			public void run() {
				result.setResult(clientSupport.getVisibleLinkIDs());
			}
		};
		
		runWait(runnable,"getVisibleLinkIDs");
		
		return result.getString();
	}

	public boolean hasProperty(final String strItem, final String strPropertyType) {
		final Result result = new Result();
		Runnable runnable = new Runnable()
		{
			public void run() {
				result.setResult(clientSupport.hasProperty(strItem, strPropertyType));
			}
		};
		
		runWait(runnable,"hasProperty");
		
		return result.getBoolean();
	}

	public boolean isElementDeleted(final String strItem) {
		
		final Result result = new Result();
		Runnable runnable = new Runnable()
		{
			public void run() {
				result.setResult(clientSupport.isElementDeleted(strItem));
			}
		};
		
		runWait(runnable,"isElementDeleted");
		
		return result.getBoolean();
	}

	public boolean isElementSelected(final String strItem) {
		final Result result = new Result();
		Runnable runnable = new Runnable()
		{
			public void run() {
				result.setResult(clientSupport.isElementSelected(strItem));
			}
		};
		
		runWait(runnable,"isElementSelected");
		
		return result.getBoolean();
	}

	public boolean isElementShown(final String strItem) {
		final Result result = new Result();
		Runnable runnable = new Runnable()
		{
			public void run() {
				result.setResult(clientSupport.isElementShown(strItem));
			}
		};
		
		runWait(runnable,"isElementShown");
		
		return result.getBoolean();
	}

	public boolean isPropertyHidden(final String strItemType, final String strPropertyType) {
		final Result result = new Result();
		Runnable runnable = new Runnable()
		{
			public void run() {
				result.setResult(clientSupport.isPropertyHidden(strItemType, strPropertyType));
			}
		};
		
		runWait(runnable,"isPropertyHidden");
		
		return result.getBoolean();
		
	}

	public void setAllEndsFixed(final boolean blnFixed) {
		
		Runnable runnable = new Runnable()
		{
			public void run() {
				clientSupport.setAllEndsFixed(blnFixed);
				}
		};
		runLater(runnable,"setAllEndsFixed");
		
	}

	public void setElementDeleted(final String strItem, final boolean blnDelete) {
		
		Runnable runnable = new Runnable()
		{
			public void run() {
				clientSupport.setElementDeleted(strItem, blnDelete);
				}
		};
		runLater(runnable,"setElementDeleted");
	}

	public void setElementHidden(final String strItem, final boolean blnHidden) {
		
		Runnable runnable = new Runnable()
		{
			public void run() {
				clientSupport.setElementHidden(strItem, blnHidden);		
				}
		};
		runLater(runnable,"setElementHidden");
	}

	public void setElementSelected(final String strItem, final boolean blnSelect) {
		
		Runnable runnable = new Runnable()
		{
			public void run() {
				clientSupport.setElementSelected(strItem, blnSelect);
				}
		};
		runLater(runnable,"setElementSelected");
	}

	public void setElementsDeleted(final String strItemList, final boolean blnDelete) {
		
		Runnable runnable = new Runnable()
		{
			public void run() {
				clientSupport.setElementsDeleted(strItemList, blnDelete);
				}
		};
		runLater(runnable,"setElementsDeleted");
	}

	public void setElementsHidden(final String strItemList, final boolean blnHidden) {
	
		Runnable runnable = new Runnable()
		{
			public void run() {
				clientSupport.setElementsHidden(strItemList, blnHidden);
				}
		};
		runLater(runnable,"setElementsHidden");
	}

	public void setElementsSelected(final String strItemList, final boolean blnSelect) {
		
		Runnable runnable = new Runnable()
		{
			public void run() {
				clientSupport.setElementsSelected(strItemList, blnSelect);
				}
		};
		runLater(runnable,"setElementsSelected");
	}

	public void setEndFixed(final String strEntity, final boolean blnFix) {
		
		Runnable runnable = new Runnable()
		{
			public void run() {

				clientSupport.setEndFixed(strEntity, blnFix);
				}
		};
		runLater(runnable,"setEndFixed");
	}

	public void setEndsFixed(final String strEntities, final boolean blnFix) {
		Runnable runnable = new Runnable()
		{
			public void run() {

				clientSupport.setEndsFixed(strEntities, blnFix);
				}
		};
		runLater(runnable,"setEndsFixed");
	}

	public void setPropertyValue(final String strItem, final String strPropertyType,
			final String strPropertyValue, final boolean blnTooltip) {
		Runnable runnable = new Runnable()
		{
			public void run() {
				clientSupport.setPropertyValue(strItem, strPropertyType, strPropertyValue, blnTooltip);
			}
		};
		runLater(runnable,"setPropertyValue");
	}

	public String getSplitStr() {
		final Result result = new Result();
		Runnable runnable = new Runnable()
		{
			public void run() {
				result.setResult(clientSupport.getSplitStr());
			}
		};
		
		runWait(runnable,"getSplitStr");
		
		return result.getString();
	}

	public void setSplitStr(final String split) {
		Runnable runnable = new Runnable()
		{
			public void run() {
				clientSupport.setSplitStr(split);
			}
		};
		
		runLater(runnable,"setSplitStr");
	}
	
	public void zoomFit()
	{
		Runnable runnable = new Runnable()
		{
			public void run() {
				clientSupport.zoomFit();
			}
		};
		runLater(runnable,"zoomFit");
	}


	public void showWaiting(final boolean blnWaiting) {		
			 Runnable runnable = new Runnable() {

		            public void run()
		            {
		            	relChart.showWaiting(blnWaiting);
		            }
			 };
			 
			 runWait(runnable,"showWaiting");	
	}
	
	
	private void runLater(Runnable runnable, String s)
	{
		if(EventQueue.isDispatchThread())
            runnable.run();
		else
			EventQueue.invokeLater(runnable);
	}
	
	private void runWait(Runnable runnable, String s)
	{
		if(EventQueue.isDispatchThread())
            runnable.run();
		else
            try
            {
                EventQueue.invokeAndWait(runnable);
            }
            catch(InterruptedException interruptedexception)
            {
            	interruptedexception.printStackTrace();
            	eventSupport.putEvent(ViewerEvent.EVENT_ERROR, "error running '" + s +"':" 
            			+ interruptedexception.getLocalizedMessage());            	
            } catch (InvocationTargetException e) {
            	eventSupport.putEvent(ViewerEvent.EVENT_ERROR, "error running '" + s +"':"
            			+ e.getLocalizedMessage());     
				e.printStackTrace();
			}
	}

	public void setFadedComposite(final float f) {
		Runnable runnable = new Runnable()
		{
			public void run() {
				clientSupport.setFadedComposite(f);
			}
		};
		
		runLater(runnable,"setFadedComposite");		
	}

	public void zoomOne() {
		Runnable runnable = new Runnable()
		{
			public void run() {
				clientSupport.zoomOne();
			}
		};
		
		runLater(runnable,"zoomOne");		
	}

	public String getLink(final String end1, final String end2) {
		final Result result = new Result();
		Runnable runnable = new Runnable()
		{
			public void run() {
				result.setResult(clientSupport.getLink(end1, end2));
			}
		};
		
		runWait(runnable,"getLink");
		
		return (result.getResult()).toString();
	}

	public String getPath(final String end1, final String end2) {
		final Result result = new Result();
		Runnable runnable = new Runnable()
		{
			public void run() {
				result.setResult(clientSupport.getPath(end1, end2));
			}
		};
		
		runWait(runnable,"getPath");
		
		return (result.getResult()).toString();
	}

	public String getLinkShape(final String link) {
		final Result result = new Result();
		Runnable runnable = new Runnable()
		{
			public void run() {
				result.setResult(clientSupport.getLinkShape(link));
			}
		};
		
		runWait(runnable,"getLinkShape");
		
		return (result.getResult()).toString();
	}

	public void setLinkShape(final String link, final String shape) {
		Runnable runnable = new Runnable()
		{
			public void run() {
				clientSupport.setLinkShape(link,shape);
			}
		};
		
		runLater(runnable,"setLinkShape");	
	}

	public void setLinkShape(final String shape) {
		Runnable runnable = new Runnable()
		{
			public void run() {
				clientSupport.setLinkShape(shape);
			}
		};
		
		runLater(runnable,"setLinkShape");		
	}

	public void setMode(final String mode) {
		Runnable runnable = new Runnable()
		{
			public void run() {	
				relChart.setMode(mode);
			}
		};
		
		runLater(runnable,"setMode");
	}

	public String getEmphasisedEndIDs() {
		final Result result = new Result();
		Runnable runnable = new Runnable()
		{
			public void run() {
				result.setResult(clientSupport.getEmphasisedEndIDs());
			}
		};
		
		runWait(runnable,"getEmphasisedEndIDs");
		
		return (result.getResult()).toString();
	}

	public void clearEndEmphasis() {
		Runnable runnable = new Runnable()
		{
			public void run() {	
				clientSupport.clearEndEmphasis();
			}
		};
		
		runLater(runnable,"clearEndEmphasis");
		
	}

	public void setEndEmphasis(final String strEntityList, final boolean blnEmphasis) {
		Runnable runnable = new Runnable()
		{
			public void run() {	
				clientSupport.setEndEmphasis(strEntityList, blnEmphasis);
			}
		};
		
		runLater(runnable,"setEndEmphasis");
		
	}
}
