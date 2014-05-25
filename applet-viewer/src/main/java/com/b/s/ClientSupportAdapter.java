package com.b.s;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import o.c15.Transformer;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import com.b.a.ClientEndSupport;
import com.b.a.ClientEventSupport;
import com.b.a.ClientViewSupport;
import com.b.a.Parameter;
import com.b.a.ViewerEvent;
import com.b.e.CatProperty;
import com.b.e.Entity;
import com.b.e.EntityType;
import com.b.e.ImageURL;
import com.b.e.Link;
import com.b.e.LinkType;
import com.b.e.Property;
import com.b.g.Generator;
import com.b.u.StringUtil;
import com.b.u.ParameterUtil;
import com.b.x.AnimatorEventOp;
import com.b.x.AnimatorLayoutTransition;
import com.b.x.HierarchyLayout;
import com.b.x.RelCrossoverScalingControl;
import com.b.x.RelRadialTreeLayout;
import com.b.x.RelVisualizationViewer;
import com.b.x.TimeSeqLayout;

import g.algorithms.cluster.WeakComponentClusterer;
import g.algorithms.filters.FilterUtils;
import g.algorithms.layout.AggregateLayout;
import g.algorithms.layout.FRLayout;
import g.algorithms.layout.Layout;
import g.algorithms.layout.StaticLayout;
import g.algorithms.layout.util.RandomLocationTransformer;
import g.algorithms.shortestpath.BFSDistanceLabeler;
import g.graph.Forest;
import g.graph.Graph;
import g.visualization.Layer;
import g.visualization.LayeredIcon;
import g.visualization.VisualizationViewer;

