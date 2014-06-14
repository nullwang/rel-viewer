package com.b.a;

public interface Parameter {

	String TOOL_BAR = "tool-bar";
	
	String BUTTON_PANNING = "button-panning";
	String BUTTON_ZOOM_IN = "button-zoom-in";
	String BUTTON_ZOOM_OUT = "button-zoom-out";
	String BUTTON_ZOOM_ONE = "button-zoom-one";
	String BUTTON_ZOOM_ACTUAL = "button-zoom-actual";
	String BUTTON_ZOOM_FIT = "button-zoom-fit";
	String BUTTON_VIEW_TIMELINE = "button-view-timeline";
	String BUTTON_VIEW_HIERARCHY = "button-view-hierarchy";
	String BUTTON_VIEW_GROUP ="button-view-group";
	String BUTTON_VIEW_ROUND = "button-view-round";
	String BUTTON_VIEW_PEACOK = "button-view-peacock";
	String BUTTON_VIEW_GRID = "button-view-grid";
	String BUTTON_REORGANIZE = "button-reorganize";
	String BUTTON_DELETE = "button-delete";
	String BUTTON_LOCK = "button-lock";
	String BUTTON_HIDDEN = "button-hidden";
	String BUTTON_UNLOCK = "button-unlock";
	String BUTTON_UNDELETE = "button-undelete";
	String BUTTON_INVERTSELECTION = "button-invertselection";
	String BUTTON_SHOWALL = "button-showall";
	
	String BUTTON_KEY_EMPHASIS = "button-key-emphasis";	
	
	String MOUSE_SCALING = "mouse-scaling";
	
	String LINK_SHAPE = "link-shape";
	
	String LAYOUT_MANAGER = "layout-manager";
	String REL_FILE = "rel-file";
	String INITIAL_WAITING_MESSAGE = "please wait...";
	String RAISE_EVENTS = "raise-events";
	String BACKGROUND_IMAGE = "background-image";
	String BACKGROUND_IMAGE_STRETCH = "background-image-stretch";
	String LAYOUT_ANIMATION = "layout-animation";
	
	String PAIR_ARROW_HIDDEN = "pair-arrow-hidden";
	String ARROW_POSITION = "arrow-position";
	
	String LABEL_DISPLAY_HIDDEN = "label-display-hidden";
	String LABEL_SEP = "label-sep";
	
	String TOOLTIP_DISPLAY_HIDDEN="tooltip-display_hidden";
	String TOOLTIP_SEP = "tool-sep";
	
	String LINK_PICKING_COLOR = "link-picking-color";
	String TEXT_PICKING_COLOR = "text-picking-color";
	String TEXT_PICKING_BGCOLOR = "text-picking-bgcolor";
	
	String TIMEBAR_BGCOLOR="timebar-bgcolor";
	
	interface Command {
		String PANNING = "panning";
		String ZOOMIN = "zoomin";
		String ZOOMOUT = "zoomout";
		String ZOOMONE = "zoomto1";
		String ZOOMFIT = "zoomtofit";
		String ZOOMAREA = "zoomtoarea";
		String VIEWGRID = "viewgrid";
		String VIEWTIMELINE = "viewtimeline";
		String VIEWHIERARCY = "viewhierarchy";
		String VIEWROUND = "viewround";
		String VIEWGROUP = "viewgroup";
		String VIEWPEACOCK="viewpeacock";
		String REORGANIZE ="reorganize";
		
		String DELETE = "delete";
		String LOCK = "lock";
		String HIDDEN = "hidden";
		String UNLOCK = "unlock";
		String UNDELETE = "undelete";
		String INVERTSELECTION = "invertselection";
		String SHOWALL = "showall";
		
		String KEYEMPHASIS = "keyemphasis";
		
		//use to test
		String TESTLOAD = "load";
		String TESTMERGE = "merge";
				
		//menu
		String MENU = "menu";
	}
	
	interface Layout{
		String GRID_LAYOUT = "grid-layout";
		String TIMELINE_LAYOUT = "time-line-layout";
		String GROUP_LAYOUT = "group-layout";
		String ROUND_LAYOUT = "round-layout";
		String HIERARCHY_LAYOUT = "hierarchy-layout";
		String PEACOCK_LAYOUT = "peacock-layout";
	}
	
	interface Shape{
		String QUAD = "quad";
		String LINE = "line";
		String PARALLEL = "parallel";
	}
	
	interface Mode{
		String TRANSFORMING = "transforming";
		String PICKING = "picking";
	}
	
	interface Position{
		String CENTER = "center";
		String ENDPOINT = "end-point";
	}
}