public class ClientSupportAdapter implements ClientEndSupport,
		ClientViewSupport {
	
	private Logger log = Logger.getLogger(this.getClass().getName());

	protected RelVisualizationViewer<String, String> vv;
	protected DataService dataService;
	protected String layoutManager = Parameter.Layout.GRID_LAYOUT;
	protected boolean blnAnimateLayout;
	protected String splitStr = ",";
	protected float costFactor = 0.85f;
	
	protected Map<String,Point2D> endLocations = Collections.synchronizedMap(new HashMap<String,Point2D>());
	
	protected ClientEventSupport eventSupport;
	
	final RelCrossoverScalingControl scaler = new RelCrossoverScalingControl();

	protected URL codeBase;
	private static Map<String, Icon> iconMap = Collections.synchronizedMap(new HashMap<String, Icon>());
	
	public void setEventSupport(ClientEventSupport eventSupport)
	{
		this.eventSupport = eventSupport;
	}
	
	private void setLayoutManager(String layoutManager) {
		if (Parameter.Layout.GRID_LAYOUT.equals(layoutManager)
				|| Parameter.Layout.GROUP_LAYOUT.equals(layoutManager)
				|| Parameter.Layout.HIERARCHY_LAYOUT.equals(layoutManager)
				|| Parameter.Layout.PEACOCK_LAYOUT.equals(layoutManager)
				|| Parameter.Layout.ROUND_LAYOUT.equals(layoutManager)
				|| Parameter.Layout.TIMELINE_LAYOUT.equals(layoutManager))
			this.layoutManager = layoutManager;
		else
			this.layoutManager = Parameter.Layout.GRID_LAYOUT;
	}

	public static Map<String, Icon> getIconMap() {
		return iconMap;
	}

	public static void setIconMap(Map<String, Icon> iconMap) {
		ClientSupportAdapter.iconMap = iconMap;
	}

	public RelVisualizationViewer<String, String> getVv() {
		return vv;
	}

	public void setVv(RelVisualizationViewer<String, String> vv) {
		this.vv = vv;
	}

	public DataService getDataService() {
		return dataService;
	}

	public void setDataService(DataService dataService) {
		this.dataService = dataService;
	}

	public URL getCodeBase() {
		return codeBase;
	}

	public void setCodeBase(URL codeBase) {
		this.codeBase = codeBase;
	}

	public void focusChartOnEnd(String entId) {
		if (dataService.isEntity(entId)) {
			Layout<String, String> layout = vv.getGraphLayout();
			Point2D q = layout.transform(entId);
			Point2D lvc = vv.getRenderContext().getMultiLayerTransformer()
					.inverseTransform(vv.getCenter());
			if (ParameterUtil.getBoolean(Parameter.LAYOUT_ANIMATION, true))
				animationMove(q, lvc);
			else
				flashMove(q,lvc);
		}
	}
	
	private void flashMove(Point2D p1, Point2D p2)
	{
		final double dx = (p2.getX() - p1.getX());
		final double dy = (p2.getY() - p1.getY());
		
		vv.getRenderContext().getMultiLayerTransformer()
		.getTransformer(Layer.LAYOUT).translate(dx, dy);
	}
	
	class AnimationMoveTask extends TimerTask
	{
		int i = 0;
		int total = 0;
		double dx;
		double dy;
		
		public AnimationMoveTask(double dx, double dy, int totalCount)
		{
			this.dx = dx;
			this.dy = dy;
			this.total = totalCount;
			reset();
		}
		
		public void reset()
		{
			i = 0;
		}

		@Override
		public void run() {
			i++;
			if( i>=total) this.cancel();
			vv.getRenderContext().getMultiLayerTransformer()
			.getTransformer(Layer.LAYOUT).translate(dx, dy);
		}
		
	}

	private void animationMove(Point2D p1, Point2D p2) {
		final int totalCount = 8;
		
		final double dx = (p2.getX() - p1.getX()) / totalCount;
		final double dy = (p2.getY() - p1.getY()) / totalCount;
		
		AnimationMoveTask task = new AnimationMoveTask(dx,dy,totalCount);
		Timer t = new Timer();
		t.schedule(task, 1, 100);
		
//		Runnable animator = new Runnable() {
//
//			public void run() {
//				for (int i = 0; i < 5; i++) {
//					vv.getRenderContext().getMultiLayerTransformer()
//							.getTransformer(Layer.LAYOUT).translate(dx, dy);
//					try {
//						Thread.sleep(100);
//					} catch (InterruptedException ex) {
//					}
//				}
//			}
//		};
//		Thread thread = new Thread(animator);
//		thread.start();
	}

	public boolean getAnimateLayout() {
		return this.blnAnimateLayout;
	}

	public String getLayoutManager() {
		return this.layoutManager;
	}

	@SuppressWarnings("unchecked")
	public void invertSelection() {
		List vertexList = new ArrayList();
		List edgeList = new ArrayList();
		for (Object vertex : vv.getPickedVertexState().getPicked()) {
			vertexList.add(vertex);
		}
		for (Object edge : vv.getPickedVertexState().getPicked()) {
			edgeList.add(edge);
		}

		vv.getPickedEdgeState().clear();
		vv.getPickedVertexState().clear();

		Graph g = vv.getGraphLayout().getGraph();
		for (Object vertex : g.getVertices()) {
			if (!vertexList.contains(vertex))
				vv.getPickedVertexState().pick(vertex.toString(), true);
		}

		for (Object edge : g.getEdges()) {
			if (!edgeList.contains(edge))
				vv.getPickedVertexState().pick(edge.toString(), true);
		}

		vertexList.clear();
		edgeList.clear();
		vv.repaint();
	}

	/**
	 * 使用当前布局重新布局
	 */
	public void reorganize() {
		reorganizeEx(layoutManager);
	}

	private void updateVv() {
		Graph<String, String> g = Generator.generateMultiGraph();

		resetIconMap();

		Layout<String, String> layout = vv.getGraphLayout();
		StaticLayout<String, String> staticLayout = new StaticLayout<String, String>(
				g, layout);

		vv.getModel().setGraphLayout(staticLayout);
		vv.repaint();
	}
	
	
	private Transformer<String,Point2D> decorateInitializer(final Transformer<String,Point2D> t, final Dimension d)
	{		
		Transformer<String,Point2D> transformer1 = new Transformer<String,Point2D>()
		{
			public Point2D transform(String v) {
				Point2D point2d = null;
				
				if( t != null){					
					point2d = t.transform(v);
				}
				if( point2d == null){
					Dimension t = new Dimension(d);
					if(d == null)  t = new Dimension(300,300);
					double x = Math.random() * t.width;
					double y = Math.random()*  t.height;
					point2d = new Point2D.Double(x , y);					
				}
				return point2d;
			}
		};
		
		return transformer1;
		
	}
	
	private Transformer<String,Point2D> initializer = null;

	public void reorganizeEx(String strLayout) {
		this.setLayoutManager(strLayout);

		Graph<String, String> g = Generator.generateMultiGraph();
		
		if( g.getVertexCount() < 1 ) return;
		
		resetIconMap();
		Dimension idealSize = Generator.getIdealGridLayoutSize(g, vv.getSize());
		int idealLen = (int) Generator.getIdealLen();
		

		if( initializer == null ) initializer = new RandomLocationTransformer<String>(idealSize);
		
		Layout<String, String> destLayout =null;
		//Transformer<String,Point2D> initializer = Generator.getInitializer(idealSize);

		if (Parameter.Layout.GRID_LAYOUT.equals(layoutManager)) {
			destLayout = new FRLayout<String, String>(g);
			((FRLayout<String,String>)destLayout).setAttractionMultiplier(1);
			((FRLayout<String,String>)destLayout).setRepulsionMultiplier(1);
			destLayout.setInitializer(initializer);
			destLayout.setSize(idealSize);
		}else if(Parameter.Layout.GROUP_LAYOUT.equals(layoutManager)){
			FRLayout<String,String> frLayout = new FRLayout<String, String>(g);
			frLayout.setAttractionMultiplier(1);
			frLayout.setRepulsionMultiplier(1);
			frLayout.setInitializer(initializer);
			frLayout.setSize(idealSize);
			destLayout = new AggregateLayout<String,String>(frLayout);
			groupCluster((AggregateLayout<String, String>) destLayout,g);			
		}
		else if (Parameter.Layout.HIERARCHY_LAYOUT.equals(layoutManager)) {
			Forest<String, String> tree = Generator.generateForest(g);
			Layout<String, String> layout = new HierarchyLayout<String, String>(
					tree, (int) (idealLen * costFactor));
			destLayout = new StaticLayout<String, String>(
					g, layout, layout.getSize());
			
		}		
		else if (Parameter.Layout.ROUND_LAYOUT.equals(layoutManager)) {
			Forest<String, String> tree = Generator.generateForest(g);
			
			RelRadialTreeLayout<String, String> layout = new RelRadialTreeLayout<String, String>(
					tree, (int) (idealLen * costFactor));
			destLayout = new StaticLayout<String, String>(
					g, layout, layout.getSize());
		} else if( Parameter.Layout.TIMELINE_LAYOUT.equals(layoutManager)){			
			List<String> themedVs = dataService.getThemedItemIds();
			Map<String,Long> timeCoord = Generator.getTimeCoord();
			destLayout = new TimeSeqLayout<String,String>(g, (int) (idealLen * costFactor), timeCoord, themedVs);
			destLayout.setInitializer(initializer);
			destLayout.setSize(idealSize);
		}		
		else {
			destLayout = vv.getGraphLayout();
		}
		
		if( destLayout !=null) {restLockEndPos(destLayout);}
		
		if (blnAnimateLayout) {
			AnimatorLayoutTransition<String,String> t = new AnimatorLayoutTransition<String,String>(vv, vv.getGraphLayout(),destLayout);
			AnimatorEventOp animatorOp = new AnimatorEventOp();
			animatorOp.setEnventSupport(eventSupport);
			animatorOp.setParam(strLayout);
			t.setAnimatorOp(animatorOp);
			t.go();
		} else {
			vv.getModel().setGraphLayout(destLayout);
			eventSupport.putEvent(ViewerEvent.EVENT_LAYOUTDONE, strLayout);
		}
		
		//Generator.setInitializer(destLayout);
		initializer = decorateInitializer(destLayout,idealSize);

		vv.repaint();
	}

	public void reorganizeSelection() {
		// TODO Auto-generated method stub

	}
	
	private void groupCluster(AggregateLayout<String,String> layout,
			Graph<String, String> g	)
	{
		WeakComponentClusterer<String,String> cluster = new WeakComponentClusterer<String,String>();
		
		Collection<Graph<String, String>> graphs = FilterUtils.createAllInducedSubgraphs(cluster.transform(g), g);
		for(Graph<String,String> ig: graphs)
		{
			if(ig.getVertexCount() < g.getVertexCount()) {
				Point2D center = layout.transform(ig.getVertices().iterator().next());				
				FRLayout<String,String> frLayout = new FRLayout<String, String>(ig);
				frLayout.setAttractionMultiplier(1);
				frLayout.setRepulsionMultiplier(1);
				frLayout.setInitializer(layout.getDelegate());
				Dimension size = Generator.getIdealGridLayoutSize(ig, vv.getSize());
				//frLayout.setInitializer(Generator.getInitializer(size));				
				frLayout.setSize(size);
				
				layout.put(frLayout,center);
			}
		}
	}

	public void scrollCenter() {
		synchronized (vv.getGraphLayout()) {
		Layout<String, String> layout = vv.getGraphLayout();
		Dimension d = layout.getSize();

		//log.info("layout size : " + d.toString());
		Point2D q = new Point2D.Double(d.getWidth() / 2d, d.getHeight() / 2d);
		Point2D lvc = vv.getRenderContext().getMultiLayerTransformer()
				.inverseTransform(vv.getCenter());
		//log.info("lvc size: " + vv.getSize());
		
		if (blnAnimateLayout)
			animationMove(q, lvc);
		else
			flashMove(q,lvc);
		}
	}

	public void setAnimateLayout(boolean bln) {
		this.blnAnimateLayout = bln;
	}

	public void setBackgroundColor(String color) {
		Color c = Color.decode(color);
		vv.setBackground(c);
		vv.repaint();
	}

	public void setBackgroundImage(String strUrl, boolean stretch) {
		ImageIcon sandstoneIcon = null;
		URL url = StringUtil.newURL(strUrl, this.codeBase);
		if (url != null)
			try {
				sandstoneIcon = new ImageIcon(url);
			} catch (Exception ex) {
				System.err.println("Can't load background image \"" + strUrl
						+ "\"");
			}
		final ImageIcon icon = sandstoneIcon;
		final boolean bstretch = stretch;

		if (icon != null) {
			vv.addPreRenderPaintable(new VisualizationViewer.Paintable() {
				public void paint(Graphics g) {
					Dimension d = vv.getSize();
					if (!bstretch) {
						d.height = icon.getIconHeight();
						d.width = icon.getIconWidth();
					}
					g.drawImage(icon.getImage(), 0, 0, d.width, d.height, vv);
				}

				public boolean useTransform() {
					return false;
				}
			});
		}
		vv.repaint();
	}

	public void setTargetSeparation(float separation) {
		Generator.setIdealLen(separation);
	}

	public String findEntities(String text, boolean blnEntity, boolean blnLink,
			boolean blnExactMatch) {
		StringBuilder sb = new StringBuilder();
		if (blnEntity) {
			sb.append(StringUtil.charSeparate(dataService.findEntities(text,
					blnExactMatch),splitStr));
		}
		if (blnLink) {
			String s = StringUtil.charSeparate(dataService.findLinks(text,
					blnExactMatch),splitStr);
			if (s.length() > 0) {
				if (sb.length() > 0)
					sb.append(splitStr);
				sb.append(s);
			}
		}
		return sb.toString();
	}

	public String getAddedEndIDs() {
		return StringUtil.charSeparate(dataService.getAddedEnds(),splitStr);
	}

	public String getAddedEnds() {
		return getAddedEndIDs();
	}

	public String getAddedLinkIDs() {
		return StringUtil.charSeparate(dataService.getAddedLinks(),splitStr);
	}

	public String getAddedLinks() {
		return getAddedLinkIDs();
	}

	public String getAllEndIDs() {
		return StringUtil.charSeparate(dataService.getAllEnds(),splitStr);
	}

	public String getAllEnds() {
		return getAllEndIDs();
	}

	public String getAllLinkIDs() {
		return StringUtil.charSeparate(dataService.getAddedLinks(),splitStr);
	}

	public String getAllLinks() {
		return getAllLinkIDs();
	}

	public String getEndLinks(String strEntity) {
		return StringUtil.charSeparate(dataService.getLinks(strEntity),splitStr);
	}

	public String getEndPos(String strEntity) {
		Layout<String, String> layout = vv.getGraphLayout();
		Point2D q = layout.transform(strEntity);
		return StringUtil.charSeparate(q.getX(), q.getY(),splitStr);
	}

	public String getEndTypesInUse() {
		return StringUtil.charSeparate(dataService.getTypeService()
				.getEntityTypes(),splitStr);
	}

	public String getItemDateTime(String strItem, String strFormat) {
		Date d = null;
		if (dataService.isEntity(strItem)) {
			d = dataService.getEntityDateTime(strItem);
		}
		
		if (dataService.isLink(strItem)) {
			d = dataService.getLinkDateTime(strItem);
		}

		if( d != null){
				return new SimpleDateFormat(strFormat).format(d);
		}

		return null;
	}

	public String getItemType(String strItem) {
		if (dataService.isEntity(strItem)) {
			return dataService.getEntityType(strItem).toString();
		}
		if (dataService.isLink(strItem))
			return dataService.getLinkType(strItem).toString();
		return null;
	}

	public String getItemTypeDisplayName(String strItemType) {
		if (dataService.isLinkType(strItemType)) {
			return dataService.getTypeService().getLinkType(strItemType)
					.getDisplayName();
		}
		if (dataService.isEntityType(strItemType)) {
			return dataService.getTypeService().getEntityType(strItemType)
					.getDisplayName();
		}
		return null;
	}

	public String getItemTypeImage(String strItemType) {
		if (dataService.isEntityType(strItemType)) {
			return dataService.getEntityTypeImage(strItemType).getURL();
		}
		return null;
	}

	public String getItemsPropertyIDs(String strItem) {
		List<CatProperty> al = new ArrayList<CatProperty>();

		if (dataService.isEntity(strItem)) {
			for (CatProperty catProperty : dataService
					.getEntityCatProperty(strItem)) {
				if (!catProperty.isHidden())
					al.add(catProperty);
			}
		}
		if (dataService.isLink(strItem)) {
			for (CatProperty catProperty : dataService
					.getLinkCatProperty(strItem)) {
				if (!catProperty.isHidden())
					al.add(catProperty);
			}
		}

		return StringUtil.charSeparate(al,splitStr);
	}

	public String getItemsPropertyIDsEx(String strItem, boolean bIncludeHidden) {
		if (!bIncludeHidden)
			return getItemsPropertyIDs(strItem);
		else {
			List<CatProperty> al = new ArrayList<CatProperty>();

			if (dataService.isEntity(strItem)) {
				for (CatProperty catProperty : dataService
						.getEntityCatProperty(strItem)) {
					al.add(catProperty);
				}
			}
			if (dataService.isLink(strItem)) {
				for (CatProperty catProperty : dataService
						.getLinkCatProperty(strItem)) {
					al.add(catProperty);
				}
			}

			return StringUtil.charSeparate(al,splitStr);
		}
	}

	public String getLinkEnd1(String strLink) {
		Link link = dataService.getEntityService().getLink(strLink);
		return link == null ? null : link.getEnt1Id();
	}

	public String getLinkEnd2(String strLink) {
		Link link = dataService.getEntityService().getLink(strLink);
		return link == null ? null : link.getEnt2Id();
	}

	public String getLinkTypeInUse() {
		return StringUtil.charSeparate(dataService.getTypeService()
				.getLinkTypes(),splitStr);
	}

	public String getMultiplePropertyValues(String strItem,
			String strPropertyType, String strSeparator) {
		return getPropertyValue(strItem, strPropertyType);
	}

	public String getPropertyDisplayName(String strItem, String strPropertyType) {
		if (dataService.isEntity(strItem)) {
			CatProperty catProperty = dataService.getEntityCatProperty(strItem,
					strPropertyType);
			return catProperty == null ? null : catProperty.getDisplayName();
		}
		if (dataService.isLink(strItem)) {
			CatProperty catProperty = dataService.getLinkCatProperty(strItem,
					strPropertyType);
			return catProperty == null ? null : catProperty.getDisplayName();
		}
		return null;
	}

	public String getPropertyValue(String strItem, String strPropertyType) {
		if (dataService.isEntity(strItem)) {
			Property property = dataService.getEntityProperty(strItem,
					strPropertyType);
			return property == null ? null : property.getText();
		}
		if (dataService.isLink(strItem)) {
			Property property = dataService.getLinkProperty(strItem,
					strPropertyType);
			return property == null ? null : property.getText();
		}
		return null;
	}

	public String getSelectedElements() {
		return getSelectedElementsEx(true, true);
	}

	public String getSelectedElementsEx(boolean blnEntity, boolean blnLink) {
		StringBuilder sb = new StringBuilder();
		if (blnEntity) {
			sb.append(StringUtil.charSeparate(vv.getPickedVertexState()
					.getPicked(),splitStr));
		}
		if (blnLink) {
			String s = StringUtil.charSeparate(vv.getPickedEdgeState()
					.getPicked(),splitStr);
			if (s.length() > 0) {
				if (sb.length() > 0)
					sb.append(splitStr);
				sb.append(s);
			}
		}

		return sb.toString();
	}

	public String getVisibleEndIDs() {
		return StringUtil.charSeparate(dataService.getVisibleEntities(),splitStr);
	}

	public String getVisibleLinkIDs() {
		return StringUtil.charSeparate(dataService.getVisibleLinks(),splitStr);
	}

	public String getInVisibleEndIDs() {
		return StringUtil.charSeparate(dataService.getHiddenEntities(),splitStr);
	}

	public String getInVisibleLinkIDs() {
		return StringUtil.charSeparate(dataService.getHiddenLinks(),splitStr);
	}

	public String getDeleteLinks() {
		return StringUtil.charSeparate(dataService.getDeletedLinks(),splitStr);
	}

	public String getDeleteEnds() {
		return StringUtil.charSeparate(dataService.getDeletedEntities(),splitStr);
	}

	public boolean hasProperty(String strItem, String strPropertyType) {
		if (dataService.isEntity(strItem)) {
			Property property = dataService.getEntityProperty(strItem,
					strPropertyType);
			return property == null ? false : true;
		}
		if (dataService.isLink(strItem)) {
			Property property = dataService.getLinkProperty(strItem,
					strPropertyType);
			return property == null ? false : true;
		}
		return false;
	}

	public boolean isElementDeleted(String strItem) {
		if (dataService.isLink(strItem)) {
			return dataService.isLinkDeleted(strItem);
		}
		if (dataService.isEntity(strItem)) {
			return dataService.isEntityDeleted(strItem);
		}
		return false;
	}

	public boolean isElementSelected(String strItem) {

		if (dataService.isEntity(strItem)) {
			return vv.getRenderContext().getPickedVertexState().isPicked(
					strItem);
		}
		if (dataService.isLink(strItem)) {
			return vv.getRenderContext().getPickedEdgeState().isPicked(strItem);
		}
		return false;
	}

	public boolean isElementShown(String strItem) {
		if (dataService.isEntity(strItem)) {
			return !dataService.isEntityHidden(strItem);
		}
		if (dataService.isLink(strItem)) {
			return !dataService.isLinkHidden(strItem);
		}
		return false;
	}

	public boolean isPropertyHidden(String strItemType, String strPropertyType) {
		if (dataService.isEntityType(strItemType)) {
			EntityType entityType = dataService.getEntityType(strItemType);
			CatProperty catProperty = entityType
					.getCatProperty(strPropertyType);
			return catProperty == null ? false : catProperty.isHidden();
		}
		if (dataService.isLinkType(strItemType)) {
			LinkType linkType = dataService.getLinkType(strItemType);
			CatProperty catProperty = linkType.getCatProperty(strPropertyType);
			return catProperty == null ? false : catProperty.isHidden();
		}
		return false;
	}

	public void setAllEndsFixed(boolean blnFixed) {
		for (Entity entity : dataService.getAllEnds()) {
			setEndFixed(entity.getId(), blnFixed);
		}
	}

	public void setElementDeleted(String strItem, boolean blnDelete) {
		if (dataService.isEntity(strItem)) {
			if (blnDelete) {
				dataService.delEntity(strItem);
			} else
				dataService.unDelEntity(strItem);
		}
		if (dataService.isLink(strItem)) {
			if (blnDelete) {
				dataService.delLink(strItem);
			} else
				dataService.unDelLink(strItem);
		}
		if( blnDelete) setElementSelected(strItem, false);
		updateVv();
	}

	public void setElementHidden(String strItem, boolean blnHidden) {
		if (dataService.isEntity(strItem)) {
			dataService.setEntityHidden(strItem, blnHidden);
		}
		if (dataService.isLink(strItem)) {
			dataService.setLinkHidden(strItem, blnHidden);
		}
		if( blnHidden) setElementSelected(strItem, false);
		vv.repaint();
	}

	public void setElementSelected(String strItem, boolean blnSelect) {
		if (dataService.isEntity(strItem)) {
			vv.getPickedVertexState().pick(strItem, blnSelect);
		}
		if (dataService.isLink(strItem)) {
			vv.getPickedEdgeState().pick(strItem, blnSelect);
		}
		vv.repaint();
	}

	public void setElementsDeleted(String strItemList, boolean blnDelete) {
		String[] ids = strItemList.split(",");
		for (int i = 0; i < ids.length; i++) {
			setElementDeleted(ids[i], blnDelete);
		}
	}

	public void setElementsHidden(String strItemList, boolean blnHidden) {
		String[] ids = strItemList.split(",");
		for (int i = 0; i < ids.length; i++) {
			setElementHidden(ids[i], blnHidden);
		}
	}

	public void setElementsSelected(String strItemList, boolean blnSelect) {
		String strItems[] = strItemList.split(",");
		for (int i = 0; i < strItems.length; i++)
			setElementSelected(strItems[i], blnSelect);

	}

	public void setEndFixed(String strEntity, boolean blnFix) {
		dataService.lockEntity(strEntity, blnFix);
		if( blnFix){
			Layout<String, String> layout = vv.getGraphLayout();
			Point2D pos = layout.transform(strEntity);
			this.endLocations.put(strEntity, pos);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void restLockEndPos(Layout layout)
	{
		Map<Object,Boolean> lockEnds= dataService.getLockedEnds();
		for( Object obj : lockEnds.keySet())
		{
			if(lockEnds.get(obj)) {
				Point2D pos = endLocations.get(obj);
				if(pos !=null)
					layout.setLocation(obj, pos);
			}

			layout.lock(obj, lockEnds.get(obj));
		}
	}

	public void setEndsFixed(String strEntities, boolean blnFix) {
		String s[] = strEntities.split(",");
		for (int i = 0; i < s.length; i++) {
			setEndFixed(s[i], blnFix);
		}
	}

	public void setPropertyValue(String strItem, String strPropertyType,
			String strPropertyValue, boolean blnTooltip) {
		if (dataService.isEntity(strItem)) {
			dataService.setEntityPropertyValue(strItem, strPropertyType,
					strPropertyValue, blnTooltip);
		}
		if (dataService.isLink(strItem)) {
			dataService.setLinkPropertyValue(strItem, strPropertyType,
					strPropertyValue, blnTooltip);
		}
	}

	private void resetIconMap() {
		
		final URL c = this.codeBase;
		
		Runnable runnable = new Runnable(){
		
		public void run() {
			for (Entity entity : dataService.getEntityService().getEntities()) {
				if (!iconMap.containsKey(entity.getId())) {
					List<ImageURL> images = dataService.getEntityImages(entity);
					for (ImageURL image : images) {
						String s = image.getURL();
						URL url = StringUtil.newURL(s, c);
						try {
							if (url != null) {
								Icon icon = new LayeredIcon(new ImageIcon(url)
										.getImage());
								if (icon.getIconHeight() > 0
										&& icon.getIconWidth() > 0
										&& !iconMap.containsKey(entity.getId()))
									iconMap.put(entity.getId(), icon);
							} 
							String imgCode = image.getImgCode();
							if(!iconMap.containsKey(entity.getId())&& imgCode!=null && !"".equals(imgCode)){
								Icon icon = new LayeredIcon(new ImageIcon(
										Base64.decode(imgCode))
										.getImage());
								if (icon.getIconHeight() > 0
										&& icon.getIconWidth() > 0
										&& !iconMap.containsKey(entity.getId()))
									iconMap.put(entity.getId(), icon);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			
		}

		vv.repaint();
			
		}};
		
		Thread t = new Thread(runnable);
		t.start();		
	}

	public void setZoom(float f) {		
		scaler.scale(vv, f, vv.getCenter());
	}
	
	public void zoomFit()
	{
		scaler.scaleFit(vv, vv.getCenter());
		//vv.scaleToLayout(scaler);
	}
	
	public void setEndEmphasis(String strEntityList, boolean blnEmphasis)
	{
		Generator.setEndEmphasis(strEntityList, blnEmphasis);
		vv.repaint();
	}
	
	public void clearEndEmphasis()
	{
		Generator.clearEndEmphasis();
		vv.repaint();
	}

	public void setConnectivityEmphasis(String strEntityTypeList) {
		Generator.setConnectivityEmphasis(strEntityTypeList);
		vv.repaint();
	}

	public void setEmphasisOn(boolean blnEmphasis) {
		Generator.setEmphasised(blnEmphasis);
		vv.repaint();
	}

	public void setMaxEmphasised(int iEntities) {
		Generator.setMaxEmphasised(iEntities);
		vv.repaint();
	}

	public void setPropertyEmphasis(String strEntityType,
			String strPropertyType, boolean blnHighest) {
		// TODO Auto-generated method stub
	}

	public String getSplitStr() {
		return splitStr;
	}

	public void setSplitStr(String splitStr) {
		this.splitStr = splitStr;
	}

	public void showWaiting(boolean blnWaiting) {
		//do nothing
		
	}

	public void setFadedComposite(float f) {
		Generator.setAlphaFactor(f);
		vv.repaint();
	}

	public void zoomOne() {		
		scaler.scaleOne(vv, vv.getCenter());
	}

	public void setTimeBar(boolean bln) {
		//just do nothing because it has been set in relviewer
		
	}

	public String getLink(String end1, String end2) {
		return StringUtil.charSeparate(dataService.getLinks(end1, end2),splitStr);		
	}

	public String getPath(String end1, String end2) {
		Graph<String,String> g = vv.getGraphLayout().getGraph();
		Set<String> mPred = new HashSet<String>();
		
		BFSDistanceLabeler<String,String> bdl = new BFSDistanceLabeler<String,String>();
		bdl.labelDistances(g, end1);

		String v = end2;
		Set<String> prd = bdl.getPredecessors(v);
		mPred.add( end2 );
		while( prd != null && prd.size() > 0) {
			v = prd.iterator().next();
			mPred.add( v );
			if ( v == end1 ) return null;
			prd = bdl.getPredecessors(v);
		}
		
		return StringUtil.charSeparate(prd,splitStr);
	}

	public String getLinkShape(String link) {
		return dataService.getLinkShape(link);
	}

	public void setLinkShape(String link, String shape) {
		dataService.setLinkShape(link, shape);	
		vv.repaint();
	}

	public void setLinkShape(String shape) {
		ParameterUtil.putString(Parameter.LINK_SHAPE, shape);
	}

	public void setMode(String mode) {
		//just do nothing because it has been set in relviewer
	}

	public String getEmphasisedEndIDs() {
		return StringUtil.charSeparate(Generator.getEmphasisedEnds(),splitStr);
	}
}
